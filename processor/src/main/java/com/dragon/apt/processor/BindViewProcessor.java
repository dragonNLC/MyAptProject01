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

    private Messager mMessager;//�ṩ��ע�⴦����һ��������󡢾����Լ���ʾ��Ϣ��;��
    private Elements mElements;//����element�Ĺ����࣬��Դ���У�ÿ�����ֶ���һ���ض����͵�element��Ҳ����ÿ��element����import��class��field��method...��
    private Types types;//����TypeMirror�Ĺ�����
    private Filer mFiler;// ���������ļ�

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
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotationElements = roundEnv.getElementsAnnotatedWith(BindView.class);//find use BindView's element

        //������
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
