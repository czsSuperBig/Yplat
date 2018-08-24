package Yplat.cache.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class SpringJdbcDao {

	private static final Logger logger = LoggerFactory.getLogger(SpringJdbcDao.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 返回单行数据 
	 */
	public JSONObject getSingleRowDate(String sql,Object[] args) {
		return jdbcTemplate.query(sql, args, new ResultSetExtractor<JSONObject>() {

			@Override
			public JSONObject extractData(ResultSet arg0) throws SQLException, DataAccessException {
				ResultSetMetaData rsd = arg0.getMetaData();
				int length = rsd.getColumnCount();
				try {
					if (arg0.next()) {
						JSONObject jo = new JSONObject();
						for(int i = 0;i < length;i++) {
							jo.put(rsd.getColumnLabel(i+1), arg0.getObject(i+1));
						}
						return jo;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				return null;
			}
			
		});
	}
	
	/**
	 * 返回数组集合 
	 */
	public JSONArray getJSONArray(String sql, Object[] params, final boolean toUpper) {

        logger.info("get sql:" + sql);
        if (params != null && params.length > 0) {
            for (Object o : params) {
                logger.info("value:" + o);
            }
        }

        return jdbcTemplate.query(sql, params, new ResultSetExtractor<JSONArray>() {
            @Override
            public JSONArray extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                ResultSetMetaData rsd = resultSet.getMetaData();
                int clength = rsd.getColumnCount();
                JSONArray ja = new JSONArray();
                String columnName;
                try {
                    while (resultSet.next()) {
                        JSONObject jo = new JSONObject();

                        for (int i = 0; i < clength; i++) {
                            columnName = rsd.getColumnLabel(i + 1);
                            columnName = toUpper ? columnName.toUpperCase() : columnName.toLowerCase();
                            jo.put(columnName, resultSet.getObject(i + 1));
                        }
                        ja.add(jo);
                    }
                } catch (Exception e) {

                }
                return ja;
            }
        });

    }

	 /**
     * 说明：插入数据
     *
     * @param sql
     * @param params
     * @return
     */
    public long insert(final String sql, final Object[] params) {
        logger.info("save sql:" + sql);
        if (params != null && params.length > 0) {
            for (Object o : params) {
                logger.info("value:" + o);
            }
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                if (params == null) {
                    return ps;
                }

                Object op = null;
                int alength = params.length;
                for (int i = 0; i < alength; ++i) {
                    op = params[i];
                    StatementCreatorUtils.setParameterValue(ps, i + 1, -2147483648, op);
                }
                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
    
    
}
