package com.ty.modules.tunnel.send.container.entity.container.yx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by tykfkf02 on 2017/7/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdYxDr {
    private String id;
    private String da;
    private String sa;
    private String su;
    private String sd;
    private String dd;
    private String rp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDa() {
        return da;
    }

    public void setDa(String da) {
        this.da = da;
    }

    public String getSa() {
        return sa;
    }

    public void setSa(String sa) {
        this.sa = sa;
    }

    public String getSu() {
        return su;
    }

    public void setSu(String su) {
        this.su = su;
    }

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public String getDd() {
        return dd;
    }

    public void setDd(String dd) {
        this.dd = dd;
    }

    public String getRp() {
        return rp;
    }

    public void setRp(String rp) {
        this.rp = rp;
    }

    public String getReceiveStatus() {
        return "DELIVRD".equals(this.su)?"1":"2";
    }
}
