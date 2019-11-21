package com.example.mynotes.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {


    @PrimaryKey(autoGenerate = true)
    private int id;
    private String text, date;
    private boolean isMarked, isDoing , isDone;

    public Note(String text, String date, boolean isMarked, boolean isDoing ,boolean isDone) {
        this.text = text;
        this.date = date;
        this.isMarked = isMarked;
        this.isDoing = isDoing;
        this.isDone = isDone;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public int getId() {
        return id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public void setDoing(boolean doing) {
        isDoing = doing;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public boolean isDoing() {
        return isDoing;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setText(String text) {
        this.text = text;
    }


}
