package com.dragon.apt.processor;

import com.dragon.apt.annotation.Factory;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.dragon.apt.annotation.Factory")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private Map<String, FactoryGroupedClasses> factoryClasses;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtils = processingEnvironment.getTypeUtils();
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        factoryClasses = new LinkedHashMap<>();
        System.out.println("FactoryProcessor");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println(factoryClasses.values().toString());
        for (Element e :
                roundEnvironment.getElementsAnnotatedWith(Factory.class)) {
            if (e.getKind() != ElementKind.CLASS) {
                error(e, "Only classes can be annotated with @%s", Factory.class.getSimpleName());
                return true;
            }
            TypeElement typeElement = (TypeElement) e;
            try {
                FactoryAnnotatedClass annotatedClass = new FactoryAnnotatedClass(typeElement);
                if (!isValidClass(annotatedClass)) {
                    return true;//û���ҵ����ϵ����ݣ��˳�
                }
                //һ�����Ǽ��isValidClass()�ɹ������ǽ����FactoryAnnotationClass����Ӧ��FactoryGroupedClasses
                FactoryGroupedClasses factoryGroupedClasses = factoryClasses.get(annotatedClass.getQualifiedFactoryGroupName());
                if (factoryGroupedClasses == null) {
                    String qualifiedGroupName = annotatedClass.getQualifiedFactoryGroupName();
                    factoryGroupedClasses = new FactoryGroupedClasses(qualifiedGroupName);
                    factoryClasses.put(qualifiedGroupName, factoryGroupedClasses);
                    //�����������@Factory��ע�����id��ͬ��ͻ
                    //�׳�IDAlreadyUsedException�쳣
                }
                factoryGroupedClasses.add(annotatedClass);
            } catch (IllegalArgumentException ex) {
                error(typeElement, ex.getMessage());
                return true;
            } catch (FactoryGroupedClasses.IDAlreadyUsedException ex2) {
                FactoryAnnotatedClass existing = ex2.getExisting();
                error(e, "Conflict:The class %s is annotated with @%s with id = '%s' but %s already uses the same id",
                        typeElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                        existing.getTypeElement().getQualifiedName().toString());
                return true;
            }
        }
        try {
            for (FactoryGroupedClasses factoryClass :
                    factoryClasses.values()) {
                System.out.println("factoryClasses.values() = " + factoryClasses.values().size());
                factoryClass.generateCode(elementUtils, filer);
            }
        } catch (IOException ioe) {
            error(null, ioe.getMessage());
        }
        factoryClasses.clear();
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Factory.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(Element element, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR,
                String.format(msg, args),
                element);
    }

    private boolean isValidClass(FactoryAnnotatedClass item) {
        //תΪTypeElement�����и����ض�����
        TypeElement classElement = item.getTypeElement();
        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            error(classElement, "The class %s is not public.", classElement.getQualifiedName().toString());
            return false;
        }
        //����Ƿ���һ��������
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            error(classElement, "The class $s is abstract. You can't annotate abstract classes with @%",
                    classElement.getQualifiedName().toString(), Factory.class.getSimpleName());
        }
        //���̳й�ϵ��������@Factory.type()ָ������������
        TypeElement superClassElement = elementUtils.getTypeElement(item.getQualifiedFactoryGroupName());
        if (superClassElement.getKind() == ElementKind.INTERFACE) {
            //���ӿ��Ƿ�ʵ����
            if (!classElement.getInterfaces().contains(superClassElement.asType())) {
                error(classElement, "The class %s annotated with @%s must implement the interface %s",
                        classElement.getQualifiedName().toString(), Factory.class.getSimpleName());
                return false;
            }
        } else {
            //�������
            TypeElement currentClass = classElement;
            while (true) {
                TypeMirror superClassType = currentClass.getSuperclass();
                if (superClassType.getKind() == TypeKind.NONE) {
                    //�Ѿ������������object���˳���û���ҵ����ϵĸ���
                    error(classElement, "The class %s annotated with @%s must inherit from %s", classElement.getQualifiedName().toString(),
                            Factory.class.getSimpleName(), item.getQualifiedFactoryGroupName());
                    return false;
                }
                if (superClassType.toString().equals(item.getQualifiedFactoryGroupName())) {
                    //�ҵ�����Ҫ��ĸ���
                    break;
                }
                //�ڼ̳����ϼ�������Ѱ��
                currentClass = (TypeElement) typeUtils.asElement(superClassType);
            }
        }

        //����Ƿ��ṩ��Ĭ�Ϲ������캯��
        for (Element enClose :
                classElement.getEnclosedElements()) {
            if (enClose.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enClose;
                if (constructorElement.getParameters().size() == 0 && constructorElement.getModifiers().contains(Modifier.PUBLIC)) {
                    //�ҵ�Ĭ�Ϲ��캯�����޲ι����Ĺ��캯��
                    return true;
                }
            }
        }
        //û���ҵ�Ĭ�Ϲ��캯��
        error(classElement, "The class %s must provide an public empty default constructor", classElement.getQualifiedName().toString());
        return false;
    }

}
