package com.transfers.repository;

import com.transfers.domain.Payment;
import com.transfers.domain.Transaction;
import com.transfers.domain.enums.PaymentStatus;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PaymentRepository {
    @Results(value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "debtorAccountName",
                    column = "debtor_account_id",
                    javaType = String.class,
                    one = @One(select = "com.transfers.repository.AccountRepository.getAccountName")),
            @Result(property = "creditorAccountName",
                    column = "creditor_account_id",
                    javaType = String.class,
                    one = @One(select = "com.transfers.repository.AccountRepository.getAccountName"))})
    @Select("SELECT * FROM payment WHERE customer_id=#{customerId}")
    List<Payment> getPayments(@Param("customerId") Long customerId);

    @Results(value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "debtorAccountName",
                    column = "debtor_account_id",
                    javaType = String.class,
                    one = @One(select = "com.transfers.repository.AccountRepository.getAccountName")),
            @Result(property = "creditorAccountName",
                    column = "creditor_account_id",
                    javaType = String.class,
                    one = @One(select = "com.transfers.repository.AccountRepository.getAccountName")),
            @Result(property = "transaction",
                    column = "id",
                    javaType = Transaction.class,
                    one = @One(select = "com.transfers.repository.TransactionRepository.getTransactionByPaymentId"))})
    @Select("SELECT * FROM payment WHERE customer_id=#{customerId} and id=#{paymentId}")
    @Options
    Payment getPayment(@Param("customerId") Long customerId, @Param("paymentId") Long paymentId);

    @Insert({"INSERT INTO payment (method, amount,  message, status, error_message,",
            " debtor_account_id, creditor_account_id, customer_id) values",
            " (#{payment.method}, #{payment.amount}, #{payment.message},  #{payment.status}, null, #{toAccountId}, #{fromAccountId}, #{payment.customerId})"})
    Long insert(@Param("payment") Payment payment,
              @Param("fromAccountId") Long fromAccountId,
              @Param("toAccountId") Long toAccountId);

    @Update("UPDATE payment SET status=#{status}, error_message=#{errorMessage} where id=#{paymentId}")
    void updateStatus(@Param("paymentId") Long paymentId,
                      @Param("status")PaymentStatus paymentStatus,
                      @Param("errorMessage") String errorMessage);
}
