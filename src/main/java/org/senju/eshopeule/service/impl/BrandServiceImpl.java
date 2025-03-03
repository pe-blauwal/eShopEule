package org.senju.eshopeule.service.impl;

import lombok.RequiredArgsConstructor;
import org.senju.eshopeule.dto.BrandDTO;
import org.senju.eshopeule.exceptions.NotFoundException;
import org.senju.eshopeule.exceptions.ObjectAlreadyExistsException;
import org.senju.eshopeule.mappers.BrandMapper;
import org.senju.eshopeule.model.product.Brand;
import org.senju.eshopeule.repository.jpa.BrandRepository;
import org.senju.eshopeule.service.BrandService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.senju.eshopeule.constant.exceptionMessage.BrandExceptionMsg.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public void createNewBrand(BrandDTO dto)  {
        Brand newBrand = brandMapper.convertToEntity(dto);
        if (brandRepository.checkBrandExistsWithNameOrSlug(newBrand.getName(), newBrand.getSlug())) {
            throw new ObjectAlreadyExistsException(BRAND_ALREADY_EXISTS_MSG);
        }
        brandRepository.save(newBrand);
    }

    @Override
    @CachePut(cacheNames = "brandCache", key = "#dto.id")
    public BrandDTO updateBrand(BrandDTO dto) {
        Brand loadedBrand = brandRepository.findById(dto.getId()).orElseThrow(
                () -> new NotFoundException(
                        String.format(BRAND_NOT_FOUND_WITH_ID_MSG, dto.getId())));
        brandMapper.updateFromDTO(dto, loadedBrand);
        if (brandRepository.checkBrandExistsWithNameOrSlugExceptId(loadedBrand.getName(), loadedBrand.getSlug(), loadedBrand.getId())) {
            throw new ObjectAlreadyExistsException(BRAND_ALREADY_EXISTS_MSG);
        }
        return brandMapper.convertToDTO(brandRepository.save(loadedBrand));
    }

    @Override
    @Cacheable(cacheNames = "brandCache", key = "#id")
    public BrandDTO getById(String id)  {
        return brandMapper.convertToDTO(brandRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format(BRAND_NOT_FOUND_WITH_ID_MSG, id)
                )
        ));
    }

    @Override
    public List<BrandDTO> getAllBrand() {
        return brandRepository.findAll().stream()
                .map(brandMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(cacheNames = "brandCache", key = "#id")
    public void deleteById(String id) {
        brandRepository.deleteById(id);
    }
}
