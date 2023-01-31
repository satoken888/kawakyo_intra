package jp.co.kawakyo.kawakyo_intra.scheduledTasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jp.co.kawakyo.kawakyo_intra.model.entity.JDNTHAEntity;
import jp.co.kawakyo.kawakyo_intra.model.logic.OrderCustomerService;

@Component
public class NotifyOrderToPatlite {

    public static String lastOrderId = "0";

    @Autowired
    OrderCustomerService orderCustomerService;

    Logger logger = LoggerFactory.getLogger(NotifyOrderToPatlite.class);

    @Scheduled(cron = "0 */1 8-16 * * MON-SAT")
    public void execute() {
        logger.debug("パトライト処理開始");
        logger.debug("lastOrderId : " + lastOrderId);
        notifyOrderToPatlite();
        logger.debug("パトライト処理終了");
    }

    public void notifyOrderToPatlite() {

        // 現在時刻の取得
        Calendar cal = Calendar.getInstance();
        String todayStr = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        String tommorowStr = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());

        // 直近10分間の受注で「当日・明日出荷」の受注情報の取得
        logger.debug("DB接続開始");
        List<JDNTHAEntity> orderList = orderCustomerService.getOrderRecord(todayStr, tommorowStr, lastOrderId);
        logger.debug("DB接続終了");

        // 当日出荷の受注が存在すれば、パトライトを表示
        if (CollectionUtils.isNotEmpty(orderList)) {

            String red = "9";
            String yellow = "9";
            String green = "9";

            // 通販からの受注（通常受注）バッチ９００の場合
            long tuhanTommorowShipOrderCount = orderList.stream()
                    .filter(record -> StringUtils.equals(record.getSyubacid().trim(), "900")).count();
            // 通販からの受注（追加分） バッチ９０１の場合
            long tuhanAddOrderCount = orderList.stream()
                    .filter(record -> StringUtils.equals(record.getSyubacid().trim(), "901")).count();

            // 店舗からの受注(通常受注) バッチ８００の場合
            long shopTommorowShipOrderCount = orderList.stream()
                    .filter(record -> StringUtils.equals(record.getSyubacid().trim(), "800")).count();
            // 店舗からの追加 バッチ８０１の場合
            long shopAddOrderCount = orderList.stream()
                    .filter(record -> StringUtils.equals(record.getSyubacid().trim(), "801")).count();

            // 当日出荷の一般オーダーの受注数 バッチ２～４の場合
            long normalOrderCount = orderList.stream()
                    .filter(record -> StringUtils.equals(record.getJucsyydt(), todayStr))
                    .filter(record -> StringUtils.equals(record.getSyubacid().trim(), "2")
                            || StringUtils.equals(record.getSyubacid().trim(), "3")
                            || StringUtils.equals(record.getSyubacid().trim(), "4"))
                    .count();

            if (normalOrderCount > 0) {
                // 一般オーダーの受注がある場合
                // 赤ランプを点灯
                red = "1";
            }
            if (shopAddOrderCount > 0) {
                // 自店舗追加オーダーの受注がある場合
                // 黄色ランプを点滅
                yellow = "2";
            } else if (shopTommorowShipOrderCount > 0) {
                // 自店舗通常受注オーダーの場合
                // 黄色ランプを点灯
                yellow = "1";
            }
            if (tuhanAddOrderCount > 0) {
                // 通販の追加受注ある場合
                // 緑ランプを点滅
                green = "2";
            } else if (tuhanTommorowShipOrderCount > 0) {
                // 通販の通常受注がある場合
                // 緑ランプを点灯
                green = "1";
            }

            // パトライトを表示
            callPatliteFlash(red, yellow, green);

            // 取得した受注リストの中から最大の受注Noを取得し、static変数に格納
            // 次回の実行時の開始受注Noに設定する
            JDNTHAEntity maxOrderNoEntity = orderList.stream().max(Comparator.comparing(JDNTHAEntity::getJdnno)).get();
            lastOrderId = maxOrderNoEntity.getJdnno();
        }

    }

    private void callPatliteFlash(String red, String yellow, String green) {
        String alertStr = String.join("", red, yellow, green, "999");

        try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
            HttpGet httpGet = new HttpGet("http://192.168.100.9/api/control?alert=" + alertStr);
            // HTTPリクエストを実行します。 HTTPステータスが200の場合は取得したHTMLを表示します。
            try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet);) {
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    // 通信成功
                    logger.debug("patlite red Flashed");
                } else {
                    // 通信失敗
                    logger.debug("flash error :" + httpResponse.getStatusLine().getStatusCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
