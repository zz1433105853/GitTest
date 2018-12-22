package com.ty.modules.tunnel.send.container.entity.container.yx;

import com.google.common.base.Ascii;
import com.google.common.collect.Lists;
import com.ty.common.utils.DateUtils;
import com.ty.common.utils.Encodes;
import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.msg.entity.MsgReport;
import com.ty.modules.tunnel.entity.MsgReply;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.xml.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Ysw on 2016/7/4.
 */
@XmlRootElement(name="data-sm")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdYxSendMoResult {

    @XmlElements(value = {@XmlElement(name = "mo", type = ThirdYxMo.class)})
    private List<ThirdYxMo> mo;

    @XmlElements(value = {@XmlElement(name = "dr", type = ThirdYxDr.class)})
    private List<ThirdYxDr> dr;

    public List<MsgReport> getMsgReportList(){
        List<MsgReport> msgReportList = Lists.newArrayList();
        if(this.dr != null && !this.dr.isEmpty()){
            for(ThirdYxDr dr:this.dr){
                MsgReport mr = new MsgReport();
                mr.setMsgId(dr.getId());
                mr.setMobile(dr.getSa());
                mr.setArrivedStatus(dr.getReceiveStatus());
                mr.setArrivedResultMessage(dr.getSu());
                mr.setArrivedTime(dr.getDd()==null?new Date():DateUtils.parseDate(dr.getDd()));
                msgReportList.add(mr);
            }
        }
        return msgReportList;
    }

    public String unicode2String(String content) {
        StringBuffer result = new StringBuffer("");
        for(int i=1;i<=content.length()/4;i++){
            result.append("\\u").append(content.substring((i-1)*4,i*4));
        }
        return Encodes.unicode2String(result.toString());
    }

    public List<MsgReply> getMsgReplyList( Map<String,MsgRecord> responseMap,String userId) throws DecoderException, UnsupportedEncodingException {
        List<MsgReply> replyLogs = Lists.newArrayList();
        if(this.mo != null && !this.mo.isEmpty()){
            for(ThirdYxMo thirdYxMo:this.mo){
                if(responseMap.get(thirdYxMo.getDa())==null) continue;
                MsgRecord mr = responseMap.get(thirdYxMo.getDa());
                MsgReply msgReply = new MsgReply();
                msgReply.setCustomer(mr.getCustomer());
                msgReply.setMobile(thirdYxMo.getSa());
                String content = "";
                if(thirdYxMo.getDc() == 15){
                    content = new String(Hex.decodeHex(thirdYxMo.getSm().toCharArray()),"gb2312");
                }else if(thirdYxMo.getDc() == 8){
                    content = unicode2String(thirdYxMo.getSm());
                }else if(thirdYxMo.getDc() == 0){
                    content = Encodes.ascii2String(thirdYxMo.getSm());
                }
                msgReply.setContent(content);
                msgReply.setSrcId(thirdYxMo.getDa().replace(userId,""));
                msgReply.setExt(mr.getExt());
                replyLogs.add(msgReply);
            }
        }
        return replyLogs;
    }

    public List<String> getAssocatedCustomerIds() {
        List<String> msgIds = Lists.newArrayList();
        for(ThirdYxMo thirdYxMo:this.mo){
            msgIds.add(thirdYxMo.getDa());
        }
        return msgIds;
    }

    public List<ThirdYxMo> getMo() {
        return mo;
    }

    public void setMo(List<ThirdYxMo> mo) {
        this.mo = mo;
    }

    public List<ThirdYxDr> getDr() {
        return dr;
    }

    public void setDr(List<ThirdYxDr> dr) {
        this.dr = dr;
    }
}
