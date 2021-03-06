package org.easyframework.easyadmin.dao;

import javax.annotation.Resource;

import org.easyframework.easyadmin.data.criterion.Restrictions;
import org.easyframework.easyadmin.data.jdbc.BaseDao;
import org.easyframework.easyadmin.po.UserPo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * 系统用户表数据访问实现类
 */
@Repository
public class UserDao extends BaseDao<UserPo> {

	public UserDao() {
		super(UserPo.EntityName, UserPo.UserId);
	}

	@Override
	@Resource
	public void setJDBCTemplate(JdbcTemplate membershipJdbcTemplate) {
		this.setJdbcTemplate(membershipJdbcTemplate);
	}

	public UserPo queryUser(String account, String password) {
		String[] columnNames = new String[] { UserPo.Account, UserPo.Password };
		Object[] args = new Object[] { account, password };
		String condition = String.format("WHERE  %1$s = ? AND %2$s = ? ", (Object[]) columnNames);
		return this.queryOne(condition, args, columnNames);
	}

	public UserPo queryByAccount(String account) {
		Object[] args = new Object[] { account };
		String condition = String.format("WHERE  %1$s = ? ", UserPo.Account);
		return this.queryOne(condition, args);
	}

	public boolean exists(String name) {
		String condition = Restrictions.equal(UserPo.UserId, "?").toString();
		return this.isExist(condition, new Object[] { name });
	}
}
