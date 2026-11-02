package se331.project2backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se331.project2backend.entity.AppUser;
import se331.project2backend.entity.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("""
            select u from AppUser u
            where u.role = :role
              and u.level between :minLevel and :maxLevel
              and (:province = '' or u.province = :province)
              and (:q = ''
                   or lower(u.name) like lower(concat('%', :q, '%'))
                   or lower(u.surname) like lower(concat('%', :q, '%'))
                   or lower(concat(u.name, ' ', u.surname)) like lower(concat('%', :q, '%')))
            """)
    Page<AppUser> search(@Param("role") Role role,
                         @Param("q") String q,
                         @Param("minLevel") int minLevel,
                         @Param("maxLevel") int maxLevel,
                         @Param("province") String province,
                         Pageable pageable);

    @Query("select distinct u.province from AppUser u where u.role = :role and u.province is not null order by u.province")
    List<String> findDistinctProvinces(@Param("role") Role role);
}
