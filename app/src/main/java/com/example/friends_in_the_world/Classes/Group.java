package com.example.friends_in_the_world.Classes;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private String ID;
    private List<Member> members;

    public Group(String name) {
        this(name, null, new ArrayList<>());
    }

    public Group(String name, List<Member> members) {
        this(name, null, members);
    }

    public Group(String name, String ID, List<Member> members) {
        this.name = name;
        this.ID = ID;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<Member> getMembers() {
        return members;
    }

}