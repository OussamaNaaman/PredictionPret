import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split, RandomizedSearchCV
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score
from sklearn.preprocessing import StandardScaler
from xgboost import XGBRegressor
from joblib import dump

# Load dataset
file_path = 'Classeur1.xlsx'
data = pd.read_excel(file_path, sheet_name='test_cleaned_ML')

# Feature Engineering
data['TotalIncome'] = data['ApplicantIncome'] + data['CoapplicantIncome']
data['IncomeToLoanRatio'] = np.log(data['TotalIncome'] / (data['LoanAmount'] + 1))
data['LoanAmountPerTerm'] = data['LoanAmount'] / data['Loan_Amount_Term']
data['DependentsSum'] = (
    data['Dependents_0'] * 0 +
    data['Dependents_1'] * 1 +
    data['Dependents_2'] * 2 +
    data['Dependents_3+'] * 3
)

# Drop rows with missing values for simplicity
data = data.dropna()

# Features and target
X = data.drop(columns=['LoanAmount'])  # Features
y = data['LoanAmount']                # Target: Loan Amount

# Train-test split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Scale features
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

# Train XGBRegressor
reg_model = XGBRegressor(random_state=42, n_estimators=300, learning_rate=0.05, max_depth=6)
reg_model.fit(X_train_scaled, y_train)

# Evaluate model
y_pred = reg_model.predict(X_test_scaled)
print("Mean Absolute Error (MAE):", mean_absolute_error(y_test, y_pred))
print("Mean Squared Error (MSE):", mean_squared_error(y_test, y_pred))
print("R² Score:", r2_score(y_test, y_pred))

# Save the model and scaler
dump(reg_model, 'loan_amount_model.joblib')
dump(scaler, 'scaler.joblib')
print("Model and scaler have been saved!")
