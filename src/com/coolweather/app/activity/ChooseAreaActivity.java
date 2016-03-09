package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();

	// ʡ���У����б�
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;

	// ѡ�е�ʡ�ݣ�����
	private Province selectedProvince;
	private City selectedCity;

	// ��ǰѡ�м���
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �Զ����ɵķ������
		super.onCreate(savedInstanceState);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false)) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO �Զ����ɵķ������
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
					/* Log.d("test", currentLevel+"c"); */
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
					Log.d("test", "��ѯ������");
				} else if (currentLevel == LEVEL_COUNTRY) {
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		Log.d("test", "��������ѯʡ��");
		queryProvinces();// ����ʡ������

	}

	// ��ѯ����ʡ�����Ȳ�ѯ���ݿ⣬���޽����ȥ��������ѯ
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
			Province province = provinceList.get(0);
			Log.d("test", "���ݿ�ʡ�ݼ��سɹ�" + province.getId());
		} else {
			queryFromServer(null, "province");

		}
	}

	// ��ѯѡ��ʡ�������У����Ȳ�ѯ���ݿ⣬û�����ѯ������
	private void queryCities() {
		/* Log.d("test", "��ѯ����"); */
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		/* Log.d("test", "1"+selectedProvince.getProvinceName()); */
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
			Log.d("test", "��ѯ����");
		} else {
			Log.d("test", "���ݿⲻ���ڣ����÷�������ѯ");
			queryFromServer(selectedProvince.getProvinceCode(), "city");

		}
	}

	// ��ѯ���������أ����Ȳ�ѯ���ݿ⣬�������ѯ������
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounty(selectedCity.getId());

		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			currentLevel = LEVEL_COUNTRY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");

		}
	}

	// ��������������������������ѯʡ������Ϣ

	private void queryFromServer(final String code, final String type) {
		String address;
		Log.d("test", "��ʼ��������ѯ");
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
			Log.d("test", "����code��ѯ" + code);
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO �Զ����ɵķ������
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCityResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountriesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				Log.d("test", "result=false������δȡ��ֵ");
				if (result) {
					// ͨ��runOnUiThread�����ص����̴߳����߼�
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO �Զ����ɵķ������
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
					Log.d("test", "true");
				}
			}

			@Override
			public void onError(Exception e) {
				// ͨ��runOnUithread�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO �Զ����ɵķ������
						closeProgressDialog();
						Log.d("test", "error");
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��",
								Toast.LENGTH_SHORT).show();

					}
				});
			}
		});

	}

	// ��ʾ���ȶԻ���
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	// �رս��ȶԻ���
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	// ����back���������ݵ�ǰ�������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳�
	@Override
	public void onBackPressed() {
		// TODO �Զ����ɵķ������
		if (currentLevel == LEVEL_COUNTRY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}

	}
}
