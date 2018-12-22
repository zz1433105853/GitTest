package com.ty.modules.msg.dao;

import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;
import com.ty.modules.msg.entity.AssignedCxTunnel;
import com.ty.modules.msg.entity.Customer;

import java.util.List;

/**
 * Created by tykfkf02 on 2016/7/29.
 */
@MyBatisDao
public interface AssignedCxTunnelDao extends CrudDao<AssignedCxTunnel> {

    /**
     * 批量更新客户通道为指定通道
     * @param list
     * @return
     */
    int updateNewTunnel(List<AssignedCxTunnel> list);

    void insertCustomerCxTunnel(Customer customer);

    void deleteByCustomer(Customer customer);
}
