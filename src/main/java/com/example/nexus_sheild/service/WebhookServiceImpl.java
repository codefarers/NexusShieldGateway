package com.example.nexus_sheild.service;

import com.example.nexus_sheild.dto.PaymentPayloadDTO;
import com.example.nexus_sheild.model.PaymentTransaction;
import com.example.nexus_sheild.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class WebhookServiceImpl implements WebhookService {

    private final TransactionRepository transactionRepository;

    public WebhookServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void processPayment(PaymentPayloadDTO payloadDTO) {
        if (transactionRepository.existsByTransactionId(payloadDTO.getTransactionId())) {
            System.out.println("Duplicate transaction ignored: " + payloadDTO.getTransactionId());
            return;
        }

        PaymentTransaction tx = new PaymentTransaction();
        tx.setTransactionId(payloadDTO.getTransactionId());
        tx.setAmount(payloadDTO.getAmount());
        tx.setStatus(payloadDTO.getStatus());
        tx.setReceivedAt(LocalDateTime.now());

        transactionRepository.save(tx);
        System.out.println("Successfully persisted transaction ID: " + payloadDTO.getTransactionId());
    }
}