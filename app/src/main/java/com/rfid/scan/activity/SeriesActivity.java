package com.rfid.scan.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.rfid.scan.R;
import com.rfid.scan.adapter.SeriesAdapter;
import com.rfid.scan.entity.BoxData;
import com.rfid.scan.entity.SetInfo;
import com.rfid.scan.series.model.ResponseHandler;
import com.rfid.scan.series.operation.U8Series;
import com.rfid.scan.series.reader.model.InventoryBuffer;
import com.rfid.scan.series.reader.server.ReaderHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SeriesActivity extends AppCompatActivity {

    private ListView mListView;
    private BoxData mBoxData;
    private SeriesAdapter seriesAdapter;

    public static boolean isInventory;
    private U8Series mUSeries;
    private Handler mHandler;
    private static Context context;
    private ReaderHelper mReaderHelper;
    private static InventoryBuffer m_curInventoryBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series1);

        mListView = (ListView)findViewById(R.id.list_view);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(R.string.action_inventory);

        context = getApplicationContext();
        mUSeries = U8Series.getInstance();

        //上层传递过来的数据
        mBoxData = (BoxData)getIntent().getSerializableExtra("boxData");

        seriesAdapter = new SeriesAdapter(this, mBoxData.getSetInfo().getInstruments());
        mListView.setAdapter(seriesAdapter);

        mHandler = new Handler();
        try {
            mReaderHelper = ReaderHelper.getDefaultHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isInventory)
            stopInventory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //停止轮询
    private void stopInventory() {
        isInventory = false;
        try {
            mUSeries.stopInventory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHandler.removeCallbacks(mRefreshRunnable);
        // inventoryTimeHistory += ((new Date().getTime()) - mRefreshTime) /
        // 1000;
//        btnStop.setEnabled(false);
//        btnStart.setEnabled(true);
    }

    //开始轮询
    private void startInventory() {

        mUSeries.startInventory(new ResponseHandler() {

            @Override
            public void onFailure(String msg) {
                super.onFailure(msg);
                Toast.makeText(context, getResources().getText(R.string.Disk_failure) + msg, Toast.LENGTH_SHORT).show();
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

                    //epc对应起来
                    //RFID  ====  map.strEPC
                    //匹配到对应的位 刷新
                    seriesAdapter.notifyDataSetChanged();
                } else {
                    System.out.println("盘询成功,返回标识异常");
                }
            }
        });
        //mHandler.postDelayed(mRefreshRunnable, 500);
    }

    private Runnable mRefreshRunnable = new Runnable() {
        public void run() {
            refreshText();
            mHandler.postDelayed(this, 2000);
        }
    };

    public void refreshText() {
        //刷新ui
//        long now = new Date().getTime();
//        String text = String.format(stringTxtRealInventoryCount, m_curInventoryBuffer.lsTagList.size(), mReaderHelper.getInventoryTotal(), m_curInventoryBuffer.nReadRate, (now - mRefreshTime) / 1000 + inventoryTimeHistory);
//        txtRealInventoryCount.setText(text);
    }
}
