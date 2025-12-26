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
    this.registerUserForm=this.formData.group({
      firstName: ['', [Validators.required, this.formValidator.getValidator('namePattern')]],
      middleName: ['', [Validators.minLength(2), this.formValidator.getValidator('namePattern')]],
      lastName: ['', [Validators.required, this.formValidator.getValidator('namePattern')]]
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
    return this.formValidator.getErrorMessage(this.getControl(controlName));
  }
  
}