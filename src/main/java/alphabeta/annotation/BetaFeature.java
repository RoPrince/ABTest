package alphabeta.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD })
@Documented
@Inherited
public @interface BetaFeature {
    /***
     * Feature key (should have corresponding {@link BetaFeature})
     * @return
     */
    String feature();
}
