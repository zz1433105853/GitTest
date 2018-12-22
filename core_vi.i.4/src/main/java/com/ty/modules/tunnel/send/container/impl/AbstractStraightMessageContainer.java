package com.ty.modules.tunnel.send.container.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ty.common.utils.StringUtils;
import com.ty.common.utils.Threads;
import com.ty.modules.msg.entity.Tunnel;
import com.ty.modules.tunnel.reply.disruptor.MessageReplyEventProducer;
import com.ty.modules.tunnel.report.disruptor.MessageReportEventProducer;
import com.ty.modules.tunnel.response.disruptor.MessageResponseEventProducer;
import com.ty.modules.tunnel.send.container.entity.AbstractCmppMessageSend;
import com.ty.modules.tunnel.send.container.entity.LimitCounter;
import com.ty.modules.tunnel.send.container.entity.MessageSend;
import com.ty.modules.tunnel.send.container.entity.cmpp.MsgCommand;
import com.ty.modules.tunnel.send.container.entity.cmpp.MsgConfig;
import com.ty.modules.tunnel.send.container.entity.cmpp.MsgHead;
import com.ty.modules.tunnel.send.container.entity.cmpp.MsgQuery;
import com.ty.modules.tunnel.send.container.mina.AbstractCmppMessageHandler;
import com.ty.modules.tunnel.send.container.type.StraightMessageContainer;
import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * Created by ljb on 2017/4/11 16:01.
 */
public abstract class AbstractStraightMessageContainer implements StraightMessageContainer {

    private static Logger logger= Logger.getLogger(AbstractStraightMessageContainer.class);

    protected Tunnel tunnel;//当前Container关联的通道实体
    protected MsgConfig msgConfig;//当前通道属性配置
    protected int connectNumber;//直连通道连接数号码
    protected int sequenceId=0;//序列编号
    protected Map<String, AbstractCmppMessageSend> windowMap = Maps.newConcurrentMap();//发送消息的WindowMap
    protected LimitCounter limitCounter;//发送速度控制器

    protected AbstractCmppMessageHandler messageHandler;//CMPP数据处理

    protected IoConnector ioConnector;
    protected ConnectFuture connectFuture;
    protected LoggingFilter loggingFilter;
    protected ProtocolCodecFilter codecFilter;
    protected IoSession ioSession;

    protected boolean isConnected;//是否连接
    protected boolean isSendingMsg = false;//是否正在发送消息
    protected boolean isClosedByException = false;//是否为异常导致的关闭
    protected boolean doActiveTest = true;//是否进行链路检测

    protected int autoReconnectCount = 0;//重连次数

    protected MessageResponseEventProducer messageResponseEventProducer;
    protected MessageReportEventProducer messageReportEventProducer;
    protected MessageReplyEventProducer messageReplyEventProducer;

    public AbstractStraightMessageContainer(Tunnel tunnel, MsgConfig msgConfig, LoggingFilter loggingFilter, ProtocolCodecFilter codecFilter,
                                            MessageResponseEventProducer messageResponseEventProducer,
                                            MessageReportEventProducer messageReportEventProducer,
                                            MessageReplyEventProducer messageReplyEventProducer) {
        this.tunnel = tunnel;
        this.msgConfig = msgConfig;
        this.loggingFilter = loggingFilter;
        this.codecFilter = codecFilter;
        limitCounter = new LimitCounter(msgConfig.getSendSpeed(),msgConfig.getTdName());
        connectNumber = msgConfig.getConnectNumber();
    }

    @Override
    public void init() {
        //清空滑动窗口
        windowMap.clear();
        //初始化发送短信状态
        isSendingMsg = false;
        isConnected = false;
        ioConnector = new NioSocketConnector();
        ioConnector.setConnectTimeoutMillis(msgConfig.getConnectCount()*1000);
        ioConnector.getFilterChain().addLast("logger", loggingFilter);
        ioConnector.getFilterChain().addLast("codec",codecFilter); // 设置编码过滤器
        ioConnector.setHandler(messageHandler);//设置事件处理器
        // 设置接收缓存区大小
        ioConnector.getSessionConfig().setMinReadBufferSize(512*20);
        ioConnector.getSessionConfig().setMaxReadBufferSize(2048*2000);
        try {
            connectFuture =ioConnector.connect(new InetSocketAddress(msgConfig.getIsmgIp(),msgConfig.getIsmgPort()));//建立连接
            if(connectFuture!=null){
                connectFuture.awaitUninterruptibly();
                ioSession= connectFuture.getSession();
            }else{
                logger.error(new StringBuilder().append(this.getTdName()).append("建立与网关的连接失败"));
            }
        }catch (Exception e) {
            if(!ioConnector.isDisposed() && !ioConnector.isDisposing()){
                ioConnector.dispose();
            }
            logger.error(new StringBuilder().append(this.getTdName()).append("网关挂了").append(e.getMessage()));
        }
    }

    public synchronized boolean checkSendInterval() {
        return limitCounter.isOverSpeed();
    }

    @Override
    public synchronized int getSequence() {
        ++sequenceId;
        if(sequenceId>255){
            sequenceId=0;
        }
        return sequenceId;
    }

    public void reConnect(boolean isAuto) {
        if(isAuto) {
            if(autoReconnectCount<3) {
                autoReconnectCount++;
                init();
            }else {
                logger.info(new StringBuilder().append(this.getTdName()).append("超出自动重启次数，开启自动5秒重连线程"));
            }
        }else {
            init();
        }
    }

    /**
     * 与ISMG连接链路检查
     * @return
     */
    public void activityTestISMG(){
        MsgHead msgHead=new MsgHead();
        msgHead.setTotalLength(12);//消息总长度，级总字节数:4+4+4(消息头)+6+16+1+4(消息主体)
        msgHead.setCommandId(MsgCommand.CMPP_ACTIVE_TEST);//标识创建连接
        msgHead.setSequenceId(getSequence());//序列，由我们指定
        ioSession.write(msgHead);
    }

    /**
     * 拆除与ISMG的链接
     * @return
     */
    public void cancelISMG(){
        MsgHead head=new MsgHead();
        head.setTotalLength(12);//消息总长度，级总字节数:4+4+4(消息头)+6+16+1+4(消息主体)
        head.setCommandId(MsgCommand.CMPP_TERMINATE);//标识创建连接
        head.setSequenceId(getSequence());//序列，由我们指定
        ioSession.write(head);
    }

    public void queryISMG(String time,byte queryType,String queryCode){
        MsgQuery query=new MsgQuery();
        query.setTotalLength(12+8+1+10+8);
        query.setCommandId(MsgCommand.CMPP_QUERY);//标识创建连接
        query.setSequenceId(getSequence());//序列，由我们指定
        query.setTime(time);
        query.setQuery_Type(queryType);
        query.setQuery_Code(queryCode);
        ioSession.write(query);
    }

    /**
     * 发送长短信、短短信公共接口
     * @param  ms
     * @return
     */

    public boolean sendMsg(MessageSend ms){
        AbstractCmppMessageSend messageSend = (AbstractCmppMessageSend) ms;
        String msg = messageSend.getContent();
        String destTermId = messageSend.getMobile();
        String srcId = messageSend.getSrcId();
        try{
            if(msg.getBytes("UTF-16BE").length<=140){//短短信
                boolean result=sendShortMsg(msg,destTermId, srcId, messageSend);
                int count=0;
                while(!result){
                    count++;
                    result=sendShortMsg(msg,destTermId, srcId, messageSend);
                    if(count>=(msgConfig.getConnectCount()-1)){//如果再次连接次数超过两次则终止连接
                        break;
                    }
                }
                return result;
            }else{//长短信
                boolean result=sendLongMsg(msg,destTermId, srcId, messageSend);
                int count=0;
                while(!result){
                    count++;
                    result=sendLongMsg(msg,destTermId, srcId, messageSend);
                    if(count>=(msgConfig.getConnectCount()-1)){//如果再次连接次数超过两次则终止连接
                        break;
                    }
                }
                return result;
            }
        }catch(Exception e){
            return false;
        }
    }

    /**
     * 等待超时，自动断开
     */
    protected void outTimeShutDown() {
        for(String key:windowMap.keySet()) {
            AbstractCmppMessageSend ms = windowMap.get(key);
            if(ms!=null) {
                long ml = System.currentTimeMillis()- ms.getIntoWindowMapTime().getTime();
                if(ml>1000*10){
                    String msgRecordId = ms.getMsgRecord()!=null? ms.getMsgRecord().getId() : "<错误数据，没有发送记录>";
                    logger.info(StringUtils.builderString(this.getTdName(), ",键", key, ",信息id：", msgRecordId, ",等待窗口时间超限,移除"));
                    windowMap.remove(key);
                }
            }
        }
    }

    @Override
    public void startActiveTestThread() {
        new ActiveTestThread(this).start();
    }

    public void stopActiveTestThread() {
        this.doActiveTest = false;
    }

    /**
     * 生成长短信拆分数组
     * @param longMsg
     * @return
     */
    protected String[] generateLongMsgShortArr(String longMsg) throws UnsupportedEncodingException {
        int maxByteLength = 134;
        List<String> resultList = Lists.newLinkedList();
        String strFotTest = "";
        for(int i=0;i<longMsg.length();i++) {
            strFotTest += longMsg.charAt(i);
            if(strFotTest.getBytes("UTF-16BE").length>maxByteLength) {
                strFotTest = strFotTest.substring(0, strFotTest.length()-1);
                i--;
                resultList.add(strFotTest);
                strFotTest = "";
            }else if(strFotTest.getBytes("UTF-16BE").length==maxByteLength) {
                resultList.add(strFotTest);
                strFotTest = "";
            }
        }
        if(!"".equals(strFotTest))resultList.add(strFotTest);
        return resultList.toArray(new String[]{});
    }

    @Override
    public String getTdName() {
        if(tunnel!=null) {
            return tunnel.getTdNameWithOutConnectNo()+"_"+connectNumber;
        }else {
            return "";
        }
    }

    @Override
    public String getTunnelType() {
        if(tunnel!=null) {
            return tunnel.getType();
        }else {
            return "";
        }
    }

    @Override
    public Tunnel getTunnel() {
        return tunnel;
    }

    @Override
    public Map<String, AbstractCmppMessageSend> getWindowMap() {
        return windowMap;
    }

    @Override
    public synchronized boolean checkWindowMapSize() {
        if(windowMap.size()<16){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获取该Container是否正常工作
     * @return
     */
    @Override
    public boolean checkContainerIsActive() {
        return isConnected;
    }

    public MsgConfig getMsgConfig() {
        return msgConfig;
    }

    public void setMsgConfig(MsgConfig msgConfig) {
        this.msgConfig = msgConfig;
    }

    /**
     * 打印Map中的MessageSend信息
     */
    /*protected void printDataInWindowMap() {
        StringBuilder result = new StringBuilder();
        for(String key:windowMap.keySet()) {
            AbstractCmppMessageSend ms = windowMap.get(key);
            if(ms!=null && ms.getMsgRecord()!=null) {
                result.append(this.getTdName()+"键："+key+"；发送信息id:"+ms.getMsgRecord().getId()).append(",发送手机号："+ms.getMobile())
                        .append("发送时间："+ms.getCreateDate());
            }
        }
        logger.info(result.toString());
    }*/

    public void setTunnel(Tunnel tunnel) {
        this.tunnel = tunnel;
    }

    public int getConnectNumber() {
        return connectNumber;
    }

    public void setConnectNumber(int connectNumber) {
        this.connectNumber = connectNumber;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public void setWindowMap(Map<String, AbstractCmppMessageSend> windowMap) {
        this.windowMap = windowMap;
    }

    public LimitCounter getLimitCounter() {
        return limitCounter;
    }

    public void setLimitCounter(LimitCounter limitCounter) {
        this.limitCounter = limitCounter;
    }

    public AbstractCmppMessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(AbstractCmppMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public IoConnector getIoConnector() {
        return ioConnector;
    }

    public void setIoConnector(IoConnector ioConnector) {
        this.ioConnector = ioConnector;
    }

    public ConnectFuture getConnectFuture() {
        return connectFuture;
    }

    public void setConnectFuture(ConnectFuture connectFuture) {
        this.connectFuture = connectFuture;
    }

    public LoggingFilter getLoggingFilter() {
        return loggingFilter;
    }

    public void setLoggingFilter(LoggingFilter loggingFilter) {
        this.loggingFilter = loggingFilter;
    }

    public ProtocolCodecFilter getCodecFilter() {
        return codecFilter;
    }

    public void setCodecFilter(ProtocolCodecFilter codecFilter) {
        this.codecFilter = codecFilter;
    }

    public IoSession getIoSession() {
        return ioSession;
    }

    public void setIoSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isSendingMsg() {
        return isSendingMsg;
    }

    public void setSendingMsg(boolean sendingMsg) {
        isSendingMsg = sendingMsg;
    }

    public boolean isClosedByException() {
        return isClosedByException;
    }

    public void setClosedByException(boolean closedByException) {
        isClosedByException = closedByException;
    }

    public boolean isDoActiveTest() {
        return doActiveTest;
    }

    public void setDoActiveTest(boolean doActiveTest) {
        this.doActiveTest = doActiveTest;
    }

    public int getAutoReconnectCount() {
        return autoReconnectCount;
    }

    public void setAutoReconnectCount(int autoReconnectCount) {
        this.autoReconnectCount = autoReconnectCount;
    }

    class ActiveTestThread extends Thread {

        private Logger logger= Logger.getLogger(ActiveTestThread.class);
        private AbstractStraightMessageContainer messageContainer;

        public ActiveTestThread(AbstractStraightMessageContainer messageContainer) {
            this.messageContainer = messageContainer;
        }

        @Override
        public void run() {
            while (messageContainer.getIoSession().isActive() && messageContainer.isDoActiveTest()) {
                if(!messageContainer.isSendingMsg()) {
                    messageContainer.activityTestISMG();
                }else {
                    logger.warn(StringUtils.builderString("------------------------------------------------------", messageContainer.getTdName(), "正在发短信，略过这次链路检测", "------------------------------------------------------"));
                }
                Threads.sleep(60000);
            }

            logger.info(StringUtils.builderString(messageContainer.getTdName(), "与短信网关会话关闭，链路检测线程关闭"));
        }


    }

}
