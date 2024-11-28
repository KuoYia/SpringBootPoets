package clover;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 定义一个名为TransactionalRead的注解，该注解可以应用于方法上
@Target({ElementType.METHOD})
// 表示该注解的生命周期为运行时，即在运行时可以通过反射获取该注解的信息
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionalRead {
    // 注解内容可以根据实际需求定义，这里为空表示没有特定的属性
}
