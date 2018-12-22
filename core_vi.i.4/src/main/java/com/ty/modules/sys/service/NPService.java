package com.ty.modules.sys.service;

import com.ty.common.service.CrudService;
import com.ty.modules.sys.dao.NPDao;
import com.ty.modules.sys.entity.NP;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by tykfkf02 on 2016/7/1.
 */
@Service
@Transactional(readOnly = true)
public class NPService extends CrudService<NPDao,NP> {

    /**
     * 加载所有携号转网的数据,用逗号分隔手机号和运营商类型
     * @return
     */
    public String loadAllNP() {
        return this.dao.loadAllNP();
    }

    public List<Map<String, String>> loadAllNpMap() {
        return this.dao.loadAllNpMap();
    }

}
