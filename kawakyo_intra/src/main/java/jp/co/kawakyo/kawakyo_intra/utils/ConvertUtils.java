package jp.co.kawakyo.kawakyo_intra.utils;

import java.util.Calendar;
import java.util.Date;

public class ConvertUtils {

	/**
     * 月初/月末日付を取得する
     * @param nengetsu
     * @param firstdayFlg
     * @return cal.getTime()
     */
	public static Date covDate(Date nengetsu, boolean firstdayFlg) {
        Calendar cal = Calendar.getInstance();

        //年月をセットする
        cal.setTime(nengetsu);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date = 1;

        cal.set(year, month, date, 0, 0, 0);

        //終了年月日を取得する
        if(!firstdayFlg){
            //月末日を取得する
            date = cal.getActualMaximum(Calendar.DATE);
            cal.set(year, month, date, 0, 0, 0);

        }

        return cal.getTime();
    }
	
	/**
	 * YYYYMMDD形式の文字列を取得する
	 * @param cal 変換したい日付情報をもったCalendarクラスパラメータ
	 * @return YYYYMMDD形式の文字列
	 */
	public static String getDateStrYYYYMMDD(Calendar cal) {
		String rtnStr = String.valueOf(cal.get(Calendar.YEAR))
				+ String.valueOf(cal.get(Calendar.MONTH) + 1 < 10 ? "0" + (cal.get(Calendar.MONTH) + 1) : cal.get(Calendar.MONTH) + 1)
				+ String.valueOf(cal.get(Calendar.DATE) < 10 ? "0" + cal.get(Calendar.DATE) : cal.get(Calendar.DATE));
		return rtnStr;
	}
}
