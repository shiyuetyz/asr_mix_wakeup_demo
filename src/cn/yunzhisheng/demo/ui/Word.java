package cn.yunzhisheng.demo.ui;

import java.util.ArrayList;


public class Word {

	public ArrayList<Syllable> mSyllables;
	private int mVsize;
	
	public Word()
	{
		mSyllables = new ArrayList<Syllable>();
	}
	
	public void addSyllable(String text, int score, long begin, long end)
	{
		Syllable syllable = new Syllable(text, score, begin, end);
		mSyllables.add(syllable);
		mVsize += syllable.mText.length();
	}
	
	public int vsize()
	{
		return mVsize;
	}
}
