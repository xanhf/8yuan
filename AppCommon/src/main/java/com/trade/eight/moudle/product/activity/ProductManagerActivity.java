package com.trade.eight.moudle.product.activity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.OptionalDao;
import com.trade.eight.entity.Exchange;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.moudle.baksource.BakSourceService;
import com.trade.eight.moudle.home.fragment.OptionalFragment;
import com.trade.eight.moudle.netty.NettyUtil;
import com.trade.eight.moudle.optional.OptHelper;
import com.trade.eight.moudle.product.adapter.DragAdapter;
import com.trade.eight.moudle.product.adapter.OtherAdapter;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.OptionalService;
import com.trade.eight.tools.Log;
import com.trade.eight.view.AppTitleView;
import com.trade.eight.view.DragGrid;
import com.trade.eight.view.OtherGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by fangzhu on 2015/5/12.
 */
public class ProductManagerActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    ProductManagerActivity context = null;
    String TAG = "ProductManagerActivity";
    AppTitleView title_bar;
    /** 用户栏目的GRIDVIEW */
    private DragGrid userGridView;
    /** 其它栏目的GRIDVIEW */
    private OtherGridView otherGridView;
    /** 用户栏目对应的适配器，可以拖动 */
    DragAdapter userAdapter;
    /** 其它栏目对应的适配器 */
    OtherAdapter otherAdapter;
    /** 用户栏目列表 */
    List<Optional> userChannelList = new ArrayList<Optional>();
    /** 其它栏目列表 */
    List<Optional> otherChannelList = new ArrayList<Optional>();

    OptionalDao optionalDao = new OptionalDao(this);

    /** 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。 */
    boolean isMove = false;

    /** 封装type交易所 */
    RecyclerView recyclerView = null;
    /** recyclerView 必须设置 LinearLayoutManager*/
    LinearLayoutManager linearLayoutManager = null;
    /**当前交易所*/
    int selectSource = 0;

    List<Exchange> exchangeList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.product_manager);
        try {
            initView();
            initData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initView() {

        title_bar = (AppTitleView) findViewById(R.id.title_bar);
        title_bar.setBaseActivity(ProductManagerActivity.this);
        title_bar.setAppCommTitle("添加自选");

        userGridView = (DragGrid) findViewById(R.id.userGridView);
        otherGridView = (OtherGridView) findViewById(R.id.otherGridView);



        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
    void initData() {
        userChannelList = optionalDao.queryAllMyOptionals();

        setData();
        getAllProducts();
    }

    void setData() {
        userAdapter = new DragAdapter(this, userChannelList);
        userGridView.setStartEnableIndex(-1);//开始允许编辑的位置；包含index  所有的均可编辑
        userAdapter.setStartEnableIndex(-1);
        userGridView.setAdapter(userAdapter);
        otherAdapter = new OtherAdapter(this, otherChannelList);
        otherGridView.setAdapter(otherAdapter);
        // 设置GRIDVIEW的ITEM的点击监听
        otherGridView.setOnItemClickListener(this);
        userGridView.setOnItemClickListener(this);

    }

    /** GRIDVIEW对应的ITEM点击监听接口 */
    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
        // 如果点击的时候，之前动画还没结束，那么就让点击事件无效
        if (isMove) {
            return;
        }
        int i = parent.getId();
        if (i == R.id.userGridView) {
        // position为 0，1, 2 的不可以进行任何操作
            if (position > userAdapter.getStartEnableIndex()) {
                //先判断是否为同一个交易所
                final Optional optional = ((DragAdapter) parent.getAdapter())
                        .getItem(position);
                if ( selectSource==position) {
                    //不是同一交易所， 不需要动画
                    userAdapter.setVisible(true);
                    userAdapter.setRemove(position);
                    userAdapter.remove();

                    //删除当前对象在本地数据库自选的记录
                    optionalDao.deleteOptional(optional);
                    return;
                }

                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final Optional channel = ((DragAdapter) parent.getAdapter())
                            .getItem(position);
                    otherAdapter.setVisible(false);
                    // 添加到最后一个
                    otherAdapter.addItem(channel);

                    //删除当前对象在本地数据库自选的记录
                    optionalDao.deleteOptional(channel);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                // 获取终点的坐标
                                otherGridView
                                        .getChildAt(otherGridView.getLastVisiblePosition())
                                        .getLocationInWindow(endLocation);
                                moveAnim(moveImageView, startLocation, endLocation, channel,
                                        userGridView);
                                userAdapter.setRemove(position);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 50L);
                }
            }

        } else if (i == R.id.otherGridView) {
            final ImageView moveImageView = getView(view);
            if (moveImageView != null) {
                TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                final int[] startLocation = new int[2];
                newTextView.getLocationInWindow(startLocation);
                final Optional channel = ((OtherAdapter) parent.getAdapter())
                        .getItem(position);
                userAdapter.setVisible(false);
                // 添加到最后一个
                userAdapter.addItem(channel);

//                optionalDao.addOrUpdateOptional(channel);
                saveSQLData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int[] endLocation = new int[2];
                            // 获取终点的坐标
                            userGridView.getChildAt(userGridView.getLastVisiblePosition())
                                    .getLocationInWindow(endLocation);
                            moveAnim(moveImageView, startLocation, endLocation, channel,
                                    otherGridView);
                            otherAdapter.setRemove(position);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 50L);
            }

        } else {
        }
    }

    void saveSQLData () {
        if (userAdapter.isListChanged()) {
            List<Optional> list = userAdapter.getChannnelLst();
            for (int i = 0; i < list.size(); i ++) {
                Optional optional = list.get(i);
                optional.setDrag(list.size() - i);
                optional.setOptional(true);
                optionalDao.addOrUpdateOptional(optional);
                //if we dont save at first, we must save than uptate
            }
        }

    }

    /**
     * 点击ITEM移动动画
     *
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param moveChannel
     * @param clickGridView
     */
    private void moveAnim(View moveView, int[] startLocation, int[] endLocation,
                          final Optional moveChannel,
                          final GridView clickGridView) {
        // 将当前栏目增加到改变过的listview中 若栏目已经存在删除点，不存在添加进去

        int[] initLocation = new int[2];
        // 获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        // 得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        // 创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);// 动画时间
        // 动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);// 动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof DragGrid) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                } else {
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     *
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 获取点击的Item的对应View，
     *
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }


    /**
     * 获取当前类型数据
     * @param position
     */
    public void loadDataByTags(final int position) {

        List<Optional> list = exchangeList.get(position).getOptionalsList();
        if (list != null) {
            optionalDao.updateOptionalList(list);
        }
        //去掉自选简单的写法  加载成功会先写到本地  只需要取不是自选的
        List<Optional> finalList = new ArrayList<Optional>();
        for (Optional desOpt : list) {
            if (!desOpt.isOptional())
                finalList.add(desOpt);
        }
        otherAdapter.setListDate(finalList);
        otherAdapter.notifyDataSetChanged();



//        new AsyncTask<Void, Void, List<Optional>>() {
//            @Override
//            protected List<Optional> doInBackground(Void... params) {
//                try {
//                    return BakSourceService.getOptionals(ProductManagerActivity.this, NettyUtil.getCodesSpecial(exchangeList.get(position).getOptionalsList()));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected void onPostExecute(List<Optional> list) {
//                super.onPostExecute(list);
//                //去掉自选简单的写法  加载成功会先写到本地  只需要取不是自选的
//                List<Optional> finalList = new ArrayList<Optional>();
//                for (Optional desOpt : list) {
//                    if (!desOpt.isOptional())
//                        finalList.add(desOpt);
//                }
//                otherAdapter.setListDate(finalList);
//                otherAdapter.notifyDataSetChanged();
//            }
//        }.execute();
    }

    private void getAllProducts() {
        final String url = AndroidAPIConfig.URL_TAB_GETALL_PRODUCT;
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return HttpClientHelper.getStringFromPost(ProductManagerActivity.this, url, null);
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                try {
                    if (!TextUtils.isEmpty(response)) {
                        JSONObject resJson = new JSONObject(response);
                        if (resJson.has("data")) {
                            JSONObject data = resJson.getJSONObject("data");
                            if (data.has("list")) {
                                JSONArray list = data.getJSONArray("list");
                                Type type = new TypeToken<ArrayList<Exchange>>() {}.getType();
                                exchangeList = (List<Exchange>) new Gson().fromJson(list.toString(), type);
                                if (exchangeList != null && exchangeList.size() > 0) {
                                    recyclerView.setVisibility(View.VISIBLE);

                                    MyAdapter myAdapter = new MyAdapter(exchangeList);
                                    recyclerView.setAdapter(myAdapter);
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                    recyclerView.setItemViewCacheSize(myAdapter.getItemCount());
                                    loadDataByTags(selectSource);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
       /* HttpClientHelper.doPostOption(this,
                url,
                null,
                null,
                new NetCallback(this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                    }

                    @Override
                    public void onResponse(String response) {

                        CommonResponse4List<Exchange> commonResponse = CommonResponse4List.fromJson(response, Exchange.class);
                        exchangeList = commonResponse.getData();
                        if (exchangeList != null && exchangeList.size() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);

                            MyAdapter myAdapter = new MyAdapter(exchangeList);
                            recyclerView.setAdapter(myAdapter);
                            recyclerView.getAdapter().notifyDataSetChanged();
                            recyclerView.setItemViewCacheSize(myAdapter.getItemCount());
                            loadDataByTags(selectSource);

                        }
                    }
                },
                false);*/
    }

    int selectPos = 0;
    View selectTextView = null;
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        List<Exchange> goodsList;

        public MyAdapter(List<Exchange> goodsList) {
            this.goodsList = goodsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View rootView = View.inflate(viewGroup.getContext(), R.layout.product_goods_item, null);
            return new MyViewHolder(rootView);
        }

        public void clear () {
            if (null != goodsList) {
                goodsList.clear();
                notifyDataSetChanged();
            }

        }
        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
            if (myViewHolder.textView == null)
                return;
            if (selectTextView == null) {
                selectTextView = myViewHolder.textView;
                selectTextView.setSelected(true);
            } else {
                myViewHolder.textView.setSelected(false);
            }
            Log.v(TAG, "onBindViewHolder--" + i);
            final int position = i;
            myViewHolder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectTextView != null)
                        selectTextView.setSelected(false);
                    selectTextView = v;
                    selectPos = position;
                    selectSource = position;
                    if (selectTextView != null)
                        selectTextView.setSelected(true);
                    recyclerView.scrollToPosition(position);
                    loadDataByTags(selectSource);
                }
            });

            myViewHolder.textView.setText(goodsList.get(i).getExchangeName());
        }

        @Override
        public int getItemCount() {
            return goodsList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textView;
            public MyViewHolder(View itemView) {
                super(itemView);
                textView = (TextView)itemView.findViewById(R.id.item_name);
            }
        }
    }

    @Override
    public void finish() {
        saveSQLData();
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
