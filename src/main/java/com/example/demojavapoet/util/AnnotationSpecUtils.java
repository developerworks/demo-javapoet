package com.example.demojavapoet.util;

import com.example.demojavapoet.DemoJavapoetApplication;
import com.example.demojavapoet.entity.VColumns;
import com.squareup.javapoet.*;
import org.apache.commons.text.CaseUtils;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnnotationSpecUtils {
    public static AnnotationSpec pathVariable() {
        return AnnotationSpec
            .builder(ClassName.get("org.springframework.web.bind.annotation", "PathVariable"))
            .build();
    }

    public static AnnotationSpec createJpaEntityAnnotation() {
        return AnnotationSpec.builder(ClassName.get("javax.persistence", "Entity")).build();
    }

    public static AnnotationSpec lombokAnnotation(String simpleName) {
        return AnnotationSpec.builder(ClassName.get("lombok", simpleName)).build();
    }

    public static List<AnnotationSpec> entityAnnotations() {
        List<AnnotationSpec> annotationSpecList = new ArrayList<>();
        annotationSpecList.add(createJpaEntityAnnotation());
        annotationSpecList.add(lombokAnnotation("Data"));
        annotationSpecList.add(lombokAnnotation("NoArgsConstructor"));
        annotationSpecList.add(lombokAnnotation("AllArgsConstructor"));

        return annotationSpecList;
    }

    /**
     * Jpa实体 @Table 注解
     *
     * @param tableName 表名称
     * @return {@link AnnotationSpec}
     */
    public static AnnotationSpec jpaTableAnnotation(String tableName) {
        return AnnotationSpec.builder(ClassName.get("javax.persistence", "Table"))
            .addMember(
                "name",
                CodeBlock.builder().add("$S", tableName).build()
            )
            .build();
    }

    public static AnnotationSpec jpaColumnAnnotation(String columnName) {
        return AnnotationSpec.builder(ClassName.get("javax.persistence", "Column"))
            .addMember(
                "name",
                CodeBlock.builder().add("$S", columnName).build()
            )
            .build();
    }

    public static AnnotationSpec autowired() {
        return AnnotationSpec
            .builder(ClassName.get("org.springframework.beans.factory.annotation", "Autowired"))
            .build();
    }

    public static AnnotationSpec override() {
        return AnnotationSpec.builder(Override.class).build();
    }

    public static AnnotationSpec restController() {
        return AnnotationSpec
            .builder(ClassName.get("org.springframework.web.bind.annotation", "RestController"))
            .build();
    }

    public static AnnotationSpec controllerMapping(String simpleName, String path) {
        AnnotationSpec.Builder builder = AnnotationSpec
            .builder(ClassName.get("org.springframework.web.bind.annotation", simpleName));
        if (null != path) {
            builder.addMember("value", "$S", path);
        }
        return builder.build();
    }

    public static AnnotationSpec idClass(String pkClassName) {
        return AnnotationSpec
            .builder(ClassName.get("javax.persistence", "IdClass"))
            .addMember("value", "$L", pkClassName)
            .build();
    }

    public static void createIdClass(String className, List<VColumns> columns) {
        // IdClass 字段集合
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        columns.forEach(column -> {
            FieldSpec fieldSpec = null;
            try {
                fieldSpec = FieldSpec.builder(
                    Class.forName(DemoJavapoetApplication.dataTypes.get(column.getDataType())),
                    CaseUtils.toCamelCase(column.getColumnName(), false, '_'),
                    Modifier.PRIVATE
                ).build();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            fieldSpecs.add(fieldSpec);
        });

        List<AnnotationSpec> annotationSpecs = new ArrayList<>();
        String[] annotations = "Getter,EqualsAndHashCode,NoArgsConstructor,AllArgsConstructor".split(",");
        for (String annotation : annotations) {
            annotationSpecs.add(
                AnnotationSpec.builder(ClassName.get("lombok", annotation)).build()
            );
        }

        TypeSpec typeSpec = TypeSpec
            .classBuilder(className)
            .addAnnotations(annotationSpecs)
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(
                ClassName.get("java.io", "Serializable")
            )
            .addFields(fieldSpecs)
            .build();

        JavaFile javaFile = JavaFile.builder(String.format("%s.entity", DemoJavapoetApplication.packageName), typeSpec).indent(DemoJavapoetApplication.INDENT).build();
        try {
            javaFile.writeTo(DemoJavapoetApplication.resourcesDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
