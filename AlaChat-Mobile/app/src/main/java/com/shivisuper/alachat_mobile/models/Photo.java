package com.shivisuper.alachat_mobile.models;

import java.io.Serializable;

/**
 * Created by Umer on 10/12/2016.
 */

public class Photo implements Serializable {
    private String PhotoName;
    private String PhotoPath;
    private int Timeout;

    public String getPhotoName() {
        return PhotoName;
    }

    public String getPhotoPath() {
        return PhotoPath;
    }

    public void setPhotoName(String photoName) {
        PhotoName = photoName;
    }

    public void setPhotoPath(String photoPath) {
        PhotoPath = photoPath;
    }

    public void setTimeout(int timeout) {
        Timeout = timeout;
    }

    public int getTimeout() {
        return Timeout;

    }
}
