package jp.co.kawakyo.kawakyo_intra.model.logic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
	 * 
	 * @param orderDateList
	 * @return
	 */
	public List<JDNTHAEntity> getOrderCustomerInfoListByOrderDate(List<String> orderDateList) {
		return jdnthaRepository.findAll(this.orderDateInclude(orderDateList));
	}

	/**
	 * 指定日の取引先・出荷予定日でまとめた受注データを取得する
	 * 
	 * @param orderDate 受注日（yyyymmdd形式）
	 * @return
	 */
	public List<OrderCustomerView> getOrderCustomerGroupByCustomerAndShipDate(String orderDate) {
		List<Object> orderCustomerInfoList = jdnthaRepository.findOrderCustomerGroupByShipDate(orderDate);
		List<OrderCustomerView> rtnOrderCustomerView = new ArrayList<OrderCustomerView>();

		Iterator<Object> itr = orderCustomerInfoList.iterator();
		while (itr.hasNext()) {
			Object[] obj = (Object[]) itr.next();
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
	 * 受注日、出荷予定日に合致し、
	 * 指定の受注Ｎｏ以降の受注を検索する
	 * 
	 * @param orderDate
	 * @param shippingDate
	 * @param orderNo
	 * @return
	 */
	public List<JDNTHAEntity> getOrderRecord(String orderDate, String shippingDate, String orderNo) {

		List<JDNTHAEntity> result = new ArrayList<JDNTHAEntity>();

		List<Object> orderCustomerInfoList = jdnthaRepository.findOrderCustomerGteOrderNo(orderDate, shippingDate,
				orderNo);

		Iterator<Object> itr = orderCustomerInfoList.iterator();
		while (itr.hasNext()) {
			Object[] obj = (Object[]) itr.next();
			JDNTHAEntity record = new JDNTHAEntity();
			record.setJdnno(String.valueOf(obj[0]));
			record.setTokcd(String.valueOf(obj[1]));
			record.setTokrn(String.valueOf(obj[2]));
			record.setJucsyydt(String.valueOf(obj[3]));
			record.setSbauodkn(String.valueOf(obj[4]));
			record.setSyubacid(String.valueOf(obj[5]));
			result.add(record);
		}
		return result;

	}

	/**
	 * SQL発行のための条件文（受注日によるin句）
	 * 
	 * @param orderDateList 検索対象の受注日（8桁文字列）のリスト
	 * @return
	 */
	private Specification<JDNTHAEntity> orderDateInclude(List<String> orderDateList) {
		return orderDateList.size() == 0 ? null : new Specification<JDNTHAEntity>() {
			@Override
			public Predicate toPredicate(Root<JDNTHAEntity> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				return root.get("jdndt").in(orderDateList);
			}
		};
	}

	public List<Object> getTodayEarningsAndCustomerName() {

		Calendar cal = Calendar.getInstance();
		String todayStr = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());

		// カクテルから情報取得
		List<Object> earningsAndCustomerList = jdnthaRepository.findEarningsAndCustomerName(todayStr);

		return earningsAndCustomerList;
	}
}
