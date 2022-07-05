package jp.co.kawakyo.kawakyo_intra.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="jdntra")
public class JDNTRAEntity {
  @Id
  private String datno;
  /** 削除区分 */
  private String datkb;
  /** 受注伝票番号 */
  private String jdnno;
  /** 受注伝票日付 */
  private String jdndt;
  /** 出荷予定日 */
  private String syuytidt;
  /** 取引先コード */
  private String tokcd;
  /** 商品コード */
  private String hincd;
  /** 商品名 */
  private String hinnma;
  /** 受注数量 */
  private String uodsu;
}