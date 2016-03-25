//package com.comp2100.MyDiary;
//
//import android.app.Activity;
//import android.os.Bundle;
//import org.apache.commons.lang3.SerializationUtils;
//
//import java.io.Serializable;
//import java.util.Date;
//
///**
// * Created by Sina on 17/03/2016.
// */
//
//public class Note implements Serializable {
//    static int _id = 20;
//    int id;
//    String body;
//    String title;
//    Date date;
//
//    public Note(String title, String body, Date date) {
//        this.body = body;
//        this.title = title;
//        this.date = date;
//        this.id = _id++;
//    }
//
////    public Note(byte[] object) {
////        this = (Note)SerializationUtils.deserialize(object);
////    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public void setBody(String body) {
//        this.body = body;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public void setDate(Date date) {
//        this.date = date;
//    }
//
//    public int getId() {
//
//        return id;
//    }
//
//    public String getBody() {
//        return body;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public Date getDate() {
//        return date;
//    }
//
//    public String toString() {
//        if (!this.title.equals(""))
//            return this.title;
//        else
//            return  this.title.substring(0, 15) + "...";
//
//    }
//
//    public byte[] getBlob() {
//        return SerializationUtils.serialize(this);
//    }
//}