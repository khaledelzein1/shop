import { DecimalPipe, KeyValuePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Category } from '../../../core/models/category.model';
import { ApiError } from '../../../core/models/page.model';
import { Product } from '../../../core/models/product.model';
import { AdminProductService } from '../../../core/services/admin-product.service';
import { CatalogService } from '../../../core/services/catalog.service';

/** Parse un textarea "clé: valeur" (une paire par ligne) en Record<string,string>. */
function parseAttributes(raw: string): Record<string, string> {
  const attributes: Record<string, string> = {};
  raw
    .split('\n')
    .map((line) => line.trim())
    .filter((line) => line.length > 0 && line.includes(':'))
    .forEach((line) => {
      const [key, ...rest] = line.split(':');
      attributes[key.trim()] = rest.join(':').trim();
    });
  return attributes;
}

function formatAttributes(attributes: Record<string, string>): string {
  return Object.entries(attributes)
    .map(([key, value]) => `${key}: ${value}`)
    .join('\n');
}

@Component({
  selector: 'app-admin-product-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, DecimalPipe, KeyValuePipe],
  templateUrl: './product-form.html',
})
export class AdminProductForm implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly productService = inject(AdminProductService);
  private readonly catalogService = inject(CatalogService);
  private readonly fb = inject(FormBuilder);

  protected readonly isNew = signal(true);
  protected readonly productId = signal<number | null>(null);
  protected readonly product = signal<Product | null>(null);
  protected readonly categories = signal<Category[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly showVariantForm = signal(false);
  protected readonly showImageForm = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    slug: ['', [Validators.required, Validators.pattern(/^[a-z0-9]+(-[a-z0-9]+)*$/)]],
    description: [''],
    brand: [''],
    active: [true],
    categoryId: [0, Validators.required],
  });

  protected readonly variantForm = this.fb.nonNullable.group({
    sku: ['', Validators.required],
    price: [0, [Validators.required, Validators.min(0.01)]],
    stock: [0, [Validators.required, Validators.min(0)]],
    active: [true],
    attributes: [''],
  });

  protected readonly imageForm = this.fb.nonNullable.group({
    url: ['', Validators.required],
    position: [0],
    primary: [false],
  });

  ngOnInit(): void {
    this.catalogService.listCategories().subscribe((cats) => this.categories.set(cats));

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam === 'new') {
      this.isNew.set(true);
      this.loading.set(false);
      return;
    }

    this.isNew.set(false);
    const id = Number(idParam);
    this.productId.set(id);
    this.loadProduct(id);
  }

  private loadProduct(id: number): void {
    this.loading.set(true);
    this.productService.getById(id).subscribe((product) => {
      this.product.set(product);
      this.form.patchValue({
        name: product.name,
        slug: product.slug,
        description: product.description ?? '',
        brand: product.brand ?? '',
        active: product.active,
        categoryId: product.category.id,
      });
      this.loading.set(false);
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.errorMessage.set(null);
    const payload = this.form.getRawValue();
    const id = this.productId();

    const request$ = id ? this.productService.update(id, payload) : this.productService.create(payload);
    request$.subscribe({
      next: (product) => {
        this.saving.set(false);
        if (!id) {
          this.router.navigate(['/admin/products', product.id]);
        } else {
          this.product.set(product);
        }
      },
      error: (err: HttpErrorResponse) => {
        this.saving.set(false);
        const apiError = err.error as ApiError | undefined;
        this.errorMessage.set(apiError?.message ?? 'Une erreur est survenue');
      },
    });
  }

  toggleVariantForm(): void {
    this.variantForm.reset({ sku: '', price: 0, stock: 0, active: true, attributes: '' });
    this.showVariantForm.set(!this.showVariantForm());
  }

  submitVariant(): void {
    const id = this.productId();
    if (!id || this.variantForm.invalid) {
      this.variantForm.markAllAsTouched();
      return;
    }
    const raw = this.variantForm.getRawValue();
    this.productService
      .addVariant(id, {
        sku: raw.sku,
        price: raw.price,
        stock: raw.stock,
        active: raw.active,
        attributes: parseAttributes(raw.attributes),
      })
      .subscribe({
        next: () => {
          this.showVariantForm.set(false);
          this.loadProduct(id);
        },
        error: (err: HttpErrorResponse) => {
          const apiError = err.error as ApiError | undefined;
          alert(apiError?.message ?? 'Erreur lors de la création de la variante');
        },
      });
  }

  updateVariantStock(variantId: number, stock: number): void {
    const id = this.productId();
    if (!id || stock < 0) {
      return;
    }
    this.productService.updateVariantStock(id, variantId, { stock }).subscribe(() => this.loadProduct(id));
  }

  deleteVariant(variantId: number): void {
    const id = this.productId();
    if (!id || !confirm('Supprimer cette variante ?')) {
      return;
    }
    this.productService.deleteVariant(id, variantId).subscribe(() => this.loadProduct(id));
  }

  toggleImageForm(): void {
    this.imageForm.reset({ url: '', position: 0, primary: false });
    this.showImageForm.set(!this.showImageForm());
  }

  submitImage(): void {
    const id = this.productId();
    if (!id || this.imageForm.invalid) {
      this.imageForm.markAllAsTouched();
      return;
    }
    this.productService.addImage(id, this.imageForm.getRawValue()).subscribe({
      next: () => {
        this.showImageForm.set(false);
        this.loadProduct(id);
      },
    });
  }

  deleteImage(imageId: number): void {
    const id = this.productId();
    if (!id || !confirm('Supprimer cette image ?')) {
      return;
    }
    this.productService.deleteImage(id, imageId).subscribe(() => this.loadProduct(id));
  }

  protected readonly formatAttributes = formatAttributes;
}
