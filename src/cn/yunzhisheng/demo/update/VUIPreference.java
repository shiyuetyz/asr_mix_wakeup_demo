/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : USCPlatformPreference.java
 * @ProjectName : vui
 * @PakageName : cn.yunzhisheng.preference
 * @Author : Dancindream
 * @CreateDate : 2013-7-16
 */
package cn.yunzhisheng.demo.update;

import cn.yunzhisheng.preference.IUpdatePreferenceListener;
import cn.yunzhisheng.preference.PrivatePreference;

/**
 * @Module : preference
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-7-16
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-7-16
 * @Modified:
 * 2013-7-16: 实现基本功能
 */
public class VUIPreference {

	



	public static final String UPDATE_SWITCH_ON = "ON";
	public static final String UPDATE_SWITCH_OFF = "OFF";
	public static String UPDATE_SWITCH = UPDATE_SWITCH_ON;
	public static String UPDATE_FOLDER = "%sdcard%/YunZhiShengdemo/vui/update/";
	public static final String UPDATE_FILE_NAME = "YunZhiShengdemo.apk";
	public static int[] UPDATE_SERVER_DOMAIN = null;

	public static final String NET_DATA_UPLOAD_ON = "ON";
	public static final String NET_DATA_UPLOAD_OFF = "OFF";
	public static String NET_DATA_UPLOAD = NET_DATA_UPLOAD_ON;
	public static int NET_DATA_ITEM_SIZE = 1000;


	public static String PROJECT_VENDOR = "";
	public static String PROJECT_TYPE = "";

	
	private static void initPreference() {
		
		PROJECT_VENDOR = PrivatePreference.getValue("project_vendor", PROJECT_VENDOR);
		PROJECT_TYPE = PrivatePreference.getValue("project_type", PROJECT_TYPE);
		
		NET_DATA_UPLOAD = PrivatePreference.getValue("net_data_upload", NET_DATA_UPLOAD_ON);
		UPDATE_SWITCH = PrivatePreference.getValue("update_switch", UPDATE_SWITCH);
		UPDATE_FOLDER = PrivatePreference.getValue("update_folder", UPDATE_FOLDER);
		UPDATE_FOLDER = PrivatePreference.transPath(UPDATE_FOLDER);

		String str = PrivatePreference.getValue("update_server_domain", "");
		if (str != null && !str.equals("")) {
			UPDATE_SERVER_DOMAIN = PrivatePreference.str2IntArray(str);
		}
		

	}

	public static void init() {
		initPreference();
		PrivatePreference.addUpdateListener(new IUpdatePreferenceListener() {
			@Override
			public void onUpdate() {
				initPreference();
			}
		});
	}

	
	
	
}
