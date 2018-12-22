package com.ty.modules.msg.service;

import com.ty.common.service.CrudService;
import com.ty.modules.msg.dao.KeywordsDao;
import com.ty.modules.msg.entity.Keywords;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by tykfkf02 on 2016/6/14.
 */
@Service
@Transactional(readOnly = true)
public class KeywordsService extends CrudService<KeywordsDao,Keywords> {
}
