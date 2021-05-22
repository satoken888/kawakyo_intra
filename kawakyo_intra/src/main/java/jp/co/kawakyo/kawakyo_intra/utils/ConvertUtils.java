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
}
