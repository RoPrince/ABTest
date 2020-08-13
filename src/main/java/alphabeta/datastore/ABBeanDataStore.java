package alphabeta.datastore;

import com. alphabeta.model.BeanPairVO;
import com. alphabeta.model.BeanVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class ABBeanDataStore {

    private static Logger logger = LoggerFactory.getLogger(ABBeanDataStore.class);

    private static Map<String, BeanPairVO> featureBeanPairMap = new HashMap<>();

    public static void addAlphaBean(String feature, BeanVO beanVO) {
        BeanPairVO beanPairVO = featureBeanPairMap.get(feature);

        if(beanPairVO == null) {
            beanPairVO = new BeanPairVO(feature);
        }
        beanPairVO.setAlphaBeanVO(beanVO);
        featureBeanPairMap.put(feature, beanPairVO);

    }

    public static void addBetaBean(String feature, BeanVO beanVO) {
        BeanPairVO beanPairVO = featureBeanPairMap.get(feature);

        if(beanPairVO == null) {
            beanPairVO = new BeanPairVO(feature);
        }
        beanPairVO.setBetaBeanVO(beanVO);
        featureBeanPairMap.put(feature, beanPairVO);
    }

    public static BeanVO getBeta(String feature) {
        BeanPairVO beanPairVO = featureBeanPairMap.get(feature);
        BeanVO beanVO = null;
        if(beanPairVO != null) {
            beanVO = beanPairVO.getBetaBeanVO();
        }
        return beanVO;
    }

    public static void validateABConfigs() {

        if(logger.isDebugEnabled()) {
            logger.debug("Validating AB bean configs");
        }

        for (BeanPairVO beanPairVO : featureBeanPairMap.values()) {

            BeanVO alphaBeanVO = beanPairVO.getAlphaBeanVO();
            BeanVO betaBeanVO = beanPairVO.getBetaBeanVO();

            if(alphaBeanVO == null || betaBeanVO == null) {
                throw new IllegalArgumentException("@AlphaFeature should have it's corresponding @BetaFeature or vise-versa defined.");
            }

            Method alphaMethod = alphaBeanVO.getTargetMethod();
            Method betaMethod = betaBeanVO.getTargetMethod();

            if (!alphaMethod.getReturnType().equals(betaMethod.getReturnType())) {
                throw new IllegalArgumentException("ReturnType mismatch was found for methods defined [Feature: " + alphaBeanVO.getFeature() + "]");
            }

            if (alphaMethod.getParameterCount() != betaMethod.getParameterCount()) {
                throw new IllegalArgumentException("Parameter mismatch was found for methods defined [Feature: " + alphaBeanVO.getFeature() + "]");
            }

            Map<Class, Integer> alphaMethodParamTypeMap = getParameterTypeMap(alphaMethod.getParameterTypes());
            Map<Class, Integer> betaMethodParamTypeMap = getParameterTypeMap(betaMethod.getParameterTypes());

            for (Map.Entry<Class, Integer> alphaEntry : alphaMethodParamTypeMap.entrySet()) {
                Integer alphaParamCountOfType = alphaEntry.getValue();
                Integer betaParamCountOfType = betaMethodParamTypeMap.get(alphaEntry.getKey());

                if (alphaParamCountOfType != betaParamCountOfType) {
                    throw new IllegalArgumentException("Parameter type mismatch was found for methods defined [Feature: " + alphaBeanVO.getFeature() + "]");
                }
            }
        }

        logger.info("Successfully validated AB bean configs");

    }

    private static Map<Class, Integer> getParameterTypeMap(Class<?>[] parameterTypes) {
        Map<Class, Integer> paramTypeMap = new HashMap<>();

        for (Class paramType : parameterTypes) {
            Integer count = paramTypeMap.get(paramType);
            if (count != null) {
                paramTypeMap.put(paramType, count++);
            } else {
                paramTypeMap.put(paramType, 1);
            }
        }
        return paramTypeMap;
    }


}
