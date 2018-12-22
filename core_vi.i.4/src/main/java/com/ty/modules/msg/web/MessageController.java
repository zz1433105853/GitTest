package com.ty.modules.msg.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ty.common.config.Global;
import com.ty.common.mapper.JaxbMapper;
import com.ty.common.mapper.JsonMapper;
import com.ty.common.security.Digests;
import com.ty.common.utils.*;
import com.ty.common.web.BaseController;
import com.ty.modules.core.common.disruptor.message.MessageCore;
import com.ty.modules.core.common.disruptor.message.MessageEventProducer;
import com.ty.modules.core.common.disruptor.message.ProductCode;
import com.ty.modules.core.common.disruptor.premessaging.ClientPremessagingEventProducer;
import com.ty.modules.msg.entity.*;
import com.ty.modules.msg.service.*;
import com.ty.modules.msg.util.TemplateUtil;
import com.ty.modules.sys.entity.Dict;
import com.ty.modules.sys.service.DictService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Ysw on 2016/6/28.
 */
@Controller
@RequestMapping("msg")
public class MessageController extends BaseController {
    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;

    @Autowired
    private MessageEventProducer messageEventProducer;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private MessageSubmitLogService messageSubmitLogService;
    @Autowired
    private MessageCxSubmitLogService messageCxSubmitLogService;
    @Autowired
    private SmsSendLogService smsSendLogService;
    @Autowired
    private KeywordsService keywordsService;
    @Autowired
    private DictService dictService;
    @Autowired
    private BlackWhiteListService blackWhiteListService;
    @Autowired
    private ClientPremessagingEventProducer clientPremessagingEventProducer;

    /**
     * sdk客户发送短信
     * @param userSubmit
     * @param request
     * @return
     */
    @RequestMapping(value="sendMsg")
    @ResponseBody
    public Map<String,Object> sendMsg(UserSubmit userSubmit, HttpServletRequest request){
        logger.info("客户提交数据"+ JsonMapper.toJsonString(userSubmit));
        Map<String,Object> result = Maps.newHashMap();
        try{
            if (userSubmit == null
                    || StringUtils.isBlank(userSubmit.getSn())
                    || StringUtils.isBlank(userSubmit.getPassword())
                    || StringUtils.isBlank(userSubmit.getMobile())
                    || StringUtils.isBlank(userSubmit.getContent())){
                result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
            }else{
                Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());
               // logger.info("wsy_客户信息"+customer);
                int toFeeCount = getPayCount(userSubmit.getMobile(),userSubmit.getContent());//扣费条数
                userSubmit.setToFeeCount(toFeeCount);
                if (customer == null){
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
                }else if(!"3".equals(customer.getStatus())) {
                    result.put("status", Status.buildStatus(ResponseCode.STATUS_ERROR, "账号禁用，请联系平台客服"));
                }else if(!validatePassword(userSubmit.getPassword(), customer.getPassword(),userSubmit.getIsCmpp())) {
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "密码错误"));
                }else if(!"sdk".equals(customer.getAccessType())){
                    result.put("status", Status.buildStatus(ResponseCode.NOT_SDK_TYPE, "用户接入类型错误，非SDK用户"));
                } else if(!"1".equals(customer.getIsSupportAfterPay()) && customer.getRestCount() < toFeeCount){
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_BALANCE_LACK, "账户余额不足"));
                }else if(!"1".equals(userSubmit.getIsCmpp()) && validIp(customer,request)){
                    result.put("status", Status.buildStatus(ResponseCode.IP_AUTH_ERROR, "非法IP"));
                }else{
                    String submitType = request.getMethod();
                    int mobileLength = userSubmit.getMobile().split(",").length;
                    if("GET".equals(submitType) && mobileLength > Global.GETCOUNT){
                        result.put("status", Status.buildStatus(ResponseCode.SUBMIT_MOBILE_TOO_MUCH, "一次性提交手机号码太多"));
                    }else{//提交成功
                        MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
                        if("1".equals(userSubmit.getIsCmpp())){
                            messageSubmitLog.preInsertDate();
                            messageSubmitLog.setId(userSubmit.getMsgId());
                        }else{
                            messageSubmitLog.preInsert();
                        }
                        messageSubmitLog.setIp(this.getIpAddr(request));
                        MessageCore messageCore = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,messageSubmitLog);
                        messageEventProducer.onData(messageCore);
                        result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "提交成功"));
                        result.put("taskid", messageSubmitLog.getId());
                    }
                }
            }
        }catch(Exception e){
            result.put("status", Status.buildStatus(ResponseCode.SYSTEM_ERROR, "系统异常，请联系客服"));
        }
        return result;
    }


    @RequestMapping("toTest")
    public String toTest() {
        return "modules/core/toTest";
    }

    /**
     * test
     * @param userSubmit
     * @param request
     * @return
     */
    @RequestMapping(value="test")
    @ResponseBody
    public Map<String,Object> test(UserSubmit userSubmit, HttpServletRequest request){
        userSubmit.setSn("SDK-AAA-0001");
        userSubmit.setPassword("123654");
        userSubmit.setMobile("13485355633");
        userSubmit.setContent("【晋商贷】金算盘XYB20170622003\n" +
                "]没有完成满标审核通过，归还冻结金额FreeMarker template error (DEBUG mode; use RETHROW in production!): \n" +
                "The following has evaluated to null or missing: \n" +
                "==> parseDouble [\n" +
                "in nameless template at line 1,\n" +
                "column 120\n" +
                "]\n" +
                "\n" +
                "----\n" +
                "Tip: If the failing expression is known to be legally refer to something that's sometimes null or missing,\n" +
                "either specify a default value like myOptionalVar!myDefault,\n" +
                "or use <#if myOptionalVar??>when-present<#else>when-missing</#if>. (These only cover the last step of the expression; to cover the whole expression,\n" +
                "use parenthesis: (myOptionalVar.foo)!myDefault,\n" +
                "(myOptionalVar.foo)??\n" +
                "----\n" +
                "\n" +
                "----\n" +
                "FTL stack trace (\"~\" means nesting-related): \n" +
                "- Failed at: ${\n" +
                "parseDouble(tender.account)?string(... [\n" +
                "in nameless template at line 1,\n" +
                "column 118\n" +
                "]\n" +
                "----\n" +
                "\n" +
                "Java stack trace (for programmers): \n" +
                "----\n" +
                "freemarker.core.InvalidReferenceException: [\n" +
                "... Exception message was already printed; see it above ...\n" +
                "]\n" +
                "at freemarker.core.InvalidReferenceException.getInstance(InvalidReferenceException.java: 134)\n" +
                "at freemarker.core.UnexpectedTypeException.newDesciptionBuilder(UnexpectedTypeException.java: 80)\n" +
                "at freemarker.core.UnexpectedTypeException.<init>(UnexpectedTypeException.java: 43)\n" +
                "at freemarker.core.NonMethodException.<init>(NonMethodException.java: 49)\n" +
                "at freemarker.core.MethodCall._eval(MethodCall.java: 85)\n" +
                "at freemarker.core.Expression.eval(Expression.java: 81)\n" +
                "at freemarker.core.BuiltInsForMultipleTypes$stringBI._eval(BuiltInsForMultipleTypes.java: 692)\n" +
                "at freemarker.core.Expression.eval(Expression.java: 81)\n" +
                "at freemarker.core.MethodCall._eval(MethodCall.java: 58)\n" +
                "at freemarker.core.Expression.eval(Expression.java: 81)\n" +
                "at freemarker.core.DollarVariable.calculateInterpolatedStringOrMarkup(DollarVariable.java: 96)\n" +
                "at freemarker.core.DollarVariable.accept(DollarVariable.java: 59)\n" +
                "at freemarker.core.Environment.visit(Environment.java: 327)\n" +
                "at freemarker.core.Environment.visit(Environment.java: 333)\n" +
                "at freemarker.core.Environment.process(Environment.java: 306)\n" +
                "at freemarker.template.Template.process(Template.java: 386)\n" +
                "at com.ddtkj.p2p.common.util.FreemarkerUtil.renderTemplate(FreemarkerUtil.java: 25)\n" +
                "at com.ddtkj.p2p.common.util.StringUtil.fillTemplet(StringUtil.java: 333)\n" +
                "at com.ddtkj.p2p.core.notice.service.impl.NoticeServiceImpl.sendNotice(NoticeServiceImpl.java: 223)\n" +
                "at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java: 57)\n" +
                "at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java: 43)\n" +
                "at java.lang.reflect.Method.invoke(Method.java: 606)\n" +
                "at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java: 302)\n" +
                "at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java: 190)\n" +
                "at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java: 157)\n" +
                "at org.springframework.transaction.interceptor.TransactionInterceptor$1.proceedWithInvocation(TransactionInterceptor.java: 99)\n" +
                "at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java: 281)\n" +
                "at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java: 96)\n" +
                "at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java: 179)\n" +
                "at org.springframework.aop.aspectj.AspectJAfterThrowingAdvice.invoke(AspectJAfterThrowingAdvice.java: 58)\n" +
                "at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java: 168)\n" +
                "at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java: 92)\n" +
                "at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java: 179)\n" +
                "at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java: 207)\n" +
                "at com.sun.proxy.$Proxy118.sendNotice(Unknown Source)\n" +
                "at com.ddtkj.p2p.core.notice.send.BaseMsg.sendMsg(BaseMsg.java: 85)\n" +
                "at com.ddtkj.p2p.core.notice.send.BaseMsg.doEvent(BaseMsg.java: 35)\n" +
                "at com.ddtkj.p2p.trade.core.borrow.executer.CancelTenderUnFeezeExcuter.handleNotice(CancelTenderUnFeezeExcuter.java: 131)\n" +
                "at com.ddtkj.p2p.core.executer.AbstractExecuter.execute(AbstractExecuter.java: 76)\n" +
                "at com.ddtkj.p2p.core.executer.AbstractExecuter.execute(AbstractExecuter.java: 133)\n" +
                "at com.ddtkj.p2p.trade.core.borrow.service.impl.AutoBorrowServiceImpl.autoCancel(AutoBorrowServiceImpl.java: 567)\n" +
                "at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java: 57)\n" +
                "at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java: 43)\n" +
                "at java.lang.reflect.Method.invoke(Method.java: 606)\n" +
                "at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java: 302)\n" +
                "at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java: 190)\n" +
                "at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java: 157)\n" +
                "at org.springframework.transaction.interceptor.TransactionInterceptor$1.proceedWithInvocation(TransactionInterceptor.java: 99)\n" +
                "at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java: 281)\n" +
                "at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java: 96)\n" +
                "at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java: 179)\n" +
                "at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java: 92)\n" +
                "at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java: 179)\n" +
                "at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java: 207)\n" +
                "at com.sun.proxy.$Proxy82.autoCancel(Unknown Source)\n" +
                "at com.ddtkj.p2p.trade.core.controller.borrow.TradeCancelBorrowController.cancelBorrow(TradeCancelBorrowController.java: 57)\n" +
                "at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java: 57)\n" +
                "at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java: 43)\n" +
                "at java.lang.reflect.Method.invoke(Method.java: 606)\n" +
                "at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java: 221)\n" +
                "at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java: 137)\n" +
                "at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java: 110)\n" +
                "at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java: 806)\n" +
                "at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java: 729)\n" +
                "at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java: 85)\n" +
                "at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java: 959)\n" +
                "at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java: 893)\n" +
                "at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java: 970)\n" +
                "at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java: 861)\n" +
                "at javax.servlet.http.HttpServlet.service(HttpServlet.java: 624)\n" +
                "at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java: 846)\n" +
                "at javax.servlet.http.HttpServlet.service(HttpServlet.java: 731)\n" +
                "at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java: 303)\n" +
                "at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java: 208)\n" +
                "at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java: 52)\n" +
                "at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java: 241)\n" +
                "at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java: 208)\n" +
                "at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java: 85)\n" +
                "at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java: 107)\n" +
                "at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java: 241)\n" +
                "at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java: 208)\n" +
                "at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java: 220)\n" +
                "at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java: 122)\n" +
                "at de.javakaffee.web.msm.RequestTrackingContextValve.invoke(RequestTrackingContextValve.java: 99)\n" +
                "at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java: 505)\n" +
                "at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java: 170)\n" +
                "at de.javakaffee.web.msm.RequestTrackingHostValve.invoke(RequestTrackingHostValve.java: 156)\n" +
                "at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java: 103)\n" +
                "at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java: 116)\n" +
                "at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java: 423)\n" +
                "at org.apache.coyote.http11.AbstractHttp11Processor.process(AbstractHttp11Processor.java: 1079)\n" +
                "at org.apache.coyote.AbstractProtocol$AbstractConnectionHandler.process(AbstractProtocol.java: 620)\n" +
                "at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java: 1753)\n" +
                "at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.run(NioEndpoint.java: 1712)\n" +
                "at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java: 1145)\n" +
                "at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java: 615)\n" +
                "at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java: 61)\n" +
                "at java.lang.Thread.run(Thread.java: 745)pt\n");
        logger.info("客户提交数据"+ JsonMapper.toJsonString(userSubmit));
        Map<String,Object> result = Maps.newHashMap();
        try{
            if (userSubmit == null
                    || StringUtils.isBlank(userSubmit.getSn())
                    || StringUtils.isBlank(userSubmit.getPassword())
                    || StringUtils.isBlank(userSubmit.getMobile())
                    || StringUtils.isBlank(userSubmit.getContent())){
                result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
            }else{
                Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());
                int toFeeCount = getPayCount(userSubmit.getMobile(),userSubmit.getContent());//扣费条数
                userSubmit.setToFeeCount(toFeeCount);
                if (customer == null){
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
                }else if(!"3".equals(customer.getStatus())) {
                    result.put("status", Status.buildStatus(ResponseCode.STATUS_ERROR, "账号禁用，请联系平台客服"));
                }else if(!validatePassword(userSubmit.getPassword(), customer.getPassword())) {
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "密码错误"));
                }else if(!"sdk".equals(customer.getAccessType())){
                    result.put("status", Status.buildStatus(ResponseCode.NOT_SDK_TYPE, "用户接入类型错误，非SDK用户"));
                } else if(!"1".equals(customer.getIsSupportAfterPay()) && customer.getRestCount() < toFeeCount){
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_BALANCE_LACK, "账户余额不足"));
                }else if(validIp(customer,request)){
                    result.put("status", Status.buildStatus(ResponseCode.IP_AUTH_ERROR, "非法IP"));
                }else{
                    String submitType = request.getMethod();
                    int mobileLength = userSubmit.getMobile().split(",").length;
                    if("GET".equals(submitType) && mobileLength > Global.GETCOUNT){
                        result.put("status", Status.buildStatus(ResponseCode.SUBMIT_MOBILE_TOO_MUCH, "一次性提交手机号码太多"));
                    }else{//提交成功
                        MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
                        messageSubmitLog.preInsert();
                        messageSubmitLog.setIp(this.getIpAddr(request));
                        MessageCore messageCore = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,messageSubmitLog);
                        messageEventProducer.onData(messageCore);
                        result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "提交成功"));
                        result.put("taskid", messageSubmitLog.getId());
                    }
                }
            }
        }catch(Exception e){
            result.put("status", Status.buildStatus(ResponseCode.SYSTEM_ERROR, "系统异常，请联系客服"));
        }
        return result;
    }

    @RequestMapping(value="whileTest")
    @ResponseBody
    public Map<String,Object> whileTest(UserSubmit userSubmit, HttpServletRequest request){
        userSubmit.setSn("SDK-AAA-0001");
        userSubmit.setPassword("123654");
        userSubmit.setMobile("13485355633");
        userSubmit.setContent("吃饭了吗【天元科技】");
        logger.info("客户提交数据"+ JsonMapper.toJsonString(userSubmit));
        Map<String,Object> result = Maps.newHashMap();
        try{
            if (userSubmit == null
                    || StringUtils.isBlank(userSubmit.getSn())
                    || StringUtils.isBlank(userSubmit.getPassword())
                    || StringUtils.isBlank(userSubmit.getMobile())
                    || StringUtils.isBlank(userSubmit.getContent())){
                result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
            }else{
                Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());
                int toFeeCount = getPayCount(userSubmit.getMobile(),userSubmit.getContent());//扣费条数
                userSubmit.setToFeeCount(toFeeCount);
                if (customer == null){
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
                }else if(!validatePassword(userSubmit.getPassword(), customer.getPassword())) {
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "密码错误"));
                }else if(!"sdk".equals(customer.getAccessType())){
                    result.put("status", Status.buildStatus(ResponseCode.NOT_SDK_TYPE, "用户接入类型错误，非SDK用户"));
                } else if(!"1".equals(customer.getIsSupportAfterPay()) && customer.getRestCount() < toFeeCount){
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_BALANCE_LACK, "账户余额不足"));
                }else if(validIp(customer,request)){
                    result.put("status", Status.buildStatus(ResponseCode.IP_AUTH_ERROR, "非法IP"));
                }else{
                    String submitType = request.getMethod();
                    int mobileLength = userSubmit.getMobile().split(",").length;
                    if("GET".equals(submitType) && mobileLength > Global.GETCOUNT){
                        result.put("status", Status.buildStatus(ResponseCode.SUBMIT_MOBILE_TOO_MUCH, "一次性提交手机号码太多"));
                    }else{//提交成功
                        String ip = this.getIpAddr(request);
                        while(true){
                            Thread.sleep(1);
                            MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
                            messageSubmitLog.preInsert();
                            messageSubmitLog.setIp(ip);
                            MessageCore messageCore = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,messageSubmitLog);
                            messageEventProducer.onData(messageCore);

                            MessageSubmitLog l1 = new MessageSubmitLog();
                            l1.preInsert();
                            l1.setIp(ip);
                            MessageCore m2 = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,l1);
                            messageEventProducer.onData(m2);

                            MessageSubmitLog l2 = new MessageSubmitLog();
                            l2.preInsert();
                            l2.setIp(ip);
                            MessageCore m3 = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,l2);
                            messageEventProducer.onData(m3);

                            MessageSubmitLog l3 = new MessageSubmitLog();
                            l3.preInsert();
                            l3.setIp(ip);
                            MessageCore m4 = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,l3);
                            messageEventProducer.onData(m4);
                        }
                    }
                }
            }
        }catch(Exception e){
            result.put("status", Status.buildStatus(ResponseCode.SYSTEM_ERROR, "系统异常，请联系客服"));
        }
        return result;
    }


    /**
     * 客户端发送短信
     * @param userSubmit
     * @param request
     * @return
     */
    @RequestMapping(value="clientSendMsg")
    @ResponseBody
    public Map<String,Object> clientSendMsg(UserSubmit userSubmit, HttpServletRequest request){
        Map<String,Object> result = Maps.newHashMap();
        if (userSubmit == null
                || StringUtils.isBlank(userSubmit.getSn())
                || StringUtils.isBlank(userSubmit.getMobile())
                || StringUtils.isBlank(userSubmit.getContent())){
            result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
        }else{
            Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());
            int toFeeCount = getPayCount(userSubmit.getMobile(),userSubmit.getContent());//扣费条数
            userSubmit.setToFeeCount(toFeeCount);
            if (customer == null){
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "请等待审核通过"));
            }else if(!"3".equals(customer.getStatus())) {
                result.put("status", Status.buildStatus(ResponseCode.STATUS_ERROR, "账号禁用，请联系平台客服"));
            }else if("sdk".equals(customer.getAccessType())){
                result.put("status", Status.buildStatus(ResponseCode.NOT_SDK_TYPE, "用户接入类型错误，非客户端用户"));
            } else if(!"1".equals(customer.getIsSupportAfterPay()) && customer.getRestCount() < toFeeCount){
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_BALANCE_LACK, "账户余额不足"));
            }else{
                String submitType = request.getMethod();
                int mobileLength = userSubmit.getMobile().split(",").length;
                if("GET".equals(submitType) && mobileLength > Global.GETCOUNT){
                    result.put("status", Status.buildStatus(ResponseCode.SUBMIT_MOBILE_TOO_MUCH, "一次性提交手机号码太多"));
                }else{//提交成功
                    MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
                    messageSubmitLog.preInsert();
                    messageSubmitLog.setIp(this.getIpAddr(request));
                    userSubmit.setExt(messageSubmitLog.getExtCode());
                    MessageSend messageSend = new MessageSend();
                    messageSend.setCustomer(customer);
                    messageSend.setUserSubmit(userSubmit);//目前发送短信只需要userSbumit中的扩展码
                    messageSend.setMessageSubmitLog(messageSubmitLog);//发送记录表需要submit_id
                    clientPremessagingEventProducer.onData(new Message(messageSend));
                    result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "提交成功"));
                    result.put("taskid", messageSubmitLog.getId());
                }
            }
        }
        return result;
    }

    /**
     * 客户端发送彩信
     * @param userSubmit
     * @param request
     * @return
     */
    @RequestMapping(value="clientSendCx")
    @ResponseBody
    public Map<String,Object> clientSendCx(UserSubmit userSubmit, HttpServletRequest request){
        Map<String,Object> result = Maps.newHashMap();
        if (userSubmit == null
                || StringUtils.isBlank(userSubmit.getSn())
                || StringUtils.isBlank(userSubmit.getMobile())
                || StringUtils.isBlank(userSubmit.getTitle())
                || StringUtils.isBlank(userSubmit.getContent())){
            result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
        }else{
            Customer c = new Customer();
            c.setSerialNumber(userSubmit.getSn());
            Customer customer = customerService.get(c);
            int toFeeCount =userSubmit.getToFeeCount();
            if (customer == null){
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "请等待审核通过"));
            }else if(!"3".equals(customer.getStatus())) {
                result.put("status", Status.buildStatus(ResponseCode.STATUS_ERROR, "账号禁用，请联系平台客服"));
            }else if("sdk".equals(customer.getAccessType())){
                result.put("status", Status.buildStatus(ResponseCode.NOT_SDK_TYPE, "用户接入类型错误，非客户端用户"));
            } else if(!"1".equals(customer.getIsSupportAfterPay()) && customer.getRestCount() < toFeeCount){
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_BALANCE_LACK, "账户余额不足"));
            }else{
                String submitType = request.getMethod();
                int mobileLength = userSubmit.getMobile().split(",").length;
                if("GET".equals(submitType) && mobileLength > Global.GETCOUNT){
                    result.put("status", Status.buildStatus(ResponseCode.SUBMIT_MOBILE_TOO_MUCH, "一次性提交手机号码太多"));
                }else{//提交成功
                    MessageCxSubmitLog messageCxSubmitLog = new MessageCxSubmitLog();
                    messageCxSubmitLog.preInsert();
                    messageCxSubmitLog.setIp(this.getIpAddr(request));
                    userSubmit.setExt(messageCxSubmitLog.getExtCode());
                    MessageSend messageSend = new MessageSend();
                    messageSend.setCustomer(customer);
                    messageSend.setUserSubmit(userSubmit);//目前发送短信只需要userSbumit中的扩展码
                    messageSend.setMessageCxSubmitLog(messageCxSubmitLog);//发送记录表需要submit_id
                    clientPremessagingEventProducer.onData(new Message(messageSend));
                    result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "提交成功"));
                    result.put("taskid", messageCxSubmitLog.getId());
                }
            }
        }
        return result;
    }

    @RequestMapping(value="clientSendSpecialMsg")
    @ResponseBody
    public Map<String,Object> clientSendSpecialMsg(UserSubmit userSubmit, HttpServletRequest request){
        Map<String,Object> result = Maps.newHashMap();
        if (userSubmit == null
                || StringUtils.isBlank(userSubmit.getSn())
                || StringUtils.isBlank(userSubmit.getMobile())
                || StringUtils.isBlank(userSubmit.getContentSpecial())
                || StringUtils.isBlank(userSubmit.getContent())){
            result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
        }else{
            Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());

            String contentSpecial = userSubmit.getContentSpecial();
            contentSpecial = contentSpecial.replace("\n","\\n").replace("\r","\\r")
                    .replace("\r\n","\\n");
            userSubmit.setContentSpecial(contentSpecial);
            List<SpecialMsgVo> data = JsonMapper.getInstance().fromJson(contentSpecial, JsonMapper.getInstance().createCollectionType(ArrayList.class, SpecialMsgVo.class));

            int toFeeCount = 0;
            for(SpecialMsgVo singleData : data) {
                toFeeCount += getPayCount(singleData.getMobile(), singleData.getContent());
            }
            userSubmit.setToFeeCount(toFeeCount);
            if (customer == null){
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
            }else if(!"3".equals(customer.getStatus())) {
                result.put("status", Status.buildStatus(ResponseCode.STATUS_ERROR, "账号禁用，请联系平台客服"));
            }else if("sdk".equals(customer.getAccessType())){
                result.put("status", Status.buildStatus(ResponseCode.NOT_SDK_TYPE, "用户接入类型错误，非客户端用户"));
            } else if(!"1".equals(customer.getIsSupportAfterPay()) && customer.getRestCount() < toFeeCount){
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_BALANCE_LACK, "账户余额不足"));
            }else{
                String submitType = request.getMethod();
                int mobileLength = userSubmit.getMobile().split(",").length;
                if("GET".equals(submitType) && mobileLength > Global.GETCOUNT){
                    result.put("status", Status.buildStatus(ResponseCode.SUBMIT_MOBILE_TOO_MUCH, "一次性提交手机号码太多"));
                }else{//提交成功
                    MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
                    messageSubmitLog.preInsert();
                    messageSubmitLog.setIp(this.getIpAddr(request));
                    MessageCore messageCore = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,messageSubmitLog);
                    messageEventProducer.onData(messageCore);
                    result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "提交成功"));
                    result.put("taskid", messageSubmitLog.getId());
                }
            }
        }
        return result;
    }

    /*------------------------------------------------测试发送短信部分----------------------------------------------------------------------*/
    @RequestMapping(value="sendTestMsg")
    @ResponseBody
    public Map<String,Object> sendTestMsg(UserSubmit userSubmit, HttpServletRequest request){
        Map<String,Object> result = Maps.newHashMap();
        if (userSubmit == null  || StringUtils.isBlank(userSubmit.getMobile())) {
            result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
        }else{
            String sn = "SDK-AAA-0001";
            Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,sn);
            userSubmit.setContent("发送时间是：" + DateUtils.getDate("yyyy-MM-dd HH:mm:ss") + "。" + "发送内容：你好，"+userSubmit.getMobile()+"【天元科技】");
            MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
            messageSubmitLog.preInsert();
            MessageCore messageCore = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,messageSubmitLog);
            messageEventProducer.onData(messageCore);
            result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "提交成功"));
            result.put("taskid", messageSubmitLog.getId());
        }
        return result;
    }

    /**
     * 发送客户短信：客户端登录获取验证码调用
     * @param userSubmit
     * @param request
     * @return
     */
    @RequestMapping(value="sendMsgToLogin")
    @ResponseBody
    public Map<String,Object> sendMsgToLogin(UserSubmit userSubmit, HttpServletRequest request){
        Map<String,Object> result = Maps.newHashMap();
        if (userSubmit == null
                || StringUtils.isBlank(userSubmit.getSn())
                || StringUtils.isBlank(userSubmit.getMobile())
                || StringUtils.isBlank(userSubmit.getContent())){
            result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
        }else{
            Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());
            int toFeeCount = getPayCount(userSubmit.getMobile(),userSubmit.getContent());//扣费条数
            userSubmit.setToFeeCount(toFeeCount);
            if (customer == null){
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
            } else if(!"1".equals(customer.getIsSupportAfterPay()) && customer.getRestCount() < toFeeCount){
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_BALANCE_LACK, "账户余额不足"));
            }else{
                String submitType = request.getMethod();
                int mobileLength = userSubmit.getMobile().split(",").length;
                if("GET".equals(submitType) && mobileLength > Global.GETCOUNT){
                    result.put("status", Status.buildStatus(ResponseCode.SUBMIT_MOBILE_TOO_MUCH, "一次性提交手机号码太多"));
                }else{//提交成功
                    MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
                    messageSubmitLog.preInsert();
                    messageSubmitLog.setIp(this.getIpAddr(request));
                    MessageCore messageCore = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,messageSubmitLog);
                    messageEventProducer.onData(messageCore);
                    result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "提交成功"));
                    result.put("taskid", messageSubmitLog.getId());
                }
            }
        }
        return result;
    }

    /**
     * 客户端发送找回密码验证码
     * @param userSubmit
     * @param request
     * @return
     */
    @RequestMapping(value="sendValidateCode")
    @ResponseBody
    public String sendValidateCode(UserSubmit userSubmit, HttpServletRequest request){
        String result = "false";
        if (userSubmit == null  || StringUtils.isBlank(userSubmit.getMobile()) || StringUtils.isBlank(userSubmit.getContent())) {
            result = "false";
        }else{
            String sn = "SDK-AAA-0001";
            Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,sn);
            if(customer==null) {
                result = "false";
            }else {
                MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
                messageSubmitLog.preInsert();
                MessageCore messageCore = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,messageSubmitLog);
                messageEventProducer.onData(messageCore);
                result = messageSubmitLog.getId();
            }
        }
        return result;
    }


    /**
     * 客户查询短信余额接口
     * @param userSubmit
     * @param request
     * @return
     */
    @RequestMapping(value="balance")
    @ResponseBody
    public Map<String,Object> balance(UserSubmit userSubmit, HttpServletRequest request){
        Map<String,Object> result = Maps.newHashMap();
        if (userSubmit == null
                || StringUtils.isBlank(userSubmit.getSn())
                || StringUtils.isBlank(userSubmit.getPassword())
                ){
            result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
        }else{
            Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());
            //userSubmit.setContent(Encodes.urlDecode(userSubmit.getContent()));
            if (customer == null){
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
            }else if(!"3".equals(customer.getStatus())) {
                result.put("status", Status.buildStatus(ResponseCode.STATUS_ERROR, "账号禁用，请联系平台客服"));
            }else if(!validatePassword(userSubmit.getPassword(), customer.getPassword())) {
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "密码错误"));
            }else if(!"sdk".equals(customer.getAccessType())){
                result.put("status", Status.buildStatus(ResponseCode.NOT_SDK_TYPE, "用户接入类型错误，非SDK用户"));
            } else if(validIp(customer,request)){
                result.put("status", Status.buildStatus(ResponseCode.IP_AUTH_ERROR, "非法IP"));
            }else{
                result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "查询余额成功"));
                result.put("data", customer.getRestCount());
            }
        }
        return result;
    }

    /**
     * 后台审核短信重发接口
     * @param msgId
     * @param isp1Id
     * @param isp2Id
     * @param isp3Id
     * @return
     */
    @RequestMapping("resendMsg")
    @ResponseBody
    public String resendMsg(String msgId,
                            String serialNumber,
                            String companyName,
                            String mobile,
                            String sendStatus,
                            String arrivedStatus,
                            String sequenceNumber,
                            String sendTime,
                            String isp1Id,
                            String isp2Id,
                            String isp3Id,
                            String isResend,
                            String resendId) {
        //根据submit批次ID查询所有sendlog
        if(StringUtils.isNotBlank(isp1Id) && StringUtils.isNotBlank(isp2Id) && StringUtils.isNotBlank(isp3Id)
                && StringUtils.isNotBlank(sendTime)) {
            MsgRecord msgRecord = new MsgRecord();
            msgRecord.setMessageSubmitLog(new MessageSubmitLog(msgId));
            Customer customer = new Customer();
            customer.setSerialNumber(serialNumber);
            customer.setCompanyName(companyName);
            msgRecord.setCustomer(customer);
            msgRecord.setMobile(mobile);
            msgRecord.setIsResend(isResend);
            msgRecord.setResendId(resendId);

            MsgResponse msgResponse = new MsgResponse();
            msgResponse.setSendStatus(sendStatus);
            Tunnel tunnel = new Tunnel();
            tunnel.setSequenceNumber(sequenceNumber);
            msgResponse.setTunnel(tunnel);
            msgRecord.setMsgResponse(msgResponse);

            MsgReport msgReport = new MsgReport();
            msgReport.setArrivedStatus(arrivedStatus);
            msgRecord.setMsgReport(msgReport);
            msgRecord.setCreateDate(DateUtils.parseDate(sendTime));
            String  sendLogWaitStr = null;
            try{
                sendLogWaitStr = smsSendLogService.loadSmsSendLog(msgRecord);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(StringUtils.isBlank(sendLogWaitStr)){
                return "false";
            }else{
                List<MsgRecord> sendLogWait = Lists.newArrayList();
                for(String row:sendLogWaitStr.split("#row#")){
                    MsgRecord msgR = new MsgRecord();
                    String[] column = row.split("#column#");
                    if(StringUtils.isNotBlank(column[0]) && !"空".equals(column[0])){
                        msgR.setId(column[0]);
                    }
                    Customer customer1 = new Customer();
                    if(StringUtils.isNotBlank(column[1]) && !"空".equals(column[1])){
                       customer1.setId(column[1]);
                    }
                    if(StringUtils.isNotBlank(column[2]) && !"空".equals(column[2])){
                       customer1.setSpecialServiceNumber(column[2]);
                    }
                    if(StringUtils.isNotBlank(column[3]) && !"空".equals(column[3])){
                        customer1.setSerialNumber(column[3]);
                    }
                    if(StringUtils.isNotBlank(column[4]) && !"空".equals(column[4])){
                       customer1.setCompanyName(column[4]);
                    }
                    msgR.setCustomer(customer1);
                    if(StringUtils.isNotBlank(column[5]) && !"空".equals(column[5])){
                        msgR.setMobile(column[5]);
                    }
                    if(StringUtils.isNotBlank(column[6]) && !"空".equals(column[6])){
                        msgR.setContent(column[6]);
                    }
                    if(StringUtils.isNotBlank(column[7]) && !"空".equals(column[7])){
                        msgR.setContentPayCount(Integer.parseInt(column[7]));
                    }
                    if(StringUtils.isNotBlank(column[8]) && !"空".equals(column[8])){
                        msgR.setExt(column[8]);
                    }
                    if(StringUtils.isNotBlank(column[9]) && !"空".equals(column[9])){
                        msgR.setcSrcId(column[9]);
                    }
                    if(StringUtils.isNotBlank(column[10]) && !"空".equals(column[10])){
                        MessageSubmitLog submitLog = new MessageSubmitLog(column[10]);
                        msgR.setMessageSubmitLog(submitLog);
                    }
                    sendLogWait.add(msgR);
                }
                Map<String, List<MsgRecord>> msgGroup = Maps.newHashMap();
                //短信根据内容分组
                String reId = RandomStringUtils.randomNumeric(3);
                for(MsgRecord ssl : sendLogWait) {
                    ssl.setResendId(reId);
                    String content = ssl.getContent();
                    List<MsgRecord> tmpList = msgGroup.get(content);
                    if(tmpList==null)tmpList = Lists.newArrayList();
                    tmpList.add(ssl);
                    msgGroup.put(content, tmpList);
                }

                for(List<MsgRecord> reSendList : msgGroup.values()) {
                    if(reSendList.isEmpty()) continue;
                    MessageSend messageSend = new MessageSend();
                    messageSend.setReSendLogList(reSendList);
                    messageSend.setContainerIsp1Id(isp1Id);
                    messageSend.setContainerIsp2Id(isp2Id);
                    messageSend.setContainerIsp3Id(isp3Id);
                    clientPremessagingEventProducer.onData(new Message(messageSend));
                }
            }
        }else{
            return "false";
        }
        return "true";
    }


    /**
     * 验证SDK客户发送短信IP鉴权
     * @param customer
     * @param request
     * @return
     */
    private boolean validIp(Customer customer,HttpServletRequest request){
        String ip = this.getIpAddr(request);
        if(StringUtils.isBlank(ip) || (StringUtils.isNotBlank(customer.getIp()) && !StringUtils.inString(ip,customer.getIp().split(",")))){
            return true;
        }
        return false;
    }
    /**
     * 获取扣费条数
     * @param mobile：手机号，多个按逗号隔开
     * @param content：短信内容
     * @return
     */
    private int getPayCount(String mobile, String content){
        int count=0;
        int mobileCount = 0;
        int contentCount = 0;
        String[] s = StringUtils.split(mobile,",");
        mobileCount = s.length;
        if(content.length() <= 70){
            contentCount = 1;
        }else{
            contentCount = content.length()/67;
            if(content.length()%67 != 0){
                contentCount += 1;
            }
        }
        count = mobileCount*contentCount;
        return count;
    }

    /**
     * 后台审核发送短信
     * @param id
     * @return
     */
    @RequestMapping(value="auditSendMessage")
    @ResponseBody
    public boolean auditSendMessage(String id){
        if (StringUtils.isBlank(id)){
            return false;
        }
        logger.info("审核提交信息id:"+id);
        MessageSubmitLog messageSubmitLog = new MessageSubmitLog(id);
        messageSubmitLog = messageSubmitLogService.get(messageSubmitLog);
        if(StringUtils.isNotBlank(messageSubmitLog.getContentSpecial())){
            Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,messageSubmitLog.getCustomer().getSerialNumber());
            messageSubmitLog.setCustomer(customer);
            messageEventProducer.onData(new MessageCore(ProductCode.SEND_SPECIAL_MESSAGE,messageSubmitLog));
        }else{
            Customer qCustomer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,messageSubmitLog.getCustomer().getSerialNumber());
            String content = messageSubmitLog.getContent();
            UserSubmit userSubmit = new UserSubmit();
            userSubmit.setExt(messageSubmitLog.getExtCode());
            userSubmit.setSrcId(messageSubmitLog.getcSrcId());
            MessageAuditSend messageAuditSend = new MessageAuditSend();
            messageAuditSend.setCustomer(qCustomer);
            messageAuditSend.setUserSubmit(userSubmit);//目前发送短信只需要userSbumit中的扩展码
            messageAuditSend.setContent(content);
            messageAuditSend.setMessageSubmitLog(messageSubmitLog);//发送记录表需要submit_id
            messageAuditSend.setMobile(messageSubmitLog.getMobile());
            clientPremessagingEventProducer.onData(new Message(messageAuditSend));
        }
        return true;

    }

    /**
     * 后台审核发送彩信
     * @param id
     * @return
     */
    @RequestMapping(value="auditSendCx")
    @ResponseBody
    public boolean auditSendCx(String id){
        if (StringUtils.isBlank(id)){
            return false;
        }
        logger.info("审核提交信息id:"+id);
        MessageCxSubmitLog messageSubmitLog = new MessageCxSubmitLog(id);
        messageSubmitLog = messageCxSubmitLogService.get(messageSubmitLog);
        Customer qCustomer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,messageSubmitLog.getCustomer().getSerialNumber());
        String content = messageSubmitLog.getContent();
        UserSubmit userSubmit = new UserSubmit();
        userSubmit.setExt(messageSubmitLog.getExtCode());
        userSubmit.setTitle(messageSubmitLog.getTitle());
        CxAuditSend auditSend = new CxAuditSend();
        auditSend.setCustomer(qCustomer);
        auditSend.setUserSubmit(userSubmit);//目前发送短信只需要userSbumit中的扩展码
        auditSend.setContent(content);
        auditSend.setMessageCxSubmitLog(messageSubmitLog);//发送记录表需要submit_id
        auditSend.setMobile(messageSubmitLog.getMobile());
        clientPremessagingEventProducer.onData(new Message(auditSend));
        return true;

    }

    /**
     * 验证密码
     * @param plainPassword 明文密码
     * @param password 密文密码
     * @return 验证成功返回true
     */
    public static boolean validatePassword(String plainPassword, String password) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Encodes.decodeHex(password.substring(0,16));
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
    }

    /**
     * 验证密码
     * @param plainPassword 明文密码
     * @param password 密文密码
     * @return 验证成功返回true
     */
    public static boolean validatePassword(String plainPassword, String password,String isCmpp) {
        if("1".equals(isCmpp)){
            return password.equals(plainPassword);
        }else{
            String plain = Encodes.unescapeHtml(plainPassword);
            byte[] salt = Encodes.decodeHex(password.substring(0,16));
            byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
            return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
        }
    }


    /**
     * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
     */
    public static String entryptPassword(String plainPassword) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
    }

    /**
     * 获取状态报告
     * @param userSubmit
     * @param request
     * @return
     */
    @RequestMapping(value="getArrivedStatus")
    @ResponseBody
    public Map<String,Object> getArrivedStatus(UserSubmit userSubmit, HttpServletRequest request){
        Map<String,Object> result = Maps.newHashMap();
        if (userSubmit == null
                || StringUtils.isBlank(userSubmit.getSn())
                || StringUtils.isBlank(userSubmit.getPassword())){
            result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
        }else{
            Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());
            if (customer == null){
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
            }else if(!"3".equals(customer.getStatus())) {
                result.put("status", Status.buildStatus(ResponseCode.STATUS_ERROR, "账号禁用，请联系平台客服"));
            }else if(!validatePassword(userSubmit.getPassword(), customer.getPassword())) {
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "密码错误"));
            }else{
                MsgReport msgReport = new MsgReport();
                Customer arrivedCustomer = new Customer(customer.getId());
                arrivedCustomer.setReplyWay("2");
                arrivedCustomer.setSdkType("0");
                msgReport.setCustomer(arrivedCustomer);
                msgReport.setArrivedSendStatus("0");
                List<MsgRecord> smsSendLogList = smsSendLogService.findArrivedSendList(msgReport);
                List<ArrivedStatusReport> arrivedStatusReports = generateStatusReport(smsSendLogList);
                result.put("data",arrivedStatusReports);
                result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "获取状态报告成功"));
                if(!smsSendLogList.isEmpty()){
                    smsSendLogService.batchUpdateArrivedSendStatus(smsSendLogList);
                }
            }
        }
        return result;
    }



    /**
     * 获取上行报告
     * @param userSubmit
     * @param request
     * @return
     */
    @RequestMapping(value="getReplyLog")
    @ResponseBody
    public Map<String,Object> getReplyLog(UserSubmit userSubmit, HttpServletRequest request){
        Map<String,Object> result = Maps.newHashMap();
        if (userSubmit == null
                || StringUtils.isBlank(userSubmit.getSn())
                || StringUtils.isBlank(userSubmit.getPassword())){
            result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
        }else{
            Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());
            if (customer == null){
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
            }else if(!"3".equals(customer.getStatus())) {
                result.put("status", Status.buildStatus(ResponseCode.STATUS_ERROR, "账号禁用，请联系平台客服"));
            }else if(!validatePassword(userSubmit.getPassword(), customer.getPassword())) {
                result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "密码错误"));
            }else{
                ReplyLog replyLog = new ReplyLog();
                replyLog.setCustomer(customer);
                replyLog.setGetStatus("0");
                List<ReplyLog> replyLogs = smsSendLogService.findReplyLogList(replyLog);
                List<ReplyReport> replyReports = generateReplyReport(replyLogs);
                result.put("data",replyReports);
                result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "获取上行报告成功"));
                if(replyLogs.size() != 0){
                    smsSendLogService.batchUpdateGetStatus(replyLogs);
                }
            }
        }
        return result;
    }

    private List<ArrivedStatusReport> generateStatusReport( List<MsgRecord> smsSendLogs){
        List<ArrivedStatusReport> arrivedStatusReports = Lists.newArrayList();
        for(MsgRecord sendLog:smsSendLogs){
            ArrivedStatusReport arrivedStatusReport = new ArrivedStatusReport();
            arrivedStatusReport.setTaskid(sendLog.getMessageSubmitLog().getId());
            arrivedStatusReport.setMobile(sendLog.getMobile());
            arrivedStatusReport.setExt(sendLog.getExt());
            String arrivedStatus = "";
            String arrivedTime = "";
            if(sendLog.getMsgReport() == null){
                arrivedStatus = "1";
            }else{
                if("1".equals(sendLog.getMsgReport().getArrivedStatus())){
                    arrivedStatus = "0";
                    if(sendLog.getMsgReport().getArrivedTime() != null){
                        arrivedTime = DateUtils.formatDateTime(sendLog.getMsgReport().getArrivedTime());
                    }
                }else{
                    arrivedStatus = "1";
                }
            }
            arrivedStatusReport.setArrivedStatus(arrivedStatus);
            arrivedStatusReport.setArrivedTime(arrivedTime);
            arrivedStatusReports.add(arrivedStatusReport);
        }
        return arrivedStatusReports;
    }

    private List<ReplyReport> generateReplyReport(List<ReplyLog> replyLogs){
        List<ReplyReport> replyReport = Lists.newArrayList();
        for(ReplyLog replyLog:replyLogs){
            ReplyReport report = new ReplyReport(replyLog.getExt(),replyLog.getMobile(),replyLog.getContent(),DateUtils.formatDateTime(replyLog.getCreateDate()));
            replyReport.add(report);
        }
        return replyReport;
    }

    /**
     * 清除ehcache缓存
     * @param type
     * @param key
     * @return
     */
    @RequestMapping("clearCache")
    @ResponseBody
    public String clearCache(String type,String key){
        try{
            if(StringUtils.isBlank(type)){
                return "false";
            }
            if(Global.CLEAR_CACHE_CUSTOMER.equals(type)){
                Customer newCustomer = new Customer();
                newCustomer.setSerialNumber(key);
                newCustomer = customerService.get(newCustomer);
                CacheUtils.put(Global.CACHE_CUSTOMER_INFO,key,newCustomer);
            }else if(Global.CLEAR_CACHE_KEYWORDS.equals(type)){
                Dict dict = new Dict();
                dict.setType("keywords_type");
                List<Dict> keyWordDicts = dictService.findList(dict);
                for(Dict k:keyWordDicts){
                    Keywords keywords = new Keywords();
                    keywords.setType(k.getValue());
                    keywords = keywordsService.get(keywords);
                    if(keywords != null){
                        CacheUtils.put(Global.CACHE_KEYWORDS_INFO,k.getValue(),keywords);
                    }
                }
            }else if(Global.CLEAR_CACHE_PLATEFORMBLACK.equals(type)){
                //平台黑白名单
                List<BlackWhiteList> blackWhiteLists = blackWhiteListService.findPlatformList();
                if(!blackWhiteLists.isEmpty()){
                    CacheUtils.put(Global.CACHE_BLACK_INFO,"platform",blackWhiteLists);
                }
            }else if(Global.CLEAR_CACHE_CUSTOMERBLACK.equals(type)){
                //客户黑白名单
                BlackWhiteList blackWhiteList = new BlackWhiteList();
                blackWhiteList.setType("black");
                List<Customer> customerList = blackWhiteListService.findAllCustomerList(blackWhiteList);
                if(!customerList.isEmpty()){
                    for(Customer customer:customerList){
                        if(!customer.getBlackWhiteListList().isEmpty()){
                            CacheUtils.put(Global.CACHE_CUSTOMER_BLACK_INFO,customer.getId(),customer.getBlackWhiteListList());
                        }
                    }
                }
            }else if(Global.CLEAR_CACHE_TEMPLATE.equals(type)) {
                TemplateUtil.clearCache(key);
            }
            return "true";
        }catch (Exception e){
            logger.info("刷新客户缓存失败"+e.getMessage());
            return "false";
        }
    }

    /**
     * sdk客户发送短信(Url GBK编码)
     * @param userSubmit
     * @param request
     * @return
     */
    @RequestMapping(value="sendMsgUrlGBK")
    @ResponseBody
    public Map<String,Object> sendMsgUrlGBK(UserSubmit userSubmit, HttpServletRequest request){
        logger.info("URLGBK客户提交数据"+JsonMapper.toJsonString(userSubmit));
        Map<String,Object> result = Maps.newHashMap();
        try{
            if (userSubmit == null
                    || StringUtils.isBlank(userSubmit.getSn())
                    || StringUtils.isBlank(userSubmit.getPassword())
                    || StringUtils.isBlank(userSubmit.getMobile())
                    || StringUtils.isBlank(userSubmit.getContent())){
                result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
            }else{
                Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());
                userSubmit.setContent(Encodes.urlDecode(userSubmit.getContent(),"GBK"));
                logger.info("URLGBK客户短信内容"+userSubmit.getContent());
                int toFeeCount = getPayCount(userSubmit.getMobile(),userSubmit.getContent());//扣费条数
                userSubmit.setToFeeCount(toFeeCount);
                if (customer == null){
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
                }else if(!"3".equals(customer.getStatus())) {
                    result.put("status", Status.buildStatus(ResponseCode.STATUS_ERROR, "账号禁用，请联系平台客服"));
                }else if(!validatePassword(userSubmit.getPassword(), customer.getPassword())) {
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
                }else if(!"sdk".equals(customer.getAccessType())){
                    result.put("status", Status.buildStatus(ResponseCode.NOT_SDK_TYPE, "用户接入类型错误，非SDK用户"));
                } else if(!"1".equals(customer.getIsSupportAfterPay()) && customer.getRestCount() < toFeeCount){
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_BALANCE_LACK, "账户余额不足"));
                }else if(validIp(customer,request)){
                    result.put("status", Status.buildStatus(ResponseCode.IP_AUTH_ERROR, "非法IP"));
                }else{
                    String submitType = request.getMethod();
                    int mobileLength = userSubmit.getMobile().split(",").length;
                    if("GET".equals(submitType) && mobileLength > Global.GETCOUNT){
                        result.put("status", Status.buildStatus(ResponseCode.SUBMIT_MOBILE_TOO_MUCH, "一次性提交手机号码太多"));
                    }else{//提交成功
                        MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
                        messageSubmitLog.preInsert();
                        messageSubmitLog.setIp(this.getIpAddr(request));
                        MessageCore messageCore = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,messageSubmitLog);
                        messageEventProducer.onData(messageCore);
                        result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "提交成功"));
                        result.put("taskid", messageSubmitLog.getId());
                    }
                }
            }
        }catch(Exception e){
            result.put("status", Status.buildStatus(ResponseCode.SYSTEM_ERROR, "系统异常，请联系客服"));
        }
        return result;
    }

    /**
     * sdk客户发送短信(Url GBK编码)
     * @param userSubmit
     * @param request
     * @return
     */
    @RequestMapping(value="sendMsgGBK")
    @ResponseBody
    public String sendMsgGBK(UserSubmit userSubmit, HttpServletRequest request){
        HttpParamUtility http = new HttpParamUtility(request);
        userSubmit.setContent(http.getParameter("content"));
        String result = "";
        SubmitResult s = new SubmitResult();
        Status status = null;
        try{
            if (userSubmit == null
                    || StringUtils.isBlank(userSubmit.getSn())
                    || StringUtils.isBlank(userSubmit.getPassword())
                    || StringUtils.isBlank(userSubmit.getMobile())
                    || StringUtils.isBlank(userSubmit.getContent())){
               status = Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数");
            }else{
                Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());
                logger.info("GBK客户短信内容"+userSubmit.getContent());
                int toFeeCount = getPayCount(userSubmit.getMobile(),userSubmit.getContent());//扣费条数
                userSubmit.setToFeeCount(toFeeCount);
                if (customer == null){
                    status = Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "error");
                }else if(!"3".equals(customer.getStatus())) {
                   status = Status.buildStatus(ResponseCode.STATUS_ERROR, "账号禁用，请联系平台客服");
                }else if(!validatePassword(userSubmit.getPassword(), customer.getPassword())) {
                    status = Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "error");
                }else if(!"sdk".equals(customer.getAccessType())){
                    status = Status.buildStatus(ResponseCode.NOT_SDK_TYPE, "error");
                } else if(customer.getRestCount() < toFeeCount){
                    status = Status.buildStatus(ResponseCode.ACCOUNT_BALANCE_LACK, "error");
                }else if(validIp(customer,request)){
                   status = Status.buildStatus(ResponseCode.IP_AUTH_ERROR, "error");
                }else{
                    String submitType = request.getMethod();
                    int mobileLength = userSubmit.getMobile().split(",").length;
                    if("GET".equals(submitType) && mobileLength > Global.GETCOUNT){
                        status = Status.buildStatus(ResponseCode.SUBMIT_MOBILE_TOO_MUCH, "error");
                    }else{//提交成功
                        MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
                        messageSubmitLog.preInsert();
                        messageSubmitLog.setIp(this.getIpAddr(request));
                        MessageCore messageCore = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,messageSubmitLog);
                        messageEventProducer.onData(messageCore);
                        status = Status.buildStatus(ResponseCode.SUCCESS, "success");
                        s.setTaskid(messageSubmitLog.getId());
                    }
                }
            }
        }catch(Exception e){
           status = Status.buildStatus(ResponseCode.SYSTEM_ERROR, "error");
        }
        s.setStatus(status);
        result = JaxbMapper.toXml(s,SubmitResult.class,"GBK");
        return result;
    }


    /**
     *  发送模板短信
     * @param userSubmit : tplId
     * @param request
     * @return
     */
    @RequestMapping("sendTplMsg")
    @ResponseBody
    public Map<String, Object> sendTplMsg(UserSubmit userSubmit, HttpServletRequest request) {

        logger.info("客户提交模板数据"+JsonMapper.toJsonString(userSubmit));
        Map<String,Object> result = Maps.newHashMap();
        try{
            if (userSubmit == null
                    || StringUtils.isBlank(userSubmit.getSn())
                    || StringUtils.isBlank(userSubmit.getPassword())
                    || StringUtils.isBlank(userSubmit.getMobile())
                    || StringUtils.isBlank(userSubmit.getTplId())
                    ){
                result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
                return result;
            }else{
                Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());
                int toFeeCount = getPayCount(userSubmit.getMobile(),userSubmit.getContent());//扣费条数
                userSubmit.setToFeeCount(toFeeCount);
                if (customer == null){
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
                    return result;
                }else if(!"3".equals(customer.getStatus())) {
                    result.put("status", Status.buildStatus(ResponseCode.STATUS_ERROR, "账号禁用，请联系平台客服"));
                    return result;
                }else if(!validatePassword(userSubmit.getPassword(), customer.getPassword())) {
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
                    return result;
                }else if(!"sdk".equals(customer.getAccessType())){
                    result.put("status", Status.buildStatus(ResponseCode.NOT_SDK_TYPE, "用户接入类型错误，非SDK用户"));
                    return result;
                } else if(!"1".equals(customer.getIsSupportAfterPay()) && customer.getRestCount() < toFeeCount){
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_BALANCE_LACK, "账户余额不足"));
                    return result;
                }else if(validIp(customer,request)){
                    result.put("status", Status.buildStatus(ResponseCode.IP_AUTH_ERROR, "非法IP"));
                    return result;
                }else{

                    //验证模板
                    Template template = TemplateUtil.getTpl(customer.getId(), userSubmit.getTplId());
                    if(template==null) {
                        //检测是否存在模板
                        result.put("status", Status.buildStatus(ResponseCode.TPL_NOT_EXIST, "不存在此模板，或未审核通过"));
                        return result;
                    }else if(template!=null) {
                        //检测是否已经传了必须的参数
                        List<String> params = template.getParams();
                        for(String p : params) {
                            if (request.getParameter(p)==null) {
                                result.put("status", Status.buildStatus(ResponseCode.TPL_LACK_PARAM, "提交的模板信息缺少参数"));
                                break;
                            }
                        }
                        return result;
                    }
                    String submitType = request.getMethod();
                    int mobileLength = userSubmit.getMobile().split(",").length;
                    if("GET".equals(submitType) && mobileLength > Global.GETCOUNT){
                        result.put("status", Status.buildStatus(ResponseCode.SUBMIT_MOBILE_TOO_MUCH, "一次性提交手机号码太多"));
                        return result;
                    }else{//提交成功
                        String content = template.convertToFinalMsg(request);
                        userSubmit.setContent(content);
                        MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
                        messageSubmitLog.preInsert();
                        messageSubmitLog.setIp(this.getIpAddr(request));
                        MessageCore messageCore = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,messageSubmitLog);
                        messageEventProducer.onData(messageCore);
                        result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "提交成功"));
                        result.put("taskid", messageSubmitLog.getId());
                        return result;
                    }
                }
            }
        }catch(Exception e){
            result.put("status", Status.buildStatus(ResponseCode.SYSTEM_ERROR, "系统异常，请联系客服"));
            return result;
        }
    }

    /**
     *  发送模板短信
     * @param userSubmit : tplId
     * @param request
     * @return
     */
    @RequestMapping("sendTplMsgX")
    @ResponseBody
    public Map<String, Object> sendTplMsgX(UserSubmit userSubmit, HttpServletRequest request) {

        logger.info("客户提交模板数据"+JsonMapper.toJsonString(userSubmit));
        Map<String,Object> result = Maps.newHashMap();
        try{
            if (userSubmit == null
                    || StringUtils.isBlank(userSubmit.getSn())
                    || StringUtils.isBlank(userSubmit.getPassword())
                    || StringUtils.isBlank(userSubmit.getMobile())
                    || StringUtils.isBlank(userSubmit.getContent())
                    ){
                result.put("status", Status.buildStatus(ResponseCode.LACK_OF_PARAM, "确认是否遗漏必须传入的参数"));
                return result;
            }else{
                Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,userSubmit.getSn());
                int toFeeCount = getPayCount(userSubmit.getMobile(),userSubmit.getContent());//扣费条数
                userSubmit.setToFeeCount(toFeeCount);
                if (customer == null){
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
                    return result;
                }else if(!"3".equals(customer.getStatus())) {
                    result.put("status", Status.buildStatus(ResponseCode.STATUS_ERROR, "账号禁用，请联系平台客服"));
                    return result;
                }else if(!validatePassword(userSubmit.getPassword(), customer.getPassword())) {
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_PASSWORD_ERROR, "账户验证失败"));
                    return result;
                }else if(!"sdk".equals(customer.getAccessType())){
                    result.put("status", Status.buildStatus(ResponseCode.NOT_SDK_TYPE, "用户接入类型错误，非SDK用户"));
                    return result;
                } else if(!"1".equals(customer.getIsSupportAfterPay()) && customer.getRestCount() < toFeeCount){
                    result.put("status", Status.buildStatus(ResponseCode.ACCOUNT_BALANCE_LACK, "账户余额不足"));
                    return result;
                }else if(validIp(customer,request)){
                    result.put("status", Status.buildStatus(ResponseCode.IP_AUTH_ERROR, "非法IP"));
                    return result;
                }else{

                    //验证模板
                    List<Template> tList = Lists.newArrayList();
                    boolean isMatch = false;
                    for(Template t : tList) {
                        if(t!=null && t.isMatch(userSubmit.getContent())) {
                            isMatch = true;
                            break;
                        }
                    }
                    if(!isMatch) {
                        result.put("status", Status.buildStatus(ResponseCode.TPL_CONTENT_NOT_MATCH, "提交内容不匹配相应模板"));
                        return result;
                    }


                    String submitType = request.getMethod();
                    int mobileLength = userSubmit.getMobile().split(",").length;
                    if("GET".equals(submitType) && mobileLength > Global.GETCOUNT){
                        result.put("status", Status.buildStatus(ResponseCode.SUBMIT_MOBILE_TOO_MUCH, "一次性提交手机号码太多"));
                        return result;
                    }else{//提交成功
                        MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
                        messageSubmitLog.preInsert();
                        messageSubmitLog.setIp(this.getIpAddr(request));
                        MessageCore messageCore = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,messageSubmitLog);
                        messageEventProducer.onData(messageCore);
                        result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "提交成功"));
                        result.put("taskid", messageSubmitLog.getId());
                        return result;
                    }
                }
            }
        }catch(Exception e){
            result.put("status", Status.buildStatus(ResponseCode.SYSTEM_ERROR, "系统异常，请联系客服"));
            return result;
        }
    }


}
