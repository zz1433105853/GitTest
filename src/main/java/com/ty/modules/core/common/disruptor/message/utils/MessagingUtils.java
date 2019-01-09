package com.ty.modules.core.common.disruptor.message.utils;

import com.google.common.collect.Lists;
import com.ty.common.config.Global;
import com.ty.common.utils.CacheUtils;
import com.ty.common.utils.IspUtils;
import com.ty.common.utils.JedisUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.dao.AssignedCxTunnelDao;
import com.ty.modules.msg.entity.*;
import com.ty.modules.msg.service.BlackWhiteListService;
import com.ty.modules.sys.service.TunnelService;
import com.ty.modules.tunnel.send.container.common.ContainerRepo;
import com.ty.modules.tunnel.send.container.cx.MessageCxContainer;
import com.ty.modules.tunnel.send.container.cx.impl.AbstractThirdCxPartyMessageContainer;
import com.ty.modules.tunnel.send.container.cx.impl.ThirdCxMdMessagerContainer;
import com.ty.modules.tunnel.send.container.entity.*;
import com.ty.modules.tunnel.send.container.entity.container.qxt.ThirdQxtSendMsgresult;
import com.ty.modules.tunnel.send.container.entity.container.yx.ThirdYxSendMsgresult;
import com.ty.modules.tunnel.send.container.impl.*;
import com.ty.modules.tunnel.send.container.type.MessageContainer;
import com.ty.modules.tunnel.send.utils.NpUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */
@Service
public class MessagingUtils {
    private static Logger logger=Logger.getLogger(MessagingUtils.class);

    @Autowired
    private BlackWhiteListService blackWhiteListService;
    @Autowired
    public TunnelService tunnelService;
    @Autowired
    public AssignedCxTunnelDao assignedCxTunnelDao;
    @Value("${bestContainerFindTryCount}")
    private int bestContainerFindTryCount;

    /**
     * 验证黑名单
     * @param mobile
     * @param customer
     * @return
     */
    public boolean validationBlackList(String mobile, Customer customer){
        if("platform".equals(customer.getBlackListType())){
            List<BlackWhiteList> blackWhiteLists = null;
            blackWhiteLists = (List<BlackWhiteList>) CacheUtils.get(Global.CACHE_BLACK_INFO,"platform");
            if(blackWhiteLists == null || blackWhiteLists.isEmpty()){
                return false;
            }else{
                for(BlackWhiteList b:blackWhiteLists){
                    if(mobile.equals(b.getMobile())){
                        return true;
                    }
                }
            }
        }else if("customer".equals(customer.getBlackListType())){
            //客户黑名单
            List<BlackWhiteList> blackWhiteLists = ( List<BlackWhiteList>)CacheUtils.get(Global.CACHE_CUSTOMER_BLACK_INFO,customer.getId());
            for(BlackWhiteList b:blackWhiteLists){
                if(mobile.equals(b.getMobile())){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean valiadtionWhiteList(String mobile,Customer customer){
        BlackWhiteList blackWhiteList = new BlackWhiteList();
        blackWhiteList.setType("white");
        blackWhiteList.setCustomer(customer);
        List<BlackWhiteList> blackWhiteLists = blackWhiteListService.findList(blackWhiteList);
        if(StringUtils.inString(mobile,blackWhiteLists.get(0).getMobile().split("\r\n"))){
            return true;
        }
        return false;
    }

    public boolean valiadtionContentLength(String content,int length){
        if(content.length() > length){
            return true;
        }
        return false;
    }

    public void incrementSendCount(String customerId,String mobile){
        String minuteKey = StringUtils.generateRedisKey(Global.MINUTE_SEND_MOBILE_COUNT,customerId,mobile);
        String minuteCount = JedisUtils.get(minuteKey);
        if(StringUtils.isBlank(minuteCount)){
            JedisUtils.set(minuteKey,"1",0);
        }else{
            int count = Integer.parseInt(minuteCount)+1;
            JedisUtils.set(minuteKey,String.valueOf(count) ,0);
        }

        String hourKey = StringUtils.generateRedisKey(Global.HOUR_SEND_MOBILE_COUNT,customerId,mobile);
        String hourCount = JedisUtils.get(hourKey);
        if(StringUtils.isBlank(hourCount)){
            JedisUtils.set(hourKey,"1",0);
        }else{
            int count = Integer.parseInt(hourCount)+1;
            JedisUtils.set(hourKey,String.valueOf(count) ,0);
        }

        String dayKey = StringUtils.generateRedisKey(Global.DAY_SEND_MOBILE_COUNT,customerId,mobile);
        String dayCount = JedisUtils.get(dayKey);
        if(StringUtils.isBlank(dayCount)){
            JedisUtils.set(dayKey,"1",0);
        }else{
            int count = Integer.parseInt(dayCount)+1;
            JedisUtils.set(dayKey,String.valueOf(count) ,0);
        }
    }

    public boolean checkSendCount(Customer customer, String mobile){
        String customerId = customer.getId();
        String minuteKey = StringUtils.generateRedisKey(Global.MINUTE_SEND_MOBILE_COUNT,customerId,mobile);
        String hourKey = StringUtils.generateRedisKey(Global.HOUR_SEND_MOBILE_COUNT,customerId,mobile);
        String dayKey = StringUtils.generateRedisKey(Global.DAY_SEND_MOBILE_COUNT,customerId,mobile);
        String minuteCount = JedisUtils.get(minuteKey);
        String hourCount = JedisUtils.get(hourKey);
        String dayCount = JedisUtils.get(dayKey);
        boolean result = true;
        if(StringUtils.isNotBlank(minuteCount) && Integer.parseInt(minuteCount) >= customer.getMinuteSendCountLimit()){
            result = false;
        }

        if(StringUtils.isNotBlank(hourCount) && Integer.parseInt(hourCount) >= customer.getHourSendCountLimit()){
            result = false;
        }

        if(StringUtils.isNotBlank(dayCount) && Integer.parseInt(dayCount) >= customer.getDaySendCountLimit()){
            result = false;
        }
        return result;
    }

    /**
     * 根据给定手机号监测该目标手机号属于哪家运营商 1: 移动 2: 联通 3: 电信
     * @param mobile
     * @return
     */
    public boolean checkIspOfTheMobile(String mobile) {
        String isp = IspUtils.getIspTypeByTopThree(mobile);
        if(StringUtils.isBlank(isp)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * @param mobile
     * @return
     */
    public String getIspOfTheMobile(String mobile) {
        String isp = NpUtils.getAllNpMap().get(mobile);
        if(StringUtils.isBlank(isp)){
            isp = IspUtils.getIspTypeByTopThree(mobile);
        }
        if(isp==null) isp = "";
        return isp;
    }

    /**
     * 根据手机号运营商类型，处理短信内容的签名，前置，后置
     * @param content
     * @return
     */
    public String modifyContentToSignByIsp(String content){
        StringBuffer stringBuffer = new StringBuffer();
        String sign = content.substring(content.indexOf("【"),content.indexOf("】")+1);//签名体
        String contentUnSign = content.replace(sign,"");//不含签名的内容体
        stringBuffer.append(sign).append(contentUnSign);
        return stringBuffer.toString();
    }

    /**
     * 为SDK调用获取最佳通道
     * @param customer 当前发送消息客户
     * @param isp 当前目标要发送的手机运营商
     * @param isMain 是否从主通道中获取最佳通道
     * @return
     */
    public MessageContainer getBestContainer(Customer customer, String isp, boolean isMain, boolean isClient, boolean isInfinite) {
        MessageContainer result = null;
        if(customer==null) return null;
        List<AssignedTunnel> assignedTunnelList = customer.getAssignedTunnelList();
        //优先获取相应运营商的主通道,从通道的获取待定规则
        List<MessageContainer> allUsableContainer = Lists.newArrayList();
        //logger.debug("wsy_assignedTunnelList是否为空"+assignedTunnelList);
        if(assignedTunnelList!=null && !assignedTunnelList.isEmpty()) {
            //已分配通道,可以进行通道操作
            for(AssignedTunnel at : assignedTunnelList) {

                if(at!=null && ("1".equals(at.getIsMainTunnel()) == isMain) && isp.equals(at.getSupportIsp())) {
                    allUsableContainer.addAll(at.getAllUsableContainer());
                }
            }
        }
        logger.debug("wsy_allUsableContainer是否为空"+allUsableContainer);
        if(!allUsableContainer.isEmpty()) {
            if(isInfinite) {
                result = getBestContainerInList(allUsableContainer, -1, isClient);
            }else {
                result = getBestContainerInList(allUsableContainer, 0, isClient);
            }
        }
       // logger.debug("wsy_获取到的通道"+result);
        return result;
    }

    public MessageContainer getBestContainerInListByTunnelId(String tdId,boolean isClient) {
        MessageContainer result = null;
        if(StringUtils.isBlank(tdId)) return result;
        String[] tmp = tdId.split("_");
        String tunnelId = "";
        if(tmp.length==2) {
            tunnelId = tmp[1];
        }else if(tmp.length==3) {
            tunnelId = tmp[2];
        }else {
            return null;
        }
        Tunnel tunnel = tunnelService.get(new Tunnel(tunnelId));
        if(tunnel==null) return result;

        List<MessageContainer> allAvailableContainer = Lists.newArrayList();
        String preFix = "";
        if("1".equals(tunnel.getType())) {
            preFix = "STRIGHT_";
            int cCount = tunnel.getConnectCount();
            for(int i=0;i<cCount;i++) {
                MessageContainer tmpContainer = ContainerRepo.getMessagerContainerMap().get(preFix+tunnel.getId()+"_"+(i+1));
                if(tmpContainer==null) continue;
                allAvailableContainer.add(tmpContainer);
            }
        }
        if("2".equals(tunnel.getType()) || "4".equals(tunnel.getType())) {
            preFix = "3STRIGHT_";
            int cCount = tunnel.getConnectCount();
            for(int i=0;i<cCount;i++) {
                MessageContainer tmpContainer = ContainerRepo.getMessagerContainerMap().get(preFix+tunnel.getId()+"_"+(i+1));
                if(tmpContainer==null) continue;
                allAvailableContainer.add(tmpContainer);
            }
        }
        if("3".equals(tunnel.getType())) {
            preFix = "NOT_STRIGHT_";
            MessageContainer tmpContainer = ContainerRepo.getMessagerContainerMap().get(preFix+tunnel.getId());
            allAvailableContainer.add(tmpContainer);
        }
        return getBestContainerInList(allAvailableContainer, 0,isClient);
    }

    /**
     * 根据ContainerId获取最佳通道
     * @param tdId
     * @return
     */
    public MessageContainer getBestContainerByTunnelId(String tdId, boolean isClient) {
        MessageContainer result = null;
        if(StringUtils.isBlank(tdId)) return result;
        String[] tmp = tdId.split("_");
        String tunnelId = "";
        if(tmp.length==2) {
            tunnelId = tmp[1];
        }else if(tmp.length==3) {
            tunnelId = tmp[2];
        }else {
            return null;
        }
        Tunnel tunnel = tunnelService.get(new Tunnel(tunnelId));
        if(tunnel==null) return result;

        List<MessageContainer> allAvailableContainer = Lists.newArrayList();
        if("1".equals(tunnel.getType())||"2".equals(tunnel.getType()) || "4".equals(tunnel.getType())) {
            int cCount = tunnel.getConnectCount();
            for(int i=0;i<cCount;i++) {
                MessageContainer tmpContainer = ContainerRepo.getMessagerContainerMap().get(tunnel.getTdNameWithOutConnectNo()+"_"+(i+1));
                if(tmpContainer==null) continue;
                allAvailableContainer.add(tmpContainer);
            }
        }
        if("3".equals(tunnel.getType())) {
            MessageContainer tmpContainer = ContainerRepo.getMessagerContainerMap().get(tunnel.getTdNameWithOutConnectNo());
            allAvailableContainer.add(tmpContainer);
        }
        return getBestContainerInList(allAvailableContainer, 0, isClient);
    }

    /**
     * 获取SRCiD
     * @param tunnel
     * @param msgRecord
     * @return
     */
    public String getSrcId(Tunnel tunnel, MsgRecord msgRecord) {
        String result = null;
        if(tunnel!=null && msgRecord!=null && msgRecord.getCustomer()!=null && StringUtils.isNotBlank(msgRecord.getCustomer().getSpecialServiceNumber())) {
            if(StringUtils.isNotBlank(tunnel.getEnterCode())) {
                result = "";
                result += tunnel.getEnterCode();
            }else {
                result = "";
            }
            result += msgRecord.getCustomer().getSpecialServiceNumber();
            if(StringUtils.isNotBlank(msgRecord.getExt())) {
                result += msgRecord.getExt();
            }
        }
        return result;
    }

    /**
     * 在指定Container列表中获取最佳发送通道
     * @param mcList
     * @param tryCount
     * @return
     */
    private MessageContainer getBestContainerInList(List<MessageContainer> mcList, int tryCount, boolean isClient) {
        MessageContainer result = null;
        for(MessageContainer mc : mcList) {
            if(mc instanceof AbstractThirdPartyMessageContainer) {
                //第三方
                result = mc;
                break;
            }else if(mc instanceof AbstractStraightMessageContainer){
                //直连
                AbstractStraightMessageContainer smc = (AbstractStraightMessageContainer) mc;
                if(smc.checkContainerIsActive() && smc.checkSendInterval() && smc.checkWindowMapSize()) {
                    result = mc;
                    break;
                }
            }
        }
        if(result!=null) {
            return result;
        }else {
            if(tryCount== -1) {
                //无限尝试
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getBestContainerInList(mcList, tryCount, isClient);
            }else {
                int bestContainerFindTryCountNow = bestContainerFindTryCount;
                if(isClient) bestContainerFindTryCountNow = bestContainerFindTryCount*10;
                if(tryCount < bestContainerFindTryCountNow) {
                    tryCount++;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                    return getBestContainerInList(mcList, tryCount, isClient);
                }else {
                    return null;
                }
            }
        }

    }

    /**
     * 根据通道类型生成相应的通道发送请求实体
     * @param container
     * @param msgRecord
     * @return
     */
    public AbstractMessageSend generateMessageSendByContainer(MessageContainer container, MsgRecord msgRecord) {
        AbstractMessageSend result = null;
        if(container instanceof AbstractStraightMessageContainer) {
            //直连通道发送
            if(container instanceof CmppMessagerContainer) {
                result = new CmppMessageSend(msgRecord);
            }else if(container instanceof CmppMessagerContainerV2) {
                result = new CmppMessageSendV2(msgRecord);
            }
        }else if(container instanceof AbstractThirdPartyMessageContainer) {
            //第三方通道发送
            if(container instanceof ThirdMdMessagerContainer) {
                result = new ThirdMessageSendMd(msgRecord);
            }else if(container instanceof ThirdTykjMessagerContainer) {
                result = new ThirdMessageSendTy(msgRecord);
            }else if(container instanceof ThirdYmqyMessagerContainer) {
                result = new ThirdMessageSendYmqy(msgRecord);
            }else if(container instanceof ThirdYxMessagerContainer) {
                result = new ThirdYxSendMsgresult(msgRecord);
            }else if(container instanceof  ThirdAhMessagerContainer){
                result = new ThirdMessageSendAh(msgRecord);
            }else if(container instanceof  ThirdQxtMessagerContainer){
                result = new ThirdQxtSendMsgresult(msgRecord);
            }else if(container instanceof  ThirdHlMessagerContainer){
                result = new ThirdMessageSendHl(msgRecord);
            }else if(container instanceof  ThirdKlwsMessagerContainer){
                result = new ThirdMessageSendklws(msgRecord);
            }else if (container instanceof  ThirdQyxsMessagerContainer){
                result = new ThirdMessageSendQyxs(msgRecord);
            }
        }
        return result;
    }

    /**
     * 生成客户端需要打包发送的MessageSend
     * @param msgRecordList
     * @param messageContainer
     * @param originRecord
     * @return
     */
    public List<AbstractMessageSend> handleClientMsgRecord(List<MsgRecord> msgRecordList, MessageContainer messageContainer, MsgRecord originRecord) {
        List<MsgRecord> result = Lists.newArrayList();
        List<AbstractMessageSend> msList = Lists.newArrayList();
        Tunnel tunnel = messageContainer.getTunnel();
        String srcId  = getSrcId(tunnel, originRecord);
        if("3".equals(messageContainer.getTunnelType())) {
            //需要打包
            int packageCount = messageContainer.getTunnel().getSendPackageSize();
            int groupCount = 0;
            if(msgRecordList.size()<packageCount) {
                MsgRecord mr = mergeMsgRecord(msgRecordList, originRecord);
                AbstractMessageSend ms = generateMessageSendByContainer(messageContainer, mr);
                ms.setTunnel(tunnel);
                ms.setSrcId(srcId);
                msList.add(ms);
            }else {
                groupCount = msgRecordList.size()/packageCount + (msgRecordList.size()%packageCount==0? 0 : 1);
                for(int i=0;i<groupCount;i++) {
                    int end = (i+1)*packageCount;
                    if(end>msgRecordList.size()) {
                        end = msgRecordList.size();
                    }
                    MsgRecord mr = mergeMsgRecord(msgRecordList.subList(i*packageCount, end), originRecord);
                    AbstractMessageSend ms = generateMessageSendByContainer(messageContainer, mr);
                    ms.setTunnel(tunnel);
                    ms.setSrcId(srcId);
                    msList.add(ms);
                }
            }
        }else {
            //直连通道不需要打包
            result = msgRecordList;
            for(MsgRecord mr : result) {
                if(mr==null) continue;
                mr.setCustomer(originRecord.getCustomer());
                AbstractMessageSend ms = generateMessageSendByContainer(messageContainer, mr);
                ms.setTunnel(tunnel);
                ms.setSrcId(srcId);
                msList.add(ms);
            }
        }
        return msList;
    }

    private MsgRecord mergeMsgRecord(List<MsgRecord> msgRecordList, MsgRecord originRecord) {
        MsgRecord result = new MsgRecord();
        List<String> mobileList =  Lists.newArrayList();
        String content = null;
        for(MsgRecord mr : msgRecordList) {
            if(mr==null || StringUtils.isBlank(mr.getMobile()) || StringUtils.isBlank(mr.getContent())) continue;
            if(StringUtils.isBlank(content)) content = mr.getContent();
            mobileList.add(mr.getMobile());
        }
        result.setCustomer(originRecord.getCustomer());
        result.setContent(content);
        result.setMobile(StringUtils.join(mobileList, ","));
        result.setMsgRecordList(msgRecordList);
        return result;
    }

    /**
     * 为SDK调用获取最佳通道
     * @param customer 当前发送消息客户
     * @param isp 当前目标要发送的手机运营商
     * @return
     */
    public MessageCxContainer getBestCxContainer(Customer customer, String isp) {
        MessageCxContainer result = null;
        if(customer==null) return null;
        AssignedCxTunnel assignedCxTunnel = new AssignedCxTunnel();
        assignedCxTunnel.setCustomer(customer);
        assignedCxTunnel.setIsMainTunnel("1");
        List<AssignedCxTunnel> assignedTunnelList = assignedCxTunnelDao.findList(assignedCxTunnel);
        //优先获取相应运营商的主通道,从通道的获取待定规则
        List<MessageCxContainer> allUsableContainer = Lists.newArrayList();
        if(assignedTunnelList!=null && !assignedTunnelList.isEmpty()) {
            //已分配通道,可以进行通道操作
            for(AssignedCxTunnel at : assignedTunnelList) {
                if(at!=null && isp.equals(at.getSupportIsp())) {
                    allUsableContainer.addAll(at.getAllUsableContainer());
                }
            }
        }
        if(!allUsableContainer.isEmpty()) {
            result = getBestCxContainerInList(allUsableContainer);
        }
        return result;
    }

    /**
     * 在指定Container列表中获取最佳发送通道
     * @param mcList
     * @return
     */
    private MessageCxContainer getBestCxContainerInList(List<MessageCxContainer> mcList) {
        MessageCxContainer result = null;
        for(MessageCxContainer mc : mcList) {
            if(mc instanceof AbstractThirdCxPartyMessageContainer) {
                //第三方
                result = mc;
                break;
            }else if(mc instanceof AbstractStraightMessageContainer){
                //直连
                AbstractStraightMessageContainer smc = (AbstractStraightMessageContainer) mc;
                if(smc.checkContainerIsActive() && smc.checkSendInterval() && smc.checkWindowMapSize()) {
                    result = mc;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 生成客户端需要打包发送的彩信
     * @param msgRecordList
     * @param originRecord
     * @return
     */
    public List<MsgCxRecord> handleClientCxMsgRecord(List<MsgCxRecord> msgRecordList, MessageCxContainer messageCxContainer, MsgCxRecord originRecord) {
        List<MsgCxRecord> result = Lists.newArrayList();
        CxTunnel tunnel = messageCxContainer.getTunnel();
        String srcId  = getSrcId(tunnel, originRecord);
        if("3".equals(messageCxContainer.getTunnelType())) {
            //需要打包
            int packageCount = messageCxContainer.getTunnel().getSendPackageSize();
            int groupCount = 0;
            if(msgRecordList.size()<packageCount) {
                MsgCxRecord mr = mergeMsgCxRecord(msgRecordList, originRecord);
                result.add(mr);
            }else {
                groupCount = msgRecordList.size()/packageCount + (msgRecordList.size()%packageCount==0? 0 : 1);
                for(int i=0;i<groupCount;i++) {
                    int end = (i+1)*packageCount;
                    if(end>msgRecordList.size()) {
                        end = msgRecordList.size();
                    }
                    MsgCxRecord mr = mergeMsgCxRecord(msgRecordList.subList(i*packageCount, end), originRecord);
                    result.add(mr);
                }
            }
        }/*else {
            //直连通道不需要打包
            result = msgRecordList;
            for(MsgRecord mr : result) {
                if(mr==null) continue;
                mr.setCustomer(originRecord.getCustomer());
                AbstractMessageSend ms = generateMessageSendByContainer(messageContainer, mr);
                ms.setTunnel(tunnel);
                ms.setSrcId(srcId);
                msList.add(ms);
            }
        }*/
        return result;
    }

    /**
     * 获取SRCiD
     * @param tunnel
     * @return
     */
    public String getSrcId(CxTunnel tunnel, MsgCxRecord msgRecord) {
        String result = null;
        if(tunnel!=null && msgRecord!=null && msgRecord.getCustomer()!=null && StringUtils.isNotBlank(msgRecord.getCustomer().getSpecialServiceNumber())) {
            if(StringUtils.isNotBlank(tunnel.getEnterCode())) {
                result = "";
                result += tunnel.getEnterCode();
            }else {
                result = "";
            }
            result += msgRecord.getCustomer().getSpecialServiceNumber();
            if(StringUtils.isNotBlank(msgRecord.getExt())) {
                result += msgRecord.getExt();
            }
        }
        return result;
    }

    private MsgCxRecord mergeMsgCxRecord(List<MsgCxRecord> msgCxRecordList, MsgCxRecord originRecord) {
        MsgCxRecord result = new MsgCxRecord();
        List<String> mobileList =  Lists.newArrayList();
        String content = null;
        String title = null;
        for(MsgCxRecord mr : msgCxRecordList) {
            if(mr==null || StringUtils.isBlank(mr.getMobile()) || StringUtils.isBlank(mr.getContent())) continue;
            if(StringUtils.isBlank(title)) title = mr.getTitle();
            if(StringUtils.isBlank(content)) content = mr.getContent();
            mobileList.add(mr.getMobile());
        }
        result.setCustomer(originRecord.getCustomer());
        result.setContent(content);
        result.setTitle(title);
        result.setMobile(StringUtils.join(mobileList, ","));
        result.setMsgRecordList(msgCxRecordList);
        return result;
    }

    /**
     * 根据通道类型生成相应的通道发送请求实体
     * @param container
     * @param msgRecord
     * @return
     */
    public AbstractMessageSend generateMessageSendByContainerCx(MessageCxContainer container, MsgCxRecord msgRecord) {
        AbstractMessageSend result = null;
        if(container instanceof AbstractThirdPartyMessageContainer) {
            //第三方通道发送
            if(container instanceof ThirdCxMdMessagerContainer) {
                result = new ThirdMessageSendMd(msgRecord);
            }
        }
        return result;
    }
}
