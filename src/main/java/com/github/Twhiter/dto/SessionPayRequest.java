package com.github.Twhiter.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SessionPayRequest {

    public int merchantId;
    public BigDecimal amount;
    public String signature;
}
