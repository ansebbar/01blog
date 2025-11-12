package com._Talent._blog.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Hometmp {


    public Hometmp(){

    }
    public Hometmp(String name) {
        this.name = name ;
    }
    @JsonProperty("first") // in the body request will send first=
    private String name;
    @JsonProperty("second")
    private String lastname = "maybe";

    public String getname(){
        return name;
    }
    //if no getter not in json
    public String getlastname(){
        return lastname;
    }
    public void setname(String nam) {
        name = nam;
    }

    public void setlname(String lnam) {
        lastname = lnam;
    }
//setter and getter shoud refer to the name of the variable that gonna change 


}
