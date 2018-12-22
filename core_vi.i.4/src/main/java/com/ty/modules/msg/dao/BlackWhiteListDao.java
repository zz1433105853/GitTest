package com.ty.modules.msg.dao;


import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;
import com.ty.modules.msg.entity.BlackWhiteList;
import com.ty.modules.msg.entity.Customer;

import java.util.List;

/**
 * Created by tykfkf02 on 2016/6/14.
 */
@MyBatisDao
public interface BlackWhiteListDao extends CrudDao<BlackWhiteList> {
    List<BlackWhiteList> findPlatformList();

    List<Customer> findAllCustomerList(BlackWhiteList blackWhiteList);
}
