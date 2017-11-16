package com.trade.eight.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.trade.eight.app.SystemContext;
import com.trade.eight.entity.trude.UserInfo;

import java.sql.SQLException;
import java.util.List;

public class UserInfoDao extends BaseDao {

	private Context context;

	public UserInfoDao(Context context) {
		this.context = context;
	}

	public boolean addOrUpdate(UserInfo userInfo) {
		if (userInfo == null)
			return false;
		SystemContext.getInstance(context).openDB();
		int count = -1;
		try {
			Dao<UserInfo, Integer> dao = SystemContext
					.getInstance(context).getDatabaseHelper().getUserInfoDao() ;
//            List<UserInfo> list = dao.queryBuilder().where().eq(UserInfo.UID, userInfo.getUserId()).query();
			List<UserInfo> list = dao.queryBuilder().query();
            if (list != null && !list.isEmpty()) {
                UserInfo user = list.get(0);//保持主键不变
				userInfo.setLocalId(user.getLocalId());
                count = dao.update(userInfo);
            } else {
                count = dao.create(userInfo);
            }

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SystemContext.getInstance(context).closeDB();
		}
		return count > 0;
	}
	public UserInfo queryUserInfo() {
		List<UserInfo> list = queryAllUserInfo();
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public boolean deleteUserInfo() {
		SystemContext.getInstance(context).openDB();
		int count = -1;
		try {
			Dao<UserInfo, Integer> dao = SystemContext
					.getInstance(context).getDatabaseHelper().getUserInfoDao() ;
			List<UserInfo> list = dao.queryBuilder().query();
			if (list != null && !list.isEmpty()) {
				count = dao.delete(list.get(0));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SystemContext.getInstance(context).closeDB();
		}
		return count > 0;
	}

	public boolean isLogin () {
		return queryUserInfo() != null;
	}

	/**
	 *
	 * 多用户
	 * @param userInfo
	 * @return
	 */
    private boolean deleteUserInfo(UserInfo userInfo) {
		SystemContext.getInstance(context).openDB();
		int count = -1;
		try {
			Dao<UserInfo, Integer> dao = SystemContext
					.getInstance(context).getDatabaseHelper().getUserInfoDao() ;
            List<UserInfo> list = dao.queryBuilder().where().eq(UserInfo.UID, userInfo.getUserId()).query();
            if (list != null && !list.isEmpty()) {
//                Goods local = list.get(0);//保持主键不变
//                goods.setLocalId(local.getLocalId());
                count = dao.delete(userInfo);
            }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SystemContext.getInstance(context).closeDB();
		}
		return count > 0;
	}
	
	private List<UserInfo> queryAllUserInfo() {
		SystemContext.getInstance(context).openDB();
		List<UserInfo> list = null ;
		try {
			Dao<UserInfo, Integer> dao = SystemContext
					.getInstance(context).getDatabaseHelper().getUserInfoDao() ;
			list = dao.queryBuilder().query();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SystemContext.getInstance(context).closeDB();
		}
		return list ;
	}
	private void addOrUpdateList(List<UserInfo> list) {
		for (int i = 0; i < list.size(); i++) {
			addOrUpdate(list.get(i));
		}
	}


	/**
	 * 更新数据库的token
	 * @param context
	 * @param token
	 */
	public static void updateToken (Context context, String token) {
		try {
			if (new UserInfoDao(context).isLogin()) {
				//更新本地数据库的数据
				UserInfoDao dao = new UserInfoDao(context);
				UserInfo u = dao.queryUserInfo();
				u.setToken(token);
				dao.addOrUpdate(u);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




}
