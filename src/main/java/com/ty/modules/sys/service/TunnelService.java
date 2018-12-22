package com.ty.modules.sys.service;

import com.ty.common.service.CrudService;
import com.ty.modules.sys.dao.TunnelDao;
import com.ty.modules.msg.entity.Tunnel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Ysw on 2016/6/14.
 */
@Service
@Transactional(readOnly =  true)
public class TunnelService extends CrudService<TunnelDao, Tunnel> {



}
