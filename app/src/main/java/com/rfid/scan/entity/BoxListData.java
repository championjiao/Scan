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


    public BoxInfo getBoxInfo(String rfid) {
        BoxInfo box = new BoxInfo();
        for(BoxInfo boxInfo:boxlist){
            if(boxInfo.getRFID().equalsIgnoreCase(rfid)){
                box = boxInfo;
                break;
            }
        }
        return box;
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
        private String i_id;
        private String RFID;
        private String Code;
        private String ImgPath;
        private String JsonPath;
        private String Img;
        private String Count;
        private String Memo1;
        private String Memo2;
        private String Memo3;
        private String Memo4;
        private String Memo5;
        private String Memo6;
        private String Memo7;
        private String Memo8;

        public String getI_id() {
            return i_id;
        }

        public void setI_id(String i_id) {
            this.i_id = i_id;
        }

        public String getRFID() {
            return RFID;
        }

        public void setRFID(String RFID) {
            this.RFID = RFID;
        }

        public String getCode() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

        public String getImgPath() {
            return ImgPath;
        }

        public void setImgPath(String imgPath) {
            ImgPath = imgPath;
        }

        public String getJsonPath() {
            return JsonPath;
        }

        public void setJsonPath(String jsonPath) {
            JsonPath = jsonPath;
        }

        public String getImg() {
            return Img;
        }

        public void setImg(String img) {
            Img = img;
        }

        public String getCount() {
            return Count;
        }

        public void setCount(String count) {
            Count = count;
        }

        public String getMemo1() {
            return Memo1;
        }

        public void setMemo1(String memo1) {
            Memo1 = memo1;
        }

        public String getMemo2() {
            return Memo2;
        }

        public void setMemo2(String memo2) {
            Memo2 = memo2;
        }

        public String getMemo3() {
            return Memo3;
        }

        public void setMemo3(String memo3) {
            Memo3 = memo3;
        }

        public String getMemo4() {
            return Memo4;
        }

        public void setMemo4(String memo4) {
            Memo4 = memo4;
        }

        public String getMemo5() {
            return Memo5;
        }

        public void setMemo5(String memo5) {
            Memo5 = memo5;
        }

        public String getMemo6() {
            return Memo6;
        }

        public void setMemo6(String memo6) {
            Memo6 = memo6;
        }

        public String getMemo7() {
            return Memo7;
        }

        public void setMemo7(String memo7) {
            Memo7 = memo7;
        }

        public String getMemo8() {
            return Memo8;
        }

        public void setMemo8(String memo8) {
            Memo8 = memo8;
        }

        @Override
        public String toString() {
            return "BoxInfo{" +
                    "i_id='" + i_id + '\'' +
                    ", RFID='" + RFID + '\'' +
                    ", Code='" + Code + '\'' +
                    ", ImgPath='" + ImgPath + '\'' +
                    ", JsonPath='" + JsonPath + '\'' +
                    ", Img='" + Img + '\'' +
                    ", Count='" + Count + '\'' +
                    ", Memo1='" + Memo1 + '\'' +
                    ", Memo2='" + Memo2 + '\'' +
                    ", Memo3='" + Memo3 + '\'' +
                    ", Memo4='" + Memo4 + '\'' +
                    ", Memo5='" + Memo5 + '\'' +
                    ", Memo6='" + Memo6 + '\'' +
                    ", Memo7='" + Memo7 + '\'' +
                    ", Memo8='" + Memo8 + '\'' +
                    '}';
        }
    }

}
