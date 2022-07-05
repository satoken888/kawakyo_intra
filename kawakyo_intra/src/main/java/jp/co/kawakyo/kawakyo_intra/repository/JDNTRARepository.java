package jp.co.kawakyo.kawakyo_intra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.kawakyo.kawakyo_intra.model.entity.JDNTRAEntity;

@Repository
public interface JDNTRARepository extends JpaRepository<JDNTRAEntity, String>{

	// @Query("select t from JDNTRAEntity t where datkb = '1' and syuytidt >= :minDate and syuytidt <= :maxDate")
	// List<JDNTRAEntity> findByShippingDate(@Param("minDate") String minDate, @Param("maxDate")String maxDate);

	@Query("select jdnno,jdndt,syuytidt,tokcd,hincd,hinnma,uodsu,datkb,datno from JDNTRAEntity t where datkb = '1' and syuytidt >= :minDate and syuytidt <= :maxDate")
	List<Object> findByShippingDate(@Param("minDate")String minDate, @Param("maxDate")String maxDate);
}
