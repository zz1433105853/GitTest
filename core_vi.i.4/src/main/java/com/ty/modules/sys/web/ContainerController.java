package com.ty.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ty.common.config.Global;
import com.ty.common.utils.StringUtils;
import com.ty.common.web.BaseController;
import com.ty.modules.tunnel.send.container.common.ContainerRepo;
import com.ty.modules.tunnel.send.container.cx.impl.AbstractThirdCxPartyMessageContainer;
import com.ty.modules.tunnel.send.container.impl.AbstractStraightMessageContainer;
import com.ty.modules.tunnel.send.container.impl.AbstractThirdPartyMessageContainer;
import com.ty.modules.tunnel.send.container.type.MessageContainer;
import com.ty.modules.tunnel.send.container.type.SdkMessageContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by 阿水 on 2017/4/18 15:51.
 */
@Controller
@RequestMapping("container")
public class ContainerController extends BaseController {

    @Autowired
    private ContainerRepo containerRepo;

    @RequestMapping(value = {"", "index.html"})
    public String index(Model model) {
        List<MessageContainer> sortedContainer = Lists.newLinkedList(ContainerRepo.getMessagerContainerMap().values());
        Collections.sort(sortedContainer, new Comparator<MessageContainer>() {
            @Override
            public int compare(MessageContainer o1, MessageContainer o2) {
                if(o1.getTunnelType()!=null && o2.getTunnelType()!=null && o1.getTunnelType().equals(o2.getTunnelType())) {
                    return o1.getTdName().compareTo(o2.getTdName());
                }else {
                    return o1.getTunnelType().compareTo(o2.getTunnelType());
                }
            }
        });
        List<AbstractStraightMessageContainer> straight = Lists.newLinkedList();
        List<AbstractThirdPartyMessageContainer> third = Lists.newLinkedList();
        for(MessageContainer mc : sortedContainer) {
            if(mc instanceof AbstractStraightMessageContainer) {
                straight.add((AbstractStraightMessageContainer) mc);
            }
            if(mc instanceof AbstractThirdPartyMessageContainer) {
                third.add((AbstractThirdPartyMessageContainer) mc);
            }
        }

    /*    List<AbstractThirdCxPartyMessageContainer> thirdCx = Lists.newLinkedList();
        for(MessageContainer cp : sortedContainer) {
            if(cp instanceof AbstractStraightMessageContainer) {
                straight.add((AbstractStraightMessageContainer) cp);
            }
            if(cp instanceof AbstractThirdCxPartyMessageContainer) {
                thirdCx.add((AbstractThirdCxPartyMessageContainer) cp);
            }
        }*/

        Map<String, List<AbstractStraightMessageContainer>> straightMcGroup = ContainerRepo.groupContainerMap(straight);
        model.addAttribute("straightMcGroup", straightMcGroup);
        model.addAttribute("third", third);
       /* model.addAttribute("thirdCx", thirdCx);*/
        return "modules/sys/index";
    }

    @RequestMapping({"dm.html"})
    public String dm(int dm, RedirectAttributes redirectAttributes) {
        Global.isDm = dm;
        redirectAttributes.addAttribute("修改区域限制开关成功");
        return "redirect:/container";
    }

    @RequestMapping("add")
    @ResponseBody
    public Map<String, Object> addContainer(String tdId) {
        Map<String, Object> result = Maps.newHashMap();
        if(StringUtils.isNotBlank(tdId)) {
            if(containerRepo.addTunnel(tdId)) {
                result.put("status", "success");
            }else {
                result.put("status", "fail");
                result.put("msg", "添加失败");
            }
        }else {
            result.put("status", "fail");
            result.put("msg", "缺少参数");
        }

        return result;
    }


    @RequestMapping("reload")
    @ResponseBody
    public Map<String, Object> reloadContainer(String tdId) {
        Map<String, Object> result = Maps.newHashMap();
        if(StringUtils.isNotBlank(tdId) && ContainerRepo.existContainer(tdId)) {
            boolean reloadResult = containerRepo.reloadTunnel(tdId);
            if(reloadResult) {
                result.put("status", "success");
            }else {
                result.put("status", "fail");
            }
        }else {
            result.put("status", "fail");
            result.put("msg", "不存在此通道");
        }
        return result;
    }

    @RequestMapping("remove")
    @ResponseBody
    public Map<String, Object> delTunnel(String tdId,String code) {
        Map<String, Object> result = Maps.newHashMap();
        if(StringUtils.isNotBlank(tdId)) {
            if(containerRepo.delTunnel(tdId)) {
                result.put("status", "success");
            }else {
                result.put("status", "fail");
                result.put("msg", "删除失败");
            }
        }else {
            result.put("status", "fail");
            result.put("msg", "缺少参数");
        }
        return result;
    }

    /**
     * 核心系统管理查询通道余额
     * @param tdId
     * @return
     */
    @RequestMapping("loadTunnelBalance")
    @ResponseBody
    public Map<String, Object> loadTunnelBalance(String tdId) {
        Map<String, Object> result = Maps.newHashMap();
        if(StringUtils.isNotBlank(tdId)) {

            MessageContainer mc = ContainerRepo.getMessagerContainerMap().get(tdId);
            if(mc instanceof SdkMessageContainer) {
                long resultCount = ((SdkMessageContainer)mc).getBalance();
                result.put("count", resultCount);
            }else {
                result.put("count", "直连通道无法查询余额");
            }

        }else {
            result.put("count", "缺少参数");
        }

        return result;
    }

}
