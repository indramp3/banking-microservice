package com.acount.service.listener;

import com.acount.service.dto.AccountMasterDTO;
import com.acount.service.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountListener {

    private final AccountService accountService;


    public AccountListener(AccountService accountService) {
        this.accountService = accountService;
    }

    public AccountMasterDTO.ResponseCreateAccount createAccount(String message){

        try {
            AccountMasterDTO.RequestCreateAccount requestCreateAccount =
                    new ObjectMapper().readValue(message, AccountMasterDTO.RequestCreateAccount.class);

            return accountService.createNewAccount(requestCreateAccount);
        }
        catch (Exception e){

            log.error("Failed to create account from message: {}", message, e);

            // return response error
            return AccountMasterDTO.ResponseCreateAccount.builder()
                    .error(true)
                    .message("Failed to insert new account")
                    .result(null)
                    .listResult(null)
                    .build();
        }
    }
}
