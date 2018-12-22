package com.ty.modules.tunnel.send.container.entity.cmpp;

/**
 * Created by ljb on 2017/4/13 10:42.
 */
public interface MsgDeliverInterface {

     long getMsg_Id();

     void setMsg_Id(long msg_Id);

     String getDest_Id();

     void setDest_Id(String dest_Id);

     String getService_Id();

     void setService_Id(String service_Id);

     byte getTP_pid();

     void setTP_pid(byte tp_pid);

     byte getTP_udhi();

     void setTP_udhi(byte tp_udhi);

     byte getMsg_Fmt();

     void setMsg_Fmt(byte msg_Fmt);

     String getSrc_terminal_Id();

     void setSrc_terminal_Id(String src_terminal_Id);

     byte getSrc_terminal_type();

     void setSrc_terminal_type(byte src_terminal_type);

     byte getRegistered_Delivery();

     void setRegistered_Delivery(byte registered_Delivery) ;

     int getMsg_Length() ;

     void setMsg_Length(int msg_Length);

     String getMsg_Content();

     void setMsg_Content(String msg_Content);

     String getLinkID();

     void setLinkID(String linkID);

     long getMsg_Id_report();

     void setMsg_Id_report(long msgIdReport);

     String getStat();

     void setStat(String stat);

     String getSubmit_time();

     void setSubmit_time(String submit_time);

     String getDone_time();

     void setDone_time(String done_time);

     String getDest_terminal_Id();

     void setDest_terminal_Id(String dest_terminal_Id);

     long getSMSC_sequence();

     void setSMSC_sequence(long smsc_sequence);

     int getResult();

     void setResult(int result);

     String getReserved();

     void setReserved(String reserved);
    
}
