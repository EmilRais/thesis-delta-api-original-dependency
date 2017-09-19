package dk.developer.security;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Security {
    String HEADER = "Authorization";

    Mechanism value();

    enum Mechanism {
        NONE, BLOCK, LOGIN
    }
}
