package com.userms.exceptionhandler;

import java.util.Map;

public class DublicateExceptionHandler extends RuntimeException {

    private static final long serialVersionUID = -9079454849611061074L;

    private String message;

    private String logger;

    private Map<String,String> userMessage;

    public DublicateExceptionHandler() {
        super();
    }

    public DublicateExceptionHandler(final String message) {
        super(message);
        this.message = message;
    }



    private String serviceErrorCode;

    public DublicateExceptionHandler(final String message,final String serviceErrorCode,
                                     final Map<String,String> userMessage) {
        super(message);
        this.message = message;
        this.serviceErrorCode=serviceErrorCode;
        this.userMessage=userMessage;
    }


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
}
