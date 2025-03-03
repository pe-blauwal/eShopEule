package org.senju.eshopeule.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.senju.eshopeule.dto.CategoryDTO;
import org.senju.eshopeule.dto.response.BaseResponse;
import org.senju.eshopeule.dto.response.SimpleResponse;
import org.senju.eshopeule.exceptions.NotFoundException;
import org.senju.eshopeule.exceptions.ObjectAlreadyExistsException;
import org.senju.eshopeule.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/r/v1/category")
public class CategoryController {

    private final CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @GetMapping
    @Operation(summary = "Get category with ID")
    public ResponseEntity<? extends BaseResponse> getCategoryWithId(@RequestParam("id") String id) {
        try {
            logger.info("Get category with id: {}", id);
            return ResponseEntity.ok(categoryService.getById(id));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(NOT_FOUND).body(new SimpleResponse(ex.getMessage()));
        }
    }

    @GetMapping(path = "/all")
    @Operation(summary = "Get all categories")
    public ResponseEntity<Collection<? extends BaseResponse>> getAllCategories() {
        logger.info("Get all categories");
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping(path = "/children")
    @Operation(summary = "Get all category children with parent's ID")
    public ResponseEntity<Collection<? extends BaseResponse>> getAllCategoryChildrenWithParentId(@RequestParam("id") String parentId) {
        logger.info("Get all categories with parent's id : {}", parentId);
        return ResponseEntity.ok(categoryService.getAllCategoryChildren(parentId));
    }

    @PostMapping
    @Operation(summary = "Create new category")
    public ResponseEntity<? extends BaseResponse> createNewCategory(@Valid @RequestBody CategoryDTO dto) {
        logger.info("Create new category");
        try {
            categoryService.createNewCategory(dto);
            return ResponseEntity.ok(new SimpleResponse("Create new category successfully!"));
        } catch (ObjectAlreadyExistsException | NotFoundException ex) {
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @PutMapping
    @Operation(summary = "Update category")
    public ResponseEntity<? extends BaseResponse> updateCategory(@Valid @RequestBody CategoryDTO dto) {
        logger.info("Update category");
        try {
            return ResponseEntity.ok(categoryService.updateCategory(dto));
        } catch (ObjectAlreadyExistsException | NotFoundException ex) {
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @DeleteMapping(path = "/del")
    @Operation(summary = "Delete category with ID")
    public ResponseEntity<?> deleteById(@RequestParam("id") String id) {
        logger.info("Delete category with id: {}", id);
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
