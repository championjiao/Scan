package com.rfid.scan.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;

import com.alibaba.fastjson.JSONObject;
import com.rfid.scan.entity.BoxData;
import com.rfid.scan.entity.BoxListData;
import com.rfid.scan.entity.SetInfo;

import java.io.BufferedReader;
import java.io.File;
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
    private Map<String, Object> mBoxMap = new HashMap<String, Object>();

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


    public void initData(Context context) {
        this.mContext = context.getApplicationContext();

        //读取数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                String text = readAssetsTxt(mContext,"boxlist.json");
                mBoxListData = JSONObject.parseObject(text, BoxListData.class);
                for(BoxListData.BoxInfo boxInfo : mBoxListData.getBoxlist()){
                    String strPath = boxInfo.getPath();
                    String content = readAssetsTxt(mContext,strPath);
                    if(!content.isEmpty()){
                        BoxData boxData= JSONObject.parseObject(content, BoxData.class);
                        mBoxMap.put(boxInfo.getRfid(),boxData);

                        for(SetInfo.RFIDInfoEx rfidInfo : boxData.getSetInfo().getInstruments()){
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

    public Map<String, Object> getmBoxMap() {
        return mBoxMap;
    }

    /**
     * 读取assets下的txt文件，返回utf-8 String
     * @param context
     * @param fileName 不包括后缀
     * @return
     */
    private static String readAssetsTxt(Context context, String fileName){
        try {
            InputStreamReader inputStreamReader=new InputStreamReader(context.getAssets().open(fileName),"UTF-8");
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
}
