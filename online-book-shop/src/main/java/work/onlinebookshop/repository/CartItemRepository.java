package work.onlinebookshop.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import work.onlinebookshop.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = "book")
    Page<CartItem> findAllByShoppingCartId(Long shoppingCartId, Pageable pageable);

    Optional<CartItem> findByShoppingCartIdAndBookId(Long shoppingCartId, Long bookId);

    Optional<CartItem> findByIdAndShoppingCartId(Long id, Long shoppingCartId);

    void deleteAllByBookId(Long bookId);
}
