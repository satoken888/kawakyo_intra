package jp.co.kawakyo.kawakyo_intra.utils;

public class KintoneConstants {
	
	private KintoneConstants() {}

	/** kintone売上予算表のAPIトークン  */
	public static final String KINTONE_SALESINFO_API_TOKEN = "mimiedXfPKYO81mtV9eEZ6szpccn1BQvdf8iePVt";

	public static final String KINTONE_NOODLECOUNT_API_TOKEN = "ftpoCSxRiwA0XkGzopb1s6itQiHNo2H4DGFmGi5K";
	
	/** 河京kintone URL */
	public static final String KINTONE_URL = "https://kawakyo.cybozu.com";
	
	//////////////
	//アプリ番号//
	//////////////
	
	/** kintoneの売上予算表のアプリ番号 */
	public static final Long KINTONE_SALESBUDGET_APP_CODE = 49L;
	public static final Long KINTONE_NOODLECOUNT_APP_CODE = 55L;
	
	////////////////////
	//フィールドコード//
	////////////////////
	
	//売上予算表
	/** 売上予算もしくは実績の売上フィールド名 */
	public static final String KINTONE_SALESBUDGET_FIELD_CODE = "数値";
	
	//////////////
	//選択肢項目//
	//////////////
	
	//売上予算表
	/** 部署 */
	public static final String DEPARTMENT_MAINOFFICE = "本社";
	public static final String DEPARTMENT_HONKAN = "本館";
	
	/** 予算・実績区分 */
	public static final String FORECAST_DIV_BUDGET = "予算";
	public static final String FORECAST_DIV_ACHIEVEMENT = "実績";

}
