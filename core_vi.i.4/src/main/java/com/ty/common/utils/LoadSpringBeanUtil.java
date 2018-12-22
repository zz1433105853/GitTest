package com.ty.common.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LoadSpringBeanUtil {
	
		public static Object getBean(String beanId) {
			String conf = "spring-context.xml";
			ApplicationContext ac = new ClassPathXmlApplicationContext(conf);
			return ac.getBean(beanId); 
		}
}
