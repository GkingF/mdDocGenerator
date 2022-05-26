package com.docgenerator.mddocgenerator.parser.translator;

public class TypeInstanceTranslator {
    public static Object translate(String canonicalText) {
        switch (canonicalText) {
            case "java.lang.Long":
                return 123456;
            case "java.lang.Integer":
            case "java.lang.Shout":
                return 1;
            case "java.lang.Boolean":
                return true;
            case "java.lang.String":
                return "string";
            case "java.lang.Byte":
                return (byte) 1;
            case "java.lang.Character":
                return 'a';
            default:
                return "";
        }
    }
}
