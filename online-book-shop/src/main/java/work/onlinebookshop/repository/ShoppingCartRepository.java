package work.onlinebookshop.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import work.onlinebookshop.model.ShoppingCart;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUserId(Long userId);
}
