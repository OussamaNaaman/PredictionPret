document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('loanForm');
    const loadingDiv = document.querySelector('.loading');
    const predictionDiv = document.getElementById('predictionResult');
    const errorDiv = document.getElementById('errorResult');
    const reportLinkContainerDiv = document.getElementById('reportLinkContainer'); // NEW
    const generateReportLink = document.getElementById('generateReportLink'); // NEW

    const creditSlider = document.getElementById('creditSlider');
    const dependentsSlider = document.getElementById('dependentsSlider');
    const creditValueDisplay = document.getElementById('creditValue');
    const dependentsValueDisplay = document.getElementById('dependentsValue');

    // Get CSRF token and header name from meta tags (if applicable)
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    form.addEventListener('submit', function (event) {
        event.preventDefault();

        loadingDiv.style.display = "block";
        predictionDiv.style.display = "none";
        errorDiv.style.display = "none";
        reportLinkContainerDiv.style.display = "none"; // **NEW**: Hide report link on new prediction


        const formData = new FormData(form);
        const formDataObject = {};

        // Income, Loan Term, Dropdowns - as before
        formDataObject['ApplicantName'] = formData.get('ApplicantName'); // **NEW**: Get Applicant Name
        formDataObject['ApplicantIncome'] = parseFloat(formData.get('ApplicantIncome'));
        formDataObject['CoapplicantIncome'] = parseFloat(formData.get('CoapplicantIncome'));
        formDataObject['Loan_Amount_Term'] = formData.get('Loan_Amount_Term');
        formDataObject['Dependents_0'] = formData.get('Dependents_0');
        formDataObject['Dependents_1'] = formData.get('Dependents_1');
        formDataObject['Dependents_2'] = formData.get('Dependents_2');
        formDataObject['Dependents_3+'] = formData.get('Dependents_3+');
        formDataObject['Gender_Male'] = formData.get('Gender_Male');
        formDataObject['Gender_Female'] = formData.get('Gender_Male') === 'Female' ? 1 : 0;
        formDataObject['Married_Yes'] = formData.get('Married_Yes');
        formDataObject['Married_No'] = formData.get('Married_Yes') === 'No' ? 1 : 0;
        formDataObject['Education_Graduate'] = formData.get('Education_Graduate');
        formDataObject['Education_Not Graduate'] = formData.get('Education_Graduate') === 'Not Graduate' ? 1 : 0;
        formDataObject['Self_Employed_Yes'] = formData.get('Self_Employed_Yes');
        formDataObject['Self_Employed_No'] = formData.get('Self_Employed_Yes') === 'No' ? 1 : 0;
        formDataObject['Property_Area_Rural'] = formData.get('Property_Area_Rural');
        formDataObject['Property_Area_Semiurban'] = formData.get('Property_Area_Rural') === 'Semiurban' ? 1 : 0;
        formDataObject['Property_Area_Urban'] = formData.get('Property_Area_Rural') === 'Urban' ? 1 : 0;
        formDataObject['Credit_History_1'] = formData.get('Credit_History_1');
        formDataObject['Credit_History_0'] = formData.get('Credit_History_1') === 'Bad' ? 1 : 0;


        // --- Fetch API Call ---
        fetch('/', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeaderName]: csrfToken
            },
            body: JSON.stringify(formDataObject)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                loadingDiv.style.display = "none";
                if (data.prediction) {
                    predictionDiv.textContent = 'Predicted Loan Amount: $' + (data.prediction * 1000);
                    predictionDiv.style.display = "block";
                    errorDiv.style.display = "none";
                    reportLinkContainerDiv.style.display = "block"; // **NEW**: Show report link on successful prediction
                    // **NEW**: Update report link href with data (simplest for now, session/backend would be better for complex data)
                    generateReportLink.href = `/report?applicantName=${encodeURIComponent(formDataObject.ApplicantName)}&predictedAmount=${encodeURIComponent(data.prediction * 1000)}&loanTerm=${encodeURIComponent(formDataObject.Loan_Amount_Term)}`;

                } else if (data.error) {
                    errorDiv.textContent = data.error;
                    errorDiv.style.display = "block";
                    predictionDiv.style.display = "none";
                    reportLinkContainerDiv.style.display = "none"; // Ensure report link is hidden on error
                } else {
                    errorDiv.textContent = "Unknown error occurred.";
                    errorDiv.style.display = "block";
                    predictionDiv.style.display = "none";
                    reportLinkContainerDiv.style.display = "none"; // Ensure hidden on unknown error too
                }
            })
            .catch(error => {
                loadingDiv.style.display = "none";
                errorDiv.textContent = 'Error fetching prediction: HTTP error! status: ' + error.message;
                errorDiv.style.display = "block";
                predictionDiv.style.display = "none";
                reportLinkContainerDiv.style.display = "none"; // Hide link on fetch error as well
                console.error('Fetch error:', error);
            });
    });

    // Slider and Clear button event listeners - unchanged ...
    // (Credit Slider, Dependents Slider, clearBtn event listeners - remain the same)
    // ...
});

