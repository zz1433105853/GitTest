package com.ty.modules.msg.cxf;

import javax.jws.WebService;
import javax.ws.rs.QueryParam;

/**
 * Created by Ysw on 2016/5/30.
 */
@WebService
public interface MessageWebService {

    String sendMsg(@QueryParam("sn") String sn, @QueryParam("password") String password,@QueryParam("mobile") String mobile
            ,@QueryParam("content") String content
            ,@QueryParam("ext") String ext
            ,@QueryParam("sendTime") String sendTime);

}
