package com.dragon.apt.processor;

import com.dragon.apt.annotation.Factory;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

public class FactoryAnnotatedClass {

    private TypeElement annotatedClassElement;
    private String qualifiedSuperClassName;
    private String simpleTypeName;
    private String id;

    public FactoryAnnotatedClass(TypeElement annotatedClassElement) throws IllegalArgumentException {
        this.annotatedClassElement = annotatedClassElement;
        Factory annotation = annotatedClassElement.getAnnotation(Factory.class);
        id = annotation.name();
        if ("".equals(id)) {
            throw new IllegalArgumentException(String.format("id() int @%s for class %s is null or empty! that's not allowed", Factory.class.getSimpleName(), annotatedClassElement.getQualifiedName()));
        }

        try {
            Class<?> clazz = annotation.type();
            qualifiedSuperClassName = clazz.getCanonicalName();
            simpleTypeName = clazz.getSimpleName();
        } catch (MirroredTypeException e) {
            DeclaredType classTypeMirror = (DeclaredType) e.getTypeMirror();
            TypeElement clzTypeElement = (TypeElement) classTypeMirror.asElement();
            qualifiedSuperClassName = clzTypeElement.getQualifiedName().toString();
            simpleTypeName = clzTypeElement.getSimpleName().toString();
        }
    }

    //获取@Factory#id()中的id
    public String getId() {
        return id;
    }

    //获取在@Factory#type()指定的类型的合法全名
    public String getQualifiedFactoryGroupName() {
        return qualifiedSuperClassName;
    }

    //获取在@Factory#type()指定的类型的名字
    public String getSimpleFactoryGroupName() {
        return simpleTypeName;
    }

    //获取被@Factory注解的原始元素
    public TypeElement getTypeElement() {
        return annotatedClassElement;
    }

}
