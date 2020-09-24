package com.example.addblog;

public class blogrefernceforrecycle {

    String heading;
    String time;
    String date;
    String photo;
    String count;

    public blogrefernceforrecycle(){

    }

    public blogrefernceforrecycle(String heading, String time, String date, String photo,String count) {
        this.heading = heading;
        this.time = time;
        this.date = date;
        this.photo = photo;
        this.count = count;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
