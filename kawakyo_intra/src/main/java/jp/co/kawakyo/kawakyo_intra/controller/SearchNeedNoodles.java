package jp.co.kawakyo.kawakyo_intra.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.kawakyo.kawakyo_intra.model.entity.HINMTFEntity;
import jp.co.kawakyo.kawakyo_intra.model.entity.HINMTZEntity;
import jp.co.kawakyo.kawakyo_intra.model.entity.JDNTRAEntity;
import jp.co.kawakyo.kawakyo_intra.model.entity.form.searchNoodlesInputForm;
import jp.co.kawakyo.kawakyo_intra.model.logic.ItemManagementService;

@Controller
public class SearchNeedNoodles {

    @Autowired
    ItemManagementService itemManagementService;

    Logger logger = Logger.getLogger(SearchNeedNoodles.class);

    /**
     * 麺製造数計算ページ初期表示
     * @param model
     * @return
     */
    @GetMapping(value="/searchNoodles")
    public String searchNeedNoodles(Model model) {

        searchNoodlesInputForm inputForm = new searchNoodlesInputForm();
        model.addAttribute("searchNoodlesInputForm", inputForm);
        return "searchNoodles";
    }

    /**
     * 製造麺数検索処理
     * @param model
     * @param inputShippingDateStart 出荷予定日　開始日
     * @param inputShippingDateEnd　出荷予定日　終了日
     * @return
     */
    @PostMapping(value="/searchNoodles")
    public String searchNeedNoodles(Model model,
                                    @RequestParam(name="inputShippingDateStart")String inputShippingDateStart,
                                    @RequestParam(name="inputShippingDateEnd")String inputShippingDateEnd) {
        logger.info("麺製造数計算処理 start");


        Map<String, Integer> resultMap = getShippingItemList(inputShippingDateStart, inputShippingDateEnd);

        //できた商品リストから麺の情報をピックアップ
        String HT_120 = String.valueOf(resultMap.get("00020890") == null ? 0 : resultMap.get("00020890"));
        String HT_130 = String.valueOf(resultMap.get("00020888") == null ? 0 : resultMap.get("00020888"));
        String yomogi = String.valueOf(resultMap.get("00007110") == null ? 0 : resultMap.get("00007110"));
        String gokubuto_120 = String.valueOf(resultMap.get("00020876") == null ? 0 : resultMap.get("00020876"));
        String kirin_150 = String.valueOf(resultMap.get("00020882") == null ? 0 : resultMap.get("00020882"));
        String hasegawa = String.valueOf(resultMap.get("00020884") == null ? 0 : resultMap.get("00020884"));
        String HT_120_hoso = String.valueOf(resultMap.get("00020891") == null ? 0 : resultMap.get("00020891"));
        String hannama = String.valueOf(resultMap.get("00020991") == null ? 0 : resultMap.get("00020991"));
        String takasui_130 = String.valueOf(resultMap.get("10001013") == null ? 0 : resultMap.get("10001013"));


        //画面表示にデータを渡す
        model.addAttribute("HT_120", HT_120);
        model.addAttribute("HT_130", HT_130);
        model.addAttribute("yomogi", yomogi);
        model.addAttribute("gokubuto_120", gokubuto_120); 
        model.addAttribute("kirin_150", kirin_150);
        model.addAttribute("hasegawa", hasegawa);
        model.addAttribute("HT_120_hoso", HT_120_hoso);
        model.addAttribute("hannama", hannama);
        model.addAttribute("takasui_130", takasui_130);
        model.addAttribute("searchNoodlesInputForm", new searchNoodlesInputForm());
        

        logger.info("麺製造数計算処理 end");
        return "searchNoodles";
    }

    /**
     * 使用品リストの取得
     * 
     * 該当の出荷予定日内の使用するアイテムリストを取得する
     * 
     * @param inputShippingDateStart 出荷予定日（開始）
     * @param inputShippingDateEnd 出荷予定日（終了）
     * @return 該当の出荷予定日期間内に使用するアイテムのリスト
     */
    public Map<String, Integer> getShippingItemList(String inputShippingDateStart,String inputShippingDateEnd) {
                //出荷予定日の期間内の受注情報を検索
                List<JDNTRAEntity> orderInfoList = itemManagementService.findAllOrderDetailsByShippingDate(inputShippingDateStart, inputShippingDateEnd);

                //検索結果から手配内訳リストを作成
                Map<String,Integer> itemQuantityMap = createItemQuantityMap(orderInfoList);
                logger.debug("=======================");
                logger.debug("商品DB取得後");
                logger.debug("00006461：" + itemQuantityMap.get("00006461        "));
                logger.debug("00006463：" + itemQuantityMap.get("00006463        "));
                logger.debug("00010019：" + itemQuantityMap.get("00010019        "));
                logger.debug("00010016：" + itemQuantityMap.get("00010016        "));
                logger.debug("=======================");
        
                //セット構成マスタに該当商品があるか検索
                //あれば商品を分解、なければそのまま
                List<HINMTFEntity> setItemInfoList = itemManagementService.findAllSetItemMst();
                Set<String> deleteItemCodeList = new TreeSet<String>();
                Map<String,Integer> addItemMap = new HashMap<String,Integer>();
                for(String itemCode : itemQuantityMap.keySet()) {
                    //手配内訳商品ごとにループ
                    for(HINMTFEntity entity : setItemInfoList) {
                        //セット商品ごとにループ
                        if(StringUtils.equals(entity.getHincd(), itemCode)){
                            //手配内訳商品の中にセット商品があった場合
                            //追加商品リストに商品コードと商品構成数×受注数の個数を追加する
                            
                            if(addItemMap.containsKey(entity.getKoshincd())) {
                                //分解先の商品がすでに追加商品リストに存在する場合
                                addItemMap.replace(entity.getKoshincd(), addItemMap.get(entity.getKoshincd()) + itemQuantityMap.get(itemCode) * Integer.valueOf(entity.getKoscassu()));
                            } else {
                                //存在しない場合
                                addItemMap.put(entity.getKoshincd(), itemQuantityMap.get(itemCode) * Integer.valueOf(entity.getKoscassu()));
                            }
                            deleteItemCodeList.add(itemCode);
                        }
                    }
                }
        
                //追加商品リストと手配内訳リストを合算
                for(String itemCode : addItemMap.keySet()) {
                    if(itemQuantityMap.containsKey(itemCode)) {
                        itemQuantityMap.replace(itemCode, addItemMap.get(itemCode) + itemQuantityMap.get(itemCode));
                    } else {
                        itemQuantityMap.put(itemCode, addItemMap.get(itemCode));
                    }
                }
        
                //もとのセット商品の情報が存在すると不要な情報が残ってしまうので、
                //構成品に分解したセット商品のおおもとの情報を削除する。
                for(String itemCode: deleteItemCodeList) {
                    itemQuantityMap.remove(itemCode);
                }
        
                logger.debug("=======================");
                logger.debug("セット商品対応後");
                logger.debug("00006461：" + itemQuantityMap.get("00006461        "));
                logger.debug("00006463：" + itemQuantityMap.get("00006463        "));
                logger.debug("00010019：" + itemQuantityMap.get("00010019        "));
                logger.debug("00010016：" + itemQuantityMap.get("00010016        "));
                logger.debug("=======================");
        
                //商品内容マスタに該当商品があるか検索
                //あれば商品を分解、なければそのまま
                List<HINMTZEntity> itemContentsMst = itemManagementService.findAllItemContentsMst();
                deleteItemCodeList = new TreeSet<String>();
                addItemMap = new HashMap<String,Integer>();
                for(String itemCode : itemQuantityMap.keySet()) {
                    for(HINMTZEntity entity : itemContentsMst) {
                        if(StringUtils.equals(entity.getHincd(), itemCode)) {
                            if(addItemMap.containsKey(entity.getKoshincd())) {
                                //分解先の商品がすでに手配内訳リストに存在する場合
                                addItemMap.replace(entity.getKoshincd(), addItemMap.get(entity.getKoshincd()) + itemQuantityMap.get(itemCode) * Integer.valueOf(entity.getKoscassu()));
                            } else {
                                //存在しない場合
                                addItemMap.put(entity.getKoshincd(), itemQuantityMap.get(itemCode) * Integer.valueOf(entity.getKoscassu()));
                            }
                            // deleteItemCodeList.add(itemCode);
                        }
                    }
                }
        
                //追加商品リストと手配内訳商品リストを合算
                for(String itemCode : addItemMap.keySet()) {
                    if(itemQuantityMap.containsKey(itemCode)) {
                        itemQuantityMap.replace(itemCode, addItemMap.get(itemCode) + itemQuantityMap.get(itemCode));
                    } else {
                        itemQuantityMap.put(itemCode, addItemMap.get(itemCode));
                    }
                }
        
                //もとの商品の情報が存在すると不要な情報が残ってしまうので、
                //構成品に分解した商品のおおもとの情報を削除する。
                //TODO:これは必要か否か確認
                // for(String itemCode: deleteItemCodeList) {
                //     itemQuantityMap.remove(itemCode);
                // }
        
                logger.debug("=======================");
                logger.debug("商品内容マスタ適用後");
                logger.debug("00006461：" + itemQuantityMap.get("00006461        "));
                logger.debug("00006463：" + itemQuantityMap.get("00006463        "));
                logger.debug("00010019：" + itemQuantityMap.get("00010019        "));
                logger.debug("00010016：" + itemQuantityMap.get("00010016        "));
                logger.debug("=======================");
        
                //キーのトリミング（DBの商品コードに空白がついてしまっているため）
                //TODO:無駄な処理
                Map<String,Integer> resultMap = new HashMap<String,Integer>();
                for(String key : itemQuantityMap.keySet()) {
                    resultMap.put(key.trim(), itemQuantityMap.get(key));
                }

                return resultMap;
    }

    /**
     * 受注情報から取得したアイテムと出荷個数のまとめたリストを作成
     * @param orderInfoList 受注テーブルから取得した情報リスト
     * @return rtnMap キーに商品コード、値に出荷個数を入れたマップ
     */
    private Map<String, Integer> createItemQuantityMap(List<JDNTRAEntity> orderInfoList) {
        Map<String, Integer> rtnMap = new TreeMap<String,Integer>();

        for(JDNTRAEntity entity : orderInfoList) {
            String itemCode = entity.getHincd();
            if(rtnMap.containsKey(itemCode)) {
                //追加処理
                rtnMap.replace(itemCode, rtnMap.get(itemCode) + Integer.valueOf(entity.getUodsu()));
            } else {
                //新規作成処理
                rtnMap.put(itemCode, Integer.valueOf(entity.getUodsu()));
            }
        }
        
        return rtnMap;
    }

}