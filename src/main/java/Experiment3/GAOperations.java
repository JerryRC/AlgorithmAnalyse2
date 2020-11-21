package Experiment3;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

public class GAOperations {

    private final static double Pc = 0.4;        //�������
    private final static double Pm = 0.04;        //�������
    private static int min = 0;    //���·��
    private static int[] result = null;

    /**
     * ���������ʼ�⣬˼·���Ȳ��������޸���Ҳ���Ա߲������޸����������λ�õĴ���������࣬���������������.
     *
     * @param popNum    ��Ⱥ��С.
     * @param length    ÿһ�����峤��.
     * @param iniPop    �����ĳ�ʼ��Ⱥ.
     * @param codes     ��������.
     * @param codeNum   ���������. �������ÿһ������λ�õĿ���ȡֵ��codeNum��.
     * @param codeCount ÿһ������ļ���.
     */
    public void RandomInitialization(int popNum, int length, int[] codes, int codeNum, int[] codeCount, int[][] iniPop) {
        int i, j;
        Random random;
        //TODO
        //����������룬��ȥ�أ��޸�
        for (i = 0; i < popNum; ++i) {
            //���������������ʼ������1�ų���,������������,���Կ��Ի��ڽ���,����һ������Ҫ�޸�
            //�Ȱ�codes���������Ϊ��ʼ����
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
//        //�������
//        for (i = 0; i < popNum; ++i) {
//            for (int tm : iniPop[i]) {
//                System.out.print(" " + tm);
//            }
//            System.out.println();
//        }
    }

    /**
     * @param pop    ����
     * @param length ���峤��.
     * @param a      �ڽӾ���
     */
    public static double computeFitness(int[] pop, int length, int[][] a) {
        //���������Ӧ��
        //TODO
        double cost = 0.0;
        for (int i = 0; i < length - 1; ++i) {
            //pop��ָ�ĳ���-1���Ǵ洢λ��...
            cost += a[pop[i] - 1][pop[i + 1] - 1];
        }
        cost += a[pop[length - 1] - 1][0];
        //��costԽ��,fitnessԽС
        if (cost < 1e-8) return Double.MAX_VALUE;
        return 1.0 / cost;
    }

    /**
     * @param popNum  ���� ����
     * @param length  ���峤��.
     * @param iniPop  ��Ⱥ
     * @param fitness ÿһ���������Ӧ��
     */
    public static void roundBet(int popNum, int length, int[][] iniPop, double[] fitness, int[] codes) {
        //���̶�
        //TODO
        //���ﱸ��һ��fitness�����ڽ���ʱ���ж�
        double[] cloneFit = fitness.clone();
        //���ﲻ����sum������ֵ�����
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

        //������������
        Vector<int[]> resultPop = new Vector<>();
        resultPop.add(iniPop[maxPos].clone());
        sum -= fitness[maxPos];
        fitness[maxPos] = 0;

        //ת������,ѡ��ʮ��֮һ�ĸ���ŵ�tmpPop,ֱ���Ŵ�����һ��
        for (int i = 0; i < popNum / 10; ++i) {
            //ת������
            double choose = Math.random();
            //��¼����ת���ĸ�λ����
            double count = 0.0;
            int pos = 0;
            for (double fit : fitness) {
                count += fit / sum;
                if (choose <= count) {
                    //ѡ��
                    sum -= fitness[pos];
                    fitness[pos] = 0;
                    break;
                }
                pos++;
            }
            resultPop.add(iniPop[pos].clone());
        }

        Disturbance(iniPop, popNum, length, new Random(System.currentTimeMillis()).nextInt(length), codes);
        //�����Ŷ�֮�������ȥ�����ָ��壨��������Ϊ��Ⱥ������������û��ֱ�Ӱ����ֵĸ�����ӽ�����
        int[] increments = new Random().ints
                (resultPop.size(), 1, (iniPop.length + resultPop.size() - 1) / resultPop.size()).toArray();
        int start = -1;
        int i = 0;
        for (int[] tmp : resultPop) {
            start = Math.min(start + increments[i++], iniPop.length - 1);    //��ֹԽ��
            iniPop[start] = tmp.clone();    //ֱ���ñ��ֵĶ����滻��ԭ���Ķ���Ҳ����һ�����ɱ���Ĺ���
        }

    }


    /**
     * �������PcӦ����Ϊһ������������Ӧ������أ�����Ϊ�˼򻯣�ֱ�Ӷ�����Pcֵ
     *
     * @param iniPop ��Ⱥ
     * @param popNum ���� ����
     * @param length ���峤��.
     * @param disPos ���������λ����
     */
    public static void Disturbance(int[][] iniPop, int popNum, int length, int disPos, int[] codes) {
        //�Ŷ�
        //TODO
        for (int i = 0; i < popNum; ++i) {
            for (int j = i + 1; j < popNum; ++j) {
                if (Math.random() < Pc) {   //��Pc���ʽ��䣬��ÿ������ֻ��������һ��
                    hybrid(iniPop[i], iniPop[j], disPos, codes);
                    break;
                }
            }
        }

        for (int i = 0; i < popNum; ++i) {
            if (Math.random() < Pm) {   //��Pm����ͻ�䣬������������������ֻ�ܲ��ý����ķ�ʽͻ�䣬����һ�޸���û��
                int variatePoint1 = new Random().ints(1, 1, length).sum();    //��ͻ�����λ
                //ͻ���Ŀ����
                int exchangeCode = codes[new Random().ints(1, 1, codes.length).sum()];    //����λ�ò��ܱ�Ϊ1�ų���
                //ͻ�������ڵ�λ��
                int variatePoint2 = getCodePos(exchangeCode, iniPop[i].length, iniPop[i]);
                //����
                iniPop[i][variatePoint2] = iniPop[i][variatePoint1];
                iniPop[i][variatePoint1] = exchangeCode;
            }
        }
    }

    /**
     * @param a      һ�Ÿ���
     * @param b      ���Ÿ���
     * @param disPos �����
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

        //�޸�
        fix(a, codes);
        fix(b, codes);
    }

    /**
     * δ���ǵ�·��ͨ��ֻ�޸��ظ��ĳ���
     *
     * @param a     ����������
     * @param codes �����ѡֵ
     */
    public static void fix(int[] a, int[] codes) {
        int codeNum = codes.length;
        int[] nJs = new int[codeNum];
        Arrays.fill(nJs, 0);

        Vector<Integer> amend = new Vector<>();    //������Ҫ����������
        for (int i = 0; i < a.length; ++i) {
            int pos = getCodePos(a[i], codeNum, codes);
            nJs[pos]++;
            if (nJs[pos] != 1) {
                a[i] = 0;       //�����ظ����ֵĳ�����գ����������codes��û��������в�����0��Ϊ���
            }

        }
        for (int i = 0; i < codeNum; ++i) {
            if (nJs[i] == 0) {
                amend.add(codes[i]);    //���δ���ֵĳ���
            }
        }

        if (amend.size() == 0) return;  //��������

        for (int i = 0; i < amend.size(); ++i) {    //���������б�
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
     * ��ȡcode��codes�е�λ��
     *
     * @param code    ����
     * @param codeNum �ܱ�����
     * @param codes   �������.
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

        //�Ŵ�һǧ��
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
