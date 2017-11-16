package com.trade.eight.service;

import android.content.Context;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.trade.eight.app.ServiceException;
import com.trade.eight.app.SystemContext;
import com.trade.eight.dao.OptionalDao;
import com.trade.eight.entity.Optional;
import com.trade.eight.moudle.exception.ErrorCodeConst;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.tools.Utils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class OptionalService  {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Context context;
    public OptionalService(Context context) {
//        super(context);
        this.context = context;
    }

    protected Object processResponseObj(Class classes, String respJsonStr)
            throws ServiceException {
        if (classes.getName().equals(String.class.getName())) {
            return respJsonStr;
        }
        if (respJsonStr == null || "".equals(respJsonStr))
            return null;
        if (respJsonStr != null) {
//			Log.i(classes.getName(), respJsonStr);
            if (respJsonStr.trim().startsWith("{")) {
                try {
                    return new Gson().fromJson(respJsonStr, classes);
                } catch (Exception e) {
                    // TODO: handle exception
                    return null;
                }
            } else {
                throw new ServiceException(
                        ErrorCodeConst.CODE_JSON_FORMAT_ERROR, "json解析异常");
            }
        } else
            return null;
    }

    protected String callGetApi(String apiUrl, Map<String, String> paraMap)
            throws ServiceException {
        if (!Utils.checkNetWork(context))
            throw new ServiceException(
                    ErrorCodeConst.CODE_CONNECT_NETWORK_FAIL, "网络连接失败，请检查你的网络");

        StringBuffer sb = new StringBuffer();
        int i = 0;
        if (paraMap != null) {
            for (Map.Entry<String, String> entry : paraMap.entrySet()) {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                if (i == 0)
                    sb.append("?");
                else
                    sb.append("&");
                sb.append(key + "=" + value);
                i++;
            }
        }
        return HttpClientHelper.getStringFromGet(context,apiUrl + sb.toString());
    }



    // 查出所有自选
    public List<Optional> queryAllMyOptional() {
        return new OptionalDao(context).queryAllMyOptionals();
    }

    public boolean updateDrag(List<Optional> list) {
        if (list == null || list.isEmpty())
            return false;
        int count = 0;
        int size = list.size();
        OptionalDao dao = new OptionalDao(context);
        for (int i = 0; i < list.size(); i++) {
            Optional optional = list.get(i);
            optional.setDrag(size - i);
            if (dao.updateOptional(optional)) {
                count++;
            }
        }
        return count == size;
    }


    public boolean updateTop(Optional op, int size) {
        if (op == null)
            return false;
        OptionalDao dao = new OptionalDao(context);
        op.setTop(size);
        return dao.updateOptional(op);
    }

    public boolean deleteOptionals(List<Optional> list) {
        return new OptionalDao(context).deleteOptionalList(list);
    }

    // 通过关键字查询 自选

    public List<Optional> queryOptionalsByKeyword(String keyword) {
        return new OptionalDao(context).queryOptionalsByKeyword(keyword);
    }

    public int getOptionalNumber() {
        return new OptionalDao(context).getIsOptionalNumber();
    }

    public Optional getOptionalBySymbol(String symbol) {
        return new OptionalDao(context).queryOptionalsBySymbol(symbol);
    }


    public boolean initDb() {
        SystemContext.getInstance(context).openDB();
        int count = -1;
        try {
            Dao<Optional, Integer> dao = SystemContext.getInstance(context)
                    .getDatabaseHelper().getOptionalDao();
            dao.queryBuilder().query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
//			System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return count > 0;
    }


}
