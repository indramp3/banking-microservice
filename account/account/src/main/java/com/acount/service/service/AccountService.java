package com.acount.service.service;

import com.acount.service.dto.AccountMasterDTO;
import com.acount.service.entity.AccountMaster;

import java.util.List;

public interface AccountService {

    void createAccount(AccountMasterDTO.RequestCreateAccount requestCreateAccount);

    AccountMasterDTO.ResponseCreateAccount createNewAccount(AccountMasterDTO.RequestCreateAccount requestCreateAccount);

    List<AccountMaster> getByNikAndAccountNumber(AccountMasterDTO accountMasterDTO);

    AccountMasterDTO.ResponseCreateAccount processTransfer(com.acount.service.dto.TransferRequestDTO transferRequest);

}
