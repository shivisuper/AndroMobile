package com.shivisuper.alachat_mobile.models;

/**
 * Created by admin on 19/10/16.
 */

public class SentToActivityModel {

    public String PhotoPath;
    public String TimeOut;
    public String Date;
    public Double Lng;
    public Double Lat;

    public SentToActivityModel()
    {

    }

    public SentToActivityModel(String uri , String timeOut , String date)
    {
        this.PhotoPath = uri;
        this.TimeOut = timeOut;
        this.Date = date;
    }

    public SentToActivityModel(String uri, String timeOut, String date, Double Lng, Double Lat)
    {
        this.PhotoPath = uri;
        this.TimeOut = timeOut;
        this.Date = date;
        this.Lng = Lng;
        this.Lat = Lat;
    }

}






