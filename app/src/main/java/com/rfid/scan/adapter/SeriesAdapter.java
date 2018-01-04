package com.rfid.scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rfid.scan.R;
import com.rfid.scan.entity.SetInfo.RFIDInfoEx;
import com.rfid.scan.series.reader.model.InventoryBuffer;

import java.util.List;

public class SeriesAdapter extends BaseAdapter{

	private LayoutInflater layoutInflater;
	private Context context;

	private List<RFIDInfoEx> list = null;

	public SeriesAdapter(Context context, List<RFIDInfoEx> list){
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.list = list;
	}
	
	public final class ListItemView{                //自定义控件集合     
		public TextView mTextInfo;
		public TextView mTextNum;
		public TextView mTextExpriedTag;
		public ImageView mImgEpc;
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
			layout = layoutInflater.inflate(R.layout.activity_item, null);
			listItemView.mTextInfo = (TextView)layout.findViewById(R.id.txtTip_info);
			listItemView.mTextNum = (TextView)layout.findViewById(R.id.txtTip_num);
			listItemView.mTextExpriedTag = (TextView)layout.findViewById(R.id.txtTip_expriedtag);
			listItemView.mImgEpc = (ImageView)layout.findViewById(R.id.img_epc);

			layout.setTag(listItemView);
		}else{
			listItemView = (ListItemView) layout.getTag();
		}

		RFIDInfoEx rfidInfoEx = list.get(position);
		/*if(map.strEPC.equals("30000002") || map.strEPC.equals("300833B2DDD9014000000000")){
			System.out.println("遇到有问题的标签");
		}*/
		

		listItemView.mTextInfo.setText(rfidInfoEx.getMaterial());
		listItemView.mTextNum.setText(rfidInfoEx.getCount());
		listItemView.mTextExpriedTag.setText(rfidInfoEx.getValidTime());
		listItemView.mImgEpc.setImageResource(R.drawable.ic_home_black_24dp);
		return layout;
	}
}
