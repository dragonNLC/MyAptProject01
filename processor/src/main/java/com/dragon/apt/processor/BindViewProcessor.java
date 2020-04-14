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

    private Messager mMessager;//�ṩ��ע�⴦����һ��������󡢾����Լ���ʾ��Ϣ��;��
    private Elements mElements;//����element�Ĺ����࣬��Դ���У�ÿ�����ֶ���һ���ض����͵�element��Ҳ����ÿ��element����import��class��field��method...��
    private Types types;//����TypeMirror�Ĺ�����
    private Filer mFiler;// ���������ļ�

    private Map<String, BindViewGroupClasses> bvGroupClasses;

    //�����ҵ����а���bindView��element
    //�������ҵ���element��field�жϣ��Ƿ�Ϊfield
    //����ǵģ���ô����field��ͬһ���ļ��У�������ͬһ���ļ�������ж���������ɶ���ļ�
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
                return true;//���������Ϣ
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

