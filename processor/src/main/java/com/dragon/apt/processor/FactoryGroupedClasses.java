package com.dragon.apt.processor;

import com.dragon.apt.annotation.Factory;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class FactoryGroupedClasses {

    private String qualifiedClassName;

    private Map<String, FactoryAnnotatedClass> itemMap;

    private static final String SUFFIX = "Factory";

    public FactoryGroupedClasses(String qualifiedClassName) {
        this.qualifiedClassName = qualifiedClassName;
        itemMap = new LinkedHashMap<>();
    }

    public void add(FactoryAnnotatedClass toInsert) throws IDAlreadyUsedException {

        FactoryAnnotatedClass existing = itemMap.get(toInsert.getId());
        if (existing != null) {
            throw new IDAlreadyUsedException(existing.getId() + " already use in " + existing.getQualifiedFactoryGroupName() + " may not use in " + toInsert.getQualifiedFactoryGroupName(), existing);
        }
        itemMap.put(toInsert.getId(), toInsert);
    }

    public static class IDAlreadyUsedException extends RuntimeException {

        private FactoryAnnotatedClass factoryAnnotatedClass;

        public IDAlreadyUsedException(String s, FactoryAnnotatedClass factoryAnnotatedClass) {
            super(s);
            this.factoryAnnotatedClass = factoryAnnotatedClass;
        }

        public FactoryAnnotatedClass getExisting() {
            return factoryAnnotatedClass;
        }

    }

    public void generateCode(Elements elementUtils, Filer filer) throws IOException {
        TypeElement superClassName = elementUtils.getTypeElement(qualifiedClassName);
        String factoryClassName = superClassName.getSimpleName() + SUFFIX;
        //String qualifiedFactoryClassName = qualifiedClassName + SUFFIX;
        PackageElement pkg = elementUtils.getPackageOf(superClassName);
        String packageName = pkg.isUnnamed() ? "" : pkg.getQualifiedName().toString();

        MethodSpec.Builder method = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "id")
                .returns(TypeName.get(superClassName.asType()));

        method.beginControlFlow("if(id == null)")
                .addStatement("throw new IllegalArgumentException($S)", "id is null!")
                .endControlFlow();

        for (FactoryAnnotatedClass item : itemMap.values()) {
            method.beginControlFlow("if($S.equals(id))", item.getId())
                    .addStatement("return new $L()", item.getTypeElement().getQualifiedName())
                    .endControlFlow();
        }
        method.addStatement("throw new IllegalArgumentException($S + id)", "Unknown id = ");
        TypeSpec typeSpec = TypeSpec.classBuilder(factoryClassName)
                .addMethod(method.build())
                .build();
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);

        System.out.println("generateCode");
    }

}
