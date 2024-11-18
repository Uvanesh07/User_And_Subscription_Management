package com.userms.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class Error {

    private String message;

    private String log;

    private Integer code;

    private Map<String,String> validation;

    private LocalDateTime dateTime;

    private HttpStatus httpStatus;

    private String serviceErrorCode;

    private Map<String,String> userMessage;


    public Map<String, String> getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(Map<String, String> userMessage) {
        this.userMessage = userMessage;
    }

    public String getServiceErrorCode() {
        return serviceErrorCode;
    }

    public void setServiceErrorCode(String serviceErrorCode) {
        this.serviceErrorCode = serviceErrorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }


    public Map<String, String> getValidation() {
        return validation;
    }

    public void setValidation(Map<String, String> validation) {
        this.validation = validation;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
