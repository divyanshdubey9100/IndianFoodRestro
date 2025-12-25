import { Injectable } from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class FormValidationMessageService {


    public static getValidatorErrorMessage(validatorName: string, validatorValue?: any) {
        const config: { [key: string]: string } = {
            // Standard Validators
            required: 'This field is required.',
            email: 'Please enter a valid email address.',
            pattern: 'The format is invalid.',
            name:'entered name should be valid String',

            // Dynamic Validators (using value)
            minlength: `Minimum length is ${validatorValue.requiredLength} characters.`,
            maxlength: `Maximum length is ${validatorValue.requiredLength} characters.`,

            // Custom Validators (match names from your ValidationService)
            cannotContainSpace: 'Spaces are not allowed in this field.',
            passwordStrength: ''
            
        }
        return config[validatorName];
    }

}