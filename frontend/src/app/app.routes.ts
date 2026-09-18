import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './core/guards/auth.guard';

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
    path: 'admin',
    loadComponent: () => import('./features/admin/layout/admin-layout').then((m) => m.AdminLayout),
    canActivate: [adminGuard],
    children: [
      { path: '', loadComponent: () => import('./features/admin/dashboard/dashboard').then((m) => m.AdminDashboard) },
      {
        path: 'categories',
        loadComponent: () => import('./features/admin/categories/categories').then((m) => m.AdminCategories),
      },
      {
        path: 'products',
        loadComponent: () =>
          import('./features/admin/products/products-list').then((m) => m.AdminProductsList),
      },
      {
        path: 'products/:id',
        loadComponent: () => import('./features/admin/products/product-form').then((m) => m.AdminProductForm),
      },
      {
        path: 'orders',
        loadComponent: () => import('./features/admin/orders/orders').then((m) => m.AdminOrders),
      },
      {
        path: 'users',
        loadComponent: () => import('./features/admin/users/users').then((m) => m.AdminUsers),
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
