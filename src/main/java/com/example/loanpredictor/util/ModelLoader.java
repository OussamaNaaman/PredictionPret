package com.example.loanpredictor.util;

import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ModelLoader {

    private static final Logger log = LoggerFactory.getLogger(ModelLoader.class);
    private Booster model;

    @Value("classpath:loan_amount_model.bin")
    private Resource modelResource;


    @PostConstruct
    public void init() throws IOException {
        this.model = loadModel();
        log.info("Model loaded successfully (Model Loader component.)");

    }

    public Booster getModel() {
        return model;
    }

    private Booster loadModel() throws IOException {
        Booster loadedModel = null;
        try (InputStream modelInputStream = modelResource.getInputStream()) {
            loadedModel = XGBoost.loadModel(modelInputStream);
            if (loadedModel == null) {
                throw new IOException("XGBoost failed to load the model from the provided input stream, please check the file format, if it is an xgboost model or try loading with python library first if it can be loaded correctly. ");
            }
        } catch (Exception ex) {

            log.error("Error loading model: An XGBoost Java specific Model loader data properties related exception using a file as resource, can not  load implementation for current configuration. Verify model/or path at method parameter, if path or a library type / data model value format parameter are corrupted :  ", ex);
            throw new IOException("An XGBoost Java specific Model loader data properties related exception using a file as resource, can not  load implementation for current configuration. Verify model/or path at method parameter, if path or a library type / data model value format parameter are corrupted :  ", ex);
        }
        return loadedModel;

    }
    public Double predictLoanAmount(DMatrix input) throws XGBoostError {
        if (model== null)
            return null;
        float[][] predictedValues = model.predict(input);
        if (predictedValues.length > 0 && predictedValues[0].length > 0) {
            return (double) predictedValues[0][0];
        }
        return null;
    }


    public DMatrix prepareDataMatrix(Map<String, Float> inputData) throws XGBoostError {
        int numFeatures = 21;
        float[] featureArray = new float[numFeatures];
        List<String> featureOrder = Arrays.asList(
                "ApplicantIncome", "CoapplicantIncome", "Loan_Amount_Term",
                "Dependents_0", "Dependents_1", "Dependents_2", "Dependents_3+",
                "Gender_Male", "Gender_Female", "Married_Yes", "Married_No",
                "Education_Graduate", "Education_Not Graduate", "Self_Employed_Yes", "Self_Employed_No",
                "Property_Area_Rural", "Property_Area_Semiurban", "Property_Area_Urban", "Credit_History_1", "Credit_History_0",
                "TotalIncome"
        );

        for(int i = 0; i < featureOrder.size() - 1; i++){
            featureArray[i] = inputData.getOrDefault(featureOrder.get(i),0.0f);
        }
        float totalIncome = inputData.get("ApplicantIncome") + inputData.get("CoapplicantIncome");
        featureArray[featureOrder.size() - 1] = totalIncome;
        int numRows = 1;
        int numCols = numFeatures;
        DMatrix dataMatrix = new DMatrix(featureArray,numRows, numCols);

        return dataMatrix;
    }

    public Map<String, Float> prepareInputData(Map<String, String> formData){
        Map<String, Float> inputData = new HashMap<>();
        inputData.put("ApplicantIncome", Float.parseFloat(formData.get("ApplicantIncome")));
        inputData.put("CoapplicantIncome", Float.parseFloat(formData.get("CoapplicantIncome")));
        inputData.put("Loan_Amount_Term", Float.parseFloat(formData.get("Loan_Amount_Term")));
        inputData.put("Dependents_0", Float.parseFloat(formData.get("Dependents_0")));
        inputData.put("Dependents_1", Float.parseFloat(formData.get("Dependents_1")));
        inputData.put("Dependents_2", Float.parseFloat(formData.get("Dependents_2")));
        inputData.put("Dependents_3+", Float.parseFloat(formData.get("Dependents_3+")));
        inputData.put("Gender_Male", Float.parseFloat(formData.get("Gender_Male")));
        inputData.put("Gender_Female", Float.parseFloat(formData.get("Gender_Female")));
        inputData.put("Married_Yes", Float.parseFloat(formData.get("Married_Yes")));
        inputData.put("Married_No", Float.parseFloat(formData.get("Married_No")));
        inputData.put("Education_Graduate", Float.parseFloat(formData.get("Education_Graduate")));
        inputData.put("Education_Not Graduate", Float.parseFloat(formData.get("Education_Not Graduate")));
        inputData.put("Self_Employed_Yes", Float.parseFloat(formData.get("Self_Employed_Yes")));
        inputData.put("Self_Employed_No", Float.parseFloat(formData.get("Self_Employed_No")));
        inputData.put("Property_Area_Rural", Float.parseFloat(formData.get("Property_Area_Rural")));
        inputData.put("Property_Area_Semiurban", Float.parseFloat(formData.get("Property_Area_Semiurban")));
        inputData.put("Property_Area_Urban", Float.parseFloat(formData.get("Property_Area_Urban")));
        inputData.put("Credit_History_1", Float.parseFloat(formData.get("Credit_History_1")));
        inputData.put("Credit_History_0", Float.parseFloat(formData.get("Credit_History_0")));
        return inputData;
    }


    public static void main(String[] args) {
        // a test case main java  entry method only to check/verify implementation using Java
    }

}