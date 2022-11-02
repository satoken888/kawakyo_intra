package jp.co.kawakyo.kawakyo_intra.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jp.co.kawakyo.kawakyo_intra.model.entity.FarmFreshPickingRecord;
import jp.co.kawakyo.kawakyo_intra.model.entity.form.FarmFreshPickingInputForm;
import jp.co.kawakyo.kawakyo_intra.model.logic.OrderCustomerService;
import jp.co.kawakyo.kawakyo_intra.utils.Constants;

@Controller
public class FarmFreshPickingController {
    
    @Autowired
    OrderCustomerService orderCustomerService;

    @Autowired
    SearchNeedNoodles searchNeedNoodles;

    Logger logger = LoggerFactory.getLogger(FarmFreshPickingController.class);

    @GetMapping("/farm-picking")
    public String showPickingInfo(Model model){

        FarmFreshPickingInputForm inputForm = new FarmFreshPickingInputForm();
        model.addAttribute("farmFreshPickingInputForm",inputForm);
        model.addAttribute("checkboxItems",getCheckBoxItems());
        return "farm-picking";
    }

    private Map<String,String> getCheckBoxItems() {
        Map<String, String> selectMap = new LinkedHashMap<String, String>();
		selectMap.put("1", "1");
		selectMap.put("2", "2");
		selectMap.put("3", "3");
		selectMap.put("4", "4");
		selectMap.put("111", "111");
        selectMap.put("222", "222");
        selectMap.put("555", "333");
        selectMap.put("888", "444");
        selectMap.put("900", "900");
        selectMap.put("999", "999");
		return selectMap;
    }

    @PostMapping("/farm-picking")
    public String searchPickingInfo(Model model,
                                   @ModelAttribute FarmFreshPickingInputForm farmFreshPickingInputForm) {
        
        //初期値取得
        String alertMessage = "";
        String inputShippingDateStart = farmFreshPickingInputForm.getShippingStartDate();
        String inputShippingDateEnd = farmFreshPickingInputForm.getShippingEndDate();
        List<String> inputBatchNoList = farmFreshPickingInputForm.getBatchNoList();
        List<FarmFreshPickingRecord> itemList = new ArrayList<FarmFreshPickingRecord>();

        //バリデーションチェック
        if(StringUtils.isBlank(inputShippingDateStart)) alertMessage = "出荷予定日（開始）が入力されておりません。";
        if(StringUtils.isBlank(inputShippingDateEnd)) alertMessage = "出荷予定日（終了）が入力されておりません。";
        if(CollectionUtils.isEmpty(inputBatchNoList)) inputBatchNoList = Constants.BATCH_NO_LIST_FULL;

        if(StringUtils.isEmpty(alertMessage)) {
            //入力された出荷予定日,バッチNoに合致する受注情報を取得する
            itemList = searchNeedNoodles.getShippingItemListByBatchNo(StringUtils.replace(inputShippingDateStart, "-", "") , StringUtils.replace(inputShippingDateEnd,"-","") , inputBatchNoList); 
        }

        //手に入れた商品リストを画面に渡す
        model.addAttribute("itemList", itemList);
        model.addAttribute("alertMessage", alertMessage);
        model.addAttribute("farmFreshPickingInputForm", farmFreshPickingInputForm);
        model.addAttribute("checkboxItems",getCheckBoxItems());
        return "farm-picking";
    }
}
