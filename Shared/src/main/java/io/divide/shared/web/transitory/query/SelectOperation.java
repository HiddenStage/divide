package io.divide.shared.web.transitory.query;

import io.divide.shared.web.transitory.TransientObject;

/**
 * Created by williamwebb on 11/25/13.
 */
public enum SelectOperation {
    COUNT(Count.class);

    private transient Class<?> type;

    private <T extends TransientObject> SelectOperation(Class<T> type){
        this.type = type;
    }

    public Class<?> getType(){
        return type;
    }

    public String getErrorMessage(){
        return this.name() + " requires type " + type.getSimpleName();
    }


}
