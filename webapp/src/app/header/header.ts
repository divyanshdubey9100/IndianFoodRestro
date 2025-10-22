import { Component } from '@angular/core';
// import { AppRoutingModule } from "../app.routes";
import { RouterLink, Router } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  templateUrl: './header.html',
  styleUrls: ['./header.css'],
  imports: [
    // AppRoutingModule,
    RouterLink,
  ]
})
export class HeaderComponent {
  constructor(private router: Router) {}

}
