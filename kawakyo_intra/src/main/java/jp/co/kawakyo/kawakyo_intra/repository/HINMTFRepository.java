package jp.co.kawakyo.kawakyo_intra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jp.co.kawakyo.kawakyo_intra.model.entity.HINMTFEntity;

@Repository
public interface HINMTFRepository extends JpaRepository<HINMTFEntity, String> {

	@Query("select hincd,koshincd,koscassu from HINMTFEntity t")
	List<Object> findAllSetItems();

}
