package com.rfid.scan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.rfid.scan.MyU8Series;
import com.rfid.scan.R;
import com.rfid.scan.entity.BoxListData;
import com.rfid.scan.series.model.ResponseHandler;
import com.rfid.scan.series.operation.U8Series;
import com.rfid.scan.series.reader.model.InventoryBuffer;
import com.rfid.scan.service.BoxDataUtil;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rfid.scan.UHFApplication.OP_Type_Change;
import static com.rfid.scan.UHFApplication.OP_Type_Search;
import static com.rfid.scan.UHFApplication.OP_Type_Series;

public class AutoActivity extends AppCompatActivity {

    private Handler mHandler;
    private int inventoryTimeHistory = 0;
    private long mRefreshTime;
    private Context mContext;

    private TextView mTextTipStatus;
    private BoxListData mBoxListData;
    private Map<String,BoxListData.BoxInfo> mMapList = new HashMap<String,BoxListData.BoxInfo>();

    private String mFlag = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.action_scan);

        mTextTipStatus = (TextView)findViewById(R.id.text_statues);
        mContext = getApplicationContext();
        mHandler = new Handler();

        mBoxListData = BoxDataUtil.getInstance().getmBoxListData();

        for(BoxListData.BoxInfo boxInfo : mBoxListData.getBoxlist()){
            mMapList.put(boxInfo.getRFID(),boxInfo);
        }

        Bundle bundle = getIntent().getExtras();
        mFlag = bundle.getString("opType");

        startInventory();
    }

    //开始轮询
    private void startInventory() {
        mRefreshTime = new Date().getTime();
        String text = String.format(this.getString(R.string.inventoryCountText), 0, 0, 0, 0);
        mTextTipStatus.setText(text);

        MyU8Series.getInstance().startInventory(new ResponseHandler() {
            @Override
            public void onFailure(String msg) {
                super.onFailure(msg);
                Toast.makeText(mContext, getResources().getText(R.string.Disk_failure) + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String msg, Object data, byte[] parameters) {
                super.onSuccess(msg, data, parameters);
                if (msg.equals(U8Series.REFRESHTEXT)) {
                    refreshText();
                } else if (msg.equals(U8Series.REFRESHLIST)) {
                    //取到数据
                    List<InventoryBuffer.InventoryTagMap> inventoryTagData = new ArrayList<InventoryBuffer.InventoryTagMap>();
                    inventoryTagData.addAll((Collection<? extends InventoryBuffer.InventoryTagMap>) data);

                    for(InventoryBuffer.InventoryTagMap map:inventoryTagData){
                        //匹配到设备
                        String strEPC = map.strEPC.replace(" ","");
                        BoxListData.BoxInfo boxInfo = mMapList.get(strEPC);
                        if(boxInfo != null){
                            stopInventory();

                            Intent intent=new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("boxRfid",boxInfo.getRFID());
                            bundle.putString("boxDesp",boxInfo.getCode());
                            //跳转
                            if(mFlag.equalsIgnoreCase(OP_Type_Series)){
                                intent.setClass(AutoActivity.this, SeriesActivity.class);
                            }else if(mFlag.equalsIgnoreCase(OP_Type_Search)){
                                bundle.putString("opType",OP_Type_Search);
                                intent.setClass(AutoActivity.this, ToolsActivity.class);
                            }else if(mFlag.equalsIgnoreCase(OP_Type_Change)){
                                bundle.putString("opType",OP_Type_Change);
                                intent.setClass(AutoActivity.this, ToolsActivity.class);
                            }
                            intent.putExtras(bundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                } else {
                    System.out.println("盘询成功,返回标识异常");
                }
            }
        });
        mHandler.postDelayed(mRefreshRunnable, 500);
    }

    //停止轮询
    private void stopInventory() {
        MyU8Series.getInstance().stopInventory();
        mHandler.removeCallbacks(mRefreshRunnable);
    }
    @Override
    public void onPause() {
        super.onPause();
        MyU8Series.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        MyU8Series.getInstance().onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyU8Series.getInstance().onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //MyU8Series.getInstance().EndWork();
    }

    private Runnable mRefreshRunnable = new Runnable() {
        public void run() {
            refreshText();
            mHandler.postDelayed(this, 2000);
        }
    };

    public void refreshText() {
        //刷新ui
        long now = new Date().getTime();
        String text = String.format(this.getString(R.string.inventoryCountText),
                MyU8Series.getInstance().getInventorySize(),
                MyU8Series.getInstance().getInventoryTotal(),
                MyU8Series.getInstance().getReadRate(),
                (now - mRefreshTime) / 1000 + inventoryTimeHistory);
        mTextTipStatus.setText(text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                stopInventory();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
