package com.localfix.common.exception;

public class VerifyProvider extends RuntimeException{

    public VerifyProvider(String sms)
    {
        super(sms);
    }

}
