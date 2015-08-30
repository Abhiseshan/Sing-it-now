package com.mygdx.game;

public class LyricFile {
    public String time;
    public String lyric;

    public LyricFile(String time, String lyric){
        this.lyric = lyric;
        this.time = time;
    }

    public void setLyric(String lyric){
        this.lyric = lyric;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getTime(){
        return time;
    }

    public String getLyric(){
        return lyric;
    }
}
