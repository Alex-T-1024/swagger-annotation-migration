package com.ae.app.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseAnnotationMapping {
    private String oldAnnotation;
    private String newAnnotation;
    private Map<String, String> mapping;
    private List<String> oldImport;
    private String newImport;


    public String getOldAnnotation() {
        return oldAnnotation;
    }

    public void setOldAnnotation(String oldAnnotation) {
        this.oldAnnotation = oldAnnotation;
    }

    public String getNewAnnotation() {
        return newAnnotation;
    }

    public void setNewAnnotation(String newAnnotation) {
        this.newAnnotation = newAnnotation;
    }

    /**
     * Returns parameters mapping {oldParameter, newParameter}
     */
    public Map<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public List<String> getOldImport() {
        return oldImport;
    }

    public void setOldImport(List<String> oldImport) {
        this.oldImport = oldImport;
    }

    public String getNewImport() {
        return newImport;
    }

    public void setNewImport(String newImport) {
        this.newImport = newImport;
    }


    public AnnotationInfo getSubstitutionInfo(String line, int lineNum) {
        AnnotationInfo result = new AnnotationInfo();
        boolean found = false;
        Pattern pattern = Pattern.compile(String.format("@\\b%s\\b\\((.*)\\)", oldAnnotation));
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            found = true;
            String oldString = matcher.group();
            String oldParenthesesContent = matcher.group(1);
            Map<String, String> oldAnnotationParameter = getAnnotationParameter(oldParenthesesContent);
            Map<String, String> newAnnotationParameter = mapAnnotationParameter(oldAnnotationParameter);
            String newString = convertMap2String(newAnnotationParameter);
            result.setLine(lineNum).setOldString(oldString).setNewString(newString)
                    .setOldAnnotationParameters(oldAnnotationParameter)
                    .setNewAnnotationParameters(newAnnotationParameter);
        }
        if (!found) return null;
        return result;
    }

    private Map<String, String> getAnnotationParameter(String annotationString) {
        Map<String, String> map = new HashMap<>();
        String[] keyValuePairs = annotationString.split(",");
        for (String keyValue : keyValuePairs) {
            String[] kvArr = keyValue.split("=");
            String key, value;

            if (kvArr.length > 1) {
                key = kvArr[0];
                value = kvArr[1];
            } else {
                // Default key is "value"
                key = "value";
                value = kvArr[0];
            }
            map.put(key, value);
        }

        return map;
    }

    protected Map<String, String> mapAnnotationParameter(Map<String, String> oldAnnotationParameter) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> entry : oldAnnotationParameter.entrySet()) {
            String key = mapping.get(entry.getKey());
            if (key == null) {
                key = entry.getKey();
            }
            String value = entry.getValue();
            map.put(key, value);
        }
        return map;
    }

    private String convertMap2String(Map<String, String> annotationParameter) {
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(newAnnotation).append("(");
        for (Map.Entry<String, String> entry : annotationParameter.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        }
        if (annotationParameter.size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }

}