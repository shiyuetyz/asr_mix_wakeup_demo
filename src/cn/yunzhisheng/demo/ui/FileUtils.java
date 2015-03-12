package cn.yunzhisheng.demo.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
	
	public static void savePcm(byte[] buffer, String path) {

		File file = new File(path);
		File dir = file.getParentFile();
		if(!dir.exists())
			dir.mkdirs();
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(buffer);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
