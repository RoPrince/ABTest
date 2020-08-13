package alphabeta.model;

import java.lang.reflect.Method;

public class BeanVO {

    public BeanVO(String feature, String beanName, Object   Object, Method   Method) {
        this.feature = feature;
        this.beanName = beanName;
        this.  Object =   Object;
        this.  Method =   Method;
    }

    private String feature;
    private Object   Object;
    private Method   Method;
    private String beanName;

    public String getFeature() {
        return feature;
    }

    public Object getTargetObject() {
        return   Object;
    }

    public Method getTargetMethod() {
        return   Method;
    }

    public String getBeanName() {
        return beanName;
    }

    @Override
    public String toString() {
        return "BeanVO{" +
                "feature='" + feature + '\'' +
                ",   Method=" +   Method +
                ", beanName='" + beanName + '\'' +
                '}';
    }
}
