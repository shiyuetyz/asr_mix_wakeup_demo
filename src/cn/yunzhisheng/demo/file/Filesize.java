package cn.yunzhisheng.demo.file;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

public class Filesize {
	private Context mContext;
	public Filesize(Context context){
		this.mContext = context;
	}
	
	/**
	 * 获得SD卡总大小
	 * 
	 * @return
	 */
	public  long getSDTotalSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
	    long blockCount = stat.getBlockCount();
	    long availCount = stat.getAvailableBlocks();
		return blockSize*blockCount;
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return
	 */
	public static long getSDAvailableSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		
		long blockSize = stat.getBlockSize();
	    long blockCount = stat.getBlockCount();
	    long availCount = stat.getAvailableBlocks();
		return availCount*blockSize/1024/1024;
	}

	/**
	 * 获得机身内存总大小
	 * 
	 * @return
	 */
	public long getRomTotalSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
	    long blockCount = stat.getBlockCount();
	    long availCount = stat.getAvailableBlocks();
		return blockSize*blockCount;
	}

	/**
	 * 获得机身可用内存
	 * 
	 * @return
	 */
	public static long getRomAvailableSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
	    long blockCount = stat.getBlockCount();
	    long availCount = stat.getAvailableBlocks();
		return availCount*blockSize/1024/1024;
	}


}
