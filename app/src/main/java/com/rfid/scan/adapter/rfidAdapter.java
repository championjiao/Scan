package com.rfid.scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rfid.scan.R;
import com.rfid.scan.entity.SetInfo;
import com.rfid.scan.service.BoxDataUtil;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class rfidAdapter extends BaseAdapter{

	private LayoutInflater layoutInflater;
	private Context mContext;
	SimpleDateFormat mCurrentTime= new SimpleDateFormat("yyyy-MM-dd");

	private List<String> list = new ArrayList<String>();

	public rfidAdapter(Context context, List<String> list){
		this.mContext = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.list = list;
	}
	
	public final class ListItemView{                //自定义控件集合     
		public TextView mTextEpc;
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

		ListItemView  listItemView = null;
		
		if(layout == null){
			listItemView = new ListItemView();
			layout = layoutInflater.inflate(R.layout.rfid_item, null);
			listItemView.mTextEpc = (TextView)layout.findViewById(R.id.mTextEpc);

			layout.setTag(listItemView);
		}else{
			listItemView = (ListItemView) layout.getTag();
		}

		String rfid = list.get(position);
		SetInfo.RFIDInfoEx rfidInfoEx = BoxDataUtil.getInstance().getmRfidMap().get(rfid);
		try {
			Date validTime=mCurrentTime.parse(rfidInfoEx.getValidTime());
			Date nowTime= new Date();
			if(((validTime.getTime() - nowTime.getTime())/(24*60*60*1000))<=0) {
				listItemView.mTextEpc.setBackgroundColor(mContext.getResources().getColor(R.color.colorred));
			}
			else{
				listItemView.mTextEpc.setBackgroundColor(mContext.getResources().getColor(R.color.text_green));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		listItemView.mTextEpc.setText(rfid);

		return layout;
	}
}
