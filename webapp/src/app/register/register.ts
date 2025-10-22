import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  templateUrl: './register.html',
  styleUrls: ['./register.css'],
  imports: [FormsModule, RouterLink, CommonModule]
})
export class RegisterComponent {
  firstName = '';
  middleName = '';
  lastName = '';
  mobile = '';
  phone = '';
  dob = '';
  gender = '';
  userType = '';
  email = '';
  password = '';
  confirmPassword = '';
  
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  async onSubmit() {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    try {
      const result = await this.authService.register({
        firstName: this.firstName,
        middleName: this.middleName,
        lastName: this.lastName,
        mobile: this.mobile,
        dob: this.dob,
        gender: this.gender,
        userType: this.userType,
        email: this.email,
        password: this.password
      });

      if (result.success) {
        this.successMessage = result.message;
        // Redirect to login page after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/sign-in']);
        }, 2000);
      } else {
        this.errorMessage = result.message;
      }
    } catch (error) {
      this.errorMessage = 'Registration failed. Please try again later.';
    } finally {
      this.isLoading = false;
    }
  }

  private validateForm(): boolean {
    // Check required fields
    if (!this.firstName || !this.lastName || !this.mobile || !this.dob || 
        !this.gender || !this.userType || !this.email || !this.password) {
      this.errorMessage = 'Please fill in all required fields.';
      return false;
    }

    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.errorMessage = 'Please enter a valid email address.';
      return false;
    }

    // Validate password
    if (this.password.length < 6) {
      this.errorMessage = 'Password must be at least 6 characters long.';
      return false;
    }

    // Validate password confirmation
    if (this.password !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return false;
    }

    // Validate mobile number
    const mobileRegex = /^[6-9]\d{9}$/;
    if (!mobileRegex.test(this.mobile)) {
      this.errorMessage = 'Please enter a valid 10-digit mobile number.';
      return false;
    }

    return true;
  }

  // Check if email is already registered
  async checkEmailAvailability() {
    if (this.email && this.authService.isEmailRegistered(this.email)) {
      this.errorMessage = 'This email is already registered.';
    } else {
      this.errorMessage = '';
    }
  }

  // Check if mobile is already registered
  async checkMobileAvailability() {
    if (this.mobile && this.authService.isMobileRegistered(this.mobile)) {
      this.errorMessage = 'This mobile number is already registered.';
    } else {
      this.errorMessage = '';
    }
  }
}
