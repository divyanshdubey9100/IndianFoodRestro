import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormValidationService } from '../services/form-validation.service';
import { FormValidationMessageService } from '../services/validation-message.service';

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
    private patternValidator:
            FormValidationService
  ) {}
  ngOnInit(): void {
    this.registerUserForm=this.formData.group({
      firstName: ['', [Validators.required, this.patternValidator.getValidator('namePattern')]],
    })
  }

  getErrorMessage(controlName: string): string | null {
    const control = this.registerUserForm.get(controlName);
    if (!control || !control.errors) {
      return null;
    }

    const errorKey = Object.keys(control.errors)[0];
    const errorValue = control.errors[errorKey];

    // If our custom pattern validator returned a message object, use it
    if (errorKey === 'pattern' && errorValue && (errorValue as any).message) {
      return (errorValue as any).message;
    }

    // Fall back to the message service (handles required, minlength, etc.)
    return FormValidationMessageService.getValidatorErrorMessage(errorKey, errorValue);
  }

  onSubmitUserForm(){
    console.log(this.registerUserForm.value);
  }

  
}