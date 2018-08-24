package Yplat.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

import Yplat.common.Const;


/**
 * Created by Administrator on 2018/1/20.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiMapping {

    String value();//接口的业务码，映射对应的逻辑方法用

    //在requst解析数据时执行
    boolean isRepeat() default false;//是否防重（No1）
    
    boolean checkSession() default true;//是否检测会话（No2）
    
    boolean isDataEncrypt() default false;//数据是否加密（No3）
    
    //在response返回数据时执行
    boolean isCrossDomain() default false;//是否进行跨域处理
    
    boolean createSession() default false;//是否创建会话，得到sessionid,保存在cookie中 需要在返回的model中有sessionModel方能保存
    
    boolean returnSession() default false;//是否返回会话到前端（一般是不允许把会话信息返回给前端的）
    
    String outPutType() default Const.RETURN_TYPE_JSON;//输出类型
    
    String freemarkerTempleName() default "";//若输出类型为html,则使用freemarker模板，此参数为需加载的freemarker模板名字
    
    String describe() default "";//对接口的描述
}
