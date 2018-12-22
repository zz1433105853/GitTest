package com.ty.modules.msg.entity;

public enum ResponseCode {
	
	SUCCESS("0"),//请求成功
	LACK_OF_PARAM("1001"),//缺少参数
	PARAM_SIGN_ERROR("1002"),//签名错误
	ACCOUNT_PASSWORD_ERROR("1003"),//账号密码错误
	IP_AUTH_ERROR("1004"),//ip鉴权失败
	ACCOUNT_BALANCE_LACK("1005"),//用户余额不足
	SYSTEM_ERROR("1006"),//系统异常
	SAME_MOBILE_TOO_MUCH("1007"),//相同手机号一天发送次数太多
	SUBMIT_MOBILE_TOO_MUCH("1008"),//一次提交手机号太多
	NOT_SDK_TYPE("1009"),//非SDK用户
	TPL_NOT_EXIST("1010"),//不存此模板
	TPL_LACK_PARAM("1011"),//模板缺少对应参数
	TPL_CONTENT_NOT_MATCH("1012"),//提交模板信息与模板不匹配
	STATUS_ERROR("1013"),//提交模板信息与模板不匹配
	;
	private String code;
	private ResponseCode(String code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return this.code;
	}
	
}
