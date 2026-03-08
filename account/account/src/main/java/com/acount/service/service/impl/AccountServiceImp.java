package com.acount.service.service.impl;

import com.acount.service.dto.AccountMasterDTO;
import com.acount.service.entity.AccountMaster;
import com.acount.service.repository.AccountMasterRepository;
import com.acount.service.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AccountServiceImp implements AccountService {

    private final AccountMasterRepository accountMasterRepository;

    public AccountServiceImp(AccountMasterRepository accountMasterRepository) {
        this.accountMasterRepository = accountMasterRepository;
    }

    @Override
    public void createAccount(AccountMasterDTO.RequestCreateAccount requestCreateAccount) {

        log.info("Creating Account");

        try{
            if (accountMasterRepository.existsById(requestCreateAccount.getNik())) {
                throw new IllegalArgumentException("Account already exists for NIK: " + requestCreateAccount.getNik());
            }

            AccountMaster accountMaster = new AccountMaster();
            accountMaster.setNik(requestCreateAccount.getNik());
            accountMaster.setAccountNumber(generateAccountNumber());
            accountMaster.setCustomerName(requestCreateAccount.getCustomerName());
            accountMaster.setBalance(requestCreateAccount.getBalance());
            accountMaster.setDateCreated(new Date());

            accountMasterRepository.saveAndFlush(accountMaster);

            log.info("Account created successfully for NIK: {}", requestCreateAccount.getNik());

        }catch (Exception e){
            log.error("Failed to create account for NIK: {}", requestCreateAccount.getNik(), e);
            throw e;
        }
    }

    @Override
    public AccountMasterDTO.ResponseCreateAccount createNewAccount(AccountMasterDTO.RequestCreateAccount requestCreateAccount) {
        log.info("Creating Account");

        try{
            if (accountMasterRepository.existsById(requestCreateAccount.getNik())) {
                throw new IllegalArgumentException("Account already exists for NIK: " + requestCreateAccount.getNik());
            }

            AccountMaster accountMaster = new AccountMaster();
            accountMaster.setNik(requestCreateAccount.getNik());
            accountMaster.setAccountNumber(generateAccountNumber());
            accountMaster.setCustomerName(requestCreateAccount.getCustomerName());
            accountMaster.setBalance(requestCreateAccount.getBalance());
            accountMaster.setDateCreated(new Date());

            accountMasterRepository.saveAndFlush(accountMaster);

            log.info("Account created successfully for NIK: {}", requestCreateAccount.getNik());

            AccountMasterDTO.DataResp dataResp = AccountMasterDTO.DataResp.builder()
                    .id(null)
                    .name(accountMaster.getCustomerName())
                    .price(accountMaster.getBalance())
                    .build();

            return new AccountMasterDTO.ResponseCreateAccount(false, "Account created successfully", dataResp, null);

        }catch (Exception e){
            log.error("Failed to create account for NIK: {}", requestCreateAccount.getNik(), e);
            throw e;
        }

    }

    @Override
    public List<AccountMaster> getByNikAndAccountNumber(AccountMasterDTO accountMasterDTO) {

        try{
            String nik = generateParameter(accountMasterDTO.getNik());
            String accountNumber = generateParameter(accountMasterDTO.getAccountNumber());
            Pageable pageable = PageRequest.of(accountMasterDTO.getPage(), accountMasterDTO.getMaxRow(),
                    Sort.by("dateCreated").descending());

            return accountMasterRepository
                    .findByNikAndAccountNumber(nik, accountNumber, pageable)
                    .getContent();
        }catch (Exception e){
            log.error("Failed to get account by NIK and Account Number", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public AccountMasterDTO.ResponseCreateAccount processTransfer(com.acount.service.dto.TransferRequestDTO transferRequest) {
        log.info("Processing transfer for TX ID: {}", transferRequest.getTransactionId());

        try {
            AccountMaster debitAcc = accountMasterRepository.findById(transferRequest.getDebitAccountNumber())
                    .orElseThrow(() -> new IllegalArgumentException("Debit Account not found"));

            AccountMaster creditAcc = accountMasterRepository.findById(transferRequest.getCreditAccountNumber())
                    .orElseThrow(() -> new IllegalArgumentException("Credit Account not found"));

            if (debitAcc.getBalance().compareTo(transferRequest.getAmount()) < 0) {
                throw new IllegalArgumentException("Insufficient balance in debit account");
            }

            // Deduct and add
            debitAcc.setBalance(debitAcc.getBalance().subtract(transferRequest.getAmount()));
            creditAcc.setBalance(creditAcc.getBalance().add(transferRequest.getAmount()));

            accountMasterRepository.save(debitAcc);
            accountMasterRepository.save(creditAcc);

            return AccountMasterDTO.ResponseCreateAccount.builder()
                    .error(false)
                    .message("Transfer successful")
                    .build();

        } catch (Exception e) {
            log.error("Failed to process transfer", e);
            return AccountMasterDTO.ResponseCreateAccount.builder()
                    .error(true)
                    .message("Transfer failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public AccountMasterDTO.ResponseCreateAccount processTopup(com.acount.service.dto.TopupRequestDTO topupRequest) {
        log.info("Processing topup for TX ID: {}", topupRequest.getTransactionId());

        try {
            AccountMaster acc = accountMasterRepository.findById(topupRequest.getAccountNumber())
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));

            // Add amount
            acc.setBalance(acc.getBalance().add(topupRequest.getAmount()));
            accountMasterRepository.save(acc);

            return AccountMasterDTO.ResponseCreateAccount.builder()
                    .error(false)
                    .message("Topup successful")
                    .build();

        } catch (Exception e) {
            log.error("Failed to process topup", e);
            return AccountMasterDTO.ResponseCreateAccount.builder()
                    .error(true)
                    .message("Topup failed: " + e.getMessage())
                    .build();
        }
    }


    private String generateAccountNumber() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private String generateParameter(String value) {

        if(value == null){
            return  "%";
        }

        return "%" + value + "%";
    }

}
