import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Address, AddressPayload } from '../../../core/models/address.model';
import { AddressService } from '../../../core/services/address.service';

@Component({
  selector: 'app-addresses',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './addresses.html',
  styleUrl: './addresses.scss',
})
export class Addresses implements OnInit {
  private readonly addressService = inject(AddressService);
  private readonly fb = inject(FormBuilder);

  protected readonly addresses = signal<Address[]>([]);
  protected readonly loading = signal(true);
  protected readonly showForm = signal(false);
  protected readonly saving = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    label: [''],
    street: ['', Validators.required],
    city: ['', Validators.required],
    zipCode: ['', Validators.required],
    country: ['', Validators.required],
    defaultAddress: [false],
  });

  ngOnInit(): void {
    this.load();
  }

  openForm(): void {
    this.form.reset({ label: '', street: '', city: '', zipCode: '', country: '', defaultAddress: false });
    this.showForm.set(true);
  }

  cancel(): void {
    this.showForm.set(false);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    const payload: AddressPayload = this.form.getRawValue();
    this.addressService.create(payload).subscribe(() => {
      this.saving.set(false);
      this.showForm.set(false);
      this.load();
    });
  }

  delete(id: number): void {
    if (!confirm('Remove this address?')) {
      return;
    }
    this.addressService.delete(id).subscribe(() => this.load());
  }

  private load(): void {
    this.loading.set(true);
    this.addressService.list().subscribe((addresses) => {
      this.addresses.set(addresses);
      this.loading.set(false);
    });
  }
}
