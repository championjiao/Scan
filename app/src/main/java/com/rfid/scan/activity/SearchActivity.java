package com.rfid.scan.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.rfid.scan.R;
import com.wang.avi.AVLoadingIndicatorView;

public class SearchActivity extends AppCompatActivity {

    private AVLoadingIndicatorView mAvi;
    private LinearLayout    mLinearContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mAvi = (AVLoadingIndicatorView)findViewById(R.id.avi);
        mLinearContent = (LinearLayout)findViewById(R.id.linearcontent);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(R.string.action_search);
        startAnim();

        //stopAnim();
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

    //kaishi
    void startAnim(){
        mAvi.show();
        mLinearContent.setVisibility(View.GONE);
    }

    void stopAnim(){
        mAvi.hide();
        mLinearContent.setVisibility(View.VISIBLE);
    }
}
