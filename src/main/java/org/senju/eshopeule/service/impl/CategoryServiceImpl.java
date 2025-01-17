package org.senju.eshopeule.service.impl;

import lombok.RequiredArgsConstructor;
import org.senju.eshopeule.dto.CategoryDTO;
import org.senju.eshopeule.exceptions.NotFoundException;
import org.senju.eshopeule.exceptions.ObjectAlreadyExistsException;
import org.senju.eshopeule.mappers.CategoryMapper;
import org.senju.eshopeule.model.product.Category;
import org.senju.eshopeule.repository.jpa.CategoryRepository;
import org.senju.eshopeule.service.CategoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.senju.eshopeule.constant.exceptionMessage.CategoryExceptionMsg.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public void createNewCategory(final CategoryDTO dto) {
        if (categoryRepository.checkCategoryExistsWithNameOrSlug(dto.getName(), dto.getSlug())) {
            throw new ObjectAlreadyExistsException(CATEGORY_ALREADY_EXITS_MSG);
        }
        Category newCategory = categoryMapper.convertToEntity(dto);
        if (newCategory.getParent() != null) {
            Category parentCategory = categoryRepository.findById(newCategory.getParent().getId()).orElseThrow(
                    () -> new NotFoundException(String.format(PARENT_CATEGORY_NOT_FOUND_WITH_ID_MSG, newCategory.getParent().getId()))
            );
            newCategory.setParent(parentCategory);
        }
        categoryRepository.save(categoryMapper.convertToEntity(dto));
    }

    @Override
    @CachePut(cacheNames = "categoryCache", key = "#dto.id")
    public CategoryDTO updateCategory(final CategoryDTO dto) {
        Category loadedCategory = categoryRepository.findById(dto.getId()).orElseThrow(
                () -> new NotFoundException(String.format(CATEGORY_NOT_FOUND_WITH_ID_MSG, dto.getId()))
        );
        if (categoryRepository.checkCategoryExistsWithNameOrSlugExceptId(dto.getName(), dto.getSlug(), dto.getId())) {
            throw new ObjectAlreadyExistsException(CATEGORY_ALREADY_EXITS_MSG);
        }
        loadedCategory = categoryMapper.updateFromDTO(dto, loadedCategory);
        return categoryMapper.convertToDTO(categoryRepository.save(loadedCategory));
    }

    @Override
    @Cacheable(cacheNames = "categoryCache", key = "#id")
    public CategoryDTO getById(String id) throws NotFoundException {
        return categoryMapper.convertToDTO(categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format(CATEGORY_NOT_FOUND_WITH_ID_MSG, id))
        ));
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getAllCategoryChildren(String parentId) {
        return categoryRepository.getAllCategoryChildrenByParentId(parentId).stream()
                .map(s -> CategoryDTO.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .slug(s.getSlug())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(cacheNames = "categoryCache", key = "#id")
    public void deleteById(String id) {
        categoryRepository.updateChildBeforeDeleteParent(id);
        categoryRepository.deleteById(id);
    }
}
