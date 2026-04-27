package com.student.products;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    /** Datos fijos en memoria (equivalente pedagógico a List.of del enunciado). */
    private static final List<Product> PRODUCTS = List.of(
            new Product(1L, "Teclado", 45.90),
            new Product(2L, "Ratón", 19.50),
            new Product(3L, "Monitor", 210.00));

    @GetMapping("/products")
    public List<Product> all() {
        return PRODUCTS;
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> one(@PathVariable long id) {
        return PRODUCTS.stream()
                .filter(p -> p.id() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
