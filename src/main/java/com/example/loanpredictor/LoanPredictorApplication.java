package com.example.loanpredictor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.example.loanpredictor", "com.example.loanpredictor.util"}) // <-- Add this line
public class LoanPredictorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanPredictorApplication.class, args);
    }
}