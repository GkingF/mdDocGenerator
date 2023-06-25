package com.docgenerator.mddocgenerator.generator;

import com.docgenerator.mddocgenerator.definition.FieldDefinition;
import com.docgenerator.mddocgenerator.definition.RestFulDefinition;

import java.util.List;

public class RestDocumentGenerator {

    private final RestFulDefinition definition;

    public RestDocumentGenerator(RestFulDefinition definition) {
        this.definition = definition;
    }

    public String generate() {
        StringBuffer docContent = new StringBuffer();
        docContent.append(interfaceNamePart());
        docContent.append("\n");
        docContent.append(urlPart());
        docContent.append("\n");
        docContent.append(methodPart());
        docContent.append("\n");
        docContent.append("\n");
        docContent.append(requestPart());
        docContent.append("\n");
        docContent.append(requestExample());
        docContent.append("\n");
        docContent.append(responsePart());
        docContent.append("\n");
        docContent.append(responseExample());
        return docContent.toString();
    }

    private String responseExample() {
        return "```json \n" + definition.getResponseExample() + "\n ```\n";
    }

    private String requestExample() {
        String requestExample = definition.getRequestExample();
        if (requestExample == null) {
            return "";
        }
        return "```json \n" + requestExample + "\n ```\n";
    }

    public String interfaceNamePart() {
        return "## " + this.definition.getDesc() + "\n";
    }

    public String urlPart() {
        return "> 请求URL：`" + this.definition.getUri() + "`\n";
    }

    public String methodPart() {
        return "> 请求方式：`" + this.definition.getHttpMethod().toUpperCase() + "`,`" + this.definition.getRequestBodyType().toString() + "`\n";
    }

    public String requestPart() {
        StringBuffer stringBuffer = new StringBuffer("### 请求参数\n \n");
        List<FieldDefinition> fieldDefinitions = this.definition.getRequest();
        if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
            stringBuffer.append("无参数\n");
            return stringBuffer.toString();
        }
        stringBuffer.append("|参数名|类型|说明|必选|\n");
        stringBuffer.append("|:----    |:---|:----- |-----   |\n");
        stringBuffer.append(this.requestDefinitionTableBody(fieldDefinitions));
        return stringBuffer.toString();
    }

    public String responsePart() {
        StringBuffer stringBuffer = new StringBuffer("### 返回参数\n \n");
        stringBuffer.append("|参数名|类型|说明|\n");
        stringBuffer.append("|:----   |:----- |-----   |\n");
        List<FieldDefinition> fieldDefinitions = this.definition.getResponse();
        stringBuffer.append(this.responseDefinitionTableBody(fieldDefinitions));
        return stringBuffer.toString();
    }

    public String responseDefinitionTableBody(List<FieldDefinition> fieldDefinitions) {
        StringBuffer stringBuffer = new StringBuffer();
        if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
            return stringBuffer.toString();
        }
        for (FieldDefinition definition : fieldDefinitions) {
            String layerChat = this.getLayerChat(definition.getLayer());
            stringBuffer.append("|" + layerChat + definition.getName());
            stringBuffer.append("|" + definition.getType());
            String visibleDesc = definition.getDesc().replaceAll("\\p{C}", "");
            stringBuffer.append("|" + visibleDesc);
            stringBuffer.append("|\n");
            if (definition.getSubFieldDefinitions() != null && !definition.getSubFieldDefinitions().isEmpty()) {
                stringBuffer.append(this.responseDefinitionTableBody(definition.getSubFieldDefinitions()));
            }
        }
        return stringBuffer.toString();
    }

    public String requestDefinitionTableBody(List<FieldDefinition> fieldDefinitions) {
        StringBuffer stringBuffer = new StringBuffer();
        if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
            return stringBuffer.toString();
        }
        for (FieldDefinition definition : fieldDefinitions) {
            String layerChat = this.getLayerChat(definition.getLayer());
            stringBuffer.append("|" + layerChat + definition.getName());
            stringBuffer.append("|" + definition.getType());
            stringBuffer.append("|" + definition.getDesc());
            stringBuffer.append("|" + (definition.isRequire() ? "是" : ""));
            stringBuffer.append("|\n");
            if (definition.getSubFieldDefinitions() != null && !definition.getSubFieldDefinitions().isEmpty()) {
                stringBuffer.append(this.responseDefinitionTableBody(definition.getSubFieldDefinitions()));
            }
        }
        return stringBuffer.toString();
    }

    public String getLayerChat(int layer) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < layer; i++) {
            stringBuffer.append("--");
        }
        return stringBuffer.toString();
    }


}
