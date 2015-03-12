package cn.yunzhisheng.demo.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class VersionForServer {

	public VersionForServer(){
		
	}
	
	public static String getVersionForserver(String serverPath) throws ClientProtocolException, IOException{
		StringBuilder versionJson = new StringBuilder();
		
		HttpClient client = new DefaultHttpClient();//建立HTTP客户端
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000);//设置连接超时时间
		HttpConnectionParams.setSoTimeout(params, 5000);//设置读取的超时时间
		//serverPath是version.json的路径
		HttpResponse response = client.execute(new HttpGet(serverPath));
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"),8192);
			String line =null;
			
			while ((line = reader.readLine()) != null) {
				versionJson.append(line + "\n");//按行读取数据追加到stringbuilder中
				
			}
			reader.close();
		}

		return versionJson.toString();
	}
}
