/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName	: IUpdateOperate.java
 * @ProjectName	: vui_sdk
 * @PakageName	: cn.yunzhisheng.vui.update
 * @Author		: Dancindream
 * @CreateDate	: 2014-2-25
 */
package cn.yunzhisheng.demo.update;

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
public interface IUpdateOperate {
	public void setUpdateListener(Object l);
	public void update();
	public void download();
	public void install();
	public String getUpdateUrl();
	public void cancel();
}
