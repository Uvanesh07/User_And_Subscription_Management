package com.userms.exceptionhandler;

public class InternalServerError extends RuntimeException {

    private static final long serialVersionUID = -9079454849611061074L;

    private String message;

    private String logger;

    public InternalServerError() {
        super();
    }

    public InternalServerError(final String message) {
        super(message);
        this.message = message;
    }


    private String serviceErrorCode;

    public InternalServerError(final String message,final String serviceErrorCode) {
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
