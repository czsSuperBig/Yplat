package Yplat.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Yplat.configManager.ConfigManager;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 新建freemarker自定义标签 
 */
public class LabelForUrl implements TemplateDirectiveModel {
	
	private static final Logger log = LoggerFactory.getLogger(LabelForUrl.class);
	
	private static final String appUrl = ConfigManager.getInstance().getSystemConfig().getString("app.url");
	
	@Override
	public void execute(Environment arg0, Map arg1, TemplateModel[] arg2, TemplateDirectiveBody arg3)
			throws TemplateException, IOException {
		// TODO Auto-generated method stub
		if (arg3 == null) {
			throw new RuntimeException("标签内部至少要加一个空格");
        }
		Writer out = arg0.getOut();
        
        //将模版里的数字参数转化成int类型的方法，，其它类型的转换请看freemarker文档
		String url = appUrl+arg1.get("src");
        log.info("=========得到<@url>结果:{}===========》",url);
        out.write(url);
        arg3.render(arg0.getOut());
        
	}

}
