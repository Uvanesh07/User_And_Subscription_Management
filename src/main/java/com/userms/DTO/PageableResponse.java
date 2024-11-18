package com.userms.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PageableResponse <T>{

    private int code;
    private String status;
    private int page;
    private int size;
    @JsonProperty("_embedded")
    private Object data;
    private String msg;
    private int totalPages;
    private long totalElements;

    public PageableResponse(int code, String status, int page, int size, Object data, String msg, int totalPages, long totalElements) {
        this.code = code;
        this.status = status;
        this.page = page;
        this.size = size;
        this.data = data;
        this.msg = msg;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}

