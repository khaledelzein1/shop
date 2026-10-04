import { DecimalPipe, KeyValuePipe, NgTemplateOutlet } from '@angular/common';
import { Component, ElementRef, HostListener, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Product, ProductImage, ProductVariant } from '../../../core/models/product.model';
import { AuthService } from '../../../core/services/auth.service';
import { CartService } from '../../../core/services/cart.service';
import { CatalogService } from '../../../core/services/catalog.service';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [RouterLink, DecimalPipe, KeyValuePipe, FormsModule, NgTemplateOutlet],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.scss',
})
export class ProductDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly catalogService = inject(CatalogService);
  private readonly cartService = inject(CartService);
  private readonly router = inject(Router);
  private readonly elementRef = inject(ElementRef);
  protected readonly auth = inject(AuthService);

  protected readonly product = signal<Product | null>(null);
  protected readonly selectedVariant = signal<ProductVariant | null>(null);
  protected readonly quantity = signal(1);
  protected readonly loading = signal(true);
  protected readonly addingToCart = signal(false);
  protected readonly addedMessage = signal(false);
  protected readonly colorDropdownOpen = signal(false);

  protected readonly colorVariants = computed(
    () => this.product()?.variants.filter((v) => 'color' in v.attributes) ?? [],
  );
  protected readonly otherVariants = computed(
    () => this.product()?.variants.filter((v) => !('color' in v.attributes)) ?? [],
  );

  /** Groups color variants by model (the "model" attribute, "Classic" by default). */
  protected readonly modelGroups = computed(() => {
    const groups = new Map<string, ProductVariant[]>();
    for (const variant of this.colorVariants()) {
      const model = variant.attributes['model'] ?? 'Classic';
      const group = groups.get(model);
      if (group) {
        group.push(variant);
      } else {
        groups.set(model, [variant]);
      }
    }
    // Within a model, colors follow their photos' position (lowest first), so the first
    // variant — the one a card opens on — is the one whose photo was placed first.
    const firstPosition = (variant: ProductVariant): number => {
      const positions = (this.product()?.images ?? []).filter((img) => img.variantId === variant.id).map((img) => img.position);
      return positions.length > 0 ? Math.min(...positions) : Number.MAX_SAFE_INTEGER;
    };
    return Array.from(groups.entries()).map(([name, variants]) => ({
      name,
      variants: [...variants].sort((a, b) => firstPosition(a) - firstPosition(b)),
    }));
  });

  /** Model grid shown on the product page: excludes the legacy "Classic" group. */
  protected readonly displayedModelGroups = computed(() => this.modelGroups().filter((g) => g.name !== 'Classic'));

  /** Groups the model grid into labeled sections by the "fit" attribute (e.g. Slim/Loose/Regular). */
  protected readonly fitGroups = computed(() => {
    const order = [
      'Trousers',
      'Straight',
      'SKINNY',
      'Puffer',
      'Leather',
      'Denim',
      'Long Sleeve',
      'Short Sleeve',
      'Polos',
      'LOAFERS',
      'BOOTS',
      'TRAINERS',
    ];
    const groups = new Map<string, { name: string; variants: ProductVariant[] }[]>();
    for (const group of this.displayedModelGroups()) {
      const fit = group.variants[0]?.attributes['fit'] ?? '';
      const list = groups.get(fit);
      if (list) {
        list.push(group);
      } else {
        groups.set(fit, [group]);
      }
    }
    // T-shirts always shows all 3 sections, even ones with no styles yet (e.g. Polos).
    if (this.product()?.slug === 'tshirts') {
      for (const fit of ['Long Sleeve', 'Short Sleeve', 'Polos']) {
        if (!groups.has(fit)) {
          groups.set(fit, []);
        }
      }
    }
    return Array.from(groups.entries())
      .sort(([a], [b]) => {
        const ai = order.indexOf(a);
        const bi = order.indexOf(b);
        if (ai === -1 && bi === -1) return 0;
        if (ai === -1) return 1;
        if (bi === -1) return -1;
        return ai - bi;
      })
      .map(([fit, models]) => ({ fit, models }));
  });

  protected readonly displayedImage = computed(() => {
    const p = this.product();
    if (!p) {
      return null;
    }
    const variantId = this.selectedVariant()?.id;
    const variantImage = p.images.find((img) => img.variantId === variantId);
    const primaryImage = p.images.find((img) => img.primary && img.variantId === null);
    return variantImage ?? primaryImage ?? p.images[0] ?? null;
  });

  /** Color/size currently previewed on each model card (model name -> color/size string). */
  protected readonly cardColorPreview = signal<Map<string, string | undefined>>(new Map());
  protected readonly cardSizePreview = signal<Map<string, string | undefined>>(new Map());
  protected readonly cardAddedFeedback = signal<string | null>(null);

  /** Model whose "choose a size" dropdown is currently open (null = none open). */
  protected readonly sizePickerOpen = signal<string | null>(null);

  private static readonly SIZE_ORDER = ['XS', 'S', 'M', 'L', 'XL', 'XXL'];

  /** When a product has fit sections (e.g. pants), which one is currently open. null = show the fit picker. */
  protected readonly selectedFit = signal<string | null>(null);

  protected readonly activeFitModels = computed(
    () => this.fitGroups().find((g) => g.fit === this.selectedFit())?.models ?? [],
  );

  ngOnInit(): void {
    const slug = this.route.snapshot.paramMap.get('slug')!;
    this.catalogService.getProductBySlug(slug).subscribe((product) => {
      this.product.set(product);
      const firstAvailable = product.variants.find((v) => v.active && v.stock > 0) ?? product.variants[0] ?? null;
      this.selectedVariant.set(firstAvailable);

      const colorPreview = new Map<string, string | undefined>();
      const sizePreview = new Map<string, string | undefined>();
      for (const group of this.modelGroups()) {
        const defaultVariant = group.variants.find((v) => v.active && v.stock > 0) ?? group.variants[0];
        if (defaultVariant) {
          colorPreview.set(group.name, defaultVariant.attributes['color']);
          sizePreview.set(group.name, defaultVariant.attributes['size']);
        }
      }
      this.cardColorPreview.set(colorPreview);
      this.cardSizePreview.set(sizePreview);
      this.selectedFit.set(null);

      // Returning from "Find your size" with a chosen model/size: jump straight to it.
      const returnModel = this.route.snapshot.queryParamMap.get('model');
      const returnSize = this.route.snapshot.queryParamMap.get('size');
      if (returnModel && returnSize) {
        const fit = this.fitGroups().find((g) => g.models.some((m) => m.name === returnModel))?.fit;
        if (fit) {
          this.selectedFit.set(fit);
        }
        this.previewCardSize(returnModel, returnSize);
      }

      this.loading.set(false);
    });
  }

  selectVariant(variant: ProductVariant): void {
    this.selectedVariant.set(variant);
    this.quantity.set(1);
  }

  selectFit(fit: string): void {
    this.selectedFit.set(fit);
  }

  backToFits(): void {
    this.selectedFit.set(null);
  }

  toggleColorDropdown(): void {
    this.colorDropdownOpen.update((open) => !open);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.colorDropdownOpen.set(false);
    }
    if (!(event.target as HTMLElement).closest('.model-card__add-wrap')) {
      this.sizePickerOpen.set(null);
    }
  }

  /** Variant currently previewed for a given model card (matching the previewed color and, if present, size). */
  cardVariant(modelName: string, variants: ProductVariant[]): ProductVariant | null {
    const color = this.cardColorPreview().get(modelName);
    const size = this.cardSizePreview().get(modelName);
    return (
      variants.find((v) => v.attributes['color'] === color && v.attributes['size'] === size) ??
      variants.find((v) => v.attributes['color'] === color) ??
      variants[0] ??
      null
    );
  }

  /**
   * Sizes available for a model (empty if the product has no sizes). Letter sizes (jackets) come
   * back in a fixed XS→XXL order; numeric sizes (trousers, e.g. "36"/"38"/"40") sort numerically.
   */
  availableSizes(variants: ProductVariant[]): string[] {
    const present = [...new Set(variants.map((v) => v.attributes['size']).filter((s): s is string => !!s))];
    if (present.length > 0 && present.every((s) => /^\d+$/.test(s))) {
      return present.sort((a, b) => Number(a) - Number(b));
    }
    return ProductDetail.SIZE_ORDER.filter((s) => present.includes(s));
  }

  /** One representative variant per distinct color (a model can have several size-variants per color). */
  uniqueColorVariants(variants: ProductVariant[]): ProductVariant[] {
    const seen = new Set<string>();
    const result: ProductVariant[] = [];
    for (const v of variants) {
      const color = v.attributes['color'];
      if (color && !seen.has(color)) {
        seen.add(color);
        result.push(v);
      }
    }
    return result;
  }

  /** Whether a color has any in-stock size for a given model. */
  colorInStock(variants: ProductVariant[], color: string | undefined): boolean {
    return variants.some((v) => v.attributes['color'] === color && v.active && v.stock > 0);
  }

  /**
   * Fixed cover photo per fit section. Deliberately hardcoded rather than derived from "whichever
   * model happens to be first" — that used to break when a single model's own photo was customized
   * (e.g. renaming/re-shooting one jacket model silently changed the whole fit tile's picture too).
   */
  private static readonly FIT_TILE_IMAGES: Record<string, string> = {
    Trousers: '/products/trousers-tile.png',
    Straight: '/products/straight-tile.png',
    SKINNY: '/products/skinny-tile.png',
    Puffer: '/products/jackets/lightweight-padded-black-model.png',
    Leather: '/products/jackets/leather-moto-model.png',
    Denim: '/products/jackets/flocked-denim-cropped-model.png',
    'Short Sleeve': '/products/basic-heavyweight-tshirt-white-model.png',
  };

  /** Representative image for a fit tile. Falls back to the first model's photo if the fit has no fixed cover. */
  fitTileImage(fit: string, models: { name: string; variants: ProductVariant[] }[]): ProductImage | { url: string } | null {
    const fixed = ProductDetail.FIT_TILE_IMAGES[fit];
    if (fixed) {
      return { url: fixed };
    }
    const firstModel = models[0];
    if (!firstModel) {
      return null;
    }
    const variant = firstModel.variants.find((v) => v.attributes['color'] === 'Black') ?? firstModel.variants[0];
    return this.cardImages(variant)[0] ?? null;
  }

  /** Every image linked to a variant (e.g. front/back/detail shots), in position order. */
  cardImages(variant: ProductVariant | null): ProductImage[] {
    const p = this.product();
    if (!p || !variant) {
      return [];
    }
    const linked = p.images.filter((img) => img.variantId === variant.id);
    if (linked.length > 0) {
      return linked;
    }
    const fallback = p.images.find((img) => img.primary && img.variantId === null) ?? p.images[0];
    return fallback ? [fallback] : [];
  }

  /** Which image (front/back/detail...) is currently shown per model card. */
  protected readonly cardImageIndex = signal<Map<string, number>>(new Map());

  /** Image currently shown for a model card's previewed variant. */
  cardImage(modelName: string, variant: ProductVariant | null): ProductImage | null {
    const images = this.cardImages(variant);
    if (images.length === 0) {
      return null;
    }
    const idx = this.cardImageIndex().get(modelName) ?? 0;
    return images[idx] ?? images[0];
  }

  selectCardImage(modelName: string, index: number): void {
    const next = new Map(this.cardImageIndex());
    next.set(modelName, index);
    this.cardImageIndex.set(next);
  }

  previewCardColor(modelName: string, color: string | undefined): void {
    const next = new Map(this.cardColorPreview());
    next.set(modelName, color);
    this.cardColorPreview.set(next);
    this.selectCardImage(modelName, 0);
  }

  previewCardSize(modelName: string, size: string): void {
    const next = new Map(this.cardSizePreview());
    next.set(modelName, size);
    this.cardSizePreview.set(next);
    this.selectCardImage(modelName, 0);
  }

  quickAdd(modelName: string, variant: ProductVariant | null): void {
    if (!variant || variant.stock === 0) {
      return;
    }
    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }
    this.cartService.addItem(variant.id, 1).subscribe({
      next: () => {
        this.cardAddedFeedback.set(modelName);
        setTimeout(() => this.cardAddedFeedback.set(null), 1500);
      },
    });
  }

  /**
   * Handles the model card's "+" button. Sized products (jackets) must have a size chosen before
   * adding to cart, so this opens a dropdown instead of adding immediately; sizeless products add
   * straight away like before.
   */
  onQuickAddClick(modelName: string, variant: ProductVariant | null, sizes: string[]): void {
    if (sizes.length === 0) {
      this.quickAdd(modelName, variant);
      return;
    }
    this.sizePickerOpen.set(this.sizePickerOpen() === modelName ? null : modelName);
  }

  /** Picks a size from the quick-add dropdown, then adds that variant to the cart. */
  chooseSizeAndAdd(modelName: string, variants: ProductVariant[], size: string): void {
    this.previewCardSize(modelName, size);
    const variant = this.cardVariant(modelName, variants);
    this.sizePickerOpen.set(null);
    this.quickAdd(modelName, variant);
  }

  addToCart(): void {
    const variant = this.selectedVariant();
    if (!variant) {
      return;
    }
    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.addingToCart.set(true);
    this.cartService.addItem(variant.id, this.quantity()).subscribe({
      next: () => {
        this.addingToCart.set(false);
        this.addedMessage.set(true);
        setTimeout(() => this.addedMessage.set(false), 2000);
      },
      error: () => this.addingToCart.set(false),
    });
  }
}
