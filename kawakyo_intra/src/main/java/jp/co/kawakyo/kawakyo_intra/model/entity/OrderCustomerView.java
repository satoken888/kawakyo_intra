package jp.co.kawakyo.kawakyo_intra.model.entity;

import lombok.Data;

/**
 * 受注取引先一覧ビュー用のエンティティ
 * @author satokent
 *
 */
@Data
public class OrderCustomerView {
	private String tokcd;
	private String tokrn;
	private String jdndt;
	private String jucsyydt;
	private Long sbauodkn;
}
