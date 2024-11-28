package clover.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Map;

public class BeanUtil {

    /**
     * 将请求参数转换为JavaBean对象
     *
     * @param beanClass 目标JavaBean类
     * @param requestParams 请求参数Map
     * @return 转换后的JavaBean对象
     */
    public static <T> T convert(Class<T> beanClass, Map<String, String> params) {
        try {
            T bean = beanClass.getDeclaredConstructor().newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String propertyName = property.getName();
                if (!propertyName.equals("class")) {
                    Method writeMethod = property.getWriteMethod();
                    if (writeMethod != null) {
                        String paramValue = params.get(propertyName);
                        if (paramValue != null) {
                            Class<?> paramType = writeMethod.getParameterTypes()[0];
                            try {
                                Object value = convert(paramType, paramValue);
                                writeMethod.invoke(bean, value);
                            } catch (Exception e) {
                                throw new RuntimeException("Error converting value '" + paramValue + "' to property '" + propertyName + "' of type " + paramType.getName(), e);
                            }
                        }
                    }
                }
            }
            return bean;
        } catch (IntrospectionException e) {
            throw new RuntimeException("Error introspecting bean class " + beanClass.getName(), e);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Error instantiating bean class " + beanClass.getName(), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error invoking method on bean class " + beanClass.getName(), e.getTargetException());
        }
    }
    /**
     * 将Map集合中的数据注入到JavaBean的属性中去
     *
     * @param bean 目标JavaBean对象
     * @param params 参数Map
     */
    public static void populate(Object bean, Map<String, String> params) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String propertyName = property.getName();
                if (!propertyName.equals("class")) {
                    Method writeMethod = property.getWriteMethod();
                    if (writeMethod != null) {
                        String value = params.get(propertyName);
                        if (value != null) {
                            Class<?> paramType = writeMethod.getParameterTypes()[0];
                            Object convertedValue = convert(paramType, value);
                            writeMethod.invoke(bean, convertedValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error populating bean", e);
        }
    }

    /**
     * 将一个对象的属性值复制到另一个对象
     *
     * @param newObject 目标对象
     * @param oldObject 源对象
     */
    public static void copyProperties(Object newObject, Object oldObject) throws NoSuchMethodException, IntrospectionException, InvocationTargetException, IllegalAccessException {
        BeanInfo oldBeanInfo = Introspector.getBeanInfo(oldObject.getClass());
        PropertyDescriptor[] oldPropertyDescriptors = oldBeanInfo.getPropertyDescriptors();

        for (PropertyDescriptor oldProperty : oldPropertyDescriptors) {
            String oldPropertyName = oldProperty.getName();
            if (!oldPropertyName.equals("class")) {
                Method readMethod = oldProperty.getReadMethod();
                Method writeMethod = newObject.getClass().getMethod("set" + capitalize(oldPropertyName), oldProperty.getPropertyType());
                Object value = readMethod.invoke(oldObject);
                writeMethod.invoke(newObject, value);
            }
        }
    }

    /**
     * 为指定bean实例的属性设值
     *
     * @param bean 目标JavaBean对象
     * @param name 属性名
     * @param value 属性值
     */
    public static void setProperty(Object bean, String propertyName, String propertyValue) {
        try {
            Field field = bean.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            field.set(bean, propertyValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static String getRequestParamValue(Map<String, String[]> requestParams, String paramName) {
        String[] values = requestParams.get(paramName);
        return values != null && values.length > 0 ? values[0] : null;
    }

    private static Object convert(Class<?> paramType, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        if (paramType == String.class) {
            return value;
        } else if (paramType == Integer.class || paramType == int.class) {
            return Integer.parseInt(value);
        } else if (paramType == Long.class || paramType == long.class) {
            return Long.parseLong(value);
        } else if (paramType == Double.class || paramType == double.class) {
            return Double.parseDouble(value);
        } else if (paramType == Float.class || paramType == float.class) {
            return Float.parseFloat(value);
        } else if (paramType == java.sql.Date.class) {
            // 将字符串转换为 java.util.Date，然后转换为 java.sql.Date
            try {
                java.util.Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(value);
                return new java.sql.Date(parsedDate.getTime());
            } catch (ParseException e) {
                throw new RuntimeException("Error parsing date", e);
            }
        } else {
            throw new IllegalArgumentException("Unsupported parameter type: " + paramType);
        }
    }


    private static Method findWriteMethod(Class<?> beanClass, String propertyName) {
        try {
            return beanClass.getMethod("set" + capitalize(propertyName), Object.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static String capitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}