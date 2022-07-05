package jp.co.kawakyo.kawakyo_intra.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="hinmtz")
public class HINMTZEntity {

  @Id
  private String hincd;

  private String koshincd;

  private String koscassu;
}