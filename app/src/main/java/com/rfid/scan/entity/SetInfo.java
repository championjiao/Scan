package com.rfid.scan.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxb on 2017-11-20.
 */

public class SetInfo implements Serializable {

    private List<RFIDInfoEx> Instruments = new ArrayList<>();

    public List<RFIDInfoEx> getInstruments() {
        return Instruments;
    }
    public void setInstruments(List<RFIDInfoEx> instruments) {
        Instruments = instruments;
    }

    @Override
    public String toString() {
        return "SetInfo{" +
                ", Instruments=" + Instruments.toString() +
                '}';
    }

    public static class RFIDInfoEx implements Serializable{
        private String i_id;
        private String RFID;
        private String Code;
        private String Sort;
        private String Code_P;
        private String ImgPath;
        private String Img;
        private String Count;
        private String CheckIn;
        private String OutStock;
        private String Stock;
        private String Wash;
        private String Factory;
        private String ValidTime;
        private String ProductionDate;
        private String PruchaseDate;
        private String Material;
        private String Size;

        private String Memo1;
        private String Memo2;
        private String Memo3;
        private String Memo4;
        private String Memo5;
        private String Memo6;
        private String Memo7;
        private String Memo8;


        private String boxRFID;
        private String boxName;

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

        public String getSort() {
            return Sort;
        }

        public void setSort(String sort) {
            Sort = sort;
        }

        public String getCode_P() {
            return Code_P;
        }

        public void setCode_P(String code_P) {
            Code_P = code_P;
        }

        public String getImgPath() {
            return ImgPath;
        }

        public void setImgPath(String imgPath) {
            ImgPath = imgPath;
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

        public String getCheckIn() {
            return CheckIn;
        }

        public void setCheckIn(String checkIn) {
            CheckIn = checkIn;
        }

        public String getOutStock() {
            return OutStock;
        }

        public void setOutStock(String outStock) {
            OutStock = outStock;
        }

        public String getStock() {
            return Stock;
        }

        public void setStock(String stock) {
            Stock = stock;
        }

        public String getWash() {
            return Wash;
        }

        public void setWash(String wash) {
            Wash = wash;
        }

        public String getFactory() {
            return Factory;
        }

        public void setFactory(String factory) {
            Factory = factory;
        }

        public String getValidTime() {
            return ValidTime;
        }

        public void setValidTime(String validTime) {
            ValidTime = validTime;
        }

        public String getProductionDate() {
            return ProductionDate;
        }

        public void setProductionDate(String productionDate) {
            ProductionDate = productionDate;
        }

        public String getPruchaseDate() {
            return PruchaseDate;
        }

        public void setPruchaseDate(String pruchaseDate) {
            PruchaseDate = pruchaseDate;
        }

        public String getMaterial() {
            return Material;
        }

        public void setMaterial(String material) {
            Material = material;
        }

        public String getSize() {
            return Size;
        }

        public void setSize(String size) {
            Size = size;
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

        public String getBoxRFID() {
            return boxRFID;
        }

        public void setBoxRFID(String boxRFID) {
            this.boxRFID = boxRFID;
        }

        public String getBoxName() {
            return boxName;
        }

        public void setBoxName(String boxName) {
            this.boxName = boxName;
        }

        @Override
        public String toString() {
            return "RFIDInfoEx{" +
                    "i_id='" + i_id + '\'' +
                    ", RFID='" + RFID + '\'' +
                    ", Code='" + Code + '\'' +
                    ", Sort='" + Sort + '\'' +
                    ", Code_P='" + Code_P + '\'' +
                    ", ImgPath='" + ImgPath + '\'' +
                    ", Img='" + Img + '\'' +
                    ", Count='" + Count + '\'' +
                    ", CheckIn='" + CheckIn + '\'' +
                    ", OutStock='" + OutStock + '\'' +
                    ", Stock='" + Stock + '\'' +
                    ", Wash='" + Wash + '\'' +
                    ", Factory='" + Factory + '\'' +
                    ", ValidTime='" + ValidTime + '\'' +
                    ", ProductionDate='" + ProductionDate + '\'' +
                    ", PruchaseDate='" + PruchaseDate + '\'' +
                    ", Material='" + Material + '\'' +
                    ", Size='" + Size + '\'' +
                    ", Memo1='" + Memo1 + '\'' +
                    ", Memo2='" + Memo2 + '\'' +
                    ", Memo3='" + Memo3 + '\'' +
                    ", Memo4='" + Memo4 + '\'' +
                    ", Memo5='" + Memo5 + '\'' +
                    ", Memo6='" + Memo6 + '\'' +
                    ", Memo7='" + Memo7 + '\'' +
                    ", Memo8='" + Memo8 + '\'' +
                    ", boxRFID='" + boxRFID + '\'' +
                    ", boxName='" + boxName + '\'' +
                    '}';
        }
    }
}
