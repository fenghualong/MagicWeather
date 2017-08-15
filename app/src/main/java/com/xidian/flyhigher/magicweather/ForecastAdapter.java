package com.xidian.flyhigher.magicweather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xidian.flyhigher.magicweather.gson.Forecast;

import java.util.List;


/**</>
 * Created by little cherry on 2017/3/17.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder>{

    private List<Forecast> mForecastList;
    private int mItemWidth = 0;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View weatherView;
        ImageView weatherImage;
        TextView date;
        TextView min_max_Temp;
        LinearLayout linearLayout;
        public ViewHolder(View view,int nWidth) {
            super(view);
            weatherView = view;
            weatherImage = (ImageView) view.findViewById(R.id.weather_image);
            date = (TextView) view.findViewById(R.id.weather_date);
            min_max_Temp = (TextView) view.findViewById(R.id.min_max_temperature);

            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
            linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(nWidth,-1));

        }

    }
    public void SetItemWidth(int nWidth){mItemWidth = nWidth;}

    public ForecastAdapter(List<Forecast> forecastList , Context context){
        mForecastList = forecastList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_item,parent,false);
        final ViewHolder holder = new ViewHolder(view,mItemWidth);

//        holder.weatherView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                int position = holder.getAdapterPosition();
//                Forecast forecast = mForecastList.get(position);
//                Toast.makeText(view.getContext(),"You clicked view"+ forecast.date,Toast.LENGTH_SHORT).show();
//            }
//
//        });
//        holder.weatherImage.setOnClickListener( new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                int position = holder.getAdapterPosition();
//                Forecast forecast = mForecastList.get(position);
//                Toast.makeText(view.getContext(),"You clicked image"+ forecast.temperature,Toast.LENGTH_SHORT).show();
//            }
//        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position) {

        Forecast forecast = mForecastList.get(position + 1);
        String weatherPic = "http://files.heweather.com/cond_icon/"+ forecast.more.info_code +".png";
        Glide.with(context).load(weatherPic).into(holder.weatherImage);
        //holder.weatherImage.setImageResource(R.drawable.weather_rain);
        holder.date.setText(forecast.date);
        holder.min_max_Temp.setText(forecast.temperature.max +"/"
                + forecast.temperature.min + "℃");
    }

    @Override
    public int getItemCount() {
        return mForecastList.size() - 1;
    }
}
