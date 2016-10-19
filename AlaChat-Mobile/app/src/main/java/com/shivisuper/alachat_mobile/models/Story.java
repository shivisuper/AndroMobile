package com.shivisuper.alachat_mobile.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.shivisuper.alachat_mobile.models.Photo;

/**
 * Created by Umer on 10/11/2016.
 */

public class Story implements Serializable {
    private String StoryName;
    private List<Photo> StoryPhotos;
    private String CreatedBy;
    private Date CreatedOn;
    private List<String> ViewBy;
    private String Thumbnail;

    public String getStoryName() {
        return StoryName;
    }

    public List<Photo> getStoryPhotos() {
        return StoryPhotos;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public Date getCreatedOn() {
        return CreatedOn;
    }

    public List<String> getViewBy() {
        return ViewBy;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setStoryName(String storyName) {
        StoryName = storyName;
    }

    public void setStoryPhotos( List<Photo> storyPhotos) {
        StoryPhotos = storyPhotos;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public void setCreatedOn(Date createdOn) {
        CreatedOn = createdOn;
    }

    public void setViewBy(List<String> viewBy) {
        ViewBy = viewBy;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }
}
