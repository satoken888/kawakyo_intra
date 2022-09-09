package jp.co.kawakyo.kawakyo_intra.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.kawakyo.kawakyo_intra.model.entity.JDNTHAEntity;

@Repository
public interface JDNTHARepository extends JpaRepository<JDNTHAEntity, String>,JpaSpecificationExecutor<JDNTHAEntity> {
	Page<JDNTHAEntity> findAll(Pageable pageable);

	/**
	 * 単日の受注金額のトータルを取得
	 * @param date
	 * @return
	 */
	@Query("select sum(u.sbauodkn) from JDNTHAEntity u where u.jdndt = :date and u.datkb = '1' group by u.jdndt")
	Long findOneDaySbauodkn(@Param("date") String date);

	@Query("select j.tokcd,j.tokrn,j.jucsyydt,sum(j.sbauodkn) from JDNTHAEntity j where j.jdndt = :jdndt and j.datkb = '1' group by j.tokcd,j.tokrn,j.jucsyydt")
	List<Object> findOrderCustomerGroupByShipDate(@Param("jdndt") String jdndt);
	
	@Query("select max(j.jdnno),j.tokcd,j.tokrn,j.jucsyydt,sum(j.sbauodkn),j.syubacid from JDNTHAEntity j where j.jdndt = :jdndt and j.jucsyydt = :jucsyydt and j.jdnno > :jdnno and j.datkb = '1' group by j.tokcd,j.tokrn,j.jucsyydt,j.syubacid")
	List<Object> findOrderCustomerGteOrderNo(@Param("jdndt") String jdndt,@Param("jucsyydt") String jucsyydt,@Param("jdnno") String jdnno);
}