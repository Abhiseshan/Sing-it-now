package com.mygdx.game;

public class TrackID {
    private String songName;
    private String album;
    private String artist;
    private String album_art;
    private String lyrics;
    private String mp3;
    private String background;
    private int identifier;

    public TrackID(String songName, String album, String artist, String album_art, String lyrics, String mp3, String background, int identifier){
        this.album = album;
        this.songName = songName;
        this.artist = artist;
        this.album_art = album_art;
        this.lyrics = lyrics;
        this.mp3 = mp3;
        this.background = background;
        this.identifier = identifier;
    }

    public String getSongName(){
        return songName;
    }

    public String getAlbum(){
        return album;
    }

    public String getArtist(){
        return artist;
    }

    public String getAlbum_art(){
        return album_art;
    }

    public int getIdentifier(){
        return identifier;
    }

    public String getLyrics(){
        return lyrics;
    }

    public String getMp3(){
        return mp3;
    }

    public String getBackground() {
        return background;
    }
}
