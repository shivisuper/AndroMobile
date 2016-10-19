package com.shivisuper.alachat_mobile.models;

/**
 * Created by admin on 19/10/16.
 */

public class SentToActivityModel {

    public String PhotoPath;
    public String TimeOut;
    public String Date;

    public SentToActivityModel()
    {

    }

    public SentToActivityModel(String uri , String timeOut , String date)
    {
        this.PhotoPath = uri;
        this.TimeOut = timeOut;
        this.Date = date;
    }

}






