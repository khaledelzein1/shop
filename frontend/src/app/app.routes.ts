import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/home').then((m) => m.Home),
  },
  {
    path: 'products',
    loadComponent: () => import('./features/catalog/product-list/product-list').then((m) => m.ProductList),
  },
  {
    path: 'products/:slug',
    loadComponent: () => import('./features/catalog/product-detail/product-detail').then((m) => m.ProductDetail),
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register').then((m) => m.Register),
  },
  {
    path: 'cart',
    loadComponent: () => import('./features/cart/cart-page').then((m) => m.CartPage),
    canActivate: [authGuard],
  },
  {
    path: 'checkout',
    loadComponent: () => import('./features/checkout/checkout-page').then((m) => m.CheckoutPage),
    canActivate: [authGuard],
  },
  {
    path: 'profile/addresses',
    loadComponent: () => import('./features/profile/addresses/addresses').then((m) => m.Addresses),
    canActivate: [authGuard],
  },
  {
    path: 'profile/orders',
    loadComponent: () => import('./features/profile/orders/orders-list').then((m) => m.OrdersList),
    canActivate: [authGuard],
  },
  {
    path: 'profile/orders/:id',
    loadComponent: () => import('./features/profile/orders/order-detail').then((m) => m.OrderDetail),
    canActivate: [authGuard],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
