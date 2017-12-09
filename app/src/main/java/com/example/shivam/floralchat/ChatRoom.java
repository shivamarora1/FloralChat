package com.example.shivam.floralchat;

import android.content.Context;

/**
 * Created by Shivam on 12/4/2017.
 */

public class ChatRoom {
    String name;
    String image;
    String description;
    Context context;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ChatRoom()
    {}

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ChatRoom(String name, String description, Context context) {
        this.name = name;
        this.context=context;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
