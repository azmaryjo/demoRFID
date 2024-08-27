package com.example.demoRFID.Service;

import com.example.demoRFID.ErrorCode;
import com.example.demoRFID.ErrorMessage;
import com.example.demoRFID.Model.Exceptions.InvalidInputException;
import com.example.demoRFID.Model.Product;
import com.example.demoRFID.Model.Exceptions.ResourceNotFoundException;
import com.example.demoRFID.Repository.ProductRepository;
import com.example.demoRFID.Utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.demoRFID.Constants.REFCODE_LENGTH;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository) {
        this.productRepository=productRepository;
    }

    /**
     * Creates a new Product in the system.
     * This method validates the product's reference code and name before saving it to the database.
     * If the product already exists, a DuplicateKeyException is thrown.
     *
     * @param product The Product object to be created.
     * @return The saved Product object.
     * @throws InvalidInputException if the reference code or name is invalid.
     * @throws DuplicateKeyException if a product with the same reference code already exists.
     */
    public Product createProduct(Product product) {
        StringBuilder message=new StringBuilder();
        if(!ValidationUtils.isValidRefCode(product.getRefCode().toString())){
            message.append(ErrorMessage.INVALID_REFCODE.format(REFCODE_LENGTH)).append("\n");
        }
        if(ValidationUtils.isNullOrEmpty(product.getName())){
            message.append(ErrorMessage.INVALID_REFCODE.format(REFCODE_LENGTH));
        }
        if(!message.isEmpty()){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.PRODUCT_INV_IN.getMessage(), message);
            throw new InvalidInputException(message.toString());
        }
        //return error if product already exists
        if(productRepository.findById(product.getRefCode()).isPresent()){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.PRODUCT_DATA_INT.getMessage(), message);
            throw new DuplicateKeyException(ErrorMessage.PRODUCT_ALREADY_EXISTS.format(product.getRefCode()));
        }
        logger.info("{}|{}",LocalDateTime.now(),ErrorMessage.PRODUCT_SUCCESS.format(product.getRefCode()));
        return productRepository.save(product);
    }

    /**
     * Retrieves a Product by its reference code.
     * This method validates the reference code before fetching the product from the database.
     * If the product does not exist, a ResourceNotFoundException is thrown.
     *
     * @param refCode The reference code of the product to retrieve.
     * @return The Product object with the specified reference code.
     * @throws InvalidInputException if the reference code is invalid.
     * @throws ResourceNotFoundException if no product with the specified reference code is found.
     */
    public Product getProductById(Long refCode) {
        if(!ValidationUtils.isValidRefCode(refCode.toString())){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.PRODUCT_INV_IN.getMessage(), ErrorMessage.INVALID_REFCODE.format(REFCODE_LENGTH));
            throw new InvalidInputException(ErrorMessage.INVALID_REFCODE.format(REFCODE_LENGTH));
        }
        Optional<Product> product = productRepository.findById(refCode);
        if (product.isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.PRODUCT_RES_NOT_FOUND.getMessage(), ErrorMessage.PRODUCT_NOT_FOUND.format(refCode));
            throw new ResourceNotFoundException(ErrorMessage.PRODUCT_NOT_FOUND.format(refCode));
        }
        logger.info("{}|{}",LocalDateTime.now(),ErrorMessage.PRODUCT_FETCH_SUCCESS.format(refCode));
        return product.get();
    }

    /**
     * Retrieves all Products from the database.
     * If no products are found, a ResourceNotFoundException is thrown.
     *
     * @return A list of all Product objects in the database.
     * @throws ResourceNotFoundException if no products are found.
     */
    public List<Product> getAllProducts() {
        List<Product> productsFromDb= productRepository.findAll();
        if(productsFromDb.isEmpty()){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.PRODUCT_RES_NOT_FOUND.getMessage(),ErrorMessage.NO_PRODUCTS.getMessage());
            throw new ResourceNotFoundException(ErrorMessage.NO_PRODUCTS.getMessage());
        }
        logger.info("{}|{}",LocalDateTime.now(),ErrorMessage.PRODUCT_FETCH_ALL_SUCCESS.format(productsFromDb.size()));
        return productsFromDb;
    }

    /**
     * Updates an existing Product in the database.
     * This method validates the product's reference code and name before updating it in the database.
     * If the product does not exist, a ResourceNotFoundException is thrown.
     *
     * @param product The Product object to be updated.
     * @return The updated Product object.
     * @throws InvalidInputException if the reference code or name is invalid.
     * @throws ResourceNotFoundException if no product with the specified reference code is found.
     */
    public Product updateProduct(Product product) {
        StringBuilder message=new StringBuilder();
        if(!ValidationUtils.isValidRefCode(product.getRefCode().toString())){
            message.append(ErrorMessage.INVALID_REFCODE.format(REFCODE_LENGTH)).append("\n");
        }
        if(ValidationUtils.isNullOrEmpty(product.getName())){
            message.append(ErrorMessage.INVALID_REFCODE.format(REFCODE_LENGTH));
        }
        if(!message.isEmpty()){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.PRODUCT_INV_IN.getMessage(), message);
            throw new InvalidInputException(message.toString());
        }
        //return error if product does not exist
        if (productRepository.findById(product.getRefCode()).isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.PRODUCT_RES_NOT_FOUND.getMessage(), ErrorMessage.PRODUCT_NOT_FOUND.format(product.getRefCode()));
            throw new ResourceNotFoundException(ErrorMessage.PRODUCT_NOT_FOUND.format(product.getRefCode()));
        }
        logger.info("{}|{}",LocalDateTime.now(),ErrorMessage.PRODUCT_UPDATE_SUCCESS.format(product.getRefCode()));
        return productRepository.save(product);
    }

    /**
     * Deletes a Product by its reference code.
     * This method validates the reference code before deleting the product from the database.
     * If the product does not exist, a ResourceNotFoundException is thrown.
     *
     * @param refCode The reference code of the product to delete.
     * @throws InvalidInputException if the reference code is invalid.
     * @throws ResourceNotFoundException if no product with the specified reference code is found.
     */
    public void deleteProduct(Long refCode) {
        if(!ValidationUtils.isValidRefCode(refCode.toString())){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.PRODUCT_INV_IN.getMessage(), ErrorMessage.INVALID_REFCODE.format(REFCODE_LENGTH));
            throw new InvalidInputException(ErrorMessage.INVALID_REFCODE.format(REFCODE_LENGTH));
        }
        //return error if product does not exist
        if (productRepository.findById(refCode).isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.PRODUCT_RES_NOT_FOUND.getMessage(), ErrorMessage.PRODUCT_NOT_FOUND.format(refCode));
            throw new ResourceNotFoundException(ErrorMessage.PRODUCT_NOT_FOUND.format(refCode));
        }
        //if product is already attached to a rfid a DataIntegrityViolationException will be automatically thrown
        logger.info("{}|{}",LocalDateTime.now(),ErrorMessage.PRODUCT_DEL_SUCCESS.format(refCode));
        productRepository.deleteById(refCode);
    }
}

