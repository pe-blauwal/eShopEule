package org.senju.eshopeule.service.impl;

import lombok.RequiredArgsConstructor;
import org.senju.eshopeule.dto.ProductMetaDTO;
import org.senju.eshopeule.exceptions.NotFoundException;
import org.senju.eshopeule.exceptions.ObjectAlreadyExistsException;
import org.senju.eshopeule.mappers.ProductMetaMapper;
import org.senju.eshopeule.model.product.ProductMeta;
import org.senju.eshopeule.repository.jpa.ProductRepository;
import org.senju.eshopeule.repository.mongodb.ProductMetaRepository;
import org.senju.eshopeule.service.ProductMetaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.senju.eshopeule.constant.exceptionMessage.ProductExceptionMsg.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductMetaServiceImpl implements ProductMetaService {

    private final ProductMetaMapper mapper;
    private final ProductMetaRepository productMetaRepository;
    private final ProductRepository productRepository;


    @Override
    public ProductMetaDTO getById(String id) {
        return mapper.convertToDTO(productMetaRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(PROD_META_NOT_FOUND_WITH_ID_MSG, id))
        ));
    }

    @Override
    public ProductMetaDTO getByProductId(String productId) {
        return mapper.convertToDTO(productMetaRepository.findByProductId(productId).orElseThrow(
                () -> new NotFoundException(String.format(PROD_META_NOT_FOUND_WITH_PRODUCT_ID_MSG, productId))
        ));
    }

    @Override
    public ProductMetaDTO createProdMeta(ProductMetaDTO dto) {
        if (!productRepository.existsById(dto.getProductId())) {
            throw new NotFoundException(String.format(PRODUCT_NOT_FOUND_WITH_ID_MSG, dto.getProductId()));
        }
        if (productMetaRepository.findByProductId(dto.getProductId()).isPresent()) {
            throw new ObjectAlreadyExistsException(String.format(PROD_META_ALREADY_EXISTS_WITH_PRODUCT_ID_MSG, dto.getProductId()));
        }
        return mapper.convertToDTO(productMetaRepository.save(mapper.convertToEntity(dto)));
    }

    @Override
    public ProductMetaDTO updateProdMeta(ProductMetaDTO dto) {
        if (dto.getId() != null && !dto.getId().isBlank()) {
            if (dto.getProductId() == null) throw new NotFoundException(PRODUCT_NOT_FOUND_MSG);
            final ProductMeta loadedProdMeta = productMetaRepository.findById(dto.getId()).orElseThrow(
                    () -> new NotFoundException(String.format(PROD_META_NOT_FOUND_WITH_ID_MSG, dto.getId()))
            );
            if (!dto.getProductId().equals(loadedProdMeta.getProductId())) {
                throw new NotFoundException(String.format(PRODUCT_NOT_FOUND_WITH_ID_MSG, dto.getProductId()));
            }
            mapper.updateFromDTO(dto, loadedProdMeta);
            return mapper.convertToDTO(productMetaRepository.save(loadedProdMeta));
        } else throw new NotFoundException(PROD_META_NOT_FOUND_MSG);
    }

    @Override
    public void deleteById(String id) {
        productMetaRepository.deleteById(id);
    }

    @Override
    public void deleteByProductId(String productId) {
        productMetaRepository.deleteByProductId(productId);
    }
}
