package com.localfix.common.exception;

public class EmailNotVerifiedException extends RuntimeException{

    public EmailNotVerifiedException(String sms)
    {
        super(sms);
    }

}
