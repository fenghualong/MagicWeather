package com.xidian.flyhigher.magicweather;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xidian.flyhigher.magicweather.db.SelectCity;

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


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(ItemListActivity.this,Main3Activity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.item_list);
//        recyclerView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(ItemListActivity.this, "aaaa", Toast.LENGTH_SHORT).show();
//                String[] mMenu = {"删除这一项","取消"};
//                AlertDialog.Builder builder = new AlertDialog.Builder(ItemListActivity.this);
//                builder.setTitle("提示");
//                builder.setItems(mMenu, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which){
//                            case 0 :
//
//                                break;
//                            case 1:
//                                //do nothings
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                });
//                builder.setNegativeButton("Cancel",null);
//                return false;
//            }
//        });
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
            Log.i("ItemListActivity","id: " + selectCity.getId());
        }
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
            holder.mIdView.setText(position + 1 + "");
            holder.mContentView.setText(mCitys.get(position));
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    String[] mMenu = {"删除这一项"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemListActivity.this);
                    builder.setTitle("提示");
                    builder.setItems(mMenu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0 :
                                    DataSupport.deleteAll(SelectCity.class,"cityName = ?", mCitys.get(position));
                                    citysList.remove(position);
                                    setupRecyclerView((RecyclerView) recyclerView);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel",null);
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

//            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    DataSupport.deleteAll(SelectCity.class,"cityName = ?", mCitys.get(position));
//                    citysList.remove(position);
//                    setupRecyclerView((RecyclerView) recyclerView);
//                    return false;
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return mCitys.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

//            @Override
//            public String toString() {
//                return super.toString() + " '" + mContentView.getText() + "'";
//            }
        }
    }
}
