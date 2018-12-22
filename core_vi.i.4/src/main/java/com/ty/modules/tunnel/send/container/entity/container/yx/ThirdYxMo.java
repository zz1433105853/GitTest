package com.ty.modules.tunnel.send.container.entity.container.yx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by tykfkf02 on 2017/7/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdYxMo {
    private int dc;
    private String sa;
    private String da;
    private String sm;

    public int getDc() {
        return dc;
    }

    public void setDc(int dc) {
        this.dc = dc;
    }

    public String getSa() {
        return sa;
    }

    public void setSa(String sa) {
        this.sa = sa;
    }

    public String getDa() {
        return da;
    }

    public void setDa(String da) {
        this.da = da;
    }

    public String getSm() {
        return sm;
    }

    public void setSm(String sm) {
        this.sm = sm;
    }
}
