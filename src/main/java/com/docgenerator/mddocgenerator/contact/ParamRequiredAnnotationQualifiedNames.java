package com.docgenerator.mddocgenerator.contact;

import com.docgenerator.mddocgenerator.utils.MyPsiSupport;
import com.intellij.psi.PsiAnnotation;

import java.util.HashSet;

public enum ParamRequiredAnnotationQualifiedNames {

    NOT_NULL("javax.validation.constraints.NotNull"),
    NOT_EMPTY("javax.validation.constraints.NotEmpty"),
    NOT_BLANK("javax.validation.constraints.NotBlank"),
    REQUEST_PARAM("org.springframework.web.bind.annotation.RequestParam");

    private final String qualifiedName;

    public static final HashSet<String> qualifiedNames = new HashSet<>();

    static {
        ParamRequiredAnnotationQualifiedNames[] values = values();
        for (ParamRequiredAnnotationQualifiedNames value : values) {
            qualifiedNames.add(value.qualifiedName);
        }
    }

    ParamRequiredAnnotationQualifiedNames(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public static ParamRequiredAnnotationQualifiedNames byQualifiedName(String qualifiedName) {
        for (ParamRequiredAnnotationQualifiedNames value : values()) {
            if (value.qualifiedName.equals(qualifiedName)) {
                return value;
            }
        }
        return null;
    }

    public static boolean required(PsiAnnotation[] psiAnnotations) {
        for (PsiAnnotation annotation : psiAnnotations) {
            String annotationQualifiedName = annotation.getQualifiedName();
            if (qualifiedNames.contains(annotationQualifiedName)) {
                ParamRequiredAnnotationQualifiedNames t = byQualifiedName(annotationQualifiedName);
                if (t == null) return true;
                switch (t) {
                    case REQUEST_PARAM: {
                        String required = MyPsiSupport.getPsiAnnotationValueByAttr(annotation, "required");
                        if (required == null) {
                            return true;
                        } else return Boolean.parseBoolean(required);
                    }
                }
                return true;
            }
        }
        return false;
    }
}
