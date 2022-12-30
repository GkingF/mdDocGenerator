package com.docgenerator.mddocgenerator.checker.impl;

import com.docgenerator.mddocgenerator.checker.EventChecker;
import com.docgenerator.mddocgenerator.utils.MyPsiSupport;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiJavaFile;

public class JavaFileChecker implements EventChecker {
    @Override
    public boolean check(AnActionEvent event) {
        //If it is not a JAVA type, it will not be displayed
        PsiJavaFile javaFile = MyPsiSupport.getPsiJavaFile(event);
        return javaFile != null;
    }
}
