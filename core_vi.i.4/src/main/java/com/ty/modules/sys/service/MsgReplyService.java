package com.ty.modules.sys.service;

import com.ty.common.service.CrudService;
import com.ty.modules.sys.dao.MsgReplyDao;
import com.ty.modules.tunnel.entity.MsgReply;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ljb on 2017/4/12 11:23.
 */
@Service
@Transactional(readOnly = true)
public class MsgReplyService extends CrudService<MsgReplyDao, MsgReply> {

    /**
     * 批量插入
     * @param list
     * @return
     */
    @Transactional(readOnly = false)
    public int batchInsert(List<MsgReply> list) {
        return this.dao.batchInsert(list);
    }

}
