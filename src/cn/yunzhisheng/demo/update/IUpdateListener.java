/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName	: IUpdateListener.java
 * @ProjectName	: vui_sdk
 * @PakageName	: cn.yunzhisheng.vui.update
 * @Author		: Dancindream
 * @CreateDate	: 2014-2-25
 */
package cn.yunzhisheng.demo.update;

import cn.yunzhisheng.common.util.ErrorUtil;

/**
 * @Module		: 隶属模块名
 * @Comments	: 描述
 * @Author		: Dancindream
 * @CreateDate	: 2014-2-25
 * @ModifiedBy	: Dancindream
 * @ModifiedDate: 2014-2-25
 * @Modified: 
 * 2014-2-25: 实现基本功能
 */
public interface IUpdateListener {
	public void onUpdateStart();
	public void onUpdateResult(String newVersion, String changeLog);
	public void onDownloadStart();
	public void onDownloadProgress(long total, long length);
	public void onDownloadComplete(String filepath);
	public void onCancel();
	public void onError(ErrorUtil error);
}
