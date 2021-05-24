package jp.co.kawakyo.kawakyo_intra.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.kawakyo.kawakyo_intra.model.entity.OrderEntity;
import jp.co.kawakyo.kawakyo_intra.model.logic.EarningsCalculate;
import jp.co.kawakyo.kawakyo_intra.utils.ConvertUtils;

@Controller
public class CocktailController {

	@Autowired
	EarningsCalculate earningsCalculate;

	Logger logger = LoggerFactory.getLogger(CocktailController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String helloWorld(Model model) {

		logger.info("[START] ORALCEに接続して受注データを取得します。");
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		String today = String.valueOf(cal.get(Calendar.YEAR))
						+ String.valueOf(cal.get(Calendar.MONTH) + 1 < 10 ? "0" + (cal.get(Calendar.MONTH) + 1) : cal.get(Calendar.MONTH) + 1)
						+ String.valueOf(cal.get(Calendar.DATE) < 10 ? "0" + cal.get(Calendar.DATE) : cal.get(Calendar.DATE));

		Map<String,Long> monthEarnings = earningsCalculate.getSomeDayEarnings(ConvertUtils.covDate(now, true) ,ConvertUtils.covDate(now, false));

		monthEarnings = earningsCalculate.addHolidayEarnings(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), monthEarnings);

		//昨年のデータを取得する
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)-1);
		Date lastYearDate = cal.getTime();

		Map<String, Long> lastYearMonthEarnings = earningsCalculate.getSomeDayEarnings(ConvertUtils.covDate(lastYearDate, true), ConvertUtils.covDate(lastYearDate, false));
		lastYearMonthEarnings = earningsCalculate.addHolidayEarnings(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), lastYearMonthEarnings);
		logger.info("[END  ] ORALCEに接続して受注データを取得します。");


		model.addAttribute("message", "こんちは世界");
		model.addAttribute("earnings", monthEarnings);
		model.addAttribute("lastYearEarnings", lastYearMonthEarnings);
		model.addAttribute("todayEarnings", monthEarnings.get(today));
		model.addAttribute("totalEarnings", earningsCalculate.getTotalEarnings(monthEarnings));
		model.addAttribute("goalEarnings", 50110000);
//		model.addAttribute("earnings", daysEarnings);
		return "index";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String nameToMessage(@RequestParam("name") String name, Model model) {

		logger.info("[START] ORALCEに接続して受注データを取得します。");
		OrderEntity order = new OrderEntity();
		order.setDatkb("1");
		order.setJucsyydt("20201216");

//		Example<OrderEntity> example = Example.of(order);
//		Page<OrderEntity> emps = orderRepository.findAll(new PageRequest(0, 10, Direction.DESC,"datno"));
//		Page<OrderEntity> emps2 = orderRepository.findAll(example, new PageRequest(0, 20, Direction.DESC,"datno"));

//		String rtnMessage = "";
//		String record;
//		for (OrderEntity emp : emps2) {
//			record = emp.getJdnno() + "の出荷予定日は" + emp.getJucsyydt() + "です。";
//			logger.info(record);
//			rtnMessage += record + "\r\n";
//		}

		/* test start */
//		String date = "20201217";
//		Long earnings = earningsRepository.findOneDayEarnings(date);
//		logger.info(date + "の売上は" + earnings + "です。");
		logger.info("[END  ] ORALCEに接続して受注データを取得します。");

		model.addAttribute("message", "こんにちは" + name + "さん");
//		model.addAttribute("search",rtnMessage);

		return "index";
	}

	@RequestMapping(value="/test", method = RequestMethod.GET)
	public String showTestPage(Model model) {
		model.addAttribute("message", "こんちは世界 test");
		return "test";
	}

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public String showTestPagePOST(@RequestParam("name") String name, Model model) {

		logger.info("[START] ORALCEに接続して受注データを取得します。");
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();


		Map<String,Long> monthEarnings = earningsCalculate.getSomeDayEarnings(ConvertUtils.covDate(now, true) ,ConvertUtils.covDate(now, false));

//		/*当日の売上金額を取得*/
//		Long todayEarnings = earningsCalculate.calculateTodayEarnings();
//
//		logger.info(simpleDateFormat.format(new Date()) + "の売上は" + todayEarnings + "です。");
//
//
//		/* 前日から１週間前までの売上データを取得 */
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
//		calendar.add(Calendar.DATE,-1);
//		Map<String, Long> daysEarnings = earningsCalculate.getSomeDayEarnings(calendar.getTime(), 6);
//
//		for(String key :daysEarnings.keySet()) {
//			logger.info(key + "の売上は" + daysEarnings.get(key) + "です。");
//		}
//
//		daysEarnings.put(simpleDateFormat.format(new Date()), todayEarnings);

		logger.info("[END  ] ORALCEに接続して受注データを取得します。");

		model.addAttribute("message", "こんにちは" + name + "さん");
		model.addAttribute("earnings", monthEarnings);

		return "test";
	}

}