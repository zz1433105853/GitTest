package com.ty.modules.msg.entity;

import com.google.common.collect.Lists;
import com.ty.common.persistence.DataEntity;
import com.ty.common.utils.StringUtils;
import com.ty.modules.tunnel.send.container.common.ContainerRepo;

import java.util.List;

/**
 * Created by ljb on 2016/6/14.
 */
public class CxTunnel extends DataEntity<CxTunnel> {

    private static final long serialVersionUID = -8382484635316770474L;

    private String type;
    private String sequenceNumber;
    private String gatewayIp;
    private Integer gatewayPort;
    private String enterCode;
    private String spId;
    private String gatewayPassword;
    private int connectCount;
    private String url;
    private String account;
    private String password;
    private int sendSpeed;
    private String restRemindMobiles;
    private int restRemindCount;
    private String status;
    private String supportIsp;
    private String serviceId;
    private int sendPackageSize;
    private Integer contentLimitCount;
    private String containerClassName;
    private String protocolType;
    private String protocolVersion;

    private List<String> supportIspList = Lists.newArrayList();

    public CxTunnel() {
    }

    public CxTunnel(String id) {
        super(id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }

    public Integer getGatewayPort() {
        return gatewayPort;
    }

    public void setGatewayPort(Integer gatewayPort) {
        this.gatewayPort = gatewayPort;
    }

    public String getEnterCode() {
        return enterCode;
    }

    public void setEnterCode(String enterCode) {
        this.enterCode = enterCode;
    }

    public String getGatewayPassword() {
        return gatewayPassword;
    }

    public void setGatewayPassword(String gatewayPassword) {
        this.gatewayPassword = gatewayPassword;
    }

    public int getConnectCount() {
        return connectCount;
    }

    public void setConnectCount(int connectCount) {
        this.connectCount = connectCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getSupportIspList() {
        return supportIspList;
    }

    public void setSupportIspList(List<String> supportIspList) {
        this.supportIspList = supportIspList;
    }

    public String getSupportIsp() {
        return supportIsp;
    }

    public void setSupportIsp(String supportIsp) {
        this.supportIsp = supportIsp;
    }

    public String getSupportIsps() {
        return StringUtils.join(getSupportIspList(), ",");
    }

    public void setSupportIsps(String isps) {
        supportIspList = Lists.newArrayList();
        if (isps != null){
            String[] isp = StringUtils.split(isps, ",");
            setSupportIspList(Lists.newArrayList(isp));
        }
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId;
    }

    public String getContainerClassName() {
        return containerClassName;
    }

    public void setContainerClassName(String containerClassName) {
        this.containerClassName = containerClassName;
    }

    public Integer getContentLimitCount() {
        return contentLimitCount;
    }

    public void setContentLimitCount(Integer contentLimitCount) {
        this.contentLimitCount = contentLimitCount;
    }

    public String getRestRemindMobiles() {
        return restRemindMobiles;
    }

    public void setRestRemindMobiles(String restRemindMobiles) {
        this.restRemindMobiles = restRemindMobiles;
    }

    public int getRestRemindCount() {
        return restRemindCount;
    }

    public void setRestRemindCount(int restRemindCount) {
        this.restRemindCount = restRemindCount;
    }

    public int getSendPackageSize() {
        return sendPackageSize;
    }

    public void setSendPackageSize(int sendPackageSize) {
        this.sendPackageSize = sendPackageSize;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getSendSpeed() {
        return sendSpeed;
    }

    public void setSendSpeed(int sendSpeed) {
        this.sendSpeed = sendSpeed;
    }

    public String getNickName() {
        return type+sequenceNumber;
    }

    /**
     * 获取没有连接数序号的通道名称  type_id
     * @return
     */
    public String getTdNameWithOutConnectNo() {
        String result = "";
        switch (type) {
            case "1":
                result = "STRIGHT_"+this.id;
                break;
            case "2":
            case "4":
                result = "3STRIGHT_"+this.id;
                break;
            case "3":
                result = "NOT_STRIGHT_"+this.id;
                break;
            default:
                break;
        }
        return result;
    }

    public List<String> getResultTdNames() {
        List<String> result = Lists.newArrayList();
        switch (type) {
            case "1":
            case "2":
            case "4":
                for (int i = 1; i <= connectCount; i++) {
                    result.add(getTdNameWithOutConnectNo() + "_" + i);
                }
                break;
            case "3":
                result.add(getTdNameWithOutConnectNo());
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 检测当前通道是否已经加入了Repo
     * @return
     */
    public boolean checkExistInRepo() {
        List<String> tdnames = getResultTdNames();
        for(String name : tdnames) {
            if(ContainerRepo.getMessagerContainerMap().containsKey(name)) {
                return true;
            }
        }
        return false;
    }

}
