package com.example.easyOnlineShop.easyOnlineShop.Service.impl;

import com.example.easyOnlineShop.easyOnlineShop.Dto.ProductDTO;
import com.example.easyOnlineShop.easyOnlineShop.Entity.Product;
import com.example.easyOnlineShop.easyOnlineShop.Enums.ProductStatus;
import com.example.easyOnlineShop.easyOnlineShop.Repo.ProductRepository;
import com.example.easyOnlineShop.easyOnlineShop.Repo.ProductImageRepository;
import com.example.easyOnlineShop.easyOnlineShop.Service.ProductService;
import com.example.easyOnlineShop.easyOnlineShop.Entity.ProductImage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductIMPL implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @Autowired
    public ProductIMPL(ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    public List<ProductDTO> findAllProducts() {
        // Retrieve all products from the repository and map them to DTOs
        return productRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    // Helper method to convert an entity to a DTO
    private ProductDTO entityToDto(Product entity) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(entity.getProductId());
        dto.setProductName(entity.getProductName());
        dto.setProductStatus(ProductStatus.valueOf(entity.getProductStatus()));

        if (entity.getProductImages() != null) {
            // Map image IDs if present
            List<Long> imageIds = entity.getProductImages().stream()
                    .map(ProductImage::getImageId)
                    .collect(Collectors.toList());
            dto.setProductImageIds(imageIds);
        }

        return dto;
    }

    // Helper method to convert a DTO to an entity
    private Product dtoToEntity(ProductDTO dto) {
        Product entity = new Product();
        entity.setProductName(dto.getProductName());
        entity.setProductStatus(dto.getProductStatus().name());

        if (dto.getProductImageIds() != null && !dto.getProductImageIds().isEmpty()) {
            // Map product image IDs to actual ProductImage entities
            List<ProductImage> productImages = dto.getProductImageIds().stream()
                    .map(productImageRepository::findById)
                    .filter(Optional::isPresent) // Filter out results that were not found
                    .map(Optional::get)          // Unwrap the Optional
                    .collect(Collectors.toList());
            entity.setProductImages(productImages);
        }

        return entity;
    }
}