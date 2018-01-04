package com.rfid.scan.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxb on 2017-11-20.
 */

public class BoxListData {
    private List<BoxInfo> boxlist = new ArrayList<>();

    public List<BoxInfo> getBoxlist() {
        return boxlist;
    }

    public void setBoxlist(List<BoxInfo> boxlist) {
        this.boxlist = boxlist;
    }

    @Override
    public String toString() {
        return "BoxListData{" +
                "boxlist=" + boxlist.toString() +
                '}';
    }

    public static class BoxInfo {
        private String desc;
        private String path;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return "BoxInfo{" +
                    "desc='" + desc + '\'' +
                    ", path='" + path + '\'' +
                    '}';
        }
    }

}
