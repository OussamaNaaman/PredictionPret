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
import org.springframework.util.StringUtils;

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
                throw new IOException("XGBoost failed to load the model.");
            }
        } catch (Exception ex) {
            log.error("Error loading model: ", ex);
            throw new IOException("Error loading XGBoost model", ex);
        }
        return loadedModel;
    }

    public Double predictLoanAmount(DMatrix input) throws XGBoostError {
        if (model == null) {
            return null;
        }
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
                "TotalIncome" // Note: TotalIncome is expected in the feature order too now
        );

        for (int i = 0; i < featureOrder.size() - 1; i++) {
            featureArray[i] = inputData.getOrDefault(featureOrder.get(i), 0.0f);
        }
        float totalIncome = inputData.get("ApplicantIncome") + inputData.get("CoapplicantIncome");
        featureArray[featureOrder.size() - 1] = totalIncome; // Assign TotalIncome value at the last index

        int numRows = 1;
        int numCols = numFeatures;
        return new DMatrix(featureArray, numRows, numCols);
    }


    public Map<String, Float> prepareInputData(Map<String, String> formData) {
        Map<String, Float> inputData = new HashMap<>();
        InputParser parser = new InputParser();

        // 1. Income and Loan Term (Still Parse as Floats)
        inputData.put("ApplicantIncome", parser.parseFloatValue(formData.get("ApplicantIncome"), "ApplicantIncome"));
        inputData.put("CoapplicantIncome", parser.parseFloatValue(formData.get("CoapplicantIncome"), "CoapplicantIncome"));
        inputData.put("Loan_Amount_Term", parser.parseFloatValue(formData.get("Loan_Amount_Term"), "Loan_Amount_Term"));

        // 2. Dependents - Handle String dropdown values and convert to binary
        String dependentsValue = formData.get("Dependents_0"); // Get string value from dropdown
        inputData.put("Dependents_0", "0".equals(dependentsValue) ? 1.0f : 0.0f);
        inputData.put("Dependents_1", "1".equals(dependentsValue) ? 1.0f : 0.0f);
        inputData.put("Dependents_2", "2".equals(dependentsValue) ? 1.0f : 0.0f);
        inputData.put("Dependents_3+", "3+".equals(dependentsValue) ? 1.0f : 0.0f);


        // 3. Gender - Handle String dropdown, convert to binary
        String genderValue = formData.get("Gender_Male");
        inputData.put("Gender_Male", "Male".equals(genderValue) ? 1.0f : 0.0f);
        inputData.put("Gender_Female", "Female".equals(genderValue) ? 1.0f : 0.0f);

        // 4. Married - Handle String dropdown, convert to binary
        String marriedValue = formData.get("Married_Yes");
        inputData.put("Married_Yes", "Yes".equals(marriedValue) ? 1.0f : 0.0f);
        inputData.put("Married_No", "No".equals(marriedValue) ? 1.0f : 0.0f);

        // 5. Education - Handle String dropdown, convert to binary
        String educationValue = formData.get("Education_Graduate");
        inputData.put("Education_Graduate", "Graduate".equals(educationValue) ? 1.0f : 0.0f);
        inputData.put("Education_Not Graduate", "Not Graduate".equals(educationValue) ? 1.0f : 0.0f);

        // 6. Self Employed - Handle String dropdown, convert to binary
        String selfEmployedValue = formData.get("Self_Employed_Yes");
        inputData.put("Self_Emoyed_Yes", "Yes".equals(selfEmployedValue) ? 1.0f : 0.0f); // Typo in original name fixed to Self_Employed_Yes
        inputData.put("Self_Employed_No", "No".equals(selfEmployedValue) ? 1.0f : 0.0f);

        // 7. Property Area - Handle String dropdown, convert to binary
        String propertyAreaValue = formData.get("Property_Area_Rural");
        inputData.put("Property_Area_Rural", "Rural".equals(propertyAreaValue) ? 1.0f : 0.0f);
        inputData.put("Property_Area_Semiurban", "Semiurban".equals(propertyAreaValue) ? 1.0f : 0.0f);
        inputData.put("Property_Area_Urban", "Urban".equals(propertyAreaValue) ? 1.0f : 0.0f);

        // 8. Credit History - Handle String dropdown, convert to binary
        String creditHistoryValue = formData.get("Credit_History_1");
        inputData.put("Credit_History_1", "Good".equals(creditHistoryValue) ? 1.0f : 0.0f); // Good credit
        inputData.put("Credit_History_0", "Bad".equals(creditHistoryValue) ? 1.0f : 0.0f);  // Bad credit


        return inputData;
    }


    // **Inner helper class for parsing with error handling and default value**
    private static class InputParser {
        public Float parseFloatValue(String valueStr, String fieldName) {
            float floatValue = 0.0f; // Default value
            if (StringUtils.hasText(valueStr)) { // Check for null and non-empty
                try {
                    floatValue = Float.parseFloat(valueStr.trim()); // Parse if valid
                } catch (NumberFormatException e) {
                    log.warn("Invalid input for field '{}': '{}'. Using default value 0.0", fieldName, valueStr); //Log warning
                    // You could choose to handle NumberFormatException differently, e.g., throw an exception, return null, etc.
                }
            } else {
                log.warn("Field '{}' is missing or empty. Using default value 0.0", fieldName); //Log warning
            }
            return floatValue;
        }
    }


    public static void main(String[] args) {
        // a test case main java  entry method only to check/verify implementation using Java
    }
}