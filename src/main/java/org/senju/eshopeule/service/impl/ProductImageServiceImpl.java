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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
    private static final Logger logger = LoggerFactory.getLogger(ProductImageService.class);

    @Override
    public String getImageUrlById(String id) throws NotFoundException {
        return productImageRepository.findById(id)
                .map(ProductImage::getImageUrl)
                .orElseThrow(() -> new NotFoundException(String.format(PROD_IMAGE_NOT_FOUND_WITH_ID_MSG, id)));
    }

    @Override
    public List<String> getAllImageUrlByProductId(String productId) {
        return productImageRepository.getAllImageUrlByProductId(productId);
    }

    @Override
    public void uploadImage(MultipartFile[] images, String productId) throws NotFoundException, ProductException {
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
    }

    @Override
    public void deleteById(String id) throws NotFoundException, ProductException {
        String imageName = productImageRepository.getNameById(id)
                .orElseThrow(() -> new NotFoundException(String.format(PROD_IMAGE_NOT_FOUND_WITH_ID_MSG, id)));
        try {
            imageService.delete(imageName);
        } catch (IOException ex) {
            throw new ProductException(ex.getMessage());
        }
        productImageRepository.deleteById(id);
    }

    @Override
    public void deleteByProductId(String productId) throws NotFoundException, ProductException{
        String imageName = productImageRepository.getNameByProductId(productId)
                .orElseThrow(() -> new NotFoundException(String.format(PROD_IMAGE_NOT_FOUND_WITH_PROD_ID_MSG, productId)));
        try {
            imageService.delete(imageName);
        } catch (IOException ex) {
            throw new ProductException(ex.getMessage());
        }

        productImageRepository.deleteByProductId(productId);
    }
}
