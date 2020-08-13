package alphabeta.filter;

import com. alphabeta.config.ABConfigurationProperties;
import com. alphabeta.datastore.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/***
 * In case of HTTP requests,
 * {@link TenantInterceptor} will find tenant_id in param/header (as specified in app yaml/properties)
 * And sets it in {@link TenantContext}
 */
public class TenantInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(TenantInterceptor.class);

    private ABConfigurationProperties abConfigurationProperties;

    public TenantInterceptor(ABConfigurationProperties abConfigurationProperties) {
        this.abConfigurationProperties = abConfigurationProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String tenantId = null;

        if(abConfigurationProperties.getHeader() != null) {
            tenantId = request.getHeader(abConfigurationProperties.getHeader());
        } else if(abConfigurationProperties.getParam() != null){
            tenantId = request.getParameter(abConfigurationProperties.getParam());
        } else {
            logger.info("No header or parameter found. Setting tenant as null");
        }

        if(tenantId != null) {
            logger.info("Tenant id: '{}' set", tenantId);
        } else {
            logger.info("Tenant id was not set.");
        }
        TenantContext.setTenantId(tenantId);
        return true;
    }
}