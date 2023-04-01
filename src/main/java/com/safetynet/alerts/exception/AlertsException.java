package com.safetynet.alerts.exception;

public class AlertsException extends Exception{

    private int errorCode;
    private String errorMessage;

    public AlertsException(Throwable throwable) {
        super(throwable);
    }

    public AlertsException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public AlertsException(String msg) {
        super(msg);
    }

    public AlertsException(String message, int errorCode) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = message;
    }


    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return this.getMessage();
//        return this.errorCode + " : " + this.getErrorMessage();
    }
}
