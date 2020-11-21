package Experiment3;

import Experiment2.BB4TSP;
import Experiment2.Back4TSP;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.stream.IntStream;

public class compareTest {

    final private static int vertex = 17;         //����
    final private static double threshold = 0;   //�ϱ߸���
    final private static int MAX = 100;            //Ȩֵ���ֵ
    final private static int MIN = 1;              //Ȩֵ��Сֵ
    final private static int[][] b = new int[vertex + 1][vertex + 1];    //�ڽӾ���
    final private static int[][] a = new int[vertex][vertex];    //GA���ڽӾ���

    Back4TSP back4TSP = new Back4TSP();
    BB4TSP bb4TSP = new BB4TSP();

    @BeforeClass
    public static void initialize() {   //��ʼ��һ���ڽӾ���

        for (int j : IntStream.range(0, vertex + 1).toArray()) {
            b[0][j] = -1;
        }

        for (int i : IntStream.range(1, vertex + 1).toArray()) {
            for (int j : IntStream.range(0, vertex + 1).toArray()) {
                if (j == 0 || Math.random() < threshold || i == j) {    //һ�������ޱ�
                    b[i][j] = -1;
                } else {
                    b[i][j] = (int) (Math.random() * (MAX - MIN) + MIN);    //����������Ȩֵ
                }
            }

            //����һ��GA�㷨���õ�ͼ
            for (int j : IntStream.range(1, vertex + 1).toArray()) {
                a[i - 1][j - 1] = b[i][j];
            }
        }

//        for (int i : IntStream.range(0, vertex + 1).toArray()) {
//            System.out.println(Arrays.toString(b[i]));              //���������
//        }
    }

    @Test(timeout = 60000)
    public void testBack4TSP() {
        back4TSP.backtrack4TSP(b, vertex);
        System.out.println("Cost: " + back4TSP.getBestC());
    }

    @Test(timeout = 60000)
    public void testBb4TSP() {
        bb4TSP.bb4TSP(b, vertex);
        System.out.println("\nCost: " + bb4TSP.getMinCost());
    }

    @Test(timeout = 60000)
    public void testGA4TSP() {
        int[] codes = IntStream.range(1, vertex + 1).toArray();
        GAOperations.run(50, vertex, codes.length, codes, a);
    }

}
