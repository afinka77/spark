package com.transfers.repository;

import com.transfers.domain.Account;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountRepository {
    @Results(value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "transactionPostings",
                    column = "id",
                    javaType = List.class,
                    many = @Many(select = "com.transfers.repository.TransactionPostingRepository.getTransactionPostingsByAccount"))})
    @Select("SELECT * FROM account WHERE customer_id=#{customerId}")
    List<Account> getAccounts(@Param("customerId") Long customerId);

    @Select("SELECT name FROM account WHERE id=#{accountId}")
    String getAccountName(@Param("accountId") Long accountId);

    @Select("SELECT * FROM account WHERE name=#{accountName}")
    Account getAccountByName(@Param("accountName") String accountName);

    @Select("SELECT * FROM account WHERE id=#{accountId} FOR UPDATE")
    Account selectForUpdate(@Param("accountId") Long accountId);

    @Select("SELECT * FROM account WHERE id=#{accountId}")
    Account selectAccount(@Param("accountId") Long accountId);

    @Update("UPDATE account SET total_balance=#{balance} WHERE id=#{accountId}")
    void updateBalance(@Param("accountId") Long accountId,
                       @Param("balance") BigDecimal balance);

    @Insert("INSERT into account (name, customer_id) values (#{name}, #{customerId})")
    Long insert(Account account);

    @Update("UPDATE account SET name=#{name},customer_id= #{customerId}, modified_on=now() where id=#{id}")
    void update(Account account);

    @Delete("DELETE account where id=#{accountId}")
    void delete(@Param("accountId") Long accountId);
}
