package org.senju.eshopeule.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.senju.eshopeule.constant.pagination.ProductPageable;
import org.senju.eshopeule.dto.ProductPostDTO;
import org.senju.eshopeule.dto.ProductPutDTO;
import org.senju.eshopeule.dto.response.BaseResponse;
import org.senju.eshopeule.dto.response.SimpleResponse;
import org.senju.eshopeule.exceptions.NotFoundException;
import org.senju.eshopeule.exceptions.ObjectAlreadyExistsException;
import org.senju.eshopeule.exceptions.PagingException;
import org.senju.eshopeule.exceptions.ProductException;
import org.senju.eshopeule.service.ProductService;
import org.senju.eshopeule.utils.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/p/v1/prod")
public class ProductController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping
    @Operation(summary = "Get product with ID")
    public ResponseEntity<? extends BaseResponse> getProductById(@RequestParam("id") String id) {
        logger.debug("Get product with id: {}", id);
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (NotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @GetMapping(path = "/{prodSlug}")
    @Operation(summary = "Get product with slug")
    public ResponseEntity<? extends BaseResponse> getProductBySlug(@PathVariable("prodSlug") String productSlug) {
        logger.debug("Get product with slug: {}", productSlug);
        try {
            return ResponseEntity.ok(productService.getProductBySlug(productSlug));
        } catch (NotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @GetMapping(path = "/detail")
    @Operation(summary = "Get product detail with ID")
    public ResponseEntity<? extends BaseResponse> getProductDetailById(@RequestParam("id") String id) {
        logger.debug("Get product detail with id: {}", id);
        try {
            return ResponseEntity.ok(productService.getProductDetailById(id));
        } catch (NotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @GetMapping(path = "/detail/{prodSlug}")
    @Operation(summary = "Get product detail with slug")
    public ResponseEntity<? extends BaseResponse> getProductDetailBySlug(@PathVariable("prodSlug") String productSlug) {
        logger.debug("Get product detail with slug: {}", productSlug);
        try {
            return ResponseEntity.ok(productService.getProductDetailBySlug(productSlug));
        } catch (NotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @GetMapping(path = "/all/brand")
    @Operation(summary = "Get all product with brand's ID")
    public ResponseEntity<? extends BaseResponse> getAllProductByBrandId(
            @RequestParam("id") String brandId,
            @RequestParam(name = "pageNo", required = false, defaultValue = ProductPageable.DEFAULT_PAGE_NO) int pageNo,
            @RequestParam(name = "pageSize", required = false, defaultValue = ProductPageable.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(name = "sortField", required = false, defaultValue = ProductPageable.DEFAULT_SORT_FIELD) String sortField,
            @RequestParam(name = "sortDir", required = false, defaultValue = ProductPageable.DEFAULT_SORT_DIRECTION) String sortDirection) {
        logger.debug("Get all product (pageable) with brand id: {}", brandId);
        try {
            return ResponseEntity.ok(
                    productService.getAllProductByBrandId(brandId,
                            PaginationUtil.findPaginated(pageNo, pageSize, sortField, sortDirection)));
        } catch (PagingException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @GetMapping(path = "/all/brand/{brandSlug}")
    @Operation(summary = "Get all product with brand's slug")
    public ResponseEntity<? extends BaseResponse> getAllProductByBrandSlug(
            @PathVariable("brandSlug") String brandSlug,
            @RequestParam(name = "pageNo", required = false, defaultValue = ProductPageable.DEFAULT_PAGE_NO) int pageNo,
            @RequestParam(name = "pageSize", required = false, defaultValue = ProductPageable.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(name = "sortField", required = false, defaultValue = ProductPageable.DEFAULT_SORT_FIELD) String sortField,
            @RequestParam(name = "sortDir", required = false, defaultValue = ProductPageable.DEFAULT_SORT_DIRECTION) String sortDirection) {
        logger.debug("Get all product (pageable) with brand slug: {}", brandSlug);
        try {
            return ResponseEntity.ok(
                    productService.getAllProductByBrandSlug(brandSlug,
                            PaginationUtil.findPaginated(pageNo, pageSize, sortField, sortDirection)));
        } catch (PagingException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @GetMapping(path = "/all/category")
    @Operation(summary = "Get all product with category's ID")
    public ResponseEntity<? extends BaseResponse> getAllProductByCategoryId(
            @RequestParam("id") String categoryId,
            @RequestParam(name = "pageNo", required = false, defaultValue = ProductPageable.DEFAULT_PAGE_NO) int pageNo,
            @RequestParam(name = "pageSize", required = false, defaultValue = ProductPageable.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(name = "sortField", required = false, defaultValue = ProductPageable.DEFAULT_SORT_FIELD) String sortField,
            @RequestParam(name = "sortDir", required = false, defaultValue = ProductPageable.DEFAULT_SORT_DIRECTION) String sortDirection) {
        logger.debug("Get all product (pageable) with category id: {}", categoryId);
        try {
            return ResponseEntity.ok(
                    productService.getAllProductByCategoryId(categoryId,
                            PaginationUtil.findPaginated(pageNo, pageSize, sortField, sortDirection))
            );
        } catch (PagingException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @GetMapping(path = "/all/category/{categorySlug}")
    @Operation(summary = "Get all product with category's slug")
    public ResponseEntity<? extends BaseResponse> getAllProductByCategorySlug(
            @PathVariable("categorySlug") String categorySlug,
            @RequestParam(name = "pageNo", required = false, defaultValue = ProductPageable.DEFAULT_PAGE_NO) int pageNo,
            @RequestParam(name = "pageSize", required = false, defaultValue = ProductPageable.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(name = "sortField", required = false, defaultValue = ProductPageable.DEFAULT_SORT_FIELD) String sortField,
            @RequestParam(name = "sortDir", required = false, defaultValue = ProductPageable.DEFAULT_SORT_DIRECTION) String sortDirection) {
        logger.debug("Get all product (pageable) with category slug: {}", categorySlug);
        try {
            return ResponseEntity.ok(
                    productService.getAllProductByCategorySlug(categorySlug,
                            PaginationUtil.findPaginated(pageNo, pageSize, sortField, sortDirection))
            );
        } catch (PagingException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create new product")
    public ResponseEntity<? extends BaseResponse> createNewProduct(
            @RequestPart(name = "file", required = false) MultipartFile[] files,
            @RequestPart(name = "prod") @Valid ProductPostDTO dto) {
        logger.debug("Create new product with name: {}", dto.getName());
        try {
            return ResponseEntity.ok(productService.createNewProduct(dto, files));
        } catch (NotFoundException | ObjectAlreadyExistsException | ProductException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @PutMapping
    @Operation(summary = "Update product")
    public ResponseEntity<? extends BaseResponse> updateProduct(@Valid @RequestBody ProductPutDTO dto) {
        logger.debug("Update product with id: {}", dto.getId());
        try {
            return ResponseEntity.ok(productService.updateProduct(dto));
        } catch (NotFoundException | ObjectAlreadyExistsException | ProductException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @DeleteMapping(path = "/del")
    @Operation(summary = "Delete product with ID")
    public ResponseEntity<?> deleteProductWithId(@RequestParam("id") String productId) {
        logger.debug("Delete product with id: {}", productId);
        try {
            productService.deleteProductWithId(productId);
            return ResponseEntity.ok(new SimpleResponse("Delete product successfully!"));
        } catch (NotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }
}
