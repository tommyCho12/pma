package com.jrp.pma.dao;

import com.jrp.pma.entities.UserAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface IUserAccountRepository extends PagingAndSortingRepository<UserAccount, Long>
{
    @Override
    List<UserAccount> findAll();
}
