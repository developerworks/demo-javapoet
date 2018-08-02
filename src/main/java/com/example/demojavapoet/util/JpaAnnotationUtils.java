package com.example.demojavapoet.util;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

public class JpaAnnotationUtils {
    private static final String PKG_JAVAX_PERSISTENCE = "javax.persistence";

    public static AnnotationSpec idClass(String idClassName) {
        return AnnotationSpec
            .builder(ClassName.get(PKG_JAVAX_PERSISTENCE, "IdClass"))
            .addMember("value", "$L", idClassName)
            .build();
    }

    public static AnnotationSpec entity() {
        return AnnotationSpec
            .builder(ClassName.get(PKG_JAVAX_PERSISTENCE, "Entity"))
            .build();
    }

    public static AnnotationSpec column(String name) {
        return AnnotationSpec
            .builder(ClassName.get(PKG_JAVAX_PERSISTENCE, "Column"))
            .addMember("name", "$L", name)
            .build();
    }

    public static AnnotationSpec id() {
        return AnnotationSpec
            .builder(ClassName.get(PKG_JAVAX_PERSISTENCE, "Id"))
            .build();
    }

    public static AnnotationSpec generatedValue(String strategy) {
        return AnnotationSpec
            .builder(ClassName.get(PKG_JAVAX_PERSISTENCE, "GeneratedValue"))
            .addMember(
                "strategy",
                CodeBlock
                    .builder()
                    .add(
                        "$T.$L",
                        ClassName.get("javax.persistence", "GenerationType"),
                        strategy
                    )
                    .build()
            )
            .build();
    }
}
