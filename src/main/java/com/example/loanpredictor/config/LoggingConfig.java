package com.example.loanpredictor.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class LoggingConfig {
    @Bean
    public Logger logger (){
        return LoggerFactory.getLogger("model loader Logger properties config Spring/Java Bean impl (from slf4j)");

    }

}