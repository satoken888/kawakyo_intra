package jp.co.kawakyo.kawakyo_intra.model.entity.form;

import java.util.List;

import lombok.Data;

@Data
public class FarmFreshPickingInputForm {
    
    private String shippingStartDate;
    private String shippingEndDate;
    private List<String> batchNoList;
}
