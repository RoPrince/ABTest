package alphabeta.datastore;

public class TenantContext {
  private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();
  public static void setTenantId(String tenantId) {
    CONTEXT.set(tenantId);
  }
  public static String getTenantId() {
    return CONTEXT.get();
  }
}