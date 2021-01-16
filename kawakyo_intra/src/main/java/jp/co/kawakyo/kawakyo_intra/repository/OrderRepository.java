package jp.co.kawakyo.kawakyo_intra.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.kawakyo.kawakyo_intra.model.entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
	Page<OrderEntity> findAll(Pageable pageable);

	/**
	 * 単日の受注金額のトータルを取得
	 * @param date
	 * @return
	 */
	@Query("select sum(u.sbauodkn) from OrderEntity u where u.jdndt = :date and u.datkb = '1' group by u.jdndt")
	Long findOneDaySbauodkn(@Param("date") String date);
}