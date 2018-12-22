package com.ty.modules.sys.service;

import com.ty.common.service.CrudService;
import com.ty.modules.msg.entity.MsgReport;
import com.ty.modules.sys.dao.MsgReportDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ljb on 2017/4/12 10:48.
 */
@Service
@Transactional(readOnly = true)
public class MsgReportService extends CrudService<MsgReportDao, MsgReport> {

    /**
     * 批量插入
     * @param list
     * @return
     */
    @Transactional(readOnly = false)
    public int batchInsert(List<MsgReport> list) {
        return this.dao.batchInsert(list);
    }

}
