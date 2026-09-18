export interface Category {
  id: number;
  name: string;
  slug: string;
  description: string | null;
}

export interface CategoryRequest {
  name: string;
  slug: string;
  description: string;
}
