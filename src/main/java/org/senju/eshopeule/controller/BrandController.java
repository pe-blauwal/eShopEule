package org.senju.eshopeule.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.senju.eshopeule.dto.BrandDTO;
import org.senju.eshopeule.dto.response.BaseResponse;
import org.senju.eshopeule.dto.response.SimpleResponse;
import org.senju.eshopeule.exceptions.NotFoundException;
import org.senju.eshopeule.exceptions.ObjectAlreadyExistsException;
import org.senju.eshopeule.service.BrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/r/v1/brand")
public class BrandController {

    private final BrandService brandService;
    private static final Logger logger = LoggerFactory.getLogger(BrandController.class);

    @GetMapping(path = "/all")
    public ResponseEntity<Collection<? extends BaseResponse>> getAllBrand() {
        return ResponseEntity.ok(brandService.getAllBrand());
    }

    @GetMapping
    public ResponseEntity<? extends BaseResponse> getBrandWithId(@RequestParam("id") String id) {
        try {
            logger.info("Get brand with id: {}", id);
            return ResponseEntity.ok(brandService.getById(id));
        } catch (NotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(NOT_FOUND).body(new SimpleResponse(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<? extends BaseResponse> createNewBrand(@Valid @RequestBody BrandDTO dto) {
        try {
            logger.info("Create new brand..");
            brandService.createNewBrand(dto);
            return ResponseEntity.ok(new SimpleResponse("Create new brand successfully!"));
        } catch (ObjectAlreadyExistsException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<? extends BaseResponse> updateBrand(@Valid @RequestBody BrandDTO dto) {
        try {
            logger.info("Update brand with id: {}", dto.getId());
            return ResponseEntity.ok(brandService.updateBrand(dto));
        } catch (ObjectAlreadyExistsException | NotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body(new SimpleResponse(ex.getMessage()));
        }
    }

    @DeleteMapping(path = "/del")
    public ResponseEntity<?> deleteBrandWithId(@RequestParam("id") String id) {
        logger.info("Delete brand with id: {}", id);
        brandService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}