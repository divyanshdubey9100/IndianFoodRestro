import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface User {
  id: number;
  email: string;
  password: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  name: string;
  mobile: string;
  dob: string;
  gender: string;
  userType: 'admin' | 'user' | 'staff';
  phone?: string;
  address?: string;
  joinDate: Date;
}

export interface RegisterData {
  firstName: string;
  middleName?: string;
  lastName: string;
  mobile: string;
  dob: string;
  gender: string;
  userType: string;
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  // Mock users database for demonstration (these are default admin/demo users)
  private mockUsers: User[] = [
    {
      id: 1,
      email: 'admin@restaurant.com',
      password: 'password123',
      firstName: 'Admin',
      lastName: 'User',
      name: 'Admin User',
      mobile: '9876543210',
      dob: '1990-01-01',
      gender: 'Male',
      userType: 'admin',
      phone: '+1-555-0101',
      address: '123 Admin St, City, State',
      joinDate: new Date('2023-01-01')
    },
    {
      id: 2,
      email: 'staff@restaurant.com',
      password: 'password123',
      firstName: 'Staff',
      lastName: 'Member',
      name: 'Staff Member',
      mobile: '9876543211',
      dob: '1992-06-15',
      gender: 'Female',
      userType: 'staff',
      phone: '+1-555-0102',
      address: '456 Staff Ave, City, State',
      joinDate: new Date('2023-06-01')
    },
    {
      id: 3,
      email: 'user@example.com',
      password: 'password123',
      firstName: 'John',
      lastName: 'Customer',
      name: 'John Customer',
      mobile: '9876543212',
      dob: '1995-03-20',
      gender: 'Male',
      userType: 'user',
      phone: '+1-555-0103',
      address: '789 Customer Blvd, City, State',
      joinDate: new Date('2024-01-15')
    }
  ];

  constructor() {
    // Load registered users from localStorage
    this.loadUsersFromStorage();
    
    // Check if user is already logged in (from localStorage)
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      this.currentUserSubject.next(JSON.parse(savedUser));
    }
  }

  /**
   * Load users from localStorage and merge with default users
   */
  private loadUsersFromStorage(): void {
    const storedUsers = localStorage.getItem('registeredUsers');
    if (storedUsers) {
      const parsedUsers = JSON.parse(storedUsers);
      // Merge stored users with default users, avoiding duplicates
      const allUsers = [...this.mockUsers];
      parsedUsers.forEach((storedUser: User) => {
        if (!allUsers.find(u => u.email.toLowerCase() === storedUser.email.toLowerCase())) {
          allUsers.push(storedUser);
        }
      });
      this.mockUsers = allUsers;
    }
  }

  /**
   * Save users to localStorage
   */
  private saveUsersToStorage(): void {
    // Save only registered users (exclude default admin/demo users with IDs 1-3)
    const registeredUsers = this.mockUsers.filter(user => user.id > 3);
    localStorage.setItem('registeredUsers', JSON.stringify(registeredUsers));
  }

  /**
   * Register a new user
   * @param registerData User registration data
   * @returns Promise<{success: boolean, message: string}>
   */
  async register(registerData: RegisterData): Promise<{success: boolean, message: string}> {
    // Simulate API call delay
    await new Promise(resolve => setTimeout(resolve, 1000));

    // Check if user already exists
    const existingUser = this.mockUsers.find(u => 
      u.email.toLowerCase() === registerData.email.toLowerCase() ||
      u.mobile === registerData.mobile
    );

    if (existingUser) {
      return {
        success: false,
        message: 'User with this email or mobile number already exists!'
      };
    }

    // Map userType from registration form to our interface types
    let userType: 'admin' | 'user' | 'staff';
    switch (registerData.userType.toLowerCase()) {
      case 'admin':
        userType = 'admin';
        break;
      case 'staff':
        userType = 'staff';
        break;
      case 'customer':
      default:
        userType = 'user';
        break;
    }

    // Create new user
    const newUser: User = {
      id: this.mockUsers.length + 1,
      email: registerData.email,
      password: registerData.password,
      firstName: registerData.firstName,
      middleName: registerData.middleName,
      lastName: registerData.lastName,
      name: `${registerData.firstName} ${registerData.lastName}`,
      mobile: registerData.mobile,
      dob: registerData.dob,
      gender: registerData.gender,
      userType: userType,
      joinDate: new Date()
    };

    // Add to users array
    this.mockUsers.push(newUser);
    
    // Save to localStorage
    this.saveUsersToStorage();

    return {
      success: true,
      message: 'Registration successful! You can now login.'
    };
  }

  /**
   * Authenticate user with email and password
   * @param email User's email
   * @param password User's password
   * @returns Promise<boolean> - true if authentication successful
   */
  async login(email: string, password: string): Promise<boolean> {
    // Simulate API call delay
    await new Promise(resolve => setTimeout(resolve, 1000));

    // Find user by email (in real app, this would be an API call)
    const user = this.mockUsers.find(u => u.email.toLowerCase() === email.toLowerCase());
    
    if (user && user.password === password) {
      // Create a copy without the password for security
      const userWithoutPassword = { ...user };
      delete (userWithoutPassword as any).password;
      
      // Store user in localStorage
      localStorage.setItem('currentUser', JSON.stringify(userWithoutPassword));
      this.currentUserSubject.next(userWithoutPassword);
      return true;
    }
    
    return false;
  }

  /**
   * Check if email is already registered
   * @param email Email to check
   * @returns boolean
   */
  isEmailRegistered(email: string): boolean {
    return this.mockUsers.some(u => u.email.toLowerCase() === email.toLowerCase());
  }

  /**
   * Check if mobile is already registered
   * @param mobile Mobile number to check
   * @returns boolean
   */
  isMobileRegistered(mobile: string): boolean {
    return this.mockUsers.some(u => u.mobile === mobile);
  }

  /**
   * Log out current user
   */
  logout(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  /**
   * Get current user
   */
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Check if user is logged in
   */
  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  /**
   * Check if current user is admin
   */
  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user?.userType === 'admin';
  }

  /**
   * Check if current user is staff
   */
  isStaff(): boolean {
    const user = this.getCurrentUser();
    return user?.userType === 'staff';
  }

  /**
   * Check if current user is regular user
   */
  isUser(): boolean {
    const user = this.getCurrentUser();
    return user?.userType === 'user';
  }
}