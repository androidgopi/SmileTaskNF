package com.gopi.smiletasknf.models;


public class GalleryItem {
    private int id;
    private String  name;
    private String imageUrl;
    private boolean fromMemory;
    private int resId;
    private String dateTaken;
    private int orientation;


    public GalleryItem(){}

    public GalleryItem(int id, boolean fromMemory){
        this.fromMemory = fromMemory;
        this.id = id;
    }


    public GalleryItem(int id, String name, String imageUrl, int orientation){
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.orientation = orientation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isFromMemory() {
        return fromMemory;
    }

    public void setFromMemory(boolean fromMemory) {
        this.fromMemory = fromMemory;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
