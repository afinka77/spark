package com.transfers.repository;

import com.transfers.domain.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaymentRepository {
    @Select("SELECT * FROM payment")
    List<Payment> getPayments();
}
