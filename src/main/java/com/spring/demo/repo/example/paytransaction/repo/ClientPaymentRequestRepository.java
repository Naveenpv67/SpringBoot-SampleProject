package com.example.paytransaction.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.paytransaction.entity.ClientPaymentRequestEntity;

public interface ClientPaymentRequestRepository extends JpaRepository<ClientPaymentRequestEntity, Integer> {

}
