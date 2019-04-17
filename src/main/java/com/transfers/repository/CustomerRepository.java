package com.transfers.repository;

import com.transfers.domain.Account;
import com.transfers.domain.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerRepository {
    @Results(value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "account", column = "account_id", javaType = Account.class, one = @One(select = "getAccount"))})
    @Select("SELECT * FROM customer")
    List<Customer> getCustomers();


    @Select("SELECT * FROM account where id=#{account_id}")
    Account getAccount(@Param("account_id") Long accountId);
}
