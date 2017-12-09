package com.example.shivam.floralchat;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Shivam on 11/29/2017.
 */

public class MessageClass {
   String content,name,time,image;
    Context context;

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MessageClass()
    {}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageClass(String content,String uName,String time,String image,Context context) {
        this.time=time;
        this.context=context;
        this.image=image;
        this.content = content;
        this.name=uName;
    }
}
