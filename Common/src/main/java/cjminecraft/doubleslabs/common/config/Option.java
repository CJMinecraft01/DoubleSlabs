package cjminecraft.doubleslabs.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {

    String name() default ""; // default same as field name

    String category() default "general";

    String[] comment();

    String translation() default "";

    String[] options() default {};

    boolean strict() default true;

    // validators



}
