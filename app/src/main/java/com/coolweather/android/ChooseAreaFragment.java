package com.coolweather.android;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.coolweather.android.database.WeatherSQLHelper;
import com.coolweather.android.model.City;
import com.coolweather.android.model.County;
import com.coolweather.android.model.Province;
import com.coolweather.android.util.HttpUtility;
import com.coolweather.android.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private static final String TAG = "ChooseAreaFragment";

    private WeatherSQLHelper sqlHelper;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;

    private Province selectedProvince;

    private  City selectedCity;

    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        provinceList = new ArrayList<>();
        countyList = new ArrayList<>();
        cityList = new ArrayList<>();

        sqlHelper = new WeatherSQLHelper(getContext(), "Weather.db", null, 4);

        View view = inflater.inflate(R.layout.choose_area, container, false);

        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "碎片绑定活动");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    Log.d(TAG, "当前状态: "+currentLevel);
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(position).getWeatherId();

                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });

        queryProvinces();
    }

    private void queryProvinces() {
        Log.d(TAG, "初始化省");

        titleText.setText("中国");

        backButton.setVisibility(View.GONE);

        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        Log.d(TAG, "数据库查询省");
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String provinceName = cursor.getString(cursor.getColumnIndex("provinceName"));
                @SuppressLint("Range") int provinceCode = cursor.getInt(cursor.getColumnIndex("provinceCode"));
                province.setId(id);
                province.setProvinceName(provinceName);
                province.setProvinceCode(provinceCode);
                provinceList.add(province);
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d(TAG, "数据库查询省结束");
        if (provinceList.size() > 0) {

            dataList.clear();

            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);

            currentLevel = LEVEL_PROVINCE;

        } else {
            Log.d(TAG, "网络请求\"http://guolin.tech/api/china\"");
            queryFromServer("province");
        }
    }

    private void queryCities() {

        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList.clear();

        Log.d(TAG, "数据库查询市");
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM City WHERE provinceId = ?", new String[] { String.valueOf(selectedProvince.getProvinceCode()) });
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String cityName = cursor.getString(cursor.getColumnIndex("cityName"));
                @SuppressLint("Range") int cityCode = cursor.getInt(cursor.getColumnIndex("cityCode"));
                @SuppressLint("Range") int provinceId = cursor.getInt(cursor.getColumnIndex("provinceId"));

                city.setId(id);
                city.setCityName(cityName);
                city.setCityCode(cityCode);
                city.setProvinceId(provinceId);
                cityList.add(city);

            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d(TAG, "数据库查询市结束");
        if (cityList.size() > 0) {

            dataList.clear();

            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);

            currentLevel = LEVEL_CITY;
        } else {
                int provinceCode = selectedProvince.getProvinceCode();
                String address = "http://guolin.tech/api/china/" + provinceCode;
                queryFromServer("city", provinceCode);
        }
    }

    private void queryCounties() {

        titleText.setText(selectedCity.getCityName());
        countyList.clear();
        backButton.setVisibility(View.VISIBLE);

        Log.d(TAG, "数据库查询县");
        SQLiteDatabase db = sqlHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM County WHERE cityId = ?", new String[] { String.valueOf(selectedCity.getCityCode()) });
        Log.d(TAG, "状态"+cursor.moveToFirst());
        if (cursor.moveToFirst()) {
            Log.d(TAG, "开始循环");
            do {
                County county = new County();
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String countyName = cursor.getString(cursor.getColumnIndex("countyName"));
                @SuppressLint("Range") String weatherId = cursor.getString(cursor.getColumnIndex("weatherId"));
                @SuppressLint("Range") int cityId = cursor.getInt(cursor.getColumnIndex("cityId"));

                county.setCityId(id);
                county.setCountyName(countyName);
                county.setWeatherId(weatherId);
                county.setCityId(cityId);
                countyList.add(county);
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d(TAG, "数据库查询县结束, " + "列表长度: " + countyList.size());
        if (countyList.size() > 0) {

            dataList.clear();

            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);

            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer("county", provinceCode, cityCode);
        }
    }

    //todo:处理回调部分
    private void queryFromServer(String type) {
        showProgressDialog();
        HttpUtility.sendRetrofitRequest("http://guolin.tech/api/");
        Call<ResponseBody> call = HttpUtility.getDataByRequest().getProvinces();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String responseText = null;
                Log.d(TAG, "开始解析返回数据");
                try {
                    if (response.body() != null) {
                        responseText = response.body().string();
                        Log.d(TAG, responseText);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "处理返回数据");
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText, sqlHelper);
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryProvinces();
                            closeProgressDialog();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void queryFromServer(String type, int code) {
        Log.d(TAG, "网络查询市级数据");
        showProgressDialog();
        HttpUtility.sendRetrofitRequest("http://guolin.tech/api/china/");
        Call<ResponseBody> call = HttpUtility.getDataByRequest().getCities(code);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseText = null;
                Log.d(TAG, "开始解析返回数据");
                try {
                    if (response.body() != null) {
                        responseText = response.body().string();
                        Log.d(TAG, responseText);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean result = false;
                if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, code, sqlHelper);
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryCities();
                            closeProgressDialog();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                closeProgressDialog();
                Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void queryFromServer(String type, int code1, int code2) {
        Log.d(TAG, "网络查询县级数据" + "cityid: " + code1 + "/" + "countyid: " + code2);
        showProgressDialog();
        HttpUtility.sendRetrofitRequest("http://guolin.tech/api/china/");
        Call<ResponseBody> call = HttpUtility.getDataByRequest().getCounties(code1, code2);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String responseText = null;
                Log.d(TAG, "开始解析返回数据");
                try {
                    if (response.body() != null) {
                        responseText = response.body().string();
                        Log.d(TAG, responseText);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean result = false;
                if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, code2, sqlHelper);
                }
                if (result) {
                    queryCounties();
                    closeProgressDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                closeProgressDialog();
                Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("光速加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
