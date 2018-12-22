package com.ty.modules.sys.dao;


import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;
import com.ty.modules.sys.entity.NP;

import java.util.List;
import java.util.Map;

/**
 * Created by tykfkf02 on 2016/7/1.
 */
@MyBatisDao
public interface NPDao extends CrudDao<NP> {

    /**
     * 加载所有携号转网的数据,用逗号分隔手机号和运营商类型
     * @return
     */
    String loadAllNP();

    List<Map<String, String>> loadAllNpMap();

}
