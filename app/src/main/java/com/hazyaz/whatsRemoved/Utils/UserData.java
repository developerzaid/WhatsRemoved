package com.hazyaz.whatsRemoved.Utils;

public class UserData {



    private String Name;
    private String Message;
    private String Time;

    public UserData(String name, String message, String time) {
        Name = name;
        Message = message;
        Time = time;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}