import { Injectable } from "@angular/core";
import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";

@Injectable({ providedIn: 'root' })
export class DateValidationService {

  dobValidator(minAge = 18, minYear = 1900): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value: string = control.value;

      // Handle empty value
      if (!value || value.trim() === '') {
        return { required: true };
      }

      const parts = value.split('-');
      if (parts.length !== 3) {
        return { invalidDate: true };
      }

      const [year, month, day] = parts.map(Number);
      
      // Check if parsing resulted in valid numbers
      if (isNaN(year) || isNaN(month) || isNaN(day)) {
        return { invalidDate: true };
      }

      const dob = new Date(year, month - 1, day);

      // Validate that the date is actually valid (handles invalid dates like Feb 30)
      if (
        dob.getFullYear() !== year ||
        dob.getMonth() !== month - 1 ||
        dob.getDate() !== day
      ) {
        return { invalidDate: true };
      }

      const today = new Date();
      today.setHours(0, 0, 0, 0);
      dob.setHours(0, 0, 0, 0);

      if (dob > today) return { futureDate: true };
      if (year < minYear) return { yearOutOfRange: true };

      let age = today.getFullYear() - year;
      if (
        today.getMonth() < month - 1 ||
        (today.getMonth() === month - 1 && today.getDate() < day)
      ) {
        age--;
      }

      return age < minAge ? { underAge: true } : null;
    };
  }
}
