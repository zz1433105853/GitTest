package com.ty.modules.msg.entity;

import com.google.common.collect.Lists;
import com.ty.common.mapper.JsonMapper;
import com.ty.common.persistence.DataEntity;
import com.ty.common.utils.StringUtils;

import java.util.List;

/**
 * Created by Ysw on 2016/6/15.
 */
public class Customer extends DataEntity<Customer> {

    private static final long serialVersionUID = -5772961209160030573L;
    private List<UserExtCode> userExtCodeList = Lists.newArrayList();
    private List<BlackWhiteList> blackWhiteListList = Lists.newArrayList();
    public static  final String CACHE_CUSTOMER_TUNNEL  = "CUSTOMER_TUNNEL:";
    private Agent agent;
    private String serialNumber;
    private String regPassword;
    private String accessType;
    private String specialServiceNumber;
    private String name;
    private String password;
    private int restCount;
    private String companyName;
    private String companyAddr;
    private String contactPhone;
    private String contactMobile;
    private String contactName;
    private String contactQq;
    private String contactMail;
    private String contactWx;
    private String sendCount;
    private int minuteSendCountLimit;
    private int hourSendCountLimit;
    private int daySendCountLimit;
    private String signature;
    private String properCard;
    private String infSafeCard;
    private int normalSendSpeed;
    private int realSendCount;
    private String contentSample;
    private String costType;
    private String customerType;
    private String restRemindMobiles;
    private int restRemindCount;
    private String keywordType;
    private String dismissKeyword;
    private List<String> keywordTypeList = Lists.newArrayList();
    private String blackWhiteType;
    private String blackListType;
    private String isOpenSubAccount;
    private int subAccountExtensionCount;
    private int subAccountCount;
    private String infoDispenseAudit;
    private int dispenseCount;
    private String ip;
    private String isSupportAfterPay;
    private String isLimitTemp;
    private String isLimitSign;
    private String status;
    private String replyWay;
    private String replyUrl;
    private String moUrl;
    private List<AssignedTunnel> assignedTunnelList = Lists.newArrayList();
    //CMPP INFO
    private String enterCode;
    private String spId;
    private String sharedPassword;
    private int maxConn;
    private String sdkType;//cmpp:1 http:0(default)
    private int sendLimit;


    public Customer() {
    }

    public Customer(String id) {
        super(id);
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getRegPassword() {
        return regPassword;
    }

    public void setRegPassword(String regPassword) {
        this.regPassword = regPassword;
    }

    public String getSpecialServiceNumber() {
        return specialServiceNumber;
    }

    public void setSpecialServiceNumber(String specialServiceNumber) {
        this.specialServiceNumber = specialServiceNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddr() {
        return companyAddr;
    }

    public void setCompanyAddr(String companyAddr) {
        this.companyAddr = companyAddr;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactQq() {
        return contactQq;
    }

    public void setContactQq(String contactQq) {
        this.contactQq = contactQq;
    }

    public String getContactMail() {
        return contactMail;
    }

    public void setContactMail(String contactMail) {
        this.contactMail = contactMail;
    }

    public String getContactWx() {
        return contactWx;
    }

    public void setContactWx(String contactWx) {
        this.contactWx = contactWx;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getProperCard() {
        return properCard;
    }

    public void setProperCard(String properCard) {
        this.properCard = properCard;
    }

    public String getInfSafeCard() {
        return infSafeCard;
    }

    public void setInfSafeCard(String infSafeCard) {
        this.infSafeCard = infSafeCard;
    }

    public String getContentSample() {
        return contentSample;
    }

    public void setContentSample(String contentSample) {
        this.contentSample = contentSample;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {

        this.customerType = customerType;
    }

    public String getKeywordType() {
        return keywordType;
    }

    public void setKeywordType(String keywordType) {
        this.keywordType = keywordType;
        this.keywordTypeList = Lists.newArrayList(this.keywordType.split(","));
    }

    public String getBlackWhiteType() {
        return blackWhiteType;
    }

    public void setBlackWhiteType(String blackWhiteType) {
        this.blackWhiteType = blackWhiteType;
    }

    public String getIsOpenSubAccount() {
        return isOpenSubAccount;
    }

    public void setIsOpenSubAccount(String isOpenSubAccount) {
        this.isOpenSubAccount = isOpenSubAccount;
    }

    public int getSubAccountExtensionCount() {
        return subAccountExtensionCount;
    }

    public void setSubAccountExtensionCount(int subAccountExtensionCount) {
        this.subAccountExtensionCount = subAccountExtensionCount;
    }

    public int getSubAccountCount() {
        return subAccountCount;
    }

    public void setSubAccountCount(int subAccountCount) {
        this.subAccountCount = subAccountCount;
    }

    public String getInfoDispenseAudit() {
        return infoDispenseAudit;
    }

    public void setInfoDispenseAudit(String infoDispenseAudit) {
        this.infoDispenseAudit = infoDispenseAudit;
    }

    public int getDispenseCount() {
        return dispenseCount;
    }

    public void setDispenseCount(int dispenseCount) {
        this.dispenseCount = dispenseCount;
    }

    public String getIsLimitTemp() {
        return isLimitTemp;
    }

    public void setIsLimitTemp(String isLimitTemp) {
        this.isLimitTemp = isLimitTemp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public List<String> getKeywordTypeList() {
        return keywordTypeList;
    }

    public void setKeywordTypeList(List<String> keywordTypeList) {
        this.keywordTypeList = keywordTypeList;
        this.keywordType = StringUtils.join(keywordTypeList, ",");
    }

    public int getRestCount() {
        return restCount;
    }

    public void setRestCount(int restCount) {
        this.restCount = restCount;
    }

    public String getBlackListType() {
        return blackListType;
    }

    public void setBlackListType(String blackListType) {
        this.blackListType = blackListType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIsLimitSign() {
        return isLimitSign;
    }

    public void setIsLimitSign(String isLimitSign) {
        this.isLimitSign = isLimitSign;
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

    public String getSendCount() {
        SendCountLimit scl = new SendCountLimit(this.minuteSendCountLimit, this.hourSendCountLimit, this.daySendCountLimit);
        sendCount = JsonMapper.toJsonString(scl);
        return sendCount;
    }

    public void setSendCount(String sendCount) {
        this.sendCount = sendCount;
        if(StringUtils.isNotBlank(sendCount)) {
            SendCountLimit sendCountLimit = null;
            try{
                sendCountLimit = (SendCountLimit) JsonMapper.fromJsonString(sendCount, SendCountLimit.class);
            }catch(Exception e){
            }
            if(sendCountLimit!=null) {
                this.minuteSendCountLimit = sendCountLimit.getMinuteSendCountLimit();
                this.hourSendCountLimit = sendCountLimit.getHourSendCountLimit();
                this.daySendCountLimit = sendCountLimit.getDaySendCountLimit();
            }
        }
    }

    public int getMinuteSendCountLimit() {
        return minuteSendCountLimit;
    }

    public void setMinuteSendCountLimit(int minuteSendCountLimit) {
        this.minuteSendCountLimit = minuteSendCountLimit;
    }

    public int getHourSendCountLimit() {
        return hourSendCountLimit;
    }

    public void setHourSendCountLimit(int hourSendCountLimit) {
        this.hourSendCountLimit = hourSendCountLimit;
    }

    public int getDaySendCountLimit() {
        return daySendCountLimit;
    }

    public void setDaySendCountLimit(int daySendCountLimit) {
        this.daySendCountLimit = daySendCountLimit;
    }

    public String getReplyWay() {
        return replyWay;
    }

    public void setReplyWay(String replyWay) {
        this.replyWay = replyWay;
    }

    public String getReplyUrl() {
        return replyUrl;
    }

    public void setReplyUrl(String replyUrl) {
        this.replyUrl = replyUrl;
    }

    public String getMoUrl() {
        return moUrl;
    }

    public void setMoUrl(String moUrl) {
        this.moUrl = moUrl;
    }

    public String getDismissKeyword() {
        return dismissKeyword;
    }

    public void setDismissKeyword(String dismissKeyword) {
        this.dismissKeyword = dismissKeyword;
    }

    public int getNormalSendSpeed() {
        return normalSendSpeed;
    }

    public void setNormalSendSpeed(int normalSendSpeed) {
        this.normalSendSpeed = normalSendSpeed;
    }

    public int getRealSendCount() {
        return realSendCount;
    }

    public void setRealSendCount(int realSendCount) {
        this.realSendCount = realSendCount;
    }

    public List<UserExtCode> getUserExtCodeList() {
        return userExtCodeList;
    }

    public void setUserExtCodeList(List<UserExtCode> userExtCodeList) {
        this.userExtCodeList = userExtCodeList;
    }

    public String getIsSupportAfterPay() {
        return isSupportAfterPay;
    }

    public void setIsSupportAfterPay(String isSupportAfterPay) {
        this.isSupportAfterPay = isSupportAfterPay;
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId;
    }

    public String getSharedPassword() {
        return sharedPassword;
    }

    public void setSharedPassword(String sharedPassword) {
        this.sharedPassword = sharedPassword;
    }

    public int getMaxConn() {
        return maxConn;
    }

    public void setMaxConn(int maxConn) {
        this.maxConn = maxConn;
    }

    public String getSdkType() {
        return sdkType;
    }

    public void setSdkType(String sdkType) {
        this.sdkType = sdkType;
    }

    public int getSendLimit() {
        return sendLimit;
    }

    public void setSendLimit(int sendLimit) {
        this.sendLimit = sendLimit;
    }

    public List<BlackWhiteList> getBlackWhiteListList() {
        return blackWhiteListList;
    }

    public void setBlackWhiteListList(List<BlackWhiteList> blackWhiteListList) {
        this.blackWhiteListList = blackWhiteListList;
    }

    public List<AssignedTunnel> getAssignedTunnelList() {
        return assignedTunnelList;
    }

    public void setAssignedTunnelList(List<AssignedTunnel> assignedTunnelList) {
        this.assignedTunnelList = assignedTunnelList;
    }

    public String getEnterCode() {
        return enterCode;
    }

    public void setEnterCode(String enterCode) {
        this.enterCode = enterCode;
    }
}
