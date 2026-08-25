package com.example.nexus_sheild.service;

import com.example.nexus_sheild.dto.PaymentPayloadDTO;

public interface WebhookService {
    void processPayment(PaymentPayloadDTO payloadDTO);
}