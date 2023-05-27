package jp.co.kawakyo.kawakyo_intra.scheduledTasks;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kintone.client.KintoneClient;
import com.kintone.client.KintoneClientBuilder;
import com.kintone.client.model.record.DateFieldValue;
import com.kintone.client.model.record.NumberFieldValue;
import com.kintone.client.model.record.Record;
import com.kintone.client.model.record.SingleLineTextFieldValue;

import jp.co.kawakyo.kawakyo_intra.model.logic.OrderCustomerService;
import jp.co.kawakyo.kawakyo_intra.utils.KintoneConstants;

@Component
public class SendKintoneFromCocktail {

    Logger logger = LoggerFactory.getLogger(SendKintoneFromCocktail.class);

    @Autowired
    OrderCustomerService orderCustomerService;

    @Scheduled(cron = "0 0 9-18 * * MON-SAT")
    public void execute() {
        // 毎日９時〜１８時の毎時kintoneへカクテルの情報を送信する
        sendTodayEarningsFromCocktail();
    }

    public void sendTodayEarningsFromCocktail() {
        // カクテルから今日の売上情報を取得
        List<Object> earningsAndCustomerList = orderCustomerService.getTodayEarningsAndCustomerName();

        // カクテルの情報がゼロ件の場合は更新・追加は実施しない
        if (earningsAndCustomerList != null && earningsAndCustomerList.size() > 0) {
            // kintoneへ取得した情報をアップする
            // （すでにデータがあれば削除して追加、なければそのまま追加）
            sendEarningsAndCustomerInfoToKintone(earningsAndCustomerList);
        }
    }

    /**
     * カクテルから取得した本日出荷の得意先別売上データをkintoneへ追加・更新する処理
     * 
     * 当日のデータがkintone上にすでにある場合は、一括削除した後に現状のカクテルデータを新規追加する
     * そのハンドリングをこの処理内にて実施する
     * 
     * @param earningsAndCustomerList
     */
    private void sendEarningsAndCustomerInfoToKintone(List<Object> earningsAndCustomerList) {

        try (KintoneClient client = KintoneClientBuilder.create(KintoneConstants.KINTONE_URL)
                .authByApiToken(KintoneConstants.KINTONE_CUSTOMER_EARNINGS_API_TOKEN).build()) {

            // すでにkintoneに登録されている本日出荷のリストを取得する
            String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String query = "jucsyydt = \"" + todayStr + "\"";
            List<Record> savedRecords = client.record().getRecords(KintoneConstants.KINTONE_CUSTOMER_EARNINGS_APP_CODE,
                    query);

            // すでに本日出荷売上の登録がある場合は既存レコードの削除をする
            if (savedRecords.size() > 0) {
                // 削除処理 start
                List<Long> deleteRecordIds = new ArrayList<Long>();
                for (Record record : savedRecords) {
                    deleteRecordIds.add(record.getId());
                }
                client.record().deleteRecords(KintoneConstants.KINTONE_CUSTOMER_EARNINGS_APP_CODE, deleteRecordIds);
                // 削除処理 end
            }

            // 新規登録処理 start
            List<Record> registRecords = createRegistRecords(earningsAndCustomerList);
            client.record().addRecords(KintoneConstants.KINTONE_CUSTOMER_EARNINGS_APP_CODE, registRecords);
            // 新規登録処理 end

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * カクテルからの得意先別日別売上データをkintoneへアップする際の
     * リクエストパラメータを生成する
     * 
     * @param earningsAndCustomerList カクテルから取得した得意先別売上データ
     * @return kintoneへ追加するレコードリスト
     */
    private List<Record> createRegistRecords(List<Object> earningsAndCustomerList) {
        List<Record> rtnList = new ArrayList<Record>();

        Iterator<Object> itr = earningsAndCustomerList.iterator();
        while (itr.hasNext()) {
            Object[] obj = (Object[]) itr.next();
            Record record = new Record();
            record.putField("tokrn", new SingleLineTextFieldValue(String.valueOf(obj[1])));
            LocalDate targetDate = DateTimeFormatter.ofPattern("uuuuMMdd")
                    .withResolverStyle(ResolverStyle.STRICT)
                    .parse(String.valueOf(obj[2]), LocalDate::from);
            record.putField("jucsyydt", new DateFieldValue(targetDate));
            record.putField("sbauodkn", new NumberFieldValue(Long.valueOf(String.valueOf(obj[3]))));
            record.putField("tannm", new SingleLineTextFieldValue(String.valueOf(obj[4])));
            rtnList.add(record);
        }

        return rtnList;
    }
}
