package jp.co.kawakyo.kawakyo_intra.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="udntha")
public class UDNTHAEntity {
	@Id
	private String datno;
	private String datkb;
	private String udndt;
	private String sbaurikn;
	private String sbauzkkn;
}
