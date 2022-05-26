package com.docgenerator.mddocgenerator.checker.impl;

import com.docgenerator.mddocgenerator.checker.EventChecker;
import com.docgenerator.mddocgenerator.utils.MyPsiSupport;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiJavaFile;

public class JavaFileChecker implements EventChecker {
    @Override
    public boolean check(AnActionEvent event) {
        //不是JAVA类型不显示
        PsiJavaFile javaFile = MyPsiSupport.getPsiJavaFile(event);
        return javaFile != null;
    }
}
