package com.docgenerator.mddocgenerator.parser;

import com.docgenerator.mddocgenerator.contact.ParamRequiredAnnotationQualifiedNames;
import com.docgenerator.mddocgenerator.definition.FieldDefinition;
import com.docgenerator.mddocgenerator.parser.translator.TypeTranslator;
import com.docgenerator.mddocgenerator.utils.Convertor;
import com.docgenerator.mddocgenerator.utils.JavaDocUtils;
import com.docgenerator.mddocgenerator.utils.MyPsiSupport;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;

import java.util.ArrayList;
import java.util.List;

public class ObjectParser extends Parser {

    private PsiClass psiClass;
    private final Integer layer;
    private final PsiType psiType;
    private final Project project;
    private final List<FieldDefinition> fieldDefinitions = new ArrayList<>();


    public ObjectParser(PsiType psiType, Project project, Integer layer) {
        this.psiType = psiType;
        this.project = project;
        this.psiClass = MyPsiSupport.getPsiClass(psiType);
        this.layer = layer;
    }

    @Override
    public String parseDefinition() {
        String type = null;
        if (psiClass == null) {
            type = TypeTranslator.docTypeTranslate(this.psiType.getCanonicalText());
        } else {
            type = TypeTranslator.docTypeTranslate(this.psiClass.getQualifiedName());
        }
        List<PsiField> psiFieldList = new ArrayList<>();
        if (TypeTranslator.TYPE_LIST.equals(type)) {
            PsiType genericsType = MyPsiSupport.getGenericsType(psiType, 0);
            if (genericsType != null && !genericsType.toString().contains("?")) {
                PsiClass genericsClass = MyPsiSupport.getPsiClass(genericsType);
                psiFieldList = this.getAvailablePsiField(genericsClass, genericsClass.getAllFields());
            }
        } else {
            psiFieldList = this.getAvailablePsiField(this.psiClass, psiClass.getAllFields());
        }

        if (psiFieldList.isEmpty()) {
            return type;
        }
        doParse(psiFieldList);
        return type;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public List<FieldDefinition> getFieldDefinitions() {
        return this.fieldDefinitions;
    }

    /**
     * ?????????Getter?????????????????????
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

    /**
     * ??????
     *
     * @param psiFields
     */
    public void doParse(List<PsiField> psiFields) {
        for (PsiField psiField : psiFields) {
            FieldDefinition definition = parseSingleFieldDefinition(psiField);
            this.fieldDefinitions.add(definition);
        }
    }


    /**
     * ????????????????????????
     *
     * @param psiField
     * @return
     */
    public FieldDefinition parseSingleFieldDefinition(PsiField psiField) {
        FieldDefinition definition = new FieldDefinition();
        String dec = JavaDocUtils.getText(psiField.getDocComment());
        String name = psiField.getName();
        /*boolean require = MyPsiSupport.getPsiAnnotation(psiField, MyContact.VALIDATOR_NOTEMPTYCHECK) != null;
        if (!require) {
            require = MyPsiSupport.getPsiAnnotation(psiField, CommonContact.CONSTRAINTS_NOTNULL) != null;
        }*/
        definition.setRequire(ParamRequiredAnnotationQualifiedNames.required(MyPsiSupport.getPsiAnnotations(psiField)));
        definition.setLayer(layer);
        definition.setName(name);
        definition.setDesc(dec);
        PsiType fieldType = getRealType(this.psiType, psiField);
        PsiClass fieldClass = MyPsiSupport.getPsiClass(fieldType);
        if (fieldClass == null) {
            definition.setType(TypeTranslator.docTypeTranslate(fieldType.getCanonicalText()));
        } else {
            definition.setType(TypeTranslator.docTypeTranslate(fieldClass.getQualifiedName()));
        }

        if (definition.getType().equals(TypeTranslator.TYPE_OBJ)) {
            PsiClass psiClass = MyPsiSupport.getPsiClass(fieldType);
            if (psiClass != null) {
                ObjectParser objectParser = new ObjectParser(fieldType, this.project, layer + 1);
                objectParser.parseDefinition();
                definition.setSubFieldDefinitions(objectParser.getFieldDefinitions());
            }
        } else if (definition.getType().equals(TypeTranslator.TYPE_LIST)) {
            PsiType genericsType = PsiUtil.extractIterableTypeParameter(psiField.getType(), true);
            if (genericsType == null) {
                genericsType = PsiUtil.extractIterableTypeParameter(fieldType, true);
            }
            PsiType listGenericsType = MyPsiSupport.getGenericsType(this.psiType, genericsType.getCanonicalText());
            if (listGenericsType != null) {
                genericsType = listGenericsType;
            }
            psiClass = MyPsiSupport.getPsiClass(genericsType);
            if (psiClass != null) {
                ObjectParser objectParser = new ObjectParser(genericsType, this.project, layer + 1);
                objectParser.parseDefinition();
                definition.setSubFieldDefinitions(objectParser.getFieldDefinitions());
            }
        }
        return definition;
    }


}
