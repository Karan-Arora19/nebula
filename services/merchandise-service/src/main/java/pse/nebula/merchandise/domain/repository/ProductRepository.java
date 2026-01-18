package pse.nebula.merchandise.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pse.nebula.merchandise.domain.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
