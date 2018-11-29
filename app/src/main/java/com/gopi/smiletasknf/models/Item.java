package com.gopi.smiletasknf.models;


public class Item {
    private String name;
    private int defaultColor;
    private String ImageUrl;
    private int imageResource;
    private int orientation;

    public Item() {
    }

    public Item(String name, int defaultColor, String imageUrl, int imageResource) {
        setName(name);
        setDefaultColor(defaultColor);
        setImageUrl(imageUrl);
        setImageResource(imageResource);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
