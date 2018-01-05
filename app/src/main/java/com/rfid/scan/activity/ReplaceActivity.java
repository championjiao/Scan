package com.rfid.scan.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rfid.scan.R;
import com.rfid.scan.entity.BoxData;
import com.rfid.scan.service.BoxDataUtil;

public class ReplaceActivity extends AppCompatActivity {

    private ListView mlistView;
    private TextView mTextBoxName;
    private TextView mTextCode;
    private TextView mTextCodeP;
    private ImageView mImgPic;
    private TextView mTextCurrentStatus;

    private BoxData mBoxData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(R.string.action_replace);

        mlistView = (ListView)findViewById(R.id.list_info);
        mTextBoxName = (TextView)findViewById(R.id.text_boxName);
        mTextCode = (TextView)findViewById(R.id.text_code);
        mTextCodeP = (TextView)findViewById(R.id.text_codep);
        mImgPic = (ImageView)findViewById(R.id.img_epc);
        mTextCurrentStatus = (TextView)findViewById(R.id.text_statues);

        //上层传递过来的数据
        String boxRfid = (String)getIntent().getSerializableExtra("boxRfid");

        //读取工具包数据
        mBoxData= (BoxData) BoxDataUtil.getInstance().getmBoxMap().get(boxRfid);
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
}
