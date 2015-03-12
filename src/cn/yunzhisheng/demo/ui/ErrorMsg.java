package cn.yunzhisheng.demo.ui;

import cn.yunzhisheng.asrfix.USCRatingError;

public class ErrorMsg {
	public static String getMsg(int error)
	{
		switch(error)
		{
		case USCRatingError.RESULT_BUILD_ERROR:
			return "建立运行环境失败";
		case USCRatingError.RESULT_INIT_ERROR:
			return "初始化失败";
		case USCRatingError.RESULT_LIB_OUT_OF_DATE:
			return "口语评测库过期";
		case USCRatingError.RESULT_RECORD_ERROR:
			return "录音初始化失败";
		case USCRatingError.RESULT_TEXT_TOO_LONG:
			return "文本太长";
		case USCRatingError.RESULT_TEXT_EMPTY:
			return "文本为空";
		case USCRatingError.RESULT_DICT_ERROR:
			return "字典中找不到文本";
		case USCRatingError.RESULT_VOICE_TOO_SHORT:
			return "语音过短";
		case USCRatingError.RESULT_VOLUME_ABNORMAL:
			return "音量过大或过小";
		case USCRatingError.RESULT_NOISE_TOO_LARGE:
			return "噪音过大";
		case USCRatingError.RESULT_READ_WRONG_TEXT:
			return "未按文本朗读";
		case USCRatingError.RESULT_RATE_ERROR:
			return "评分失败";
		default:
			return "";
		}
	}
}
