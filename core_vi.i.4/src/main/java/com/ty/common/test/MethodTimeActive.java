package com.ty.common.test;

import com.google.common.collect.Maps;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.time.StopWatch;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Ysw on 2017/3/13.
 */
public class MethodTimeActive implements MethodInterceptor {

    /**
     * 自定义map集合，key：方法名，value：[0：运行次数，1：总时间]
     */
    public static Map<String,Long[]> methodTest = Maps.newHashMap();


    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        // 创建一个计时器
        StopWatch watch = new StopWatch();
        // 计时器开始
        watch.start();
        // 执行方法
        Object object = methodInvocation.proceed();
        // 计时器停止
        watch.stop();
        // 方法名称
        String methodName = methodInvocation.getMethod().getName();
        // 获取计时器计时时间
        Long time = watch.getTime();
        if(methodTest.containsKey(methodName)) {
            Long[] x = methodTest.get(methodName);
            x[0]++;
            x[1] += time;
        }else{
            methodTest.put(methodName, new Long[] {1L,time});
        }
        return object;
    }

}
