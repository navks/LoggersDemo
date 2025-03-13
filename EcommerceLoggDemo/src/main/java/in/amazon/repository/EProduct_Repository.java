package in.amazon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.amazon.entity.EProduct;

@Repository
public interface EProduct_Repository extends JpaRepository<EProduct, Long> {
	
	Optional<EProduct> findByName(String name);

}
