package com.example.sudoku.utils;

import java.util.Random;

public class Sudoku
{
    //当前矩阵
    private static int[][] n = new int[11][11];
    //辅助随机数生成数组
    private static int[] numFlag = new int[10];
    //随机数产生
    private static Random r = new Random();

    /**
     * @Description 根据难度系数生成数独矩阵
     * @Param coe: 难度系数
     * @return: int[][] 返回新的矩阵
     **/
    public static int[][] createMatrixByDifficulty(int coe)
    {
        n = createMatrix();
        //需要消除的数字数量
        int breakBlock = 0;
        //难度平方级上升
        breakBlock += 10 + (int) (1.6 * coe * coe);
        while (breakBlock > 0)
        {
            int x = r.nextInt(9) + 1;
            int y = r.nextInt(9) + 1;
            if (n[x][y] == 0) continue;
            else
            {
                //置空
                n[x][y] = 0;
                breakBlock--;
            }
        }
        return n;
    }

    /**
     * @Description 产生一个新的数独矩阵
     * @return: int[][] 返回全新矩阵
     **/
    public static int[][] createMatrix()
    {
        n = new int[11][11];
        // 生成数字
        for (int i = 1; i <= 9; i++)
        {
            //当前位置尝试次数
            int time = 0;
            for (int j = 1; j <= 9; j++)
            {
                // 产生数字
                n[i][j] = fillNum(time);
                // 如果返回值为0，则代表卡住，退回处理
                // 退回处理的原则是：如果不是第一列，则先倒退到前一列，否则倒退到前一行的最后一列
                if (n[i][j] == 0)
                {
                    // 不是第一列，则倒退一列
                    if (j > 1)
                    {
                        j -= 2;
                    }
                    // 是第一列，则倒退到上一行的最后一列
                    else
                    {
                        i--;
                        j = 9;
                    }
                    continue;
                }
                // 填充成功
                if (isCorret(i, j))
                {
                    time = 0;
                }
                else
                {
                    time++;
                    j--;
                }
            }
        }
        return n;
    }

    /**
     * @Description 填充
     * @Param time: 当前填充次数
     * @return: int 尝试的数字
     **/
    private static int fillNum(int time)
    {
        // 第一次尝试时，初始化随机数字源数组
        if (time == 0)
        {
            numFlag = new int[10];
        }
        // 第10次填充，表明该位置已经卡住，则返回0，由主程序处理退回
        if (time == 9)
        {
            return 0;
        }
        int index = 0;
        while (true)
        {
            index = r.nextInt(9) + 1;
            if (numFlag[index] == 1)
            {
                continue;
            }
            numFlag[index] = 1;
            return index;
        }
    }

    /**
     * @Description 检查当前位置行、列、3*3方格内是否合理
     * @Param row: 行号
     * @Param col: 列号
     * @return: boolean 合理则返回true
     **/
    private static boolean isCorret(int row, int col)
    {
        return (checkRow(row) & checkColumn(col) & checkNine(row, col));
    }

    /**
     * @Description 检查当前行是否合理
     * @Param row: 行号
     * @return: boolean 合理则返回true
     **/
    private static boolean checkRow(int row)
    {
        int[] flag = new int[10];
        for (int i = 1; i <= 9; i++)
        {
            if (n[row][i] == 0)
            {
                continue;
            }
            if (flag[n[row][i]] == 1)
            {
                return false;
            }
            flag[n[row][i]] = 1;
        }
        return true;
    }

    /**
     * @Description 检查当前列是否合理
     * @Param col: 列号
     * @return: boolean 合理则返回true
     **/
    private static boolean checkColumn(int col)
    {
        int[] flag = new int[10];
        for (int i = 1; i <= 9; i++)
        {
            if (n[i][col] == 0)
            {
                continue;
            }
            if (flag[n[i][col]] == 1)
            {
                return false;
            }
            flag[n[i][col]] = 1;
        }
        return true;
    }

    /**
     * @Description 检查当前3*3方格内是否合理
     * @Param row: 行号
     * @Param col: 列号
     * @return: boolean 合理则返回true
     **/
    private static boolean checkNine(int row, int col)
    {
        int[] flag = new int[10];
        // 获得左上角的坐标
        int j = (int) Math.ceil((row - 1) / 3) * 3 + 1;
        int k = (int) Math.ceil((col - 1) / 3) * 3 + 1;
        // 循环比较
        for (int i = 0; i < 9; i++)
        {
            if (n[j + i / 3][k + i % 3] == 0)
            {
                continue;
            }
            if (flag[n[j + i / 3][k + i % 3]] == 1)
                return false;
            flag[n[j + i / 3][k + i % 3]] = 1;
        }
        return true;
    }

}
