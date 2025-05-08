package com.example.mobilalkfejl_online_telefonbolt;

public class PhoneItem {
    private String id;
    private String name;
    private String desc;
    private String price;
    private int imageRsc;
    private int cartCount;

    public PhoneItem(){}
    public PhoneItem(String name, String desc, String price, int imageRsc, int cartCount) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.imageRsc = imageRsc;
        this.cartCount = cartCount;
    }


    public String getName(){
        return name;
    }
    public String getDesc(){
        return desc;
    }
    public String getPrice(){
        return price;
    }

    public int getResource(){
        return imageRsc;
    }
    public int getCartCount(){return cartCount;}
    public void setResource(int res){
        imageRsc = res;
    }
    public String _getID(){
        return id;
    }
    public void setID(String id){
        this.id = id;
    }
    public void cartCountUp(){
        cartCount += 1;
    }
    public void cartCountDown(){
        cartCount -= 1;
    }
    public void setCartCount(int count){
        cartCount = count;
    }
}
