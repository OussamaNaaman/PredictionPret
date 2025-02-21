package com.example.loanpredictor.controller;

import com.example.loanpredictor.entity.LoanPrediction;
import com.example.loanpredictor.entity.User;
import com.example.loanpredictor.service.LoanPredictionService;
import com.example.loanpredictor.util.ModelLoader;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoostError;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    private final ModelLoader modelLoader;
    private final LoanPredictionService loanPredictionService;


    public MainController(ModelLoader modelLoader, LoanPredictionService loanPredictionService) { // added parameter  implementaion method
        this.modelLoader=modelLoader; //set value from implementation
        this.loanPredictionService = loanPredictionService;
    }
    @GetMapping("/")
    public String index(){
        return "index";
    }
    @PostMapping("/")
    public String index(@RequestParam Map<String, String> form, Model model,
                        @AuthenticationPrincipal User user, HttpServletRequest request) throws XGBoostError {

        Map<String, Float> inputData = modelLoader.prepareInputData(form);
        DMatrix dataMatrix = modelLoader.prepareDataMatrix(inputData);
        Double predictedAmount = modelLoader.predictLoanAmount(dataMatrix);
        if (predictedAmount == null){
            model.addAttribute("error", "Error during prediction, please try again");
            return "index";
        }

        LoanPrediction loanPrediction = new LoanPrediction();
        loanPrediction.setUser(user);
        loanPrediction.setPredictionData(form.toString());
        loanPrediction.setPredictedAmount(predictedAmount);
        loanPrediction.setTimestamp(LocalDateTime.now());
        loanPredictionService.saveLoanPrediction(loanPrediction);

        model.addAttribute("prediction", predictedAmount);
        return "index";
    }

    @GetMapping("/history")
    public String history(Model model, @AuthenticationPrincipal User user){
        List<LoanPrediction> predictions = loanPredictionService.getPredictionsByUser(user);
        model.addAttribute("predictions", predictions);
        return "history";
    }
}