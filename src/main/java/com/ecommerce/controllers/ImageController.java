package com.ecommerce.controllers;

import com.ecommerce.models.ProductImage;
import com.ecommerce.repositories.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ProductImageRepository productImageRepository;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        return productImageRepository.findById(id)
                .map(image -> {
                    HttpHeaders headers = new HttpHeaders();
                    if (image.getContentType() != null) {
                        headers.add(HttpHeaders.CONTENT_TYPE, image.getContentType());
                    } else {
                        headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");
                    }
                    return new ResponseEntity<>(image.getImageData(), headers, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
