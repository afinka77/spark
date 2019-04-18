package com.transfers.repository;

import com.transfers.domain.Account;
import com.transfers.domain.Customer;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerRepository {
    @Select("SELECT * FROM customer")
    List<Customer> getCustomers();

    @Results(value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "accounts", column = "id", javaType = List.class, many = @Many(select = "getAccounts"))})
    @Select("SELECT * FROM customer where id=#{customerId}")
    Customer getCustomer(@Param("customerId") Long customerId);


    @Select("SELECT * FROM account where customer_id=#{customerId}")
    List<Account> getAccounts(@Param("customerId") Long customerId);
}
