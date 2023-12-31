package com.isep.acme.services;

import java.io.IOException;
import java.util.Optional;

import com.isep.acme.model.Product;
import com.isep.acme.model.ProductDTO;
import com.isep.acme.model.ProductDetailDTO;
import com.isep.acme.model.User;

public interface ProductService {

    Optional<ProductDTO> findBySku(final String sku);

    Optional<Product> getProductBySku( final String sku );

    Iterable<ProductDTO> findByDesignation(final String designation);

    Iterable<ProductDTO> getCatalog();

    ProductDetailDTO getDetails(final String sku);

    void create(final Product manager) throws IOException;

    ProductDTO updateBySku(final String sku, final Product product);

    void deleteBySku(final String sku);

}
