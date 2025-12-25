import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors, Validators, ValidatorFn } from '@angular/forms';
import { FormValidationMessageService } from './validation-message.service';

@Injectable({
    providedIn: 'root'
})
export class FormValidationService {

        static emailPattern: string = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
        static passwordPattern: string = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[A-Za-z\\d@$!%*?&]{8,}";
        static namePattern: string = "^[a-zA-Z ]*$";


    constructor() { }

    /**
     * Custom Validator: Checks if the input contains spaces.
     * Usage: validators: [ValidationService.cannotContainSpace]
     */
    static cannotContainSpace(control: AbstractControl): ValidationErrors | null {
        if ((control.value as string).indexOf(' ') >= 0) {
            return { cannotContainSpace: true };
        }
        return null;
    }

    static getRegex(validatorName: string, validatorValue?: any): string | undefined {
        const config: { [key: string]: string } = {
            emailPattern:  "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$",
            passwordPattern: "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[A-Za-z\\d@$!%*?&]{8,}",
            namePattern: "^[a-zA-Z ]*$"
        }
        return config[validatorName];
    }

    /**
     * Returns an Angular ValidatorFn for the named regex validator.
     * Usage: validators: [patternValidator.getValidator('namePattern')]
     */
    public getValidator(validatorName: string): ValidatorFn {
        const regex = FormValidationService.getRegex(validatorName);
        if (!regex) {
            return Validators.nullValidator;
        }

        const re = new RegExp(regex);
        return (control: AbstractControl): ValidationErrors | null => {
            const value = control.value;
            if (value === null || value === undefined || value === '') {
                return null; // leave required handling to Validators.required
            }
            const isValid = re.test(String(value));
            if (isValid) {
                return null;
            }

            const message = FormValidationMessageService.getValidatorErrorMessage('pattern');
            return { pattern: { message } };
        };
    }

}