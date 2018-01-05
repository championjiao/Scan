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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SeriesAdapter extends BaseAdapter{

	private LayoutInflater layoutInflater;
	private Context context;

	private Map<String,setInfoEx> list = null;

	public SeriesAdapter(Context context, Map<String,setInfoEx> list){
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.list = list;
	}
	
	public final class ListItemView{                //自定义控件集合     
		public TextView mTextInfo;
		public ImageView mImgEpc;
		public TextView mTextCorrentNum;
		public TextView mTextMissNum;

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
			listItemView.mImgEpc = (ImageView)layout.findViewById(R.id.img_epc);
			listItemView.mTextCorrentNum = (TextView)layout.findViewById(R.id.txtTip_correct);
			listItemView.mTextMissNum = (TextView)layout.findViewById(R.id.txtTip_miss);


			layout.setTag(listItemView);
		}else{
			listItemView = (ListItemView) layout.getTag();
		}

		setInfoEx rfidInfoEx = list.get(position);
		/*if(map.strEPC.equals("30000002") || map.strEPC.equals("300833B2DDD9014000000000")){
			System.out.println("遇到有问题的标签");
		}*/

		listItemView.mTextInfo.setText(rfidInfoEx.getCode());
		listItemView.mTextCorrentNum.setText(rfidInfoEx.getCorrent());
		listItemView.mTextMissNum.setText(rfidInfoEx.getMissing());
		listItemView.mImgEpc.setImageResource(R.drawable.ic_home_black_24dp);
		return layout;
	}


	public static class setInfoEx implements Serializable {

		private List<String> correntRfids;
		private List<String> rfids;
		private String Code;
		private String ImgPath;
		private int Corrent;
		private int Missing;

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

		public int getCorrent() {
			return Corrent;
		}

		public void setCorrent(int corrent) {
			Corrent = corrent;
		}

		public int getMissing() {
			return Missing;
		}

		public void setMissing(int missing) {
			Missing = missing;
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
					", Corrent=" + Corrent +
					", Missing=" + Missing +
					'}';
		}
	}
}
