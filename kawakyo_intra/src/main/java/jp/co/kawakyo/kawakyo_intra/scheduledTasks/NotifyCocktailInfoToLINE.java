package jp.co.kawakyo.kawakyo_intra.scheduledTasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jp.co.kawakyo.kawakyo_intra.model.logic.EarningsCalculate;
import jp.co.kawakyo.kawakyo_intra.utils.ConvertUtils;
import jp.co.kawakyo.kawakyo_intra.utils.LineNotify;


@Component
public class NotifyCocktailInfoToLINE {

    @Autowired
	EarningsCalculate earningsCalculate;

    @Scheduled(cron="0 0 19  * * ? ")   //每30秒ごと
    public void execute(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //フォーマット
        Calendar cal = Calendar.getInstance();
		Calendar lastYearCal = Calendar.getInstance();
		Date now = cal.getTime();
		String today = ConvertUtils.getDateStrYYYYMMDD(cal);

        //今月の日別売上金額の取得
		Map<String,Long> monthEarnings = earningsCalculate.getSomeDayEarnings(ConvertUtils.covDate(now, true) ,ConvertUtils.covDate(now, false));

		//休日に売上金0の情報を追加する。
		monthEarnings = earningsCalculate.addHolidayEarnings(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), monthEarnings);

		//当日までの累計売上を産出
		Long cumulativeSales = earningsCalculate.getTotalEarnings(monthEarnings);

        //昨年の同月売上データを取得する
        lastYearCal.set(Calendar.YEAR, cal.get(Calendar.YEAR)-1);
        Date lastYearDate = lastYearCal.getTime();
        Map<String, Long> lastYearMonthEarnings = earningsCalculate.getSomeDayEarnings(ConvertUtils.covDate(lastYearDate, true), ConvertUtils.covDate(lastYearDate, false));
        lastYearMonthEarnings = earningsCalculate.addHolidayEarnings(lastYearCal.get(Calendar.YEAR),lastYearCal.get(Calendar.MONTH), lastYearMonthEarnings);

        String message = "\n本日の売上金額：" + String.format("%,d",monthEarnings.get(today)) + "\n\n" +
                        "今月の累計売上金額：" + String.format("%,d",cumulativeSales) +  "\n\n" + 
                        "昨年の同月売上合計金額：" + String.format("%,d",lastYearMonthEarnings.values().stream().mapToLong(l -> l).sum())) + "\n\n" +
                        "みなさん、本日もお疲れ様でした！";
        
        //営業部LINEへ通知
		LineNotify.notify(message, "XGNXPyyhlUKgItjPt93tVQvX8WTcFnmfkWMKkuZPMgk");
    }
}
