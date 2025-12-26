import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormValidatorService } from '../services/form-validaton.service';

@Component({
  selector: 'app-register',
  standalone: true,
  templateUrl: './register.html',
  styleUrls: ['./register.css'],
  imports: [CommonModule, 
            ReactiveFormsModule,            
          ],
})
export class RegisterComponent implements OnInit{
 
  registerUserForm !: FormGroup

  constructor(
    private formData: FormBuilder,
    private formValidator: FormValidatorService
  ) {}
  ngOnInit(): void {
    this.registerUserForm = this.formData.group({
      firstName: ['', [Validators.required, Validators.minLength(2), this.formValidator.getValidator('name', 'name')]],
      middleName: ['', [Validators.minLength(2), this.formValidator.getValidator('name', 'name')]],
      lastName: ['', [Validators.required, Validators.minLength(2), this.formValidator.getValidator('name', 'name')]],
      mobile: ['', [Validators.required, this.formValidator.getValidator('mobile', 'mobile')]]
    })
  }

  onSubmitUserForm(){
    if (this.registerUserForm.invalid) {
      this.registerUserForm.markAllAsTouched();
      return;
    }
    console.log(this.registerUserForm.value);
  }

  getControl(name: string): AbstractControl | null {
    return this.registerUserForm.get(name);
  }

  showErrors(control: AbstractControl | null): boolean {
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  getErrorMessage(controlName: string): string {
    const control = this.getControl(controlName);
    if (!control || !control.errors) {
      return '';
    }
    if (control.errors['required']) return `${controlName} is required`;
    if (control.errors['minlength']) return `${controlName} must be at least ${control.errors['minlength'].requiredLength} characters`;
    if (control.errors['maxlength']) return `${controlName} cannot exceed ${control.errors['maxlength'].requiredLength} characters`;
    if (control.errors['pattern']) return `${controlName} format is invalid`;
    return this.formValidator.getErrorMessage(control);
  }
  
}