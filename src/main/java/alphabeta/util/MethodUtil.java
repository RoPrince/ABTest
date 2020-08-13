package alphabeta.util;

import com.alphabeta.annotation.AlphaFeature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodUtil {

    /***
     * This method is executed when    method is <b>NOT</b> annotated with {@link AlphaFeature}.
     * @param methodName
     * @param alphaServiceObject
     * @param args
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object executeOriginalMethod(String methodName, Object alphaServiceObject, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method originalMethod = getOriginalMethod(methodName, alphaServiceObject, args);

        return originalMethod.invoke(alphaServiceObject, args);
    }

    public static Method getOriginalMethod(String methodName, Object alphaServiceObject, Object[] args) throws NoSuchMethodException {
        Method originalMethod;
        if (args != null && args.length > 0) {

            Class[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }

            originalMethod = alphaServiceObject.getClass().getMethod(methodName, paramTypes);

        } else {
            originalMethod = alphaServiceObject.getClass().getMethod(methodName);
        }
        return originalMethod;
    }
}
