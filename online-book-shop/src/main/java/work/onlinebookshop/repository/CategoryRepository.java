package work.onlinebookshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import work.onlinebookshop.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
