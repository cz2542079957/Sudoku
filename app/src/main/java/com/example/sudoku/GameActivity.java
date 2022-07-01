package com.example.sudoku;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.sudoku.config.GameConfig;
import com.example.sudoku.utils.GameDraw;
import com.example.sudoku.utils.MySurfaceView;
import com.example.sudoku.utils.Sudoku;

public class GameActivity extends AppCompatActivity
{
    public MySurfaceView surfaceView;
    //设备宽高
    public static int deviceWidth = 0;
    public static int deviceHeight = 0;
    //矩阵
    //单个数字就是初始数字不可改变
    //1前缀是用户无冲突数字,2前缀是用户有冲突数字,3前缀是用户匹配数字
    //5前缀是初始有冲突数字,6前缀是初始匹配数字
    public static int[][] map = new int[11][11];
    //选中的行列
    public static int row = 0;
    public static int col = 0;
    //显示的数字,当难度为初级及以下时会帮助排除数字
    public static boolean[] passNum = new boolean[10];
    //toast提示信息单例
    private Toast toast;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        //初始化
        init();
        surfaceView = findViewById(R.id.sv);
        surfaceView.setZOrderOnTop(true);
        new Thread(surfaceView).start();

        surfaceView.setOnTouchListener((v, event) ->
        {
            int preRow = (int) Math.floor((event.getY() - GameConfig.cellLength * 3) / GameConfig.cellLength) + 1;
            int preCol = (int) Math.floor((event.getX() - GameConfig.cellLength / 2) / GameConfig.cellLength) + 1;
            if (preRow <= 0 || preCol <= 0 || preRow >= 10 || preCol >= 10)
            {
                //填入数字
                if (preRow == 12 && preCol >= 1 && preCol <= 9)
                {
                    if (GameConfig.difficultCoefficient <= 2 && passNum[preCol])
                        return false;
                    if (row == 0 || col == 0)
                    {
                        showToast("您还未选中位置");
                        return false;
                    }
                    else if (map[row][col] != 0)
                    {
                        showToast("当前位置不是空的");
                        return false;
                    }
                    map[row][col] = preCol + 10;
                    interactiveProcessor();
                }
                //擦除数字
                else if (preRow == 14 && preCol >= 1 && preCol <= 2)
                {
                    if (row == 0 || col == 0)
                    {
                        showToast("您还未选中位置");
                        return false;
                    }
                    else if (map[row][col] == 0)
                    {
                        showToast("当前位置是空的");
                        return false;
                    }
                    else if (map[row][col] / 10 == 0)
                    {
                        showToast("初始单元格无法擦除");
                        return false;
                    }
                    map[row][col] = 0;
                    interactiveProcessor();
                }
            }
            //选中单元格
            else
            {
                row = preRow;
                col = preCol;
                //System.out.println(map[row][col]);
                //排除数字
                ruleOutNumbers();
                interactiveProcessor();
            }
            surfaceView.drawMap();
            return false;
        });

    }

    /**
     * @Description 初始化
     * @return: void
     **/
    private void init()
    {
        row = 0;
        col = 0;
        passNum = new boolean[10];
        fitScreen();
        GameDraw.init();
        map = Sudoku.createMatrixByDifficulty(GameConfig.difficultCoefficient);
    }

    /**
     * @Description 适配手机屏幕
     * @return: void
     **/
    private void fitScreen()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        deviceWidth = size.x;
        deviceHeight = size.y;
        GameConfig.cellLength = deviceWidth / 10;
    }

    /**
     * @Description 排除数字(在难度不高于初级的情况下调用)
     * @return: void
     **/
    private void ruleOutNumbers()
    {
        passNum = new boolean[10];
        if (map[row][col] != 0)
        {
            return;
        }
        if (GameConfig.difficultCoefficient <= 2)
        {
            //初始化排除序列
            for (int i = 1; i < 10; i++)
            {
                passNum[map[row][i] % 10] = true;
                passNum[map[i][col] % 10] = true;
            }
            // 获得左上角的坐标
            int j = (int) Math.ceil((row - 1) / 3) * 3 + 1;
            int k = (int) Math.ceil((col - 1) / 3) * 3 + 1;
            for (int i = 0; i < 9; i++)
            {
                passNum[map[j + i / 3][k + i % 3] % 10] = true;
            }
        }
    }

    /**
     * @Description 还原地图到原始状态，在每次互动前执行
     * @return: void
     **/
    private void restoreMap()
    {
        for (int i = 1; i < 10; i++)
            for (int j = 1; j < 10; j++)
            {
                //还原初始冲突数字
                if (map[i][j] / 10 == 5)
                {
                    map[i][j] %= 10;
                }
                //还原初始匹配数字
                else if (map[i][j] / 10 == 6)
                {
                    map[i][j] %= 10;
                }
                //还原用户匹配数字
                else if (map[i][j] / 10 == 3)
                {
                    map[i][j] = map[i][j] % 10 + 10;
                }
            }
    }

    /**
     * @Description 玩家互动操作处理器，用来处理玩家操作后的计算工作
     * @return: void
     **/
    private void interactiveProcessor()
    {
        restoreMap();
        //检查相同
        checkAndMarkSameNum();
        //检查冲突
        if (map[row][col] / 10 != 0)
        {
            checkAndMarkRowCollision();
            checkAndMarkColCollision();
            checkAndMarkNineCollision();
        }
        //检查是否胜利
        if (checkWin())
        {
            showToast("胜利，用时:" + String.format("%d分%d秒", GameConfig.time / 60, GameConfig.time % 60) + " ,错误次数:" + GameConfig.errorCount);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            Intent i = new Intent(GameActivity.this, MainActivity.class);
            startActivity(i);
            onBackPressed();
        }
    }


    /**
     * @Description 检查并且标记相同的单元格
     * @return: void
     **/
    private void checkAndMarkSameNum()
    {
        if (map[row][col] == 0) return;
        for (int i = 1; i < 10; i++)
        {
            for (int j = 1; j < 10; j++)
            {
                if (map[i][j] == 0) continue;
                if (i == row && j == col) continue;
                if (map[i][j] % 10 == map[row][col] % 10)
                {
                    if (map[i][j] / 10 == 0)
                        map[i][j] = map[i][j] % 10 + 60;
                    else if (map[i][j] / 10 == 1)
                        map[i][j] = map[i][j] % 10 + 30;
                }
            }
        }
    }

    /**
     * @Description 检查并且标记冲突的同行单元格
     * @return: void
     **/
    private void checkAndMarkRowCollision()
    {
        for (int i = 1; i < 10; i++)
        {
            if (i == col) continue;
            if (map[row][i] % 10 == map[row][col] % 10)
            {
                if (map[row][col] / 10 != 2)
                {
                    map[row][col] = map[row][col] % 10 + 20;
                    GameConfig.errorCount++;
                }
                if (map[row][i] / 10 == 0 || map[row][i] / 10 == 6)
                {
                    map[row][i] = map[row][i] % 10 + 50;
                }
                else
                {
                    map[row][i] = map[row][i] % 10 + 20;
                }

            }
        }
    }

    /**
     * @Description 检查并且标记冲突的同列单元格
     * @return: void
     **/
    private void checkAndMarkColCollision()
    {
        for (int i = 1; i < 10; i++)
        {
            if (i == row) continue;
            if (map[i][col] % 10 == map[row][col] % 10)
            {
                if (map[row][col] / 10 != 2)
                {
                    map[row][col] = map[row][col] % 10 + 20;
                    GameConfig.errorCount++;
                }
                if (map[i][col] / 10 == 0 || map[i][col] / 10 == 6)
                {
                    map[i][col] = map[i][col] % 10 + 50;
                }
                else
                {
                    map[i][col] = map[i][col] % 10 + 20;
                }

            }
        }
    }

    /**
     * @Description 检查并且标记冲突的3*3区域内单元格
     * @return: void
     **/
    private void checkAndMarkNineCollision()
    {
        // 获得左上角的坐标
        int j = (int) Math.ceil((row - 1) / 3) * 3 + 1;
        int k = (int) Math.ceil((col - 1) / 3) * 3 + 1;
        // 循环比较
        for (int i = 0; i < 9; i++)
        {
            if ((j + i / 3) == row && (k + i % 3) == col) continue;
            if (map[j + i / 3][k + i % 3] % 10 == map[row][col] % 10)
            {
                if (map[row][col] / 10 != 2)
                {
                    map[row][col] = map[row][col] % 10 + 20;
                    GameConfig.errorCount++;
                }
                if (map[j + i / 3][k + i % 3] / 10 == 0 || map[j + i / 3][k + i % 3] / 10 == 6 || map[j + i / 3][k + i % 3] / 10 == 5)
                {
                    map[j + i / 3][k + i % 3] = map[j + i / 3][k + i % 3] % 10 + 50;
                }
                else
                {
                    map[j + i / 3][k + i % 3] = map[j + i / 3][k + i % 3] % 10 + 20;
                }
            }
        }
    }

    /**
     * @Description 判断玩家是否胜利
     * @return: boolean
     **/
    private boolean checkWin()
    {
        for (int i = 1; i < 10; i++)
            for (int j = 1; j < 10; j++)
            {
                if (map[i][j] == 0 || map[i][j] / 10 == 2)
                {
                    return false;
                }
            }
        return true;
    }

    /**
     * @Description 当前页面的Toast单例，为了避免重复创建导致的不间断显示问题
     * @Param content: 上下文信息
     * @return: void
     **/
    private void showToast(String content)
    {
        if (toast == null)
        {
            toast = Toast.makeText(this, content, Toast.LENGTH_LONG);
            toast.show();
        }
        else
        {
            toast.setText(content);
            toast.show();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        surfaceView.mIsDrawing = false;
        finish();
    }


}


