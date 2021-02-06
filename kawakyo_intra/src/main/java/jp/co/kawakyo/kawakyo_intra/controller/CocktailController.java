package jp.co.kawakyo.kawakyo_intra.controller;

import java.text.SimpleDateFormat;
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

@Controller
public class CocktailController {

	@Autowired
	EarningsCalculate earningsCalculate;

	Logger logger = LoggerFactory.getLogger(CocktailController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String helloWorld(Model model) {

		logger.info("[START] ORALCEに接続して受注データを取得します。");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");

		/*当日の売上金額を取得*/
		Long todayEarnings = earningsCalculate.calculateTodayEarnings();

		logger.info(simpleDateFormat.format(new Date()) + "の売上は" + todayEarnings + "です。");


		/* 前日から１週間前までの売上データを取得 */
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE,-1);
		Map<String, Long> daysEarnings = earningsCalculate.getSomeDayEarnings(calendar.getTime(), 6);

		for(String key :daysEarnings.keySet()) {
			logger.info(key + "の売上は" + daysEarnings.get(key) + "です。");
		}

		daysEarnings.put(simpleDateFormat.format(new Date()), todayEarnings);

		logger.info("[END  ] ORALCEに接続して受注データを取得します。");


		model.addAttribute("message", "こんちは世界");
		model.addAttribute("earnings", daysEarnings);
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
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

		/*当日の売上金額を取得*/
		Long todayEarnings = earningsCalculate.calculateTodayEarnings();

		logger.info(simpleDateFormat.format(new Date()) + "の売上は" + todayEarnings + "です。");


		/* 前日から１週間前までの売上データを取得 */
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE,-1);
		Map<String, Long> daysEarnings = earningsCalculate.getSomeDayEarnings(calendar.getTime(), 6);

		for(String key :daysEarnings.keySet()) {
			logger.info(key + "の売上は" + daysEarnings.get(key) + "です。");
		}

		daysEarnings.put(simpleDateFormat.format(new Date()), todayEarnings);

		logger.info("[END  ] ORALCEに接続して受注データを取得します。");

		model.addAttribute("message", "こんにちは" + name + "さん");
		model.addAttribute("earnings", daysEarnings);

		return "test";
	}

}