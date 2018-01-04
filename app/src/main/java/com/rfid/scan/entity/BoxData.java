package com.rfid.scan.entity;

import java.io.Serializable;

/**
 * Created by jxb on 2017-11-20.
 */

public class BoxData implements Serializable {
    private SetInfo setInfo;

    public SetInfo getSetInfo() {
        return setInfo;
    }

    public void setSetInfo(SetInfo setInfo) {
        this.setInfo = setInfo;
    }
}
