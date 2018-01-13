package com.rfid.scan.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.rfid.scan.R;
import com.rfid.scan.adapter.SeriesAdapter;
import com.rfid.scan.adapter.ToolsAdapter;
import com.rfid.scan.adapter.rfidAdapter;
import com.rfid.scan.entity.BoxData;
import com.rfid.scan.entity.SetInfo;
import com.rfid.scan.service.BoxDataUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rfid.scan.UHFApplication.OP_Type_Change;
import static com.rfid.scan.UHFApplication.OP_Type_Search;

public class ToolsActivity extends AppCompatActivity {

    private GridView mGridView;
    private TextView mTextBoxName;

    private ToolsAdapter toolsAdapter;
    private List<ToolsAdapter.ToolInfo> mList = new ArrayList<ToolsAdapter.ToolInfo>();
    private BoxData mBoxData;
    private Context mContext;
    private String  mBoxDesp;
    //操作类型 1：查找 2：识别 3：替换
    private String mFlag;

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ToolsAdapter.ToolInfo info = mList.get(position);
            pop(view,info.getRfids());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(R.string.str_tools);
        mContext = getApplicationContext();
        mGridView = (GridView)findViewById(R.id.grid_view);
        mTextBoxName = (TextView)findViewById(R.id.text_boxName);

        mGridView.setOnItemClickListener(onItemClick);

        Bundle bundle = getIntent().getExtras();
        mFlag = bundle.getString("opType");
        //上层传递过来的数据
        String boxRfid = bundle.getString("boxRfid");
        mBoxDesp = bundle.getString("boxDesp");

        //读取工具包数据
        mBoxData= (BoxData) BoxDataUtil.getInstance().getmBoxMap().get(boxRfid);
        mTextBoxName.setText(mBoxDesp);
        Map<String,ToolsAdapter.ToolInfo> map = new HashMap<String,ToolsAdapter.ToolInfo>();
        for(SetInfo.RFIDInfoEx infoEx:mBoxData.getSetInfo().getInstruments()){
            String code = infoEx.getCode();
            ToolsAdapter.ToolInfo info = map.get(code);
            if(info != null){
                info.getRfids().add(infoEx.getRFID());
            }
            else{
                info = new ToolsAdapter.ToolInfo();
                info.setCode(code);
                info.setCodeP(infoEx.getCode_P());
                info.getRfids().add(infoEx.getRFID());
                info.setImgPath(infoEx.getImg());
                map.put(code,info);
            }
        }
        for (Map.Entry<String, ToolsAdapter.ToolInfo> entry : map.entrySet()) {
            mList.add(entry.getValue());
        }

        toolsAdapter = new ToolsAdapter(this,mList);
        mGridView.setAdapter(toolsAdapter);
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

    private void pop(View v,final List<String> list){
        View popupView = ToolsActivity.this.getLayoutInflater().inflate(R.layout.popwindows, null);
        ListView lsvMore = (ListView) popupView.findViewById(R.id.lsvMore);
        lsvMore.setAdapter(new rfidAdapter(ToolsActivity.this, list));

        final PopupWindow window = new PopupWindow(popupView, 300, 400);
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
                bundle.putString("toolRfid",toolRfid);
                bundle.putString("boxDesp",mBoxDesp);
                bundle.putString("opType",mFlag);
                intent.putExtras(bundle);
                intent.setClass(ToolsActivity.this, ToolInfoActivity.class);
                startActivity(intent);
                window.dismiss();
            }
        });
    }
}
