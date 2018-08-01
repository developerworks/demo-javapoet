package com.example.demojavapoet.util;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;

public class LombokAnnotation {
    public static AnnotationSpec setter() {
        return AnnotationSpec
            .builder(ClassName.get("lombok", "Setter"))
            .build();
    }

    public static AnnotationSpec getter() {
        return AnnotationSpec
            .builder(ClassName.get("lombok", "Getter"))
            .build();
    }

    public static AnnotationSpec data() {
        return AnnotationSpec
            .builder(ClassName.get("lombok", "Data"))
            .build();
    }

    public static AnnotationSpec noArgsConstructor() {
        return AnnotationSpec
            .builder(ClassName.get("lombok", "NoArgsConstructor"))
            .build();
    }

    public static AnnotationSpec allArgsConstructor() {
        return AnnotationSpec
            .builder(ClassName.get("lombok", "AllArgsConstructor"))
            .build();
    }
}
