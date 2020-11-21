package Experiment3;

import Experiment2.BB4TSP;
import Experiment2.Back4TSP;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.stream.IntStream;

public class compareTest {

    final private static int vertex = 17;         //点数
    final private static double threshold = 0;   //断边概率
    final private static int MAX = 100;            //权值最大值
    final private static int MIN = 1;              //权值最小值
    final private static int[][] b = new int[vertex + 1][vertex + 1];    //邻接矩阵
    final private static int[][] a = new int[vertex][vertex];    //GA的邻接矩阵

    Back4TSP back4TSP = new Back4TSP();
    BB4TSP bb4TSP = new BB4TSP();

    @BeforeClass
    public static void initialize() {   //初始化一次邻接矩阵

        for (int j : IntStream.range(0, vertex + 1).toArray()) {
            b[0][j] = -1;
        }

        for (int i : IntStream.range(1, vertex + 1).toArray()) {
            for (int j : IntStream.range(0, vertex + 1).toArray()) {
                if (j == 0 || Math.random() < threshold || i == j) {    //一定概率无边
                    b[i][j] = -1;
                } else {
                    b[i][j] = (int) (Math.random() * (MAX - MIN) + MIN);    //其他情况随机权值
                }
            }

            //构造一个GA算法能用的图
            for (int j : IntStream.range(1, vertex + 1).toArray()) {
                a[i - 1][j - 1] = b[i][j];
            }
        }

//        for (int i : IntStream.range(0, vertex + 1).toArray()) {
//            System.out.println(Arrays.toString(b[i]));              //输出矩阵检查
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
