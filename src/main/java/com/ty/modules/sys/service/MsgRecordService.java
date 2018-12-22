package com.ty.modules.sys.service;

import com.ty.common.service.CrudService;
import com.ty.modules.msg.dao.SmsSendLogDao;
import com.ty.modules.msg.entity.MsgRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ljb on 2017/4/12 10:04.
 */
@Service
@Transactional(readOnly = true)
public class MsgRecordService extends CrudService<SmsSendLogDao, MsgRecord> {


    public MsgRecord getCustomerIdBySrcId(String srcId){
        return dao.getCustomerIdBySrcId(srcId);
    }

}
