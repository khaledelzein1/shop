package com.shop.catalog;

import com.shop.catalog.dto.CategoryRequest;
import com.shop.catalog.dto.CategoryResponse;
import com.shop.common.exception.ConflictException;
import com.shop.common.exception.ResourceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  @Cacheable("categories")
  @Transactional(readOnly = true)
  public List<CategoryResponse> list() {
    return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
        .map(CategoryResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public CategoryResponse getBySlug(String slug) {
    return CategoryResponse.from(findBySlugOrThrow(slug));
  }

  @CacheEvict(value = "categories", allEntries = true)
  @Transactional
  public CategoryResponse create(CategoryRequest request) {
    if (categoryRepository.existsBySlug(request.slug())) {
      throw new ConflictException("A category already exists with slug: " + request.slug());
    }
    Category category = new Category();
    applyRequest(category, request);
    return CategoryResponse.from(categoryRepository.save(category));
  }

  @CacheEvict(value = "categories", allEntries = true)
  @Transactional
  public CategoryResponse update(Long id, CategoryRequest request) {
    Category category = findByIdOrThrow(id);
    if (!category.getSlug().equals(request.slug())
        && categoryRepository.existsBySlug(request.slug())) {
      throw new ConflictException("A category already exists with slug: " + request.slug());
    }
    applyRequest(category, request);
    return CategoryResponse.from(category);
  }

  @CacheEvict(value = "categories", allEntries = true)
  @Transactional
  public void delete(Long id) {
    Category category = findByIdOrThrow(id);
    if (productRepository.existsByCategoryId(id)) {
      throw new ConflictException(
          "Cannot delete category '" + category.getName() + "': products are still attached to it");
    }
    categoryRepository.delete(category);
  }

  private void applyRequest(Category category, CategoryRequest request) {
    category.setName(request.name());
    category.setSlug(request.slug());
    category.setDescription(request.description());
  }

  private Category findByIdOrThrow(Long id) {
    return categoryRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found (id=" + id + ")"));
  }

  private Category findBySlugOrThrow(String slug) {
    return categoryRepository
        .findBySlug(slug)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found (slug=" + slug + ")"));
  }
}
