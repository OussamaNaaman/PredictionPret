package com.example.loanpredictor.service;

import com.example.loanpredictor.entity.LoanPrediction;
import com.example.loanpredictor.entity.User;
import com.example.loanpredictor.repository.LoanPredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class LoanPredictionService {
    private final LoanPredictionRepository loanPredictionRepository;

    public  LoanPredictionService(LoanPredictionRepository loanPredictionRepository) {
        this.loanPredictionRepository = loanPredictionRepository; // implement value with required type param. by Spring Dependency system via method  explicit parameters of their correct types by implementing with a proper java code  using Spring java annotation pattern on this component with constructor of specific object parameter types implementation before execution

    }


    public List<LoanPrediction> getPredictionsByUser(User user){
        return loanPredictionRepository.findByUser(user);
    }

    public void saveLoanPrediction(LoanPrediction loanPrediction){
        loanPredictionRepository.save(loanPrediction);
    }
}