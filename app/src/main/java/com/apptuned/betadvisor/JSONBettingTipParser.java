package com.apptuned.betadvisor;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by davies on 9/28/17.
 */

public class JSONBettingTipParser {
    private Context context;

    public JSONBettingTipParser(Context context){
        this.context = context;
    }

    public BettingTip parse(JSONObject jsonObject) {
        BettingTip bettingTip = null;
        try {
            bettingTip = new BettingTip();
            bettingTip.setTitle(jsonObject.getString("title"));
            bettingTip.setMessage(jsonObject.getString("message"));
            bettingTip.setDateCreated(new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(jsonObject.getString("created_at")));
            bettingTip.setDateScheduled(new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(jsonObject.getString("date_scheduled")));
        } catch (JSONException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return bettingTip;
    }
}
