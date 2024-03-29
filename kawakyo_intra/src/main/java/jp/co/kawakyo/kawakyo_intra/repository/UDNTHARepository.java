package jp.co.kawakyo.kawakyo_intra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.kawakyo.kawakyo_intra.model.entity.UDNTHAEntity;

@Repository
public interface UDNTHARepository extends JpaRepository<UDNTHAEntity, String> {

	@Query("select sum(u.sbaurikn - u.sbauzkkn) from UDNTHAEntity u where u.udndt = :date and u.datkb = '1' group by u.udndt")
	Long findOneDayEarnings(@Param("date") String date);

	@Query("select u.udndt,sum(u.sbaurikn - u.sbauzkkn) from UDNTHAEntity u where u.udndt >= :startDate and u.udndt <= :endDate and u.datkb = '1' group by u.udndt")
	List<Object> findDaysEarnings(@Param("startDate") String startDate,@Param("endDate") String endDate);

}
