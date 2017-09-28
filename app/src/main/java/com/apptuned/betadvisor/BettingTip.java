package com.apptuned.betadvisor;

import java.util.Date;

/**
 * Created by davies on 9/28/17.
 */

public class BettingTip {

    private String title, message;
    private Date dateCreated, dateScheduled;

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String string){
        this.title = title;
    }

    public String getMessage(){
        return this.message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public Date getDateCreated(){
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated){
        this.dateCreated = dateCreated;
    }

    public Date getDateScheduled(){
        return this.dateScheduled;
    }

    public void setDateScheduled(Date dateScheduled){
        this.dateScheduled = dateScheduled;
    }
}
