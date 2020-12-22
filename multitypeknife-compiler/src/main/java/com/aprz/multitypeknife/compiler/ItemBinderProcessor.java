package com.aprz.multitypeknife.compiler;

import androidx.annotation.NonNull;

import com.aprz.multitypeknife.annotation.ItemBinder;
import com.aprz.multitypeknife.annotation.ItemLayoutId;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import static com.aprz.multitypeknife.compiler.Constants.BASE_VIEW_HOLDER;
import static com.aprz.multitypeknife.compiler.Constants.ITEM_VIEW_BINDER;
import static com.aprz.multitypeknife.compiler.Constants.LAYOUT_INFLATER;
import static com.aprz.multitypeknife.compiler.Constants.ITEM_BINDER_PROCESSOR_ANNOTATION;
import static com.aprz.multitypeknife.compiler.Constants.VIEW;
import static com.aprz.multitypeknife.compiler.Constants.VIEW_GROUP;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

@AutoService(Processor.class)
@SupportedAnnotationTypes({ITEM_BINDER_PROCESSOR_ANNOTATION})
public class ItemBinderProcessor extends BaseProcessor {

    private static final String ON_CREATE_VIEW_HOLDER_METHOD = "onCreateViewHolder";
    private static final String ON_BIND_VIEW_HOLDER_METHOD = "onBindViewHolder";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!CollectionUtils.isEmpty(annotations)) {
            Set<? extends Element> adapterAnnotations = roundEnv.getElementsAnnotatedWith(ItemBinder.class);

            try {
                this.parseAnnotation(adapterAnnotations);
            } catch (Exception ignored) {
                return false;
            }
            return true;
        }

        return false;
    }

    private void parseAnnotation(Set<? extends Element> adapterAnnotations) {
        if (CollectionUtils.isEmpty(adapterAnnotations)) {
            return;
        }

        for (Element element : adapterAnnotations) {
            // element 必须实现 Layout 接口
            TypeElement typeElement = (TypeElement) element;
            DeclaredType declaredType = (DeclaredType) typeElement.getSuperclass();
            TypeElement superTypeElement = (TypeElement) declaredType.asElement();

            if (!superTypeElement.getQualifiedName()
                    .equals(elementUtils.getTypeElement(BASE_VIEW_HOLDER).getQualifiedName())) {
                messager.printMessage(Diagnostic.Kind.ERROR, "class should extend BaseViewHolder: " + element.toString());
                return;
            }

            if (element.getModifiers().contains(PRIVATE)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "private modifiers is not allowed: " + element.toString() + "#" + element.toString());
                return;
            }

            if (!element.getModifiers().contains(STATIC)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "you should add static modifiers: " + element.toString());
                return;
            }

            ItemBinder annotation = element.getAnnotation(ItemBinder.class);
            String className = annotation.name();

            TypeMirror viewHolderTypeMirror = element.asType();
            TypeMirror itemBeanTypeMirror = declaredType.getTypeArguments().get(0);

            MethodSpec.Builder onCreateViewHolderBuilder = buildOnCreateViewHolderMethod(element, viewHolderTypeMirror);
            MethodSpec.Builder onBindViewHolderBuilder = buildOnBindViewHolderMethod(viewHolderTypeMirror, itemBeanTypeMirror);

            ParameterizedTypeName superClass = ParameterizedTypeName.get(fromName(ITEM_VIEW_BINDER),
                    TypeName.get(itemBeanTypeMirror), TypeName.get(viewHolderTypeMirror));

            try {
                JavaFile.builder(getAdapterQualifiedName(element),
                        TypeSpec.classBuilder(className)
                                .superclass(superClass)
                                .addModifiers(PUBLIC)
                                .addMethod(Objects.requireNonNull(onCreateViewHolderBuilder).build())
                                .addMethod(onBindViewHolderBuilder.build())
                                .build()
                ).build().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private MethodSpec.Builder buildOnCreateViewHolderMethod(Element element, TypeMirror viewHolderTypeMirror) {
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        String layoutRef = "";
        for (Element enclosedElement : enclosedElements) {
            ItemLayoutId annotation = enclosedElement.getAnnotation(ItemLayoutId.class);
            if (annotation != null) {
                if (enclosedElement.getModifiers().contains(PRIVATE)) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "private modifiers is not allowed: " + element.toString() + "#" + enclosedElement.toString());
                    return null;
                }
                if (!enclosedElement.getModifiers().contains(STATIC)) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "you should add static modifiers: " + element.toString() + "#" + enclosedElement.toString());
                    return null;
                }
                layoutRef = enclosedElement.toString();
            }
        }
        if (StringUtils.isEmpty(layoutRef)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "at least one field should add @ItemLayoutId annotation: " + element.toString());
            return null;
        }

        MethodSpec.Builder onCreateViewHolderBuilder = MethodSpec.methodBuilder(ON_CREATE_VIEW_HOLDER_METHOD);
        onCreateViewHolderBuilder.addModifiers(PROTECTED).addAnnotation(Override.class).addAnnotation(NonNull.class);
        onCreateViewHolderBuilder.returns(TypeName.get(viewHolderTypeMirror));
        onCreateViewHolderBuilder.addParameter(ParameterSpec
                .builder(fromName(LAYOUT_INFLATER), "inflater")
                .addAnnotation(NonNull.class).build());
        onCreateViewHolderBuilder.addParameter(ParameterSpec
                .builder(fromName(VIEW_GROUP), "parent")
                .addAnnotation(NonNull.class).build());
        onCreateViewHolderBuilder.addStatement("$T itemView = inflater.inflate($T.$L, parent, false)", fromName(VIEW), element.asType(), layoutRef);
        onCreateViewHolderBuilder.addStatement("return new $T(itemView)", viewHolderTypeMirror);
        return onCreateViewHolderBuilder;
    }

    private MethodSpec.Builder buildOnBindViewHolderMethod(TypeMirror viewHolderTypeMirror, TypeMirror itemTypeMirror) {
        MethodSpec.Builder onBindViewHolderBuilder = MethodSpec.methodBuilder(ON_BIND_VIEW_HOLDER_METHOD);
        onBindViewHolderBuilder.addModifiers(PROTECTED).addAnnotation(Override.class);
        onBindViewHolderBuilder.addParameter(ParameterSpec
                .builder(TypeName.get(viewHolderTypeMirror), "holder")
                .addAnnotation(NonNull.class).build());
        onBindViewHolderBuilder.addParameter(ParameterSpec
                .builder(TypeName.get(itemTypeMirror), "item")
                .addAnnotation(NonNull.class).build());
        onBindViewHolderBuilder.addStatement("holder.bindView(item)");
        return onBindViewHolderBuilder;
    }

    private String getAdapterQualifiedName(Element element) {
        TypeElement hostElement = (TypeElement) element.getEnclosingElement();
        // 获取全名
        String qualifiedName = hostElement.getQualifiedName().toString();
        // 获取包名
        return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
    }

    private ClassName fromName(String className) {
        return ClassName.get(elementUtils.getTypeElement(className));
    }

}
