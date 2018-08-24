package Yplat.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对api字段的描述 
 * 实现api列表用
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiDescribe {

	String describe() default "";//字段的中文描述
	
	int length() default 10;//字段的长度表述
	
	boolean required() default true;//字段是否必填
}
