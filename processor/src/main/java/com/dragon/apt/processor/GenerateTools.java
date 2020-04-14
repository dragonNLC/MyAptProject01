package com.dragon.apt.processor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class GenerateTools {

    public static void error(Messager messager, Element element, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR,
                String.format(msg, args),
                element);
    }

    public static void printer(String content) {
        System.out.println(content);
    }

}
