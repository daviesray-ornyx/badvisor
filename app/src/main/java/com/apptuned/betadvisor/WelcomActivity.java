package com.apptuned.betadvisor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WelcomActivity extends AppCompatActivity {

    private static final String APP_INITIALIZED = "Application_Initialized";
    private static final String COUNTRY_CODE = "Country_Code";
    private static final String COUNTRY_CODE_WITH_PLUS = "Country_Code_plus";
    private static final String COUNTRY_NAME = "Country_Name";

    private String countryCode, countryCodeWithPlus, countryName;

    private static SharedPreferences spConfig;
    public CountryCodePicker ccp;
    private Button btnGetStarted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);

        spConfig = getSharedPreferences("com.apptuned.betadvisor.Config", this.MODE_PRIVATE);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        btnGetStarted = (Button) findViewById(R.id.btn_getStarted);
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Check if country is selected
                // If country is not selected, show an alert for country to be selected
                if(ccp.getSelectedCountryName() == null || ccp.getSelectedCountryName().length() < 1 ){
                    // Country is not selected
                    Toast.makeText(getApplicationContext(), "Select Country to Proceed.", Toast.LENGTH_SHORT).show();
                }
                else{
                    // If country is selected
                    // TODO Flag app as initialized and take user to the accounts page
                    // TODO get COUNTRY_CODE, COUNTRY_CODE_WITTH_PLUS, COUNTRY_NAME
                    countryCode = ccp.getSelectedCountryCode();
                    countryCodeWithPlus = ccp.getSelectedCountryCodeWithPlus();
                    countryName = ccp.getSelectedCountryName();

                    SharedPreferences.Editor editor  = spConfig.edit();
                    editor.putString(COUNTRY_CODE, countryCode);
                    editor.putString(COUNTRY_CODE_WITH_PLUS, countryCodeWithPlus);
                    editor.putString(COUNTRY_NAME, countryName);
                    editor.putBoolean(APP_INITIALIZED, true);
                    editor.commit();

                    moveToNextActivity();
                }

            }
        });

        if(spConfig.getBoolean(APP_INITIALIZED, false))
            moveToNextActivity();

    }

    public void moveToNextActivity(){
        Intent intent = new Intent(getApplicationContext(), BetInvestorActivity.class);
        startActivity(intent);
    }
}
