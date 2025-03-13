package in.amazon.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.slf4j.Logger;
import in.amazon.dto.EProductDto;
import in.amazon.service.IEProductService;

@RestController
@RequestMapping("/api/v1/Eproduct")
public class EProductController {

	private static final Logger logger = LoggerFactory.getLogger(EProductController.class);

	@Autowired
	private IEProductService productService;

	@PostMapping("/saveProducts")
	public ResponseEntity<EProductDto> saveProduct(@RequestBody EProductDto productDto) {
		try {
			logger.info("Received request to add product: {}", productDto.getName());
			logger.info("Received request to add product: {}", productDto.getPrice());
			EProductDto savedproduct = productService.saveEProduct(productDto);
			return new ResponseEntity<EProductDto>(savedproduct, HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("Error while saving product: {}", e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<EProductDto> getProduct(@PathVariable Long id) {
		try {
			logger.info("Received request to fetch product with id: {}", id);
			EProductDto product = productService.getProductById(id);
			if (product == null) {
				logger.warn("Product with id: {} not found", id);
				return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<EProductDto>(product, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			logger.error("Error while fetching product: {}", e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<EProductDto> deleteById(@PathVariable Long id) {
		logger.info("Received request to delete the product with id: {}", id);
        try {
            EProductDto result = productService.deleteById(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while deleting product: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@PutMapping("/update/{id}")
    public ResponseEntity<EProductDto> updateProduct(@PathVariable Long id, @RequestBody EProductDto productDto) {
        try {
            logger.info("Received request to update product with id: {}", id);
            EProductDto updatedProduct = productService.updateEProduct(id, productDto);
            if (updatedProduct == null) {
                logger.warn("Product with id: {} not found for update", id);
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while updating product: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<EProductDto>> getAllProducts() {
        try {
            logger.info("Received request to fetch all products.");
            List<EProductDto> productList = productService.getAllProducts();
            if (productList.isEmpty()) {
                logger.warn("No products found.");
                return new ResponseEntity<>(productList, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(productList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while fetching all products: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
