package com.pma.services;

import com.pma.dao.IUserAccountRepository;
import com.pma.entities.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAccountService
{
    @Autowired
    IUserAccountRepository uaRepo;

    public List<UserAccount> findAll(){
        return uaRepo.findAll();
    }

    public void save(UserAccount ua){
        uaRepo.save(ua);
    }
}
