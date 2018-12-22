package com.ty.modules.msg.util;

import com.ty.common.config.Global;
import com.ty.common.utils.CacheUtils;
import com.ty.common.utils.SpringContextHolder;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.Customer;
import com.ty.modules.msg.entity.Template;
import com.ty.modules.msg.service.TemplateService;

import java.util.List;

/**
 * Created by Ysw on 2017/2/16.
 */
public class TemplateUtil {

    private static TemplateService templateService = SpringContextHolder.getBean(TemplateService.class);

    public static Template getThisCustomerTplMap(String custometrId, String id) {
        Template template = (Template) CacheUtils.get(Global.CACHE_TPL_INFO,custometrId+id);
        return template;
    }

    public static Template getTpl(String customerId ,String id) {
       if(StringUtils.isBlank(customerId) || StringUtils.isBlank(id)) return null;
        return getThisCustomerTplMap(customerId,id);
    }

    public static void clearCache(String customerId) {
        Template queryT = new Template();
        queryT.setStatus("1");
        queryT.setCustomer(new Customer(customerId));
        List<Template> templateList = templateService.findList(queryT);
        for(Template t : templateList) {
            if(t==null) continue;
            CacheUtils.put(Global.CACHE_TPL_INFO,t.getCustomer().getId()+t.getId(),t);
        }
    }
}
