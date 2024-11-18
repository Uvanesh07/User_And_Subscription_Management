package com.userms.security.jwt;

public class JwtResponse {
    private int code;
    private String status;
    private String token;
    private String username;
    private String msg;

    public JwtResponse(int code, String status, String token, String username, String msg) {
        this.code = code;
        this.status = status;
        this.token = token;
        this.username = username;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

//public class JwtResponse {
//    private String token;
//    private String username;
//
//    public JwtResponse(String token, String username) {
//        this.token = token;
//        this.username = username;
//    }
//
//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//}
