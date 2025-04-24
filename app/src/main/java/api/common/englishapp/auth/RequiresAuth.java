package api.common.englishapp.auth;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresAuth {
    String[] roles() default {};

    boolean allowAnonymous() default false;
}
