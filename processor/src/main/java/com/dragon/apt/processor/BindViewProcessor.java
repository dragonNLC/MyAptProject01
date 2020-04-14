package com.dragon.apt.processor;

import com.dragon.apt.annotation.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeSpec;

import java.util.LinkedHashSet;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@SupportedAnnotationTypes("com.dragon.apt.annotation.BindView")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    private Messager mMessager;//提供给注解处理器一个报告错误、警告以及提示信息的途径
    private Elements mElements;//处理element的工具类，在源码中，每个部分都是一个特定类型的element，也即，每个element代表（import、class、field、method...）
    private Types types;//处理TypeMirror的工具类
    private Filer mFiler;// 用来创建文件

    //遍历找到所有包含bindView的element
    //将所有找到的element与field判断，是否为field
    //如果是的，那么假如field在同一个文件中，则生成同一个文件，如果有多个，则生成多个文件
    //

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mElements = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        types = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotationElements = roundEnv.getElementsAnnotatedWith(BindView.class);//find use BindView's element

        //创建类
        /*TypeSpec hType = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(methodSpec)
                .addJavadoc("This class create from annotationProcessor!")
                .build();*/

        /*for (Element e :
                annotationElements) {
                if (e.getKind() == ElementKind.FIELD) {//if element is Field element
                    VariableElement typeElement = (VariableElement) e;//cast to variableElement
                    //create a BindView file

                    System.out.println("id = " + typeElement.getAnnotation(BindView.class).id());
                    System.out.println("onClick = " + typeElement.getAnnotation(BindView.class).onClick());
                }
            }*/
        Set<? extends Element> elements = roundEnv.getRootElements();
        elements.forEach(o -> {
            /*System.out.println("o = " + o.getSimpleName().toString());
            System.out.println("o = " + o.getKind());
            System.out.println("o = " + o.asType().toString());*/
        });
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(BindView.class.getCanonicalName());
        return annotations;
    }
}
