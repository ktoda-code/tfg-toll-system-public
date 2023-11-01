// Element selectors
const signDiv = document.querySelector('#signDiv');
const logDiv = document.querySelector('#loginDiv');
const step1 = document.querySelector('#step-1-acc-registration');
const step2 = document.querySelector('#step-2-acc-registration');
const step3 = document.querySelector('#step-3-acc-registration');
const step4 = document.querySelector('#step-4-acc-registration');
const fieldsStep1 = document.querySelectorAll('#step-1-acc-registration input[required]');
const fieldsStep2 = document.querySelectorAll('#step-2-acc-registration input[required]');
const fieldsStep3 = document.querySelectorAll('#step-3-acc-registration input[required]');
const fieldsStep4 = document.querySelectorAll('#step-4-acc-registration input[required]');
const emailField = document.querySelector('#emailFieldCA');
const passwordFields = [document.querySelector('#passwordCA'), document.querySelector('#passwordFiled')];
const tooltips = [document.querySelector('#password-2'), document.querySelector('#password-1')];
const btns = [
    document.querySelector('#step-1-acc-registration .custom-btn-shape-color'),
    document.querySelector('#step-2-acc-registration #step-2-next'),
    document.querySelector('#step-3-acc-registration #step-3-next'),
    document.querySelector('#step-4-acc-registration #step-4-next')
];

function validateEmail() {
    if (emailField.validity.patternMismatch) {
        emailField.setCustomValidity('Please enter a valid email with .com domain.');
    } else {
        emailField.setCustomValidity('');
    }
}

function validatePassword(passwordField) {
    if (passwordField.validity.patternMismatch) {
        passwordField.setCustomValidity('Password must be 8-18 characters long with at least one digit, one special character, and one uppercase letter.');
    } else {
        passwordField.setCustomValidity('');
    }
}

function checkFields(fields, btn) {
    let allFilled = true;
    let allValid = true;
    fields.forEach(field => {
        if (!field.value) allFilled = false;
        if (!field.checkValidity()) allValid = false;
    });
    btn.disabled = !allFilled || !allValid;
}

function handleStepsValidation(btn, currentStep, nextStep, fields) {
    btn.addEventListener('click', function () {
        let allValid = true;
        fields.forEach(field => {
            if (!field.checkValidity()) {
                allValid = false;
            }
        });
        if (allValid) {
            currentStep.setAttribute('hidden', 'hidden');
            if (nextStep) {
                nextStep.removeAttribute('hidden');
            }
        }
    });
}

document.addEventListener('DOMContentLoaded', function () {
    // Check path
    const currentPath = window.location.pathname;

    // Check if the path is /login
    if (currentPath === '/login') {
        showSignIn();
    }

    // Check if the path is /sign
    if (currentPath === '/sign') {
        showSignUp();
    }

    // Email validation
    emailField.addEventListener('input', validateEmail);

    // Password validation
    passwordFields.forEach((field, index) => {
        field.addEventListener('input', () => validatePassword(field));
        field.addEventListener('focus', function () {
            tooltips[index].removeAttribute('hidden');
        });
        field.addEventListener('blur', function () {
            tooltips[index].setAttribute('hidden', 'hidden');
        });
    });

    // Fields validation
    fieldsStep1.forEach(field => {
        field.addEventListener('input', () => checkFields(fieldsStep1, btns[0]));
    });

    fieldsStep2.forEach(field => {
        field.addEventListener('input', () => checkFields(fieldsStep2, btns[1]));
    });

    fieldsStep3.forEach(field => {
        field.addEventListener('input', () => checkFields(fieldsStep3, btns[2]));
    });

    fieldsStep4.forEach(field => {
        field.addEventListener('input', () => checkFields(fieldsStep4, btns[3]));
    });

    // Handling steps validation
    handleStepsValidation(btns[0], step1, step2, fieldsStep1);
    handleStepsValidation(btns[1], step2, step3, fieldsStep2);
    handleStepsValidation(btns[2], step3, step4, fieldsStep3);
    handleStepsValidation(btns[3], step4, undefined, fieldsStep4);

    document.getElementById('category').addEventListener('change', function () {
        const descriptionDiv = document.getElementById('category-description');
        switch (this.value) {
            case 'CATEGORY_I':
                descriptionDiv.textContent = 'Two-Wheelers (Motorcycles), Cars';
                break;
            case 'CATEGORY_II':
                descriptionDiv.textContent = 'Caravan,Buses';
                break;
            case 'CATEGORY_III':
                descriptionDiv.textContent = 'Heavy Camions (Trucks), Tractors';
                break;
            default:
                descriptionDiv.textContent = '';
                break;
        }
    });
});

function showSignIn() {
    logDiv.removeAttribute('hidden');
    signDiv.setAttribute('hidden', 'hidden');
    window.history.pushState(null, null, '/login');
}

function showSignUp() {
    signDiv.removeAttribute('hidden');
    logDiv.setAttribute('hidden', 'hidden');
    window.history.pushState(null, null, '/sign');
}

function goToStep1() {
    step1.removeAttribute('hidden');
    step2.setAttribute('hidden', 'hidden');
}

function goToStep2() {
    step2.removeAttribute('hidden');
    step3.setAttribute('hidden', 'hidden');
}

function goToStep3() {
    step3.removeAttribute('hidden');
    step4.setAttribute('hidden', 'hidden');
}
