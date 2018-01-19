package biologyiswell.factions.util.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NoSerialize {

    class NoSerializeAdapter implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return fieldAttributes.getAnnotation(NoSerialize.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return aClass.getAnnotation(NoSerialize.class) != null;
        }
    }
}
