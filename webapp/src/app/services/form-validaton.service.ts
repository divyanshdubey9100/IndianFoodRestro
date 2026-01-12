import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { VALIDATION_PATTERNS } from '../form-validation/form-validation.config';
import { VALIDATION_ERRORS } from '../validation-message/validation-message';
import { DateValidationService } from './date-validation.service';

@Injectable({ providedIn: 'root' })
export class FormValidatorService {

  constructor(
    private dateValidationService: DateValidationService
  ) {}

  // ✅ Pattern validator
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

  // ✅ Centralized error message resolver
  getErrorMessage(control: AbstractControl | null): string {
    if (!control || !control.errors) return '';

    const firstKey =
      Object.keys(control.errors)[0] as keyof typeof VALIDATION_ERRORS;

    const messageOrFn = VALIDATION_ERRORS[firstKey];
    const validatorValue = control.errors[firstKey];

    return typeof messageOrFn === 'function'
      ? messageOrFn(validatorValue)
      : messageOrFn ?? '';
  }

  // ✅ CORRECT: call DateValidationService
  getDobValidator(
    minAge = 18,
    minYear = 1900
  ): ValidatorFn {
    return this.dateValidationService.dobValidator(minAge, minYear);
  }
}
