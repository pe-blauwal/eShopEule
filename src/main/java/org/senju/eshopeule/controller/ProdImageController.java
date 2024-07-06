package org.senju.eshopeule.controller;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.senju.eshopeule.dto.response.SimpleResponse;
import org.senju.eshopeule.exceptions.NotFoundException;
import org.senju.eshopeule.exceptions.ProductException;
import org.senju.eshopeule.service.ProductImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.senju.eshopeule.constant.pattern.RegexPattern.ID_PATTERN;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/p/v1/img")
public class ProdImageController {

    private final ProductImageService productImageService;
    private static final Logger logger = LoggerFactory.getLogger(ProdImageController.class);

    @GetMapping
    public ResponseEntity<?> getImageUrlById(@RequestParam("id") @Pattern(regexp = ID_PATTERN, message = "ID is invalid") String id) {
        logger.debug("Get product image url with id: {}", id);
        try {
            return ResponseEntity.ok(productImageService.getImageUrlById(id));
        } catch (NotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @GetMapping(path = "/prod")
    public ResponseEntity<?> getAllImageUrlByProductId(@RequestParam("id") @Pattern(regexp = ID_PATTERN, message = "ID is invalid") String productId) {
        logger.debug("Get all product image url with product id: {}", productId);
        return ResponseEntity.ok(productImageService.getAllImageUrlByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<?> uploadProductImages(
            @RequestParam("file") MultipartFile[] files,
            @RequestParam("id") @Pattern(regexp = ID_PATTERN, message = "ID is invalid") String productId) {
        logger.debug("Upload product images");
        try {
            productImageService.uploadImage(files, productId);
            return ResponseEntity.ok(new SimpleResponse("Upload product images successfully!"));
        } catch (NotFoundException | ProductException ex) {
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteById(@RequestParam("id") @Pattern(regexp = ID_PATTERN, message = "ID is invalid") String id) {
        logger.debug("Delete product image with id: {}", id);
        try {
            productImageService.deleteById(id);
            return ResponseEntity.ok(new SimpleResponse("Delete product image successfully!"));
        } catch (NotFoundException | ProductException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @DeleteMapping(path = "/prod")
    public ResponseEntity<?> deleteByProductId(@RequestParam("id") @Pattern(regexp = ID_PATTERN, message = "ID is invalid") String productId) {
        logger.debug("Delete product image with product id: {}", productId);
        try {
            productImageService.deleteByProductId(productId);
            return ResponseEntity.ok(new SimpleResponse("Delete product image successfully!"));
        } catch (NotFoundException | ProductException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }
}
