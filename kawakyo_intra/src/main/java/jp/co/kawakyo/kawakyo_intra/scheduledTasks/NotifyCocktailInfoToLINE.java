package jp.co.kawakyo.kawakyo_intra.scheduledTasks;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kintone.client.KintoneClient;
import com.kintone.client.KintoneClientBuilder;
import com.kintone.client.model.record.NumberFieldValue;
import com.kintone.client.model.record.Record;

import jp.co.kawakyo.kawakyo_intra.controller.SearchNeedNoodles;
import jp.co.kawakyo.kawakyo_intra.model.logic.EarningsCalculate;
import jp.co.kawakyo.kawakyo_intra.utils.ConvertUtils;
import jp.co.kawakyo.kawakyo_intra.utils.KintoneConstants;
import jp.co.kawakyo.kawakyo_intra.utils.LineNotify;


@Component
public class NotifyCocktailInfoToLINE {

    Logger logger = LoggerFactory.getLogger(NotifyCocktailInfoToLINE.class);

    @Autowired
	EarningsCalculate earningsCalculate;
    @Autowired
    SearchNeedNoodles searchNeedNoodles;

    @Scheduled(cron="0 0 19  * * ? ")   //毎日19時に実施
    public void execute(){
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

        //今年、前年の同日までの売上合計金額を算出
        //その後前年同日比を算出する。
        BigDecimal earningsToToday = new BigDecimal(0);
        BigDecimal earningsToLastYearToday = new BigDecimal(0);
        ArrayList<Long> monthEarningsList = new ArrayList<Long>(monthEarnings.values());
        ArrayList<Long> lastYearMonthEarningsList = new ArrayList<Long>(lastYearMonthEarnings.values());
        DecimalFormat dNum = new DecimalFormat("##0.00%");
        for(int i = 0; i < cal.get(Calendar.DAY_OF_MONTH); i++) {
            earningsToToday = earningsToToday.add(new BigDecimal(monthEarningsList.get(i)));
            earningsToLastYearToday = earningsToLastYearToday.add(new BigDecimal(lastYearMonthEarningsList.get(i)));
        }


        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Map<String, Integer> shippingItemList = searchNeedNoodles.getShippingItemList(format.format(now), format.format(now));
        Integer todayNoodles = getTodaysNoodleCount(shippingItemList);

        String message = "\n本日の売上金額：" + String.format("%,d",monthEarnings.get(today)) + "\n\n" +
                        "今月の累計売上金額：" + String.format("%,d",cumulativeSales) +  "\n" +
                        "前年同日比：" + dNum.format(earningsToToday.divide(earningsToLastYearToday, 6,BigDecimal.ROUND_HALF_UP)) +  "\n\n" +
                        "昨年の同月売上合計金額：" + String.format("%,d",lastYearMonthEarnings.values().stream().mapToLong(l -> l).sum()) + "\n\n" +
                        "みなさん、本日もお疲れ様でした！\n" +
                        "==========================\n" + 
                        "本日の出荷麺数：" + String.format("%,d", todayNoodles) + "個です\n" +
                        "★冬場の餅問題対策のため実施↓↓↓\n"+
                        "本日の黄箱5食出荷数：" + String.format("%,d",shippingItemList.get("00007600")) + "個です";

        
        //営業部LINEへ通知
		LineNotify.notify(message, "XGNXPyyhlUKgItjPt93tVQvX8WTcFnmfkWMKkuZPMgk");

        //kintoneに出荷麺数情報を送付
        sendTodayNoodlesInfoToKintone(shippingItemList.get("00020890"),
                                        shippingItemList.get("00020888"),
                                        shippingItemList.get("00007110"),
                                        shippingItemList.get("00020876"),
                                        shippingItemList.get("00020882"),
                                        shippingItemList.get("00020884"),
                                        shippingItemList.get("00020891"),
                                        shippingItemList.get("00020991"),
                                        shippingItemList.get("10001013"));
    }

    /**
     * 麺の数をkintoneへ送信する
     * @param HT_120
     * @param HT_130
     * @param yomogi
     * @param gokubuto_120
     * @param kirin_150
     * @param hasegawa
     * @param HT_120_hoso
     * @param hannama
     * @param takasui_130
     */
    private void sendTodayNoodlesInfoToKintone(Integer HT_120, Integer HT_130, Integer yomogi, Integer gokubuto_120,
            Integer kirin_150, Integer hasegawa, Integer HT_120_hoso, Integer hannama, Integer takasui_130) {

                //nullチェック
                if(HT_120 == null) HT_120 = 0;
                if(HT_130 == null) HT_130 = 0;
                if(yomogi == null) yomogi = 0;
                if(gokubuto_120 == null) gokubuto_120 = 0;
                if(kirin_150 == null) kirin_150 = 0;
                if(hasegawa == null) hasegawa = 0;
                if(HT_120_hoso == null) HT_120_hoso = 0;
                if(hannama == null) hannama = 0;
                if(takasui_130 == null) takasui_130 = 0;

                try(KintoneClient client = KintoneClientBuilder.create(KintoneConstants.KINTONE_URL).authByApiToken(KintoneConstants.KINTONE_NOODLECOUNT_API_TOKEN).build()) {
                    Record todayNoodleRecord = new Record();
                    todayNoodleRecord.putField("HT_120",new NumberFieldValue(HT_120));
                    todayNoodleRecord.putField("HT_130", new NumberFieldValue(HT_130));
                    todayNoodleRecord.putField("yomogi",new NumberFieldValue(yomogi));
                    todayNoodleRecord.putField("gokubuto_120",new NumberFieldValue(gokubuto_120));
                    todayNoodleRecord.putField("kirin_150",new NumberFieldValue(kirin_150));
                    todayNoodleRecord.putField("hasegawa",new NumberFieldValue(hasegawa));
                    todayNoodleRecord.putField("HT_120_hoso",new NumberFieldValue(HT_120_hoso));
                    todayNoodleRecord.putField("hannama",new NumberFieldValue(hannama));
                    todayNoodleRecord.putField("takasui_130",new NumberFieldValue(takasui_130));
                    client.record().addRecord(KintoneConstants.KINTONE_NOODLECOUNT_APP_CODE, todayNoodleRecord);
                } catch(IOException e ) {
                    e.printStackTrace();
                }

    }

    /**
     * カクテルから取得したアイテムリストから麺の情報のみ抽出する
     * @param itemList
     * @return
     */
    private Integer getTodaysNoodleCount(Map<String, Integer> itemList) {
        Integer countNoodles = 0;

        //できた商品リストから麺の情報をピックアップ
        Integer HT_120 = itemList.get("00020890") == null ? 0 : itemList.get("00020890");
        Integer HT_130 = itemList.get("00020888") == null ? 0 : itemList.get("00020888");
        Integer yomogi = itemList.get("00007110") == null ? 0 : itemList.get("00007110");
        Integer gokubuto_120 = itemList.get("00020876") == null ? 0 : itemList.get("00020876");
        Integer kirin_150 = itemList.get("00020882") == null ? 0 : itemList.get("00020882");
        Integer hasegawa = itemList.get("00020884") == null ? 0 : itemList.get("00020884");
        Integer HT_120_hoso = itemList.get("00020891") == null ? 0 : itemList.get("00020891");
        Integer hannama = itemList.get("00020991") == null ? 0 : itemList.get("00020991");
        Integer takasui_130 = itemList.get("10001013") == null ? 0 : itemList.get("10001013");

        countNoodles += HT_120;
        countNoodles += HT_130;
        countNoodles += yomogi;
        countNoodles += gokubuto_120;
        countNoodles += kirin_150;
        countNoodles += hasegawa; 
        countNoodles += HT_120_hoso;
        countNoodles += hannama;
        countNoodles += takasui_130;

        return countNoodles;
    } 
}
