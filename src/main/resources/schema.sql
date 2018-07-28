DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id`         bigint(20)  NOT NULL AUTO_INCREMENT,
  `name`       varchar(255) CHARACTER SET utf8
  COLLATE utf8_general_ci  NULL     DEFAULT NULL,
  `created_at` datetime(0) NULL     DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)
  ENGINE = MyISAM
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8;


CREATE OR REPLACE ALGORITHM = UNDEFINED
  DEFINER = `root`@`localhost`
  SQL SECURITY DEFINER VIEW `javapoet`.`v_tables` AS
  SELECT
    uuid_short()                                    AS `id`,
    `information_schema`.`tables`.`TABLE_CATALOG`   AS `TABLE_CATALOG`,
    `information_schema`.`tables`.`TABLE_SCHEMA`    AS `TABLE_SCHEMA`,
    `information_schema`.`tables`.`TABLE_NAME`      AS `TABLE_NAME`,
    `information_schema`.`tables`.`TABLE_TYPE`      AS `TABLE_TYPE`,
    `information_schema`.`tables`.`ENGINE`          AS `ENGINE`,
    `information_schema`.`tables`.`VERSION`         AS `VERSION`,
    `information_schema`.`tables`.`ROW_FORMAT`      AS `ROW_FORMAT`,
    `information_schema`.`tables`.`TABLE_ROWS`      AS `TABLE_ROWS`,
    `information_schema`.`tables`.`AVG_ROW_LENGTH`  AS `AVG_ROW_LENGTH`,
    `information_schema`.`tables`.`DATA_LENGTH`     AS `DATA_LENGTH`,
    `information_schema`.`tables`.`MAX_DATA_LENGTH` AS `MAX_DATA_LENGTH`,
    `information_schema`.`tables`.`INDEX_LENGTH`    AS `INDEX_LENGTH`,
    `information_schema`.`tables`.`DATA_FREE`       AS `DATA_FREE`,
    `information_schema`.`tables`.`AUTO_INCREMENT`  AS `AUTO_INCREMENT`,
    `information_schema`.`tables`.`CREATE_TIME`     AS `CREATE_TIME`,
    `information_schema`.`tables`.`UPDATE_TIME`     AS `UPDATE_TIME`,
    `information_schema`.`tables`.`CHECK_TIME`      AS `CHECK_TIME`,
    `information_schema`.`tables`.`TABLE_COLLATION` AS `TABLE_COLLATION`,
    `information_schema`.`tables`.`CHECKSUM`        AS `CHECKSUM`,
    `information_schema`.`tables`.`CREATE_OPTIONS`  AS `CREATE_OPTIONS`,
    `information_schema`.`tables`.`TABLE_COMMENT`   AS `TABLE_COMMENT`
  FROM
    `information_schema`.`tables`;

CREATE OR REPLACE ALGORITHM = UNDEFINED
  DEFINER = `root`@`localhost`
  SQL SECURITY DEFINER VIEW `javapoet`.`v_columns` AS
  SELECT
    uuid_short()                                              AS `id`,
    `information_schema`.`columns`.`TABLE_CATALOG`            AS `TABLE_CATALOG`,
    `information_schema`.`columns`.`TABLE_SCHEMA`             AS `TABLE_SCHEMA`,
    `information_schema`.`columns`.`TABLE_NAME`               AS `TABLE_NAME`,
    `information_schema`.`columns`.`COLUMN_NAME`              AS `COLUMN_NAME`,
    `information_schema`.`columns`.`ORDINAL_POSITION`         AS `ORDINAL_POSITION`,
    `information_schema`.`columns`.`COLUMN_DEFAULT`           AS `COLUMN_DEFAULT`,
    `information_schema`.`columns`.`IS_NULLABLE`              AS `IS_NULLABLE`,
    `information_schema`.`columns`.`DATA_TYPE`                AS `DATA_TYPE`,
    `information_schema`.`columns`.`CHARACTER_MAXIMUM_LENGTH` AS `CHARACTER_MAXIMUM_LENGTH`,
    `information_schema`.`columns`.`CHARACTER_OCTET_LENGTH`   AS `CHARACTER_OCTET_LENGTH`,
    `information_schema`.`columns`.`NUMERIC_PRECISION`        AS `NUMERIC_PRECISION`,
    `information_schema`.`columns`.`NUMERIC_SCALE`            AS `NUMERIC_SCALE`,
    `information_schema`.`columns`.`DATETIME_PRECISION`       AS `DATETIME_PRECISION`,
    `information_schema`.`columns`.`CHARACTER_SET_NAME`       AS `CHARACTER_SET_NAME`,
    `information_schema`.`columns`.`COLLATION_NAME`           AS `COLLATION_NAME`,
    `information_schema`.`columns`.`COLUMN_TYPE`              AS `COLUMN_TYPE`,
    `information_schema`.`columns`.`COLUMN_KEY`               AS `COLUMN_KEY`,
    `information_schema`.`columns`.`EXTRA`                    AS `EXTRA`,
    `information_schema`.`columns`.`PRIVILEGES`               AS `PRIVILEGES`,
    `information_schema`.`columns`.`COLUMN_COMMENT`           AS `COLUMN_COMMENT`,
    `information_schema`.`columns`.`GENERATION_EXPRESSION`    AS `GENERATION_EXPRESSION`
  FROM
    `information_schema`.`columns`;