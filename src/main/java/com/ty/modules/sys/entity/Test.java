package com.ty.modules.sys.entity;

/**
 * Created by Ysw on 2016/5/23.
 */
public class Test {

    private String id;
    private String name;

    public Test(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
