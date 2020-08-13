package alphabeta.config;

import com. alphabeta.annotation.AlphaFeature;
import com. alphabeta.annotation.BetaFeature;
import com. alphabeta.datastore.ABBeanDataStore;
import com. alphabeta.factory.DynamicABProxyBeanFactory;
import com. alphabeta.model.BeanVO;
import com. alphabeta.service.ABMetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/***
 * This component is used to modify Bean with @{@link AlphaFeature} annotation.
 * Enables bean to decide if alpha or beta bean should be executed.
 *
 * Pre-requisite bean should have a no-args constructor.
 */

public class ABBeanProcessor implements BeanPostProcessor {

    private Logger logger = LoggerFactory.getLogger(ABBeanProcessor.class);

    @Autowired
    private ABMetricService abMetricService;

    @PostConstruct
    public void init() {
        dynamicABProxyBeanFactory = new DynamicABProxyBeanFactory(abMetricService);
    }

    private DynamicABProxyBeanFactory dynamicABProxyBeanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> objClz = bean.getClass();
        if (org.springframework.aop.support.AopUtils.isAopProxy(bean)) {
            objClz = org.springframework.aop.support.AopUtils.getTargetClass(bean);
        }

        Object alphaBeanObject = null;

        for (Method method : objClz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AlphaFeature.class)) {

                throwIAEIfNoArgConstructorIsNotAvailable(objClz);

                Annotation annotation = method.getAnnotation(AlphaFeature.class);
                String feature = ((AlphaFeature) annotation).feature();

                BeanVO alphaBeanVO = new BeanVO(feature, beanName, bean, method);
                alphaBeanObject = bean;
                if (logger.isDebugEnabled()) {
                    logger.debug("Alpha Bean added for AB processing '{}'", alphaBeanVO);
                }

                ABBeanDataStore.addAlphaBean(feature, alphaBeanVO);
            } else if (method.isAnnotationPresent(BetaFeature.class)) {
                Annotation annotation = method.getAnnotation(BetaFeature.class);
                String feature = ((BetaFeature) annotation).feature();
                BeanVO betaBeanVO = new BeanVO(feature, beanName, bean, method);

                if (logger.isDebugEnabled()) {
                    logger.debug("Beta Bean added for AB processing '{}'", betaBeanVO);
                }

                ABBeanDataStore.addBetaBean(feature, betaBeanVO);

            }
        }

        /*
         Not Null value of alphaBeanObject denotes that this bean is alpha bean,
         and hence candidate for proxying
         */
        if (alphaBeanObject != null) {

            logger.info("Generating proxy implementation for '{}'", beanName);

            Object proxiedAlphaService = dynamicABProxyBeanFactory.generateFor(alphaBeanObject);
            return proxiedAlphaService;
        }

        return bean;
    }

    private void throwIAEIfNoArgConstructorIsNotAvailable(Class<?> objClz) {

        boolean isNoArgConstructorAvailable = false;
        for (Constructor constructor : objClz.getConstructors()) {
            if (constructor.getParameterCount() == 0) {
                isNoArgConstructorAvailable = true;
                break;
            }
        }

        if (!isNoArgConstructorAvailable) {
            logger.error("Public `no arg constructor` is not available in '{}'.", objClz.getName());
            throw new IllegalArgumentException("Public `no arg constructor` is required in bean with @AlphaFeature : ".concat(objClz.getName()));
        }

    }

}
