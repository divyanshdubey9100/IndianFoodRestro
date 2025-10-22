import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

interface FeaturedDish {
  id: number;
  name: string;
  description: string;
  price: number;
  rating: number;
  image: string;
  tags: string[];
}

interface RestaurantFeature {
  icon: string;
  title: string;
  description: string;
}

interface CustomerTestimonial {
  name: string;
  title: string;
  text: string;
  rating: number;
  image: string;
}

interface SpecialOffer {
  title: string;
  description: string;
  discount: string;
  validUntil: Date;
}

interface OperatingHours {
  day: string;
  time: string;
}

@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule, RouterModule, DatePipe],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit, OnDestroy {
  newsletterEmail = '';
  currentTestimonial = 0;
  testimonialInterval: any;

  featuredDishes: FeaturedDish[] = [
    {
      id: 1,
      name: 'Butter Chicken',
      description: 'Tender chicken in a rich, creamy tomato-based sauce with aromatic spices.',
      price: 18.99,
      rating: 4.8,
      image: '/assets/images/butter-chicken.jpg',
      tags: ['Popular', 'Creamy', 'Mild']
    },
    {
      id: 2,
      name: 'Biryani Special',
      description: 'Fragrant basmati rice cooked with tender meat and traditional spices.',
      price: 22.99,
      rating: 4.9,
      image: '/assets/images/biryani.jpg',
      tags: ['Signature', 'Aromatic', 'Spicy']
    },
    {
      id: 3,
      name: 'Paneer Tikka Masala',
      description: 'Grilled cottage cheese in a flavorful onion and tomato gravy.',
      price: 16.99,
      rating: 4.7,
      image: '/assets/images/paneer-tikka.jpg',
      tags: ['Vegetarian', 'Grilled', 'Popular']
    },
    {
      id: 4,
      name: 'Lamb Rogan Josh',
      description: 'Traditional Kashmiri curry with tender lamb in aromatic spices.',
      price: 24.99,
      rating: 4.6,
      image: '/assets/images/lamb-rogan.jpg',
      tags: ['Traditional', 'Spicy', 'Premium']
    }
  ];

  restaurantFeatures: RestaurantFeature[] = [
    {
      icon: 'fas fa-utensils',
      title: 'Authentic Recipes',
      description: 'Traditional family recipes passed down through generations, prepared with authentic techniques and ingredients.'
    },
    {
      icon: 'fas fa-leaf',
      title: 'Fresh Ingredients',
      description: 'We source the finest ingredients daily, including imported spices and locally grown fresh produce.'
    },
    {
      icon: 'fas fa-heart',
      title: 'Warm Hospitality',
      description: 'Experience genuine Indian hospitality in our welcoming atmosphere designed to feel like home.'
    },
    {
      icon: 'fas fa-award',
      title: 'Award Winning',
      description: 'Recognized for excellence in authentic Indian cuisine and outstanding customer service.'
    },
    {
      icon: 'fas fa-clock',
      title: 'Quick Service',
      description: 'Enjoy freshly prepared meals with efficient service, perfect for lunch breaks or dinner dates.'
    },
    {
      icon: 'fas fa-users',
      title: 'Family Friendly',
      description: 'A welcoming environment for families, couples, and groups with options for all ages and preferences.'
    }
  ];

  customerTestimonials: CustomerTestimonial[] = [
    {
      name: 'Sarah Johnson',
      title: 'Food Enthusiast',
      text: 'The best Indian food I\'ve ever had! The butter chicken is absolutely divine, and the service is impeccable. This place has become our family\'s go-to restaurant.',
      rating: 5,
      image: '/assets/images/testimonial-1.jpg'
    },
    {
      name: 'Michael Chen',
      title: 'Local Food Blogger',
      text: 'Spice Palace delivers authentic flavors that transport you straight to India. The biryani is phenomenal, and the staff genuinely cares about your dining experience.',
      rating: 5,
      image: '/assets/images/testimonial-2.jpg'
    },
    {
      name: 'Emily Rodriguez',
      title: 'Regular Customer',
      text: 'I\'ve been coming here for years, and the quality never disappoints. The vegetarian options are incredible, and the atmosphere is perfect for both casual and special occasions.',
      rating: 5,
      image: '/assets/images/testimonial-3.jpg'
    },
    {
      name: 'David Thompson',
      title: 'Business Executive',
      text: 'Perfect for business lunches and family dinners alike. The service is prompt, the food is consistently excellent, and the prices are very reasonable.',
      rating: 4,
      image: '/assets/images/testimonial-4.jpg'
    }
  ];

  specialOffers: SpecialOffer[] = [
    {
      title: 'Lunch Special',
      description: 'Get 20% off on all lunch combos served with rice, naan, and drink.',
      discount: '20% OFF',
      validUntil: new Date('2025-11-15')
    },
    {
      title: 'Family Feast',
      description: 'Order for 4 or more and receive a complimentary dessert platter.',
      discount: 'FREE DESSERT',
      validUntil: new Date('2025-12-31')
    },
    {
      title: 'Weekend Brunch',
      description: 'Special weekend brunch menu with traditional Indian breakfast items.',
      discount: '15% OFF',
      validUntil: new Date('2025-10-31')
    }
  ];

  operatingHours: OperatingHours[] = [
    { day: 'Monday - Thursday', time: '11:00 AM - 9:00 PM' },
    { day: 'Friday - Saturday', time: '11:00 AM - 10:00 PM' },
    { day: 'Sunday', time: '12:00 PM - 9:00 PM' }
  ];

  ngOnInit(): void {
    this.startTestimonialCarousel();
  }

  ngOnDestroy(): void {
    if (this.testimonialInterval) {
      clearInterval(this.testimonialInterval);
    }
  }

  viewDish(dish: FeaturedDish): void {
    // This could navigate to a detailed dish page or show a modal
    console.log('Viewing dish:', dish.name);
    alert(`${dish.name}\n\n${dish.description}\n\nPrice: $${dish.price}\nRating: ${dish.rating}/5`);
  }

  getStarRating(rating: number): string {
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;
    let stars = '';
    
    // Add full stars
    for (let i = 0; i < fullStars; i++) {
      stars += '★';
    }
    
    // Add half star if needed
    if (hasHalfStar) {
      stars += '☆';
    }
    
    // Add empty stars to make it 5 total
    const remainingStars = 5 - Math.ceil(rating);
    for (let i = 0; i < remainingStars; i++) {
      stars += '☆';
    }
    
    return stars;
  }

  startTestimonialCarousel(): void {
    this.testimonialInterval = setInterval(() => {
      this.nextTestimonial();
    }, 5000); // Change testimonial every 5 seconds
  }

  nextTestimonial(): void {
    this.currentTestimonial = (this.currentTestimonial + 1) % this.customerTestimonials.length;
  }

  previousTestimonial(): void {
    this.currentTestimonial = this.currentTestimonial === 0 
      ? this.customerTestimonials.length - 1 
      : this.currentTestimonial - 1;
  }

  goToTestimonial(index: number): void {
    this.currentTestimonial = index;
  }

  subscribeNewsletter(): void {
    if (this.newsletterEmail && this.isValidEmail(this.newsletterEmail)) {
      // Here you would typically send the email to a backend service
      console.log('Newsletter subscription:', this.newsletterEmail);
      alert(`Thank you for subscribing! We'll send you updates at ${this.newsletterEmail}`);
      this.newsletterEmail = '';
    } else {
      alert('Please enter a valid email address.');
    }
  }

  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }
}
