package org.senju.eshopeule.repository.jpa;

import org.senju.eshopeule.model.product.Product;
import org.senju.eshopeule.repository.projection.ProductQuantityView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findBySlug(String slug);

    @Query(value = "SELECT p FROM Product p WHERE p.brand.id = :brandId")
    Page<Product> findAllByBrandId(@Param("brandId") String brandId, Pageable pageable);

    @Query(value = "SELECT p FROM Product p WHERE p.brand.name = :brandSlug")
    Page<Product> findAllByBrandSlug(@Param("brandSlug") String brandSlug, Pageable pageable);

    @Query(value = "SELECT p FROM Product p JOIN ProductCategory pc WHERE pc.category.id = :categoryId")
    Page<Product> findAllByCategoryId(@Param("categoryId") String categoryId, Pageable pageable);

    @Query(value = "SELECT p FROM Product p JOIN ProductCategory pc WHERE pc.category.slug = :cateSlug")
    Page<Product> findAllByCategorySlug(@Param("cateSlug") String categorySlug, Pageable pageable);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM products WHERE slug = :slug)", nativeQuery = true)
    boolean checkExistsWithSlug(@Param("slug") String slug);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM products WHERE slug = :slug AND id != :prodId)", nativeQuery = true)
    boolean checkExistsWithSlugExceptId(@Param("prodId") String productId, @Param("slug") String slug);

    @Query(value = "SELECT id, quantity FROM products WHERE id = :prodId", nativeQuery = true)
    ProductQuantityView getQuantityViewById(@Param("prodId") String productId);

    @Query(value = "SELECT EXISTS " +
            "(SELECT 1 FROM products " +
            "WHERE is_published = TRUE AND is_allowed_to_order = TRUE " +
            "AND quantity > 0 AND id = :prodId)", nativeQuery = true)
    boolean checkAllowedToOrder(@Param("prodId") String productId);
}
