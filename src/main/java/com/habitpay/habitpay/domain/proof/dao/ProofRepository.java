package com.habitpay.habitpay.domain.proof.dao;

import com.habitpay.habitpay.domain.proof.domain.Proof;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProofRepository extends JpaRepository<Proof, Long> {

}
