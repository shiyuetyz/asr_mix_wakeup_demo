package cn.yunzhisheng.demo.update;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import cn.yunzhisheng.common.JsonTool;
import cn.yunzhisheng.common.download.DownloadCenter;
import cn.yunzhisheng.common.download.IDownloadListener;
import cn.yunzhisheng.common.update.UpdateCenter;
import cn.yunzhisheng.common.util.ErrorUtil;
import cn.yunzhisheng.common.util.LogUtil;
import cn.yunzhisheng.preference.PrivatePreference;

public class UpdateDoing {
	public static final String TAG = "UpdateDoing";

	private String mUpdateUrl = "";
	private UpdateCenter mCommonUpdateCenter = null;
	private DownloadCenter mCommonDownloadCenter = null;
	private IUpdateListener mUpdateListener = null;
	private Context mContext;
	public static final int UPDATE_SERVER_PROTOCAL_ERROR = -101007;
	public static final int UPDATE_ERROR_NO_SERVER_FILE_URL = -101008;

	
	public static final String OPERATE_UPDATE = "OPERATE_UPDATE";
	
	
	public UpdateDoing(Context context) {
		mContext = context;
	}

	private cn.yunzhisheng.common.update.IUpdateListener mCommonUpdateListener = new cn.yunzhisheng.common.update.IUpdateListener() {

		@Override
		public void onUpdateStart() {//开始更新
			LogUtil.d(TAG, "onUpdateStart " + mUpdateListener);
			if (mUpdateListener != null) {
				mUpdateListener.onUpdateStart();
			}
		}

		@Override
		public void onResult(String arg0) {//得到的结果
			LogUtil.d(TAG, "onResult:" + arg0);
			JSONObject obj = JsonTool.parseToJSONObject(arg0);
			if (obj != null) {
				int rc = JsonTool.getJsonValue(obj, "rc", -1);
				if (rc == 0) {
					String version = JsonTool.getJsonValue(obj, "version", "");
					String change = JsonTool.getJsonValue(obj, "change", "");
					mUpdateUrl = JsonTool.getJsonValue(obj, "url", "");

					if (mUpdateListener != null) {
						mUpdateListener.onUpdateResult(version, change);
					}
				} else {
					String message = JsonTool.getJsonValue(obj, "message", "");
					if (mUpdateListener != null) {
						mUpdateListener.onError(new ErrorUtil(rc, message));
					}
				}
			} else {
				if (mUpdateListener != null) {
					mUpdateListener
							.onError(getErrorUtil(UPDATE_SERVER_PROTOCAL_ERROR));
				}
			}
		}

		@Override
		public void onError(ErrorUtil arg0) {
			LogUtil.d(TAG, "onError:" + arg0.code + ";" + arg0.message);
			if (mUpdateListener != null) {
				mUpdateListener.onError(arg0);
			}
		}
	};

	private IDownloadListener mCommonDownloadListener = new IDownloadListener() {//下载的监听

		@Override
		public void onProgress(long arg0, long arg1) {//下载进度
			LogUtil.d(TAG, "onProgress:" + arg0 + ";" + arg1);
			if (mUpdateListener != null) {
				mUpdateListener.onDownloadProgress(arg0, arg1);
			}
		}

		@Override
		public void onError(ErrorUtil arg0) {//下载错误
			LogUtil.d(TAG, "onError:" + arg0.code + ";" + arg0.message);
			if (mUpdateListener != null) {
				mUpdateListener.onError(arg0);
			}
		}

		@Override
		public void onDownloadStart() {//开始下载
			LogUtil.d(TAG, "onDownloadStart");
			if (mUpdateListener != null) {
				mUpdateListener.onDownloadStart();
			}
		}

		@Override
		public void onDownloadComplete(String arg0) {//下载完成时回调
			LogUtil.d(TAG, "onDownloadComplete:" + arg0);
			if (mCommonDownloadCenter != null) {
				mCommonDownloadCenter.setUrl("");
			}
			if (mUpdateListener != null) {
				mUpdateListener.onDownloadComplete(arg0);
			}
			mUpdateUrl = "";
		}
	};


	public void initUpdate() {//初始化更新
		LogUtil.d(TAG, "initUpdate");
		if (VUIPreference.UPDATE_SWITCH_ON.equals(VUIPreference.UPDATE_SWITCH)) {
			mCommonUpdateCenter = new UpdateCenter();
			mCommonUpdateCenter.setListener(mCommonUpdateListener);
			if (VUIPreference.UPDATE_SERVER_DOMAIN != null) {
				mCommonUpdateCenter.setDomain(PrivatePreference
						.DeEncrypt(VUIPreference.UPDATE_SERVER_DOMAIN));
			}
			
			mCommonUpdateCenter.setVendor("vui_demo");//设置vender
			mCommonUpdateCenter.setType(VUIPreference.PROJECT_TYPE);
			
			mCommonUpdateCenter.setPackage(PrivatePreference.PACKAGE);//package
			mCommonUpdateCenter.setVersion(PrivatePreference.VERSION);//设置version
			mCommonUpdateCenter.setUdid(PrivatePreference.IMEI);
			mCommonDownloadCenter = new DownloadCenter();
			mCommonDownloadCenter.setListener(mCommonDownloadListener);
			VUIPreference.UPDATE_FOLDER = PrivatePreference.transPath(VUIPreference.UPDATE_FOLDER);
			mCommonDownloadCenter.setFilePath(VUIPreference.UPDATE_FOLDER + VUIPreference.UPDATE_FILE_NAME);
		}
	}

	private IUpdateOperate mUpdateOperate = new IUpdateOperate() {//更新操作的回调

		@Override
		public void update() {
			if (mCommonUpdateCenter != null) {
				LogUtil.d(TAG, "Operate Start...");
				mCommonUpdateCenter.update();
			}
		}

		@Override
		public void setUpdateListener(Object l) {//设置安装的监听
			mUpdateListener = (IUpdateListener) l;
		}

		@Override
		public void install() {//安装
			installUpdateApk();
		}

		@Override
		public void download() {//下载
			if (mUpdateUrl == null || mUpdateUrl.equals("")) {
				if (mUpdateListener != null) {
					mUpdateListener
							.onError(getErrorUtil(UPDATE_ERROR_NO_SERVER_FILE_URL));
				}
				return;
			}
			LogUtil.d(TAG, "download:" + mUpdateUrl);
			if (mCommonDownloadCenter != null) {
				mCommonDownloadCenter.setUrl(mUpdateUrl);
				mCommonDownloadCenter.download();
			}
		}

		@Override
		public void cancel() {
			mUpdateUrl = "";
			if (mCommonUpdateCenter != null) {
				mCommonUpdateCenter.cancel();
			}
			if (mCommonDownloadCenter != null) {
				mCommonDownloadCenter.cancel();
			}
			if (mUpdateListener != null) {
				mUpdateListener.onCancel();
			}
		}

		@Override
		public String getUpdateUrl() {//得到url（下载的链接）
			return mUpdateUrl;
		}
	};

	private void installUpdateApk() {//安装更新apk
		if (mContext != null) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(
					Uri.parse("file://" + VUIPreference.UPDATE_FOLDER
							+ VUIPreference.UPDATE_FILE_NAME),
					"application/vnd.android.package-archive");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(i);
		}
	}

	private ErrorUtil getErrorUtil(int code) {
		switch (code) {

		case UPDATE_SERVER_PROTOCAL_ERROR:
			return new ErrorUtil(UPDATE_SERVER_PROTOCAL_ERROR, "自动更新服务反馈异常");
		case UPDATE_ERROR_NO_SERVER_FILE_URL:
			return new ErrorUtil(UPDATE_ERROR_NO_SERVER_FILE_URL,
					"没有指定更新文件，请先检测更新");
		default:
			return new ErrorUtil(code, "未知错误");
		}
	}
	
	public Object getOperate(String tag) {
		if (OPERATE_UPDATE.equals(tag)) {
			if (VUIPreference.UPDATE_SWITCH_ON.equals(VUIPreference.UPDATE_SWITCH)) {
				return mUpdateOperate;
			}
		} 
		return null;
	}

}
