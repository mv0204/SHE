package com.example.she.contact;

public class RecModel {
    private int img;
    private String txt;
    private String txt2;

    public int getImg() {
        return img;
    }

    public String getTxt() {
        return txt;
    }
    public String getTxt2() {
        return txt2;
    }

    public RecModel(int img, String txt,String txt2) {
        this.img = img;
        this.txt = txt;
        this.txt2 = txt2;

    }
}
