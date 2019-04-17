package com.transfers.repository;

import com.transfers.domain.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AccountRepository {
    @Select("SELECT * FROM account")
    List<Account> getAccounts();
}
