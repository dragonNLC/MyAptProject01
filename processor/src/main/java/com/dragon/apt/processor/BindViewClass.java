package com.dragon.apt.processor;

import com.dragon.apt.annotation.BindView;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

public class BindViewClass {

    private VariableElement varElement;
    private String qualifiedSuperClassName;
    private String simpleTypeName;
    private int resId;
    private boolean onClick;

    public BindViewClass(VariableElement varElement) {
        this.varElement = varElement;
        BindView bindView =  varElement.getAnnotation(BindView.class);
        System.out.println("bindView.id() = " + bindView.id());
        resId = bindView.id();
        onClick = bindView.onClick();
        simpleTypeName = varElement.getSimpleName().toString();
        qualifiedSuperClassName = varElement.getEnclosingElement().getSimpleName().toString();
        if (resId <= 0) {//没有设置资源id，报错
            throw new IllegalArgumentException(String.format("resId() int @%s for field %s is null or empty!this's not allowed", BindView.class.getSimpleName(), varElement.getSimpleName()));
        }
        DeclaredType classTypeMirror = (DeclaredType) varElement.asType();
        TypeElement clzTypeElement = (TypeElement) classTypeMirror.asElement();
        qualifiedSuperClassName = clzTypeElement.getQualifiedName().toString();
        simpleTypeName = clzTypeElement.getSimpleName().toString();
    }

    public int getResId() {
        return resId;
    }

    public String getQualifiedSuperClassName() {
        return qualifiedSuperClassName;
    }

    public String getSimpleTypeName() {
        return simpleTypeName;
    }

    public boolean onClick() {
        return onClick;
    }

    public VariableElement getVarElement() {
        return varElement;
    }

}
