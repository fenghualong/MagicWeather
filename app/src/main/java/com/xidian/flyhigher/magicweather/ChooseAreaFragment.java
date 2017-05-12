package com.xidian.flyhigher.magicweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.xidian.flyhigher.magicweather.db.AllPCC;
import com.xidian.flyhigher.magicweather.db.SelectCity;
import com.xidian.flyhigher.magicweather.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {

    private static final String TAG = "ChooseAreaFragment";

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    private List<AllPCC> allPCCs;

    /**
     * 省列表
     */
    //private List<Province> provinceList;
    private List<String> provincesList = new ArrayList<>();;

    /**
     * 市列表
     */
    //private List<City> cityList;
    private List<String> citysList = new ArrayList<>();;

    /**
     * 县列表
     */
    //private List<County> countyList;
    private List<String> countysList = new ArrayList<>();;

    /**
     * 选中的省份
     */
    //private Province selectedProvince;
    private String sselectedProvince;

    /**
     * 选中的城市
     */
    //private City selectedCity;
    private String sselectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    sselectedProvince = provincesList.get(position);
                    Log.i(TAG,"sselectedProvince" + sselectedProvince);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    sselectedCity = citysList.get(position);
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY) {
                    String cityName = countysList.get(position);
                    List<SelectCity> citys = DataSupport.where("cityName = ?",cityName).find(SelectCity.class);
                    if(citys.isEmpty()){
                        Log.i(TAG,"citys size is: " + citys.size());
                        SelectCity selectCity = new SelectCity();
                        selectCity.setCityName(cityName);
                        selectCity.save();
                        Intent intent = new Intent(getActivity(),ItemListActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getContext(),"该城市已存在",Toast.LENGTH_SHORT).show();
                        for (SelectCity selectCity : citys) {
                            Log.i(TAG, selectCity.getCityName() +": id: " + selectCity.getId());
                        }
                    }

                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
//    private void queryProvinces() {
//        titleText.setText("中国");
//        backButton.setVisibility(View.GONE);
//        provinceList = DataSupport.findAll(Province.class);
//        if (provinceList.size() > 0) {
//            dataList.clear();
//            for (Province province : provinceList) {
//                dataList.add(province.getProvinceName());
//            }
//            adapter.notifyDataSetChanged();
//            listView.setSelection(0);
//            currentLevel = LEVEL_PROVINCE;
//        } else {
//            String address = "http://guolin.tech/api/china";
//            queryFromServer(address, "province");
//        }
//    }
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        allPCCs = DataSupport.select("province").find(AllPCC.class);
        if (allPCCs.size() > 0) {
            dataList.clear();
            provincesList.clear();
            String province = "";
            for (AllPCC allPCC : allPCCs) {
                if(!province.equals(allPCC.getProvince())){
                    dataList.add(allPCC.getProvince());
                    provincesList.add(allPCC.getProvince());
                    province = allPCC.getProvince();
                }
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer();
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
//    private void queryCities() {
//        titleText.setText(selectedProvince.getProvinceName());
//        backButton.setVisibility(View.VISIBLE);
//        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
//        if (cityList.size() > 0) {
//            dataList.clear();
//            for (City city : cityList) {
//                dataList.add(city.getCityName());
//            }
//            adapter.notifyDataSetChanged();
//            listView.setSelection(0);
//            currentLevel = LEVEL_CITY;
//        }
//    }
    private void queryCities() {
        titleText.setText(sselectedProvince);
        backButton.setVisibility(View.VISIBLE);
        allPCCs = DataSupport.select("city").where("province = ?",sselectedProvince).find(AllPCC.class);
        if (allPCCs.size() > 0) {
            dataList.clear();
            citysList.clear();
            String city = "";
            for (AllPCC allPCC : allPCCs) {
                if(!city.equals(allPCC.getCity())){
                    dataList.add(allPCC.getCity());
                    citysList.add(allPCC.getCity());
                    city = allPCC.getCity();
                }
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        titleText.setText(sselectedCity);
        backButton.setVisibility(View.VISIBLE);
        allPCCs = DataSupport.select("county").where("province = ? and city = ?",sselectedProvince,sselectedCity).find(AllPCC.class);
        if (allPCCs.size() > 0) {
            dataList.clear();
            countysList.clear();
            for (AllPCC allPCC : allPCCs) {
                dataList.add(allPCC.getCounty());
                countysList.add(allPCC.getCounty());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据。
     */
//    private void queryFromServer(String address, final String type) {
//        showProgressDialog();
//        HttpUtil.sendOkHttpRequest(address, new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String responseText = response.body().string();
//                boolean result = false;
//                if ("province".equals(type)) {
//                    result = Utility.handleProvinceResponse(responseText);
//                } else if ("city".equals(type)) {
//                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
//                } else if ("county".equals(type)) {
//                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
//                }
//                if (result) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            closeProgressDialog();
//                            if ("province".equals(type)) {
//                                queryProvinces();
//                            } else if ("city".equals(type)) {
//                                queryCities();
//                            } else if ("county".equals(type)) {
//                                queryCounties();
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // 通过runOnUiThread()方法回到主线程处理逻辑
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        closeProgressDialog();
//                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }

    private void queryFromServer() {
        String weatherUrl = "https://cdn.heweather.com/china-city-list.json";
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.i("ChooseAreaFragment","queryFromServer()" + responseText);
                try{
                    JSONArray jsonArray = new JSONArray(responseText);
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String provinceZh = jsonObject.getString("provinceZh");
                        String leaderZh = jsonObject.getString("leaderZh");
                        String cityZh = jsonObject.getString("cityZh");
                        AllPCC allPCC = new AllPCC();
                        allPCC.setProvince(provinceZh);
                        allPCC.setCity(leaderZh);
                        allPCC.setCounty(cityZh);
                        allPCC.save();
                        Log.i("ChooseAreaFragment",""+i);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            closeProgressDialog();
                            queryProvinces();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
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

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }



}
