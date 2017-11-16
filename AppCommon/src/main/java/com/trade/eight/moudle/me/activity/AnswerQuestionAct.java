package com.trade.eight.moudle.me.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.google.gson.Gson;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.MissionTaskDao;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.missioncenter.MissionTaskData;
import com.trade.eight.entity.missioncenter.QuestionData;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.view.CircleBlueBackgroundSpan;

import java.util.HashMap;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/28.
 * 答题中心
 */

public class AnswerQuestionAct extends BaseActivity implements View.OnClickListener {

    LinearLayout line_root;
    ImageView img_cache;
    TextView text_answerprogress;
    ProgressBar pr_answer;
    TextView text_question;
    LinearLayout line_answer_a;
    TextView text_answer_a;
    ImageView img_answer_a;
    LinearLayout line_answer_b;
    TextView text_answer_b;
    ImageView img_answer_b;
    LinearLayout line_explaine;
    TextView text_explaine;
    Button btn_next_question;

    private int answerStatus = ANSWERSTATUS_NORMAL;// 答题状态
    private static final int ANSWERSTATUS_NORMAL = 0;//初始状态
    private static final int ANSWERSTATUS_RIGHT = 1;//正确状态
    private static final int ANSWERSTATUS_ERROR = 2;//错误状态

    private int answerProgress = ANSWERPROGRESS_UNDO;// 答题进度
    private static final int ANSWERPROGRESS_UNDO = 0;//未答题
    private static final int ANSWERPROGRESS_DID = 1;//答题完成
    private static final int ANSWERPROGRESS_DOING = 2;//进行中

    MissionTaskData missionTaskData;
    private List<QuestionData> questionDataList;
    private QuestionData currentQuestionData;//当前问题

    private boolean isCommitCore = false;

    public static void startAct(Context context, MissionTaskData missionTaskData) {
        Intent intent = new Intent();
        intent.setClass(context, AnswerQuestionAct.class);
        intent.putExtra("missionTaskData", missionTaskData);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_answerquestion);
        initData();
        initView();
        getAQData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        missionTaskData = (MissionTaskData) getIntent().getSerializableExtra("missionTaskData");
        if (missionTaskData != null) {

            if (missionTaskData.getQueSucessNum() == 0) {
                answerProgress = ANSWERPROGRESS_UNDO;
            } else if (missionTaskData.getQueSucessNum() == missionTaskData.getQueTotalNum()) {
                answerProgress = ANSWERPROGRESS_DID;
                answerStatus = ANSWERSTATUS_RIGHT;
            } else {
                answerProgress = ANSWERPROGRESS_DOING;
            }
        }
    }

    private void initView() {
        setAppCommonTitle(missionTaskData.getTaskTitle());
        img_cache = (ImageView) findViewById(R.id.img_cache);
        line_root = (LinearLayout) findViewById(R.id.line_root);
        text_answerprogress = (TextView) findViewById(R.id.text_answerprogress);
        pr_answer = (ProgressBar) findViewById(R.id.pr_answer);
        text_question = (TextView) findViewById(R.id.text_question);
        line_answer_a = (LinearLayout) findViewById(R.id.line_answer_a);
        text_answer_a = (TextView) findViewById(R.id.text_answer_a);
        img_answer_a = (ImageView) findViewById(R.id.img_answer_a);
        line_answer_b = (LinearLayout) findViewById(R.id.line_answer_b);
        text_answer_b = (TextView) findViewById(R.id.text_answer_b);
        img_answer_b = (ImageView) findViewById(R.id.img_answer_b);
        line_explaine = (LinearLayout) findViewById(R.id.line_explaine);
        text_explaine = (TextView) findViewById(R.id.text_explaine);
        btn_next_question = (Button) findViewById(R.id.btn_next_question);
        // 已经答完了 只能浏览
        if (answerProgress != ANSWERPROGRESS_DID) {
            line_answer_a.setOnClickListener(this);
            line_answer_b.setOnClickListener(this);
        }
        btn_next_question.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.line_answer_a:
                judgeAnswer(1);
                break;
            case R.id.line_answer_b:
                judgeAnswer(2);
                break;
            case R.id.btn_next_question:
                switchQuestion();
                if (answerProgress != ANSWERPROGRESS_DID) {
                    answerStatus = ANSWERSTATUS_NORMAL;
                }
                break;
        }
    }

    /**
     * 切换题目
     */
    private void switchQuestion() {
        // 没有答完
        if (currentQuestionData.getQueSeqNum() != missionTaskData.getQueTotalNum()) {
            if (answerStatus == ANSWERSTATUS_ERROR) {//该题答错
                exitAnimation();
                displayView(questionDataList.get(currentQuestionData.getQueSeqNum() - 1));
            } else if (answerStatus == ANSWERSTATUS_RIGHT) {//该题答对
                nextQustionAnimation();
                displayView(questionDataList.get(currentQuestionData.getQueSeqNum()));
            }
        } else {//  最后一题  直接弹窗
            if (!isCommitCore && answerProgress != ANSWERPROGRESS_DID) {
                showNetLoadingProgressDialog("成绩提交中");
                return;
            }
            if (answerProgress != ANSWERPROGRESS_DID) {

                DialogUtil.showAQCompleteDlg(this,
                        new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message msg) {
                                AnswerQuestionAct.this.finish();
                                return false;
                            }
                        }, new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message msg) {
                                IntegralMarketActivity.startIntegralMarketActivity(AnswerQuestionAct.this, null);
                                return false;
                            }
                        });
            } else {
                AnswerQuestionAct.this.finish();
            }
        }
    }

    private void displayView(QuestionData questionData) {
        currentQuestionData = questionData;

//        if (answerProgress != ANSWERPROGRESS_DID || missionTaskData.getLocalqueSucessNum() == missionTaskData.getQueTotalNum()) {
        if (answerProgress != ANSWERPROGRESS_DID) {
            text_answerprogress.setText(currentQuestionData.getQueSeqNum() - 1 + "/" + missionTaskData.getQueTotalNum());
            pr_answer.setProgress(currentQuestionData.getQueSeqNum() - 1);
            if (missionTaskData.getLocalqueSucessNum() == missionTaskData.getQueTotalNum()) {
                text_answerprogress.setText((currentQuestionData.getQueSeqNum()) + "/" + missionTaskData.getQueTotalNum());
                pr_answer.setProgress(currentQuestionData.getQueSeqNum());
            }
        } else {
            text_answerprogress.setText((currentQuestionData.getQueSeqNum()) + "/" + missionTaskData.getQueTotalNum());
            pr_answer.setProgress(currentQuestionData.getQueSeqNum());
        }
        pr_answer.setMax(missionTaskData.getQueTotalNum());
        String displayTitle = getResources().getString(R.string.question_text, questionData.getQueSeqNum(), questionData.getQueDesc());
        SpannableString ss = new SpannableString(displayTitle);
        float textSize = 17 * getResources().getDisplayMetrics().scaledDensity; // sp to px
        ss.setSpan(new CircleBlueBackgroundSpan(this, textSize), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text_question.setText(ss);//题目
        text_answer_a.setText(questionData.getQueAnsList().get(0).getQueAnsDesc());
        text_answer_b.setText(questionData.getQueAnsList().get(1).getQueAnsDesc());
        text_explaine.setText(questionData.getQueExplain());// 解释
        line_answer_a.setBackgroundResource(R.drawable.bg_btn_blue);
        text_answer_a.setTextColor(getResources().getColor(R.color.sub_blue));
        img_answer_a.setVisibility(View.INVISIBLE);
        img_answer_a.setImageDrawable(getResources().getDrawable(R.drawable.img_answer_select));
        line_answer_b.setBackgroundResource(R.drawable.bg_btn_blue);
        text_answer_b.setTextColor(getResources().getColor(R.color.sub_blue));
        img_answer_b.setVisibility(View.INVISIBLE);
        img_answer_b.setImageDrawable(getResources().getDrawable(R.drawable.img_answer_select));
        line_explaine.setVisibility(View.GONE);
        btn_next_question.setVisibility(View.GONE);

        if (answerProgress != ANSWERPROGRESS_DID) {
            //答道最后一题退出  这个时候应该要去请求  直接提交成绩
            if (missionTaskData.getLocalqueSucessNum() == missionTaskData.getQueTotalNum()) {
                line_explaine.setVisibility(View.VISIBLE);
                btn_next_question.setVisibility(View.VISIBLE);
                btn_next_question.setText(getResources().getString(R.string.view_score));
                if (currentQuestionData.getQueAnsSeqNum() == 1) {

                    showAnswerState(
                            R.drawable.bg_btn_opt_lt_4,
                            R.color.color_opt_lt,
                            View.VISIBLE,
                            R.drawable.img_answer_select,
                            R.drawable.bg_btn_subbg,
                            R.color.grey,
                            View.INVISIBLE,
                            R.drawable.img_answer_select);

                } else {
                    showAnswerState(
                            R.drawable.bg_btn_subbg,
                            R.color.grey,
                            View.INVISIBLE,
                            R.drawable.img_answer_select,
                            R.drawable.bg_btn_opt_lt_4,
                            R.color.color_opt_lt,
                            View.VISIBLE,
                            R.drawable.img_answer_select
                    );
                }
            }
        } else {
            line_explaine.setVisibility(View.VISIBLE);
            btn_next_question.setVisibility(View.VISIBLE);
            if (currentQuestionData.getQueAnsSeqNum() == 1) {

                showAnswerState(
                        R.drawable.bg_btn_opt_lt_4,
                        R.color.color_opt_lt,
                        View.VISIBLE,
                        R.drawable.img_answer_select,
                        R.drawable.bg_btn_subbg,
                        R.color.grey,
                        View.INVISIBLE,
                        R.drawable.img_answer_select
                );
            } else {
                showAnswerState(
                        R.drawable.bg_btn_subbg,
                        R.color.grey,
                        View.INVISIBLE,
                        R.drawable.img_answer_select,
                        R.drawable.bg_btn_opt_lt_4,
                        R.color.color_opt_lt,
                        View.VISIBLE,
                        R.drawable.img_answer_select
                );
            }
            if (currentQuestionData.getQueSeqNum() == missionTaskData.getQueTotalNum()) {
                btn_next_question.setText(getResources().getString(R.string.aq_go_missioncenter));
            } else {
                btn_next_question.setText(getResources().getString(R.string.next_question));
                answerStatus = ANSWERSTATUS_RIGHT;
            }
        }
    }

    /**
     * 判断答案是否正确,改变界面
     */
    private void judgeAnswer(int queAnsSeqNum) {
        if (currentQuestionData.getQueAnsSeqNum() == queAnsSeqNum) {
            answerStatus = ANSWERSTATUS_RIGHT;
            //答对了就保存本地数据
            MissionTaskDao missionTaskDao = new MissionTaskDao(AnswerQuestionAct.this);
            missionTaskData.setLocalqueSucessNum(currentQuestionData.getQueSeqNum());
            missionTaskDao.addOrUpdate(missionTaskData);
            btn_next_question.setText(getResources().getString(R.string.next_question));
            text_answerprogress.setText(currentQuestionData.getQueSeqNum() + "/" + missionTaskData.getQueTotalNum());
            pr_answer.setProgress(currentQuestionData.getQueSeqNum());
            pr_answer.setMax(missionTaskData.getQueTotalNum());
            // 当达到最后一题时
            if (currentQuestionData.getQueSeqNum() == missionTaskData.getQueTotalNum()) {
                btn_next_question.setText(getResources().getString(R.string.view_score));
                //提交成績
                commitScore();
            }
        } else {
            answerStatus = ANSWERSTATUS_ERROR;
            btn_next_question.setText(getResources().getString(R.string.answer_again));
        }
        line_explaine.setVisibility(View.VISIBLE);
        btn_next_question.setVisibility(View.VISIBLE);
        if (answerStatus == ANSWERSTATUS_RIGHT) {
            if (currentQuestionData.getQueAnsSeqNum() == 1) {
                showAnswerState(
                        R.drawable.bg_btn_opt_lt_4,
                        R.color.color_opt_lt,
                        View.VISIBLE,
                        R.drawable.img_answer_select,
                        R.drawable.bg_btn_subbg,
                        R.color.grey,
                        View.INVISIBLE,
                        R.drawable.img_answer_select);
            } else {
                showAnswerState(
                        R.drawable.bg_btn_subbg,
                        R.color.grey,
                        View.INVISIBLE,
                        R.drawable.img_answer_select,
                        R.drawable.bg_btn_opt_lt_4,
                        R.color.color_opt_lt,
                        View.VISIBLE,
                        R.drawable.img_answer_select);

            }
        } else if (answerStatus == ANSWERSTATUS_ERROR) {
            if (currentQuestionData.getQueAnsSeqNum() == 1) {
                showAnswerState(R.drawable.bg_btn_opt_lt_4,
                        R.color.color_opt_lt,
                        View.VISIBLE,
                        R.drawable.img_answer_select,
                        R.drawable.bg_btn_opt_gt_4,
                        R.color.color_opt_gt,
                        View.VISIBLE,
                        R.drawable.img_answer_error);
            } else {
                showAnswerState(
                        R.drawable.bg_btn_opt_gt_4,
                        R.color.color_opt_gt,
                        View.VISIBLE,
                        R.drawable.img_answer_error,
                        R.drawable.bg_btn_opt_lt_4,
                        R.color.color_opt_lt,
                        View.VISIBLE,
                        R.drawable.img_answer_select);
            }
        }
        enterAnimation();
    }

    /**
     * 答案條目展示的狀態
     *
     * @param line_answer_a
     * @param text_answer_a
     * @param img_answer_a_visible
     * @param img_answer_a
     * @param line_answer_b
     * @param text_answer_b
     * @param img_answer_b_visible
     * @param img_answer_b
     */
    private void showAnswerState(int line_answer_a, int text_answer_a, int img_answer_a_visible, int img_answer_a, int line_answer_b, int text_answer_b, int img_answer_b_visible, int img_answer_b) {
        this.line_answer_a.setBackgroundResource(line_answer_a);
        this.text_answer_a.setTextColor(getResources().getColor(text_answer_a));
        this.img_answer_a.setImageDrawable(getResources().getDrawable(img_answer_a));
        this.img_answer_a.setVisibility(img_answer_a_visible);
        this.line_answer_b.setBackgroundResource(line_answer_b);
        this.text_answer_b.setTextColor(getResources().getColor(text_answer_b));
        this.img_answer_b.setVisibility(img_answer_b_visible);
        this.img_answer_b.setImageDrawable(getResources().getDrawable(img_answer_b));
    }

    @Override
    public void doMyfinish() {

        if (answerProgress != ANSWERPROGRESS_DID) {
            showExitDialog();
        } else {
            super.doMyfinish();
        }
    }

    /**
     * 是否确认退出
     */
    private void showExitDialog() {
        DialogUtil.showTitleAndContentDialog(this,
                getResources().getString(R.string.aq_exit_title),
                getResources().getString(R.string.aq_exit_content),
                getResources().getString(R.string.aq_exit_navbtnmsg),
                getResources().getString(R.string.aq_exit_posbtnmsg),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        AnswerQuestionAct.this.finish();
                        return false;
                    }
                }, new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        return false;
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void enterAnimation() {
        ObjectAnimator anim_line_explaine = ObjectAnimator.ofFloat(line_explaine, "alpha", 0f, 1.0f);
        ObjectAnimator anim_btn_next_question = ObjectAnimator.ofFloat(btn_next_question, "alpha", 0f, 1.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim_line_explaine).with(anim_btn_next_question);
        animSet.setDuration(1000);
        animSet.start();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void exitAnimation() {
        ObjectAnimator anim_line_explaine = ObjectAnimator.ofFloat(line_explaine, "alpha", 1.0f, 0f);
        ObjectAnimator anim_btn_next_question = ObjectAnimator.ofFloat(btn_next_question, "alpha", 1.0f, 0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim_line_explaine).with(anim_btn_next_question);
        animSet.setDuration(600);
        animSet.start();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void nextQustionAnimation() {

        line_root.setDrawingCacheEnabled(true);
        line_root.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        line_root.setDrawingCacheBackgroundColor(Color.WHITE);
        int w = line_root.getWidth();
        int h = line_root.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
//        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */
        line_root.layout(0, 0, w, h);
        line_root.draw(c);
        img_cache.setImageBitmap(bmp);
        img_cache.setVisibility(View.VISIBLE);
        ObjectAnimator anim_line_explaine = ObjectAnimator.ofFloat(img_cache, "translationX", 0f, -1080f);
        line_explaine.setVisibility(View.GONE);
        btn_next_question.setVisibility(View.GONE);
        ObjectAnimator anim_btn_next_question = ObjectAnimator.ofFloat(line_root, "translationX", 1080f, 0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim_line_explaine).with(anim_btn_next_question);
        animSet.setDuration(600);
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                img_cache.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

    }

    /**
     * 获取任务数据
     */
    private void getAQData() {
        // 从本地获取数据
        if (!TextUtils.isEmpty(missionTaskData.getQuestionData())) {
            CommonResponse4List<QuestionData> response4List = CommonResponse4List.fromJson(missionTaskData.getQuestionData(), QuestionData.class);
            questionDataList = response4List.getData();
            if (questionDataList != null && questionDataList.size() > 0) {

                if (answerProgress != ANSWERPROGRESS_DID) {
                    // 本地无记录
                    if (missionTaskData.getLocalqueSucessNum() == 0) {
                        displayView(questionDataList.get(missionTaskData.getLocalqueSucessNum()));
                    } else if (missionTaskData.getLocalqueSucessNum() < missionTaskData.getQueTotalNum()) {
                        displayView(questionDataList.get(missionTaskData.getLocalqueSucessNum()));
                    } else {
                        // 本地已经做完直接开始提交
                        displayView(questionDataList.get(missionTaskData.getLocalqueSucessNum() - 1));
                        commitScore();
                    }
                } else {
                    displayView(questionDataList.get(0));
                }
            }
            return;
        }

        HashMap<String, String> request = new HashMap<String, String>();
        UserInfoDao userInfoDao = new UserInfoDao(this);
        request.put("userId", userInfoDao.queryUserInfo().getUserId());
        request.put("taskId", missionTaskData.getTaskId() + "");
        HttpClientHelper.doPostOption(this, AndroidAPIConfig.URL_MISSIONCENTER_QUESTION_LIST, request, null, new NetCallback(this) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                showCusToast(resultMsg);
                AnswerQuestionAct.this.finish();
            }

            @Override
            public void onResponse(String response) {
                CommonResponse4List<QuestionData> commonResponse4List =  CommonResponse4List.fromJson(response,QuestionData.class);
                questionDataList = commonResponse4List.getData();
                if (questionDataList != null && questionDataList.size() > 0) {
//                    if (missionTaskData.getLocalqueSucessNum() == 0) {
//                        displayView(questionDataList.get(missionTaskData.getLocalqueSucessNum()));
//                    } else
                    // 本地记录没有答完
                    if (missionTaskData.getLocalqueSucessNum() < missionTaskData.getQueTotalNum()) {
                        displayView(questionDataList.get(missionTaskData.getLocalqueSucessNum()));
                    } else {
                        // 本地记录已经哒完
                        displayView(questionDataList.get(missionTaskData.getLocalqueSucessNum() - 1));
                    }
                }
                // 保存题目数据
                MissionTaskDao missionTaskDao = new MissionTaskDao(AnswerQuestionAct.this);
                missionTaskData.setQuestionData(new Gson().toJson(response));
                missionTaskDao.addOrUpdate(missionTaskData);
            }
        },true);
    }

    /**
     * 提交成績
     */
    private void commitScore() {
        HashMap<String, String> request = new HashMap<String, String>();
        UserInfoDao userInfoDao = new UserInfoDao(this);
        request.put("userId", userInfoDao.queryUserInfo().getUserId());
        request.put("taskId", missionTaskData.getTaskId() + "");
        request.put("queSeqNum", currentQuestionData.getQueSeqNum() + "");

        HttpClientHelper.doPostOption(this, AndroidAPIConfig.URL_MISSIONCENTER_QUESTIONSAVE, request, null, new NetCallback(this) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                showCusToast(resultMsg);
            }

            @Override
            public void onResponse(String response) {
                CommonResponse<TempObject> commonResponse = CommonResponse.fromJson(response,TempObject.class);
                if (commonResponse.success) {
                    isCommitCore = true;
                }
            }
        },true);

    }
}
