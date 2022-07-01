package com.example.sudoku.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import com.example.sudoku.config.GameConfig;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable
{
    public SurfaceHolder mHolder;
    public Canvas mCanvas;
    public boolean mIsDrawing = true;//线程控制器

    @Override
    public void run()
    {
        GameConfig.time = -1;
        GameConfig.errorCount = 0;
        while (mIsDrawing)
        {
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            if (!mIsDrawing) break;
            GameConfig.time++;
            drawMap();
        }

    }

    /**
     * @Description 绘制整个游戏画面
     * @return: void
     **/
    public void drawMap()
    {
        mCanvas = mHolder.lockCanvas();
        mCanvas.drawColor(Color.WHITE);
        GameDraw.drawSelectedEffect(mCanvas);
        GameDraw.drawSelectedNumEffect(mCanvas);
        GameDraw.drawLines(mCanvas);
        GameDraw.drawButton(mCanvas);
        GameDraw.drawGameInfo(mCanvas);
        mHolder.unlockCanvasAndPost(mCanvas);
    }

    public MySurfaceView(Context context)
    {
        super(context);
        initView();
    }

    public MySurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView();
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView()
    {
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder)
    {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder)
    {

    }
}
