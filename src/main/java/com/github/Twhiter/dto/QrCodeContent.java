package com.github.Twhiter.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QrCodeContent {
    public int id;
    public final String type = "merchantConfirmation";
    public int sessionId;
    public BigDecimal amount;
}
