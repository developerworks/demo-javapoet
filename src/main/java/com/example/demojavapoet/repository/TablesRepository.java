package com.example.demojavapoet.repository;

import com.example.demojavapoet.entity.VTables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TablesRepository extends JpaRepository<VTables, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM v_tables WHERE TABLE_SCHEMA = ?1 AND TABLE_TYPE = ?2")
    List<VTables> fetchAll(String dbName, String tableType);
}
