package com.userms.exceptionhandler;

public class BadRequestException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private String message;

    private String logger;

    public BadRequestException() {
        super();
    }

    public BadRequestException(final String message) {
        super(message);
        this.message = message;
    }


    private String serviceErrorCode;

    public BadRequestException(final String message, final String serviceErrorCode) {
        super(message);
        this.message = message;
        this.serviceErrorCode=serviceErrorCode;
    }

    public String getServiceErrorCode() {
        return serviceErrorCode;
    }

    public void setServiceErrorCode(String serviceErrorCode) {
        this.serviceErrorCode = serviceErrorCode;
    }
}
