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
