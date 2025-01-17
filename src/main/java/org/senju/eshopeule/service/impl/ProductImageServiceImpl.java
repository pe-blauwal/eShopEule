package org.senju.eshopeule.service.impl;

import lombok.RequiredArgsConstructor;
import org.senju.eshopeule.exceptions.NotFoundException;
import org.senju.eshopeule.exceptions.ProductException;
import org.senju.eshopeule.model.product.Product;
import org.senju.eshopeule.model.product.ProductImage;
import org.senju.eshopeule.repository.jpa.ProductImageRepository;
import org.senju.eshopeule.repository.jpa.ProductRepository;
import org.senju.eshopeule.service.ImageService;
import org.senju.eshopeule.service.ProductImageService;
import org.senju.eshopeule.service.ProductSyncDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.senju.eshopeule.constant.exceptionMessage.ProductExceptionMsg.*;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ImageService imageService;
    private final ProductSyncDataService productSyncDataService;

    @Override
    public String getImageUrlById(String id) {
        return productImageRepository.findById(id)
                .map(ProductImage::getImageUrl)
                .orElseThrow(() -> new NotFoundException(String.format(PROD_IMAGE_NOT_FOUND_WITH_ID_MSG, id)));
    }

    @Override
    public List<String> getAllImageUrlByProductId(String productId) {
        return productImageRepository.getAllImageUrlByProductId(productId);
    }

    @Override
    @Transactional
    public void uploadImage(MultipartFile[] images, String productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException(String.format(PRODUCT_NOT_FOUND_WITH_ID_MSG, productId))
        );
        Arrays.stream(images).forEach(
                image -> {
                    try {
                        final String imageName = imageService.save(image);
                        final ProductImage newProductImage = ProductImage.builder()
                                .product(product)
                                .name(imageName)
                                .imageUrl(imageService.getImageUrl(imageName))
                                .build();
                        productImageRepository.save(newProductImage);
                    } catch (IOException ex) {
                        throw new ProductException("Error upload image");
                    }
                }
        );
        productSyncDataService.syncImageData(productId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        String imageName = productImageRepository.getNameById(id)
                .orElseThrow(() -> new NotFoundException(String.format(PROD_IMAGE_NOT_FOUND_WITH_ID_MSG, id)));
        try {
            imageService.delete(imageName);
            productImageRepository.deleteById(id);
        } catch (IOException ex) {
            throw new ProductException(ex.getMessage());
        }
        productImageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByProductId(String productId){
        List<String> imageNames = productImageRepository.getNamesByProductId(productId);
        imageNames.forEach(
                name -> {
                    try {
                        imageService.delete(name);
                    } catch (IOException ex) {
                        throw new ProductException(ex.getMessage());
                    }
                }
        );
        productImageRepository.deleteByProductId(productId);
        productSyncDataService.syncImageData(productId);
    }
}
