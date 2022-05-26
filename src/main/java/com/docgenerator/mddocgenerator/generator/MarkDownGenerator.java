package com.docgenerator.mddocgenerator.generator;

import java.lang.reflect.Field;
import java.util.List;

public class MarkDownGenerator {

    private StringBuilder builder;

    public MarkDownGenerator() {
        this.builder = new StringBuilder();
    }

    public void appendH1(String h1) {
        builder.append(String.format("# %s\n", h1));
    }

    public void appendH2(String h2) {
        builder.append(String.format("## %s\n", h2));
    }

    public void appendH3(String h3) {
        builder.append(String.format("### %s\n", h3));
    }

    public void appendH4(String h4) {
        builder.append(String.format("#### %s\n", h4));
    }

    public void appendH5(String h5) {
        builder.append(String.format("##### %s\n", h5));
    }

    public void appendCode(String type, String code) {
        builder.append(String.format("```%s\n", type));
        builder.append(code);
        builder.append("```\n");
    }

    public void appendTable(List table) {
        Object o = table.get(0);
        Field[] fields = o.getClass().getFields();
        int length = fields.length;
        StringBuffer str = new StringBuffer("| ");
        for (Field field : fields) {
            str.append(field.getName());
            str.append(" | ");
        }
        str.append("\n");


        str.append("| ");
        for (Field field : fields) {
            str.append(" ---- ");
            str.append(" | ");
        }
        str.append("\n");


        table.forEach(e -> {
            str.append("| ");
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    String value = field.get(e).toString();
                    str.append(field.getName());
                    str.append(" | ");
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }
            }
            str.append("\n");
        });
        builder.append(str);
    }

    public void appendBlock(String str) {
        builder.append("> ");
        builder.append(str);
        builder.append("\n");
    }


    public String getText() {
        return builder.toString();
    }
}
