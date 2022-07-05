package jp.co.kawakyo.kawakyo_intra.model.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.kawakyo.kawakyo_intra.model.entity.HINMTFEntity;
import jp.co.kawakyo.kawakyo_intra.model.entity.HINMTZEntity;
import jp.co.kawakyo.kawakyo_intra.model.entity.JDNTRAEntity;
import jp.co.kawakyo.kawakyo_intra.repository.HINMTFRepository;
import jp.co.kawakyo.kawakyo_intra.repository.HINMTZRepository;
import jp.co.kawakyo.kawakyo_intra.repository.JDNTRARepository;

@Service
public class ItemManagementService {

    @Autowired
    HINMTFRepository hinmtfRepository;
    @Autowired
    HINMTZRepository hinmtzRepository;
    @Autowired
    JDNTRARepository jdntraRepository;

    public List<HINMTFEntity> findAllSetItemMst() {
        List<HINMTFEntity> result = new ArrayList<HINMTFEntity>();
        List<Object> dataList = hinmtfRepository.findAllSetItems();
        Iterator<Object> itr = dataList.iterator();
		while(itr.hasNext()) {
			Object[] obj = (Object[])itr.next();
            HINMTFEntity entity = new HINMTFEntity();
            entity.setHincd(String.valueOf(obj[0]));
            entity.setKoshincd(String.valueOf(obj[1]));
            entity.setKoscassu(String.valueOf(obj[2]));
            result.add(entity);
		}
        return result;
    }

    public List<HINMTZEntity> findAllItemContentsMst() {
        List<HINMTZEntity> result = new ArrayList<HINMTZEntity>();
        List<Object> dataList = hinmtzRepository.findAllItemContents();
        Iterator<Object> itr = dataList.iterator();
		while(itr.hasNext()) {
			Object[] obj = (Object[])itr.next();
            HINMTZEntity entity = new HINMTZEntity();
            entity.setHincd(String.valueOf(obj[0]));
            entity.setKoshincd(String.valueOf(obj[1]));
            entity.setKoscassu(String.valueOf(obj[2]));
            result.add(entity);
		}
        return result;
    }

    public List<JDNTRAEntity> findAllOrderDetailsByShippingDate(String startDate,String endDate) {
        List<JDNTRAEntity> result = new ArrayList<JDNTRAEntity>();

        //DBアクセス
        //JDNTRAテーブルから受注情報を検索
        //テーブルにPK定義されていないためObject型で取得
        List<Object> dataList = jdntraRepository.findByShippingDate(startDate, endDate);

        Iterator<Object> itr = dataList.iterator();
		while(itr.hasNext()) {
			Object[] obj = (Object[])itr.next();
            JDNTRAEntity entity = new JDNTRAEntity();
            entity.setJdnno(String.valueOf(obj[0]));
            entity.setJdndt(String.valueOf(obj[1]));
            entity.setSyuytidt(String.valueOf(obj[2]));
            entity.setTokcd(String.valueOf(obj[3]));
            entity.setHincd(String.valueOf(obj[4]));
            entity.setHinnma(String.valueOf(obj[5]));
            entity.setUodsu(String.valueOf(obj[6]));
            entity.setDatkb(String.valueOf(obj[7]));
            entity.setDatno(String.valueOf(obj[8]));
			result.add(entity);
		}
        return result;
    }
}
