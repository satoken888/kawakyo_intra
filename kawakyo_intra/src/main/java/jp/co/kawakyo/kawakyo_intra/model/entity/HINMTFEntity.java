package jp.co.kawakyo.kawakyo_intra.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="hinmtf")
public class HINMTFEntity {

  @Id
  private String hincd;

  private String koshincd;

  private String koscassu;
}