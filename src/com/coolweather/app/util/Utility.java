package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;

//提供用于解析和处理服务器返回省级市级和县级数据的三个方法，解析规则：先用逗号分割，再按单竖线分隔，接着将解析出来的数据设置到实力类中，最后调用CoolWeatherDB中的三个save方法将数据存储到相应表中
public class Utility {

	// 解析和处理服务器返回的省级数据
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
					// 解析出来的数据存储到province表
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	// 解析和处理服务器返回的市级数据
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
					// 将解析出来的数据存储到city表
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	// 解析和处理服务器返回的县级数据
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
					// 将解析出来的数据存储到country表
					coolWeatherDB.saveCountry(country);
				}
				return true;
			}
		}
		return false;
	}
}
