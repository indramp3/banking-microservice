package com.transaction.service.repository;

import com.transaction.service.entity.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, String> {

    @Query("SELECT t FROM Transactions t WHERE t.creditAccountNumber = :account OR t.debitAccountNumber = :account")
    Page<Transactions> findByAccountNumber(@Param("account") String account, Pageable pageable);

    Page<Transactions> findByDateCreatedBetween(Date startDate, Date endDate, Pageable pageable);
}
