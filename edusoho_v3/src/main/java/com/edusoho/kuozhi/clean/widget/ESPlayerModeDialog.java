package com.edusoho.kuozhi.clean.widget;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

public class ESPlayerModeDialog extends DialogFragment {

    private TextView                  mVideo;
    private TextView                  mAudio;
    private TextView                  mCancel;
    private DialogButtonClickListener mVideoListener;
    private DialogButtonClickListener mAudioListener;
    private DialogButtonClickListener mCancelListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public static ESPlayerModeDialog newInstance() {
        return new ESPlayerModeDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.es_audio_switch_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAudio = view.findViewById(R.id.tv_audio);
        mVideo = view.findViewById(R.id.tv_video);
        mCancel = view.findViewById(R.id.tv_cancel);

        mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoListener != null) {
                    mVideoListener.onClick(ESPlayerModeDialog.this);
                }
            }
        });
        mAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioListener.onClick(ESPlayerModeDialog.this);
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCancelListener.onClick(ESPlayerModeDialog.this);
            }
        });
    }

    public ESPlayerModeDialog setVideoClickListener(DialogButtonClickListener l) {
        this.mVideoListener = l;
        return this;
    }

    public ESPlayerModeDialog setAudioClickListener(DialogButtonClickListener l) {
        this.mAudioListener = l;
        return this;
    }

    public ESPlayerModeDialog setCancelClickListener(DialogButtonClickListener l) {
        this.mCancelListener = l;
        return this;
    }

    public interface DialogButtonClickListener {
        void onClick(DialogFragment dialog);
    }
}
