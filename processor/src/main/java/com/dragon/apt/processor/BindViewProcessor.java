package com.dragon.apt.processor;

import com.dragon.apt.annotation.BindView;
import com.dragon.apt.annotation.Factory;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
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

    private Map<String, BindViewGroupClasses> bvGroupClasses;

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
        bvGroupClasses = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotationElements = roundEnv.getElementsAnnotatedWith(BindView.class);//find use BindView's element
        for (Element e :
                annotationElements) {
            GenerateTools.printer("e = " + e.getSimpleName());
            GenerateTools.printer("e = " + e.getEnclosingElement().getEnclosingElement().getSimpleName());
            if (e.getKind() != ElementKind.FIELD) {
                GenerateTools.error(mMessager, e, "Only field can be annotated with @%s", BindView.class.getSimpleName());
                return true;//输出错误信息
            }
            VariableElement variableElement = (VariableElement) e;
            try {
                BindViewClass bindViewClass = new BindViewClass(variableElement);
                if (!isValidField(bindViewClass)) {
                    return true;
                }
                BindViewGroupClasses groupClasses = bvGroupClasses.get(bindViewClass.getQualifiedSuperClassName());
                if (groupClasses == null) {
                    String qualifiedGroupName = bindViewClass.getQualifiedSuperClassName();
                    groupClasses = new BindViewGroupClasses(qualifiedGroupName);
                    bvGroupClasses.put(qualifiedGroupName, groupClasses);
                }
                groupClasses.add(bindViewClass);
            } catch (IllegalArgumentException ex) {
                GenerateTools.error(mMessager, variableElement, ex.getMessage());
                return true;
            }
        }
        try {
            for (BindViewGroupClasses bgc :
                    bvGroupClasses.values()) {
                GenerateTools.printer("bgc = " + bgc.toString());
                bgc.generateCode(mElements, mFiler);
                GenerateTools.printer("bindViewClass = " + bgc.toString());
            }
        } catch (IOException ioe) {
            GenerateTools.error(mMessager, null, ioe.getMessage());
        }
        bvGroupClasses.clear();
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

    public boolean isValidField(BindViewClass bindViewClass) {
        VariableElement variableElement = bindViewClass.getVarElement();
        if (variableElement.getModifiers().contains(Modifier.PRIVATE)) {
            GenerateTools.error(mMessager, variableElement, "The field may not has a private domain!");
            return false;
        }
        /*TypeElement superClassElement = mElements.getTypeElement(bindViewClass.getQualifiedSuperClassName());
        if (superClassElement.getKind() == ElementKind.INTERFACE) {
            if (variableElement.asType().)
        }*/
        // TODO: 2020/4/14 check the field parent class.
        return true;
    }

}

