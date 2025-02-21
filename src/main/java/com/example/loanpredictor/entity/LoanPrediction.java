package com.example.loanpredictor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "loan_predictions")
public class LoanPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(columnDefinition = "TEXT")
    private String predictionData;
    @Column(nullable = false)
    private Double predictedAmount;
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public String getPredictionData() {
        return predictionData;
    }

    public void setPredictionData(String predictionData) {
        this.predictionData = predictionData;
    }

    public Double getPredictedAmount() {
        return predictedAmount;
    }

    public void setPredictedAmount(Double predictedAmount) {
        this.predictedAmount = predictedAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setUser(User user){
        this.user = user;
    }
}