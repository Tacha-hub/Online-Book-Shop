package work.onlinebookshop.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import work.onlinebookshop.model.Role;
import work.onlinebookshop.model.Role.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
