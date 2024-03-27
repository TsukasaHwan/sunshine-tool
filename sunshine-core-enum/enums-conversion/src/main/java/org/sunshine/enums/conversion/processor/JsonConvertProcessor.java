package org.sunshine.enums.conversion.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.sunshine.enums.conversion.annotation.JsonAutoConverter;
import org.sunshine.enums.conversion.converter.AbstractJsonConverter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.persistence.Converter;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author: Teamo
 * @date: 2020/1/18 10:57
 * @description:
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.sunshine.enums.conversion.annotation.JsonAutoConverter")
public class JsonConvertProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;
    public static final String doc = "\n This codes are generated automatically. Do not modify! \n -.- \n created by sneb \n";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();
        messager = processingEnvironment.getMessager();
        elementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        messager.printMessage(Diagnostic.Kind.NOTE, "Processor : " + getClass().getSimpleName());

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(JsonAutoConverter.class);

        elements.forEach(x -> {
            // 被扫描的类的包路径
            PackageElement packageElement = elementUtils.getPackageOf(x);
            String packageName = packageElement.getQualifiedName().toString();

            // 获取类上面的注解
            JsonAutoConverter annotation = x.getAnnotation(JsonAutoConverter.class);

            // 构建类
            TypeSpec clazz = TypeSpec.classBuilder(x.getSimpleName() + "Converter")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(AnnotationSpec.builder(Converter.class).addMember("autoApply", CodeBlock.builder().add("$L", annotation.autoApply()).build()).build())
                    .addJavadoc(" generator for Json converter " + doc)
                    .superclass(ParameterizedTypeName.get(ClassName.get(AbstractJsonConverter.class),
                            ClassName.get((TypeElement) x)))
                    .build();
            try {
                // 创建java文件
                JavaFile javaFile = JavaFile.builder(packageName, clazz)
                        .build();
                // 写入
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 构建集合转换类
            TypeSpec listClazz = TypeSpec.classBuilder(x.getSimpleName() + "ListConverter")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(AnnotationSpec.builder(Converter.class).addMember("autoApply", CodeBlock.builder().add("$L", annotation.autoApply()).build()).build())
                    .addJavadoc(" generator for Json converter " + doc)
                    .superclass(ParameterizedTypeName.get(ClassName.get(AbstractJsonConverter.class),
                            ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get((TypeElement) x))))
                    .build();
            try {
                // 创建java文件
                JavaFile javaFile = JavaFile.builder(packageName, listClazz)
                        .build();
                // 写入
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        return false;
    }
}
