package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;

//�ṩ���ڽ����ʹ������������ʡ���м����ؼ����ݵ����������������������ö��ŷָ�ٰ������߷ָ������Ž������������������õ�ʵ�����У�������CoolWeatherDB�е�����save���������ݴ洢����Ӧ����
public class Utility {

	// �����ʹ�����������ص�ʡ������
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] arrayStrings = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(arrayStrings[0]);
					province.setProvinceName(arrayStrings[1]);
					// �������������ݴ洢��province��
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	// �����ʹ�����������ص��м�����
	public static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					// ���������������ݴ洢��city��
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	// �����ʹ�����������ص��ؼ�����
	public static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCountries = response.split(",");
			if (allCountries != null && allCountries.length > 0) {
				for (String c : allCountries) {
					String[] array = c.split("\\|");
					Country country = new Country();
					country.setCountryCode(array[0]);
					country.setCountryName(array[1]);
					country.setCityId(cityId);
					// ���������������ݴ洢��country��
					coolWeatherDB.saveCountry(country);
				}
				return true;
			}
		}
		return false;
	}
}
