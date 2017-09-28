package com.apptuned.betadvisor;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.util.Date;

import java.text.SimpleDateFormat;

/**
 * Created by davies on 9/26/17.
 */

public class JSONMatchParser {

    private Context context;

    public JSONMatchParser(Context context){
        this.context = context;
    }
    public Match parse(JSONObject jsonObject){
        Match match = new Match();
        try{
            match.setLeague(jsonObject.getString("league"));
            match.setHomeTeam(jsonObject.getString("home_team"));
            match.setHomeTeamIcon(jsonObject.getString("get_home_team_icon"));
            match.setAwayTeam(jsonObject.getString("away_team"));
            match.setAwayTeamIcon(jsonObject.getString("get_away_team_icon"));
            match.setPrediction(jsonObject.getString("prediction"));
            match.setResult(jsonObject.getString("result"));

            float homeTeamOdds = (float) jsonObject.getDouble("home_team_odds");
            float awayTeamOdds = (float) jsonObject.getDouble("away_team_odds");
            match.setHomeTeamOdds(homeTeamOdds);
            match.setAwayTeamOdds(awayTeamOdds);

            Date matchDate = new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("match_date"));
            match.setMatchDate(matchDate);
            Date matchTime = new SimpleDateFormat("hh:mm:ss").parse(jsonObject.getString("match_time"));
            match.setMatchTime(matchTime);

            match.setComplete(jsonObject.getBoolean("complete"));

        } catch (JSONException e){
            if(context != null){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return null;
        } catch (ParseException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
        return match;
    }
}
