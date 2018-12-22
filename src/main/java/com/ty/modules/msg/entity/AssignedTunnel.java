package com.ty.modules.msg.entity;


import com.google.common.collect.Lists;
import com.ty.common.persistence.DataEntity;
import com.ty.common.utils.StringUtils;
import com.ty.modules.tunnel.send.container.common.ContainerRepo;
import com.ty.modules.tunnel.send.container.type.MessageContainer;

import java.util.List;

/**
 * Created by Ysw on 2016/6/17.
 */
public class AssignedTunnel extends DataEntity<AssignedTunnel> {

    private static final long serialVersionUID = -3682538788721854865L;
    private Customer customer;
    private Tunnel tunnel;
    private String supportIsp;
    private String groupCode;
    private String isMainTunnel;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Tunnel getTunnel() {
        return tunnel;
    }

    public void setTunnel(Tunnel tunnel) {
        this.tunnel = tunnel;
    }

    public String getSupportIsp() {
        return supportIsp;
    }

    public void setSupportIsp(String supportIsp) {
        this.supportIsp = supportIsp;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getIsMainTunnel() {
        return isMainTunnel;
    }

    public void setIsMainTunnel(String isMainTunnel) {
        this.isMainTunnel = isMainTunnel;
    }

    /**
     * 获取当前客户，当前通道可用的所有Container In Map List
     * @return
     */
    public List<MessageContainer> getAllUsableContainer() {
        List<MessageContainer> result = Lists.newArrayList();
        if(tunnel==null) return result;
        if(StringUtils.inString(tunnel.getType(), "1", "2","4")) {
            //直连
            String preFix = "STRIGHT_";
            if(StringUtils.inString(tunnel.getType(), "2","4")) {
                //第三方
                preFix = "3STRIGHT_";
            }
            int connCount = tunnel.getConnectCount();//最大连接数
            for(int i=1;i<connCount+1;i++) {
                if(ContainerRepo.getMessagerContainerMap().get(preFix+tunnel.getId()+"_"+i)!=null) {
                    result.add(ContainerRepo.getMessagerContainerMap().get(preFix+tunnel.getId()+"_"+i));
                }
            }
        }else {
            //非直连
            String containerId = "NOT_STRIGHT_"+tunnel.getId();
            if(ContainerRepo.getMessagerContainerMap().get(containerId)!=null) {
                result.add(ContainerRepo.getMessagerContainerMap().get(containerId));
            }
        }
        return result;
    }
}
