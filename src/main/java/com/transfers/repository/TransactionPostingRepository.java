package com.transfers.repository;

import com.transfers.domain.TransactionPosting;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TransactionPostingRepository {
    @Insert({"INSERT into transaction_posting (transaction_id, account_id, debit, credit)",
            " values (#{transactionId}, #{accountId}, #{debit}, #{credit})"})
    Long insert(TransactionPosting transactionPosting);

    @Select("SELECT * FROM transaction_posting where transaction_id=#{transactionId}")
    List<TransactionPosting> getTransactionPostings(@Param("transactionId") Long transactionId);

    @Select("SELECT * FROM transaction_posting where account_id=#{accountId}")
    List<TransactionPosting> getTransactionPostingsByAccount(@Param("accountId") Long accountId);
}
