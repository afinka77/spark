package com.transfers.repository;

import com.transfers.domain.Customer;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CustomerRepository {
    @Select("SELECT * FROM customer")
    List<Customer> getCustomers();

    @Results(value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "accounts",
                    column = "id",
                    javaType = List.class,
                    many = @Many(select = "com.transfers.repository.AccountRepository.getAccounts"))})
    @Select("SELECT * FROM customer WHERE id=#{customerId}")
    Customer getCustomer(@Param("customerId") Long customerId);

    @Insert("INSERT into customer (name) values (#{name})")
    Long insert(Customer customer);

    @Update("UPDATE customer SET name=#{name},modified_on=now() where id=#{id}")
    void update(Customer customer);

    @Delete("DELETE customer where id=#{customerId}")
    void delete(@Param("customerId") Long customerId);
}
