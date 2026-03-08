package com.acount.service.repository;

import com.acount.service.dto.AccountMasterDTO;
import com.acount.service.entity.AccountMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountMasterRepository extends JpaRepository<AccountMaster, String> {

    @Query("""
            select a from AccountMaster a
                where a.nik like :nik
                and a.accountNumber like :accountNumber
            """)
    Page<AccountMaster> findByNikAndAccountNumber(
            @Param("nik") String nik,
            @Param("accountNumber") String accountNumber,
            Pageable pageable
    );
}
