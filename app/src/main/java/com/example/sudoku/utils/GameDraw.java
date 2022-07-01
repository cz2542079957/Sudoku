package com.example.sudoku.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.example.sudoku.GameActivity;
import com.example.sudoku.config.GameConfig;

public class GameDraw
{
    private static int cellLength;
    private static int top;
    private static int left;

    private static Paint whiteFontP = new Paint(), darkFontP = new Paint(), redFontP = new Paint(), darkFloorP = new Paint(), greenFloorP = new Paint(), redFloorP = new Paint();

    /**
     * @Description 绘制工具初始化
     * @return: void
     **/
    public static void init()
    {
        cellLength = GameConfig.cellLength;
        top = cellLength * 3;
        left = cellLength / 2;
        whiteFontP.setARGB(255, 255, 255, 255);
        whiteFontP.setTextSize(cellLength);
        whiteFontP.setStrokeWidth(2);
        darkFontP.setARGB(255, 130, 140, 150);
        redFontP.setTextSize(cellLength);
        redFontP.setStrokeWidth(2);
        redFontP.setARGB(255, 240, 62, 62);
        darkFontP.setTextSize(cellLength);
        darkFontP.setStrokeWidth(2);
        darkFloorP.setARGB(255, 130, 140, 150);
        greenFloorP.setARGB(255, 100, 200, 0);
        redFloorP.setARGB(255, 240, 62, 62);
    }

    /**
     * @Description 绘制矩阵竖线
     * @Param canvas: 传入画布
     * @return: void
     **/
    public static void drawLines(Canvas canvas)
    {
        Paint p1 = new Paint();
        p1.setARGB(100, 0, 0, 0);
        p1.setStrokeWidth(4);
        Paint p2 = new Paint();
        p2.setARGB(200, 0, 0, 0);
        p2.setStrokeWidth(8);
        //横线
        for (int i = 0; i <= 9; i++)
        {
            if (i == 0 || i == 3 || i == 6 || i == 9)
                canvas.drawLine(left + i * cellLength, top, left + i * cellLength, top + 9 * cellLength, p2);
            else canvas.drawLine(left + i * cellLength, top, left + i * cellLength, top + 9 * cellLength, p1);
        }
        //竖线
        for (int i = 0; i <= 9; i++)
        {
            if (i == 0 || i == 3 || i == 6 || i == 9)
                canvas.drawLine(left, top + i * cellLength, left + 9 * cellLength, top + i * cellLength, p2);
            else canvas.drawLine(left, top + i * cellLength, left + 9 * cellLength, top + i * cellLength, p1);
        }

    }

    /**
     * @Description 绘制选中数字底部效果
     * @Param canvas: 传入画布
     * @return: void
     **/
    public static void drawSelectedEffect(Canvas canvas)
    {
        int row = GameActivity.row;
        int col = GameActivity.col;
        if (row == 0 || col == 0) return;
        // 获得当前所在3*3区域左上角的单元格位置
        int j = (int) Math.ceil((row - 1) / 3) * 3;
        int k = (int) Math.ceil((col - 1) / 3) * 3;
        Paint p = new Paint();
        p.setARGB(40, 0, 0, 0);
        canvas.drawRect(left + (col - 1) * cellLength, top + (row - 1) * cellLength, left + col * cellLength, top + row * cellLength, p);
        for (int i = 0; i < 9; i++)
        {
            int x = (k + i % 3) * cellLength;
            int y = (j + i / 3) * cellLength;
            canvas.drawRect(left + x, top + y, left + x + cellLength, top + y + cellLength, p);
        }

        //绘制所在行底部
        for (int i = 0; i < 9; i++)
        {
            if (i < j || i > j + 2)
                canvas.drawRect(left + (col - 1) * cellLength, top + i * cellLength, left + (col) * cellLength, top + (i + 1) * cellLength, p);
        }

        //绘制所在列底部
        for (int i = 0; i < 9; i++)
        {
            if (i < k || i > k + 2)
                canvas.drawRect(left + i * cellLength, top + (row - 1) * cellLength, left + (i + 1) * cellLength, top + (row) * cellLength, p);

        }

    }

    /**
     * @Description 绘制数字效果
     * @Param canvas:
     * @return: void
     **/
    public static void drawSelectedNumEffect(Canvas canvas)
    {
        for (int i = 1; i < 10; i++)
            for (int j = 1; j < 10; j++)
            {
                if (GameActivity.map[i][j] == 0) continue;
                //选中数字
                if (i == GameActivity.row && j == GameActivity.col && GameActivity.map[i][j] / 10 != 2)
                    drawCommonSelectedCell(canvas, i, j, GameActivity.map[i][j] % 10);
                else
                {
                    if (GameActivity.map[i][j] / 10 == 0 || GameActivity.map[i][j] / 10 == 1)
                        drawCommonCell(canvas, i, j, GameActivity.map[i][j] % 10);
                    else if (GameActivity.map[i][j] / 10 == 3 || GameActivity.map[i][j] / 10 == 6)
                        drawMatchedCell(canvas, i, j, GameActivity.map[i][j] % 10);
                    else if (GameActivity.map[i][j] / 10 == 5)
                    {
                        drawCollisionCell1(canvas, i, j, GameActivity.map[i][j] % 10);
                    }
                    else if (GameActivity.map[i][j] / 10 == 2)
                    {
                        drawCollisionCell2(canvas, i, j, GameActivity.map[i][j] % 10);
                    }

                }

            }
    }

    /**
     * @Description 绘制没有被选中的普通区块
     * @Param canvas: 画布
     * @Param row: 所在行
     * @Param col: 所在列
     * @Param num: 显示的数字
     * @return: void
     **/
    public static void drawCommonCell(Canvas canvas, int row, int col, int num)
    {
        canvas.drawText(String.valueOf(num), left + (int) ((col - 0.8) * cellLength), top + (int) ((row - 0.1) * cellLength), darkFontP);
    }

    /**
     * @Description 绘制被选中的区块
     * @Param canvas: 画布
     * @Param row: 所在行
     * @Param col: 所在列
     * @Param num: 显示的数字
     * @return: void
     **/
    public static void drawCommonSelectedCell(Canvas canvas, int row, int col, int num)
    {
        canvas.drawRect(left + (col - 1) * cellLength, top + (row - 1) * cellLength, left + col * cellLength, top + row * cellLength, darkFloorP);
        canvas.drawText(String.valueOf(num), left + (int) ((col - 0.8) * cellLength), top + (int) ((row - 0.1) * cellLength), whiteFontP);
    }

    /**
     * @Description 绘制与当前选择的数字相同区块
     * @Param canvas: 画布
     * @Param row: 所在行
     * @Param col: 所在列
     * @Param num: 显示的数字
     * @return: void
     **/
    public static void drawMatchedCell(Canvas canvas, int row, int col, int num)
    {
        canvas.drawRect(left + (col - 1) * cellLength, top + (row - 1) * cellLength, left + col * cellLength, top + row * cellLength, greenFloorP);
        canvas.drawText(String.valueOf(num), left + (int) ((col - 0.8) * cellLength), top + (int) ((row - 0.1) * cellLength), whiteFontP);

    }

    /**
     * @Description 绘制与当前选中数字冲突的初始的单元格
     * @Param canvas: 画布
     * @Param row: 所在行
     * @Param col: 所在列
     * @Param num: 显示的数字
     * @return: void
     **/
    public static void drawCollisionCell1(Canvas canvas, int row, int col, int num)
    {
        canvas.drawText(String.valueOf(num), left + (int) ((col - 0.8) * cellLength), top + (int) ((row - 0.1) * cellLength), redFontP);

    }

    /**
     * @Description 绘制与当前选中的数字冲突的用户单元格
     * @Param canvas: 画布
     * @Param row: 所在行
     * @Param col: 所在列
     * @Param num: 显示的数字
     * @return: void
     **/
    public static void drawCollisionCell2(Canvas canvas, int row, int col, int num)
    {
        canvas.drawRect(left + (col - 1) * cellLength, top + (row - 1) * cellLength, left + col * cellLength, top + row * cellLength, redFloorP);
        canvas.drawText(String.valueOf(num), left + (int) ((col - 0.8) * cellLength), top + (int) ((row - 0.1) * cellLength), whiteFontP);

    }

    /**
     * @Description 绘制下方备选数字列表
     * @Param canvas: 画布
     * @Param row: 所在行
     * @Param col: 所在列
     * @Param num: 显示的数字
     * @return: void
     **/
    public static void drawButton(Canvas canvas)
    {
        //绘制填充数字列表
        for (int i = 1; i < 10; i++)
        {
            if (GameConfig.difficultCoefficient <= 2 && GameActivity.passNum[i])
                continue;
            canvas.drawText(String.valueOf(i), left + (int) ((i - 0.8) * cellLength), top + 12 * cellLength, darkFontP);
        }
        //绘制擦除按钮
        canvas.drawText("擦除", left, top + 14 * cellLength, redFontP);
    }

    /**
     * @Description 绘制当局游戏统计数据信息
     * @Param canvas: 画布
     * @Param row: 所在行
     * @Param col: 所在列
     * @Param num: 显示的数字
     * @return: void
     **/
    public static void drawGameInfo(Canvas canvas)
    {
        String dif = "";
        switch (GameConfig.difficultCoefficient)
        {
            case 0:
                dif = "菜鸟";
                break;
            case 1:
                dif = "入门";
                break;
            case 2:
                dif = "初级";
                break;
            case 3:
                dif = "中级";
                break;
            case 4:
                dif = "高级";
                break;
            case 5:
                dif = "大师";
                break;
        }
        Paint p = new Paint(darkFontP);
        p.setTextSize(75);
        canvas.drawText("当前难度等级:" + dif, left + (int) ((1 - 0.8) * cellLength), (int) (0.8 * cellLength), p);
        p.setTextSize(60);
        p.setARGB(255, 240, 80, 80);
        canvas.drawText("错误次数:" + GameConfig.errorCount, left + (int) ((1 - 0.8) * cellLength), (int) (1.8 * cellLength), p);
        p.setARGB(255, 130, 200, 30);
        canvas.drawText(String.format("%02d:%02d", GameConfig.time / 60, GameConfig.time % 60), left + (int) ((1 - 0.8) * cellLength), (int) (2.5 * cellLength), p);
    }
}
