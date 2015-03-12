package cn.yunzhisheng.demo.ui;



public class Syllable {///音节划分
	
	public final static String TAG = "Syllable";
	
	public String mText;
	public int mScore;
	public long mBegin;
	public long mEnd;
	
	public Syllable(String text, int score, long begin, long end)
	{
		mText = text;
		mScore = score;
		mBegin = begin;
		mEnd = end;
		if(mScore < 6)
			mText = mText + mScore;
	}

}
