package jp.co.kawakyo.kawakyo_intra.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jp.co.kawakyo.kawakyo_intra.model.entity.OrderCustomerView;
import jp.co.kawakyo.kawakyo_intra.model.logic.OrderCustomerService;

@Controller
public class OrderCustomerController {
	
	@Autowired
	OrderCustomerService orderCustomerService;
	
	Logger logger = Logger.getLogger(OrderCustomerController.class);
	
	@GetMapping(value="/todayClient")
	public String showTodayOrderCustomer(Model model) {		
		logger.info("本日のお客様一覧　表示開始");
		Calendar cal = Calendar.getInstance();
		
		String today = new SimpleDateFormat("yyyyMMdd").format(cal.getTime()); 

		//本日の受注取引先取得
		List<OrderCustomerView> todayOrderList = orderCustomerService.getOrderCustomerGroupByCustomerAndShipDate(today);
		//売上の降順にソート
		todayOrderList.sort(Comparator.comparing(OrderCustomerView::getSbauodkn).reversed()
									.thenComparing(Comparator.comparing(OrderCustomerView::getJucsyydt)));
		
		logger.info("本日の受注取引先 start");
		Long totalOrderEarnings = 0L;
		for(OrderCustomerView entity : todayOrderList) {
			logger.info(entity.getTokrn() + "　売上：" + entity.getSbauodkn() + "円　出荷予定日：" + entity.getJucsyydt());
			totalOrderEarnings += Long.valueOf(entity.getSbauodkn());
		}
		logger.info("合計：" + todayOrderList.size() + "件");
		logger.info("受注金額：" + totalOrderEarnings + "円");
		logger.info("本日の受注取引先 end");
		logger.info("本日のお客様一覧　表示終了");
		
		model.addAttribute("orderCustomerInfoList", todayOrderList);
		return "todayClient";
	}
	
}
