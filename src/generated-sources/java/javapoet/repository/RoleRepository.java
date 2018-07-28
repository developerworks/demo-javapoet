package javapoet.repository;

import java.lang.Long;
import javapoet.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
}
