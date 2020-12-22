package com.aprz.multitypeknife.compiler;


import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


/**
 * 容易犯的错误：
 * 1. auto-service 需要同时配置 annotationProcessor 与 implementation，否则会报错/无法生效
 * 2. AutoService 注解不要导错包
 */
public abstract class BaseProcessor extends AbstractProcessor {

    protected TypeElement mTypeElementString;
    protected TypeElement mTypeElementInteger;
    protected TypeElement mTypeElementList;
    protected TypeElement mTypeElementArrayList;
    protected TypeElement mTypeElementHashSet;

    /**
     * Filer/Types/Elements 这些都是比较常用的
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
