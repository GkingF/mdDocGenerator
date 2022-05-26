package com.docgenerator.mddocgenerator.checker.impl;

import com.docgenerator.mddocgenerator.checker.EventChecker;
import com.docgenerator.mddocgenerator.utils.MyPsiSupport;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;

public class SpringControllerChecker implements EventChecker {

    @Override
    public boolean check(AnActionEvent event) {
        PsiClass[] psiClasses = MyPsiSupport.getPsiClass(event);
        if (psiClasses == null) {
            return false;
        }
        boolean hasEntityAnnotation = false;
        for (PsiClass pClass : psiClasses) {
            for (PsiAnnotation psiAnnotation : pClass.getAnnotations()) {
                if (psiAnnotation.getQualifiedName().equals("org.springframework.web.bind.annotation.RestController")) {
                    hasEntityAnnotation = true;
                }
            }
        }
        return hasEntityAnnotation;
    }
}
