import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, FormControl } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormValidatorService } from '../services/form-validaton.service';
import { DatePickerComponent } from '../date-picker/date-picker.component';

@Component({
  selector: 'app-register',
  standalone: true,
  templateUrl: './register.html',
  styleUrls: ['./register.css'],
  imports: [CommonModule, 
            ReactiveFormsModule,
            DatePickerComponent         
          ],
})
export class RegisterComponent implements OnInit{
 
  registerUserForm !: FormGroup
  today = new Date().toISOString().split('T')[0];
  isSubmitting = false;
  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private formData: FormBuilder,
    private formValidator: FormValidatorService
  ) {}

  passwordMatchValidator = (formGroup: AbstractControl): { [key: string]: any } | null => {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  };
  ngOnInit(): void {
    this.registerUserForm = this.formData.group({
      firstName: ['', [Validators.required, Validators.minLength(2), this.formValidator.getValidator('name', 'name')]],
      middleName: ['', [Validators.minLength(2), this.formValidator.getValidator('name', 'name')]],
      lastName: ['', [Validators.required, Validators.minLength(2), this.formValidator.getValidator('name', 'name')]],
      mobile: ['', [Validators.required, this.formValidator.getValidator('mobile', 'mobile')]],
      dob: new FormControl<string>('', {
          nonNullable: true,
          validators: [this.formValidator.getDobValidator(18, 1900)],
        }),
      gender: ['', [Validators.required]],
      userType: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required, 
        Validators.minLength(8),
        this.formValidator.getValidator('password', 'passwordStrength')
      ]],
      confirmPassword: ['', [
        Validators.required,
        Validators.minLength(8),
        this.formValidator.getValidator('password', 'passwordStrength')
      ]]
    }, { validators: this.passwordMatchValidator });
  }

  onSubmitUserForm(){
    if (this.registerUserForm.invalid) {
      this.registerUserForm.markAllAsTouched();
      return;
    }
    
    this.isSubmitting = true;
    
    // Simulate API call or add your registration logic here
    console.log('Registration Data:', this.registerUserForm.value);
    
    // Reset the submitting state after completion
    // In a real app, this would be done in the success/error callback
    setTimeout(() => {
      this.isSubmitting = false;
      // You might want to navigate to login page or show success message
    }, 2000);
  }

  getControl(name: string): AbstractControl | null {
    return this.registerUserForm.get(name);
  }

  showErrors(control: AbstractControl | null): boolean {
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  getErrorMessage(controlName: string): string {
    const control = this.getControl(controlName);
    
    // Handle missing controls gracefully
    if (!control) {
      return '';
    }
    
    // Check for form-level errors first (cross-field validations)
    if (this.hasFormLevelError(controlName)) {
      return this.getFormLevelErrorMessage(controlName);
    }
    
    // No field-level errors
    if (!control.errors) {
      return '';
    }
    
    // Delegate to validation service for all error handling
    let errorMessage = this.formValidator.getErrorMessage(control);
    
    // Enhance message with proper field names for better UX
    if (errorMessage) {
      errorMessage = this.personalizeErrorMessage(errorMessage, controlName);
    }
    
    return errorMessage || `${this.getDisplayName(controlName)} is invalid`;
  }
  
  private hasFormLevelError(controlName: string): boolean {
    return controlName === 'confirmPassword' && 
           this.registerUserForm.hasError('passwordMismatch');
  }
  
  private getFormLevelErrorMessage(controlName: string): string {
    if (controlName === 'confirmPassword' && this.registerUserForm.hasError('passwordMismatch')) {
      return 'Passwords do not match';
    }
    return '';
  }
  
  private personalizeErrorMessage(message: string, controlName: string): string {
    const displayName = this.getDisplayName(controlName);
    
    // Replace common generic terms with specific field names
    return message
      .replace(/\bthis field\b/gi, displayName)
      .replace(/\bfield\b/gi, displayName)
      .replace(/\bcontrol\b/gi, displayName)
      .replace(/\binput\b/gi, displayName)
      .replace(/\bvalue\b/gi, displayName);
  }

  private getDisplayName(controlName: string): string {
    // Convert camelCase to proper display name
    // e.g., 'firstName' -> 'First Name', 'confirmPassword' -> 'Confirm Password'
    return controlName
      .replace(/([A-Z])/g, ' $1') // Add space before uppercase letters
      .replace(/^./, (str) => str.toUpperCase()) // Capitalize first letter
      .trim(); // Remove any leading/trailing spaces
  }

   get dob(): FormControl<string> {
    return this.registerUserForm.get('dob') as FormControl<string>;
  }
  
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
  
  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }
  
}