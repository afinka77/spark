package com.transfers.repository;

import com.transfers.domain.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountRepository {
    @Select("SELECT * FROM account WHERE customer_id=#{customerId}")
    List<Account> getAccounts(@Param("customerId") Long customerId);

    @Select("SELECT name FROM account WHERE id=#{accountId}")
    String getAccountName(@Param("accountId") Long accountId);

    @Select("SELECT * FROM account WHERE name=#{accountName}")
    Account getAccountByName(@Param("accountName") String accountName);

    @Select("SELECT * FROM account WHERE id=#{accountId} FOR UPDATE")
    Account selectForUpdate(@Param("accountId") Long accountId);

    @Update("UPDATE account SET total_balance=#{balance} WHERE id=#{accountId}")
    void updateBalance(@Param("accountId") Long accountId,
                       @Param("balance") BigDecimal balance);
}
