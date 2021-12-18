package jp.co.kawakyo.kawakyo_intra.model.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jp.co.kawakyo.kawakyo_intra.model.entity.JDNTHAEntity;
import jp.co.kawakyo.kawakyo_intra.model.entity.OrderCustomerView;
import jp.co.kawakyo.kawakyo_intra.repository.JDNTHARepository;

@Service
public class OrderCustomerService {

	@Autowired
	JDNTHARepository jdnthaRepository;
	
	/**
	 * 指定受注日の受注情報を取得する
	 * @param orderDateList
	 * @return
	 */
	public List<JDNTHAEntity> getOrderCustomerInfoListByOrderDate (List<String> orderDateList) {
		return jdnthaRepository.findAll(this.orderDateInclude(orderDateList));
	}
	
	/**
	 * 指定日の取引先・出荷予定日でまとめた受注データを取得する
	 * @param orderDate 受注日（yyyymmdd形式）
	 * @return
	 */
	public List<OrderCustomerView> getOrderCustomerGroupByCustomerAndShipDate(String orderDate) {
		List<Object> orderCustomerInfoList = jdnthaRepository.findOrderCustomerGroupByShipDate(orderDate);
		List<OrderCustomerView> rtnOrderCustomerView = new ArrayList<OrderCustomerView>();
		
		Iterator<Object> itr = orderCustomerInfoList.iterator();
		while(itr.hasNext()) {
			Object[] obj = (Object[])itr.next();
			OrderCustomerView record = new OrderCustomerView();
			record.setTokcd(String.valueOf(obj[0]));
			record.setTokrn(String.valueOf(obj[1]));
			record.setJucsyydt(String.valueOf(obj[2]));
			record.setSbauodkn(Long.valueOf(String.valueOf(obj[3])));
			rtnOrderCustomerView.add(record);
		}
		
		return rtnOrderCustomerView;
	}
	
	/**
	 * SQL発行のための条件文（受注日によるin句）
	 * @param orderDateList 検索対象の受注日（8桁文字列）のリスト
	 * @return
	 */
	private Specification<JDNTHAEntity> orderDateInclude(List<String> orderDateList) {
		return orderDateList.size() == 0 ? null : new Specification<JDNTHAEntity>() {
			@Override
			public Predicate toPredicate(Root<JDNTHAEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
	            return root.get("jdndt").in(orderDateList);
	        }
		};
	}
}
