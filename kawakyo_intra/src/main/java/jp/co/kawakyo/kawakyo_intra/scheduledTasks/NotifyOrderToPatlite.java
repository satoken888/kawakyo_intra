package jp.co.kawakyo.kawakyo_intra.scheduledTasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.kawakyo.kawakyo_intra.model.entity.OrderCustomerView;
import jp.co.kawakyo.kawakyo_intra.model.logic.OrderCustomerService;
import jp.co.kawakyo.kawakyo_intra.repository.JDNTHARepository;

@Component
public class NotifyOrderToPatlite {

    public static String lastOrderId;

    @Autowired
	OrderCustomerService orderCustomerService;
    
    //TODO:cron化(10分ごとに処理)
    public void execute(){
        notifyOrderToPatlite();
    }

    public void notifyOrderToPatlite(){

        //現在時刻の取得
        Calendar cal = Calendar.getInstance();
        String todayStr = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());

        //直近10分間の受注で当日出荷の受注情報の取得
        List<OrderCustomerView> orderList = orderCustomerService.getOrderCustomerGroupByCustomerAndShipDate(todayStr);
        orderList = orderList.stream().filter(s -> StringUtils.equals(s.getJucsyydt(), todayStr)).collect(Collectors.toList());

        //TODO:当日出荷のリストの中から10分以内の受注をどう判別するか。
        //     毎回、最新の受注ID（lastOrderId）を取得してそれ以降は新しいものとしていくか。。。
        //当日出荷の受注が存在すれば、
        //パトライトを表示

        //LINEグループに通知を送る

    }
}
