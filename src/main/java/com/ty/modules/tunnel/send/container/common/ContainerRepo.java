package com.ty.modules.tunnel.send.container.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.CxTunnel;
import com.ty.modules.sys.service.*;
import com.ty.modules.msg.entity.Tunnel;
import com.ty.modules.tunnel.reply.disruptor.MessageReplyEventProducer;
import com.ty.modules.tunnel.report.disruptor.MessageReportEventProducer;
import com.ty.modules.tunnel.response.disruptor.MessageResponseEventProducer;
import com.ty.modules.tunnel.send.container.cx.MessageCxContainer;
import com.ty.modules.tunnel.send.container.cx.impl.AbstractThirdCxPartyMessageContainer;
import com.ty.modules.tunnel.send.container.entity.cmpp.MsgConfig;
import com.ty.modules.tunnel.send.container.impl.AbstractStraightMessageContainer;
import com.ty.modules.tunnel.send.container.impl.AbstractThirdPartyMessageContainer;
import com.ty.modules.tunnel.send.container.impl.CmppMessagerContainer;
import com.ty.modules.tunnel.send.container.impl.CmppMessagerContainerV2;
import com.ty.modules.tunnel.send.container.type.MessageContainer;
import com.ty.modules.tunnel.send.container.type.StraightMessageContainer;
import org.apache.log4j.Logger;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * Created by ljb on 2017/4/13 17:00.
 */
@Component
@Lazy(false)
@DependsOn(value = {"springContextHolder"})
public class ContainerRepo {

    private static Logger logger= Logger.getLogger(ContainerRepo.class);

    private static Map<String, MessageContainer> messagerContainerMap = Maps.newConcurrentMap();
    private static Map<String, MessageCxContainer> messagerCxContainerMap = Maps.newConcurrentMap();


    @Autowired
    private LoggingFilter loggingFilter;
    @Autowired
    private ProtocolCodecFilter codecFilter;
    @Autowired
    private ProtocolCodecFilter codecFilterV2;
    @Autowired
    private TunnelService tunnelService;
    @Autowired
    private CxTunnelService cxTunnelService;
    @Autowired
    private MsgRecordService msgRecordService;
    @Autowired
    private MsgResponseService msgResponseService;
    @Autowired
    private MsgReportService msgReportService;
    @Autowired
    private MsgReplyService msgReplyService;

    @Autowired
    private MessageResponseEventProducer messageResponseEventProducer;
    @Autowired
    private MessageReportEventProducer messageReportEventProducer;
    @Autowired
    private MessageReplyEventProducer messageReplyEventProducer;

    @Value("${MsgRecord.Client}")
    private String msgRecordClientKey;

    @Value("${server.count}")
    private int serverCount;

    @Value("${MsgRecord.Sdk}")
    private String msgRecordSdkKey;

    @PostConstruct
    public void init() {
        initTunnelFromDB("1","2","3","4");
        //初始化彩信通道
        initCxTunnelFromDB("1","2","3","4");
    }

    /**
     * 通过类型初始化数据库中的通道
     * @param type
     */
    private void initTunnelFromDB(String ...type) {
        if(type==null || type.length==0) return;
        logger.info(StringUtils.builderString("初始化通道开始:", StringUtils.join(type, ",")));
        List<Tunnel> tunnelFromDb = Lists.newArrayList();
        Tunnel tunnelForQuery = new Tunnel();
        tunnelForQuery.setStatus("notest");
        for(String tunnelType : type) {
            tunnelForQuery.setType(tunnelType);
            List<Tunnel> tunnelList = tunnelService.findList(tunnelForQuery);
            if(tunnelList==null || tunnelList.isEmpty())continue;
            tunnelFromDb.addAll(tunnelList);
        }
        for(Tunnel t : tunnelFromDb) {
            if(t==null || StringUtils.isBlank(t.getId())) continue;
            initTunnel(t);
        }
        logger.info(StringUtils.builderString("初始化通道结束:", StringUtils.join(type, ",")));

    }

    /**
     * 通过类型初始化数据库中的彩信通道
     * @param type
     */
    private void initCxTunnelFromDB(String ...type) {
        if(type==null || type.length==0) return;
        logger.info(StringUtils.builderString("初始化彩信通道开始:", StringUtils.join(type, ",")));
        List<CxTunnel> tunnelFromDb = Lists.newArrayList();
        CxTunnel tunnelForQuery = new CxTunnel();
        tunnelForQuery.setStatus("notest");
        for(String tunnelType : type) {
            tunnelForQuery.setType(tunnelType);
            List<CxTunnel> tunnelList = cxTunnelService.findList(tunnelForQuery);
            if(tunnelList==null || tunnelList.isEmpty())continue;
            tunnelFromDb.addAll(tunnelList);
        }
        for(CxTunnel t : tunnelFromDb) {
            if(t==null || StringUtils.isBlank(t.getId())) continue;
            initCxTunnel(t);
        }
        logger.info(StringUtils.builderString("初始化通道结束:", StringUtils.join(type, ",")));

    }

    /**
     * 断开所有直连通道
     */
    public void destroy() {
        for(MessageContainer mc : messagerContainerMap.values()) {
            if(mc instanceof StraightMessageContainer) {
                if(mc.checkContainerIsActive() && mc.checkContainerIsActive()) {
                    ((StraightMessageContainer) mc).cancelISMG();
                }
            }
        }
    }


    /**
     * 向Repo添加通道
     * @param tdId
     * @return
     */
    public boolean addTunnel(String tdId) {
        Tunnel tunnel = tunnelService.get(new Tunnel(tdId));
        //检测是否已经在Repo里面
        if(tunnel != null){
            if(tunnel.checkExistInRepo()) {
                return false;
            }else {
                initTunnel(tunnel);
                return true;
            }
        }else{
            return  false;
        }
    }


    public boolean delTunnel(String tdId) {
        if(!messagerContainerMap.containsKey(tdId)) return false;
        MessageContainer mc = messagerContainerMap.get(tdId);
        try {
            if(mc instanceof AbstractStraightMessageContainer && mc.checkContainerIsActive()) {
                ((AbstractStraightMessageContainer)mc).cancelISMG();
            }
            messagerContainerMap.remove(tdId);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 重新加载通道信息
     * @param tdId
     * @return
     */
    public boolean reloadTunnel(String tdId) {
        MessageContainer mc =messagerContainerMap.get(tdId);
        if(mc!=null) {
            Tunnel tunnel = mc.getTunnel();
            if(tunnel!=null && StringUtils.isNotBlank(tunnel.getId())) {
                String tunnelId = tunnel.getId();
                tunnel = tunnelService.get(new Tunnel(tunnelId));
                if(tunnel!=null) {
                    if(mc instanceof StraightMessageContainer && mc.checkContainerIsActive()) {
                        ((StraightMessageContainer)mc).cancelISMG();
                    }
                    reloadSingleContainer(tdId, tunnel);
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    /**
     * 重新加载单一条通道，包括直连的单个连接或者是第三方通道
     * @param tdId
     * @param tunnel
     */
    private void reloadSingleContainer(String tdId, Tunnel tunnel) {
        if(tunnel==null || StringUtils.isBlank(tdId)) return;
        String tunnelType = tunnel.getType();
        if(StringUtils.inString(tunnelType, "1","2","4")) {
            StraightMessageContainer smc = (StraightMessageContainer) messagerContainerMap.get(tdId);
            if(smc==null) return;
            int connectCode =  smc.getMsgConfig().getConnectNumber();
            MsgConfig tmpCfg = new MsgConfig(
                    tunnel.getGatewayIp(),
                    tunnel.getGatewayPort(),
                    3,
                    tunnel.getSpId(),
                    tunnel.getGatewayPassword(),
                    tunnel.getEnterCode(),
                    tunnel.getServiceId(),
                    tunnel.getProtocolType(),
                    tunnel.getProtocolVersion(),
                    tunnel.getSendSpeed(),
                    tunnel.getType(),
                    tunnel.getSequenceNumber()
            );
            tmpCfg.setConnectNumber(connectCode);
            tmpCfg.setTdName(tdId);
            AbstractStraightMessageContainer cmppContainer = null;
            if("3".equals(tunnel.getProtocolVersion())) {
                cmppContainer = new CmppMessagerContainer(tunnel, tmpCfg, loggingFilter, codecFilter, messageResponseEventProducer, messageReportEventProducer, messageReplyEventProducer);
            }else {
                cmppContainer = new CmppMessagerContainerV2(tunnel, tmpCfg, loggingFilter, codecFilterV2, messageResponseEventProducer, messageReportEventProducer, messageReplyEventProducer);
            }
            messagerContainerMap.put(tdId, cmppContainer);
        }else {
            initThirdPartyTunnel(tunnel);
        }
    }

    /**
     * 检测是否存在此通道
     * @param tdId
     * @return
     */
    public static boolean existContainer(String tdId) {
        return getMessagerContainerMap().containsKey(tdId);
    }

    /**
     * 初始化通道
     * @param tunnel
     */
    private void initTunnel(Tunnel tunnel) {
        if(tunnel==null)return;
        String tunnelType = tunnel.getType();
        switch (tunnelType) {
            case "1":
            case "2":
            case "4":
                initCmppTunnel(tunnel);
                break;
            case "3":
                initThirdPartyTunnel(tunnel);
                break;
            default:
                logger.info("为定义的通道类型，初始化失败");
                break;
        }
    }

    /**
     * 初始化彩信通道
     * @param tunnel
     */
    private void initCxTunnel(CxTunnel tunnel) {
        if(tunnel==null)return;
        String tunnelType = tunnel.getType();
        switch (tunnelType) {
            case "1":
            case "2":
            case "4":
               // initCmppCxTunnel(tunnel);
                break;
            case "3":
                initThirdPartyCxTunnel(tunnel);
                break;
            default:
                logger.info("为定义的通道类型，初始化失败");
                break;
        }
    }

    /**
     * 初始化直连通道.
     * @param tunnel
     */
    private void initCmppTunnel(Tunnel tunnel) {
        if(tunnel==null) return;
        int connectCount = tunnel.getConnectCount()/serverCount;
        for(int i=0;i<connectCount;i++) {
            MsgConfig tmpCfg = new MsgConfig(
                    tunnel.getGatewayIp(),
                    tunnel.getGatewayPort(),
                    3,
                    tunnel.getSpId(),
                    tunnel.getGatewayPassword(),
                    tunnel.getEnterCode(),
                    tunnel.getServiceId(),
                    tunnel.getProtocolType(),
                    tunnel.getProtocolVersion(),
                    tunnel.getSendSpeed(),
                    tunnel.getType(),
                    tunnel.getSequenceNumber()
            );
            tmpCfg.setId(tunnel.getId());
            tmpCfg.setConnectNumber(i+1);
            tmpCfg.setTdName(tunnel.getTdNameWithOutConnectNo()+"_"+(i+1));
            AbstractStraightMessageContainer cmppContainer = null;
            if("3".equals(tunnel.getProtocolVersion())) {
                cmppContainer = new CmppMessagerContainer(tunnel, tmpCfg, loggingFilter, codecFilter, messageResponseEventProducer, messageReportEventProducer, messageReplyEventProducer);
            }else {
                cmppContainer = new CmppMessagerContainerV2(tunnel, tmpCfg, loggingFilter, codecFilterV2, messageResponseEventProducer, messageReportEventProducer, messageReplyEventProducer);
            }
            messagerContainerMap.put(tunnel.getTdNameWithOutConnectNo()+"_"+(i+1), cmppContainer);
        }
    }

    /**
     * 初始化直连通道.
     * @param tunnel
     */
    private void initCmppCxTunnel(CxTunnel tunnel) {
        if(tunnel==null) return;
        int connectCount = tunnel.getConnectCount()/serverCount;
        for(int i=0;i<connectCount;i++) {
            MsgConfig tmpCfg = new MsgConfig(
                    tunnel.getGatewayIp(),
                    tunnel.getGatewayPort(),
                    3,
                    tunnel.getSpId(),
                    tunnel.getGatewayPassword(),
                    tunnel.getEnterCode(),
                    tunnel.getServiceId(),
                    tunnel.getProtocolType(),
                    tunnel.getProtocolVersion(),
                    tunnel.getSendSpeed(),
                    tunnel.getType(),
                    tunnel.getSequenceNumber()
            );
            tmpCfg.setId(tunnel.getId());
            tmpCfg.setConnectNumber(i+1);
            tmpCfg.setTdName(tunnel.getTdNameWithOutConnectNo()+"_"+(i+1));
            AbstractStraightMessageContainer cmppContainer = null;
            if("3".equals(tunnel.getProtocolVersion())) {
             //   cmppContainer = new CmppMessagerContainer(tunnel, tmpCfg, loggingFilter, codecFilter, messageResponseEventProducer, messageReportEventProducer, messageReplyEventProducer);
            }else {
              //  cmppContainer = new CmppMessagerContainerV2(tunnel, tmpCfg, loggingFilter, codecFilterV2, messageResponseEventProducer, messageReportEventProducer, messageReplyEventProducer);
            }
            messagerContainerMap.put(tunnel.getTdNameWithOutConnectNo()+"_"+(i+1), cmppContainer);
        }
    }

    /**
     * 初始化第三方通道
     * @param tunnel
     */
    private void initThirdPartyTunnel(Tunnel tunnel) {
        if(tunnel==null) return;
        String containerClassName = tunnel.getContainerClassName();
        AbstractThirdPartyMessageContainer thirdContainer = null;
        try {
            Class<?> clazz = Class.forName(containerClassName);
            Constructor<?> cons[] = clazz.getConstructors();
            thirdContainer = (AbstractThirdPartyMessageContainer) cons[0].newInstance(tunnel,
                    messageResponseEventProducer,
                    messageReportEventProducer,
                    messageReplyEventProducer,
                    msgRecordService,
                    msgResponseService,
                    msgReportService,
                    msgReplyService);
            messagerContainerMap.put(tunnel.getTdNameWithOutConnectNo(), thirdContainer);
        } catch (ClassNotFoundException e) {
            logger.error(StringUtils.builderString("初始化通道：", tunnel.getTdNameWithOutConnectNo(), "失败，不存在此通道:", e.getMessage()));
        } catch (Exception e) {
            logger.error(StringUtils.builderString("初始化通道：", tunnel.getTdNameWithOutConnectNo(), "失败，未知原因:", e.getMessage()));
        }
    }

    /**
     * 初始化第三方彩信通道
     * caixin
     * @param tunnel
     */
    private void initThirdPartyCxTunnel(CxTunnel tunnel) {
        if(tunnel==null) return;
        String containerClassName = tunnel.getContainerClassName();
        AbstractThirdCxPartyMessageContainer thirdContainer = null;
        try {
            Class<?> clazz = Class.forName(containerClassName);
            Constructor<?> cons[] = clazz.getConstructors();
            thirdContainer = (AbstractThirdCxPartyMessageContainer) cons[0].newInstance(tunnel,
                    msgResponseService,
                    msgReportService);
            messagerCxContainerMap.put(tunnel.getTdNameWithOutConnectNo(), thirdContainer);
        } catch (ClassNotFoundException e) {
            logger.error(StringUtils.builderString("初始化通道：", tunnel.getTdNameWithOutConnectNo(), "失败，请检查类名或路径:", e.getMessage()));
        } catch (Exception e) {
            logger.error(StringUtils.builderString("初始化通道：", tunnel.getTdNameWithOutConnectNo(), "失败，未知原因:", e.getMessage()));
        }
    }

    /**
     * 获取通道map
     * @return
     */
    public static Map<String, MessageContainer> getMessagerContainerMap() {
        return messagerContainerMap;
    }

    /**
     * 获取caixin通道map
     * @return
     */
    public static Map<String, MessageCxContainer> getMessagerCxContainerMap() {
        return messagerCxContainerMap;
    }

    public static Map<String, List<AbstractStraightMessageContainer>> groupContainerMap(List<AbstractStraightMessageContainer> mcList) {
        Map<String, List<AbstractStraightMessageContainer>> result = Maps.newHashMap();
        for(AbstractStraightMessageContainer mc : mcList) {
            List<AbstractStraightMessageContainer> mcListNow = result.get(mc.getTunnel().getNickName());
            if(mcListNow==null) mcListNow = Lists.newArrayList();
            mcListNow.add(mc);
            result.put(mc.getTunnel().getNickName(), mcListNow);
        }
        return result;
    }



}
