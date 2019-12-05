package com.cp470group7.launcherapp.MusicStreamerBast9100;

public class MusicStreamItem {
    private long id;
    private String MusicName ;
    public long getId() {
        return id;
    }

    public MusicStreamItem () {}
    public  MusicStreamItem (String name) {
        this.MusicName = name ;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getMusicName() {
        return MusicName;
    }

    public void setMusicName(String musicName) {
        this.MusicName = musicName;
    }

    public String toString () {
        return MusicName + " " + id;
    }
}
