import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

/**
 * Standalone "Find your size" page, modeled on Zara's own size-finder: a short
 * questionnaire (height, weight, how you like your clothes to fit) that produces a
 * recommended size, in place of the customer having to guess from a static chart.
 */
@Component({
  selector: 'app-find-my-size',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './find-my-size.html',
  styleUrl: './find-my-size.scss',
})
export class FindMySize {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private static readonly DEFAULT_SIZES = ['XS', 'S', 'M', 'L', 'XL', 'XXL'];
  protected static readonly FIT_OPTIONS = ['Tight', 'Regular', 'Loose'] as const;

  /** Model/product the customer came from, so "Use this size" can send them straight back. */
  private readonly returnModel = this.route.snapshot.queryParamMap.get('model');
  private readonly returnProduct = this.route.snapshot.queryParamMap.get('product');
  /** The actual size scale for that model (letters for jackets, numbers like 36/38/40 for trousers). */
  private readonly sizeScale = (
    this.route.snapshot.queryParamMap.get('sizes')?.split(',').filter(Boolean) ?? FindMySize.DEFAULT_SIZES
  );

  protected readonly step = signal<1 | 2 | 3>(1);
  protected readonly heightCm = signal<number | null>(null);
  protected readonly weightKg = signal<number | null>(null);
  protected readonly fitPreference = signal<(typeof FindMySize.FIT_OPTIONS)[number] | null>(null);
  protected readonly recommendedSize = signal<string | null>(null);

  protected readonly fitOptions = FindMySize.FIT_OPTIONS;
  protected readonly hasReturnTarget = !!this.returnModel && !!this.returnProduct;

  submitMeasurements(): void {
    if (!this.heightCm() || !this.weightKg()) {
      return;
    }
    this.step.set(2);
  }

  chooseFit(fit: (typeof FindMySize.FIT_OPTIONS)[number]): void {
    this.fitPreference.set(fit);
    this.recommendedSize.set(this.calculateSize());
    this.step.set(3);
  }

  restart(): void {
    this.heightCm.set(null);
    this.weightKg.set(null);
    this.fitPreference.set(null);
    this.recommendedSize.set(null);
    this.step.set(1);
  }

  useThisSize(): void {
    const size = this.recommendedSize();
    if (!size || !this.returnProduct || !this.returnModel) {
      return;
    }
    this.router.navigate(['/products', this.returnProduct], {
      queryParams: { model: this.returnModel, size },
    });
  }

  /** Rough height/weight → size band, nudged by fit preference. Same idea as Zara's own estimator: a guide, not gospel. */
  private calculateSize(): string {
    const heightM = (this.heightCm() ?? 170) / 100;
    const weight = this.weightKg() ?? 70;
    const bmi = weight / (heightM * heightM);

    let index = Math.round((bmi - 18) / 2.2);
    if (this.fitPreference() === 'Tight') {
      index -= 1;
    } else if (this.fitPreference() === 'Loose') {
      index += 1;
    }
    index = Math.max(0, Math.min(this.sizeScale.length - 1, index));
    return this.sizeScale[index];
  }
}
