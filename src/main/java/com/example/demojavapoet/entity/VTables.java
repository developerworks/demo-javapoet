package com.example.demojavapoet.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "v_tables", schema = "javapoet")
@Immutable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VTables {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "TABLE_CATALOG")
    private String tableCatalog;

    @Column(name = "TABLE_SCHEMA")
    private String tableSchema;

    @Column(name = "TABLE_NAME")
    private String tableName;

    @Column(name = "TABLE_TYPE")
    private String tableType;

    @Column(name = "ENGINE")
    private String engine;

    @Column(name = "VERSION")
    private Long version;

    @Column(name = "ROW_FORMAT")
    private String rowFormat;

    @Column(name = "TABLE_ROWS")
    private Long tableRows;

    @Column(name = "AVG_ROW_LENGTH")
    private Long avgRowLength;

    @Column(name = "DATA_LENGTH")
    private Long dataLength;

    @Column(name = "MAX_DATA_LENGTH")
    private Long maxDataLength;

    @Column(name = "INDEX_LENGTH")
    private Long indexLength;

    @Column(name = "DATA_FREE")
    private Long dataFree;

    @Column(name = "AUTO_INCREMENT")
    private Long autoIncrement;

    @Column(name = "CREATE_TIME")
    private Timestamp createTime;

    @Column(name = "UPDATE_TIME")
    private Timestamp updateTime;

    @Column(name = "CHECK_TIME")
    private Timestamp checkTime;

    @Column(name = "TABLE_COLLATION")
    private String tableCollation;

    @Column(name = "CHECKSUM")
    private Long checksum;

    @Column(name = "CREATE_OPTIONS")
    private String createOptions;

    @Column(name = "TABLE_COMMENT")
    private String tableComment;
}
