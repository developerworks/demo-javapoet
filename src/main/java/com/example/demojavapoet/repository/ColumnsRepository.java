package com.example.demojavapoet.repository;

import com.example.demojavapoet.entity.VColumns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnsRepository extends JpaRepository<VColumns, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM v_columns WHERE TABLE_SCHEMA = ?1 AND TABLE_NAME = ?2")
    List<VColumns> fetchAll(String dbName, String tableName);
}
