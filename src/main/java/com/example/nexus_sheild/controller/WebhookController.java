package com.example.nexus_sheild.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.nexus_sheild.dto.PaymentPayloadDTO;
import com.example.nexus_sheild.security.SignatureVerifier;
import com.example.nexus_sheild.service.WebhookService;

import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {
    private final WebhookService webhookService;
    private final ObjectMapper objectMapper;

    private static final String PARTNERS_SHARED_SECRET = "super-secret-partner-key-123";

    public WebhookController(WebhookService webhookService, ObjectMapper objectMapper) {
        this.webhookService = webhookService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/payment")
    public ResponseEntity<String> handlePaymentWebhook (
        @RequestBody String rawPayload,
        @RequestHeader(value = "X-Signature", required = false) String signature,
        @RequestHeader(value = "X-Timestamp", required = false) String timeStamp) {
            if(signature == null || timeStamp == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing security signature or timestamp headers");
            }

            boolean isValid = SignatureVerifier.verifySignature(rawPayload, timeStamp, signature, PARTNERS_SHARED_SECRET);

            if(!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Checksum Signature");
            }

            try{
                PaymentPayloadDTO payloadDTO = objectMapper.readValue(rawPayload, PaymentPayloadDTO.class);
                webhookService.processPayment(payloadDTO);

                return ResponseEntity.ok("Webhook successfully received and processed");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing transaction data");
            }
    }
}
