import { RouterModule, Routes } from '@angular/router';
import { HeaderComponent } from './header/header';
import { NgModule } from '@angular/core';
import { AboutUs } from './about-us/about-us';
import { Contactus } from './contactus/contactus';
import { Menu } from './menu/menu';
import { LoginComponent } from './login/login';
import { Career } from './career/career';
import { RegisterComponent } from './register/register';
import { Home } from './home/home';
import { PageNotFound } from './page-not-found/page-not-found';
import { Profile } from './profile/profile';

export const routes: Routes = [
    { path: '', component: Home },
    { path: 'about-us', component: AboutUs },
    { path: 'contact-us', component: Contactus },
    { path:'menu',component: Menu},
    { path:'sign-in',component:LoginComponent},
    { path:'career',component:Career},
    { path:'sign-up',component:RegisterComponent},
    { path:'profile',component:Profile},
    { path: '**',component:PageNotFound}
];