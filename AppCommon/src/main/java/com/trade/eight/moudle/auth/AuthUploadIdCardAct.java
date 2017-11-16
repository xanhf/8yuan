package com.trade.eight.moudle.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nim.uikit.common.media.picker.model.PhotoInfo;
import com.netease.nim.uikit.common.util.storage.StorageType;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.session.constant.Extras;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.moudle.auth.data.AuthDataHelp;
import com.trade.eight.moudle.auth.entity.CardAuthObj;
import com.trade.eight.moudle.auth.entity.IdCardObj;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.trade.activity.FXBTGCashInH5Act;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.UploadFileUtil;
import com.trade.eight.tools.refresh.RefreshUtil;
import com.trade.eight.view.picker.wheelPicker.picker.DatePicker;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dufangzhu on 2017/5/19.
 * 上传身份证
 */

public class AuthUploadIdCardAct extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "AuthUploadIdCardAct";
    public AuthUploadIdCardAct context = this;
    /*时间选择器开始年月日*/
    public static final int DATE_START_YEAR = 1900;
    public static final int DATE_START_MONTH = 1;
    public static final int DATE_START_DAY = 1;
    /*时间选择器结束年月日*/
    public static final int DATE_END_YEAR = 2047;
    public static final int DATE_END_MONTH = 1;
    public static final int DATE_END_DAY = 1;


    View statusView, contentView;
    View idCardView01, idCardView02, cardinfo01, cardinfo02,
            idcard01OK, idcard02OK, idcard_01bg, idcard_02bg,
            tv_idcard01, tv_idcard02, reload01, reload02, tv_rule;

    ImageView img_card01, img_card02, img_status;
    EditText ed_idno, ed_name;
    TextView tv_sex, tv_status01, tv_status02, tv_reload01, tv_reload02, tv_startdate, tv_enddate;
    Button btn_status_commit, btn_commit;

    RefreshUtil refreshUtil;

    TextView text_card_input_tips_1;
    TextView text_card_input_tips_2;


    public static void start(Context context) {
        context.startActivity(new Intent(context, AuthUploadIdCardAct.class));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_idcard);
        setAppCommonTitle(getResources().getString(R.string.me_account_auth));
        if (!new UserInfoDao(context).isLogin()) {
            LoginActivity.start(context);
            finish();
            return;
        }

        initViews();

        checkStatus();

        initRefresh();
    }

    void initRefresh() {
        refreshUtil = new RefreshUtil(this);
        refreshUtil.setRefreshTime(AppSetting.getInstance(this).getRefreshTimeWPList());
        refreshUtil.setOnRefreshListener(new RefreshUtil.OnRefreshListener() {
            @Override
            public Object doInBackground() {

                return AuthDataHelp.getCheckStatus(AuthUploadIdCardAct.this);
            }

            @Override
            public void onUpdate(Object result) {
                if (result != null) {
                    cardAuthObj = (CardAuthObj) result;
                    initStatus(cardAuthObj.getStatus());
                }
            }
        });
    }

    @Override
    protected void appCommonGoBack() {
        if (exitRegConfig()) {
            return;
        } else {
            doMyfinish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitRegConfig()) {
                return true;
            } else {
                doMyfinish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    boolean exitRegConfig() {
        if (cardAuthObj != null
                && cardAuthObj.getStatus() == CardAuthObj.STATUS_NOT_SUBMMIT) {
            String msg = context.getResources().getString(R.string.auth_exit_msg);
            String negStr = context.getResources().getString(R.string.auth_reg_neg);
            String postStr = context.getResources().getString(R.string.auth_reg_post);
            DialogUtil.showConfirmDlg(context, msg, negStr, postStr, true, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    doMyfinish();
                    return false;
                }
            }, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    return false;
                }
            });
            return true;
        }
        return false;
    }

    public void initViews() {
        statusView = findViewById(R.id.statusView);
        contentView = findViewById(R.id.contentView);
        idCardView01 = findViewById(R.id.idCardView01);
        idCardView02 = findViewById(R.id.idCardView02);
        img_card01 = (ImageView) findViewById(R.id.img_card01);
        img_card02 = (ImageView) findViewById(R.id.img_card02);

        tv_idcard01 = findViewById(R.id.tv_idcard01);
        tv_idcard02 = findViewById(R.id.tv_idcard02);


        reload01 = findViewById(R.id.reload01);
        reload02 = findViewById(R.id.reload02);

        idcard01OK = findViewById(R.id.idcard01OK);
        idcard02OK = findViewById(R.id.idcard02OK);


        idcard_01bg = findViewById(R.id.idcard_01bg);
        idcard_02bg = findViewById(R.id.idcard_02bg);

        img_status = (ImageView) findViewById(R.id.img_status);

        cardinfo01 = findViewById(R.id.cardinfo01);
        cardinfo02 = findViewById(R.id.cardinfo02);

        ed_idno = (EditText) findViewById(R.id.ed_idno);
        ed_name = (EditText) findViewById(R.id.ed_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);

        tv_reload01 = (TextView) findViewById(R.id.tv_reload01);
        tv_reload02 = (TextView) findViewById(R.id.tv_reload02);

        tv_reload01.setText(Html.fromHtml(getString(R.string.auth_upload_failed)));
        tv_reload02.setText(Html.fromHtml(getString(R.string.auth_upload_failed)));

        tv_startdate = (TextView) findViewById(R.id.tv_startdate);
        tv_enddate = (TextView) findViewById(R.id.tv_enddate);
        tv_startdate.setOnClickListener(this);
        tv_enddate.setOnClickListener(this);

        tv_status01 = (TextView) findViewById(R.id.tv_status01);
        tv_status02 = (TextView) findViewById(R.id.tv_status02);

        text_card_input_tips_1 = (TextView) findViewById(R.id.text_card_input_tips_1);
        text_card_input_tips_2 = (TextView) findViewById(R.id.text_card_input_tips_2);


        btn_status_commit = (Button) findViewById(R.id.btn_status_commit);
        idCardView01.setOnClickListener(this);
        idCardView02.setOnClickListener(this);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(this);
        btn_commit.setEnabled(false);

//        tv_sex.setOnClickListener(this);

        tv_rule = findViewById(R.id.tv_rule);
        tv_rule.setOnClickListener(this);

    }

    CardAuthObj cardAuthObj;

    public void checkStatus() {
        showNetLoadingProgressDialog(null);
        AuthDataHelp.checkStatus(context, new NetCallback(context) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                showCusToast(ConvertUtil.NVL(resultMsg, context.getResources().getString(R.string.network_problem)));
            }

            @Override
            public void onResponse(String response) {
                CommonResponse<CardAuthObj> commonResponse = CommonResponse.fromJson(response, CardAuthObj.class);
                if (commonResponse == null) {
                    showCusToast(context.getResources().getString(R.string.network_problem));
                    return;
                }
                cardAuthObj = commonResponse.getData();
                if (!commonResponse.isSuccess()
                        || cardAuthObj == null) {
                    showCusToast(ConvertUtil.NVL(commonResponse.getErrorInfo(), context.getResources().getString(R.string.network_problem)));
                    return;
                }

                if (commonResponse != null
                        && commonResponse.isSuccess()
                        && cardAuthObj != null) {
                    initStatus(cardAuthObj.getStatus());
                }
            }
        });
    }

    private static final int PICK_AVATAR_REQUEST = 0x0E;
    boolean isFront = true;
    //正面上传成功
    boolean isCard01OK = false;
    boolean isCard02OK = false;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_AVATAR_REQUEST) {
            String url = data.getStringExtra(com.netease.nim.uikit.session.constant.Extras.EXTRA_FILE_PATH);
            if (com.trade.eight.tools.StringUtil.isEmpty(url)) {
                List<PhotoInfo> list = (List<PhotoInfo>) data.getSerializableExtra(Extras.EXTRA_PHOTO_LISTS);
                if (list != null && list.size() > 0)
                    url = list.get(0).getAbsolutePath();
            }
            //updateAvatar
            if (url != null) {
                final String path = url;
                Log.v(TAG, "path=" + path);
                String api = AndroidAPIConfig.URL_AUTH_FRONT_CARD;
                if (!isFront) {
                    api = AndroidAPIConfig.URL_AUTH_BACK_CARD;
                }
                showNetLoadingProgressDialog(null);
                final String reqUrl = api;
                /*
                * 注意这在upload里面进行了图片压缩
                * */
                AuthDataHelp.upload(context, new File(path),
                        reqUrl,
                        new UploadFileUtil.LoadCallBack() {
                            @Override
                            public void hand(CommonResponse<IdCardObj> res, String localPath) {
                                hideNetLoadingProgressDialog();
                                if (res == null) {
                                    showCusToast(getString(R.string.network_problem));
                                    return;
                                }

                                if (res.isSuccess()
                                        && res.getData() != null) {
                                    //设置信息
                                    Log.v(TAG, "isFront=" + isFront);
                                    IdCardObj idCardObj = res.getData();
                                    if (isFront) {
                                        //正面信息成功
//                                        PickerlImageLoadTool.disPlay("file://" + path, new RotateImageViewAware(img_card01, path),
//                                                com.netease.nim.uikit.R.drawable.nim_image_default);
//                                        ImageLoader.getInstance().displayImage("file://" + path, img_card01);
                                        setImg(img_card01, localPath);
                                        text_card_input_tips_1.setVisibility(View.GONE);
                                        btn_commit.setEnabled(false);

                                        isCard01OK = true;

                                        String idNo = ConvertUtil.NVL(idCardObj.getIdNo(), "");
                                        String name = ConvertUtil.NVL(idCardObj.getName(), "");
                                        String sex = ConvertUtil.NVL(idCardObj.getSex(), "");

                                        //显示证件正面信息
                                        if (idCardObj != null) {
                                            ed_idno.setText(idNo);
                                            ed_idno.setSelection(ed_idno.getText().length());
                                            ed_name.setText(name);
                                            tv_sex.setText(sex);
                                        }
                                        cardinfo01.setVisibility(View.VISIBLE);

                                        idcard01OK.setVisibility(View.VISIBLE);
                                        idcard_01bg.setVisibility(View.INVISIBLE);


                                        if (TextUtils.isEmpty(idNo) || TextUtils.isEmpty(name) || TextUtils.isEmpty(sex)) {
                                            text_card_input_tips_1.setVisibility(View.VISIBLE);
                                            text_card_input_tips_1.setText(getResources().getString(R.string.card_input_tips_3));
                                            return;
                                        }
                                        if ((idNo.length() != 15 && idNo.length() != 18)) {
                                            text_card_input_tips_1.setVisibility(View.VISIBLE);
                                            text_card_input_tips_1.setText(getResources().getString(R.string.card_input_tips_1));
                                        }

                                    } else {
                                        text_card_input_tips_2.setVisibility(View.GONE);
                                        btn_commit.setEnabled(false);

                                        //反面信息成功
                                        isCard02OK = true;
//                                        ImageLoader.getInstance().displayImage("file://" + path, img_card02);
                                        setImg(img_card02, localPath);

                                        String startdate = ConvertUtil.NVL(idCardObj.getExpirationStart(), "");
                                        String enddate = ConvertUtil.NVL(idCardObj.getExpirationEnd(), "");

                                        //显示信息成功
                                        if (idCardObj != null) {
                                            tv_startdate.setText(startdate);
                                            tv_enddate.setText(enddate);
                                        }
                                        cardinfo02.setVisibility(View.VISIBLE);

                                        idcard02OK.setVisibility(View.VISIBLE);
                                        idcard_02bg.setVisibility(View.INVISIBLE);

                                        if (TextUtils.isEmpty(startdate) || TextUtils.isEmpty(enddate)) {
                                            text_card_input_tips_2.setVisibility(View.VISIBLE);
                                            text_card_input_tips_2.setText(getResources().getString(R.string.card_input_tips_3));
                                            return;
                                        }
                                        if (!TextUtils.isEmpty(enddate) && DateUtil.compareDateWithNow(enddate)) {
                                            text_card_input_tips_2.setVisibility(View.VISIBLE);
                                            text_card_input_tips_2.setText(getResources().getString(R.string.card_input_tips_2));
                                        }
                                    }
                                } else {
                                    String msgStr = ConvertUtil.NVL(res.getErrorInfo(), context.getResources().getString(R.string.network_problem));
                                    DialogUtil.showMsgDialog(context, msgStr, null);

                                    if (isFront) {
                                        tv_idcard01.setVisibility(View.GONE);
                                        reload01.setVisibility(View.VISIBLE);
                                    } else {
                                        tv_idcard02.setVisibility(View.GONE);
                                        reload02.setVisibility(View.VISIBLE);
                                    }
                                }
                                //都成功了就可以点击
                                if (isCard01OK && isCard02OK) {
//                                    int idNolength = ed_idno.getText().toString().length();
//                                    if ((idNolength == 15 || idNolength == 18)) {
//                                        String enDate = tv_enddate.getText().toString();
//                                        if (!TextUtils.isEmpty(enDate) && !DateUtil.compareDateWithNow(enDate)) {
//                                            btn_commit.setEnabled(true);
//                                        }
//                                    }
                                    if (text_card_input_tips_1.getVisibility() == View.GONE && text_card_input_tips_2.getVisibility() == View.GONE) {
                                        btn_commit.setEnabled(true);
                                    }
                                }
                                return;
                            }
                        });
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.idCardView01
                || id == R.id.idCardView02) {
//            if (!NetworkUtil.checkNetwork(context)) {
//                showCusToast(getString(R.string.network_problem));
//                return;
//            }
            if (id == R.id.idCardView01) {
                isFront = true;
                //图片的显示方向问题
//                /storage/emulated/0/DCIM/Camera/IMG_20170522_184829.jpg
//                String path = "/storage/emulated/0/DCIM/Camera/IMG_20170522_184829.jpg";
//                ImageLoader.getInstance().displayImage("file://" + path, img_card01);
//                return;
            } else {
                isFront = false;
            }


            PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
            option.titleResId = R.string.app_name;
            option.crop = false;
            option.multiSelect = false;
            //这里cropOutputImageWidth；当设置了crop=false之后就没用到了
            option.cropOutputImageWidth = 720;
            option.cropOutputImageHeight = 720;
            option.isUseDiyCamera = true;
            if (isFront) {
                option.diyCameraTips = getResources().getString(R.string.card_takephoto_tips_1);
            } else {
                option.diyCameraTips = getResources().getString(R.string.card_takephoto_tips_2);
            }
            option.outputPath = tempFile();
            PickImageHelper.pickImage(context, PICK_AVATAR_REQUEST, option);
        } else if (id == R.id.btn_commit) {
            DialogUtil.showConfirmDlg(context,
                    context.getResources().getString(R.string.auth_confirm),
                    context.getResources().getString(R.string.auth_confirm_cancle),
                    context.getResources().getString(R.string.auth_confirm_submit),
                    true,
                    null,
                    new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message message) {
                            submit();
                            return false;
                        }
                    });
        } else if (id == R.id.tv_sex) {
            final String[] sexChoose = {"男", "女"};
            int sel = 0;
            if (tv_sex.getText().toString().contains("女")) {
                sel = 1;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                builder = new AlertDialog.Builder(context, R.style.dialog_holo);
            }

            builder.setSingleChoiceItems(sexChoose, sel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    tv_sex.setText(sexChoose[which]);
                }
            }).create().show();
        } else if (id == R.id.tv_rule) {
            WebActivity.start(context, getString(R.string.auth_rule_title), AndroidAPIConfig.URL_AGRE_OPEN_ACCOUNT);
        } else if (id == R.id.tv_enddate) {
//            showDatePicker(tv_enddate);
        } else if (id == R.id.tv_startdate) {
//            showDatePicker(tv_startdate);
        }
    }

    /**
     * 时间选择器
     *
     * @param tv
     */
    private void showDatePicker(final TextView tv) {
        DatePicker picker = new DatePicker(this, DatePicker.YEAR_MONTH_DAY);
        picker.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        picker.setRangeStart(DATE_START_YEAR, DATE_START_MONTH, DATE_START_DAY);
        picker.setRangeEnd(DATE_END_YEAR, DATE_END_MONTH, DATE_END_DAY);
        picker.setSelectedItem(Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                if (year != null
                        && month != null
                        && day != null) {
                    tv.setText(year + "-" + month + "-" + day);
                }
            }
        });
        picker.show();
    }

    void submit() {
        String idno = ed_idno.getText().toString();
        String name = ed_name.getText().toString();
        String sex = tv_sex.getText().toString();
        String expiresStart = tv_startdate.getText().toString();
        String expiresEnd = tv_enddate.getText().toString();

        if (StringUtil.isEmpty(idno)) {
            showCusToast(getString(R.string.card_input_idno));
            return;
        }
        if (StringUtil.isEmpty(name)) {
            showCusToast(getString(R.string.card_input_name));
            return;
        }
        if (StringUtil.isEmpty(sex)) {
            showCusToast(getString(R.string.card_input_sex));
            return;
        }
        if (StringUtil.isEmpty(expiresStart)) {
            showCusToast(getString(R.string.card_input_startdate));
            return;
        }
        if (StringUtil.isEmpty(expiresEnd)) {
            showCusToast(getString(R.string.card_input_enddate));
            return;
        }
        //提交
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("idNo", idno);
        map.put("sex", sex);
        map.put("expiresStart", expiresStart);
        map.put("expiresEnd", expiresEnd);
        AuthDataHelp.submit(context, map, new NetCallback(context) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                showCusToast(ConvertUtil.NVL(resultMsg, context.getResources().getString(R.string.network_problem)));
            }

            @Override
            public void onResponse(String response) {
                CommonResponse<TempObject> commonResponse = CommonResponse.fromJson(response, TempObject.class);
                if (commonResponse == null) {
                    showCusToast(context.getResources().getString(R.string.network_problem));
                    return;
                }
                if (!commonResponse.isSuccess()) {
                    showCusToast(ConvertUtil.NVL(commonResponse.getErrorInfo(), context.getResources().getString(R.string.network_problem)));
                    return;
                }
                //成功之后 显示当前状态
                initStatus(CardAuthObj.STATUS_CHECKING);

            }
        });
    }

    public static final String JPG = ".jpg";

    private String tempFile() {
        String filename = StringUtil.get32UUID() + JPG;
        return StorageUtil.getWritePath(filename, StorageType.TYPE_TEMP);
    }

    void initStatus(int status) {
        //审核通过
        if (status == CardAuthObj.STATUS_SUCCESS) {
            contentView.setVisibility(View.GONE);
            statusView.setVisibility(View.VISIBLE);
            img_status.setImageResource(R.drawable.ic_card_status_ok);
            tv_status01.setText(getString(R.string.card_status_success));
            tv_status02.setText("");
            initStatusBtn(status);
        } else if (status == CardAuthObj.STATUS_CHECKING) {
            contentView.setVisibility(View.GONE);
            statusView.setVisibility(View.VISIBLE);
            img_status.setImageResource(R.drawable.ic_card_status_checking);
            tv_status01.setText(getString(R.string.card_status_checking));
            tv_status02.setText(getString(R.string.card_status_checking_hint));
            initStatusBtn(status);
            refreshUtil.start();
        } else if (status == CardAuthObj.STATUS_FAILED) {
            contentView.setVisibility(View.GONE);
            statusView.setVisibility(View.VISIBLE);
            img_status.setImageResource(R.drawable.ic_card_status_fail);
            tv_status01.setText(getString(R.string.card_status_failed));
            String msg = getString(R.string.card_status_failed_hint);
            if (cardAuthObj != null) {
                msg = ConvertUtil.NVL(cardAuthObj.getReturnMsg(), msg);
            }
            tv_status02.setText(msg);
            initStatusBtn(status);
        } else {
            //还没提交过资料
            contentView.setVisibility(View.VISIBLE);
            statusView.setVisibility(View.GONE);

        }
    }

    void initStatusBtn(final int status) {
        if (status == CardAuthObj.STATUS_SUCCESS) {
            btn_status_commit.setText(getString(R.string.btn_card_status_success));
        } else if (status == CardAuthObj.STATUS_CHECKING) {
            btn_status_commit.setText(getString(R.string.btn_card_status_checking));
        } else if (status == CardAuthObj.STATUS_FAILED) {
            btn_status_commit.setText(getString(R.string.btn_card_status_failed));
        }
        btn_status_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == CardAuthObj.STATUS_SUCCESS) {
                    //跳转到入金页面
                    FXBTGCashInH5Act.startCashin(context);
                    finish();
                } else if (status == CardAuthObj.STATUS_CHECKING) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    finish();

                } else if (status == CardAuthObj.STATUS_FAILED) {
                    //重新提交
                    contentView.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                }
            }
        });
    }

    void setImg(ImageView imgView, String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            if (bitmap.getWidth() < bitmap.getHeight()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            imgView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (refreshUtil != null) {
            refreshUtil.stop();
        }
    }
}
