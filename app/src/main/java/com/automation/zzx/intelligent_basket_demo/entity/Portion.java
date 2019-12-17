package com.automation.zzx.intelligent_basket_demo.entity;

public class Portion {
    private String name;  // 功能名称
    private int imageId;  // 功能示意图
    private int state; // 开关状态(0: 尚未上传， 1：上传成功)

    public Portion(String name, int imageId, int state){
        this.name = name;
        this.imageId = imageId;
        this.state = state;
    }

    public String getName(){
        return name;
    }

    public int getImageId(){
        return imageId;
    }

    public  int getState(){
        return state;
    }

    public void setState(int state){
        this.state = state;
    }
}
