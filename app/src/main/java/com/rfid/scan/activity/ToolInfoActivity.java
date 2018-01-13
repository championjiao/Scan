package com.rfid.scan.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rfid.scan.MyU8Series;
import com.rfid.scan.R;
import com.rfid.scan.adapter.SeriesAdapter;
import com.rfid.scan.entity.SetInfo;
import com.rfid.scan.series.model.Message;
import com.rfid.scan.series.model.ResponseHandler;
import com.rfid.scan.series.operation.U8Series;
import com.rfid.scan.series.reader.model.InventoryBuffer;
import com.rfid.scan.series.reader.server.ReaderHelper;
import com.rfid.scan.service.BoxDataUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.rfid.scan.R.id.lsvMore;
import static com.rfid.scan.UHFApplication.OP_Type_Change;
import static com.rfid.scan.UHFApplication.OP_Type_Recognize;
import static com.rfid.scan.UHFApplication.OP_Type_Search;

public class ToolInfoActivity extends AppCompatActivity implements View.OnClickListener{

    //rfid 显示
    private ListView mlistView;
    private TextView mTextBoxName;
    private TextView mTextCode;
    private TextView mTextCodeP;
    private TextView mTextTipStatus;
    private ImageView mImgPic;
    private List<instruValue> mList = new ArrayList<instruValue>();
    private List<instruValue> mList1 = new ArrayList<instruValue>();
    private List<instruValue> mList2 = new ArrayList<instruValue>();
    private Set<String> mScanList = new HashSet<String>();
    private TextView mTextTab1;
    private TextView mTextTab2;
    private InstruAdapter mInstruAdapter;

    //op
    private Button mBnStart;
    private Button mBnChange;
    //当前rfid
    private SetInfo.RFIDInfoEx mRfidInfo;
    private String mStrRfid="";
    //当前环境上下文
    private Context mContext;
    //操作提示
    private Handler mHandler;
    private int inventoryTimeHistory = 0;
    private long mRefreshTime;

    //操作类型 1：查找 2：识别 3：替换
    private int mFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolinfo);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        InitView();

        Bundle bundle = getIntent().getExtras();
        String opType = bundle.getString("opType");
        if(opType.equalsIgnoreCase(OP_Type_Change)){//替换 一个现有的rfid
            mFlag = 3;
            mStrRfid = bundle.getString("toolRfid");
            actionBar.setTitle(R.string.str_toolInfo_replace);
            initData(mStrRfid);
            mBnChange.setVisibility(View.GONE);
        }else if(opType.equalsIgnoreCase(OP_Type_Search)){//查找  根据rfid找到对应的设备
            mFlag = 1;
            mStrRfid = bundle.getString("toolRfid");
            actionBar.setTitle(R.string.str_toolInfo_search);
            initData(mStrRfid);
            mBnChange.setVisibility(View.GONE);
        }else if(opType.equalsIgnoreCase(OP_Type_Recognize)){//识别 识别所有的设备，只有和工具包里的匹配就算
            mFlag=2;
            actionBar.setTitle(R.string.str_toolInfo_recognize);
            mBnChange.setVisibility(View.GONE);
            mScanList.clear();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 4:
                this.finish();
                break;
            case 80://扫描按键
                String buttonText  = mBnStart.getText().toString();
                if(buttonText.equalsIgnoreCase("开始")){
                    mBnStart.setText("停止");
                    startInventory();
                }else{
                    mBnStart.setText("开始");
                    stopInventory();
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_index1:
                if(MyU8Series.getInstance().isInventory()) return;
                mTextTab1.setBackgroundResource(R.drawable.textview_selector);
                mTextTab2.setBackgroundResource(0);
                mList.clear();
                mList.addAll(mList1);
                mInstruAdapter.notifyDataSetChanged();
                break;
            case R.id.tab_index2:
                if(MyU8Series.getInstance().isInventory()) return;
                mTextTab2.setBackgroundResource(R.drawable.textview_selector);
                mTextTab1.setBackgroundResource(0);
                mList.clear();
                mList.addAll(mList2);
                mInstruAdapter.notifyDataSetChanged();
                break;
            case R.id.bt_start:
                String buttonText  = mBnStart.getText().toString();
                if(buttonText.equalsIgnoreCase("开始")){
                    mBnStart.setText("停止");
                    startInventory();
                }else{
                    mBnStart.setText("开始");
                    stopInventory();
                }
                break;
            case R.id.bt_change:
                String buttonText2  = mBnChange.getText().toString();
                if(buttonText2.equalsIgnoreCase("替换")){
                    mBnChange.setText("暂停");
//                    popWin(getWindow().getDecorView().findViewById(R.id.bt_change),
//                            "1212",
//                            "1212",
//                            "121222222");
                    //startInventory();
                }else{
                    mBnChange.setText("替换");
                    stopInventory();
                }
                break;
        }
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
                        if(mFlag == 1){//查找  找到就暂停
                            String strEPC = map.strEPC.replace(" ","");
                            if(mStrRfid.equalsIgnoreCase(strEPC)){
                                stopInventory();
                                mBnStart.setText("开始");
                                Toast.makeText(mContext, "已找到设备！", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }else if(mFlag == 2) {//识别 找到后 弹框确认是否找到
                            String strEPC = map.strEPC.replace(" ","");
                            if(!mScanList.contains(strEPC)){//已经扫描到的就过滤掉
                                mScanList.add(strEPC);
                                SetInfo.RFIDInfoEx rfidInfoEx = BoxDataUtil.getInstance().getmRfidMap().get(strEPC);
                                if(rfidInfoEx != null){//识别到就暂停
                                    stopInventory();
                                    mBnStart.setText("开始");
                                    initData(strEPC);
                                    break;
                                }
                            }
                        }else if(mFlag == 3){ //替换  弹框确认是否要替换
                            String strEPC = map.strEPC.replace(" ","");
                            SetInfo.RFIDInfoEx rfidInfoEx = BoxDataUtil.getInstance().getmRfidMap().get(strEPC);
                            if(rfidInfoEx != null){//说明没有在设备列表
                                stopInventory();
                                mBnStart.setText("开始");
                                popWin(getWindow().getDecorView().findViewById(R.id.bt_change),
                                        mRfidInfo.getCode(),
                                        mRfidInfo.getCode_P(),
                                        mRfidInfo.getImg(),mStrRfid,strEPC);
                                break;
                            }
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
        try {
            MyU8Series.getInstance().stopInventory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHandler.removeCallbacks(mRefreshRunnable);
    }

    private void InitView() {
        mlistView = (ListView)findViewById(R.id.list_info);
        mTextBoxName = (TextView)findViewById(R.id.text_boxName);
        mTextCode = (TextView)findViewById(R.id.text_code);
        mTextCodeP = (TextView)findViewById(R.id.text_codep);
        mTextTipStatus = (TextView)findViewById(R.id.text_statues);
        mImgPic = (ImageView)findViewById(R.id.img_epc);

        mTextTab1 = (TextView)findViewById(R.id.tab_index1);
        mTextTab2 = (TextView)findViewById(R.id.tab_index2);
        mBnStart = (Button)findViewById(R.id.bt_start);
        mBnChange = (Button)findViewById(R.id.bt_change);

        mTextTab1.setOnClickListener(this);
        mTextTab2.setOnClickListener(this);
        mBnStart.setOnClickListener(this);
        mBnChange.setOnClickListener(this);

        mContext = getApplicationContext();
        mHandler = new Handler();
    }
    void initData(String toolRfid){
        //读取工具包数据
        mRfidInfo= (SetInfo.RFIDInfoEx) BoxDataUtil.getInstance().getmRfidMap().get(toolRfid);
        mTextBoxName.setText(mRfidInfo.getBoxName());
        //工具图片
        String filePath = BoxDataUtil.getInstance().getDiskDir()+mRfidInfo.getImg();
        File file = new File(filePath);
        Glide.with(mContext).
                load(file).
                asBitmap().
                diskCacheStrategy(DiskCacheStrategy.RESULT).//保存最终图片
                into(mImgPic);
        //rfid
        mTextCode.setText(mRfidInfo.getCode());
        mTextCodeP.setText(mRfidInfo.getCode_P());

        mList1.add(new instruValue("Factory:",mRfidInfo.getFactory()));
        mList1.add(new instruValue("Valid time:",mRfidInfo.getValidTime()));
        mList1.add(new instruValue("Production date:",mRfidInfo.getProductionDate()));
        mList1.add(new instruValue("Purchase date:",mRfidInfo.getPruchaseDate()));
        mList1.add(new instruValue("Material:",mRfidInfo.getMaterial()));
        mList1.add(new instruValue("Size:",mRfidInfo.getSize()));

        mList2.add(new instruValue("Check In:",mRfidInfo.getCheckIn()));
        mList2.add(new instruValue("Stock:",mRfidInfo.getStock()));
        mList2.add(new instruValue("Stock Out:",mRfidInfo.getOutStock()));
        mList2.add(new instruValue("Wash:",mRfidInfo.getWash()));

        mTextTab1.setBackgroundResource(R.drawable.textview_selector);
        mTextTab2.setBackgroundResource(0);
        mList.addAll(mList1);
        mInstruAdapter = new InstruAdapter(mContext,mList);
        mlistView.setAdapter(mInstruAdapter);
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class InstruAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private Context context;
        private List<instruValue> list = null;

        public InstruAdapter(Context context, List<instruValue> list){
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
            this.list = list;
        }

        public final class ListItemView{                //自定义控件集合
            public TextView mTextTitle;
            public TextView mTextInfo;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View layout, ViewGroup parent) {
            ListItemView listItemView = null;
            if(layout == null){
                listItemView = new ListItemView();
                layout = layoutInflater.inflate(R.layout.tool_item_info, null);
                listItemView.mTextTitle = (TextView)layout.findViewById(R.id.text_title);
                listItemView.mTextInfo = (TextView)layout.findViewById(R.id.text_content);
                layout.setTag(listItemView);
            }else{
                listItemView = (ListItemView) layout.getTag();
            }
            instruValue map = list.get(position);
            listItemView.mTextInfo.setText(map.getContent());
            listItemView.mTextTitle.setText(map.getTitle());
            return layout;
        }
    }

    public static class instruValue{
        private String title;
        private String content;

        public instruValue(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "instruValue{" +
                    "title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    private void popWin(View v,String code,String codeP,String img,final String oldEPC,final String newEPC){
        View popupView = ToolInfoActivity.this.getLayoutInflater().inflate(R.layout.confirm_window, null);

        TextView textCode = (TextView)popupView.findViewById(R.id.text_code);
        TextView textCodeP = (TextView)popupView.findViewById(R.id.text_codep);
        ImageView imgPic = (ImageView)popupView.findViewById(R.id.img_icon);
        Button bnOk = (Button)popupView.findViewById(R.id.bt_ok);

        textCode.setText(code);
        textCodeP.setText(codeP);
        String filePath = BoxDataUtil.getInstance().getDiskDir()+img;
        File file = new File(filePath);
        Glide.with(mContext).
                load(file).
                asBitmap().
                diskCacheStrategy(DiskCacheStrategy.RESULT).//保存最终图片
                into(imgPic);

        final PopupWindow window = new PopupWindow(popupView, 280, 400);
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        window.showAtLocation(v, Gravity.CENTER,0,0);

        bnOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //更换rfid
                BoxDataUtil.getInstance().changeRfidData(oldEPC,newEPC);
                window.dismiss();
            }
        });
    }
}
