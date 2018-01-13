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
import com.rfid.scan.service.BoxDataUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolsAdapter extends BaseAdapter{

	private LayoutInflater layoutInflater;
	private Context mContext;

	private List<ToolInfo> list = new ArrayList<ToolInfo>();

	public ToolsAdapter(Context context, List<ToolInfo> list){
		this.mContext = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.list = list;
	}
	
	public final class ListItemView{                //自定义控件集合     
		public TextView mTextCode;
		public ImageView mImgEpc;
		public TextView mTextCodeP;
        public TextView mTextCount;
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
			layout = layoutInflater.inflate(R.layout.tool_item, null);
			listItemView.mTextCode = (TextView)layout.findViewById(R.id.txtTip_code);
			listItemView.mImgEpc = (ImageView)layout.findViewById(R.id.img_epc);
			listItemView.mTextCodeP = (TextView)layout.findViewById(R.id.txtTip_codeP);
            listItemView.mTextCount = (TextView)layout.findViewById(R.id.txtTip_count);

			layout.setTag(listItemView);
		}else{
			listItemView = (ListItemView) layout.getTag();
		}

		ToolInfo info = list.get(position);
		/*if(map.strEPC.equals("30000002") || map.strEPC.equals("300833B2DDD9014000000000")){
			System.out.println("遇到有问题的标签");
		}*/

		listItemView.mTextCode.setText(info.getCode());
		listItemView.mTextCodeP.setText(info.getCodeP());
        listItemView.mTextCount.setText(String.valueOf(info.getRfids().size()));

		String filePath = BoxDataUtil.getInstance().getDiskDir()+info.getImgPath();
		File file = new File(filePath);
		Glide.with(mContext).
				load(file).
				asBitmap().
				diskCacheStrategy(DiskCacheStrategy.RESULT).//保存最终图片
				into(listItemView.mImgEpc);

		return layout;
	}


	public static class ToolInfo{

		private List<String> rfids = new ArrayList<String>();
		private String Code;
		private String CodeP;
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

		public String getCodeP() {
			return CodeP;
		}

		public void setCodeP(String codeP) {
			CodeP = codeP;
		}

		public String getImgPath() {
			return ImgPath;
		}

		public void setImgPath(String imgPath) {
			ImgPath = imgPath;
		}
	}
}
