package com.example.demojavapoet;

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
import org.springframework.util.StringUtils;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.*;

@SpringBootApplication
@Slf4j
public class DemoJavapoetApplication {

    private static final String INDENT = "    ";
    private static final File resourcesDirectory = new File("src/generated-sources/java");
    private static final String packageName = "javapoet";

    private Map<String, String> dataTypes = new HashMap<>();


    private TablesRepository tablesRepository;
    private ColumnsRepository columnsRepository;

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


    private List<AnnotationSpec> createEntityAnnotations() {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(ClassName.get("javax.persistence", "Entity")).build();
        AnnotationSpec data = AnnotationSpec.builder(ClassName.get("lombok", "Data")).build();
        AnnotationSpec noArgsConstructor = AnnotationSpec.builder(ClassName.get("lombok", "NoArgsConstructor")).build();
        AnnotationSpec allArgsConstructor = AnnotationSpec.builder(ClassName.get("lombok", "AllArgsConstructor")).build();


        List<AnnotationSpec> annotationSpecList = new ArrayList<>();
        annotationSpecList.add(annotationSpec);
        annotationSpecList.add(data);
        annotationSpecList.add(noArgsConstructor);
        annotationSpecList.add(allArgsConstructor);

        return annotationSpecList;
    }


    @Bean
    CommandLineRunner commandLineRunner() {

        dataTypes.put("bigint", "BigInteger");
        dataTypes.put("datetime", "Timestamp");
        dataTypes.put("timestamp", "Timestamp");
        dataTypes.put("time", "Time");
        dataTypes.put("date", "Date");
        dataTypes.put("varchar", "String");
        dataTypes.put("char", "String");
        dataTypes.put("int", "Integer");
        dataTypes.put("tinyint", "Byte");
        dataTypes.put("smallint", "Short");
        dataTypes.put("decimal", "BigDecimal");
        dataTypes.put("double", "Double");
        dataTypes.put("float", "Float");
        dataTypes.put("binary", "Byte");
        dataTypes.put("bit", "Boolean");
        dataTypes.put("blob", "Blob");

        return args -> {
            generateEntities();
            Thread.sleep(3000);
            generateInterfaces();
            Thread.sleep(3000);
            generateRepositories();
            Thread.sleep(3000);
            generateServiceImpls();
        };
    }

    private void generateEntities() {
        tablesRepository.fetchAll(packageName, "BASE TABLE").forEach(item -> {
            log.info("Table name: {}, type: {}", item.getTableName(), item.getTableType());
            // 实体
            TypeSpec entity = TypeSpec.classBuilder(StringUtils.capitalize(item.getTableName()))
                .addField(
                    FieldSpec.builder(String.class, "username", Modifier.PRIVATE)
                        .addJavadoc("用户名\n")
                        .build()

                ).build();
            // 实体注解
            List<AnnotationSpec> annotationSpecList = createEntityAnnotations();
            // 实体字段
            List<FieldSpec> fieldSpecs = new ArrayList<>();
            columnsRepository.fetchAll(packageName, item.getTableName()).forEach(column -> {
                log.info("- Column name: {}, column type: {}, data type: {} => {}, camelcase column name: {}",
                    column.getColumnName(),
                    column.getColumnType(),
                    column.getDataType(),
                    dataTypes.get(column.getDataType()),
                    CaseUtils.toCamelCase(column.getColumnName(), false, '_')
                );
                FieldSpec fieldSpec = FieldSpec.builder(
                    String.class,
                    CaseUtils.toCamelCase(column.getColumnName(), false, '_'),
                    Modifier.PRIVATE
                ).build();
                fieldSpecs.add(fieldSpec);
            });
            // 实体类
            TypeSpec typeSpec = TypeSpec.classBuilder(StringUtils.capitalize(item.getTableName()))
                .addAnnotations(annotationSpecList)
                .addModifiers(Modifier.PUBLIC)
                .addFields(fieldSpecs)
                .build();
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec).indent(INDENT).build();
            try {
                javaFile.writeTo(resourcesDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateInterfaces() {
        tablesRepository.fetchAll(packageName, "BASE TABLE").forEach(item -> {
            // 服务接口
            TypeSpec typeSpec1 = TypeSpec
                .interfaceBuilder(StringUtils.capitalize(item.getTableName()) + "Service")
                .addMethod(
                    MethodSpec.methodBuilder("get" + StringUtils.capitalize(item.getTableName()))
                        .addJavadoc("通过ID获取用户对象\n")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(Long.class, "id")
                        .returns(
                            getClassByName(item.getTableName())
                        )
                        .build()
                )
                .addMethod(
                    MethodSpec
                        .methodBuilder("update" + StringUtils.capitalize(item.getTableName()))
                        .addJavadoc("更新对象\n")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(
                            getClassByName(item.getTableName()),
                            item.getTableName().toLowerCase()
                        )
                        .returns(
                            getClassByName(item.getTableName())
                        ).build()
                )
                .addMethod(
                    MethodSpec
                        .methodBuilder("create" + StringUtils.capitalize(item.getTableName()))
                        .addJavadoc("创建对象\n")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(
                            getClassByName(item.getTableName()),
                            item.getTableName().toLowerCase()
                        )
                        .returns(
                            getClassByName(item.getTableName())
                        ).build()
                )
                .build();

            JavaFile javaFile1 = JavaFile.builder(packageName, typeSpec1)
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
        tablesRepository.fetchAll(packageName, "BASE TABLE").forEach(item -> {
            // 服务接口
            TypeSpec typeSpec1 = TypeSpec
                .classBuilder(StringUtils.capitalize(item.getTableName()) + "ServiceImpl")
                .addSuperinterface(
                    getClassByName(StringUtils.capitalize(item.getTableName()) + "Service")
                )
                .addAnnotation(createStereotypeAnnotation("Service"))
                .addMethod(
                    MethodSpec.methodBuilder("get" + StringUtils.capitalize(item.getTableName()))
                        .addJavadoc("通过ID获取用户对象\n")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Long.class, "id")
                        .addStatement(StringUtils.capitalize(item.getTableName()) + " " + item.getTableName() + " = new " + StringUtils.capitalize(item.getTableName()) + "()")
                        .addStatement("return " + item.getTableName())
                        .returns(
                            getClassByName(item.getTableName())
                        )
                        .build()
                )
                .addMethod(
                    MethodSpec
                        .methodBuilder("update" + StringUtils.capitalize(item.getTableName()))
                        .addJavadoc("更新对象\n")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(
                            getClassByName(item.getTableName()),
                            item.getTableName().toLowerCase()
                        )
                        .addStatement("return " + item.getTableName())
                        .returns(
                            getClassByName(item.getTableName())
                        ).build()
                )
                .addMethod(
                    MethodSpec
                        .methodBuilder("create" + StringUtils.capitalize(item.getTableName()))
                        .addJavadoc("创建对象\n")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(
                            getClassByName(item.getTableName()),
                            item.getTableName().toLowerCase()
                        )
                        .addStatement("return " + item.getTableName())
                        .returns(
                            getClassByName(item.getTableName())
                        ).build()
                )
                .build();
            JavaFile javaFile1 = JavaFile.builder(packageName, typeSpec1)
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
        tablesRepository.fetchAll(packageName, "BASE TABLE").forEach(item -> {
            // 服务接口
            TypeSpec typeSpec1 = TypeSpec
                .interfaceBuilder(StringUtils.capitalize(item.getTableName()) + "Repository")
                .addSuperinterface(ParameterizedTypeName.get(
                    JpaRepository.class,
                    getClassByName(StringUtils.capitalize(item.getTableName())),
                    Long.class
                ))
                .addSuperinterface(ParameterizedTypeName.get(
                    JpaSpecificationExecutor.class,
                    getClassByName(StringUtils.capitalize(item.getTableName()))
                ))
                .addAnnotation(createStereotypeAnnotation("Repository"))
                .build();
            JavaFile javaFile1 = JavaFile.builder(packageName, typeSpec1)
                .indent(INDENT)
                .build();
            try {
                javaFile1.writeTo(resourcesDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Class<?> getClassByName(String tableName) {
        Class<?> class1 = null;
        try {
            class1 = Class.forName(packageName + "." + StringUtils.capitalize(tableName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return class1;
    }

    private AnnotationSpec createStereotypeAnnotation(String annotationName) {
        return AnnotationSpec.builder(ClassName.get("org.springframework.stereotype", annotationName)).build();
    }
}
