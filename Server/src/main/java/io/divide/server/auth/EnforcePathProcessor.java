/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
