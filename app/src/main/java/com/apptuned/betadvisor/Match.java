package com.apptuned.betadvisor;


import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by davies on 9/26/17.
 */

public class Match implements Comparable{
    private String league, homeTeam, homeTeamIcon, awayTeam, awayTeamIcon, prediction, result;
    private float homeTeamOdds, awayTeamOdds;
    private Date matchDate, matchTime;
    private boolean complete;

    public String getLeague(){
        return this.league;
    }

    public void setLeague(String league){
        this.league = league;
    }

    public String getHomeTeam(){
        return this.homeTeam;
    }

    public void setHomeTeam(String homeTeam){
        this.homeTeam = homeTeam;
    }

    public String getHomeTeamIcon(){
        return this.homeTeamIcon;
    }

    public void setHomeTeamIcon(String homeTeamIcon){
        this.homeTeamIcon = homeTeamIcon;
    }

    public String getAwayTeam(){
        return this.awayTeam;
    }

    public void setAwayTeam(String awayTeam){
        this.awayTeam = awayTeam;
    }

    public String getAwayTeamIcon(){
        return this.awayTeamIcon;
    }

    public void setAwayTeamIcon(String awayTeamIcon){
        this.awayTeamIcon = awayTeamIcon;
    }

    public String getPrediction(){
        return this.prediction;
    }

    public void setPrediction(String prediction){
        this.prediction = prediction;
    }

    public String getResult(){
        return this.result;
    }

    public void setResult(String result){
        this.result = result;
    }

    public float getHomeTeamOdds(){
        return this.homeTeamOdds;
    }

    public void setHomeTeamOdds(float homeTeamOdds){
        this.homeTeamOdds = homeTeamOdds;
    }

    public float getAwayTeamOdds(){
        return  this.awayTeamOdds;
    }

    public void  setAwayTeamOdds(float awayTeamOdds){
        this.awayTeamOdds = awayTeamOdds;
    }

    public Date getMatchDate(){
        return this.matchDate;
    }

    public void setMatchDate(Date matchDate){
        this.matchDate = matchDate;
    }

    public Date getMatchTime(){
        return this.matchTime;
    }

    public void setMatchTime(Date matchTime){
        this.matchTime = matchTime;
    }

    public boolean isComplete(){
        return this.complete;
    }

    public void setComplete(boolean complete){
        this.complete = complete;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Match compareToMatch = (Match)o;
        Date compareToMatchDateTime = dateTime(compareToMatch.getMatchDate(), compareToMatch.getMatchTime());
        Date thisMatchDatetime = dateTime(this.getMatchDate(), this.getMatchTime());

        // For ascending
        long timeDifference = thisMatchDatetime.getTime() - compareToMatchDateTime.getTime();
        if(timeDifference == 0)
            return 0;
        else if(timeDifference >= 0)
            return 1;
        else
            return -1;
    }


    public Date dateTime(Date date, Date time) {
        return new Date(
                date.getYear(), date.getMonth(), date.getDay(),
                time.getHours(), time.getMinutes(), time.getSeconds()
        );
    }
}
