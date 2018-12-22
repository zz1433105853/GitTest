package com.ty.modules.msg.service;

import com.ty.common.service.CrudService;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.dao.AgentDao;
import com.ty.modules.msg.entity.Agent;
import com.ty.modules.msg.entity.Customer;
import com.ty.modules.msg.dao.AccountLogDao;
import com.ty.modules.msg.dao.CustomerDao;
import com.ty.modules.msg.entity.AccountLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Ysw on 2016/6/28.
 */
@Service
@Transactional(readOnly = true)
public class CustomerService extends CrudService<CustomerDao, Customer> {
    private static  final String CACHE_CUSTOMER_INFO  = "customerCache";
    @Autowired
    private AgentDao agentDao;
    @Autowired
    private AccountLogDao accountLogDao;
    /**
     * 更新客户余额
     * @param customer
     */
    @Transactional(readOnly = false)
    public void rechargeSmsCount(Customer customer){
      dao.rechargeSmsCount(customer);
    }

    public Customer getById(Customer customer) {
        return dao.getById(customer);
    }
    /**
     * 获取已设置余额提醒的客户列表
     */
    public List<Customer> findHaveSetRestRemind() {
        return dao.findHaveSetRestRemind();
    }

    @Transactional(readOnly = false)
    public void paySmsCount(Customer customer) {
        Agent payAgent = new Agent(customer.getAgent().getId());
        int payCount = StringUtils.split(customer.getRestRemindMobiles(),"\n").length;
        payAgent.setRestCount(-payCount);
        agentDao.rechargeSmsCount(payAgent);
        //资金记录
        AccountLog agentAccountLog = new AccountLog();
        agentAccountLog.preInsert();
        agentAccountLog.setCostType("pay");
        agentAccountLog.setObjectType("agent");
        agentAccountLog.setAgent(payAgent);
        agentAccountLog.setCostCount(payCount);
        agentAccountLog.setRemarks("余额不足提醒！");
        accountLogDao.insert(agentAccountLog);
    }

    @Transactional(readOnly = false)
    public void payCount(Customer customer,String mobiles) {
        Agent payAgent = new Agent(customer.getAgent().getId());
        int payCount = StringUtils.split(mobiles,",").length;
        payAgent.setRestCountType("trade");
        payAgent.setRestCount(-payCount);
        agentDao.rechargeSmsCount(payAgent);
        //资金记录
        AccountLog agentAccountLog = new AccountLog();
        agentAccountLog.preInsert();
        agentAccountLog.setCostType("pay");
        agentAccountLog.setObjectType("agent");
        agentAccountLog.setAgent(payAgent);
        agentAccountLog.setCostCount(payCount);
        agentAccountLog.setRemarks("待审核信息提醒发送短信！");
        accountLogDao.insert(agentAccountLog);
    }

    public List<Customer> findSendCountByMinute() {
        return dao.findSendCountByMinute();
    }

    public Customer useingList(Customer customer) {
        return dao.useingList(customer);
    }

    public List<String> findIdList() {
        return dao.findIdList();
    }

    public List<Customer> getRestCount() {
        return dao.getRestCount();
    }
}
