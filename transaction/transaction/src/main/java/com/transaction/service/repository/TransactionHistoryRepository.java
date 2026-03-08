package com.transaction.service.repository;

import com.transaction.service.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, String> {
    List<TransactionHistory> findByTransactionIdOrderByDateCreatedDesc(String transactionId);
}
