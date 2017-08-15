package com.xidian.flyhigher.magicweather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.xidian.flyhigher.magicweather.service.AlarmService;

import static com.xidian.flyhigher.magicweather.ContractListActivity.PHONE_NUMBER_STRING;

public class EditsActivity extends AppCompatActivity {

    private String LOG_LAG = EditsActivity.class.getSimpleName();

    private SharedPreferences.Editor editor;

    public static final String EDITS_STRING = "edits";

    private Spinner mSuggestionOneSpinner;

    private Spinner mSuggestionTwoSpinner;

    public static final int SUGGESTION_ONE = 1;

    public static final int SUGGESTION_TWO = 2;

    public static final String SUGGESTION_ONE_STRING = "suggestion_one";

    public static final String SUGGESTION_TWO_STRING = "suggestion_two";

    private Spinner mUpdateFrequenceSpinner;

    public static final String UPDATE_FREQUENCE_STRING = "update_frequence";

    public static final int ONE_HOUR = 1;

    public static final int TWO_HOUR = 2;

    public static final int SIX_HOUR = 6;

    public static final int TWELVE_HOUR = 12;

    public static final int ONE_DAY = 24;

    public static final int NOT_UPDATE = 0;

    private CheckBox mNotifyBank;

    public static final String NOTIFY_BANK_STRING = "notify_bank";

    public static final int NOTIFY = 100;

    public static final int NOT_NOTIFY = 101;

    private CheckBox mSmsTime;

    public static final String SMS_TIME_STRING = "sms_bank";

    private CheckBox mSmsWeatherChange;

    public static final String SMS_WEATHER_CHANGE_STRING = "sms_weather_change";

    private LinearLayout infomationSMS_Container;

    private LinearLayout weatherSMS_Container;

    private EditText telephoneEditText;

    private EditText massageEditText;

    private EditText citySMS_editText;

    private EditText weatherSMS_editText;

    public String timeOrChange;

    public String CITY_SMS_STRING = "abc";

    private Button sureButton;

    private  EditText timingSending;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edits);

        sureButton = (Button) findViewById(R.id.sure_button);

        weatherSMS_Container = (LinearLayout) findViewById(R.id.weather_sms_container);

        infomationSMS_Container = (LinearLayout) findViewById(R.id.infomation_sms_container);

        telephoneEditText = (EditText) findViewById(R.id.telephone);

        massageEditText = (EditText) findViewById(R.id.massage);

        citySMS_editText = (EditText) findViewById(R.id.sms_city);

        weatherSMS_editText = (EditText) findViewById(R.id.weather_sms_edit_text);

        editor = getSharedPreferences(EDITS_STRING, MODE_PRIVATE).edit();

        mSuggestionOneSpinner = (Spinner) findViewById(R.id.spinner_suggestion1);

        mSuggestionTwoSpinner = (Spinner) findViewById(R.id.spinner_suggestion2);

        mUpdateFrequenceSpinner = (Spinner) findViewById(R.id.update_frequence_spinner);

        mNotifyBank = (CheckBox) findViewById(R.id.notify_bank);

        mSmsTime = (CheckBox) findViewById(R.id.sms_time);

        mSmsWeatherChange = (CheckBox) findViewById(R.id.sms_weather_change);

        timingSending = (EditText) findViewById(R.id.timing_sending);

        setupSuggestionSpinner(mSuggestionOneSpinner, SUGGESTION_ONE);

        setupSuggestionSpinner(mSuggestionTwoSpinner, SUGGESTION_TWO);

        setupUpdateFrequenceSpinner();

        mNotifyBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = mNotifyBank.isChecked();
                Log.i(LOG_LAG, "mNotifyBank.isChecked: " + checked);
                if (checked) {
                    editor.putInt(NOTIFY_BANK_STRING, NOTIFY);
                } else {
                    editor.putInt(NOTIFY_BANK_STRING, NOT_NOTIFY);
                }
                editor.apply();
            }
        });

        mSmsTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = mSmsTime.isChecked();
                Log.i(LOG_LAG, "mNotifyBank.isChecked: " + checked);
                Intent intent = new Intent(EditsActivity.this, AlarmService.class);

                if (checked) {
                    if (ContextCompat.checkSelfPermission(EditsActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditsActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                    }


                    editor.putInt(SMS_TIME_STRING, NOTIFY);
                    editor.putInt("timeOrChange", 1);
                    infomationSMS_Container.setVisibility(View.VISIBLE);
                    weatherSMS_Container.setVisibility(View.VISIBLE);
                    timeOrChange = "SmsTime";
                    SharedPreferences prefTime = getSharedPreferences("SmsTime", MODE_PRIVATE);
                    String number = prefTime.getString(PHONE_NUMBER_STRING, null);
                    if (number != null) {
                        telephoneEditText.setText(number);
                    }
                } else {
                    stopService(intent);
                    editor.putInt(SMS_TIME_STRING, NOT_NOTIFY);
                    infomationSMS_Container.setVisibility(View.GONE);
                    editor.putInt("timeOrChange", 0);
                }
                editor.apply();
            }
        });



        mSmsWeatherChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = mSmsWeatherChange.isChecked();
                Log.i(LOG_LAG, "mNotifyBank.isChecked: " + checked);
                if (checked) {
                    if (ContextCompat.checkSelfPermission(EditsActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditsActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                    }

                    editor.putInt(SMS_WEATHER_CHANGE_STRING, NOTIFY);
                    infomationSMS_Container.setVisibility(View.VISIBLE);
                    weatherSMS_Container.setVisibility(View.GONE);
                    timeOrChange = "SmsWeatherChange";
                    SharedPreferences prefTime = getSharedPreferences("SmsWeatherChange", MODE_PRIVATE);
                    String number = prefTime.getString(PHONE_NUMBER_STRING, null);
                    if (number != null) {
                        telephoneEditText.setText(number);
                    }
                    editor.putInt("timeOrChange", 2);
                } else {
                    editor.putInt(SMS_WEATHER_CHANGE_STRING, NOT_NOTIFY);
                    infomationSMS_Container.setVisibility(View.GONE);
                    editor.putInt("timeOrChange", 0);
                }
                editor.apply();
            }
        });


        telephoneEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(EditsActivity.this, ContractListActivity.class);
                SharedPreferences pref = getSharedPreferences(EDITS_STRING, MODE_PRIVATE);
                int timeorchangeInt = pref.getInt("timeOrChange", 0);
                if (timeorchangeInt == 1) {
                    timeOrChange = "SmsTime";
                } else if (timeorchangeInt == 2) {
                    timeOrChange = "SmsWeatherChange";
                }
                intent.putExtra("phone", timeOrChange);
                startActivity(intent);
                finish();
                return true;
            }
        });

        citySMS_editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(CITY_SMS_STRING, MODE_PRIVATE).edit();
                editor.putString("city_sms", "1");
                SharedPreferences pref = getSharedPreferences(EDITS_STRING, MODE_PRIVATE);
                int timeorchangeInt = pref.getInt("timeOrChange", 0);
                if (timeorchangeInt == 1) {
                    timeOrChange = "SmsTime";
                } else if (timeorchangeInt == 2) {
                    timeOrChange = "SmsWeatherChange";
                }
                editor.putString("city", timeOrChange);
                editor.apply();
                Intent intent = new Intent(EditsActivity.this, Main3Activity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });

        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences(EDITS_STRING, MODE_PRIVATE);
                int timeorchange = pref.getInt("timeOrChange", 0);

                if (timeorchange == 1) {
                    timeOrChange = "SmsTime";
                    SharedPreferences.Editor editor = getSharedPreferences(timeOrChange, MODE_PRIVATE).edit();
                    editor.putString(PHONE_NUMBER_STRING, telephoneEditText.getText().toString().trim());
                    editor.putString("city_sms", citySMS_editText.getText().toString().trim());
                    editor.putString("massage", massageEditText.getText().toString().trim());
                    editor.putString("weather_sms", weatherSMS_editText.getText().toString().trim());
                    editor.putString("timing_sending",timingSending.getText().toString().trim());

                    editor.apply();
                    Intent intent = new Intent(EditsActivity.this, AlarmService.class);
                    startService(intent);
                }
                if (timeorchange == 2) {
                    timeOrChange = "SmsWeatherChange";
                    SharedPreferences.Editor editor = getSharedPreferences(timeOrChange, MODE_PRIVATE).edit();
                    editor.putString(PHONE_NUMBER_STRING, telephoneEditText.getText().toString().trim());
                    editor.putString("city_sms", citySMS_editText.getText().toString().trim());
                    editor.putString("massage", massageEditText.getText().toString().trim());
                    editor.apply();
                }
                editor.putInt("timeOrChange", 0);
                editor.apply();
                infomationSMS_Container.setVisibility(View.GONE);
            }
        });


        initSetup();
    }


    private void initSetup() {
        SharedPreferences pref = getSharedPreferences(EDITS_STRING, MODE_PRIVATE);
        int suggestionOne = pref.getInt(SUGGESTION_ONE_STRING, 0);
        int suggestionTwo = pref.getInt(SUGGESTION_TWO_STRING, 0);
        subInitSuggestion(mSuggestionOneSpinner, suggestionOne);
        subInitSuggestion(mSuggestionTwoSpinner, suggestionTwo);

        int update = pref.getInt(UPDATE_FREQUENCE_STRING, ONE_HOUR);
        switch (update) {
            case ONE_HOUR:
                mUpdateFrequenceSpinner.setSelection(1);
                break;
            case TWO_HOUR:
                mUpdateFrequenceSpinner.setSelection(2);
                break;
            case SIX_HOUR:
                mUpdateFrequenceSpinner.setSelection(3);
                break;
            case TWELVE_HOUR:
                mUpdateFrequenceSpinner.setSelection(4);
                break;
            case ONE_DAY:
                mUpdateFrequenceSpinner.setSelection(5);
                break;
            default:
                mUpdateFrequenceSpinner.setSelection(0);
                break;
        }

        int notify_bank = pref.getInt(NOTIFY_BANK_STRING, NOT_NOTIFY);
        if (notify_bank == NOTIFY) {
            mNotifyBank.setChecked(true);
        } else {
            mNotifyBank.setChecked(false);
        }

        int notify_sms_time = pref.getInt(SMS_TIME_STRING, NOT_NOTIFY);
        if (notify_sms_time == NOTIFY) {
            mSmsTime.setChecked(true);
        } else {
            mSmsTime.setChecked(false);
        }

        int notify_sms_change = pref.getInt(SMS_WEATHER_CHANGE_STRING, NOT_NOTIFY);
        if (notify_sms_change == NOTIFY) {
            mSmsWeatherChange.setChecked(true);
        } else {
            mSmsWeatherChange.setChecked(false);
        }

        int timeorchange = pref.getInt("timeOrChange", 0);
        if (timeorchange == 0) {
            infomationSMS_Container.setVisibility(View.GONE);
        } else {
            infomationSMS_Container.setVisibility(View.VISIBLE);
            if (timeorchange == 1) {
                SharedPreferences prefTime = getSharedPreferences("SmsTime", MODE_PRIVATE);
                String number = prefTime.getString(PHONE_NUMBER_STRING, null);
                String city = prefTime.getString("city_sms", null);
                if (number != null) {
                    telephoneEditText.setText(number);
                }
                if (city != null) {
                    citySMS_editText.setText(city);
                }
            }
            if (timeorchange == 2) {
                weatherSMS_Container.setVisibility(View.GONE);
                SharedPreferences prefChange = getSharedPreferences("SmsWeatherChange", MODE_PRIVATE);
                String number = prefChange.getString(PHONE_NUMBER_STRING, null);
                String city = prefChange.getString("city_sms", null);
                if (number != null) {
                    telephoneEditText.setText(number);
                }
                if (city != null) {
                    citySMS_editText.setText(city);
                }
            }
        }
    }

    /******************************************
     *
     * @param mSuggestionSpinner
     * @param flag   建议指数的代表数字
     */

    private void subInitSuggestion(Spinner mSuggestionSpinner, int flag) {
        switch (flag) {
            case 1:
                mSuggestionSpinner.setSelection(1);
                break;
            case 2:
                mSuggestionSpinner.setSelection(2);
                break;
            case 3:
                mSuggestionSpinner.setSelection(3);
                break;
            case 4:
                mSuggestionSpinner.setSelection(4);
                break;
            case 5:
                mSuggestionSpinner.setSelection(5);
                break;
            case 6:
                mSuggestionSpinner.setSelection(6);
                break;
//            case 7:
//                mSuggestionSpinner.setSelection(7);
//                break;
            default:
                mSuggestionSpinner.setSelection(0);
                break;
        }
    }

    /******************************************************
     *
     * @param mSuggestionSpinner
     * @param spinnerId   建议指数的哪一个
     */

    private void setupSuggestionSpinner(Spinner mSuggestionSpinner, final int spinnerId) {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_suggestions_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSuggestionSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mSuggestionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
//                    if (selection.equals(getString(R.string.air))) {
//                        suggestionSpinnerOneOrTwo(spinnerId, 1);
//                    } else
                    if (selection.equals(getString(R.string.cw))) {
                        suggestionSpinnerOneOrTwo(spinnerId, 1);
                    } else if (selection.equals(getString(R.string.drsg))) {
                        suggestionSpinnerOneOrTwo(spinnerId, 2);
                    } else if (selection.equals(getString(R.string.comf))) {
                        suggestionSpinnerOneOrTwo(spinnerId, 3);
                    } else if (selection.equals(getString(R.string.uv))) {
                        suggestionSpinnerOneOrTwo(spinnerId, 4);
                    } else if (selection.equals(getString(R.string.flu))) {
                        suggestionSpinnerOneOrTwo(spinnerId, 5);
                    } else if (selection.equals(getString(R.string.sport))) {
                        suggestionSpinnerOneOrTwo(spinnerId, 6);
                    } else if (selection.equals(getString(R.string.unshow))) {
                        suggestionSpinnerOneOrTwo(spinnerId, 0);
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                suggestionSpinnerOneOrTwo(spinnerId, 1);
            }
        });
    }

    /**********************************
     *
     * @param spinnerId 那个spinner(one two)
     * @param flag 第几个选项（1 2 3 4  5 6 7 8）
     */

    private void suggestionSpinnerOneOrTwo(int spinnerId, int flag) {
        //SharedPreferences.Editor editor = getSharedPreferences("edits", MODE_PRIVATE).edit();
        if (spinnerId == SUGGESTION_ONE) {
            editor.putInt(SUGGESTION_ONE_STRING, flag);
        } else {
            editor.putInt(SUGGESTION_TWO_STRING, flag);
        }
        editor.apply();
    }

    private void setupUpdateFrequenceSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_update_frequence_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mUpdateFrequenceSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mUpdateFrequenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                //SharedPreferences.Editor editor = getSharedPreferences("edits", MODE_PRIVATE).edit();
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.one_hour))) {
                        editor.putInt(UPDATE_FREQUENCE_STRING, ONE_HOUR);
                    } else if (selection.equals(getString(R.string.two_hour))) {
                        editor.putInt(UPDATE_FREQUENCE_STRING, TWO_HOUR);
                    } else if (selection.equals(getString(R.string.six_hour))) {
                        editor.putInt(UPDATE_FREQUENCE_STRING, SIX_HOUR);
                    } else if (selection.equals(getString(R.string.twelve_hour))) {
                        editor.putInt(UPDATE_FREQUENCE_STRING, TWELVE_HOUR);
                    } else if (selection.equals(getString(R.string.one_day))) {
                        editor.putInt(UPDATE_FREQUENCE_STRING, ONE_DAY);
                    } else if (selection.equals(getString(R.string.not_update))) {
                        editor.putInt(UPDATE_FREQUENCE_STRING, NOT_UPDATE);
                    }
                }
                editor.apply();
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                SharedPreferences.Editor editor = getSharedPreferences(EDITS_STRING, MODE_PRIVATE).edit();
                editor.putInt(UPDATE_FREQUENCE_STRING, ONE_HOUR);
                editor.apply();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("mCount_1", timeOrChange);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        timeOrChange = savedInstanceState.getString("mCount_1");
    }


}
