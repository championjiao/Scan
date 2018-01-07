package com.rfid.scan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rfid.scan.R;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeriesAdapter extends BaseAdapter{

	private LayoutInflater layoutInflater;
	private Context context;

	private Map<String,setInfoEx> list = null;
	private List<String> listWap = new ArrayList<String>();

	private int clickTemp = -1;
	public void setSeclection(int position) {
		clickTemp = position;
	}

	public SeriesAdapter(Context context, Map<String,setInfoEx> list){
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.list = list;
		listWap.clear();
		for (Map.Entry<String, setInfoEx> entry : list.entrySet()) {
			listWap.add(entry.getKey());
		}
	}
	
	public final class ListItemView{                //自定义控件集合     
		public TextView mTextInfo;
		public ImageView mImgEpc;
		public TextView mTextCorrentNum;
		public TextView mTextMissNum;
    }

    public setInfoEx getContent(int position){
		String key = listWap.get(position);
		return list.get(key);
	}

	@Override
	public int getCount() {
		return listWap.size();
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
			listItemView.mImgEpc = (ImageView)layout.findViewById(R.id.img_epc);
			listItemView.mTextCorrentNum = (TextView)layout.findViewById(R.id.txtTip_correct);
			listItemView.mTextMissNum = (TextView)layout.findViewById(R.id.txtTip_miss);

			layout.setTag(listItemView);
		}else{
			listItemView = (ListItemView) layout.getTag();
		}

		String key = listWap.get(position);
		setInfoEx rfidInfoEx = list.get(key);
		/*if(map.strEPC.equals("30000002") || map.strEPC.equals("300833B2DDD9014000000000")){
			System.out.println("遇到有问题的标签");
		}*/

		listItemView.mTextInfo.setText(rfidInfoEx.getCode());
		listItemView.mTextCorrentNum.setText(String.valueOf(rfidInfoEx.getCorrentRfids().size()));
		listItemView.mTextMissNum.setText(String.valueOf(rfidInfoEx.getRfids().size()));
		listItemView.mImgEpc.setImageResource(R.drawable.ic_home_black_24dp);

		if (clickTemp == position) {
			layout.setBackgroundResource(R.drawable.grid_view_backgroud);
		} else {
			layout.setBackgroundResource(R.drawable.view_boder);
		}

		return layout;
	}


	public static class setInfoEx implements Serializable {

		private List<String> correntRfids = new ArrayList<String>();
		private List<String> rfids = new ArrayList<String>();
		private String Code;
		private String ImgPath;

		public List<String> getRfids() {
			return rfids;
		}

		public void setRfids(List<String> rfids) {
			this.rfids = rfids;
		}

		public String getCode() {
			return Code;
		}

		public void setCode(String code) {
			Code = code;
		}

		public String getImgPath() {
			return ImgPath;
		}

		public void setImgPath(String imgPath) {
			ImgPath = imgPath;
		}


		public List<String> getCorrentRfids() {
			return correntRfids;
		}

		public void setCorrentRfids(List<String> correntRfids) {
			this.correntRfids = correntRfids;
		}

		@Override
		public String toString() {
			return "setInfoEx{" +
					"correntRfids=" + correntRfids +
					", rfids=" + rfids +
					", Code='" + Code + '\'' +
					", ImgPath='" + ImgPath + '\'' +
					'}';
		}
	}
}
