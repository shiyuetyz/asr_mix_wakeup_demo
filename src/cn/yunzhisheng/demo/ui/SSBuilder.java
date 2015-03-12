package cn.yunzhisheng.demo.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class SSBuilder {
	public final static String TAG = "ResultParser";
	
	public static SpannableString buildString(Word[] words)///spannableString表示这类文本是不变的
	{//文本着色，正确绿色（wordcount > 5），错误红色（wordcount <5），中间位橙色（wordcount = 5）
		int wordCount = words.length;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < wordCount; i++)
		{
			Word word = words[i];
			int size = word.mSyllables.size();
			for(int j = 0; j < size; j++)
			{
				Syllable s = words[i].mSyllables.get(j);
				sb.append(s.mText);
			}
			sb.append(" ");
		}
		
		SpannableString ss = new SpannableString(sb.toString());
		int offset = 0;
		for(int i = 0; i < wordCount; i++)
		{
			Word word = words[i];
			int size = word.mSyllables.size();
			for(int j = 0; j < size; j++)
			{
				Syllable s = words[i].mSyllables.get(j);
				double score = s.mScore;
				int len = s.mText.length();
				int extlen = score < 6 ? 1 : 0;
				int color = 0xFF006600;
				if(score < 4)
					color = Color.RED;
				else if(score < 6)
					color = 0xFFFF9900;
				ss.setSpan(new ForegroundColorSpan(color), offset, offset + len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				ss.setSpan(new StyleSpan(Typeface.BOLD), offset, offset + len - extlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				if(score < 6)
					ss.setSpan(new AbsoluteSizeSpan(12, true), offset + len - extlen, offset + len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				offset += len;
			}
			offset += 1;
		}
		
		return ss;
	}
	
	public static double makeRecScore(double score)
	{
		score = makeRecScoreV2(score * 10);
		if (score > 9.5)
			score = 9.5;
		if (score < 1.0)
			score = 1.0;
		return score;
	}
	
	private static double makeRecScoreV2(double score)
	{
		double fscore = 0;
		if(score > -150000)
		{
			fscore = 6 / (1 + Math.exp((125000 + score) / -20000)) + 5;
		}
		else
		{
			fscore = (score + 125000) / 10000 + 6;
		}
		return fscore;
	}
}
