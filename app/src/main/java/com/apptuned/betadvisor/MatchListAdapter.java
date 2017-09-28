package com.apptuned.betadvisor;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by davies on 9/26/17.
 */

public class MatchListAdapter  extends RecyclerView.Adapter<MatchListAdapter.MatchListViewHolder>{

    private ArrayList<Match> matchArrayList;
    private Context context;

    public MatchListAdapter(Context context, ArrayList<Match> matchArray){
        this.context = context;
        this.matchArrayList = matchArray;
    }

    @Override
    public MatchListAdapter.MatchListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_bet_investor_matchlist_row, parent, false);
        // Pass view to ViewHolder
        MatchListViewHolder leaguesListViewHolder = new MatchListViewHolder(view);
        return leaguesListViewHolder;
    }

    @Override
    public void onBindViewHolder(MatchListAdapter.MatchListViewHolder holder, final int position) {
        final Match currentMatch = matchArrayList.get(position);

        holder.txtHomeTeam.setText(currentMatch.getHomeTeam());
        // Set home team icon
        Resources res = context.getResources();
        String iconURL = currentMatch.getHomeTeamIcon();
        if(iconURL == null || iconURL.length() < 1)
            iconURL = "home_club_logo";
        int resID = res.getIdentifier("com.apptuned.betadvisor:drawable/" + iconURL, null, null);
        holder.civHomeTeamLogo.setImageResource(resID);

        //Set away team icon
        iconURL = currentMatch.getAwayTeamIcon();
        if(iconURL == null || iconURL.length() < 1)
            iconURL = "away_club_logo";
        resID = res.getIdentifier("com.apptuned.betadvisor:drawable/" + iconURL, null, null);
        holder.civAwayTeamLogo.setImageResource(resID);

        holder.txtMatchDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(currentMatch.getMatchDate()));

        holder.txtMatchTime.setText(new SimpleDateFormat("hh:mm").format(currentMatch.getMatchTime()));

        holder.txtAwayTeam.setText(currentMatch.getAwayTeam());

        holder.txtMatchPrediction.setText(currentMatch.getPrediction());

        // TODO Set onclicklistener for each holder
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Match holdingMatch = currentMatch;
                //Toast.makeText(view.getContext(), holdingMatch.getLeague() + ": has been clicked!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchArrayList.size();
    }

    public class MatchListViewHolder extends RecyclerView.ViewHolder{

        TextView txtHomeTeam, txtAwayTeam, txtMatchDate, txtMatchTime, txtMatchPrediction;
        CircularImageView civHomeTeamLogo, civAwayTeamLogo;


        public MatchListViewHolder(View itemView){

            super(itemView);

            txtHomeTeam = (TextView) itemView.findViewById(R.id.tv_home_team);
            txtAwayTeam = (TextView) itemView.findViewById(R.id.tv_away_team);
            txtMatchDate = (TextView) itemView.findViewById(R.id.tv_match_date);
            txtMatchTime = (TextView) itemView.findViewById(R.id.tv_match_time);
            txtMatchPrediction = (TextView) itemView.findViewById(R.id.tv_match_prediction);
            civHomeTeamLogo = (CircularImageView) itemView.findViewById(R.id.civ_home_team_logo);
            civAwayTeamLogo = (CircularImageView) itemView.findViewById(R.id.civ_away_team_logo);
        }
    }
}
