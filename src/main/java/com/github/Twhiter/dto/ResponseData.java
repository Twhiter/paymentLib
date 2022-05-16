package com.github.Twhiter.dto;

import lombok.Data;

@Data
public class ResponseData<T> {

    public static int OK = 200;
    public static int ERROR = -1;


    public String errorPrompt = null;
    public Integer status = OK;
    public T data = null;
}
