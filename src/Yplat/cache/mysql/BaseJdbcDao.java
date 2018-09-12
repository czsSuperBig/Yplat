package Yplat.cache.mysql;

import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.alibaba.fastjson.JSONObject;

import Yplat.cache.mysql.bean.Page;

/**
 * <B>系统名称：</B>通用系统功能<BR>
 * <B>模块名称：</B>数据访问通用功能<BR>
 * <B>中文类名：</B>基础数据访问<BR>
 * <B>概要说明：</B><BR>
 * 
 * @author 交通运输部规划研究院（bhz）
 * @since 2013-10-28
 */

public class BaseJdbcDao<T> {
	
	private static final Logger log = LoggerFactory.getLogger(BaseJdbcDao.class);

    /** JSON数据行映射器 */
    private static final JsonRowMapper JSON_ROW_MAPPER = new JsonRowMapper();

    /** JDBC调用模板 */
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private Class<T> entityClass;
    
    @SuppressWarnings("unchecked")
    public BaseJdbcDao() {
    	ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
    	entityClass = (Class<T>) type.getActualTypeArguments()[0];
    }
    
    
    /**
     * <B>取得：</B>JDBC调用模板<BR>
     * 
     * @return JdbcTemplate JDBC调用模板
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }


    /**
     * <B>方法名称：</B>查询JSON列表<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param sql SQL语句
     * @param args 参数
     * @return List<JSONObject> JSON列表
     */
    public List<JSONObject> queryForJsonList(String sql, List<Object> args) {
    	log.info("=======数据库查询语句：{}=========",sql);
    	if (args != null) {
    		log.info("=======数据库查询参数：{}=========",args.toString());
		}
        return this.jdbcTemplate.query(sql, JSON_ROW_MAPPER, args.toArray());
    }
    
    /**
     * 返回的是泛型对应的类 
     */
    public List<T> queryTList(String sql, List<Object> args) {
    	RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
    	log.info("=======数据库查询语句：{}=========",sql);
    	if (args != null) {
    		log.info("=======数据库查询参数：{}=========",args.toString());
		}
        return this.jdbcTemplate.query(sql, rowMapper, args.toArray());
    }

    /**
     * <B>方法名称：</B>查询JSON数据<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param sql SQL语句
     * @param args 参数
     * @return JSONObject JSON数据
     */
    public JSONObject queryForJsonObject(String sql, List<Object> args) {
        List<JSONObject> jsonList = queryForJsonList(sql, args);
        if (jsonList == null || jsonList.size() < 1) {
            return null;
        }
        return jsonList.get(0);
    }
    
    /**
     * 返回的是泛型对应的类 
     */
    public T queryTObject(String sql, List<Object> args) {
        List<T> jsonList = queryTList(sql, args);
        if (jsonList == null || jsonList.size() < 1) {
            return null;
        }
        return jsonList.get(0);
    }

    /**
     * <B>方法名称：</B>查询文本<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param sql SQL语句
     * @param args 参数
     * @return String 文本
     */
    public String queryForString(String sql, List<Object> args) {
    	log.info("=======数据库查询语句：{}=========",sql);
    	log.info("=======数据库查询参数：{}=========",args.toString());
        List<String> dataList = this.jdbcTemplate.queryForList(sql, args.toArray(), String.class);
        if (dataList == null || dataList.size() < 1) {
            return null;
        }
        return dataList.get(0);
    }
    
    /**
     * 按照条件查询 
     */
    public List<JSONObject> queryJsonObjectByPara(String sqlPara,JSONObject para,String order){
    	StringBuffer sql = new StringBuffer(sqlPara);
    	List<Object> sqlArgs = new ArrayList<Object>();
        if (para != null) {
         	sql.append(" WHERE ");
         	Set<Entry<String, Object>> set = para.entrySet();
         	for (Iterator<Entry<String, Object>> iterator = set.iterator(); iterator.hasNext();) {
     			Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
     			sql.append(entry.getKey() + "=? AND ");
     			sqlArgs.add(entry.getValue());
     		}
            sql.delete(sql.length() - 4, sql.length());
 		}
        if (StringUtils.isNotEmpty(order)) {
        	sql.append(" ORDER BY ").append(order);
		}
        return queryForJsonList(sql.toString(), sqlArgs);
    }
    
    /**
     * 按照条件查询 
     */
    public List<T> queryTByPara(String sqlPara,JSONObject para,String order){
    	StringBuffer sql = new StringBuffer(sqlPara);
    	List<Object> sqlArgs = new ArrayList<Object>();
        if (para != null) {
         	sql.append(" WHERE ");
         	Set<Entry<String, Object>> set = para.entrySet();
         	for (Iterator<Entry<String, Object>> iterator = set.iterator(); iterator.hasNext();) {
     			Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
     			sql.append(entry.getKey() + "=? AND ");
     			sqlArgs.add(entry.getValue());
     		}
            sql.delete(sql.length() - 4, sql.length());
 		}
        if (StringUtils.isNotEmpty(order)) {
        	sql.append(" ORDER BY ").append(order);
		}
        return queryTList(sql.toString(), sqlArgs);
    }
    
    /**
     * 按照条件分页查询 
     */
    public Page<T> queryJsonObjectByPage(String sqlPara,JSONObject para,Page<T> page,String order,String tableName){
    	StringBuffer sql = new StringBuffer(sqlPara);
    	List<Object> sqlArgs = new ArrayList<Object>();
        if (para != null) {
         	sql.append(" WHERE ");
         	Set<Entry<String, Object>> set = para.entrySet();
         	for (Iterator<Entry<String, Object>> iterator = set.iterator(); iterator.hasNext();) {
     			Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
     			sql.append(entry.getKey() + "=? AND ");
     			sqlArgs.add(entry.getValue());
     		}
             sql.delete(sql.length() - 4, sql.length());
 		}
        if (StringUtils.isNotEmpty(order)) {
        	sql.append(" ORDER BY ").append(order);
		} 
        page.setTotalSize(queryObjCount(tableName, para));
        sql.append(" LIMIT ?,? ");
        sqlArgs.add(page.getStartSize());
        sqlArgs.add(page.getPageSize());
        page.setResultList(queryTList(sql.toString(), sqlArgs));
        return page;
    }
    
    
    
    /**
     * 查询记录数 
     */
    public int queryObjCount(String tableName,JSONObject para) {
    	StringBuffer sql = new StringBuffer();
        sql.append(" SELECT COUNT(1) FROM ").append(tableName);
        List<Object> sqlArgs = new ArrayList<Object>();
        if (para != null) {
        	sql.append(" WHERE ");
        	Set<Entry<String, Object>> set = para.entrySet();
        	for (Iterator<Entry<String, Object>> iterator = set.iterator(); iterator.hasNext();) {
    			Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
    			sql.append(entry.getKey() + "=? AND ");
    			sqlArgs.add(entry.getValue());
    		}
            sql.delete(sql.length() - 4, sql.length());
		}
        log.info("=======数据库查询语句：{}=========",sql);
        if (sqlArgs != null) {
    		log.info("=======数据库查询参数：{}=========",sqlArgs.toString());
		}
        int count = this.getJdbcTemplate().queryForObject(sql.toString(), sqlArgs.toArray(), Integer.class);
        return count;
    }

    /**
     * <B>方法名称：</B>拼接分页语句<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param sql SQL语句
     * @param start 开始记录行数（0开始）
     * @param limit 每页限制记录数
     */
    public void appendPageSql(StringBuffer sql, int start, int limit) {
        sql.insert(0, "SELECT * FROM (SELECT PAGE_VIEW.*, ROWNUM AS ROW_SEQ_NO FROM (");
        sql.append(") PAGE_VIEW WHERE ROWNUM <= " + (start + limit));
        sql.append(") WHERE ROW_SEQ_NO > " + start);
    }

    /**
     * <B>方法名称：</B>用于 in 通配符(?) 的拼接<BR>
     * <B>概要说明：</B>字段 in(?,?,?,?,?)<BR>
     * 
     * @param sql sql
     * @param sqlArgs 参数容器
     * @param params 参数的个数
     */
    public void appendSqlIn(StringBuffer sql, List<Object> sqlArgs, String[] params) {
        if (params != null && params.length > 0) {
            sql.append(" (");
            for (int i = 0; i < params.length; i++) {
                if (i == 0) {
                    sql.append("?");
                }
                else {
                    sql.append(",?");
                }
                sqlArgs.add(params[i]);
            }
            sql.append(") ");
        }
    }


    
    /**
     * <B>方法名称：</B>单表INSERT方法<BR>
     * <B>概要说明：</B>单表INSERT方法<BR>
     * @param tableName 表名
     * @param data JSONObject对象
     */
    protected int insert(String tableName, JSONObject data) {
        
    	if (data.size() <= 0) {
            return 0;
        }
    	
        StringBuffer sql = new StringBuffer();
        sql.append(" INSERT INTO ");
        sql.append(tableName + " ( ");
    	
    	Set<Entry<String, Object>> set = data.entrySet();
    	List<Object> sqlArgs = new ArrayList<Object>();
    	for (Iterator<Entry<String, Object>> iterator = set.iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
			sql.append(entry.getKey() + ",");
			sqlArgs.add(entry.getValue());
		}

        sql.delete(sql.length() - 1, sql.length());
        sql.append(" ) VALUES ( ");
        for (int i = 0; i < set.size(); i++) {
            sql.append("?,");
        }
        
        sql.delete(sql.length() - 1, sql.length());
        sql.append(" ) ");
        log.info("=======数据库新增语句：{}=========",sql);
        if (sqlArgs != null) {
        	log.info("=======数据库新增参数：{}=========",sqlArgs.toString());
		}
        return this.getJdbcTemplate().update(sql.toString(), sqlArgs.toArray()); 
    }
    
    
    /**
     * <B>方法名称：</B>批量新增數據方法<BR>
     * <B>概要说明：</B><BR>
     * @param tableName 数据库表名称
     * @param list 插入数据集合
     */
    protected void insertBatch(String tableName, final List<LinkedHashMap<String, Object>> list) {
        
        if (list.size() <= 0) {
            return;
        }
        
        LinkedHashMap<String, Object> linkedHashMap = list.get(0);
        
        StringBuffer sql = new StringBuffer();
        sql.append(" INSERT INTO ");
        sql.append(tableName + " ( ");
        
        final String[] keyset =  (String[]) linkedHashMap.keySet().toArray(new String[linkedHashMap.size()]);
        
        for (int i = 0; i < linkedHashMap.size(); i++) {
            sql.append(keyset[i] + ",");
        }
        
        sql.delete(sql.length() - 1, sql.length());
       
        sql.append(" ) VALUES ( ");
        for (int i = 0; i < linkedHashMap.size(); i++) {
            sql.append("?,");
        }
        
        sql.delete(sql.length() - 1, sql.length());
        sql.append(" ) ");
        
        this.getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                LinkedHashMap<String, Object>  map = list.get(i);
                Object[] valueset = map.values().toArray(new Object[map.size()]);
                int len = map.keySet().size();
                for (int j = 0; j < len; j++) {
                    ps.setObject(j + 1, valueset[j]);
                }
            }
            public int getBatchSize() {
                return list.size();
            }
      });
    } 
    
    /**
     * 更新操作
     */
    protected int update(String tableName, JSONObject filed,JSONObject condition) {
        StringBuffer sql = new StringBuffer();
        sql.append(" UPDATE ");
        sql.append(tableName + " SET ");
    	
    	Set<Entry<String, Object>> set = filed.entrySet();
    	List<Object> sqlArgs = new ArrayList<Object>();
    	for (Iterator<Entry<String, Object>> iterator = set.iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
			sql.append(entry.getKey() + "=?,");
			sqlArgs.add(entry.getValue());
		}

        sql.delete(sql.length() - 1, sql.length());
        if (condition != null) {
        	sql.append(" WHERE ");
        	Set<Entry<String, Object>> conditionSet = condition.entrySet();
        	for (Iterator<Entry<String, Object>> iterator = conditionSet.iterator(); iterator.hasNext();) {
    			Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
    			sql.append(entry.getKey() + "=? AND ");
    			sqlArgs.add(entry.getValue());
    		}
        	sql.delete(sql.length() - 4, sql.length());
		}
        log.info("=======数据库修改语句：{}=========",sql);
        if (sqlArgs != null) {
        	log.info("=======数据库修改参数：{}=========",sqlArgs.toString());
		}
        return this.getJdbcTemplate().update(sql.toString(), sqlArgs.toArray()); 
    }
    
    protected void updateBatch(String tableName, final List<LinkedHashMap<String, Object>> list,final List<LinkedHashMap<String, Object>> paraList) {
        
        if (list.size() <= 0) {
            return;
        }
        
        LinkedHashMap<String, Object> linkedHashMap = list.get(0);
        
        StringBuffer sql = new StringBuffer();
        sql.append(" UPDATE ");
        sql.append(tableName + " SET ");
        
        final String[] keyset =  (String[]) linkedHashMap.keySet().toArray(new String[linkedHashMap.size()]);
        
        for (int i = 0; i < linkedHashMap.size(); i++) {
            sql.append(keyset[i] + "=?,");
        }
        
        sql.delete(sql.length() - 1, sql.length());
        
        if (paraList != null && paraList.size() > 0) {
        	sql.append(" WHERE ");
        	LinkedHashMap<String, Object> paraLinkedHashMap = paraList.get(0);
        	final String[] paraKeyset =  (String[]) paraLinkedHashMap.keySet().toArray(new String[linkedHashMap.size()]);
        	for (int i = 0; i < paraLinkedHashMap.size(); i++) {
    			sql.append(paraKeyset[i] + "=? AND ");
    		}
        	sql.delete(sql.length() - 4, sql.length());
		}
        
        this.getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                LinkedHashMap<String, Object>  map = list.get(i);
                Object[] valueset = map.values().toArray(new Object[map.size()]);
                int len = map.keySet().size();
                for (int j = 0; j < len; j++) {
                    ps.setObject(j + 1, valueset[j]);
                }
                LinkedHashMap<String, Object> paraMap = paraList.get(i);
                Object[] paraValueset = paraMap.values().toArray(new Object[paraMap.size()]);
                int paralen = paraMap.keySet().size();
                for (int j = len; j < len+paralen; j++) {
                    ps.setObject(j + 1, paraValueset[j]);
                }
            }
            public int getBatchSize() {
                return list.size();
            }
      });
    }
    
    /**
     * 删除 
     */
    public int deletObj(String tableName,JSONObject para) {
    	StringBuffer sql = new StringBuffer();
        sql.append(" DELETE * FROM ").append(tableName);
        List<Object> sqlArgs = new ArrayList<Object>();
        if (para != null) {
        	sql.append(" WHERE ");
        	Set<Entry<String, Object>> set = para.entrySet();
        	for (Iterator<Entry<String, Object>> iterator = set.iterator(); iterator.hasNext();) {
    			Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
    			sql.append(entry.getKey() + "=? AND ");
    			sqlArgs.add(entry.getValue());
    		}
            sql.delete(sql.length() - 4, sql.length());
		}
        log.info("=======数据库删除语句：{}=========",sql);
        if (sqlArgs != null) {
        	log.info("=======数据库删除参数：{}=========",sqlArgs.toString());
		}
        return this.getJdbcTemplate().update(sql.toString(), sqlArgs.toArray());
    }
}
