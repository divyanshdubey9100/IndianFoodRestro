// pattern-validator.service.ts
import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { VALIDATION_PATTERNS } from '../validation-pattern/form-validation.config';
import { VALIDATION_ERRORS } from '../validation-message/validation-message.message';

@Injectable({ providedIn: 'root' })
export class FormValidatorService {
    getValidator(
        patternKey: keyof typeof VALIDATION_PATTERNS,
        errorMessageKey: keyof typeof VALIDATION_ERRORS
    ): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!control.value) return null;

            const pattern = new RegExp(VALIDATION_PATTERNS[patternKey]);

            return pattern.test(control.value)
                ? null
                : { [errorMessageKey]: true };
        };
    }

    getErrorMessage(control: AbstractControl | null): string {
        if (!control || !control.errors) return '';
        const firstKey = Object.keys(control.errors)[0] as keyof typeof VALIDATION_ERRORS;
        const messageOrFn = VALIDATION_ERRORS[firstKey];
        const validatorValue = control.errors[firstKey];
        if (typeof messageOrFn === 'function') {
            try {
                return messageOrFn(validatorValue);
            } catch {
                return '';
            }
        }
        return messageOrFn ?? '';
    }
}
