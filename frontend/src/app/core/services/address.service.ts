import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Address, AddressPayload } from '../models/address.model';

@Injectable({ providedIn: 'root' })
export class AddressService {
  constructor(private readonly http: HttpClient) {}

  list(): Observable<Address[]> {
    return this.http.get<Address[]>(`${environment.apiUrl}/me/addresses`);
  }

  create(payload: AddressPayload): Observable<Address> {
    return this.http.post<Address>(`${environment.apiUrl}/me/addresses`, payload);
  }

  update(id: number, payload: AddressPayload): Observable<Address> {
    return this.http.put<Address>(`${environment.apiUrl}/me/addresses/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/me/addresses/${id}`);
  }
}
