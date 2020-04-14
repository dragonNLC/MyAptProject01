package com.dragon.apt.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class BindViewGroupClasses {

    private Map<Integer, BindViewClass> itemMap;

    private String qualifiedClassName;

    public static final String SUFFIX = "_ViewBinding";

    public BindViewGroupClasses(String qualifiedClassName) {
        itemMap = new HashMap<>();
        this.qualifiedClassName = qualifiedClassName;
    }

    public void add(BindViewClass viewClass) throws IDAlreadyUsedException {
        BindViewClass existing = itemMap.get(viewClass.getResId());
        if (existing != null) {
            throw new IDAlreadyUsedException(existing.getResId() + " already use in " + existing.getQualifiedSuperClassName() + " may not use in " + viewClass.getQualifiedSuperClassName(), existing);
        }
        itemMap.put(viewClass.getResId(), viewClass);
    }

    public void generateCode(Elements elementUtils, Filer filer) throws IOException {
        TypeElement superClassName = elementUtils.getTypeElement(qualifiedClassName);
        if (superClassName != null) {
            System.out.println("generateCode");
            String factoryClassName = superClassName.getSimpleName() + SUFFIX;
            System.out.println("generateCode2");
            //String qualifiedFactoryClassName = qualifiedClassName + SUFFIX;
            System.out.println("generateCode3");
            PackageElement pkg = elementUtils.getPackageOf(superClassName);
            System.out.println("generateCode4");
            String packageName = pkg.isUnnamed() ? "" : pkg.getQualifiedName().toString();

            System.out.println("generateCode5");
            MethodSpec.Builder method = MethodSpec.methodBuilder("create")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(String.class, "id")
                    .returns(TypeName.get(superClassName.asType()));

            System.out.println("generateCode6");
            method.beginControlFlow("if(id == null)")
                    .addStatement("throw new IllegalArgumentException($S)", "id is null!")
                    .endControlFlow();

            System.out.println("generateCode7");
            for (BindViewClass item : itemMap.values()) {
                method.beginControlFlow("if($S.equals(id))", item.getResId())
                        .addStatement("return new $L()", item.getVarElement().getSimpleName())
                        .endControlFlow();
            }
            System.out.println("generateCode8");
            method.addStatement("throw new IllegalArgumentException($S + id)", "Unknown id = ");
            System.out.println("generateCode");
            TypeSpec typeSpec = TypeSpec.classBuilder(factoryClassName)
                    .addMethod(method.build())
                    .build();
            System.out.println("generateCode9");
            JavaFile.builder(packageName, typeSpec).build().writeTo(filer);

            System.out.println("generateCode10");
        }
    }

    public static class IDAlreadyUsedException extends RuntimeException {

        private BindViewClass bindViewClass;

        public IDAlreadyUsedException(String s, BindViewClass bindViewClass) {
            super(s);
            this.bindViewClass = bindViewClass;
        }

        public BindViewClass getExisting() {
            return bindViewClass;
        }

    }

}
