package com.ty.common.listener;

import com.ty.common.config.Global;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

public class WebContextListener extends org.springframework.web.context.ContextLoaderListener {
	
	@Override
	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n======================================================================\r\n");
		sb.append("\r\n    欢迎使用 "+ Global.getConfig("productName")+"\r\n");
		sb.append("\r\n======================================================================\r\n");
		System.out.println(sb.toString());
		return super.initWebApplicationContext(servletContext);
	}
}
