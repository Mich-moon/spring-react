//  repository for persisting and accessing data for roles

package com.demo.project2.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.project2.model.URole;
import com.demo.project2.model.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(URole name);

}
