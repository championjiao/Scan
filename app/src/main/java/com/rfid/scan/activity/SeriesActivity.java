package com.rfid.scan.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.rfid.scan.MyU8Series;
import com.rfid.scan.R;
import com.rfid.scan.adapter.SeriesAdapter;
import com.rfid.scan.adapter.ToolsAdapter;
import com.rfid.scan.entity.BoxData;
import com.rfid.scan.entity.SetInfo;
import com.rfid.scan.series.model.ResponseHandler;
import com.rfid.scan.series.operation.U8Series;
import com.rfid.scan.series.reader.model.InventoryBuffer;
import com.rfid.scan.series.reader.server.ReaderHelper;
import com.rfid.scan.service.BoxDataUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rfid.scan.UHFApplication.OP_Type_Recognize;

public class SeriesActivity extends AppCompatActivity implements View.OnClickListener{

    private GridView mGridView;
    private TextView mTextBoxName;
    private TextView mTextTipStatus;
    private TextView mTextToatl;
    private TextView mTextMissing;
    private Button mBtReset;

    private SeriesAdapter seriesAdapter;
    private Map<String,SeriesAdapter.setInfoEx> mList = new HashMap<String,SeriesAdapter.setInfoEx>();
    private BoxData mBoxData;
    private String  mBoxDesp;

    private Handler mHandler;
    private static Context mContext;

    private Map<String, SetInfo.RFIDInfoEx> mRfidMap = new HashMap<String, SetInfo.RFIDInfoEx>();

    private String stringTxtRealInventoryCount = "";
    private int inventoryTimeHistory = 0;
    private long mRefreshTime;

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SeriesAdapter.setInfoEx info = seriesAdapter.getContent(position);
            popWin(view,info.getRfids());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series1);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.action_inventory);

        mGridView = (GridView)findViewById(R.id.grid_view);
        mTextBoxName = (TextView)findViewById(R.id.text_boxName);
        mTextTipStatus = (TextView)findViewById(R.id.text_tipStatus);
        mTextToatl = (TextView)findViewById(R.id.text_total);
        mTextMissing = (TextView)findViewById(R.id.text_missing);
        mBtReset = (Button)findViewById(R.id.bt_reset);
        mBtReset.setOnClickListener(this);
        mBtReset.setText("开始");
        mGridView.setOnItemClickListener(onItemClick);

        mContext = getApplicationContext();

        //上层传递过来的数据
        Bundle bundle = getIntent().getExtras();
        String boxRfid = bundle.getString("boxRfid");
        mBoxDesp = bundle.getString("boxDesp");

        //读取工具包数据
        mBoxData= (BoxData) BoxDataUtil.getInstance().getmBoxMap().get(boxRfid);
        mTextBoxName.setText(mBoxDesp);
        //数据需要处理一下
        //找到code一样的
        for(SetInfo.RFIDInfoEx infoEx:mBoxData.getSetInfo().getInstruments()){
            String code = infoEx.getCode();
            SeriesAdapter.setInfoEx setinfo = mList.get(code);
            if(setinfo != null){
                setinfo.getRfids().add(infoEx.getRFID());
            }
            else{
                setinfo = new SeriesAdapter.setInfoEx();
                setinfo.setCode(code);
                setinfo.getRfids().add(infoEx.getRFID());
                setinfo.setImgPath(infoEx.getImg());
                mList.put(code,setinfo);
            }
        }
        seriesAdapter = new SeriesAdapter(this,mList);
        mGridView.setAdapter(seriesAdapter);
        stringTxtRealInventoryCount = this.getString(R.string.inventoryCountText);
        mTextTipStatus.setText(String.format(stringTxtRealInventoryCount, 0, 0, 0, 0));
        mHandler = new Handler();
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
    public void onDestroy() {
        super.onDestroy();
        //MyU8Series.getInstance().EndWork();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyU8Series.getInstance().onStop();
    }

    private void stopInventory() {
        try {
            MyU8Series.getInstance().stopInventory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHandler.removeCallbacks(mRefreshRunnable);
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

                    synchronized (SeriesActivity.class){
                        for(InventoryBuffer.InventoryTagMap map:inventoryTagData){
                            SeriesAdapter.setInfoEx setinfo = mList.get(map.strCRC);
                            if(setinfo != null){
                                setinfo.getCorrentRfids().add(map.strEPC);
                            }
                        }
                    }
                    seriesAdapter.notifyDataSetChanged();

                } else {
                    System.out.println("盘询成功,返回标识异常");
                }
            }
        });
        mHandler.postDelayed(mRefreshRunnable, 500);
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
    public void reset(){
        mBtReset.setText("重置");
        stopInventory();
        synchronized (SeriesActivity.class){
            for (Map.Entry<String, SeriesAdapter.setInfoEx> entry : mList.entrySet()) {
                SeriesAdapter.setInfoEx info = mList.get(entry.getKey());
                info.getCorrentRfids().clear();
            }
        }
        seriesAdapter.notifyDataSetChanged();

        startInventory();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_reset:
                reset();
                break;
        }
    }

    private void popWin(View v,final List<String> list){
        View popupView = SeriesActivity.this.getLayoutInflater().inflate(R.layout.popwindows, null);
        ListView lsvMore = (ListView) popupView.findViewById(R.id.lsvMore);
        lsvMore.setAdapter(new ArrayAdapter<String>(SeriesActivity.this, android.R.layout.simple_list_item_1, list));

        PopupWindow window = new PopupWindow(popupView, 300, 400);
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        window.showAtLocation(v, Gravity.CENTER,0,0);

        lsvMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String toolRfid = list.get(position);
                //跳转到详情页
                Intent intent=new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("toolRfid",toolRfid);
                bundle.putSerializable("boxDesp",mBoxDesp);
                //在这里到详情页 只有替换
                bundle.putString("opType",OP_Type_Recognize);
                intent.putExtras(bundle);
                intent.setClass(SeriesActivity.this, ToolInfoActivity.class);
                startActivity(intent);
            }
        });
    }
}
