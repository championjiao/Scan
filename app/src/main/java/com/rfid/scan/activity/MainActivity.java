package com.rfid.scan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rfid.scan.R;
import com.rfid.scan.entity.BoxListData;
import com.rfid.scan.service.BoxDataUtil;

import java.util.ArrayList;
import java.util.List;

import static com.rfid.scan.UHFApplication.OP_Type_Change;
import static com.rfid.scan.UHFApplication.OP_Type_Recognize;
import static com.rfid.scan.UHFApplication.OP_Type_Search;
import static com.rfid.scan.UHFApplication.OP_Type_Series;

public class MainActivity extends AppCompatActivity {

    private TextView mTextChoose;
    private TextView mTextAuto;
    private Button mBtChoose;
    private Button mBtAuto;
    private Spinner mSpinnerBox;
    private android.support.v7.app.ActionBar mActionBar;

    private BoxListData mBoxListData = null;
    private String mBoxRfid = "";
    private String mBoxDesp = "";

    private List<String> boxList = new ArrayList<String>();               //工具包列表
    ArrayAdapter<String> mBoxAdapter = null;
    private String mOpType;                                  //上一次点击的位置

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
                    mOpType = OP_Type_Series;
                    return true;
                case R.id.navigation_search:
                    mTextChoose.setText(R.string.choose_search);
                    mTextAuto.setText(R.string.scan_search);
                    mBtChoose.setText(R.string.action_search);
                    mBtAuto.setText(R.string.scan_search);
                    mActionBar.setTitle(R.string.action_search);
                    mOpType = OP_Type_Search;
                    return true;
                case R.id.navigation_replace:
                    mTextChoose.setText(R.string.choose_replace);
                    mTextAuto.setText(R.string.scan_replace);
                    mBtChoose.setText(R.string.action_replace);
                    mBtAuto.setText(R.string.sacn_replace);
                    mActionBar.setTitle(R.string.action_replace);
                    mOpType = OP_Type_Change;
                    return true;
            }
            return false;
        }

    };

    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            String code = mBoxAdapter.getItem(position);
            String path = "";
            for(BoxListData.BoxInfo boxInfo : mBoxListData.getBoxlist()){
                if(code.equalsIgnoreCase(boxInfo.getCode())){
                    mBoxRfid = boxInfo.getRFID();
                    mBoxDesp = code;
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
            case R.id.action_recognize:
                //跳转到识别
                Intent intent=new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("opType",OP_Type_Recognize);
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, ToolInfoActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 4:
                this.finish();
                break;
            case 80:
                int j = 0;
                //startActivity(new Intent(MainActivity.this, TestActivity.class));
                break;
            default:
                int i = 0;
//                if(inventoryFragment instanceof InventoryFragment){
//                    ((InventoryFragment)inventoryFragment).onKeyDown(keyCode, event);
//                    return true;
//                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

//			case KeyEvent.KEYCODE_BACK:
////				Intent home=new Intent(Intent.ACTION_MAIN);
////				home.addCategory(Intent.CATEGORY_HOME);
////				startActivity(home);
////				System.exit(0);
//
//
//				break;

            default:
                int i = 0;
//                if (inventoryFragment instanceof InventoryFragment) {
//                    ((InventoryFragment) inventoryFragment).onKeyUp(keyCode, event);
//                    return true;
//                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void initData(){
        mBoxListData = BoxDataUtil.getInstance().getmBoxListData();
        for(BoxListData.BoxInfo boxInfo : mBoxListData.getBoxlist()){
            boxList.add(boxInfo.getCode());
        }
        mBoxAdapter = new ArrayAdapter<>(this, cn.fuen.xmldemo.R.layout.spinner_item, boxList);
        mSpinnerBox.setAdapter(mBoxAdapter);

        mBoxRfid = mBoxListData.getBoxlist().get(0).getRFID();
        mBoxDesp = mBoxListData.getBoxlist().get(0).getCode();
        mOpType = OP_Type_Series;
    }

    public void doChoose(){
        if(mBoxRfid == null){
            Toast.makeText(this, "读取工具包数据异常，请联系管理员！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mBoxRfid.isEmpty()){
            Toast.makeText(this, "请选择工具包...", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent=new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("boxRfid",mBoxRfid);
        bundle.putString("boxDesp",mBoxDesp);
        bundle.putString("opType",mOpType);
        if(mOpType.equalsIgnoreCase(OP_Type_Series)){
            intent.setClass(MainActivity.this, SeriesActivity.class);
        }else{
            intent.setClass(MainActivity.this, ToolsActivity.class);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void doAuto(){
        Intent intent=new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("opType",mOpType);
        intent.setClass(MainActivity.this, AutoActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
