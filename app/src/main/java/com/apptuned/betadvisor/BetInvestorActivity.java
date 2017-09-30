package com.apptuned.betadvisor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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


    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private final SimpleDateFormat simpleFullDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private JSONFileHandler jsonFileHandler;
    private JSONMatchParser jsonMatchParser;
    private TextView tvMatchlistHeader, tvMatchlistHeaderPlusOne, tvMatchlistHeaderPlusTwo;

    private Date lastSyncDateTime;

    private MenuItem miRefresh;

    private RecyclerView rvMatchList, rvMatchlistPlusOne, rvMatchlistPlusTwo;
    private ArrayList<Match> fullMatchlistArray;
    private String currentLeague = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bet_investor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initRotationAnimator();

        tvMatchlistHeader = (TextView)findViewById(R.id.tv_matchlist_header);
        tvMatchlistHeaderPlusOne = (TextView)findViewById(R.id.tv_matchlist_header_plus_one);
        tvMatchlistHeaderPlusTwo = (TextView)findViewById(R.id.tv_matchlist_header_plus_two);
        Date today = new Date();
        tvMatchlistHeader.setText(new SimpleDateFormat("E, dd MMMM yyyy").format(today));
        tvMatchlistHeaderPlusOne.setText(new SimpleDateFormat("E, dd MMMM yyyy").format(today.getTime() + 1 * 24 * 60 * 60 * 1000));
        tvMatchlistHeaderPlusTwo.setText(new SimpleDateFormat("E, dd MMMM yyyy").format(today.getTime() + 2 * 24 * 60 * 60 * 1000));

        jsonFileHandler = new JSONFileHandler(this);
        jsonMatchParser = new JSONMatchParser(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        // First Matchlist RV
        rvMatchList = (RecyclerView) findViewById(R.id.rv_match_list);
        rvMatchList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvMatchList.getContext(),linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_recyclerview));
        rvMatchList.addItemDecoration(dividerItemDecoration);

        // Second Matchlist RV
        rvMatchlistPlusOne = (RecyclerView) findViewById(R.id.rv_match_list_plus_one);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        rvMatchlistPlusOne.setLayoutManager(linearLayoutManager2);
        dividerItemDecoration = new DividerItemDecoration(rvMatchList.getContext(),linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_recyclerview));
        rvMatchlistPlusOne.addItemDecoration(dividerItemDecoration);
        // Third Matchlist RV
        rvMatchlistPlusTwo = (RecyclerView) findViewById(R.id.rv_match_list_plus_two);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        rvMatchlistPlusTwo.setLayoutManager(linearLayoutManager3);
        dividerItemDecoration = new DividerItemDecoration(rvMatchList.getContext(),linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_recyclerview));
        rvMatchlistPlusTwo.addItemDecoration(dividerItemDecoration);

        // TODO Check last_synced datetime value.. In shared preferences
        spConfig = getSharedPreferences("com.apptuned.betadvisor.Config", this.MODE_PRIVATE);
        String lastSyncString = spConfig.getString(LAST_SYNC_DATETIME, null);
        if(lastSyncString != null){
            try{
                lastSyncDateTime = simpleFullDateFormat.parse(lastSyncString);
                String todayString = simpleFullDateFormat.format(new Date());
                if(getDifferenceDays(lastSyncDateTime, simpleFullDateFormat.parse(todayString)) > 1)
                    syncMatches(true);
                else {
                    String jsonMatches = jsonFileHandler.readJSONFile(MATCHES_FILE);
                    try {
                        JSONArray jsonMatchesArray = new JSONArray(jsonMatches);
                        if(jsonMatchesArray == null || jsonMatchesArray.length() ==0)
                            syncMatches(true);
                        else
                            updateLeagueSelection();
                            updateView(jsonMatchesArray);
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
            case R.id.miFilter:
                // Get relevant league list from the array
                if(fullMatchlistArray == null || fullMatchlistArray.size() < 1)
                    return true;
                final ArrayList<String> leaguesArray = new ArrayList<String>();
                leaguesArray.add("All");
                for (Match match: fullMatchlistArray) {
                    if(leaguesArray.contains(match.getLeague()))
                        continue;
                    else
                        leaguesArray.add(match.getLeague());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select a league");
                builder.setItems(leaguesArray.toArray(new String[leaguesArray.size()]), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection

                        ArrayList<Match> filteredMatches = new ArrayList<Match>();

                        if(item == 0){
                            for (Match match: fullMatchlistArray) {
                                filteredMatches.add(match);
                            }
                            updateView(fullMatchlistArray);
                            currentLeague = null;
                        }
                        else if(item > 0){
                            currentLeague = leaguesArray.get(item);
                            for (Match match: fullMatchlistArray) {
                                if(leaguesArray.contains(match.getLeague()))
                                {
                                    filteredMatches.add(match);
                                }
                            }
                        }
                        else
                            return;
                        updateLeagueSelection();
                        updateView(filteredMatches);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateView(JSONArray jsonMatchesArray){
        fullMatchlistArray = new ArrayList<Match>();
        try{
            for (int i = 0; i < jsonMatchesArray.length(); i++) {
                JSONObject obj = jsonMatchesArray.getJSONObject(i);
                Match match = jsonMatchParser.parse(obj);
                if(match != null)
                    fullMatchlistArray.add(match);
            }
            Collections.sort(fullMatchlistArray);
            // Onto updating the indicidual recyclerviews

            Date today = new Date();
            updateRecyclerView(today, rvMatchList);
            updateRecyclerView(addDate(today, 1), rvMatchlistPlusOne);
            updateRecyclerView(addDate(today, 2), rvMatchlistPlusTwo);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void updateView(ArrayList<Match> matchesArray){
        Collections.sort(matchesArray);
        // Onto updating the indicidual recyclerviews

        Date today = new Date();
        updateRecyclerView(matchesArray, today, rvMatchList);
        updateRecyclerView(matchesArray, addDate(today, 1), rvMatchlistPlusOne);
        updateRecyclerView(matchesArray, addDate(today, 2), rvMatchlistPlusTwo);
    }

    public void updateRecyclerView(Date date, RecyclerView rvObj){
        ArrayList<Match> filteredMatchList = new ArrayList<Match>();
        for (Match match: this.fullMatchlistArray) {
            if(simpleDateFormat.format(match.getMatchDate()).equalsIgnoreCase(simpleDateFormat.format(date))){
                 if(currentLeague == null)
                    filteredMatchList.add(match);
                else if(match.getLeague().equalsIgnoreCase(currentLeague))
                    filteredMatchList.add(match);
                else
                    continue;
            }
        }
        MatchListAdapter matchListAdapter = new MatchListAdapter(getApplicationContext(), filteredMatchList);
        rvObj.setAdapter(matchListAdapter);
    }

    public void updateRecyclerView(ArrayList<Match> matchArrayList, Date date, RecyclerView rvObj){
        ArrayList<Match> filteredMatchList = new ArrayList<Match>();
        for (Match match: matchArrayList) {
            if(simpleDateFormat.format(match.getMatchDate()).equalsIgnoreCase(simpleDateFormat.format(date))){
                if(currentLeague == null)
                    filteredMatchList.add(match);
                else if(match.getLeague().equalsIgnoreCase(currentLeague))
                    filteredMatchList.add(match);
                else
                    continue;
            }
        }
        MatchListAdapter matchListAdapter = new MatchListAdapter(getApplicationContext(), filteredMatchList);
        rvObj.setAdapter(matchListAdapter);
    }

    public void syncMatches(boolean isManualSync){
        syncMode = true;

        RestAdapter.get("api/json_matchlist", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray){
                jsonFileHandler.writeJSONFile(MATCHES_FILE, responseArray.toString());
                updateView(responseArray);
                syncMode = false;
                currentLeague = null;
                updateLeagueSelection();
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

    public Date addDate(Date date, int daysToadd){
        String resultString = simpleFullDateFormat.format(date.getTime() + daysToadd * 24 * 60 * 60 * 1000);
        Date result = null;
        try {
            result = simpleFullDateFormat.parse(resultString);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return result;
    }
    @Override
    public void onBackPressed() {
        // your code.
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public void updateLeagueSelection(){
        TextView tvLeagueHeader = (TextView)findViewById(R.id.tv_league_header);
        if(currentLeague == null){
            tvLeagueHeader.setText("All Leagues");
        }
        else
            tvLeagueHeader.setText(currentLeague);
    }

}
