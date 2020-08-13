package alphabeta.filter;

import com. alphabeta.datastore.TenantContext;

/**
 * {@link ContextTenantFinder} is default implementation of {@link TenantFinder}.
 * It makes use of tenant_id set by {@link TenantInterceptor} and returns the tenant_id
 */
public class ContextTenantFinder implements TenantFinder{
    /***
     * Finds the tenant_id in {@link TenantContext}
     * @param args args of method on which {@link com. alphabeta.annotation.AlphaFeature} is declared
     * @return Tenant id
     */
    @Override
    public String find(Object... args) {
        return TenantContext.getTenantId();
    }
}
