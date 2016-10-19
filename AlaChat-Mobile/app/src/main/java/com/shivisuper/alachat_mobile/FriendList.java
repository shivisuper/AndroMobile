package com.shivisuper.alachat_mobile;

public class FriendList {

    private String name;

    public FriendList()
    {

    }

    public FriendList(String name)
    {
        this.name = name;
    }

    public String getFriendName()
    {
        return name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + name + "]";
    }
}
