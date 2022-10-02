package jp.co.kawakyo.kawakyo_intra.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kintone.client.KintoneClient;
import com.kintone.client.KintoneClientBuilder;
import com.kintone.client.model.record.Record;

import jp.co.kawakyo.kawakyo_intra.model.logic.EarningsCalculate;
import jp.co.kawakyo.kawakyo_intra.utils.ConvertUtils;
import jp.co.kawakyo.kawakyo_intra.utils.KintoneConstants;

@Controller
public class CocktailController {

	@Autowired
	EarningsCalculate earningsCalculate;

	Logger logger = LoggerFactory.getLogger(CocktailController.class);

	@GetMapping(value = "/")
	public String showIndex(Model model) {

		logger.debug("[START] ORALCEに接続して受注データを取得します。");
		Calendar cal = Calendar.getInstance();
		Calendar lastYearCal = Calendar.getInstance();
		Date now = cal.getTime();
		String today = ConvertUtils.getDateStrYYYYMMDD(cal);

		//kintone検索用に日付文字列を「yyyy年mm月」で作成
		String kintoneForecastDate = String.valueOf(cal.get(Calendar.YEAR)) + "年" + String.valueOf(cal.get(Calendar.MONTH)+1) + "月";

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
		logger.debug("[END  ] ORALCEに接続して受注データを取得します。");

		//kintoneとのコネクション作成
		logger.debug("kintone初期化処理 開始");
		try(KintoneClient client = KintoneClientBuilder.create(KintoneConstants.KINTONE_URL).authByApiToken(KintoneConstants.KINTONE_SALESINFO_API_TOKEN).build()) {

			logger.debug("kintone初期化処理 終了");

			//kintoneの売上予算表から売上予算の取得
			logger.debug("kintone売上情報取得 開始");
			List<Record> records =  client.record().getRecords(KintoneConstants.KINTONE_SALESBUDGET_APP_CODE,"month in (\"" + kintoneForecastDate + "\") and forecast_div in (\"予算\")");
			logger.debug("kintone売上情報取得 終了");

			logger.debug("===取得結果===");
			BigDecimal forecast_earnings = new BigDecimal(0);
			for(Record record : records) {
				logger.debug("対象年月：" + record.getDropDownFieldValue("month"));
				logger.debug("売上予算：" + record.getNumberFieldValue(KintoneConstants.KINTONE_SALESBUDGET_FIELD_CODE));
				logger.debug("==============");
				//売上予算を格納
				forecast_earnings = record.getNumberFieldValue(KintoneConstants.KINTONE_SALESBUDGET_FIELD_CODE);
			}

			//view用にmodelに格納する
			//現時点までの今月の日別売上金額
			model.addAttribute("earnings", monthEarnings);
			//昨年の売上合計金額
			model.addAttribute("lastYearEarnings", lastYearMonthEarnings);
			//本日の売上合計金額
			model.addAttribute("todayEarnings", monthEarnings.get(today));
			//今月の累計売上金額
			model.addAttribute("totalEarnings", cumulativeSales);
			//今月の売上予算
			model.addAttribute("goalEarnings", forecast_earnings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "index";
	}
}