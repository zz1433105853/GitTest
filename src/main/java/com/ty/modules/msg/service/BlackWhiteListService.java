package com.ty.modules.msg.service;

import com.ty.common.service.CrudService;
import com.ty.modules.msg.entity.BlackWhiteList;
import com.ty.modules.msg.entity.Customer;
import com.ty.modules.msg.dao.BlackWhiteListDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by tykfkf02 on 2016/6/14.
 */
@Service
@Transactional(readOnly = true)
public class BlackWhiteListService extends CrudService<BlackWhiteListDao,BlackWhiteList> {
    public List<BlackWhiteList> findPlatformList() {
       return dao.findPlatformList();
    }

    public List<Customer> findAllCustomerList(BlackWhiteList blackWhiteList) {
        return dao.findAllCustomerList(blackWhiteList);
    }
}
