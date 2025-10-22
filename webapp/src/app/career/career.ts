import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

interface Job {
  id: number;
  title: string;
  type: string;
  location: string;
  department: string;
  experience: string;
  description: string;
  requirements: string[];
  salary?: string;
}

interface ApplicationData {
  fullName: string;
  email: string;
  phone: string;
  experience: string;
  coverLetter: string;
  resume?: File;
}

@Component({
  selector: 'app-career',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './career.html',
  styleUrl: './career.css'
})
export class Career {
  currentJobs: Job[] = [
    {
      id: 1,
      title: 'Sous Chef',
      type: 'Full-time',
      location: 'Main Restaurant',
      department: 'Kitchen',
      experience: '3-5 years',
      description: 'We are looking for an experienced Sous Chef to join our kitchen team and help create authentic Indian dishes.',
      requirements: [
        'Culinary degree or equivalent experience',
        'Minimum 3 years experience in Indian cuisine',
        'Knowledge of traditional cooking techniques',
        'Ability to work in a fast-paced environment',
        'Strong leadership and communication skills'
      ],
      salary: '$45,000 - $55,000'
    },
    {
      id: 2,
      title: 'Server',
      type: 'Part-time',
      location: 'Dining Room',
      department: 'Front of House',
      experience: '1-2 years',
      description: 'Join our front-of-house team to provide exceptional service to our guests and share knowledge about Indian cuisine.',
      requirements: [
        'Previous restaurant serving experience preferred',
        'Excellent communication and interpersonal skills',
        'Knowledge of Indian cuisine is a plus',
        'Ability to work evenings and weekends',
        'Professional appearance and demeanor'
      ],
      salary: '$15 - $18/hour + tips'
    },
    {
      id: 3,
      title: 'Kitchen Assistant',
      type: 'Full-time',
      location: 'Main Kitchen',
      department: 'Kitchen',
      experience: 'Entry Level',
      description: 'Perfect opportunity for someone starting their culinary career to learn authentic Indian cooking techniques.',
      requirements: [
        'High school diploma or equivalent',
        'Willingness to learn and follow instructions',
        'Ability to work in a hot, fast-paced kitchen',
        'Basic food safety knowledge',
        'Reliable and punctual'
      ],
      salary: '$16 - $20/hour'
    }
  ];

  showApplicationModal = false;
  selectedJob: Job | null = null;
  applicationData: ApplicationData = {
    fullName: '',
    email: '',
    phone: '',
    experience: '',
    coverLetter: ''
  };

  applyForJob(job: Job): void {
    this.selectedJob = job;
    this.showApplicationModal = true;
    this.resetApplicationForm();
  }

  viewJobDetails(job: Job): void {
    // This could open a detailed job description modal or navigate to a job detail page
    console.log('Viewing details for job:', job.title);
    // For now, we'll just scroll to the job card or highlight it
    alert(`Job Details:\n\nTitle: ${job.title}\nDepartment: ${job.department}\nType: ${job.type}\nExperience: ${job.experience}\n\nDescription: ${job.description}`);
  }

  openGeneralApplication(): void {
    this.selectedJob = {
      id: 0,
      title: 'General Application',
      type: 'Various',
      location: 'Restaurant',
      department: 'Various',
      experience: 'Any',
      description: 'Submit your resume for future opportunities',
      requirements: []
    };
    this.showApplicationModal = true;
    this.resetApplicationForm();
  }

  closeApplicationModal(): void {
    this.showApplicationModal = false;
    this.selectedJob = null;
    this.resetApplicationForm();
  }

  submitApplication(): void {
    if (this.validateApplication()) {
      // Here you would typically send the application data to a backend service
      console.log('Submitting application:', this.applicationData);
      
      // Simulate successful submission
      alert(`Thank you for your application for ${this.selectedJob?.title}! We will review your application and get back to you within 5-7 business days.`);
      
      this.closeApplicationModal();
    }
  }

  onFileSelect(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files[0]) {
      const file = target.files[0];
      
      // Validate file size (5MB max)
      if (file.size > 5 * 1024 * 1024) {
        alert('File size must be less than 5MB');
        target.value = '';
        return;
      }
      
      // Validate file type
      const allowedTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
      if (!allowedTypes.includes(file.type)) {
        alert('Please upload a PDF, DOC, or DOCX file');
        target.value = '';
        return;
      }
      
      this.applicationData.resume = file;
    }
  }

  private resetApplicationForm(): void {
    this.applicationData = {
      fullName: '',
      email: '',
      phone: '',
      experience: '',
      coverLetter: ''
    };
  }

  private validateApplication(): boolean {
    if (!this.applicationData.fullName || !this.applicationData.email || !this.applicationData.phone) {
      alert('Please fill in all required fields');
      return false;
    }
    
    if (!this.applicationData.resume) {
      alert('Please upload your resume');
      return false;
    }
    
    // Basic email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.applicationData.email)) {
      alert('Please enter a valid email address');
      return false;
    }
    
    return true;
  }
}
