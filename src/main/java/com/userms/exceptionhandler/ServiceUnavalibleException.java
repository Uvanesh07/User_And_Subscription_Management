package com.userms.exceptionhandler;

public class ServiceUnavalibleException extends RuntimeException {

    private static final long serialVersionUID = -9079454849611061074L;

    private String message;

    private String serviceErrorCode;

    private String logger;

    public ServiceUnavalibleException() {
        super();
    }

    public ServiceUnavalibleException(final String message) {
        super(message);
        this.message = message;
    }


    public ServiceUnavalibleException(final String message,final String serviceErrorCode) {
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