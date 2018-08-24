package Yplat.exception;

import Yplat.core.ApiStore;

/**
 * Created by Administrator on 2018/1/21.
 */
public class ApiException extends Exception {

    public ApiException(String message,Throwable cause){
        super(message, cause);

    }

    public ApiException(String message){
        super(message,null);
    }
}
