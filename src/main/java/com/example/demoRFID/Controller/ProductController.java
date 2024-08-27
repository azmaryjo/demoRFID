package com.example.demoRFID.Controller;

import com.example.demoRFID.ErrorMessage;
import com.example.demoRFID.Model.Exceptions.InvalidInputException;
import com.example.demoRFID.Model.Product;
import com.example.demoRFID.Model.Exceptions.ResourceNotFoundException;
import com.example.demoRFID.Service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Create a new product.
     *
     * @param product The product details.
     * @return The created product or an error message.
     */
    @Operation(summary = "Create a new product", description = "Creates a new product in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        Product createdProduct;
        try {
            createdProduct = productService.createProduct(product);
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());

        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        catch (Exception ex) {
            // Handle any other exceptions that might occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }


    /**
     * Get a product by its reference code.
     *
     * @param refCode The reference code of the product.
     * @return The product details or an error message.
     */
    @Operation(summary = "Get a product by reference code", description = "Retrieves a product by its reference code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid reference code",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/{refCode}")
    public ResponseEntity<?> getProductByRefCode(@PathVariable Long refCode) {
        Product product;
        try {
            product = productService.getProductById(refCode);

        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());

        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            // Handle any other exceptions that might occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
        return ResponseEntity.ok(product);
    }

    /**
     * Get all products.
     *
     * @return The list of all products or an error message.
     */
    @Operation(summary = "Get all products", description = "Retrieves a list of all products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "No products found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = List.of();
        try {
            products = productService.getAllProducts();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            // Handle any other exceptions that might occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Update an existing product.
     *
     * @param productDetails The updated product details.
     * @return The updated product or an error message.
     */
    @Operation(summary = "Update an existing product", description = "Updates the details of an existing product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @PutMapping("/{refCode}")
    public ResponseEntity<?> updateProduct(@RequestBody Product productDetails) {
        Product savedProduct;
        try {
            savedProduct = productService.updateProduct(productDetails);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            // Handle any other exceptions that might occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
        return new ResponseEntity<>(savedProduct, HttpStatus.OK);
    }

    /**
     * Delete a product by its reference code.
     *
     * @param refCode The reference code of the product to be deleted.
     * @return A success message or an error message.
     */
    @Operation(summary = "Delete a product by reference code", description = "Deletes a product from the system using its reference code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid reference code"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Product deletion conflict due to data integrity issues"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    @DeleteMapping("/{refCode}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long refCode) {
        try {
            productService.deleteProduct(refCode);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());

        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(ErrorMessage.PRODUCT_DELETE_FAILURE.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(ErrorMessage.PRODUCT_DELETE_SUCCESS.format(refCode));
    }

}
