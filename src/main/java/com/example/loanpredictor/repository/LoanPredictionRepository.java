package com.example.loanpredictor.repository;

import com.example.loanpredictor.entity.LoanPrediction;
import com.example.loanpredictor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository

public interface LoanPredictionRepository extends JpaRepository<LoanPrediction, Long> {
    List<LoanPrediction> findByUser(User user);

}