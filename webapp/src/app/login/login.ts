import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';


@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
  imports: [FormsModule, RouterLink]
})
export class LoginComponent {
  email = '';
  password = '';
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  async onSubmit() {
    if (this.email && this.password.length >= 6) {
      this.isLoading = true;
      this.errorMessage = '';

      try {
        const success = await this.authService.login(this.email, this.password);
        
        if (success) {
          // Navigate to profile page on successful login
          this.router.navigate(['/profile']);
        } else {
          this.errorMessage = 'Invalid email or password. Please try again.';
        }
      } catch (error) {
        this.errorMessage = 'Login failed. Please try again later.';
      } finally {
        this.isLoading = false;
      }
    }
  }

  // Method to show demo credentials
  fillDemoCredentials(userType: 'admin' | 'staff' | 'user') {
    switch (userType) {
      case 'admin':
        this.email = 'admin@restaurant.com';
        break;
      case 'staff':
        this.email = 'staff@restaurant.com';
        break;
      case 'user':
        this.email = 'user@example.com';
        break;
    }
    this.password = 'password123';
  }
}
