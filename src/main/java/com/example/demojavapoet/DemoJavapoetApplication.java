package com.example.demojavapoet;

import com.example.demojavapoet.entity.VColumns;
import com.example.demojavapoet.entity.VTables;
import com.example.demojavapoet.repository.ColumnsRepository;
import com.example.demojavapoet.repository.TablesRepository;
import com.example.demojavapoet.util.AnnotationSpecUtils;
import com.example.demojavapoet.util.TableType;
import com.squareup.javapoet.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNullApi;
import org.springframework.util.StringUtils;

import javax.lang.model.element.Modifier;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@SpringBootApplication
@Slf4j
public class DemoJavapoetApplication implements ApplicationContextAware {

    public static final String INDENT = "    ";
    public static final File resourcesDirectory = new File("src/generated-sources/java");
    public static final String packageName = "com.example.generated";
    private static final String databaseName = "gold_dev";

    public static Map<String, String> dataTypes = new HashMap<>();

    private TablesRepository tablesRepository;
    private ColumnsRepository columnsRepository;
    private List<VTables> baseTables;

    private ApplicationContext applicationContext;

    @Autowired
    public void setTablesRepository(TablesRepository tablesRepository) {
        this.tablesRepository = tablesRepository;
    }

    @Autowired
    public void setColumnsRepository(ColumnsRepository columnsRepository) {
        this.columnsRepository = columnsRepository;
    }

    public static void main(String[] args) {
        // Mysql to Java
        dataTypes.put("bigint", "java.lang.Long");
        dataTypes.put("binary", "java.lang.Byte");
        dataTypes.put("bit", "java.lang.Boolean");
        dataTypes.put("blob", "java.sql.Blob");
        dataTypes.put("char", "java.lang.String");
        dataTypes.put("date", "java.sql.Date");
        dataTypes.put("datetime", "java.sql.Timestamp");
        dataTypes.put("decimal", "java.math.BigDecimal");
        dataTypes.put("double", "java.lang.Double");
        dataTypes.put("float", "java.lang.Float");
        dataTypes.put("int", "java.lang.Integer");
        dataTypes.put("longblob", "java.sql.Blob");
        dataTypes.put("smallint", "java.lang.Short");
        dataTypes.put("text", "java.lang.String");
        dataTypes.put("time", "java.sql.Time");
        dataTypes.put("timestamp", "java.sql.Timestamp");
        dataTypes.put("tinyint", "java.lang.Byte");
        dataTypes.put("varchar", "java.lang.String");
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(DemoJavapoetApplication.class)
            .run();
//        SpringApplication.run(DemoJavapoetApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner() {

        // FIXME:有依赖关系, Service 类中依赖实体类, 需要CLASSPATH中存在对应的 $Type.class 文件才能生成服务类.
        // FIXME:服务实现依赖服务接口
        // FIXME:仓库类依赖实体类
        return args -> {
            // 获取基础表
            getBaseTables();
            // 生成实体
            generateEntities();
            // todo: 生成 Dto 数据传输对象
            // 生成服务接口
//            generateServices();
            // 生成数据访问接口
//            generateRepositories();
            // 生成服务实现类
            // todo: 生成动态分页查询代码
//            generateServiceImpls();
            // TODO: 生成 Webflux Rest Api 控制器类
            // TODO: 生成测试代码
            // TODO: 生成 Ant design CRUD 代码(PC端和移动端)

//            generateControllers();

//            SpringApplication.exit(this.applicationContext, () -> 0);

        };
    }

    private void generateEntities() {
        log.info(">>> 生成实体类...");
        baseTables.forEach(item -> {
            // 实体注解
            List<AnnotationSpec> annotationSpecList = AnnotationSpecUtils.entityAnnotations();
            // 实体字段
            List<FieldSpec> fieldSpecs = new ArrayList<>();

            // 获取主键名称
            log.info("当前数据库: {}, 表: {}", databaseName, item.getTableName());
            List<VColumns> primaryKeys = columnsRepository.getPrimaryKeyNames(databaseName, item.getTableName());

            if (primaryKeys.size() == 0) {
                log.error("当前数据库表 {} 没有主键, 终止生成...");
                return;
            } else {
                primaryKeys.forEach(column -> {
                    log.info("主键列: {}", column.getColumnName());
                });
            }
            // 填充字段
            columnsRepository.fetchAll(databaseName, item.getTableName()).forEach(column -> {
                FieldSpec.Builder fieldSpecBuiler = null;
                try {
                    // 主键列
                    if ("PRI".equals(column.getColumnKey())) {
                        AnnotationSpec id = AnnotationSpec.builder(ClassName.get("javax.persistence", "Id")).build();

                        AnnotationSpec generatedValue = AnnotationSpec
                            .builder(ClassName.get("javax.persistence", "GeneratedValue"))
                            .addMember(
                                "strategy",
                                CodeBlock.builder().add("$T.IDENTITY", Class.forName("javax.persistence.GenerationType")).build()
                            )
                            .build();

                        fieldSpecBuiler = FieldSpec
                            .builder(
                                Class.forName(dataTypes.get(column.getDataType())),
                                CaseUtils.toCamelCase(column.getColumnName(), false, '_'),
                                Modifier.PRIVATE
                            );
                        if (primaryKeys.size() == 1) {
                            fieldSpecBuiler
                                .addAnnotation(id)
                                .addAnnotation(generatedValue);
                        } else {
                            fieldSpecBuiler.addAnnotation(id);
                        }
                        fieldSpecBuiler.addAnnotation(AnnotationSpecUtils.jpaColumnAnnotation(column.getColumnName()));
                    }
                    // TODO: UNIQUE 列
                    // 非主键列
                    else {
                        fieldSpecBuiler = FieldSpec
                            .builder(
                                Class.forName(dataTypes.get(column.getDataType())),
                                CaseUtils.toCamelCase(column.getColumnName(), false, '_'),
                                Modifier.PRIVATE
                            )
                            .addAnnotation(AnnotationSpecUtils.jpaColumnAnnotation(column.getColumnName()));
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                FieldSpec fieldSpec = fieldSpecBuiler.build();

                // 生成额外的复合组件类
                if (primaryKeys.size() > 1) {
                    AnnotationSpecUtils.createIdClass(toCamelCase(item.getTableName()) + "PK", primaryKeys);
                }

                fieldSpecs.add(fieldSpec);
            });
            // JPA实体
            // TODO: Use google case formatter to convert snake case to camel case
            TypeSpec.Builder typeSpecBuilder = TypeSpec
                .classBuilder(CaseUtils.toCamelCase(item.getTableName(), true, '_'))
                .addJavadoc("Generated by javapoet\n")
                .addAnnotations(annotationSpecList)
                .addAnnotation(AnnotationSpecUtils.jpaTableAnnotation(item.getTableName()))
                .addModifiers(Modifier.PUBLIC)
                .addFields(fieldSpecs);

            if (primaryKeys.size() > 1) {
                typeSpecBuilder.addAnnotation(AnnotationSpecUtils.idClass(toCamelCase(item.getTableName()) + "PK.class"));
            }

            TypeSpec typeSpec = typeSpecBuilder.build();
            JavaFile javaFile = JavaFile.builder(String.format("%s.entity", packageName), typeSpec).indent(INDENT).build();
            try {
                javaFile.writeTo(resourcesDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateServices() {
        baseTables.forEach(item -> {
            // 服务接口
            String entityName = CaseUtils.toCamelCase(item.getTableName(), true, '_');
            TypeSpec typeSpec1 = TypeSpec
                .interfaceBuilder(entityName + "Service")
                .addJavadoc("Generated by javapoet\n")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(
                    MethodSpec.methodBuilder("get" + entityName)
                        .addJavadoc("通过ID获取用户对象\n")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(Long.class, "id")
                        .returns(
                            className("entity", item.getTableName())
                        )
                        .build()
                )
                .addMethod(
                    MethodSpec
                        .methodBuilder("update" + entityName)
                        .addJavadoc("更新对象\n")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(
                            className("entity", item.getTableName()),
                            item.getTableName().toLowerCase()
                        )
                        .addException(Exception.class)
                        .returns(
                            className("entity", item.getTableName())
                        ).build()
                )
                .addMethod(
                    MethodSpec
                        .methodBuilder("create" + entityName)
                        .addJavadoc("创建对象\n")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(
                            className("entity", item.getTableName()),
                            item.getTableName().toLowerCase()
                        )
                        .returns(
                            className("entity", item.getTableName())
                        ).build()
                )
                .addMethod(
                    MethodSpec
                        .methodBuilder("paginate" + entityName + "s")
                        .addJavadoc("对象分页列表\n")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(Integer.class, "page")
                        .addParameter(Integer.class, "size")
                        .addParameter(String.class, "sort")
                        .addParameter(String[].class, "sortby")
                        .returns(
                            ParameterizedTypeName.get(
                                ClassName.get("java.util", "List"),
                                ClassName.get(String.format("%s.%s", packageName, "entity"), entityName)
                            )
                        ).build()
                )
                .build();

            JavaFile javaFile1 = JavaFile.builder(String.format("%s.service", packageName), typeSpec1)

                .indent(INDENT)
                .build();

            try {
                javaFile1.writeTo(resourcesDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateServiceImpls() {
        baseTables.forEach(item -> {
            String entityName = CaseUtils.toCamelCase(item.getTableName(), true, '_');

            String repositoryFieldName = CaseUtils.toCamelCase(String.format("%s_repository", item.getTableName()), false, '_');
            Class<?> repositoryClass = className("repository", item.getTableName() + "_repository");

            // 更新方法
            MethodSpec.Builder updateBuilder = MethodSpec
                .methodBuilder("update" + item.getTableName())
                .addJavadoc("更新对象\n")
                .addAnnotation(AnnotationSpecUtils.override())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                    className("entity", item.getTableName()),
                    item.getTableName().toLowerCase()
                )
                .addStatement(
                    "$T $LObject = $L.findById($L.getId()).orElseThrow(() -> new Exception($S))",
                    className("entity", item.getTableName()),
                    item.getTableName(),
                    repositoryFieldName,
                    item.getTableName(),
                    "对象不存在"
                )
                .addException(Exception.class)

                .returns(
                    className("entity", item.getTableName())
                );

            columnsRepository.fetchAll(databaseName, item.getTableName()).forEach(column -> {
                if (!"id".equals(column.getColumnName())) {
                    updateBuilder.addStatement(
                        "$LObject.set$L($L.get$L())",
                        item.getTableName(),
                        CaseUtils.toCamelCase(column.getColumnName(), true, '_'),
                        item.getTableName(),
                        CaseUtils.toCamelCase(column.getColumnName(), true, '_')
                    );
                }

            });
            updateBuilder.addStatement("return $LObject", item.getTableName());
            MethodSpec updateObjectMethod = updateBuilder.build();

            // 创建方法
            MethodSpec.Builder createBuilder = MethodSpec
                .methodBuilder("create" + entityName)
                .addJavadoc("创建对象\n")
                .addAnnotation(AnnotationSpecUtils.override())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                    className("entity", item.getTableName()),
                    item.getTableName().toLowerCase()
                )
                .addStatement(
                    "$T $LObject = new $T()",
                    className("entity", item.getTableName()),
                    item.getTableName(),
                    className("entity", item.getTableName())
                )
                .returns(
                    className("entity", item.getTableName())
                );

            columnsRepository.fetchAll(databaseName, item.getTableName()).forEach(column -> {
                if (!"id".equals(column.getColumnName())) {
                    createBuilder.addStatement(
                        "$LObject.set$L($L.get$L())",
                        item.getTableName(),
                        CaseUtils.toCamelCase(column.getColumnName(), true, '_'),
                        item.getTableName(),
                        CaseUtils.toCamelCase(column.getColumnName(), true, '_')
                    );
                }
            });
            createBuilder.addStatement("return $LObject", item.getTableName());
            MethodSpec createObjectMethod = createBuilder.build();

            // 服务接口
            TypeSpec typeSpec1 = TypeSpec
                .classBuilder(entityName + "ServiceImpl")
                .addJavadoc("Generated by javapoet\n")
                .addSuperinterface(
                    className("service", item.getTableName() + "_service")
                )
                .addField(
                    FieldSpec.builder(
                        className("repository", item.getTableName() + "_repository"),
                        repositoryFieldName,
                        Modifier.PRIVATE
                    ).build()
                )
                .addMethod(
                    MethodSpec
                        .methodBuilder("set" + repositoryFieldName)
                        .addAnnotation(AnnotationSpecUtils.autowired())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(repositoryClass, repositoryFieldName)
                        .addStatement("this.$L = $L", repositoryFieldName, repositoryFieldName)
                        .build()
                )
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(createStereotypeAnnotation("Service"))
                .addMethod(
                    MethodSpec.methodBuilder("get" + entityName)
                        .addJavadoc("通过ID获取用户对象\n")
                        .addAnnotation(AnnotationSpecUtils.override())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Long.class, "id")
//                        .addStatement(entityName + " " + item.getTableName() + " = new " + entityName + "()")
                        .addStatement("$T $L = new $T()",
                            className("entity", item.getTableName()),
                            item.getTableName(),
                            className("entity", item.getTableName())

                        )
                        .addStatement("return " + item.getTableName())
                        .returns(
                            className("entity", item.getTableName())
                        )
                        .build()
                )
                .addMethod(updateObjectMethod)
                .addMethod(createObjectMethod)
                .addMethod(
                    MethodSpec
                        .methodBuilder("paginate" + entityName + "s")
                        .addJavadoc("对象分页列表\n")
                        .addAnnotation(AnnotationSpecUtils.override())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Integer.class, "page")
                        .addParameter(Integer.class, "size")
                        .addParameter(String.class, "sort")
                        .addParameter(String[].class, "sortby")
                        .addStatement("return $L.findAll()", repositoryFieldName)
                        .returns(
                            ParameterizedTypeName.get(
                                ClassName.get("java.util", "List"),
                                ClassName.get(String.format("%s.%s", packageName, "entity"), entityName)
                            )
                        ).build()
                )
                .build();
            JavaFile javaFile1 = JavaFile.builder(String.format("%s.service.impl", packageName), typeSpec1)
                .indent(INDENT)
                .build();

            try {
                javaFile1.writeTo(resourcesDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateRepositories() {
        baseTables.forEach(item -> {
            String entityName = CaseUtils.toCamelCase(item.getTableName(), true, '_');

            log.info("===============> entityName: {}", entityName);
            // 服务接口
            TypeSpec typeSpec1 = TypeSpec
                .interfaceBuilder(entityName + "Repository")
                .addJavadoc("Generated by javapoet\n")
                .addAnnotation(createStereotypeAnnotation("Repository"))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(
                    JpaRepository.class,
                    className("entity", item.getTableName()),
                    Long.class
                ))
                .addSuperinterface(ParameterizedTypeName.get(
                    JpaSpecificationExecutor.class,
                    className("entity", item.getTableName())
                ))
                .build();
            JavaFile javaFile1 = JavaFile.builder(String.format("%s.repository", packageName), typeSpec1)
                .indent(INDENT)
                .build();
            try {
                javaFile1.writeTo(resourcesDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateControllers() {
        log.info(">>> 生成控制器...");
        baseTables.forEach(item -> {
            String entityName = CaseUtils.toCamelCase(item.getTableName(), true, '_');
            log.info("Controller name: {}", entityName + "Controller");
            String repositoryFieldName = CaseUtils.toCamelCase(
                String.format("%s_repository", item.getTableName()), false, '_'
            );
            // 获取主键名称
            log.info("当前数据库: {}, 表: {}", databaseName, item.getTableName());
            List<VColumns> primaryKeys = columnsRepository.getPrimaryKeyNames(databaseName, item.getTableName());
            log.info("主键: {}", primaryKeys);

            if (primaryKeys.size() == 0) {
                log.error("当前数据库表 {} 没有主键, 终止生成...");
                return;
            }

            Class<?> repositoryClass =
                className("repository", item.getTableName() + "_repository");

            // 服务接口
            TypeSpec.Builder controllerBuilder = TypeSpec
                .classBuilder(entityName + "Controller")
                .addJavadoc("Generated by javapoet\n")
                .addAnnotation(AnnotationSpecUtils.restController())
                .addAnnotation(AnnotationSpecUtils.controllerMapping("RequestMapping", "/" + item.getTableName()))
                .addField(
                    FieldSpec.builder(
                        repositoryClass,
                        repositoryFieldName,
                        Modifier.PRIVATE
                    ).build()
                )
                .addMethod(
                    MethodSpec
                        .methodBuilder("set" + StringUtils.capitalize(repositoryFieldName))
                        .addAnnotation(AnnotationSpecUtils.autowired())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(repositoryClass, repositoryFieldName)
                        .addStatement("this.$L = $L", repositoryFieldName, repositoryFieldName)
                        .build()
                )
                .addModifiers(Modifier.PUBLIC);

            // create$T
            MethodSpec.Builder createBuilder = MethodSpec.methodBuilder("create" + toCamelCase(entityName))
                .addAnnotation(AnnotationSpecUtils.controllerMapping("PostMapping", null))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                    className("entity", item.getTableName()), toCamelCaseFirstLower(item.getTableName()) + "Dto"

                )
                .returns(
                    ParameterizedTypeName.get(
                        ClassName.get("reactor.core.publisher", "Mono"),
                        ClassName.get(String.format("%s.%s", packageName, "entity"), entityName)
                    )
                );
            createBuilder
                .addStatement(
                    "$T $L = new $T()",
                    className("entity", item.getTableName()),
                    toCamelCaseFirstLower(item.getTableName()),
                    className("entity", item.getTableName())
                );
            columnsRepository.fetchAll(databaseName, item.getTableName()).forEach(column -> {
                if (!"id".equals(column.getColumnName())) {
                    createBuilder.addStatement(
                        "$L.set$L($L.get$L())",
                        toCamelCaseFirstLower(item.getTableName()),
                        toCamelCase(column.getColumnName()),
                        toCamelCaseFirstLower(item.getTableName()) + "Dto",
                        toCamelCase(column.getColumnName())
                    );
                }
            });
            createBuilder
                .addStatement("$T saved = $L.save($L)",
                    className("entity", item.getTableName()),
                    repositoryFieldName,
                    toCamelCaseFirstLower(item.getTableName())
                )
                .addStatement("return $T.just($L)",
                    ClassName.get("reactor.core.publisher", "Mono"),
                    "saved"
                );
            MethodSpec createMethodSpec = createBuilder.build();
            // 创建
            controllerBuilder.addMethod(createMethodSpec);

            // delete$T
            try {
                controllerBuilder.addMethod(
                    MethodSpec.methodBuilder("delete" + toCamelCase(item.getTableName()))
                        .addAnnotation(AnnotationSpecUtils.controllerMapping("DeleteMapping", "/{id}"))
                        .addAnnotation(
                            AnnotationSpec.builder(ClassName.get("org.springframework.transaction.annotation", "Transactional"))
                                .addMember(
                                    "propagation",
                                    CodeBlock.builder().add("$T.REQUIRED", Class.forName("org.springframework.transaction.annotation.Propagation")).build()
                                )
                                .build()
                        )
                        .addModifiers(Modifier.PUBLIC)
                        .returns(
                            ParameterizedTypeName.get(
                                ClassName.get("reactor.core.publisher", "Mono"),
                                ClassName.get(String.format("%s.%s", packageName, "entity"), entityName)
                            )
                        )
                        .addParameter(
                            ParameterSpec.builder(Long.class, "id")
                                .addAnnotation(AnnotationSpecUtils.pathVariable())
                                .build()
                        )
                        .addException(Exception.class)
                        .addStatement(
                            "$T $L = $L.findById(id).orElseThrow(() -> new Exception($S))",
                            className("entity", item.getTableName()),
                            toCamelCaseFirstLower(item.getTableName()),
                            repositoryFieldName,
                            "对象不存在"
                        )
                        .addStatement(
                            "$L.delete($L)",
                            repositoryFieldName,
                            toCamelCaseFirstLower(item.getTableName())
                        )
                        .addStatement("return $T.just($L)",
                            ClassName.get("reactor.core.publisher", "Mono"),
                            toCamelCaseFirstLower(item.getTableName())
                        )
                        .build()
                );
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            // JSON更新(API)

            MethodSpec.Builder updateBuilder = MethodSpec.methodBuilder("update" + StringUtils.capitalize(entityName))
                .addJavadoc("API更新方法\n")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpecUtils.controllerMapping("PutMapping", null))
                .returns(
                    ParameterizedTypeName.get(
                        ClassName.get("reactor.core.publisher", "Mono"),
                        ClassName.get(String.format("%s.%s", packageName, "entity"), entityName)
                    )
                )
                .addParameter(
                    className("entity", item.getTableName()),
                    toCamelCaseFirstLower(item.getTableName()) + "Dto"
                )
                .addException(Exception.class)
                .addStatement(
                    "$T $L = $L.findById($L).orElseThrow(() -> new Exception($S))",
                    className("entity", item.getTableName()),
                    toCamelCaseFirstLower(item.getTableName()),
                    repositoryFieldName,
                    toCamelCaseFirstLower(item.getTableName()) + "Dto.getId()",
                    "要更新的对象不存在或者已经被删除"
                );
            columnsRepository.fetchAll(databaseName, item.getTableName()).forEach(column -> {
                if (!"id".equals(column.getColumnName())) {
                    updateBuilder.addStatement(
                        "$L.set$L($L.get$L())",
                        toCamelCaseFirstLower(item.getTableName()),
                        toCamelCase(column.getColumnName()),
                        toCamelCaseFirstLower(item.getTableName()) + "Dto",
                        toCamelCase(column.getColumnName())
                    );
                }
            });
            updateBuilder.addStatement("$T saved = $L.save($L)",
                className("entity", item.getTableName()),
                repositoryFieldName,
                toCamelCaseFirstLower(item.getTableName())
            );
            updateBuilder.addStatement("return $T.just($L)",
                ClassName.get("reactor.core.publisher", "Mono"),
                "saved"
            );
            controllerBuilder.addMethod(updateBuilder.build());
            // 表单更新
            controllerBuilder.addMethod(
                MethodSpec.methodBuilder("update" + StringUtils.capitalize(entityName))
                    .addJavadoc("HTML表单更新方法\n")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(AnnotationSpecUtils.controllerMapping("PutMapping", "/{id}"))
                    .addParameter(
                        ParameterSpec.builder(Long.class, "id")
                            .addAnnotation(AnnotationSpecUtils.pathVariable())
                            .build()
                    )
                    .build()
            );
            // 获取
            controllerBuilder.addMethod(
                MethodSpec.methodBuilder("get" + StringUtils.capitalize(entityName))
                    .addAnnotation(AnnotationSpecUtils.controllerMapping("GetMapping", "/{id}"))
                    .build()
            );
            TypeSpec typeSpec = controllerBuilder.build();
            JavaFile javaFile1 = JavaFile.builder(String.format("%s.controller", packageName), typeSpec)
                .indent(INDENT)
                .build();
            try {
                javaFile1.writeTo(resourcesDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Get base table from database
     */
    private void getBaseTables() {
        baseTables = tablesRepository.fetchAll(databaseName, TableType.BASE_TABLE);
    }

    private Class<?> className(String subPackageName, String tableName) {
        Class<?> class1 = null;
        try {
            String className = String.format("%s.%s.%s", packageName, subPackageName,
                CaseUtils.toCamelCase(tableName, true, '_')
            );
            class1 = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return class1;
    }

    private AnnotationSpec createStereotypeAnnotation(String annotationName) {
        return AnnotationSpec.builder(ClassName.get("org.springframework.stereotype", annotationName)).build();
    }

    /**
     * Lambda 表达式
     *
     * @return {@link CodeBlock}
     */
    private CodeBlock generateLambda() {
        return CodeBlock
            .builder()
            .addStatement("$T<$T> names = new $T<>()", List.class, String.class, ArrayList.class)
            .addStatement("$T.range($L, $L).forEach(i -> names.add(name))", IntStream.class, 0, 10)
            .addStatement("names.forEach(System.out::println)")
            .build();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private String toCamelCase(String name) {
        return CaseUtils.toCamelCase(name, true, '_');
    }

    private String toCamelCaseFirstLower(String name) {
        return CaseUtils.toCamelCase(name, false, '_');
    }
}
