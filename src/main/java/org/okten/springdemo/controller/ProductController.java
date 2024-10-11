package org.okten.springdemo.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.okten.springdemo.dto.ReviewDTO;
import org.okten.springdemo.entity.Product;
import org.okten.springdemo.entity.Review;
import org.okten.springdemo.repository.ProductRepository;
import org.okten.springdemo.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    private final ProductService productService;

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return ResponseEntity.of(this.productRepository.findById(id));
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice
    ) {

        if (minPrice != null && maxPrice != null) {
            return ResponseEntity.ok(this.productRepository.findAllByPriceBetween(minPrice, maxPrice));
        } else if (minPrice != null) {
            return ResponseEntity.ok(this.productRepository.findAllByPriceGreaterThan(minPrice));
        } else if (maxPrice != null) {
            return ResponseEntity.ok(this.productRepository.findAllByPriceLessThan(maxPrice));
        } else {
            return ResponseEntity.ok(this.productRepository.findAll());
        }
    }

//    @Secured("SELLER")
    @RolesAllowed("SELLER")
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(this.productRepository.save(product));
    }

    @Transactional
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable(name = "id") Long productId, @RequestBody Product product) {
        return ResponseEntity.of(
                this.productRepository
                        .findById(productId)
                        .map(oldProduct -> {
                            oldProduct.setName(product.getName());
                            oldProduct.setPrice(product.getPrice());
                            oldProduct.setAvailability(product.getAvailability());
//                            return productRepository.save(oldProduct); // save is not required as there is @Transactional
                            return oldProduct;
                        })
        );
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Long productId) {
        this.productRepository.deleteById(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/products/{id}/reviews")
    public ResponseEntity<ReviewDTO> createReview(@PathVariable(name = "id") Long productId, @RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.ok(this.productService.createReview(productId, reviewDTO));
    }

    @GetMapping("/products/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable Long id) {
        return ResponseEntity.ok(this.productService.findAllReviewsForProduct(id));
    }
}
