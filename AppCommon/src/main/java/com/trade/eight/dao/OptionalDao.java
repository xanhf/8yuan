package com.trade.eight.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.trade.eight.app.SystemContext;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.moudle.baksource.BakSourceInterface;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;

import java.sql.SQLException;
import java.util.List;

public class OptionalDao extends BaseDao {

    private Context context;

    public OptionalDao(Context context) {
        this.context = context;
    }

    public boolean updateCount(Optional optional) {
        SystemContext.getInstance(context).openDB();
        int count = -1;
        try {
            Dao<Optional, Integer> dao = SystemContext.getInstance(context)
                    .getDatabaseHelper().getOptionalDao();
            count = dao.update(optional);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return count > 0;
    }

    public boolean updateFavLocal(Optional optional) {
        SystemContext.getInstance(context).openDB();
        int count = -1;
        List<Optional> list;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            if (StringUtil.isEmpty(optional.getType())) {
                list = dao.queryBuilder().where()
                        .eq("exchangeID", optional.getExchangeID()).query();
            } else {
                //按照交易所分
                list = dao.queryBuilder().where()
                        .eq("exchangeID", optional.getExchangeID()).and()
                        .eq("instrumentID", optional.getInstrumentID()).query();
            }

            if (list != null && !list.isEmpty()) {
                optional.setLocalId(list.get(0).getLocalId());
                optional.setTop(list.get(0).getTop());
                optional.setOptional(optional.isOptional());
                optional.setHostory(list.get(0).isHostory());
                if (list.get(0).getExchangeID() != null && list.get(0).getExchangeID().trim().length() > 0)
                    optional.setExchangeID(list.get(0).getExchangeID());
                optional.setDrag(list.get(0).getDrag());
                optional.setTop(list.get(0).getTop());

                count = dao.update(optional);
            } else { // 创建
                count = dao.create(optional);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return count > 0;
    }


    public boolean addOrUpdateOptional(Optional optional) {
        SystemContext.getInstance(context).openDB();
        int count = -1;
        List<Optional> list;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            if (StringUtil.isEmpty(optional.getExchangeID())) {
                list = dao.queryBuilder().where()
                        .eq("instrumentID", optional.getInstrumentID()).query();
            } else {
                list = dao.queryBuilder().where()
                        .eq("exchangeID", optional.getExchangeID()).and()
                        .eq("instrumentID", optional.getInstrumentID()).query();
            }

            if (list != null && !list.isEmpty()) {
                optional.setLocalId(list.get(0).getLocalId());
                optional.setOptional(list.get(0).isOptional());
                optional.setHostory(list.get(0).isHostory());
                optional.setExchangeID(list.get(0).getExchangeID());
                optional.setDrag(optional.getDrag());
                optional.setTop(list.get(0).getTop());
                optional.setOptional(true);
                count = dao.update(optional);
            } else { // 创建
                optional.setOptional(true);
                count = dao.create(optional);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return count > 0;
    }

    public boolean updateOptional(Optional optional) {
        SystemContext.getInstance(context).openDB();
        int count = -1;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            count = dao.update(optional);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return count > 0;
    }

    public boolean updateOptionalList(List<Optional> optionals) {
        if (optionals != null && !optionals.isEmpty()) {
            SystemContext.getInstance(context).openDB();
            int count = -1;
            int updateCount = 0;
            try {
                Dao<Optional, Integer> dao = SystemContext
                        .getInstance(context).getDatabaseHelper()
                        .getOptionalDao();

                for (int i = 0; i < optionals.size(); i++) {
                    List<Optional> list = null;
                    if (StringUtil.isEmpty(optionals.get(i).getType())) {
                        list = dao.queryBuilder().where()
                                .eq("instrumentID", optionals.get(i).getInstrumentID()).query();
                    } else {
                        list = dao.queryBuilder().where()
                                .eq("exchangeID", optionals.get(i).getExchangeID()).and()
                                .eq("instrumentID", optionals.get(i).getInstrumentID()).query();
                    }
                    if (list != null && !list.isEmpty()) {
                        optionals.get(i).setLocalId(list.get(0).getLocalId());
                        optionals.get(i).setOptional(list.get(0).isOptional());
                        optionals.get(i).setHostory(list.get(0).isHostory());
                        optionals.get(i).setExchangeID(list.get(0).getExchangeID());
                        optionals.get(i).setDrag(list.get(0).getDrag());
                        optionals.get(i).setTop(list.get(0).getTop());
                        count = dao.update(optionals.get(i));
                    } else { // 创建
                        count = dao.create(optionals.get(i));
                    }
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println(e.getMessage());
            } finally {
                SystemContext.getInstance(context).closeDB();
            }
            return updateCount == optionals.size();
        }
        return false;
    }

    public List<Optional> queryOptionals() {
        SystemContext.getInstance(context).openDB();
        List<Optional> list = null;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            list = dao.queryBuilder().orderBy("drag", false)
                    .orderBy("top", false).query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return list;
    }

    /**
     * 根据source 交易所的名称查找
     *
     * @param type
     * @return
     */
    public List<Optional> queryOptionalsByType(String type) {
        SystemContext.getInstance(context).openDB();
        List<Optional> list = null;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            list = dao.queryBuilder().orderBy("drag", false)
                    .orderBy("top", false).where().eq("exchangeID", type).query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return list;
    }

    /**
     * 根据source 交易所的名称查找自选的品种
     *
     * @param type
     * @return
     */
    public List<Optional> queryMyOptionalsByType(String type) {
        SystemContext.getInstance(context).openDB();
        List<Optional> list = null;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            list = dao.queryBuilder().orderBy("drag", false)
                    .orderBy("top", false).where().eq("optional", true).and().eq("type", type).query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return list;
    }

    /**
     * 所有自选的品种
     *
     * @return
     */
    public List<Optional> queryAllMyOptionals() {
        SystemContext.getInstance(context).openDB();
        List<Optional> list = null;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            list = dao.queryBuilder().orderBy("drag", false)
                    .orderBy("top", false).where().eq("optional", true)
                    .query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return list;
    }


    public boolean isExits(Optional optional) {
        SystemContext.getInstance(context).openDB();
        List<Optional> list = null;
        try {
            Dao<Optional, Integer> dao = SystemContext.getInstance(context).getDatabaseHelper().getOptionalDao();
            list = dao.queryBuilder().where().eq("treaty", optional.getTreaty()).query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return (list != null && !list.isEmpty());
    }

    /**
     * 清空表内容
     *
     * @return
     */
    public boolean dropAllOptional() {
        SystemContext.getInstance(context).openDB();
        int count = -1;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            count = dao.delete(dao.queryForAll());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return (count > 0);
    }


    public boolean deleteOptional(Optional optional) {
        if (optional == null)
            return false;
        SystemContext.getInstance(context).openDB();
        int count = -1;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            optional.setOptional(false);
            optional.setDrag(0);
            optional.setTop(0);
            count = dao.update(optional);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return (count > 0);
    }

    public boolean deleteOptionalList(List<Optional> optionals) {
        if (optionals == null || optionals.isEmpty())
            return false;

        SystemContext.getInstance(context).openDB();
        int count = -1;
        int size = 0;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            for (Optional hangqing_OPtional : optionals) {
                hangqing_OPtional.setOptional(false);
                hangqing_OPtional.setDrag(0);
                hangqing_OPtional.setTop(0);
                count = dao.update(hangqing_OPtional);
                if (count > 0)
                    size++;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return (size == optionals.size());
    }

    public List<Optional> queryOptionalsByKeyword(String keyword) {
        SystemContext.getInstance(context).openDB();
        List<Optional> list = null;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            list = dao.queryBuilder().orderBy("drag", false)
                    .orderBy("top", false).where()
                    .like("treaty", "%" + keyword + "%").or()
                    .like("title", "%" + keyword + "%").or()
                    .query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return list;
    }

    //  获取当前添加的自选股数量
    public int getIsOptionalNumber() {
        SystemContext.getInstance(context).openDB();
        List<Optional> list = null;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            list = dao.queryBuilder().where().eq("optional", true).query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return list == null ? 0 : list.size();
    }

    public Optional queryOptionalsBySymbol(String symbol) {
        SystemContext.getInstance(context).openDB();
        List<Optional> list = null;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            list = dao.queryBuilder().where()
                    .eq("treaty", symbol)
                    .query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    /**
     * code相同
     *  分交易所查处当前的品种信息
     * @param excode
     * @param symbol
     * @return
     */
    public Optional queryOptionalsBySymbol(String excode, String symbol) {
        SystemContext.getInstance(context).openDB();
        List<Optional> list = null;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            list = dao.queryBuilder().where()
                    .eq("exchangeID", excode).and()
                    .eq("instrumentID", symbol)
                    .query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }


    /**
     * 修改之前错的type
     *
     * @return
     */
    public boolean updateGGType() {
        SystemContext.getInstance(context).openDB();
        int count = -1;
        List<Optional> list;
        try {
            Dao<Optional, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getOptionalDao();
            list = dao.queryBuilder().where()
                    .eq("type", "WEIPAN").query();
            if (list != null && !list.isEmpty()) {
                for (Optional o : list) {
                    o.setType(BakSourceInterface.TRUDE_SOURCE_WEIPAN);
                    count = dao.update(o);
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return count > 0;
    }

    /**
     * 删除已经下架的产品的自选
     */
    public void deleteDisableP () {
        OptionalDao dao = new OptionalDao(context);
        //删掉之前的哈贵油的自选
        Optional o = dao.queryOptionalsBySymbol(TradeConfig.code_hg, TradeProduct.CODE_OIL);
        if (o != null) {
            dao.deleteOptional(o);
        }
        //吉银
        o = dao.queryOptionalsBySymbol(TradeConfig.code_jn, TradeProduct.CODE_JN_AG);
        if (o != null) {
            dao.deleteOptional(o);
        }
        //吉油
        o = dao.queryOptionalsBySymbol(TradeConfig.code_jn, TradeProduct.CODE_JN_CO);
        if (o != null) {
            dao.deleteOptional(o);
        }
    }

}
