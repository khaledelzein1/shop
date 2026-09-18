import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Category } from '../../../core/models/category.model';
import { ApiError } from '../../../core/models/page.model';
import { CatalogService } from '../../../core/services/catalog.service';

@Component({
  selector: 'app-admin-categories',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './categories.html',
})
export class AdminCategories implements OnInit {
  private readonly catalogService = inject(CatalogService);
  private readonly fb = inject(FormBuilder);

  protected readonly categories = signal<Category[]>([]);
  protected readonly loading = signal(true);
  protected readonly editingId = signal<number | null>(null);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    slug: ['', [Validators.required, Validators.pattern(/^[a-z0-9]+(-[a-z0-9]+)*$/)]],
    description: [''],
  });

  ngOnInit(): void {
    this.load();
  }

  startCreate(): void {
    this.errorMessage.set(null);
    this.form.reset({ name: '', slug: '', description: '' });
    this.editingId.set(0);
  }

  startEdit(category: Category): void {
    this.errorMessage.set(null);
    this.form.reset({ name: category.name, slug: category.slug, description: category.description ?? '' });
    this.editingId.set(category.id);
  }

  cancel(): void {
    this.editingId.set(null);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.errorMessage.set(null);
    const payload = this.form.getRawValue();
    const id = this.editingId();
    const request$ = id
      ? this.catalogService.updateCategory(id, payload)
      : this.catalogService.createCategory(payload);

    request$.subscribe({
      next: () => {
        this.editingId.set(null);
        this.load();
      },
      error: (err: HttpErrorResponse) => {
        const apiError = err.error as ApiError | undefined;
        this.errorMessage.set(apiError?.message ?? 'Une erreur est survenue');
      },
    });
  }

  delete(id: number): void {
    if (!confirm('Supprimer cette catégorie ?')) {
      return;
    }
    this.catalogService.deleteCategory(id).subscribe({
      next: () => this.load(),
      error: (err: HttpErrorResponse) => {
        const apiError = err.error as ApiError | undefined;
        alert(apiError?.message ?? 'Suppression impossible');
      },
    });
  }

  private load(): void {
    this.loading.set(true);
    this.catalogService.listCategories().subscribe((cats) => {
      this.categories.set(cats);
      this.loading.set(false);
    });
  }
}
