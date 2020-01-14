package com.npu.gmall.demo.rabbit.bean;

import java.io.Serializable;

public class User implements Serializable {
    String username;
    Integer id;

    public User() {
    }

    public User(String username, Integer id) {
        this.username = username;
        this.id=id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", id=" + id +
                '}';
    }
}
