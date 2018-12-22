package com.ty.modules.tunnel.send.container.entity.cmpp;

/**
 * 
* <p>Title: </p>
* <p>Description:CMPP协议常量类</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午02:07:44
 */
public  class MsgCommand {
	public static final  int CMPP_CONNECT=0x00000001;			//请求连接
	public static final  int CMPP_CONNECT_RESP=0x80000001;		//请求连接应答
	public static final  int CMPP_TERMINATE=0x00000002;			//终止连接
	public static final  int CMPP_TERMINATE_RESP=0x80000002;	//终止连接应答
	public static final  int CMPP_SUBMIT=0x00000004;			//提交短信
	public static final  int CMPP_SUBMIT_RESP=0x80000004;		//提交短信应答
	public static final  int CMPP_DELIVER=0x00000005;			//短信下发
	public static final  int CMPP_DELIVER_RESP=0x80000005;		//下发短信应答
	public static final  int CMPP_QUERY=0x00000006;				//发送短信状态查询
	public static final  int CMPP_QUERY_RESP=0x80000006;		//发送短信状态查询应答
	public static final  int CMPP_CANCEL=0x00000007;			//删除短信
	public static final  int CMPP_CANCEL_RESP=0x80000007;		//删除短信应答
	public static final  int CMPP_ACTIVE_TEST=0x00000008;		//激活测试
	public static final  int CMPP_ACTIVE_TEST_RESP=0x80000008;	//激活测试应答
}
