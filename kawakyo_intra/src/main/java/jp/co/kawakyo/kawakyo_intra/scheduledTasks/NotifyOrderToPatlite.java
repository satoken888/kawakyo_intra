package jp.co.kawakyo.kawakyo_intra.scheduledTasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
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

    @Scheduled(cron = "0 */1 8-18 * * ?")
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

        // 直近10分間の受注で「当日出荷」の受注情報の取得
        logger.debug("DB接続開始");
        List<JDNTHAEntity> orderList = orderCustomerService.getOrderRecord(todayStr, todayStr, lastOrderId);
        logger.debug("DB接続終了");

        // 当日出荷の受注が存在すれば、パトライトを表示
        if (CollectionUtils.isNotEmpty(orderList)) {
            // パトライトを表示
            callPatliteRedFlash("2", "0", "0");
            
            // 取得した受注リストの中から最大の受注Noを取得し、static変数に格納
            // 次回の実行時の開始受注Noに設定する
            JDNTHAEntity maxOrderNoEntity = orderList.stream().max(Comparator.comparing(JDNTHAEntity::getJdnno)).get();
            lastOrderId = maxOrderNoEntity.getJdnno();
        }

    }

    private void callPatliteRedFlash(String red, String yellow, String green) {
        String alertStr = String.join("", red, yellow, green, "000");

        try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
            HttpGet httpGet = new HttpGet("http://192.168.1.97/api/control?alert=" + alertStr);
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
