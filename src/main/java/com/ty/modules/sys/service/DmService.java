package com.ty.modules.sys.service;

import com.ty.common.service.CrudService;
import com.ty.modules.sys.dao.DmDao;
import com.ty.modules.sys.entity.Dm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by tykfkf02 on 2016/7/1.
 */
@Service
@Transactional(readOnly = true)
public class DmService extends CrudService<DmDao,Dm> {
}
