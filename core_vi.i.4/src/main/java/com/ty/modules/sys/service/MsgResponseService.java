package com.ty.modules.sys.service;

import com.google.common.collect.Maps;
import com.ty.common.service.CrudService;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.MsgResponse;
import com.ty.modules.sys.dao.MsgResponseDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by 阿水 on 2017/4/12 10:31.
 */
@Service
public class MsgResponseService extends CrudService<MsgResponseDao, MsgResponse> {

    /**
     * 批量插入
     * @param list
     * @return
     */
    /**
     * 批量插入
     * @param list
     * @return
     */
    @Transactional(readOnly = false)
    public int batchInsert(List<MsgResponse> list) {
        return this.dao.batchInsert(list);
    }

    @Transactional(readOnly = false)
    public int batchInsert(StringBuilder sql) {
        return this.dao.batchInsertSql(sql);
    }

    /**
     * 根据第三方返回的msgId查询之前发送的短信属于哪个客户
     * @param list
     * @return
     */
    public List<MsgResponse> loadMsgResponseInfoByMsgIds(List<String> list) {
        return this.dao.loadMsgResponseInfoByMsgIds(list);
    }

    public Map<String, MsgResponse> loadMsgResponseInfoMapByMsgIds(List<String> list) {
        Map<String, MsgResponse> result = Maps.newHashMap();
        List<MsgResponse> listResponse = loadMsgResponseInfoByMsgIds(list);
        if(listResponse!=null && !listResponse.isEmpty()) {
            for(MsgResponse mr : listResponse) {
                if(mr==null
                        || mr.getMsgRecord()==null
                        || mr.getMsgRecord().getCustomer()==null
                        || StringUtils.isBlank(mr.getMsgRecord().getCustomer().getId())
                        || StringUtils.isBlank(mr.getSrcId())
                        || StringUtils.isBlank(mr.getMsgId())
                ) continue;

                result.put(mr.getMsgId(), mr);
            }
        }
        return result;
    }
}
