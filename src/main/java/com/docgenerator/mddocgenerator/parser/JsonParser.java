package com.docgenerator.mddocgenerator.parser;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.docgenerator.mddocgenerator.parser.translator.TypeInstanceTranslator;
import com.docgenerator.mddocgenerator.parser.translator.TypeTranslator;
import com.docgenerator.mddocgenerator.utils.Convertor;
import com.docgenerator.mddocgenerator.utils.MyPsiSupport;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonParser extends Parser {

    private PsiClass psiClass;
    private final Integer layer;
    private final PsiType psiType;


    public JsonParser(PsiType psiType, Integer layer) {
        this.psiType = psiType;
        this.psiClass = MyPsiSupport.getPsiClass(psiType);
        this.layer = layer;
    }

    @Override
    public String parseDefinition() {
        JSONObject obj = JSONUtil.createObj();
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
                String s = genericsType.toString();
                PsiClass genericsClass = MyPsiSupport.getPsiClass(genericsType);
                psiFieldList = this.getAvailablePsiField(genericsClass, genericsClass.getAllFields());
            }
        } else {
            for (PsiField psiField : psiClass.getAllFields()) {
                if (MyPsiSupport.findPsiMethod(psiClass, Convertor.getFieldGetterName(psiField.getName())) != null) {
                    psiFieldList.add(psiField);
                }
                if (MyPsiSupport.findPsiMethod(psiClass, Convertor.getFieldBoolGetterName(psiField.getName())) != null) {
                    psiFieldList.add(psiField);
                }
            }
        }
        if (psiFieldList.isEmpty()) {
            return type;
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
                MapParser mapParser = new MapParser(fieldType, 1);
                Map map = mapParser.generateMap();
//                Map map = generateMap(fieldType, 1);
                obj.put(psiField.getName(), map);

            } else if ("List".equals(flag)) {
                obj.put(psiField.getName(), new ArrayList<>());
            } else {
                Object translate = TypeInstanceTranslator.translate(psiField.getType().getCanonicalText());
                obj.put(psiField.getName(), translate);
            }

        }
        return obj.toStringPretty();


//            FieldDefinition definition = new FieldDefinition();
//            String dec = JavaDocUtils.getText(psiField.getDocComment());
//            String name = psiField.getName();
//
//            boolean require = MyPsiSupport.getPsiAnnotation(psiField, MyContact.VALIDATOR_NOTEMPTYCHECK) != null;
//            if (!require) {
//                require = MyPsiSupport.getPsiAnnotation(psiField, CommonContact.CONSTRAINTS_NOTNULL) != null;
//            }
//            definition.setLayer(layer);
//            definition.setName(name);
//            definition.setDesc(dec);
//            definition.setRequire(require);
//
//
//            if (definition.getType().equals(TypeTranslator.TYPE_OBJ)) {
//                PsiClass psiClass = MyPsiSupport.getPsiClass(fieldType);
//                if (psiClass != null) {
//                    JsonParser objectParser = new JsonParser(fieldType, layer + 1);
//                    objectParser.parseDefinition();
//                    definition.setSubFieldDefinitions(objectParser.getFieldDefinitions());
//                }
//            } else if (definition.getType().equals(TypeTranslator.TYPE_LIST)) {
//                PsiType genericsType = PsiUtil.extractIterableTypeParameter(psiField.getType(), true);
//                if (genericsType == null) {
//                    genericsType = PsiUtil.extractIterableTypeParameter(fieldType, true);
//                }
//                PsiType listGenericsType = MyPsiSupport.getGenericsType(this.psiType, genericsType.getCanonicalText());
//                if (listGenericsType != null) {
//                    genericsType = listGenericsType;
//                }
//                psiClass = MyPsiSupport.getPsiClass(genericsType);
//                if (psiClass != null) {
//                    JsonParser objectParser = new JsonParser(genericsType, layer + 1);
//                    objectParser.parseDefinition();
//                    definition.setSubFieldDefinitions(objectParser.getFieldDefinitions());
//                }
//            }
//        }
//        return type;
    }


//    public Map generateMap(PsiType psiType, Integer layer){
//        if (layer >= 3) return null;
//        HashMap<String, Object> obj = new HashMap<>();
//        String type = null;
//        if (psiClass == null) {
//            type = TypeTranslator.docTypeTranslate(this.psiType.getCanonicalText());
//        } else {
//            type = TypeTranslator.docTypeTranslate(this.psiClass.getQualifiedName());
//        }
//        List<PsiField> psiFieldList = new ArrayList<>();
//        if (TypeTranslator.TYPE_LIST.equals(type)) {
//            PsiType genericsType = MyPsiSupport.getGenericsType(psiType, 0);
//            if (genericsType != null) {
//                PsiClass genericsClass = MyPsiSupport.getPsiClass(genericsType);
//                psiFieldList = this.getAvailablePsiField(genericsClass, genericsClass.getAllFields());
//            }
//        } else {
//            for (PsiField psiField : psiClass.getAllFields()) {
//                if (MyPsiSupport.findPsiMethod(psiClass, Convertor.getFieldGetterName(psiField.getName())) != null) {
//                    psiFieldList.add(psiField);
//                }
//                if (MyPsiSupport.findPsiMethod(psiClass, Convertor.getFieldBoolGetterName(psiField.getName())) != null) {
//                    psiFieldList.add(psiField);
//                }
//            }
//        }
//        if (psiFieldList.isEmpty()) {
//            return obj;
//        }
//        for (PsiField psiField : psiFieldList) {
//            PsiType fieldType = getRealType(this.psiType, psiField);
//            PsiClass fieldClass = MyPsiSupport.getPsiClass(fieldType);
//            String flag = "";
//            if (fieldClass == null) {
//                flag = TypeTranslator.docTypeTranslate(fieldType.getCanonicalText());
//            } else {
//                flag = TypeTranslator.docTypeTranslate(fieldClass.getQualifiedName());
//            }
//            if ("Object".equals(flag)) {
//                Map map = generateMap(fieldType, layer+1);
//                obj.put(psiField.getName(),map);
//            } else if ("List".equals(flag)) {
//
//            } else {
//                Object translate = TypeInstanceTranslator.translate(psiField.getType().getCanonicalText());
//                obj.put(psiField.getName(), translate);
//            }
//
//        }
//        return obj;
//    }

    /**
     * Filter fields without Getter methods
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


//
//    /**
//     * 单个字段递归解析
//     *
//     * @param psiField
//     * @return
//     */
//    public FieldDefinition parseSingleFieldDefinition(PsiField psiField) {
//        FieldDefinition definition = new FieldDefinition();
//        String dec = JavaDocUtils.getText(psiField.getDocComment());
//        String name = psiField.getName();
//
//        boolean require = MyPsiSupport.getPsiAnnotation(psiField, MyContact.VALIDATOR_NOTEMPTYCHECK) != null;
//        if (!require) {
//            require = MyPsiSupport.getPsiAnnotation(psiField, CommonContact.CONSTRAINTS_NOTNULL) != null;
//        }
//        definition.setLayer(layer);
//        definition.setName(name);
//        definition.setDesc(dec);
//        definition.setRequire(require);
//        PsiType fieldType = getRealType(this.psiType, psiField);
//        PsiClass fieldClass = MyPsiSupport.getPsiClass(fieldType);
//        if (fieldClass == null) {
//            definition.setType(TypeTranslator.docTypeTranslate(fieldType.getCanonicalText()));
//        } else {
//            definition.setType(TypeTranslator.docTypeTranslate(fieldClass.getQualifiedName()));
//        }
//
//        if (definition.getType().equals(TypeTranslator.TYPE_OBJ)) {
//            PsiClass psiClass = MyPsiSupport.getPsiClass(fieldType);
//            if (psiClass != null) {
//                JsonParser objectParser = new JsonParser(fieldType, layer + 1);
//                objectParser.parseDefinition();
//                definition.setSubFieldDefinitions(objectParser.getFieldDefinitions());
//            }
//        } else if (definition.getType().equals(TypeTranslator.TYPE_LIST)) {
//            PsiType genericsType = PsiUtil.extractIterableTypeParameter(psiField.getType(), true);
//            if (genericsType == null) {
//                genericsType = PsiUtil.extractIterableTypeParameter(fieldType, true);
//            }
//            PsiType listGenericsType = MyPsiSupport.getGenericsType(this.psiType, genericsType.getCanonicalText());
//            if (listGenericsType != null) {
//                genericsType = listGenericsType;
//            }
//            psiClass = MyPsiSupport.getPsiClass(genericsType);
//            if (psiClass != null) {
//                JsonParser objectParser = new JsonParser(genericsType, layer + 1);
//                objectParser.parseDefinition();
//                definition.setSubFieldDefinitions(objectParser.getFieldDefinitions());
//            }
//        }
//        return definition;
//    }


}
