import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService, User } from '../services/auth.service';

@Component({
  selector: 'app-profile',
  imports: [CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {
  currentUser: User | null = null;
  
  // Mock data for different user types
  adminStats = {
    totalOrders: 1250,
    totalRevenue: 45680,
    activeUsers: 342,
    staffCount: 15
  };
  
  staffTasks = [
    { id: 1, task: 'Prepare table 5 order', priority: 'high', status: 'pending' },
    { id: 2, task: 'Clean kitchen area', priority: 'medium', status: 'completed' },
    { id: 3, task: 'Update menu items', priority: 'low', status: 'pending' }
  ];
  
  userOrders = [
    { id: 1, date: '2024-10-12', items: 'Butter Chicken, Naan', total: 28.50, status: 'delivered' },
    { id: 2, date: '2024-10-08', items: 'Biryani, Raita', total: 22.00, status: 'delivered' },
    { id: 3, date: '2024-10-05', items: 'Dal Makhani, Rice', total: 18.75, status: 'delivered' }
  ];

  constructor(
    private authService: AuthService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}
  // userName:any | null="";
  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (!user) {
        // Redirect to login if not authenticated
        this.router.navigate(['/sign-in']);
      }
      // this.userName= this.activatedRoute.snapshot.paramMap.get('name');
      // console.log(this.userName);
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/sign-in']);
  }

  // Helper methods
  isAdmin(): boolean {
    return this.currentUser?.userType === 'admin';
  }

  isStaff(): boolean {
    return this.currentUser?.userType === 'staff';
  }

  isUser(): boolean {
    return this.currentUser?.userType === 'user';
  }

  getTaskPriorityClass(priority: string): string {
    switch (priority) {
      case 'high': return 'badge bg-danger';
      case 'medium': return 'badge bg-warning';
      case 'low': return 'badge bg-success';
      default: return 'badge bg-secondary';
    }
  }

  getTaskStatusClass(status: string): string {
    return status === 'completed' ? 'badge bg-success' : 'badge bg-primary';
  }

  getOrderStatusClass(status: string): string {
    switch (status) {
      case 'delivered': return 'badge bg-success';
      case 'pending': return 'badge bg-warning';
      case 'preparing': return 'badge bg-info';
      default: return 'badge bg-secondary';
    }
  }
}
