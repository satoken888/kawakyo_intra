package jp.co.kawakyo.kawakyo_intra.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="jdntha")
public class JDNTHAEntity {
  @Id
  private String datno;
  /** 削除区分 */
  private String datkb;
  /** 取引先コード */
  private String tokcd;
  /** 取引先名 */
  private String tokrn;
  /** 受注番号 */
  private String jdnno;
  /** 受注日付 */
  private String jdndt;
  /** 納期日付 */
  private String defnokdt;
  /** 出荷予定日 */
  private String jucsyydt;
  /** 受注金額（伝票計） */
  private String sbauodkn;

}