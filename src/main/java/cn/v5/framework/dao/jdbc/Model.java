package cn.v5.framework.dao.jdbc;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import cn.v5.framework.ex.NotSupportMethodException;

import com.google.common.collect.Maps;

/**
 * 目前只支持约定操作，如果tableName为t_+类名的小写下划线驼峰，字段就是字段的小写下划线驼峰
 * 
 * @author qgan
 * @version 2013年12月17日 上午11:05:40
 */
@SuppressWarnings("rawtypes")
public class Model <M extends Model> implements Serializable {

	private static final long serialVersionUID = -3347963022597705403L;

	private JdbcTemplate template;

	/**
	 * 缓存的sql.
	 */
	private static Map<String, String> SQL_CACHE = Maps.newConcurrentMap();

	public Model() {
		super();
	}

	/**
	 * 设置spring jdbcTemplate，以便数据库操作
	 * 
	 * @param template
	 */
	public void setTemplate(JdbcTemplate template) {
		this.template = template;
	}

	public int save() {
		// 反射生成的效率有点低
		Class modelClazz = this.getClass();
		String tableName = DbHelper.getTableName(modelClazz.getSimpleName());
		StringBuilder sb = new StringBuilder("insert into `").append(tableName).append("` (");
		StringBuilder args = new StringBuilder();
		Field[] fields = modelClazz.getDeclaredFields();

		List<Object> list = new ArrayList<Object>();
		int i = 0;
		for (Field f : fields) {
			String fieldName = f.getName();
			if ("serialVersionUID".equals(fieldName))
				continue;
			Object fiedlValue = null;
			try {
				fiedlValue = f.get(this);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			list.add(fiedlValue);
			if (i > 0) {
				sb.append(",");
				args.append(",");
			}
			sb.append("`").append(DbHelper.getColumnName(fieldName))
					.append("`");
			args.append("?");
			i++;
		}
		sb.append(") values (").append(args).append(")");
		String sql = sb.toString();
		return this.template.update(sql, list.toArray());
	}

	public int delete(Long id) {
		Class modelClazz = this.getClass();
		String tableName = DbHelper.getTableName(modelClazz.getSimpleName());
		StringBuilder sb = new StringBuilder("delete from `").append(tableName).append("` where id=?");
		String sql = sb.toString();
		return this.template.update(sql, id);
	}

	/**
	 * 只更新非null值
	 * 
	 * @return
	 */
	public int update() {
		// 反射生成的效率有点低
		Class modelClazz = this.getClass();
		String tableName = DbHelper.getTableName(modelClazz.getSimpleName());
		StringBuilder sb = new StringBuilder("update `").append(tableName).append("` set ");
		Field[] fields = modelClazz.getDeclaredFields();

		List<Object> list = new ArrayList<Object>();
		Object id = null;
		int i = 0;
		for (Field f : fields) {
			String fieldName = f.getName();
			if ("serialVersionUID".equals(fieldName))
				continue;
			Object fiedlValue = null;
			try {
				fiedlValue = f.get(this);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if(null == fiedlValue) // null值不更新
				continue;
			
			if ("id".equals(fieldName)) { // 设置id
				id = fiedlValue;
				continue;
			}
			list.add(fiedlValue);
			if (i > 0) {
				sb.append(",");
			}
			sb.append("`").append(DbHelper.getColumnName(fieldName))
					.append("`=?");
			i++;
		}
		sb.append(" where id=?");
		String sql = sb.toString();
		list.add(id);
		return this.template.update(sql, list.toArray());
	}
	
	protected RowMapper<M> getBeanResultSetCallback() {
		throw new NotSupportMethodException("not support");
	}

	public List<M> find(String sql, Object... params) {
		return this.template.query(sql, params, getBeanResultSetCallback());
	}
	
	public M findOne(String sql, Object... params) {
		List<M> list = find(sql, params);
		if(CollectionUtils.isEmpty(list))
			return null;
		return list.get(0);
	}
}
