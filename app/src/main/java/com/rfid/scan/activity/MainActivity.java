package com.rfid.scan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.rfid.scan.R;
import com.rfid.scan.entity.BoxData;
import com.rfid.scan.entity.BoxListData;
import com.rfid.scan.entity.SetInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextChoose;
    private TextView mTextAuto;
    private Button mBtChoose;
    private Button mBtAuto;
    private Spinner mSpinnerBox;
    private android.support.v7.app.ActionBar mActionBar;

    private BoxListData mBoxListData = null;
    private String mSetInfoPath = "";

    private List<String> boxList = new ArrayList<String>();               //工具包列表
    ArrayAdapter<String> mBoxAdapter = null;
    private int mCurrentPosition = 0;                                  //上一次点击的位置

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_inventory:
                    mTextChoose.setText(R.string.choose_inventory);
                    mTextAuto.setText(R.string.scan_inventory);
                    mBtChoose.setText(R.string.action_inventory);
                    mBtAuto.setText(R.string.scan_box_tag);
                    mActionBar.setTitle(R.string.action_inventory);
                    mCurrentPosition = 0;
                    return true;
                case R.id.navigation_search:
                    mTextChoose.setText(R.string.choose_search);
                    mTextAuto.setText(R.string.scan_search);
                    mBtChoose.setText(R.string.action_search);
                    mBtAuto.setText(R.string.scan_search);
                    mActionBar.setTitle(R.string.action_search);
                    mCurrentPosition = 1;
                    return true;
                case R.id.navigation_replace:
                    mTextChoose.setText(R.string.choose_replace);
                    mTextAuto.setText(R.string.scan_replace);
                    mBtChoose.setText(R.string.action_replace);
                    mBtAuto.setText(R.string.sacn_replace);
                    mActionBar.setTitle(R.string.action_replace);
                    mCurrentPosition = 2;
                    return true;
            }
            return false;
        }

    };

    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            String desp = mBoxAdapter.getItem(position);
            String path = "";
            for(BoxListData.BoxInfo boxInfo : mBoxListData.getBoxlist()){
                if(desp.equalsIgnoreCase(boxInfo.getDesc())){
                    mSetInfoPath = boxInfo.getPath();
                    break;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private Button.OnClickListener onClickListener = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_choose:
                    doChoose();
                    break;
                case R.id.bt_auto:
                    doAuto();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.action_inventory);

        mTextChoose = (TextView) findViewById(R.id.txtTip_choose);
        mTextAuto = (TextView) findViewById(R.id.txtTip_auto);
        mBtChoose = (Button) findViewById(R.id.bt_choose);
        mBtAuto = (Button) findViewById(R.id.bt_auto);
        mSpinnerBox = (Spinner) findViewById(R.id.box_spinner);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mSpinnerBox.setOnItemSelectedListener(mOnItemSelectedListener);
        mBtChoose.setOnClickListener(onClickListener);
        mBtAuto.setOnClickListener(onClickListener);
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
//                Toast.makeText(this, "你点击了“用户”按键！", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initData(){
        String text = readAssetsTxt(this,"boxlist.json");
        mBoxListData = JSONObject.parseObject(text, BoxListData.class);
        for(BoxListData.BoxInfo boxInfo : mBoxListData.getBoxlist()){
            boxList.add(boxInfo.getDesc());
        }
        mBoxAdapter = new ArrayAdapter<>(this, cn.fuen.xmldemo.R.layout.spinner_item, boxList);
        mSpinnerBox.setAdapter(mBoxAdapter);
    }

    public void doChoose(){
        if(mSetInfoPath.isEmpty()){
            Toast.makeText(this, "请选择工具包...", Toast.LENGTH_SHORT).show();
            return;
        }

        //读取工具包数据
        String text = readAssetsTxt(this,mSetInfoPath);
        BoxData boxData= JSONObject.parseObject(text, BoxData.class);

        Intent intent=new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("boxData",boxData);
        intent.putExtras(bundle);

        switch (mCurrentPosition) {
            case 0:
                intent.setClass(MainActivity.this, SeriesActivity.class);
                break;
            case 1:
                intent.setClass(MainActivity.this, SearchActivity.class);
                break;
            case 2:
                intent.setClass(MainActivity.this, ReplaceActivity.class);
                break;
        }
        startActivity(intent);
    }

    public void doAuto(){

        Intent intent=new Intent();
        switch (mCurrentPosition) {
            case 0:
                intent.setClass(MainActivity.this, SeriesActivity.class);
                break;
            case 1:
                intent.setClass(MainActivity.this, SearchActivity.class);
                break;
            case 2:
                intent.setClass(MainActivity.this, ReplaceActivity.class);
                break;
        }
        startActivity(intent);
    }
    /**
     * 读取assets下的txt文件，返回utf-8 String
     * @param context
     * @param fileName 不包括后缀
     * @return
     */
    public static String readAssetsTxt(Context context, String fileName){
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
