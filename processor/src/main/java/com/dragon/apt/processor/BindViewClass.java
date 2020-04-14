package com.dragon.apt.processor;

import javax.lang.model.element.VariableElement;

public class BindViewClass {

    private VariableElement varElement;
    private String qualifiedName;
    private int resId;
    private boolean onClick;

    public BindViewClass(VariableElement varElement, String qualifiedName, int resId, boolean onClick) throws IllegalArgumentException {
        this.varElement = varElement;
        this.qualifiedName = qualifiedName;
        this.resId = resId;
        this.onClick = onClick;

    }
}
