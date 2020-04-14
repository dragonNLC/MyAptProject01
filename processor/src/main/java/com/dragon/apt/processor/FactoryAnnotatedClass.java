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

    //��ȡ@Factory#id()�е�id
    public String getId() {
        return id;
    }

    //��ȡ��@Factory#type()ָ�������͵ĺϷ�ȫ��
    public String getQualifiedFactoryGroupName() {
        return qualifiedSuperClassName;
    }

    //��ȡ��@Factory#type()ָ�������͵�����
    public String getSimpleFactoryGroupName() {
        return simpleTypeName;
    }

    //��ȡ��@Factoryע���ԭʼԪ��
    public TypeElement getTypeElement() {
        return annotatedClassElement;
    }

}
