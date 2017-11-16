package com.trade.eight.tools.trade;

import com.trade.eight.entity.Optional;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.tools.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fangzhu on 16/11/3.
 * 交易部分 客户端自已定排序处理
 */
public class TradeSortUtil {
    /**
     * 订单list 分组处理
     *
     * @param list
     * @return 已经分组的  油 银 咖啡
     */
    public static List<List<TradeOrder>> sortOrderInGroup(List<TradeOrder> list) {
        //最外层的list
        List<List<TradeOrder>> mainList = new ArrayList<>();
        //当前类别 所有的品种
        List<TradeOrder> detailList = new ArrayList<>();
        //重新整理成list
        if (list != null && list.size() > 0) {
            Map<String, String> filterIds = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                TradeOrder product = list.get(i);
                String contract = product.getInstrumentName();
                if (StringUtil.isEmpty(contract))
                    continue;
                if (filterIds.containsKey(contract)) {
                    continue;
                }
                detailList = new ArrayList<>();
                mainList.add(detailList);
                filterIds.put(contract, contract);
                for (int j = 0; j < list.size(); j++) {
                    if (contract.equals(list.get(j).getInstrumentName())) {
                        detailList.add(list.get(j));
                    }
                }
//                Collections.sort(detailList, new Comparator<TradeOrder>() {
//                    @Override
//                    public int compare(TradeOrder tradeProduct, TradeOrder t1) {
//                        try {
//                            //价格升序排序 接口不统一 没有这个字段
//                            return (int) (Double.parseDouble(tradeProduct.getProductPrice()) - Double.parseDouble(t1.getProductPrice()));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        return -1;
//                    }
//                });
            }
        }
        return TradeSortUtil.sortOrder(mainList);
    }

    /**
     * 持仓订单 客户端排序处理
     *
     * @param mainList 已经分组的
     * @return 分组返回  油 银 咖啡
     */
    public static List<List<TradeOrder>> sortOrder(List<List<TradeOrder>> mainList) {
        //排序 油 银 咖啡
        List<List<TradeOrder>> sortlist = new ArrayList<>();
        if (mainList == null)
            return sortlist;
        List<List<TradeOrder>> leftlist = new ArrayList<>();
        int size = mainList.size();
        for (int i = 0; i < size; i++) {
            //包含文字
            if (mainList.get(i).get(0).getProductName().contains("油")) {
                sortlist.add(0, mainList.get(i));
            } else if (mainList.get(i).get(0).getProductName().contains("银")) {
                sortlist.add(mainList.get(i));
            } else {
                leftlist.add(mainList.get(i));
            }
        }
        sortlist.addAll(leftlist);
        return sortlist;
    }

    /**
     * 排序行情
     *
     * @param list
     * @return
     */
    public static List<Optional> sortQB(List<Optional> list) {
        //排序 油 银 咖啡
        List<Optional> sortlist = new ArrayList<>();
        if (list == null)
            return sortlist;
        List<Optional> leftlist = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            //包含文字
            if (list.get(i).getTitle().contains("油")
                    || list.get(i).getTitle().contains("尿素")) {
                sortlist.add(0, list.get(i));
            } else if (list.get(i).getTitle().contains("银")) {
                sortlist.add(list.get(i));
            } else {
                leftlist.add(list.get(i));
            }
        }
        sortlist.addAll(leftlist);
        return sortlist;
    }

    /**
     * 交易大厅的数据
     * @param list
     * @return  油银咖啡
     */
    public static List<List<TradeProduct>> sortProducts(List<TradeProduct> list) {
        if (list == null)
            return null;

        //最外层的list
        List<List<TradeProduct>> mainList = new ArrayList<>();
        //当前类别 所有的品种
        List<TradeProduct> detailList = new ArrayList<>();
        //重新整理成list
        if (list != null && list.size() > 0) {
            Map<String, String> filterIds = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                TradeProduct product = list.get(i);
                String contract = product.getContract();
                if (StringUtil.isEmpty(contract))
                    continue;
                if (filterIds.containsKey(contract)) {
                    continue;
                }

                detailList = new ArrayList<>();
                mainList.add(detailList);
                filterIds.put(contract, contract);
                for (int j = 0; j < list.size(); j++) {
                    if (contract.equals(list.get(j).getContract())) {
                        detailList.add(list.get(j));
                    }
                }
                Collections.sort(detailList, new Comparator<TradeProduct>() {
                    @Override
                    public int compare(TradeProduct tradeProduct, TradeProduct t1) {
                        try {
                            //价格升序排序
                            return (int) (Double.parseDouble(tradeProduct.getPrice()) - Double.parseDouble(t1.getPrice()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return -1;
                    }
                });
            }
        }

        //排序成 油 银咖啡
        List<List<TradeProduct>> sortlist = new ArrayList<>();
        if (mainList == null)
            return sortlist;
        List<List<TradeProduct>> leftlist = new ArrayList<>();
        int size = mainList.size();
        for (int i = 0; i < size; i++) {
            //包含文字
            if (mainList.get(i).get(0).getName().contains("油")) {
                sortlist.add(0, mainList.get(i));
            } else if (mainList.get(i).get(0).getName().contains("银")) {
                sortlist.add(mainList.get(i));
            } else {
                leftlist.add(mainList.get(i));
            }
        }
        sortlist.addAll(leftlist);

        return sortlist;
    }
}
