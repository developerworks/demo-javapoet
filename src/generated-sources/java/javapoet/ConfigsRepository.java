package javapoet;

import java.lang.Long;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
interface ConfigsRepository extends JpaRepository<Configs, Long>, JpaSpecificationExecutor<Configs> {
}
