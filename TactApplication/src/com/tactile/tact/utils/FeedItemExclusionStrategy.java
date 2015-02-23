package com.tactile.tact.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.lang.reflect.Field;

/**
 * Created by sebafonseca on 12/2/14.
 */
public class FeedItemExclusionStrategy implements ExclusionStrategy {

    Class excludeClass;

    public FeedItemExclusionStrategy(Class clazz){
        excludeClass = clazz;
    }

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        Boolean result = f.getDeclaringClass() == excludeClass;
        Boolean resultAux = false;
        if (result) {
            for (Field field : excludeClass.getDeclaredFields()) {
                resultAux |= f.getName().equals(field.getName());
            }
        }
        return result && resultAux;
    }
}
