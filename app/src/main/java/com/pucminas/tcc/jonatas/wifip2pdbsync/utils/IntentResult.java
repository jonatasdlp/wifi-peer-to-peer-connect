package com.pucminas.tcc.jonatas.wifip2pdbsync.utils;

/**
 * Created by jonatas on 14/04/16.
 */
public class IntentResult {

    public static int RESULT_REFRESH_DEVICE = 1;

    private int result;
    private String message;

    public IntentResult(int result, String message) {
        this.result = result;
        this.message = message;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
