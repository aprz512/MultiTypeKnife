package com.aprz.multitypeknife.compiler;


import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public abstract class BaseProcessor extends AbstractProcessor {

    protected TypeElement mTypeElementString;
    protected TypeElement mTypeElementInteger;
    protected TypeElement mTypeElementList;
    protected TypeElement mTypeElementArrayList;
    protected TypeElement mTypeElementHashSet;

    /**
     * Filer/Types/Elements
     */
    Filer mFiler;
    Types types;
    Elements elementUtils;
    Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mFiler = processingEnv.getFiler();
        types = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
