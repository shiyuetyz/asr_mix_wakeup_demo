package cn.yunzhisheng.demo.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlayPcmThread extends Thread {///pcm 脉冲编码调制
	public static final String TAG = "PlayPcmThread";

	public PlayPcmThread(String path)
	{
		mPath = path;
	}

	public void setPlayerListener(IPlayListener listener) {
		mPlayerListener = listener;
	}
	
	public void setDuration(long offset, long end)
	{
		mOffset = offset;
		mLen = (int)(end - offset);
	}
	
	private long mOffset = 0;
	private int mLen = -1;
	
	public void reqStop()
	{
		mRun = false;
	}
	
	private String mPath;///文件路径
	private IPlayListener mPlayerListener;

	private boolean mRun;
	
	@Override
	public void run() {
		mRun = true;
		int minBufferSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);///得到最小的缓存
		AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize,
				AudioTrack.MODE_STREAM);///得到声音轨道
		InputStream is = null;///输入流
		try {
			if (track.getState() == AudioTrack.STATE_INITIALIZED) {
				track.play();
				byte[] buffer = new byte[minBufferSize];
				is = new FileInputStream(mPath);///从文件中的数据读取到输入流
				while(mOffset > 0)
				{
					long skipcount = is.skip(mOffset);///计算遗漏的字节
					mOffset -= skipcount;
				}
				
				while(mRun)
				{
					int count = is.read(buffer);
					if(count == -1 || mLen == 0)
						break;
					if(mLen != -1)
					{
						if(count <= mLen)
						{
							mLen -= count;
						}
						else
						{
							count = mLen;
							mLen = 0;
						}
					}
					track.write(buffer, 0, count);
				}
				//for galaxy note2
				if(mLen != -1 && mLen < 20 * 320)
				{
					byte[] zero = new byte[6400 - mLen];
					track.write(zero, 0, zero.length);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (track != null) {
				track.stop();
				track.release();
				track = null;
			}
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(mPlayerListener != null)
				mPlayerListener.onPlayEnd();
		}
	}
}
