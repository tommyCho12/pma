package com.pma.dao;

import com.pma.entities.UserAccount;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface IUserAccountRepository extends PagingAndSortingRepository<UserAccount, Long> {
    @Override
    List<UserAccount> findAll();

    Optional<UserAccount> findByEmail(String email);
}
