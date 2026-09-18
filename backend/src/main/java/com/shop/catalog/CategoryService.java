package com.shop.catalog;

import com.shop.catalog.dto.CategoryRequest;
import com.shop.catalog.dto.CategoryResponse;
import com.shop.common.exception.ConflictException;
import com.shop.common.exception.ResourceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

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

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.slug())) {
            throw new ConflictException("Une catégorie existe déjà avec le slug : " + request.slug());
        }
        Category category = new Category();
        applyRequest(category, request);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findByIdOrThrow(id);
        if (!category.getSlug().equals(request.slug()) && categoryRepository.existsBySlug(request.slug())) {
            throw new ConflictException("Une catégorie existe déjà avec le slug : " + request.slug());
        }
        applyRequest(category, request);
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = findByIdOrThrow(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new ConflictException(
                    "Impossible de supprimer la catégorie '" + category.getName() + "' : des produits y sont rattachés");
        }
        categoryRepository.delete(category);
    }

    private void applyRequest(Category category, CategoryRequest request) {
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());
    }

    private Category findByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable (id=" + id + ")"));
    }

    private Category findBySlugOrThrow(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable (slug=" + slug + ")"));
    }
}
