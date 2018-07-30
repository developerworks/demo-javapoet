package com.example.demojavapoet;

import com.example.demojavapoet.entity.VTables;
import com.example.demojavapoet.repository.ColumnsRepository;
import com.example.demojavapoet.repository.TablesRepository;
import com.squareup.javapoet.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@SpringBootApplication
@Slf4j
public class DemoJavapoetApplication {

    private static final String INDENT = "    ";
    private static final File resourcesDirectory = new File("src/generated-sources/java");
    private static final String packageName = "com.example.generated";
    private static final String databaseName = "gold_game";

    private Map<String, String> dataTypes = new HashMap<>();

    private TablesRepository tablesRepository;
    private ColumnsRepository columnsRepository;
    private List<VTables> baseTables;

    @Autowired
    public void setTablesRepository(TablesRepository tablesRepository) {
        this.tablesRepository = tablesRepository;
    }

    @Autowired
    public void setColumnsRepository(ColumnsRepository columnsRepository) {
        this.columnsRepository = columnsRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoJavapoetApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner() {

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
        };
    }

    private void generateEntities() {
        baseTables.forEach(item -> {
            // 实体注解
            List<AnnotationSpec> annotationSpecList = createEntityAnnotations();
            // 实体字段
            List<FieldSpec> fieldSpecs = new ArrayList<>();
            // 填充字段
            columnsRepository.fetchAll(databaseName, item.getTableName()).forEach(column -> {
                FieldSpec fieldSpec = null;
                try {
                    if ("id".equals(column.getColumnName())) {
                        AnnotationSpec id = AnnotationSpec.builder(ClassName.get("javax.persistence", "Id")).build();
                        AnnotationSpec generatedValue = AnnotationSpec
                            .builder(ClassName.get("javax.persistence", "GeneratedValue"))
//                            .addMember("strategy", "$T.$L", Class.forName("javax.persistence.GenerationType"), "IDENTITY")
                            .addMember(
                                "strategy",
                                CodeBlock.builder().add("$T.IDENTITY", Class.forName("javax.persistence.GenerationType")).build()
                            )
                            .build();

                        fieldSpec = FieldSpec
                            .builder(
                                Class.forName(dataTypes.get(column.getDataType())),
                                CaseUtils.toCamelCase(column.getColumnName(), false, '_'),
                                Modifier.PRIVATE
                            )
                            .addAnnotation(id)
                            .addAnnotation(generatedValue)
                            .addAnnotation(createJpaColumnAnnotation(column.getColumnName()))
                            .build();
                    } else {
//                        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}", column.getDataType());
                        fieldSpec = FieldSpec
                            .builder(
                                Class.forName(dataTypes.get(column.getDataType())),
                                CaseUtils.toCamelCase(column.getColumnName(), false, '_'),
                                Modifier.PRIVATE
                            )
                            .addAnnotation(createJpaColumnAnnotation(column.getColumnName()))
                            .build();
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                fieldSpecs.add(fieldSpec);
            });
            // JPA实体
            // TODO: Use google case formatter to convert snake case to camel case
            TypeSpec typeSpec = TypeSpec.classBuilder(
                CaseUtils.toCamelCase(item.getTableName(), true, '_')
            )
                .addJavadoc("Generated by javapeot\n")
                .addAnnotations(annotationSpecList)
                .addAnnotation(createJpaTableAnnotation(item.getTableName()))
                .addModifiers(Modifier.PUBLIC)
                .addFields(fieldSpecs)
                .build();
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
                .addAnnotation(annotationOverride())
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
                .addAnnotation(annotationOverride())
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
                        .addAnnotation(annotationAutowired())
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
                        .addAnnotation(annotationOverride())
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
                        .addAnnotation(annotationOverride())
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

    /**
     * Get base table from database
     */
    private void getBaseTables() {
        baseTables = tablesRepository.fetchAll(databaseName, "BASE TABLE");
    }

    private Class<?> className(String subPackageName, String tableName) {
        log.info("<<<<<<<<<<<<<<<< tableName: {}", tableName);
        Class<?> class1 = null;
        try {
            String className = String.format("%s.%s.%s", packageName, subPackageName,
                CaseUtils.toCamelCase(tableName, true, '_')
            );
            log.info(">>>>>>> Class.forName {}", className);
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

    private AnnotationSpec createJpaEntityAnnotation() {
        return AnnotationSpec.builder(ClassName.get("javax.persistence", "Entity")).build();
    }

    private AnnotationSpec createLombokAnnotation(String simpleName) {
        return AnnotationSpec.builder(ClassName.get("lombok", "")).build();
    }

    private List<AnnotationSpec> createEntityAnnotations() {
        List<AnnotationSpec> annotationSpecList = new ArrayList<>();
        annotationSpecList.add(createJpaEntityAnnotation());
        annotationSpecList.add(createLombokAnnotation("Data"));
        annotationSpecList.add(createLombokAnnotation("NoArgsConstructor"));
        annotationSpecList.add(createLombokAnnotation("AllArgsConstructor"));

        return annotationSpecList;
    }

    /**
     * Jpa实体 @Table 注解
     *
     * @param tableName 表名称
     * @return {@link AnnotationSpec}
     */
    private AnnotationSpec createJpaTableAnnotation(String tableName) {
        return AnnotationSpec.builder(ClassName.get("javax.persistence", "Table"))
            .addMember(
                "name",
                CodeBlock.builder().add("$S", tableName).build()
            )
            .build();
    }

    private AnnotationSpec createJpaColumnAnnotation(String columnName) {
        return AnnotationSpec.builder(ClassName.get("javax.persistence", "Column"))
//            .addMember("name", "$S", columnName)
            .addMember(
                "name",
                CodeBlock.builder().add("$S", columnName).build()
            )
            .build();
    }

    private AnnotationSpec annotationAutowired() {
        return AnnotationSpec.builder(ClassName.get("org.springframework.beans.factory.annotation", "Autowired"))
            .build();
    }

    private AnnotationSpec annotationOverride() {
        return AnnotationSpec.builder(Override.class).build();
    }
}
