package alphabeta.annotation;

import com. alphabeta.filter.ContextTenantFinder;
import com. alphabeta.filter.TenantFinder;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@Documented
@Inherited
public @interface AlphaFeature {
    /***
     * Feature key (should have corresponding {@link BetaFeature})
     * @return
     */
    String feature();

    /***
     * Define tenantFinder when you want to find out tenant with custom logic
     * Otherwise, by default it'll be picked from context (set by {@link com. alphabeta.filter.TenantInterceptor})
     * @return
     */
    Class<? extends TenantFinder> tenantFinder() default ContextTenantFinder.class;
}
