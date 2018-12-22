package com.ty.modules.msg.service;

import com.ty.common.service.CrudService;
import com.ty.modules.msg.dao.ConfigDao;
import com.ty.modules.msg.entity.Config;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Ysw on 2016/6/14.
 */
@Service
@Transactional(readOnly = true)
public class ConfigService extends CrudService<ConfigDao, Config> {
}
