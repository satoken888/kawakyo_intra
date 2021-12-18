package jp.co.kawakyo.kawakyo_intra.model.logic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.kawakyo.kawakyo_intra.repository.UDNTHARepository;
import jp.co.kawakyo.kawakyo_intra.repository.JDNTHARepository;
import jp.co.kawakyo.kawakyo_intra.utils.ConvertUtils;

@Service
public class EarningsCalculate {

	@Autowired
	JDNTHARepository jdnthaRepository;
	@Autowired
	UDNTHARepository udnthaRepository;

	/**
	 * 本日の売上金額取得
	 * @return プログラム実施当日の売上金額（受注計上データより取得）
	 */
	public Long calculateTodayEarnings() {
		return calculateOneDayEarnings(new Date());
	}

	/**
	 * 単日の売上金額取得
	 * @param date
	 * @return 単日の売上金額（受注計上データより取得）
	 */
	public Long calculateOneDayEarnings(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Long earnings = jdnthaRepository.findOneDaySbauodkn(dateFormat.format(date));
		return (Objects.isNull(earnings)) ? 0L : earnings;
	}

	/**
	 * 指定の日から数日間さかのぼった売上情報を取得
	 * @param endDate
	 * @param days
	 * @return
	 */
	public Map<String, Long> getSomeDayEarnings(Date endDate,int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.add(Calendar.DATE, (days-1)*(-1));
		return getSomeDayEarnings(calendar.getTime(), endDate);
	}

	/**
	 * 複数日の売上情報の取得
	 * @param startDate
	 * @param endDate
	 * @return keyに売上日付、valueに売上数値（売上計上のテーブルより取得）のマップ
	 */
	public Map<String, Long> getSomeDayEarnings(Date startDate, Date endDate) {
		Map<String, Long> someDayEarnings = new LinkedHashMap<String,Long>();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

		/* startDateからendDateまでの売上情報をDBから取得 */
		List<Object> earningsData = udnthaRepository.findDaysEarnings(dateFormat.format(startDate), dateFormat.format(endDate));

		Iterator<Object> itr = earningsData.iterator();
		while(itr.hasNext()) {
			Object[] obj = (Object[])itr.next();
			someDayEarnings.put(String.valueOf(obj[0]),Long.valueOf(String.valueOf(obj[1])));
		}
		return someDayEarnings;
	}

	/**
	 * 定休日の日に０の売上を付与する
	 * @param earnings
	 * @return
	 */
	public Map<String, Long> addHolidayEarnings(int searchYear,int searchMonth,  Map<String,Long> earnings) {

		Calendar cal = Calendar.getInstance();
		//今月の初日を設定
		cal.setTime(new Date());
		cal.set(Calendar.DAY_OF_MONTH,1);

		String year = String.valueOf(searchYear);
		String month = searchMonth + 1 < 10 ? "0" + String.valueOf(searchMonth+1) : String.valueOf(searchMonth+1);
//		String day = cal.get(Calendar.DATE) < 10 ? "0" + String.valueOf(cal.get(Calendar.DATE)) : String.valueOf(cal.get(Calendar.DATE));

		cal.setTime(ConvertUtils.covDate(new Date(), false));
		int lastDay = cal.get(Calendar.DATE);

		for(int i = 1; i <= lastDay;i++) {
			String day = i < 10 ? "0" + String.valueOf(i) :String.valueOf(i);
			String compareDateStr = year + month + day;

			if(!earnings.containsKey(compareDateStr)) {
				earnings.put(compareDateStr, 0L);
			}
		}


		Map<String, Long> sortedEarnings = new TreeMap<String, Long>(earnings);

		return sortedEarnings;
	}

	public Long getTotalEarnings(Map<String, Long> earnings) {
		Long totalEarnings = 0L;

		for(Long oneDayEarnings : earnings.values()) {
			totalEarnings += oneDayEarnings;
		}

		return totalEarnings;
	}

}
