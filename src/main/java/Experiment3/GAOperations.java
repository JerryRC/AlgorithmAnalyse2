package Experiment3;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

public class GAOperations {

    private final static double Pc = 0.4;        //交叉概率
    private final static double Pm = 0.04;        //变异概率
    private static int min = 0;    //最短路径
    private static int[] result = null;

    /**
     * 随机产生初始解，思路：先产生，后修复（也可以边产生边修复，如产生的位置的代码计数过多，则重新随机产生）.
     *
     * @param popNum    种群大小.
     * @param length    每一个个体长度.
     * @param iniPop    产生的初始种群.
     * @param codes     编码序列.
     * @param codeNum   编码的数量. 即基因的每一个编码位置的可能取值有codeNum种.
     * @param codeCount 每一个编码的计数.
     */
    public void RandomInitialization(int popNum, int length, int[] codes, int codeNum, int[] codeCount, int[][] iniPop) {
        int i, j;
        Random random;
        //TODO
        //随机产生编码，并去重，修复
        for (i = 0; i < popNum; ++i) {
            //随机产生种子且起始城市是1号城市,由于是排列树,所以可以基于交换,这样一来不需要修复
            //先把codes深复制下来作为初始排序
            iniPop[i] = codes.clone();

            random = new Random();
            int[] changeList = random.ints(length, 1, codeNum).toArray();

            for (j = 1; j < length; ++j) {
                int pos = changeList[j];
                int tmp = iniPop[i][j];
                iniPop[i][j] = iniPop[i][pos];
                iniPop[i][pos] = tmp;

            }
        }
//        //输出种子
//        for (i = 0; i < popNum; ++i) {
//            for (int tm : iniPop[i]) {
//                System.out.print(" " + tm);
//            }
//            System.out.println();
//        }
    }

    /**
     * @param pop    个体
     * @param length 个体长度.
     * @param a      邻接矩阵
     */
    public static double computeFitness(int[] pop, int length, int[][] a) {
        //计算个体适应度
        //TODO
        double cost = 0.0;
        for (int i = 0; i < length - 1; ++i) {
            //pop所指的城市-1才是存储位置...
            cost += a[pop[i] - 1][pop[i + 1] - 1];
        }
        cost += a[pop[length - 1] - 1][0];
        //即cost越大,fitness越小
        if (cost < 1e-8) return Double.MAX_VALUE;
        return 1.0 / cost;
    }

    /**
     * @param popNum  个体 个数
     * @param length  个体长度.
     * @param iniPop  种群
     * @param fitness 每一个个体的适应度
     */
    public static void roundBet(int popNum, int length, int[][] iniPop, double[] fitness, int[] codes) {
        //轮盘赌
        //TODO
        //这里备份一个fitness，用于交配时的判断
        double[] cloneFit = fitness.clone();
        //这里不考虑sum超出阈值的情况
        double sum = 0;
        double maxFitness = fitness[0];
        int maxPos = 0;
        for (int i = 0; i < length; i++) {
            sum += fitness[i];
            if (maxFitness < fitness[i]) {
                maxFitness = fitness[i];
                maxPos = i;
            }
        }

        result = iniPop[maxPos].clone();

        //保留最优种子
        Vector<int[]> resultPop = new Vector<>();
        resultPop.add(iniPop[maxPos].clone());
        sum -= fitness[maxPos];
        fitness[maxPos] = 0;

        //转动轮盘,选择十分之一的个体放到tmpPop,直接遗传到下一轮
        for (int i = 0; i < popNum / 10; ++i) {
            //转动轮盘
            double choose = Math.random();
            //记录轮盘转到哪个位置了
            double count = 0.0;
            int pos = 0;
            for (double fit : fitness) {
                count += fit / sum;
                if (choose <= count) {
                    //选中
                    sum -= fitness[pos];
                    fitness[pos] = 0;
                    break;
                }
                pos++;
            }
            resultPop.add(iniPop[pos].clone());
        }

        Disturbance(iniPop, popNum, length, new Random(System.currentTimeMillis()).nextInt(length), codes);
        //经过扰动之后，再随机去掉部分个体（这里是因为种群数量不改所以没法直接把留种的个体添加进来）
        int[] increments = new Random().ints
                (resultPop.size(), 1, (iniPop.length + resultPop.size() - 1) / resultPop.size()).toArray();
        int start = -1;
        int i = 0;
        for (int[] tmp : resultPop) {
            start = Math.min(start + increments[i++], iniPop.length - 1);    //防止越界
            iniPop[start] = tmp.clone();    //直接拿保种的对象替换掉原来的对象，也就是一个随机杀死的过程
        }

    }


    /**
     * 这里，本来Pc应该作为一个变量，与适应度正相关，但是为了简化，直接定死了Pc值
     *
     * @param iniPop 种群
     * @param popNum 个体 个数
     * @param length 个体长度.
     * @param disPos 随机交换的位置数
     */
    public static void Disturbance(int[][] iniPop, int popNum, int length, int disPos, int[] codes) {
        //扰动
        //TODO
        for (int i = 0; i < popNum; ++i) {
            for (int j = i + 1; j < popNum; ++j) {
                if (Math.random() < Pc) {   //以Pc概率交配，且每个个体只主动交配一次
                    hybrid(iniPop[i], iniPop[j], disPos, codes);
                    break;
                }
            }
        }

        for (int i = 0; i < popNum; ++i) {
            if (Math.random() < Pm) {   //以Pm概率突变，由于是排列树，所以只能采用交换的方式突变，否则一修复就没了
                int variatePoint1 = new Random().ints(1, 1, length).sum();    //不突变零号位
                //突变的目标结果
                int exchangeCode = codes[new Random().ints(1, 1, codes.length).sum()];    //其他位置不能变为1号城市
                //突变结果所在的位次
                int variatePoint2 = getCodePos(exchangeCode, iniPop[i].length, iniPop[i]);
                //交换
                iniPop[i][variatePoint2] = iniPop[i][variatePoint1];
                iniPop[i][variatePoint1] = exchangeCode;
            }
        }
    }

    /**
     * @param a      一号个体
     * @param b      二号个体
     * @param disPos 交叉点
     */
    public static void hybrid(int[] a, int[] b, int disPos, int[] codes) {
        if (disPos < a.length / 2) {
            for (int i = 0; i <= disPos; ++i) {
                int tmp = a[i];
                a[i] = b[i];
                b[i] = tmp;
            }
        } else {
            for (int i = disPos; i < a.length; ++i) {
                int tmp = a[i];
                a[i] = b[i];
                b[i] = tmp;
            }
        }

        //修复
        fix(a, codes);
        fix(b, codes);
    }

    /**
     * 未考虑道路可通，只修复重复的城市
     *
     * @param a     待修正数组
     * @param codes 编码可选值
     */
    public static void fix(int[] a, int[] codes) {
        int codeNum = codes.length;
        int[] nJs = new int[codeNum];
        Arrays.fill(nJs, 0);

        Vector<Integer> amend = new Vector<>();    //保存需要修正的数字
        for (int i = 0; i < a.length; ++i) {
            int pos = getCodePos(a[i], codeNum, codes);
            nJs[pos]++;
            if (nJs[pos] != 1) {
                a[i] = 0;       //后面重复出现的城市清空，这里必须是codes中没有这个城市才能用0作为标记
            }

        }
        for (int i = 0; i < codeNum; ++i) {
            if (nJs[i] == 0) {
                amend.add(codes[i]);    //添加未出现的城市
            }
        }

        if (amend.size() == 0) return;  //不用修正

        for (int i = 0; i < amend.size(); ++i) {    //打乱修正列表
            int exchange = new Random().nextInt(amend.size());
            int tmp = amend.get(i);
            amend.set(i, amend.get(exchange));
            amend.set(i, tmp);
        }

        int pos = 0;
        for (int i = 0; i < a.length; ++i) {
            if (a[i] == 0) {
                a[i] = amend.get(pos);
                pos++;
            }
        }
    }

    /**
     * 获取code在codes中的位置
     *
     * @param code    编码
     * @param codeNum 总编码数
     * @param codes   编码矩阵.
     */
    public static int getCodePos(int code, int codeNum, int[] codes) {
        int pos = 0;
        for (; pos < codeNum; pos++) {
            if (code == codes[pos]) {
                return pos;
            }
        }
        return -1;
    }


    public static void main(String[] args) {
        int popNum = 10;
        int length = 5;
        int codeNum = 5;
        int[] codes = {1, 2, 3, 4, 5};
        int[] codeCount = {1, 1, 1, 1, 1};
        int[][] a = {
                {100, 3, 1, 5, 8},
                {3, 100, 6, 7, 9},
                {1, 6, 100, 4, 2},
                {5, 7, 4, 100, 3},
                {8, 9, 2, 3, 100}
        };
        int[][] iniPop = new int[popNum][length];    //10 population, individual's length is 5
        GAOperations gaOperations = new GAOperations();

        gaOperations.RandomInitialization(popNum, length, codes, codeNum, codeCount, iniPop);

        //遗传一千代
        int T = 1000;
        while(T-- != 0){
            int i;
            double[] fitness = new double[popNum];
            for (i = 0; i < popNum; i++) {
                fitness[i] = GAOperations.computeFitness(iniPop[i], length, a);
            }

            GAOperations.roundBet(popNum, length, iniPop, fitness, codes);
        }

        for(int i=0;i<result.length-1;++i){
            min += a[result[i]-1][result[i+1]-1];
        }
        min += a[result[result.length-1]-1][0];

        System.out.println(min);
        System.out.println(Arrays.toString(result));

    }
}
