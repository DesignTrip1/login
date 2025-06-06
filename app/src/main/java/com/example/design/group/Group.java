package com.example.design.group;

import java.util.List;

public class Group {
    private String name;
    private List<String> members;

    public Group(String name, List<String> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public List<String> getMembers() {
        return members;
    }
}
