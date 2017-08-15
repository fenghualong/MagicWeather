package com.xidian.flyhigher.magicweather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ContractListActivity extends AppCompatActivity {

    public static final String PHONE_NUMBER_STRING = "phone_number";

    private String LOG_TAG = ContractListActivity.class.getSimpleName();

    private List<Contract> contractList = new ArrayList<>();

    private ListView contractListView;

    private ArrayAdapter<Contract> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_list);

        Intent intent = getIntent();
        final String data = intent.getStringExtra("phone");

        contractListView = (ListView)findViewById(R.id.contract_list_view);

        adapter = new ContractAdapter(this, R.layout.contract_list_item, contractList);

        contractListView.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            readContacts();
        }

        contractListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences(data, MODE_PRIVATE).edit();
                editor.putString(PHONE_NUMBER_STRING, adapter.getItem(position).getNumber());
                editor.apply();
                Intent intent = new Intent(ContractListActivity.this,EditsActivity.class);
                //intent.putExtra("phone",adapter.getItem(position).getNumber());
                startActivity(intent);
                //Log.i(LOG_TAG,adapter.getItem(position).getName() + adapter.getItem(position).getNumber());
                //send1(adapter.getItem(position),"白天天气晴好，明媚的阳光在给您带来好心情的同时，也会使您感到有些热，不很舒适。");
            }
        });
    }

    private void readContacts() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Contract contract = new Contract(displayName,number);
                    contractList.add(contract);

                }
                adapter.notifyDataSetChanged();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    public class Contract{
        private String name;

        private String number;

        public Contract(String name,String number){
            this.name = name;
            this.number = number;
        }

        public String getName(){
            return name;
        }

        public  String getNumber(){
            return number;
        }
    }

    public class ContractAdapter extends ArrayAdapter<Contract>{

        private int resourceId;

        public ContractAdapter(Context context,int resourceId, List<Contract> objects){
            super(context,resourceId,objects);
            this.resourceId = resourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Contract contract = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            TextView name = (TextView)view.findViewById(R.id.name_list_item);
            TextView number = (TextView)view.findViewById(R.id.number_list_item);
            name.setText(contract.getName());
            number.setText(contract.getNumber());
            return view;
        }
    }
}
