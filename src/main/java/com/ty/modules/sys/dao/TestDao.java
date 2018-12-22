package com.ty.modules.sys.dao;

import com.ty.common.persistence.annotation.MyBatisDao;
import com.ty.modules.sys.entity.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by Ysw on 2016/5/23.
 */
@MyBatisDao
public interface TestDao {

    List<Map<String, Object>> findList();

    int save(Test test);


}
