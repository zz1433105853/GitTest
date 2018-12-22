package com.ty.modules.tunnel.send.container.entity.container.md;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="string", namespace="http://tempuri.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdMdBalance {

	@XmlValue
	private String balance;

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}
	
	
}
