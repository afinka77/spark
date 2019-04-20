package com.transfers.repository;

import com.transfers.domain.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TransactionRepository {
    @Insert({"INSERT INTO transaction (payment_id)",
            " VALUES (#{paymentId})"})
    Long insert(@Param("paymentId") Long paymentId);

    @Results(value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "transactionPostings",
                    column = "id",
                    javaType = List.class,
                    many = @Many(select = "com.transfers.repository.TransactionPostingRepository.getTransactionPostings"))})
    @Select("SELECT * FROM transaction WHERE payment_id=#{paymentId}")
    Transaction getTransactionByPaymentId(@Param("paymentId") Long paymentId);
}
