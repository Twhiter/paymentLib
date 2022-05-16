package com.github.Twhiter.dto;

import lombok.Data;

@Data
public class RefundRequest {

    public int merchantId;
    public int payId;
    public String signature;
}
