package com.rfid.scan.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rfid.scan.entity.BoxData;
import com.rfid.scan.entity.BoxListData;
import com.rfid.scan.entity.SetInfo;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jxb on 2018-01-05.
 */

public class BoxDataUtil {

    private static String TAG = "BoxDataUtil";

    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    //存储工具箱索引数据
    private BoxListData mBoxListData = null;
    //所有的rfid 及其对象
    private Map<String, SetInfo.RFIDInfoEx> mRfidMap = new HashMap<String, SetInfo.RFIDInfoEx>();
    //所有工具包的rfid 及其对象
    private Map<String, BoxData> mBoxMap = new HashMap<String, BoxData>();

    private BoxDataUtil() {}
    private static volatile BoxDataUtil instance;

    public static BoxDataUtil getInstance() {
        if (instance == null) {
            synchronized (BoxDataUtil.class) {
                if (instance == null) {
                    instance = new BoxDataUtil();
                }
            }
        }
        return instance;
    }

    public String getDiskDir() {
        String cachePath = Environment.getExternalStorageDirectory().getPath()+"/storage/scan/";
        return cachePath;
    }


    public void initData(Context context) {
        this.mContext = context.getApplicationContext();

        //读取数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                String text = readAssetsTxt("boxlist.json");
                mBoxListData = JSONObject.parseObject(text, BoxListData.class);
                for(BoxListData.BoxInfo boxInfo : mBoxListData.getBoxlist()){
                    String strPath = boxInfo.getJsonPath();
                    String content = readAssetsTxt(strPath);
                    if(!content.isEmpty()){
                        BoxData boxData= JSONObject.parseObject(content, BoxData.class);
                        mBoxMap.put(boxInfo.getRFID(),boxData);

                        for(SetInfo.RFIDInfoEx rfidInfo : boxData.getSetInfo().getInstruments()){
                            rfidInfo.setBoxName(boxInfo.getCode());
                            rfidInfo.setBoxRFID(boxInfo.getRFID());
                            mRfidMap.put(rfidInfo.getRFID(),rfidInfo);
                        }
                    }
                }
            }
        }).start();
    }


    public BoxListData getmBoxListData() {
        return mBoxListData;
    }

    public Map<String, SetInfo.RFIDInfoEx> getmRfidMap() {
        return mRfidMap;
    }

    public Map<String, BoxData> getmBoxMap() {
        return mBoxMap;
    }

    //需要保存这个工具箱的所有数据
    public void changeRfidData(String oldRfid,String newRfid) {
        //找到要替换的工具
        SetInfo.RFIDInfoEx rfidInfoEx = getmRfidMap().get(oldRfid);
        //这个工具对应的工具箱
        String boxRfid = rfidInfoEx.getBoxRFID();

        //工具箱索引数据
        final BoxListData.BoxInfo info = mBoxListData.getBoxInfo(boxRfid);

        //更新数据 工具箱数据
        final BoxData box = mBoxMap.get(boxRfid);
        for (SetInfo.RFIDInfoEx rfidInfo : box.getSetInfo().getInstruments()){
            if(rfidInfo.getRFID().equalsIgnoreCase(oldRfid)){
                rfidInfoEx.setRFID(newRfid);
                mRfidMap.remove(oldRfid);
                mRfidMap.put(newRfid,rfidInfoEx);
                break;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                //保存数据
                String json = JSON.toJSONString(box);
                try {
                    writeSDFile(info.getJsonPath(),json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //用于更新工具箱索引数据
    private void saveBoxIndex() {
        if(mBoxListData != null){
            String json= JSON.toJSONString(mBoxListData);
            try {
                writeSDFile("boxlist.json",json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取assets下的txt文件，返回utf-8 String
     * @param fileName 不包括后缀
     * @return
     */
    private String readAssetsTxt(String fileName){
        try {
            String filePath = getDiskDir()+fileName;
            FileInputStream fin = new FileInputStream(filePath);
            InputStreamReader inputStreamReader=new InputStreamReader(fin,"UTF-8");
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder=new StringBuilder();
            while ((line=bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //存储路径
    ///storage/emulated/0/storage/scan/
    public void writeSDFile(String fileName, String write_str)
            throws IOException {
        String saveFile = getDiskDir() + fileName;
        try {
            FileOutputStream fos= new FileOutputStream(saveFile);
            fos.write(write_str.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
