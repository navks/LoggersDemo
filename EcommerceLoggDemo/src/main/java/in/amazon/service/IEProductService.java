package in.amazon.service;


import java.util.List;

import in.amazon.dto.EProductDto;

public interface IEProductService {

	EProductDto saveEProduct(EProductDto productDto);

	EProductDto getProductById(Long id);

	EProductDto deleteById(Long id);

	EProductDto updateEProduct(Long id, EProductDto productDto);

	List<EProductDto> getAllProducts();



}
