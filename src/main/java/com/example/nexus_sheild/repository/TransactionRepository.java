package com.example.nexus_sheild.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.nexus_sheild.model.PaymentTransaction;

public interface TransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    boolean existsByTransactionId(String transactionId);
}
