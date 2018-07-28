package javapoet;

import java.lang.Long;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
interface ConfigRepository extends JpaRepository<Config, Long>, JpaSpecificationExecutor<Config> {
}
