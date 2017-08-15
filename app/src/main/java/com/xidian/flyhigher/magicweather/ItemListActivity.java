package com.xidian.flyhigher.magicweather;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xidian.flyhigher.magicweather.db.SelectCity;
import com.xidian.flyhigher.magicweather.gson.Weather;
import com.xidian.flyhigher.magicweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private List<String> citysList = new ArrayList<>();

    private List<SelectCity> selectCityList;

    private View recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        initCity();

       // Log.i("ItemListActivity","id: " + citysList.size());
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());
        View item_activity_view = findViewById(R.id.item_activity);
        item_activity_view.setBackgroundResource(R.drawable.citylistbkgnd);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                if (citysList.size() < 6) {
                    Intent intent = new Intent(ItemListActivity.this,Main3Activity.class);
                    startActivity(intent);
                    finish();
                    //Log.i("ItemListActivity","id: " + citysList.size());
                }else {
                    Toast.makeText(ItemListActivity.this,"最多可选6个城市",Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(citysList));
    }

    private void initCity(){
        selectCityList = DataSupport.findAll(SelectCity.class);
        for (SelectCity selectCity : selectCityList) {
            citysList.add(selectCity.getCityName());
            //Log.i("ItemListActivity","id: " + selectCity.getId());
        }
    }

    private int itemIcoSelected(String weatherInfo) {
        int iIco = R.drawable.item_cloud;
        if (weatherInfo.indexOf("多云") != -1) {
            iIco = R.drawable.item_cloud;
        }
        if (weatherInfo.indexOf("雨") != -1) {
            iIco = R.drawable.item_rain;
        }
        if (weatherInfo.indexOf("晴") != -1) {
            iIco = R.drawable.item_sunshine;
        }
        if (weatherInfo.indexOf("雪") != -1) {
            iIco = R.drawable.item_snow;
        }
        return iIco;
    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<String> mCitys;

        public SimpleItemRecyclerViewAdapter(List<String> citys) {
            mCitys = citys;
        }



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            //holder.mIdView.setText(position + 1 + "");
            SharedPreferences preferences = getSharedPreferences("weather_data", MODE_PRIVATE);
            String weatherString = preferences.getString(citysList.get(position), null);
            Weather weather = Utility.handleWeatherResponse(weatherString);

            holder.mCityView.setText(mCitys.get(position));
            holder.mTemperatureView.setText(weather.now.temperature + "℃");
            String weatherInfo = weather.now.more.info;
            holder.mWeatherView.setText(weatherInfo);
            int itemIco = itemIcoSelected(weatherInfo);
            holder.mView.setBackgroundResource(itemIco);
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemListActivity.this);
                    builder.setTitle("删除这一项");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener(){
                        public void onClick( DialogInterface dialog, int id){

                            DataSupport.deleteAll(SelectCity.class, "cityName = ?",mCitys.get(position));
                            citysList.remove(position);
                            setupRecyclerView((RecyclerView) recyclerView);

                        }
                    });
                    builder.setNegativeButton("否",null);
                    builder.show();
                    return false;
                }
            });
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if(position != 0){
                        //DataSupport.deleteAll(SelectCity.class,"cityName = ?", mCitys.get(position));
                        //citysList.remove(position);
                        //setupRecyclerView((RecyclerView) recyclerView);
                    //}else{
                    //    Toast.makeText(ItemListActivity.this,"第一行不可删除",Toast.LENGTH_SHORT).show();
                    //}
                    Intent intent = new Intent(ItemListActivity.this,Main2Activity.class);
                    intent.putExtra("position",position);
                    startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return mCitys.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //public final TextView mIdView;
            public final TextView mCityView;
            public final TextView mTemperatureView;
            public final TextView mWeatherView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                //mIdView = (TextView) view.findViewById(R.id.id);
                mCityView = (TextView) view.findViewById(R.id.city_item);
                mTemperatureView = (TextView)view.findViewById(R.id.temperature_item);
                mWeatherView = (TextView)view.findViewById(R.id.weather_item);
            }

//            @Override
//            public String toString() {
//                return super.toString() + " '" + mContentView.getText() + "'";
//            }
        }
    }
}
