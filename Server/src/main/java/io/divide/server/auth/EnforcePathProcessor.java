/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.divide.server.auth;

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

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes({ //
        "io.divide.authenticator.server.auth.EnforcePath" //
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
