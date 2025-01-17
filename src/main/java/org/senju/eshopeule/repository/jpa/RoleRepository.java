package org.senju.eshopeule.repository.jpa;

import org.senju.eshopeule.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);

    void deleteByName(String name);

    @Query(value = "SELECT r FROM Role r WHERE r.name NOT IN ('ADMIN', 'CUSTOMER', 'VENDOR')")
    List<Role> getAllStaffRole();
}
