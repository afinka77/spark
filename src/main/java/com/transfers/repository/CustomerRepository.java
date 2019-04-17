package com.transfers.repository;

import com.transfers.domain.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerRepository {
    @Select("SELECT * FROM account")
    List<Customer> getCustomers();
}
