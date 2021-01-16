package jp.co.kawakyo.kawakyo_intra.model.logic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.kawakyo.kawakyo_intra.repository.EarningsRepository;
import jp.co.kawakyo.kawakyo_intra.repository.OrderRepository;

@Service
public class EarningsCalculate {

	@Autowired
	OrderRepository orderRepository;
	@Autowired
	EarningsRepository earningsRepository;

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
		Long earnings = orderRepository.findOneDaySbauodkn(dateFormat.format(date));
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
		List<Object> earningsData = earningsRepository.findDaysEarnings(dateFormat.format(startDate), dateFormat.format(endDate));

		Iterator<Object> itr = earningsData.iterator();
		while(itr.hasNext()) {
			Object[] obj = (Object[])itr.next();
			someDayEarnings.put(String.valueOf(obj[0]),Long.valueOf(String.valueOf(obj[1])));
		}
		return someDayEarnings;
	}

}
