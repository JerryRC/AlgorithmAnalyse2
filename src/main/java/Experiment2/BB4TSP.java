package Experiment2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Vector;
import java.util.stream.IntStream;

public class BB4TSP {

	int NoEdge = -1; //表示没有边
	private int minCost = Integer.MAX_VALUE; //当前最小代价

	public int getMinCost() {
		return minCost;
	}

	public void setMinCost(int minCost) {
		this.minCost = minCost;
	}

//    Comparator<HeapNode> cmp = new Comparator<HeapNode>() {
//        public int compare(HeapNode e1, HeapNode e2) {//从大到小排序
//            return e2.lcost - e1.lcost;
//        }
//    };

	Comparator<HeapNode> cmp = Comparator.comparingInt(e -> e.lcost);

	private final PriorityQueue<HeapNode> priorHeap = new PriorityQueue<>(100, cmp);//存储活节点
	private Vector<Integer> bestH = new Vector<>();


	public static class HeapNode implements Comparable<HeapNode> {
		Vector<Integer> liveNode;//城市排列
		int lcost; //代价的下界
		int level;//0-level的城市是已经排好的

		//构造方法
		public HeapNode(Vector<Integer> node, int lb, int lev) {
			liveNode = new Vector<>(node);
			lcost = lb;
			level = lev;
		}

		@Override
		public int compareTo(HeapNode heapNode) {//升序排列, 每一次pollFirst
			int xu = heapNode.lcost;
			return Integer.compare(lcost, xu);
		}

		public boolean equals(Object x) {
			return (x instanceof HeapNode) && lcost == ((HeapNode) x).lcost;
		}

	}

	/**
	 * 这个函数用于计算已选路径的实际开销。
	 *
	 * @param liveNode 城市的排列
	 * @param level    当前确定的城市的个数.
	 * @param cMatrix  邻接矩阵，第0行，0列不算
	 * @return 已选路径的开销
	 */
	private int countSelectedPaths(Vector<Integer> liveNode, int level, int[][] cMatrix) {
		int result = 0;

		//当前已选择路径求和
		for (int i : IntStream.range(1, level).toArray()) {
			result += cMatrix[liveNode.get(i - 1)][liveNode.get(i)];
		}

		//如果是叶子节点，直接检查是否可以闭合回路，可以则计算最后结果，否则返回一个大数表示不可达
		if (level == liveNode.size()) {
			int closeV = cMatrix[liveNode.get(level - 1)][liveNode.get(0)];
			if (closeV == NoEdge) {
				return Integer.MAX_VALUE;
			}
			result += closeV;
			return result;
		}

		result *= 2;
		return result;
	}


	/**
	 * 计算部分解的下界。
	 * 这里由于是处理非对称矩阵，故采取行列分别取最小值的算法
	 *
	 * @param liveNode 城市的排列
	 * @param level    当前确定的城市的个数.
	 * @param cMatrix  邻接矩阵，第0行，0列不算
	 * @throws IllegalArgumentException 参数错误
	 */
	public int computeLB(Vector<Integer> liveNode, int level, int[][] cMatrix) {
		//TODO
		int result = countSelectedPaths(liveNode, level, cMatrix);
		if (level == liveNode.size()) {
			return result;
		}

		//r1 不在路径上的最小入度，列最小
		int min = Integer.MAX_VALUE;
		for (int i = 2; i < cMatrix.length; ++i) {
			if (cMatrix[i][1] != NoEdge && cMatrix[i][1] < min) {  //有边
				//而且r1的入度一定还未选择，肯定不在路径上
				min = cMatrix[i][1];
			}
		}
		result += min;

		//rk 不在路径上的最小出度,行最小
		min = Integer.MAX_VALUE;
		int rk = liveNode.get(level - 1);
		for (int j = 1; j < cMatrix[rk].length; ++j) {
			if (cMatrix[rk][j] != NoEdge && cMatrix[rk][j] < min) {  //有边
				//而且rk的出度一定还未选择，肯定不在路径上
				min = cMatrix[rk][j];
			}
		}
		result += min;

		//剩余点 （level+1 开始）的下界求和：出度，行最小
		for (int i : IntStream.range(level, liveNode.size()).toArray()) {

			int rtmp = liveNode.get(i);

			min = Integer.MAX_VALUE;
			for (int j = 1; j < cMatrix[rtmp].length; ++j) {
				if (cMatrix[rtmp][j] != NoEdge && cMatrix[rtmp][j] < min) {    //有边
					min = cMatrix[rtmp][j];
				}
			}
			result += min;
		}

		//剩下的点的下界求和：入度，列最小
		for (int j : IntStream.range(level, liveNode.size()).toArray()) {

			int rtmp = liveNode.get(j);

			min = Integer.MAX_VALUE;
			for (int i = 1; i < cMatrix.length; ++i) {
				if (cMatrix[i][rtmp] != NoEdge && cMatrix[i][rtmp] < min) {    //有边
					min = cMatrix[i][rtmp];
				}
			}
			result += min;
		}

		result++;   //向上取整
		result /= 2;

//        System.out.print("[ ");
//        for (int i = 0; i < level; ++i) {
//            System.out.print(liveNode.get(i) + " ");
//        }
//        System.out.print("]  ");
//        System.out.println(result);

		return result == 0 ? -1 : result;
	}

	/**
	 * 计算部分解的下界。
	 * 这里是处理对称矩阵，故采取取两个最小值的算法
	 *
	 * @param liveNode 城市的排列
	 * @param level    当前确定的城市的个数.
	 * @param cMatrix  邻接矩阵，第0行，0列不算
	 * @throws IllegalArgumentException 参数错误
	 */
	public int computeSymLB(Vector<Integer> liveNode, int level, int[][] cMatrix) {
		//TODO
		int result = countSelectedPaths(liveNode, level, cMatrix);
		if (level == liveNode.size()) {
			return result;
		}


		if (level != 1) {//r1 不在路径上的最小入度，列最小
			int min = Integer.MAX_VALUE;
			for (int j = 1; j < cMatrix[1].length; ++j) {
				if (cMatrix[1][j] != NoEdge && cMatrix[1][j] < min && j != liveNode.get(1)) {  //有边且不在路径上
					min = cMatrix[1][j];
				}
			}
			result += min;

			//rk 不在路径上的最小出度,行最小
			min = Integer.MAX_VALUE;
//            for (int j = 1; j < cMatrix[level].length; ++j) {
//                if (cMatrix[level][j] != NoEdge && cMatrix[level][j] < min && j != liveNode.get(level - 2)) {  //有边且不在路径上
//                    min = cMatrix[level][j];
//                }
//            }
			int rk = liveNode.get(level - 1);
			for (int j = 1; j < cMatrix[rk].length; ++j) {
				if (cMatrix[rk][j] != NoEdge && cMatrix[rk][j] < min && j != liveNode.get(level - 2)) {  //有边且不在路径上
					min = cMatrix[rk][j];
				}
			}
			result += min;
		} else {
			int min1 = Integer.MAX_VALUE;
			int min2 = Integer.MAX_VALUE;
			for (int j = 1; j < cMatrix[1].length; ++j) {
				if (cMatrix[1][j] != NoEdge && cMatrix[1][j] < min1) {    //有边
					min2 = min1;
					min1 = cMatrix[1][j];
				} else if (cMatrix[1][j] != NoEdge && cMatrix[1][j] < min2) {
					min2 = cMatrix[1][j];
				}
			}
			result += min1;
			result += min2;
		}

		//剩余点 （level+1 开始）的下界求和：出度，行最小
		for (int i : IntStream.range(level, liveNode.size()).toArray()) {

			int rtmp = liveNode.get(i);

			int min1 = Integer.MAX_VALUE;
			int min2 = Integer.MAX_VALUE;
			for (int j = 1; j < cMatrix[rtmp].length; ++j) {
				if (cMatrix[rtmp][j] != NoEdge && cMatrix[rtmp][j] < min1) {    //有边
					min2 = min1;
					min1 = cMatrix[rtmp][j];
				} else if (cMatrix[rtmp][j] != NoEdge && cMatrix[rtmp][j] < min2) {
					min2 = cMatrix[rtmp][j];
				}
			}
			result += min1;
			result += min2;
		}

		result++;   //向上取整
		result /= 2;

//        System.out.print("[ ");
//        for (int i = 0; i < level; ++i) {
//            System.out.print(liveNode.get(i) + " ");
//        }
//        System.out.print("]  ");
//        System.out.println(result);

		return result == 0 ? -1 : result;
	}

	/**
	 * 计算TSP问题的最小代价的路径.
	 *
	 * @param cMatrix 邻接矩阵，第0行，0列不算
	 * @param n       城市个数.
	 * @throws IllegalArgumentException 参数错误
	 */
	public int bb4TSP(int[][] cMatrix, int n) {
		//构造初始节点
		Vector<Integer> liveNode = new Vector<>();//城市排列
		for (int i = 1; i <= n; i++) liveNode.add(i);

		//深搜找到一个解作为上界
		int[] x = new int[n + 1];
		for (int i = 0; i <= n; i++)
			x[i] = i;
		backtrack(2, n, cMatrix, 0, x);

		if (minCost != Integer.MAX_VALUE) {  //由于采用了非零边，如果上界计算出来为0，则代表一个解都没有，直接退出
			int level = 1;//0-level的城市是已经排好的

			int lcost = computeLB(liveNode, level, cMatrix); //代价的下界
//            int lcost = computeSymLB(liveNode, level, cMatrix); //代价的下界

			HeapNode cNode = new HeapNode(liveNode, lcost, level);  //根节点
			priorHeap.add(cNode);

			while (level != n + 1 && !priorHeap.isEmpty()) {
				//TODO
				//参考优先队列，不停扩展节点,选取下一个节点
				cNode = priorHeap.remove();
				level = cNode.level;
				liveNode = new Vector<>(cNode.liveNode);

//                if (level == n) { //优先队列出来的是叶子，那么一定是解
//                    bestH = new Vector<>(cNode.liveNode);
//                    setMinCost(cNode.lcost);
//                    break;
//                }

				for (int j = level; j < n; ++j) {
					if (cMatrix[liveNode.get(level - 1)][liveNode.get(j)] != NoEdge) {
						int tmp = liveNode.get(level);  //level城的下一个
						liveNode.set(level, liveNode.get(j));
						liveNode.set(j, tmp);

						level++;
						lcost = computeLB(liveNode, level, cMatrix);
//                        lcost = computeSymLB(liveNode, level, cMatrix);
						if (lcost < getMinCost()) {
							cNode = new HeapNode(new Vector<>(liveNode), lcost, level);  //插入新节点
							priorHeap.add(cNode);

							if (level == n) { //如果新的点是叶子，那么更新界
								bestH = new Vector<>(cNode.liveNode);
								setMinCost(cNode.lcost);
							}
						}
						level--;

						tmp = liveNode.get(level);  //恢复现场
						liveNode.set(level, liveNode.get(j));
						liveNode.set(j, tmp);
					}
				}

				level++;    //当前层加一判断是否到叶节点
			}
		}

		//输出解向量
		System.out.print("The best path: " + bestH.toString());

		return minCost;
	}

	/**
	 * 这里只能深搜给出第一个可行解即可作为上界
	 * 因为测试用例中并不是完全图，所以没办法贪心每次找最小的值来得到更小的上界
	 * 因为那样的话在无边情况多的情况下容易适得其反，搜索更久
	 *
	 * @param i  初始深度
	 * @param n  城市数量
	 * @param a  邻接矩阵
	 * @param cc 当前cost
	 * @param x  当前城市排序
	 */
	public void backtrack(int i, int n, int[][] a, int cc, int[] x) {
		if (i > n) {
			//得到一个解，更新当前最小值 minCost，保存当前解，然后退出
			setMinCost(cc + a[x[i - 1]][1]);

//            /***************** try ********************/
//            setMinCost(16);

			bestH = new Vector<>();
			for (int tmp : x) {
				bestH.add(tmp);
			}
		} else {
			for (int j = i; j <= n; ++j) {
				swap(i, j, x);
				if (check(i, n, a, x)) {
					cc += a[x[i - 1]][x[i]];
					backtrack(i + 1, n, a, cc, x);
					if (minCost != Integer.MAX_VALUE)
						return;
					cc -= a[x[i - 1]][x[i]];
				}
				swap(i, j, x);
			}
		}
	}

	public void swap(int i, int j, int[] x) {
		if (i != j) {
			int temp = x[i];
			x[i] = x[j];
			x[j] = temp;
		}
	}

	public boolean check(int pos, int n, int[][] a, int[] x) {
		if (pos < n
				&& a[x[pos - 1]][x[pos]] != NoEdge) {

			return true;
		} else
			return pos == n
					&& a[x[pos - 1]][x[pos]] != NoEdge
					&& a[x[pos]][1] != NoEdge;
	}


}
