package org.senju.eshopeule.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.senju.eshopeule.dto.ProductMetaDTO;
import org.senju.eshopeule.dto.response.BaseResponse;
import org.senju.eshopeule.dto.response.SimpleResponse;
import org.senju.eshopeule.exceptions.NotFoundException;
import org.senju.eshopeule.exceptions.ObjectAlreadyExistsException;
import org.senju.eshopeule.service.ProductMetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/r/v1/pm")
public class ProdMetaController {

    private final ProductMetaService productMetaService;
    private static final Logger logger = LoggerFactory.getLogger(ProdMetaController.class);

    @GetMapping
    public ResponseEntity<? extends BaseResponse> getProdMetaById(@RequestParam("id") String prodMetaId) {
        logger.info("Get by product meta id: {}", prodMetaId);
        try {
            return ResponseEntity.ok(productMetaService.getById(prodMetaId));
        } catch (NotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @GetMapping(path = "/prod")
    public ResponseEntity<? extends BaseResponse> getByProductId(@RequestParam("id") String productId) {
        logger.info("Get by product's ID: {}", productId);
        try {
            return ResponseEntity.ok(productMetaService.getByProductId(productId));
        } catch (NotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<? extends BaseResponse> createProductMeta(@Valid @RequestBody ProductMetaDTO dto) {
        logger.info("Create product meta");
        try {
            return ResponseEntity.ok(productMetaService.createProdMeta(dto));
        } catch (NotFoundException | ObjectAlreadyExistsException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<? extends BaseResponse> updateProductMeta(@Valid @RequestBody ProductMetaDTO dto) {
        logger.info("Update product meta with product meta ID: {}", dto.getProductId());
        try {
            return ResponseEntity.ok(productMetaService.updateProdMeta(dto));
        } catch (NotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @DeleteMapping(path = "/del")
    public ResponseEntity<?> deleteById(@RequestParam("id") String prodMetaId) {
        logger.info("Delete with product meta ID: {}", prodMetaId);
        productMetaService.deleteById(prodMetaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/prod/del")
    public ResponseEntity<?> deleteByProductId(@RequestParam("id") String productId) {
        logger.info("Delete with product ID: {}", productId);
        productMetaService.deleteByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}