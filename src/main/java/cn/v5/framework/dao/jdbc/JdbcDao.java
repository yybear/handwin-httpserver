package cn.v5.framework.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;
import javax.swing.text.html.parser.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import cn.v5.framework.web.Page;

import com.google.common.collect.Collections2;

/** 
 * @author qgan
 * @version 2013年12月17日 上午10:51:48
 */
public class JdbcDao {
	private static final Logger LOG = LoggerFactory.getLogger(JdbcDao.class);
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		if (jdbcTemplate == null || dataSource != jdbcTemplate.getDataSource()) {
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
	}

	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	private RowMapper<Record> recordCallback = new RowMapper<Record>() {
        public Record mapRow(ResultSet rs, int rowNum) throws SQLException {
        	Record record = new Record();
        	ResultSetMetaData rsmd = rs.getMetaData();
        	int columnCount = rsmd.getColumnCount();
        	for(int i = 1; i <= columnCount; i++) {
        		Object value;
        		int type = rsmd.getColumnType(i);
        		if(type < Types.BLOB) {
        			value = rs.getObject(i);
        		} else if(type == Types.CLOB) {
        			value = DbHelper.handleClob(rs.getClob(i));
        		} else if(type == Types.NCLOB) {
        			value = DbHelper.handleClob(rs.getNClob(i));
        		} else if(type == Types.BLOB) {
        			value = DbHelper.handleBlob(rs.getBlob(i));
        		} else 
        			value = rs.getObject(i);
        		
        		record.set(rsmd.getColumnLabel(i), value);
        	}
            return record;
        }
    };
	
    /**
     * 自增id保存方式，最后返回id
     * @param tableName
     * @param record
     * @return
     */
	public long save(String tableName, Record record) {
		StringBuilder sqlBuilder = new StringBuilder("insert into `");
		sqlBuilder.append(tableName.trim());
		Map<String, Object> columns = record.getColumns();
		
		int i = 0;
		StringBuilder names = new StringBuilder("`(");
		StringBuilder values = new StringBuilder(") values (");
		final int size = columns.size();
		final Object[] params = new Object[size];
		for(Entry<String, Object> entity : columns.entrySet()) {
			if(i > 0) {
				names.append(",");
				values.append(",");
			}
			names.append("`").append(entity.getKey()).append("`");
			values.append("?");
			params[i] = entity.getValue();
			i++;
		}
		values.append(")");
		sqlBuilder.append(names).append(values);
		
		final String sql = sqlBuilder.toString();
		LOG.debug(sql);
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection conn)
                    throws SQLException {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                for(int i = 1; i <= size; i++) {
                	ps.setObject(i, params[i-1]);
                }
                return ps;
            }
        }, keyHolder);
		return keyHolder.getKey().longValue();
	}
	
	public int deleteById(String tableName, Object id) {
		return deleteById(tableName, "id", id);
	}
	
	public int deleteById(String tableName, String primaryKey, Object id) {
		StringBuilder sql = new StringBuilder("delete from `");
		sql.append(tableName.trim()).append("` where `").append(primaryKey).append("` = ?");
		
		return jdbcTemplate.update(sql.toString(), id);
	}
	
	public Record findById(String tableName, Object id) {
		return findById(tableName, "id", id);
	}
	
	public Record findById(String tableName, String primaryKey, Object id) {
		StringBuilder sql = new StringBuilder("select * from `").append(tableName).append("`").append(" where `").append(primaryKey).append("` = ?");
		Record r = jdbcTemplate.queryForObject(sql.toString(), recordCallback, id);
		
		return r;
	}
	
	public List<Record> find(String sql) {
		return jdbcTemplate.query(sql, recordCallback);
	}
	
	public List<Record> find(String sql, Object... args) {
		return jdbcTemplate.query(sql, args, recordCallback);
	}
	
	public Record findOne(String sql, Object... args) {
		List<Record> list = find(sql, args);
		if(CollectionUtils.isEmpty(list))
			return null;
		else 
			return list.get(0);
	}
	
	public int update(String tableName, Record record) {
		return update(tableName, "id", record);
	}
	
	public int update(String sql, Object... args) {
		return jdbcTemplate.update(sql, args);
	}
	
	public int update(String tableName, String primaryKey, Record record) {
		StringBuilder sql = new StringBuilder("update `").append(tableName).append("`").append(" set ");
		
		Map<String, Object> columns = record.getColumns();
		Set<Entry<String, Object>> entries = record.getColumns().entrySet();
		Object[] args = new Object[columns.size() + 1];
		int i = 0;
		for(Entry<String, Object> entity : entries) {
			if(i > 0)
				sql.append(",");
			sql.append("`").append(entity.getKey()).append("`=?");
			args[i] = entity.getValue();
			i++;
		}
		sql.append(" where `").append(primaryKey).append("` = ?");

		args[i] = record.get(primaryKey);
		return jdbcTemplate.update(sql.toString(), args);
	}
	
	private static final String SQL_COUNT = "select count(*) ";
	public Page<Record> paginate(long start, long limit,  String sqlSelect, String sqlWhere, Object... paras) {
		if(start < 1)
			start = 1;
		
		if(limit < 1)
			throw new RuntimeException("paginate, limit must > 0");
		
		Long totalRow = 0l;
		long totalPage = 0;
		
		totalRow = jdbcTemplate.queryForObject(SQL_COUNT + DbHelper.replaceFormatSqlOrderBy(sqlWhere), paras, Long.class);
		totalPage = (long) (totalRow / limit);
		if (totalRow % limit != 0) {
			totalPage++;
		}
		
		Object[] args = new Object[paras.length+2];
		for(int i = 0; i < paras.length; i++) {
			args[i] = paras[i];
		}
		args[paras.length] = (start -1) * limit;
		args[paras.length + 1] = limit;
		String sql = sqlSelect + " " + sqlWhere + " limit ?, ?";
		List<Record> list = find(sql, args);
		
		return new Page<Record>(list, start, limit, totalPage, totalRow);
	}
}
