package com.android.server.wm.sw;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class InfinityWindow {
    public static InfinityWindow main(Context context) {
        IWThread thread = new IWThread(context);
        thread.start();
        synchronized (thread) {
            while (thread.mWindow == null) {
                try {
                    thread.wait();
                } catch (InterruptedException e) {
                    
                }
            }
        }
        return thread.mWindow;
    }

    private static class IWThread extends Thread {
        private Context mContext = null;
        InfinityWindow mWindow = null;
        public IWThread(Context context) {
            mContext = context;
        }

        @Override
        public void run() {
            Looper.prepare();
            synchronized (this) {
                mWindow = new InfinityWindow(mContext);
                this.notify();
            }
            Looper.loop();
        }
    }

    public Context mContext = null;
    public WindowManager mWm = null;
    public Button mBtn = null;
    public static final int MSG_SHOW = 1;
    public static final int MSG_HIDE = 2;
    
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_SHOW:
                handleShow();
                break;
            case MSG_HIDE:
                handleHide();
                break;
            default:
                break;
            }
        }
    };

    public InfinityWindow(Context context) {
        mContext = context;
        mWm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void show() {
        mHandler.sendEmptyMessage(MSG_SHOW);
        Log.d("zdw", "infinity window showed!");
    }
    public void hide() {
        mHandler.sendEmptyMessage(MSG_HIDE);
        Log.d("zdw", "infinity window hidden!");
    }
    private void handleShow() {
        if (mBtn == null) {
            mBtn = new Button(mContext);
            mBtn.setText("Click to quit mult-window2");
            mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hide();
                }
            });
            WindowManager.LayoutParams attrs = new WindowManager.LayoutParams();
            attrs.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;
            attrs.gravity = Gravity.CENTER;
            attrs.width = WindowManager.LayoutParams.WRAP_CONTENT;
            attrs.height = WindowManager.LayoutParams.WRAP_CONTENT;
            attrs.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            mWm.addView(mBtn, attrs);
        }
    }

    private void handleHide() {
        if (mBtn != null) {
            mWm.removeView(mBtn);
            mBtn =  null;
        }
    }
}
