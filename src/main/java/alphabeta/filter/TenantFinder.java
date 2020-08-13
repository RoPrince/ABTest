package alphabeta.filter;

/***
 * Implement this to interface to define where your tenant exist in method param.
 *
 * You can either pass this as part of {@link com. alphabeta.annotation.AlphaFeature}
 * or you can pass it in application yaml as <b>app.do-raaste.tenant-filter</b>
 */
public interface TenantFinder {

    /***
     * This method will receive all method params of the {@link com. alphabeta.annotation.AlphaFeature}
     * You can use custom logic to get tenant_id this will be used by library to decide A or B.
     * @param args args of method on which {@link com. alphabeta.annotation.AlphaFeature} is declared
     * @return Tenant id
     */
    String find(Object... args);
}
