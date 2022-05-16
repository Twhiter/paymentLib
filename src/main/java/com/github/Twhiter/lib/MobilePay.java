package com.github.Twhiter.lib;

import com.github.Twhiter.dto.*;
import com.github.Twhiter.util.RSAUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Data
@AllArgsConstructor
public class MobilePay {

    private String serverPublicKeyBase64;
    private String publicKeyBase64;
    private String privateKeyBase64;
    private int merchantId;

    private  String remoteUrl;

    public QrCodeContent requestSessionPay(BigDecimal amount) throws IOException, InterruptedException {

        SessionPayRequest sessionPayRequest = new SessionPayRequest();
        sessionPayRequest.merchantId = merchantId;
        sessionPayRequest.amount = amount.setScale(2, RoundingMode.UNNECESSARY);

        String signatureFormat = String.format("%d,%s", merchantId, sessionPayRequest.amount);

        byte[] signatureBytes = RSAUtil.sign(signatureFormat.getBytes(StandardCharsets.UTF_8), privateKeyBase64);

        sessionPayRequest.signature = Base64.getEncoder().encodeToString(signatureBytes);

        byte[] objectBytes = new ObjectMapper().writeValueAsBytes(sessionPayRequest);
        byte[] encryptedBytes = RSAUtil.encrypt(objectBytes,serverPublicKeyBase64);

        String strToSend = Base64.getEncoder().encodeToString(encryptedBytes);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(remoteUrl + "/api/sessionPay"))
                .version(HttpClient.Version.HTTP_2)
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(strToSend))
                .build();

        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());


        ResponseData<SessionPayResp> responseData = new ObjectMapper().readValue(response.body(), new TypeReference<>() {});

        QrCodeContent qrCodeContent = new QrCodeContent();

        qrCodeContent.sessionId = responseData.data.sessionId;
        qrCodeContent.amount = amount;
        qrCodeContent.id = merchantId;

        return qrCodeContent;
    }


    public VerifyPayResp verifySessionPay(int sessionId) throws IOException, InterruptedException {


        MerchantVerifyInfo merchantVerifyInfo = new MerchantVerifyInfo();
        merchantVerifyInfo.sessionId = sessionId;

        byte[] signatureBytes = (sessionId + "").getBytes(StandardCharsets.UTF_8);
        merchantVerifyInfo.signature = Base64.getEncoder().encodeToString(signatureBytes);

        byte[] objBytes = new ObjectMapper().writeValueAsBytes(merchantVerifyInfo);
        byte[] encryptedBytes = RSAUtil.encrypt(objBytes,serverPublicKeyBase64);

        String strToSend=  Base64.getEncoder().encodeToString(encryptedBytes);

        HttpClient client = HttpClient.newHttpClient();


        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(remoteUrl + "/api/payVerify"))
                .version(HttpClient.Version.HTTP_2)
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(strToSend))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ResponseData<VerifyPayResp> responseData = new ObjectMapper().readValue(response.body(), new TypeReference<>() {});

        return responseData.data;
    }


    public String refund(int payId) throws IOException, InterruptedException {

        RefundRequest refundRequest = new RefundRequest();
        refundRequest.payId= payId;

        byte[] signatureBytes = String.format("%d,%d",merchantId,payId).getBytes(StandardCharsets.UTF_8);
        refundRequest.signature = Base64.getEncoder().encodeToString(signatureBytes);

        byte[] objBytes = new ObjectMapper().writeValueAsBytes(refundRequest);
        byte[] encryptedBytes = RSAUtil.encrypt(objBytes,serverPublicKeyBase64);

        String strToSend=  Base64.getEncoder().encodeToString(encryptedBytes);

        HttpClient client = HttpClient.newHttpClient();


        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(remoteUrl + "/api/refund"))
                .version(HttpClient.Version.HTTP_2)
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(strToSend))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ResponseData<String> responseData = new ObjectMapper().readValue(response.body(), new TypeReference<>() {});
        return responseData.data;
    }
}
