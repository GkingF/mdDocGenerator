package com.docgenerator.mddocgenerator.parser;

import com.docgenerator.mddocgenerator.utils.MyPsiSupport;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

public abstract class Parser {

    abstract public String parseDefinition();

    public PsiType getRealType(PsiType psiType, PsiField psiField) {
        PsiType fieldType = MyPsiSupport.getGenericsType(psiType, psiField);
        if (fieldType == null) {
            fieldType = psiField.getType();
        }
        return fieldType;
    }

}
