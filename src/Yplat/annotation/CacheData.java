package Yplat.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对某个方法的执行的结果缓存，针对不经常更新的数据使用
 * 目的 利用缓存减少对 不经常更新的数据的查询
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheData {

	//缓存的key值
	String name() default "";
	
	//附加的查询条件
	String condition() default "";
	
	//缓存时间 默认600s
	int time() default 600;
	
	//是否增加缓存 默认 是  选择否的话 删除缓存
	boolean addCache() default true;
}
