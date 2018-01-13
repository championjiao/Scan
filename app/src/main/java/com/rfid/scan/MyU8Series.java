package com.rfid.scan;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.rfid.scan.series.model.IResponseHandler;
import com.rfid.scan.series.model.Message;
import com.rfid.scan.series.model.ResponseHandler;
import com.rfid.scan.series.operation.U8Series;
import com.rfid.scan.series.reader.model.InventoryBuffer;
import com.rfid.scan.series.reader.server.ReaderHelper;

import cn.fuen.xmldemo.activity.SetAndSaveActivity;

public class MyU8Series {
	private static final String TAG = "MyU8Series";
	private static Context mContext;

	private U8Series mUSeries;
	private ReaderHelper mReaderHelper;
	private static InventoryBuffer m_curInventoryBuffer;
	public static boolean isInventory;

	//单例
	private static MyU8Series mInstance;
	private MyU8Series() {

	}
	public static MyU8Series getInstance() {
		if (mInstance == null) {
			mInstance = new MyU8Series();
			return mInstance;
		}
		return mInstance;
	}

	public boolean initFist(Context context){
		mContext = context;
		mUSeries = U8Series.getInstance();
		U8Series.setContext(context);

		Message openSerialPortMeaasge = mUSeries.openSerialPort("U8");
		//打开失败 跳转到配置界面
		if (openSerialPortMeaasge.getCode() != 0) {
			//jumpToConfigurationTool();
			return false;
		}

		try {
			mReaderHelper = ReaderHelper.getDefaultHelper();
			m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void startInventory(final IResponseHandler responseHandler) {
		isInventory = true;
		mUSeries.startInventory(new ResponseHandler() {
			@Override
			public void onFailure(String msg) {
				super.onFailure(msg);
				responseHandler.onFailure(msg);
			}

			@Override
			public void onSuccess(String msg, Object data, byte[] parameters) {
				super.onSuccess(msg, data, parameters);
				if(!isInventory())
					return;
				responseHandler.onSuccess(msg, data, parameters);
			}
		});
	}

	public int getInventorySize(){
		return m_curInventoryBuffer.lsTagList.size();
	}
	public int getInventoryTotal(){
		return mReaderHelper.getInventoryTotal();
	}
	public int getReadRate(){
		return m_curInventoryBuffer.nReadRate;
	}

	public void stopInventory() {
		isInventory = false;
		try {
			mUSeries.stopInventory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isInventory(){
		return isInventory;
	}
	/**
	 * 模块下电，关闭串口
	 */
	private void EndWork() {
		mUSeries.closeSerialPort();
		mUSeries.modulePowerOff("U8");
	}

	public void onPause() {
		if (isInventory)
			stopInventory();
	}
	public void onStop() {
		if (isInventory) {
			stopInventory();
			mUSeries.modulePowerOff("U8");
		}
	}
	/**
	 * 模块上电
	 */
	public void onResume() {
		Message powerOnMessage = mUSeries.modulePowerOn("U8");
	};

	public void onDestroy() {
		EndWork();
	}

	/**
	 * 若配置文件丟失,跳转到配置工具
	 */
	private void jumpToConfigurationTool() {
//		Intent intent = new Intent();
//		intent.putExtra("modelName", "U8");
//		intent.putExtra("packageName", packageName);
//		intent.putExtra("activityName", activityName);
//		intent.setClass(this, SetAndSaveActivity.class);
//		startActivity(intent);
//		isJumpExit = true;
//		finish();
	}
}
