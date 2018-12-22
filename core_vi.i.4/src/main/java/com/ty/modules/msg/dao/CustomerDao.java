package com.ty.modules.msg.dao;

import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;
import com.ty.modules.msg.entity.Customer;

import java.util.List;

/**
 * Created by Ysw on 2016/6/28.
 */
@MyBatisDao
public interface CustomerDao extends CrudDao<Customer> {
    void rechargeSmsCount(Customer customer);

    Customer getById(Customer customer);

    List<Customer> findHaveSetRestRemind();

    List<Customer> findSendCountByMinute();

    Customer useingList(Customer customer);

    List<String> findIdList();

    List<Customer> getRestCount();
}
