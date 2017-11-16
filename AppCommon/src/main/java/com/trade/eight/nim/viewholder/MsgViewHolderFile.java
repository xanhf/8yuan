package com.trade.eight.nim.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.common.util.file.AttachmentStore;
import com.netease.nim.uikit.common.util.file.FileUtil;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.trade.eight.tools.IntentUtil;

/**
 * Created by zhoujianghua on 2015/8/6.
 */
public class MsgViewHolderFile extends MsgViewHolderBase {


    private ImageView fileIcon;
    private TextView fileNameLabel;
    private TextView fileStatusLabel;
    private ProgressBar progressBar;

    private FileAttachment msgAttachment;

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_file;
    }

    @Override
    protected void inflateContentView() {
        fileIcon = (ImageView) view.findViewById(R.id.message_item_file_icon_image);
        fileNameLabel = (TextView)view.findViewById(R.id.message_item_file_name_label);
        fileStatusLabel = (TextView)view.findViewById(R.id.message_item_file_status_label);
        progressBar = (ProgressBar) view.findViewById(R.id.message_item_file_transfer_progress_bar);
    }

    @Override
    protected void bindContentView() {
        msgAttachment = (FileAttachment) message.getAttachment();
        String path = msgAttachment.getPath();
        initDisplay();

        if (!TextUtils.isEmpty(path)) {
            loadImageView();
        } else {
            AttachStatusEnum status = message.getAttachStatus();
            switch (status) {
            case def:
                updateFileStatusLabel();
                break;
            case transferring:
                fileStatusLabel.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                int percent = (int) (getAdapter().getProgress (message) * 100);
                progressBar.setProgress(percent);
                break;
            case transferred:
            case fail:
                updateFileStatusLabel();
                break;
            }
        }
    }

    private void loadImageView() {
        fileStatusLabel.setVisibility(View.VISIBLE);
        // 文件长度
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.formatFileSize(msgAttachment.getSize()));
        fileStatusLabel.setText(sb.toString());

        progressBar.setVisibility(View.GONE);
    }

    private void initDisplay() {
        //各文件的显示图标
//        int iconResId = FileIcons.smallIcon(msgAttachment.getDisplayName());
        //check by fangzhu  简单显示 就是文件
        int iconResId = R.drawable.file_ic_detail_unknow;
        fileIcon.setImageResource(iconResId);
        fileNameLabel.setText(msgAttachment.getDisplayName());
    }

    private void updateFileStatusLabel() {
        fileStatusLabel.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        // 文件长度
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.formatFileSize(msgAttachment.getSize()));
        sb.append("  ");
        // 下载状态
        String path = msgAttachment.getPathForSave();
        if (AttachmentStore.isFileExist(path)) {
            sb.append(context.getString(R.string.file_transfer_state_downloaded));
        } else {
            sb.append(context.getString(R.string.file_transfer_state_undownload));
        }
        fileStatusLabel.setText(sb.toString());
    }

    @Override
    protected int leftBackground() {
        return R.drawable.nim_message_left_white_bg;
    }

    @Override
    protected int rightBackground() {
        return R.drawable.nim_message_right_blue_bg;
    }

    /**
     *  add by fangzhu
     *  添加显示文件的功能
     *  可以打开  可以下载
     */
    @Override
    protected void onItemClick() {
        try {
            String path = msgAttachment.getPathForSave();
            if (AttachmentStore.isFileExist(path)) {
                //已经下载了
                context.startActivity(IntentUtil.getIntent(path, msgAttachment.getDisplayName()));
            } else {
                //没有下载
                downloadAttachment();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    protected void downloadAttachment() {
        if (message.getAttachment() != null && message.getAttachment() instanceof FileAttachment)
            NIMClient.getService(MsgService.class).downloadAttachment(message, true);
    }
}
