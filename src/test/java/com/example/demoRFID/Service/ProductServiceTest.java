package com.example.demoRFID.Service;

import com.example.demoRFID.ErrorMessage;
import com.example.demoRFID.Model.Product;
import com.example.demoRFID.Model.Exceptions.InvalidInputException;
import com.example.demoRFID.Model.Exceptions.ResourceNotFoundException;
import com.example.demoRFID.Repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.Optional;

import static com.example.demoRFID.Constants.REFCODE_LENGTH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;


    @InjectMocks
    private ProductService productService;

    private Product validProduct;
    private Product invalidProduct;

    @BeforeEach
    public void setUp() {
        validProduct = new Product();
        validProduct.setRefCode(12345L);
        validProduct.setName("Valid Product");

        invalidProduct = new Product();
        invalidProduct.setRefCode(123L);
        invalidProduct.setName("");
    }

    @Test
    public void testCreateProduct_ValidProduct() {
        when(productRepository.findById(validProduct.getRefCode())).thenReturn(Optional.empty());
        when(productRepository.save(validProduct)).thenReturn(validProduct);

        Product createdProduct = productService.createProduct(validProduct);

        assertNotNull(createdProduct);
        assertEquals(validProduct, createdProduct);
        verify(productRepository, times(1)).save(validProduct);
    }

    @Test
    public void testCreateProduct_InvalidProduct_ThrowsInvalidInputException() {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            productService.createProduct(invalidProduct);
        });

        assertTrue(exception.getMessage().contains(ErrorMessage.INVALID_REFCODE.format(REFCODE_LENGTH)));
    }

    @Test
    public void testCreateProduct_DuplicateProduct_ThrowsDuplicateKeyException() {
        when(productRepository.findById(validProduct.getRefCode())).thenReturn(Optional.of(validProduct));

        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class, () -> {
            productService.createProduct(validProduct);
        });

        assertTrue(exception.getMessage().contains(ErrorMessage.PRODUCT_ALREADY_EXISTS.format(validProduct.getRefCode())));
    }

    @Test
    public void testGetProductById_ValidId() {
        when(productRepository.findById(validProduct.getRefCode())).thenReturn(Optional.of(validProduct));

        Product foundProduct = productService.getProductById(validProduct.getRefCode());

        assertNotNull(foundProduct);
        assertEquals(validProduct, foundProduct);
        verify(productRepository, times(1)).findById(validProduct.getRefCode());
    }

    @Test
    public void testGetProductById_InvalidId_ThrowsInvalidInputException() {
        Long invalidRefCode = 123L;

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            productService.getProductById(invalidRefCode);
        });

        assertTrue(exception.getMessage().contains(ErrorMessage.INVALID_REFCODE.format(REFCODE_LENGTH)));
    }

    @Test
    public void testGetProductById_ProductNotFound_ThrowsResourceNotFoundException() {
        when(productRepository.findById(validProduct.getRefCode())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(validProduct.getRefCode());
        });

        assertTrue(exception.getMessage().contains(ErrorMessage.PRODUCT_NOT_FOUND.format(validProduct.getRefCode())));
    }

    @Test
    public void testGetAllProducts_ProductsFound() {
        when(productRepository.findAll()).thenReturn(List.of(validProduct));

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
        assertEquals(validProduct, products.get(0));
    }

    @Test
    public void testGetAllProducts_NoProductsFound_ThrowsResourceNotFoundException() {
        when(productRepository.findAll()).thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.getAllProducts();
        });

        assertTrue(exception.getMessage().contains(ErrorMessage.NO_PRODUCTS.getMessage()));
    }

    @Test
    public void testUpdateProduct_ValidProduct() {
        when(productRepository.findById(validProduct.getRefCode())).thenReturn(Optional.of(validProduct));
        when(productRepository.save(validProduct)).thenReturn(validProduct);

        Product updatedProduct = productService.updateProduct(validProduct);

        assertNotNull(updatedProduct);
        assertEquals(validProduct, updatedProduct);
        verify(productRepository, times(1)).save(validProduct);
    }

    @Test
    public void testUpdateProduct_ProductNotFound_ThrowsResourceNotFoundException() {
        when(productRepository.findById(validProduct.getRefCode())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(validProduct);
        });

        assertTrue(exception.getMessage().contains(ErrorMessage.PRODUCT_NOT_FOUND.format(validProduct.getRefCode())));
    }

    @Test
    public void testDeleteProduct_ValidId() {
        when(productRepository.findById(validProduct.getRefCode())).thenReturn(Optional.of(validProduct));

        productService.deleteProduct(validProduct.getRefCode());

        verify(productRepository, times(1)).deleteById(validProduct.getRefCode());
    }

    @Test
    public void testDeleteProduct_InvalidId_ThrowsInvalidInputException() {
        Long invalidRefCode = 123L;

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            productService.deleteProduct(invalidRefCode);
        });

        assertTrue(exception.getMessage().contains(ErrorMessage.INVALID_REFCODE.format(REFCODE_LENGTH)));
    }

    @Test
    public void testDeleteProduct_ProductNotFound_ThrowsResourceNotFoundException() {
        when(productRepository.findById(validProduct.getRefCode())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(validProduct.getRefCode());
        });

        assertTrue(exception.getMessage().contains(ErrorMessage.PRODUCT_NOT_FOUND.format(validProduct.getRefCode())));
    }
}
