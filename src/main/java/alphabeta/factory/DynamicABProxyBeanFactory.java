package alphabeta.factory;

import com. alphabeta.annotation.AlphaFeature;
import com. alphabeta.constants.MetricNameConstants;
import com. alphabeta.datastore.ABBeanDataStore;
import com. alphabeta.datastore.ABMappingsDataStore;
import com. alphabeta.datastore.TenantContext;
import com. alphabeta.filter.TenantFinder;
import com. alphabeta.model.BeanVO;
import com. alphabeta.service.ABMetricService;
import com. alphabeta.util.MethodUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class DynamicABProxyBeanFactory {

    private Logger logger = LoggerFactory.getLogger(DynamicABProxyBeanFactory.class);

    private ABMetricService abMetricService;


    public DynamicABProxyBeanFactory(ABMetricService abMetricService) {
        this.abMetricService = abMetricService;
    }

    public Object generateFor(Object originalAlphaServiceObj) {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(originalAlphaServiceObj.getClass());
        setPilotCallback(originalAlphaServiceObj, enhancer);

        return enhancer.create();

    }

    private void setPilotCallback(Object originalAlphaServiceObj, Enhancer enhancer) {
        enhancer.setCallback((MethodInterceptor) (cglibEnhancedAlphaServiceObj, cglibEnhancedMethod, args, proxy) -> {

            String methodName = cglibEnhancedMethod.getName();

            if (cglibEnhancedMethod.isAnnotationPresent(AlphaFeature.class)) {

                Annotation annotation = cglibEnhancedMethod.getAnnotation(AlphaFeature.class);
                String effectiveFeature = ((AlphaFeature) annotation).feature();

                String tenantIdInRequest = getTenantId((AlphaFeature) annotation, args);

                /**If zero tenants are enabled for beta feature.
                 * (i.e., feature is absent in property file or has NULL value)
                 **/
                boolean isFeatureDisabledInAllTenants = ABMappingsDataStore.isAppMappingAbsentInProperties(effectiveFeature);

                if (isFeatureDisabledInAllTenants) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Feature: '{}' is disabled on all tenants", effectiveFeature);
                    }
                    return executeOriginalMethodWhenAnnotatedWithAlphaFeature(effectiveFeature, methodName, originalAlphaServiceObj, args);
                } else if (ABMappingsDataStore.isFeatureChainWide(effectiveFeature)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Feature: '{}' is chain-wide", effectiveFeature);
                    }
                    return executeBeta(effectiveFeature, args);
                } else if (ABMappingsDataStore.isPilotEnabledFor(effectiveFeature, tenantIdInRequest)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Feature: '{}' is enabled for the tenant: '{}'", effectiveFeature, TenantContext.getTenantId());
                    }
                    return executeBeta(effectiveFeature, args);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Feature: '{}' is disabled for the tenant: '{}'", effectiveFeature, TenantContext.getTenantId());
                    }
                    return executeOriginalMethodWhenAnnotatedWithAlphaFeature(effectiveFeature, methodName, originalAlphaServiceObj, args);
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Method is non-annotated '{}'", cglibEnhancedMethod.getName());
                }
                return MethodUtil.executeOriginalMethod(methodName, originalAlphaServiceObj, args);
            }

        });
    }

    private String getTenantId(AlphaFeature annotation, Object[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        Class<? extends TenantFinder> finderCls = ABMappingsDataStore.getTenantFinderBy(annotation.feature());

        if (finderCls == null) {
            finderCls = annotation.tenantFinder();
        }

        Constructor<? extends TenantFinder> constructor = finderCls.getConstructor();
        Object tenantFilterObj = constructor.newInstance();
        return ((TenantFinder) tenantFilterObj).find(args);
    }

    /***
     * This method is executed when    method is annotated with {@link AlphaFeature}.
     * But feature is not enabled for the tenant
     * @param feature
     * @param methodName
     * @param alphaServiceObject
     * @param args
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Object executeOriginalMethodWhenAnnotatedWithAlphaFeature(String feature, String methodName, Object alphaServiceObject, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method originalMethod = MethodUtil.getOriginalMethod(methodName, alphaServiceObject, args);

        ABMetricService.MetricWrapper metricWrapper = abMetricService.startCapture(MetricNameConstants.ALPHA, feature);
        try {

            Object returnVal = originalMethod.invoke(alphaServiceObject, args);
            abMetricService.finalize(metricWrapper);
            return returnVal;

        } catch (Exception e) {
            abMetricService.finalize(metricWrapper, e);
            throw e;
        }
    }

    private Object executeBeta(String feature, Object[] args) throws InvocationTargetException, IllegalAccessException {

        BeanVO betaBeanVO = ABBeanDataStore.getBeta(feature);

        Method betaMethod = betaBeanVO.getTargetMethod();

        ABMetricService.MetricWrapper metricWrapper = abMetricService.startCapture(MetricNameConstants.BETA, feature);

        try {

            Object returnVal = betaMethod.invoke(betaBeanVO.getTargetObject(), args);
            abMetricService.finalize(metricWrapper);
            return returnVal;

        } catch (Exception e) {
            abMetricService.finalize(metricWrapper, e);
            throw e;
        }
    }

}
