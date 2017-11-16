package com.trade.eight.view.password;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.StringUtil;

/**
 * Belong to the Project —— MyPayUI
 * Created by WangJ on 2015/11/25 15:39.
 */
public class PasswordView extends RelativeLayout implements View.OnClickListener {
    Context context;

    private String strPassword;     //输入的密码
    private TextView[] tvList;      //用数组保存6个TextView，为什么用数组？
    //因为就6个输入框不会变了，用数组内存申请固定空间，比List省空间（自己认为）
    private GridView gridView;    //用GrideView布局键盘，其实并不是真正的键盘，只是模拟键盘的功能
    private ArrayList<Map<String, String>> valueList;    //有人可能有疑问，为何这里不用数组了？
    //因为要用Adapter中适配，用数组不能往adapter中填充

    private int currentIndex = -1;    //用于记录当前输入密码格位置

    public TextView tv_title;
    public TextView tv_content;
    public Button btnNav;
    public Button btnPos;


    Banks selectedBank;
    Handler.Callback posCall;
    OnPasswordInputFinish onPasswordInputFinish;

    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = View.inflate(context, R.layout.layout_popup_bottom, null);

        valueList = new ArrayList<Map<String, String>>();
        tvList = new TextView[6];

        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        btnNav = (Button) view.findViewById(R.id.btnNav);
        btnPos = (Button) view.findViewById(R.id.btnPos);

        tvList[0] = (TextView) view.findViewById(R.id.tv_pass1);
        tvList[1] = (TextView) view.findViewById(R.id.tv_pass2);
        tvList[2] = (TextView) view.findViewById(R.id.tv_pass3);
        tvList[3] = (TextView) view.findViewById(R.id.tv_pass4);
        tvList[4] = (TextView) view.findViewById(R.id.tv_pass5);
        tvList[5] = (TextView) view.findViewById(R.id.tv_pass6);

        gridView = (GridView) view.findViewById(R.id.gv_keybord);

        setView();

        addView(view);      //必须要，不然不显示控件

        btnNav.setOnClickListener(this);
        btnPos.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.btnNav){
            onPasswordInputFinish.onCancle();
        }else if(id==R.id.btnPos){
            String pwd = getStrPassword();
            if(TextUtils.isEmpty(pwd)||pwd.length()!=6){
                return;
            }
            if(posCall!=null){
                Message message = new Message();
                message.obj = pwd;
                posCall.handleMessage(message);
            }
            onPasswordInputFinish.onCancle();
        }
    }

    private void setView() {
        /* 初始化按钮上应该显示的数字 */
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<String, String>();
            if (i < 10) {
                map.put("name", String.valueOf(i));
            } else if (i == 10) {
                map.put("name", "完成");
            } else if (i == 11) {
                map.put("name", String.valueOf(0));
            } else if (i == 12) {
                map.put("name", "");
            }
            valueList.add(map);
        }

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 11 && position != 9) {    //点击0~9按钮
                    if (currentIndex >= -1 && currentIndex < 5) {      //判断输入位置————要小心数组越界
                        tvList[++currentIndex].setText(valueList.get(position).get("name"));
                    }
                } else {
                    if (position == 11) {      //点击退格键
                        if (currentIndex - 1 >= -1) {      //判断是否删除完毕————要小心数组越界
                            tvList[currentIndex--].setText("");
                        }
                    }
                    if(position==9){
                        String pwd = getStrPassword();
                        if(TextUtils.isEmpty(pwd)||pwd.length()!=6){
                            return;
                        }
                        if(posCall!=null){
                            Message message = new Message();
                            message.obj = pwd;
                            posCall.handleMessage(message);
                        }
                        onPasswordInputFinish.onCancle();
                    }
                }
            }
        });
    }

    //设置监听方法，在第6位输入完成后触发
    public void setOnFinishInput(final OnPasswordInputFinish pass) {
        onPasswordInputFinish = pass;
        tvList[5].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    strPassword = "";     //每次触发都要先将strPassword置空，再重新获取，避免由于输入删除再输入造成混乱
                    for (int i = 0; i < 6; i++) {
                        strPassword += tvList[i].getText().toString().trim();
                    }
                    pass.inputFinish();    //接口中要实现的方法，完成密码输入完成后的响应逻辑
                }
            }
        });
    }

    /* 获取输入的密码 */
    public String getStrPassword() {
        return strPassword;
    }


    //GrideView的适配器
    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return valueList.size();
        }

        @Override
        public Object getItem(int position) {
            return valueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_gride, null);
                viewHolder = new ViewHolder();
                viewHolder.btnKey = (TextView) convertView.findViewById(R.id.btn_keys);
                viewHolder.img_input_delete = (ImageView) convertView.findViewById(R.id.img_input_delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.btnKey.setText(valueList.get(position).get("name"));
            if (position != 9 && position != 11) {
                viewHolder.img_input_delete.setVisibility(GONE);
                TextPaint tp = viewHolder.btnKey.getPaint();
                tp.setFakeBoldText(true);
                viewHolder.btnKey.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            }
            if (position == 9) {
                viewHolder.btnKey.setBackgroundResource(R.drawable.selector_key_del);
                TextPaint tp = viewHolder.btnKey.getPaint();
                tp.setFakeBoldText(false);
                viewHolder.btnKey.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                viewHolder.img_input_delete.setVisibility(GONE);
            }
            if (position == 11) {
                viewHolder.btnKey.setBackgroundResource(R.drawable.selector_key_del);
                viewHolder.img_input_delete.setVisibility(VISIBLE);
                TextPaint tp = viewHolder.btnKey.getPaint();
                tp.setFakeBoldText(false);
                viewHolder.btnKey.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            }
            return convertView;
        }
    };

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView btnKey;
        public ImageView img_input_delete;
    }


    public void setSelectedBank(Banks selectedBank) {
        this.selectedBank = selectedBank;
        if(selectedBank==null){
            return;
        }
        tv_content.setText(context.getResources().getString(R.string.lable_cashin_bankmoney_tips,
                ConvertUtil.NVL(selectedBank.getBankName(),""),
                StringUtil.getHintCardNo(selectedBank.getBankAccount())));
    }


    public void setPosCall(Handler.Callback posCall) {
        this.posCall = posCall;
    }
}
