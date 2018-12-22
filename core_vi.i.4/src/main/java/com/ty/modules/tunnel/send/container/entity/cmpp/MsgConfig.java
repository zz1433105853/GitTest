package com.ty.modules.tunnel.send.container.entity.cmpp;


import com.ty.common.persistence.DataEntity;

/**
 * Created by tykjkf01 on 2016/5/20.
 */
public class MsgConfig extends DataEntity<MsgConfig> {

    private static final long serialVersionUID = 8534361685323303784L;
    private String type;
    private String sequNumber;
    private String tdName;
    private String ismgIp;
    private int ismgPort;
    private int connectCount;
    private String spId;
    private String secret;
    private String spCode;
    private String serviceId;
    private int connectNumber;
    private String protocolVersion;
    private String protocolType;
    private int sendSpeed;

    public MsgConfig(String ismgIp, int ismgPort, int connectCount, String spId, String secret, String spCode, String serviceId, String protocolType, String protocolVersion, int sendSpeed,String type,String sequNumber) {
        this.ismgIp = ismgIp;
        this.ismgPort = ismgPort;
        this.connectNumber = connectNumber;
        this.connectCount = connectCount;
        this.spId = spId;
        this.secret = secret;
        this.spCode = spCode;
        this.serviceId = serviceId;
        this.protocolType = protocolType;
        this.protocolVersion = protocolVersion;
        this.sendSpeed = sendSpeed;
        this.type = type;
        this.sequNumber = sequNumber;
    }

    public String getIsmgIp() {
        return ismgIp;
    }

    public void setIsmgIp(String ismgIp) {
        this.ismgIp = ismgIp;
    }

    public int getIsmgPort() {
        return ismgPort;
    }

    public void setIsmgPort(int ismgPort) {
        this.ismgPort = ismgPort;
    }

    public int getConnectCount() {
        return connectCount;
    }

    public void setConnectCount(int connectCount) {
        this.connectCount = connectCount;
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSpCode() {
        return spCode;
    }

    public void setSpCode(String spCode) {
        this.spCode = spCode;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getConnectNumber() {
        return connectNumber;
    }

    public void setConnectNumber(int connectNumber) {
        this.connectNumber = connectNumber;
    }

    public String getTdName() {
        return tdName;
    }

    public void setTdName(String tdName) {
        this.tdName = tdName;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public int getSendSpeed() {
        return sendSpeed;
    }

    public void setSendSpeed(int sendSpeed) {
        this.sendSpeed = sendSpeed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSequNumber() {
        return sequNumber;
    }

    public void setSequNumber(String sequNumber) {
        this.sequNumber = sequNumber;
    }
}
