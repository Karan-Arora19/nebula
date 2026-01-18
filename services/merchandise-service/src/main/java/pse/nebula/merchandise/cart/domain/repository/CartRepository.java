package pse.nebula.merchandise.cart.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pse.nebula.merchandise.cart.domain.model.Cart;

public interface CartRepository extends JpaRepository<Cart, String> {
}
