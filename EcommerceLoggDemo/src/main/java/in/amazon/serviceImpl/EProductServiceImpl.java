package in.amazon.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import in.amazon.dto.EProductDto;
import in.amazon.entity.EProduct;
import in.amazon.exceptions.ProductAlreadyExistsException;
import in.amazon.exceptions.ProductNotFoundException;
import in.amazon.repository.EProduct_Repository;
import in.amazon.service.IEProductService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class EProductServiceImpl implements IEProductService {

	private static final Logger logger = LoggerFactory.getLogger(EProductServiceImpl.class);
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	@Autowired
	private EProduct_Repository productRepo;

	private EProductDto convertToDto(EProduct eProduct) {
		if (eProduct == null)
			return null;
		EProductDto pDto = new EProductDto();
		pDto.setId(eProduct.getId());
		pDto.setName(eProduct.getName());
		pDto.setPrice(eProduct.getPrice());
		return pDto;

	}

	private EProduct convertToEntity(EProductDto eproductDto) {
		if (eproductDto == null)
			return null;
		EProduct pEntity = new EProduct();
		pEntity.setName(eproductDto.getName());
		pEntity.setPrice(eproductDto.getPrice());
		return pEntity;

	}

	@Override
	public EProductDto saveEProduct(EProductDto productDto) {
		
		lock.writeLock().lock();
		
		try {
		if (productDto == null) {
			logger.error("Product data is null.");
			throw new IllegalArgumentException("Product data cannot be null.");
		}
		logger.debug("Adding products: {}", productDto.getName(), productDto.getPrice());

	
			Optional<EProduct> existingProduct = productRepo.findByName(productDto.getName());
			if (existingProduct.isPresent()) {
				logger.warn("Product with name '{}' already exists.", productDto.getName());
				throw new ProductAlreadyExistsException(
						"Product with name '" + productDto.getName() + "' already exists.");
			}
			EProduct result = convertToEntity(productDto);
			EProduct saved = productRepo.save(result);
			logger.info("Product added successfully: {}", saved);
			return convertToDto(saved);

		} catch (Exception e) {
			logger.error("Database error while saving product: {}", e.getMessage());
			throw new RuntimeException("Database error occurred. Please try again later.");
		}
		
	 finally {
         lock.writeLock().unlock();
     }
	}

	@Override
	public EProductDto getProductById(Long id) {
		
		lock.readLock().lock();
		try {
		if (id == null) {
			logger.error("Product ID is null.");
			throw new IllegalArgumentException("Product ID cannot be null.");
		}
		logger.debug("Fetching product with id: {}", id);

	
			Optional<EProduct> eProduct = productRepo.findById(id);
			if (eProduct.isEmpty()) {
				logger.warn("Product with id '{}' not found.", id);
				throw new ProductNotFoundException("Product with id " + id + " not found.");
			}
			logger.info("Product with id '{}' retrieved successfully.", id);
			return convertToDto(eProduct.get());

		} catch (Exception e) {
			logger.error("Database error while fetching product by ID: {}", e.getMessage());
			throw new RuntimeException("Database error occurred. Please try again later.");

		}
	finally {
        lock.readLock().unlock();
    }

	}

	@Override
	public EProductDto deleteById(Long id) {
		
		lock.writeLock().lock();
		try {
		if (id == null) {
            logger.error("Product ID is null.");
            throw new IllegalArgumentException("Product ID cannot be null.");
		}
		logger.debug("Deleting product with id: {}", id);
		
	            Optional<EProduct> eProduct = productRepo.findById(id);
	            if (eProduct.isEmpty()) {
	                logger.warn("Product with id '{}' not found.", id);
	                throw new ProductNotFoundException("Product with id " + id + " not found.");
	            }
	            productRepo.deleteById(id);
	            logger.info("Product with id '{}' deleted successfully.", id);
	            return convertToDto(eProduct.get());

	        } catch (Exception e) {
	            logger.error("Database error while deleting product: {}", e.getMessage());
	            throw new RuntimeException("Database error occurred. Please try again later.");
	        }
		finally {
            lock.writeLock().unlock();
        }

	}

	@Override
	public EProductDto updateEProduct(Long id, EProductDto productDto) {
		 lock.writeLock().lock();
		    try {
		        if (id == null) {
		            logger.error("Product ID is null.");
		            throw new IllegalArgumentException("Product ID cannot be null.");
		        }
		        if (productDto == null) {
		            logger.error("Product data is null.");
		            throw new IllegalArgumentException("Product data cannot be null.");
		        }
		        logger.debug("Updating product with id: {}", id);

		        // Check if the product exists by its ID
		        Optional<EProduct> existingProduct = productRepo.findById(id);
		        if (existingProduct.isEmpty()) {
		            logger.warn("Product with id '{}' not found for update.", id);
		            throw new ProductNotFoundException("Product with id " + id + " not found.");
		        }

		        // Update the product with the new data
		        EProduct productToUpdate = existingProduct.get();
		        productToUpdate.setName(productDto.getName());  // Update name
		        productToUpdate.setPrice(productDto.getPrice());  // Update price

		        // Save the updated product in the database
		        EProduct updatedProduct = productRepo.save(productToUpdate);
		        logger.info("Product with id '{}' updated successfully.", id);

		        return convertToDto(updatedProduct);  // Convert to DTO and return
		    } catch (Exception e) {
		        logger.error("Error while updating product with id '{}': {}", id, e.getMessage());
		        throw new RuntimeException("Database error occurred while updating the product.");
		    } finally {
		        lock.writeLock().unlock();  
		    }
	}

	@Override
	public List<EProductDto> getAllProducts() {
		 lock.readLock().lock();  
		    try {
		        logger.debug("Fetching all products.");

		        // Retrieve all products from the database
		        List<EProduct> allProducts = productRepo.findAll();
		        
		        if (allProducts.isEmpty()) {
		            logger.warn("No products found.");
		        }

		        // Convert the list of EProduct entities to EProductDto objects
		        List<EProductDto> productDtos = allProducts.stream()
		                .map(this::convertToDto)
		                .collect(Collectors.toList());

		        return productDtos;  // Return the list of DTOs
		    } catch (Exception e) {
		        logger.error("Error while fetching all products: {}", e.getMessage());
		        throw new RuntimeException("Database error occurred while fetching the products.");
		    } finally {
		        lock.readLock().unlock(); 
		    }
	}
}
