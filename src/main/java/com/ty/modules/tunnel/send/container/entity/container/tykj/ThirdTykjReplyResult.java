package com.ty.modules.tunnel.send.container.entity.container.tykj;

import org.apache.log4j.Logger;

import java.util.List;

public class ThirdTykjReplyResult {
    private static Logger logger= Logger.getLogger(ThirdTykjReportResult.class);


    private List<ThirdTykjReplySimple> data;
    public List<ThirdTykjReplySimple> getData() {
        return data;
    }
    public void setData(List<ThirdTykjReplySimple> data)
    { this.data=data; }

}
