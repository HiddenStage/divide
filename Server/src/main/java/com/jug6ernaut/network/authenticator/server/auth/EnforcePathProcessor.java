package com.jug6ernaut.network.authenticator.server.auth;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.ws.rs.Path;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/16/13
 * Time: 11:05 AM
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes({ //
        "com.jug6ernaut.network.authenticator.server.auth.EnforcePath" //
})

public class EnforcePathProcessor extends AbstractProcessor {

    public EnforcePathProcessor(){
        super();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(EnforcePath.class);
        for(Element e : elements){

            Path path = e.getAnnotation(Path.class);

            if(path==null || path.value() == null){
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Java class " + e.getSimpleName().toString() + " must have a @Path annotation");
            } else if(!path.value().equals("/auth")){
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Java class " + e.getSimpleName().toString() + " must have a @Path(\"/auth\") annotation");
            }

        }

        return false;
    }
}
