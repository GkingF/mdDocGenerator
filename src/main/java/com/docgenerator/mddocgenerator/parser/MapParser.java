package com.docgenerator.mddocgenerator.parser;

import com.docgenerator.mddocgenerator.parser.translator.TypeInstanceTranslator;
import com.docgenerator.mddocgenerator.parser.translator.TypeTranslator;
import com.docgenerator.mddocgenerator.utils.Convertor;
import com.docgenerator.mddocgenerator.utils.MyPsiSupport;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapParser {

    private PsiClass psiClass;
    private Integer layer;
    private PsiType psiType;

    public MapParser(PsiType psiType, Integer layer) {
        this.psiType = psiType;
        this.psiClass = MyPsiSupport.getPsiClass(psiType);
        this.layer = layer;
    }

    public Map generateMap() {
        if (layer >= 3) return null;
        HashMap<String, Object> obj = new HashMap<>();
        String type = null;
        if (psiClass == null) {
            type = TypeTranslator.docTypeTranslate(this.psiType.getCanonicalText());
        } else {
            type = TypeTranslator.docTypeTranslate(this.psiClass.getQualifiedName());
        }
        List<PsiField> psiFieldList = new ArrayList<>();
        if (TypeTranslator.TYPE_LIST.equals(type)) {
            PsiType genericsType = MyPsiSupport.getGenericsType(psiType, 0);
            if (genericsType != null) {
                PsiClass genericsClass = MyPsiSupport.getPsiClass(genericsType);
                psiFieldList = this.getAvailablePsiField(genericsClass, genericsClass.getAllFields());
            }
        } else {
            if (psiClass != null) {
                for (PsiField psiField : psiClass.getAllFields()) {
                    if (MyPsiSupport.findPsiMethod(psiClass, Convertor.getFieldGetterName(psiField.getName())) != null) {
                        psiFieldList.add(psiField);
                    }
                    if (MyPsiSupport.findPsiMethod(psiClass, Convertor.getFieldBoolGetterName(psiField.getName())) != null) {
                        psiFieldList.add(psiField);
                    }
                }
            }
        }
        if (psiFieldList.isEmpty()) {
            return obj;
        }
        for (PsiField psiField : psiFieldList) {
            PsiType fieldType = getRealType(this.psiType, psiField);
            PsiClass fieldClass = MyPsiSupport.getPsiClass(fieldType);
            String flag = "";
            if (fieldClass == null) {
                flag = TypeTranslator.docTypeTranslate(fieldType.getCanonicalText());
            } else {
                flag = TypeTranslator.docTypeTranslate(fieldClass.getQualifiedName());
            }
            if ("Object".equals(flag)) {
                MapParser mapParser = new MapParser(fieldType, layer + 1);
                Map map = mapParser.generateMap();
                obj.put(psiField.getName(), map);
            } else if ("List".equals(flag)) {

            } else {
                Object translate = TypeInstanceTranslator.translate(psiField.getType().getCanonicalText());
                obj.put(psiField.getName(), translate);
            }

        }
        return obj;
    }

    /**
     * 将没有Getter方法的字段过滤
     *
     * @return
     */
    public List<PsiField> getAvailablePsiField(PsiClass psiClass, PsiField[] psiFields) {
        List<PsiField> psiFieldList = new ArrayList<>();
        for (PsiField psiField : psiFields) {
            if (MyPsiSupport.findPsiMethod(psiClass, Convertor.getFieldGetterName(psiField.getName())) != null) {
                psiFieldList.add(psiField);
            }
            if (MyPsiSupport.findPsiMethod(psiClass, Convertor.getFieldBoolGetterName(psiField.getName())) != null) {
                psiFieldList.add(psiField);
            }
        }
        return psiFieldList;
    }

    public PsiType getRealType(PsiType psiType, PsiField psiField) {
        PsiType fieldType = MyPsiSupport.getGenericsType(psiType, psiField);
        if (fieldType == null) {
            fieldType = psiField.getType();
        }
        return fieldType;
    }
}
