package jp.co.kawakyo.kawakyo_intra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.kawakyo.kawakyo_intra.model.entity.HINMTAEntity;


@Repository
public interface HINMTARepository extends JpaRepository<HINMTAEntity, String> {

	@Query("select hincd,hinnma from HINMTAEntity t where t.hincd in :hincdList")
	List<Object> findAllItemsInHincd(@Param("hincdList") List<String> hincdList);

}
