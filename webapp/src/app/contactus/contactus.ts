import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';

interface ContactData {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  subject: string;
  message: string;
  subscribe: boolean;
  reservationDate?: string;
  reservationTime?: string;
  partySize?: string;
}

interface OperatingHours {
  day: string;
  time: string;
}

interface FAQ {
  question: string;
  answer: string;
  isOpen: boolean;
}

@Component({
  selector: 'app-contactus',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './contactus.html',
  styleUrls: ['./contactus.css']
})
export class Contactus implements OnInit {
  contactData: ContactData = {
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    subject: '',
    message: '',
    subscribe: false
  };

  isSubmitting = false;
  showSuccessModal = false;
  today = '';

  operatingHours: OperatingHours[] = [
    { day: 'Mon - Thu', time: '11:00 AM - 9:00 PM' },
    { day: 'Fri - Sat', time: '11:00 AM - 10:00 PM' },
    { day: 'Sunday', time: '12:00 PM - 9:00 PM' }
  ];

  frequentlyAskedQuestions: FAQ[] = [
    {
      question: 'Do you take reservations?',
      answer: 'Yes, we accept reservations for parties of all sizes. You can make a reservation through our contact form, by calling us, or using our online reservation system. We recommend booking in advance, especially for weekends and holidays.',
      isOpen: false
    },
    {
      question: 'Do you offer vegetarian and vegan options?',
      answer: 'Absolutely! We have an extensive selection of vegetarian dishes, and many can be prepared vegan upon request. Our menu clearly marks vegetarian options, and our staff can help you choose vegan-friendly dishes.',
      isOpen: false
    },
    {
      question: 'Can you accommodate food allergies and dietary restrictions?',
      answer: 'Yes, we take food allergies and dietary restrictions very seriously. Please inform our staff about any allergies or dietary needs when ordering, and our kitchen will prepare your meal accordingly. We can accommodate gluten-free, nut-free, and other dietary requirements.',
      isOpen: false
    },
    {
      question: 'Do you offer catering services?',
      answer: 'Yes, we provide catering services for events, parties, and corporate functions. We offer various catering packages and can customize menus to suit your needs. Please contact us at least 48 hours in advance for catering orders.',
      isOpen: false
    },
    {
      question: 'What are your spice levels?',
      answer: 'We offer dishes ranging from mild to very spicy. Our menu indicates spice levels, and you can always request to adjust the spice level when ordering. Our staff can help you choose dishes that match your spice preference.',
      isOpen: false
    },
    {
      question: 'Do you offer delivery or takeout?',
      answer: 'Yes, we offer both delivery and takeout services. You can place orders by phone, through our website, or via popular delivery apps. Delivery is available within a 5-mile radius, and we offer contactless delivery options.',
      isOpen: false
    },
    {
      question: 'Do you have parking available?',
      answer: 'Yes, we have a dedicated parking lot with ample spaces for our guests. Street parking is also available in the surrounding area. Parking is free for all our customers.',
      isOpen: false
    },
    {
      question: 'Can I purchase gift cards?',
      answer: 'Yes, we offer gift cards in various denominations. Gift cards can be purchased in-person at our restaurant or by calling us. They make perfect gifts for food lovers and never expire.',
      isOpen: false
    }
  ];

  ngOnInit(): void {
    // Set today's date for the date picker minimum value
    const today = new Date();
    this.today = today.toISOString().split('T')[0];
  }

  submitContactForm(): void {
    if (this.isSubmitting) return;

    this.isSubmitting = true;

    // Simulate form submission with a delay
    setTimeout(() => {
      console.log('Contact form submitted:', this.contactData);
      
      // Show success modal
      this.showSuccessModal = true;
      
      // Reset form after successful submission
      this.resetContactData();
      
      this.isSubmitting = false;
    }, 2000);
  }

  resetForm(form: NgForm): void {
    if (confirm('Are you sure you want to clear the form? All entered data will be lost.')) {
      form.resetForm();
      this.resetContactData();
    }
  }

  resetContactData(): void {
    this.contactData = {
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      subject: '',
      message: '',
      subscribe: false
    };
  }

  isFieldInvalid(fieldName: string, form: NgForm): boolean {
    const field = form.controls[fieldName];
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }

  toggleFAQ(index: number): void {
    this.frequentlyAskedQuestions[index].isOpen = !this.frequentlyAskedQuestions[index].isOpen;
  }

  openMap(): void {
    // In a real application, this would open Google Maps with the restaurant's location
    const address = encodeURIComponent('123 Spice Avenue, Food District, Flavor City, FC 12345');
    const googleMapsUrl = `https://www.google.com/maps/search/?api=1&query=${address}`;
    window.open(googleMapsUrl, '_blank');
  }

  closeSuccessModal(): void {
    this.showSuccessModal = false;
  }
}
