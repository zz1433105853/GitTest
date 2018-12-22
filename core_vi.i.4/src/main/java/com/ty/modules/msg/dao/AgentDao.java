package com.ty.modules.msg.dao;

import com.ty.modules.msg.entity.Agent;
import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;

/**
 * Created by Ysw on 2016/6/13.
 */
@MyBatisDao
public interface AgentDao extends CrudDao<Agent> {
    void assignSpecialServiceNum(Agent agent);

    /**
     *  检查是否已经存在给定代理号的代理商
     * @param code
     * @return
     */
    int checkExistCode(String code);

    /**
     * 更新代理商管理员ID
     * @param agent
     * @return
     */
    int updateAdmin(Agent agent);

    /**
     * 给代理商放款
     * @param agent
     */
    void rechargeSmsCount(Agent agent);
}
