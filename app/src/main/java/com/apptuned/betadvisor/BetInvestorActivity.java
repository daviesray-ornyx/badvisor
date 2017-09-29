package com.apptuned.betadvisor;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.apptuned.betadvisor.JSONMatchParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

public class BetInvestorActivity extends AppCompatActivity {

    private static boolean syncMode = false;

    private static final String MATCHES_FILE = "matches.json";
    private static final String LAST_SYNC_DATETIME = "last_synced_date_time";

    private RotateAnimation rotateAnimation;

    private SharedPreferences spConfig;


    private SimpleDateFormat simpleDateFormat;

    private JSONFileHandler jsonFileHandler;
    private JSONMatchParser jsonMatchParser;
    private RecyclerView rvMatchList;
    private TextView tvMatchlistHeader;

    private Date lastSyncDateTime;

    private MenuItem miRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bet_investor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initRotationAnimator();

        tvMatchlistHeader = (TextView)findViewById(R.id.tv_matchlist_header);
        Date today = new Date();
        String todayDate = new SimpleDateFormat("dd-MM-yyyy").format(today);
        String toDate = new SimpleDateFormat("dd-MM-yyyy").format(today.getTime() + 3 * 24 * 60 * 60 * 1000);

        tvMatchlistHeader.setText("PREDICTIONS: " + todayDate + " to " + toDate);

        jsonFileHandler = new JSONFileHandler(this);
        jsonMatchParser = new JSONMatchParser(this);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        rvMatchList = (RecyclerView) findViewById(R.id.rv_match_list);
        // Setting a linera layour manager to the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvMatchList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvMatchList.getContext(),linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_recyclerview));
        rvMatchList.addItemDecoration(dividerItemDecoration);

        // TODO Check last_synced datetime value.. In shared preferences
        spConfig = getSharedPreferences("com.apptuned.betadvisor.Config", this.MODE_PRIVATE);
        String lastSyncString = spConfig.getString(LAST_SYNC_DATETIME, null);
        if(lastSyncString != null){
            try{
                lastSyncDateTime = simpleDateFormat.parse(lastSyncString);
                String todayString = simpleDateFormat.format(new Date());
                if(getDifferenceDays(lastSyncDateTime, simpleDateFormat.parse(todayString)) > 1)
                    syncMatches(true);
                else {
                    String jsonMatches = jsonFileHandler.readJSONFile(MATCHES_FILE);
                    try {
                        JSONArray jsonMatchesArray = new JSONArray(jsonMatches);
                        if(jsonMatchesArray == null || jsonMatchesArray.length() ==0)
                            syncMatches(true);
                        else
                            updateRecyclerView(jsonMatchesArray);
                    }catch (JSONException e){
                        syncMatches(true);
                    }
                }
            }catch (ParseException e){
                syncMatches(true);
            }
        }else {
            syncMatches(true);
        }

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // TODO Create a function to get tips
//                getBettingTips();
//                getBettingTips();
//            }
//        }, 5000); // 1000 x 60 for a minute
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.miRefresh:
                // Refresh code
                if(syncMode)
                    Toast.makeText(getApplicationContext(), "Sync in progress. Please wait.", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(getApplicationContext(), "Sync in progress.", Toast.LENGTH_SHORT).show();
                    syncMatches(false);
                }
                return true;
            case R.id.miShare:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "Check out the free Bet Advisor App at https://play.google.com/store/apps/details?id=com.jumia.android";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Bet Advisor app");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateRecyclerView(JSONArray jsonMatchesArray){
        ArrayList<Match> matchArrayList = new ArrayList<Match>();
        try{
            for (int i = 0; i < jsonMatchesArray.length(); i++) {
                JSONObject obj = jsonMatchesArray.getJSONObject(i);
                Match match = jsonMatchParser.parse(obj);
                if(match != null)
                    matchArrayList.add(match);
            }
            Collections.sort(matchArrayList);
            MatchListAdapter matchListAdapter = new MatchListAdapter(getApplicationContext(), matchArrayList);
            rvMatchList.setAdapter(matchListAdapter);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void syncMatches(boolean isManualSync){
        syncMode = true;

        RestAdapter.get("api/json_matchlist", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray){
                jsonFileHandler.writeJSONFile(MATCHES_FILE, responseArray.toString());
                updateRecyclerView(responseArray);
                //Update last sync date
                endRotationAnimator();
                syncMode = false;
                spConfig.edit().putString(LAST_SYNC_DATETIME, simpleDateFormat.format(new Date())).commit();
                Toast.makeText(getApplicationContext(), "Sync successful.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONObject responseObject = (JSONObject) response;
                try {
                    Toast.makeText(getApplicationContext(), responseObject.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                endRotationAnimator();
                syncMode = false;
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "Sync failed. Please try again.", Toast.LENGTH_SHORT).show();
                endRotationAnimator();
                syncMode = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                // Handle error retrieving token
                Toast.makeText(getApplicationContext(), "Sync failed. Please try again.", Toast.LENGTH_SHORT).show();
                endRotationAnimator();
                syncMode = false;
            }
        });
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private void initRotationAnimator(){
        this.rotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        this.rotateAnimation.setRepeatCount(Animation.INFINITE);
        this.rotateAnimation.setRepeatMode(Animation.RESTART);
        this.rotateAnimation.setDuration(900);
    }

    private void endRotationAnimator(){
        this.rotateAnimation.cancel();
        this.rotateAnimation.reset();
    }

    public void getBettingTips(){
        Toast.makeText(this, "Get betting tips has been instantiated.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        // your code.
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }


}
