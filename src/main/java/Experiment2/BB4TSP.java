package Experiment2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Vector;
import java.util.stream.IntStream;

public class BB4TSP {

	int NoEdge = -1; //��ʾû�б�
	private int minCost = Integer.MAX_VALUE; //��ǰ��С����

	public int getMinCost() {
		return minCost;
	}

	public void setMinCost(int minCost) {
		this.minCost = minCost;
	}

//    Comparator<HeapNode> cmp = new Comparator<HeapNode>() {
//        public int compare(HeapNode e1, HeapNode e2) {//�Ӵ�С����
//            return e2.lcost - e1.lcost;
//        }
//    };

	Comparator<HeapNode> cmp = Comparator.comparingInt(e -> e.lcost);

	private final PriorityQueue<HeapNode> priorHeap = new PriorityQueue<>(100, cmp);//�洢��ڵ�
	private Vector<Integer> bestH = new Vector<>();


	public static class HeapNode implements Comparable<HeapNode> {
		Vector<Integer> liveNode;//��������
		int lcost; //���۵��½�
		int level;//0-level�ĳ������Ѿ��źõ�

		//���췽��
		public HeapNode(Vector<Integer> node, int lb, int lev) {
			liveNode = new Vector<>(node);
			lcost = lb;
			level = lev;
		}

		@Override
		public int compareTo(HeapNode heapNode) {//��������, ÿһ��pollFirst
			int xu = heapNode.lcost;
			return Integer.compare(lcost, xu);
		}

		public boolean equals(Object x) {
			return (x instanceof HeapNode) && lcost == ((HeapNode) x).lcost;
		}

	}

	/**
	 * ����������ڼ�����ѡ·����ʵ�ʿ�����
	 *
	 * @param liveNode ���е�����
	 * @param level    ��ǰȷ���ĳ��еĸ���.
	 * @param cMatrix  �ڽӾ��󣬵�0�У�0�в���
	 * @return ��ѡ·���Ŀ���
	 */
	private int countSelectedPaths(Vector<Integer> liveNode, int level, int[][] cMatrix) {
		int result = 0;

		//��ǰ��ѡ��·�����
		for (int i : IntStream.range(1, level).toArray()) {
			result += cMatrix[liveNode.get(i - 1)][liveNode.get(i)];
		}

		//�����Ҷ�ӽڵ㣬ֱ�Ӽ���Ƿ���Ապϻ�·���������������������򷵻�һ��������ʾ���ɴ�
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
	 * ���㲿�ֽ���½硣
	 * ���������Ǵ���ǶԳƾ��󣬹ʲ�ȡ���зֱ�ȡ��Сֵ���㷨
	 *
	 * @param liveNode ���е�����
	 * @param level    ��ǰȷ���ĳ��еĸ���.
	 * @param cMatrix  �ڽӾ��󣬵�0�У�0�в���
	 * @throws IllegalArgumentException ��������
	 */
	public int computeLB(Vector<Integer> liveNode, int level, int[][] cMatrix) {
		//TODO
		int result = countSelectedPaths(liveNode, level, cMatrix);
		if (level == liveNode.size()) {
			return result;
		}

		//r1 ����·���ϵ���С��ȣ�����С
		int min = Integer.MAX_VALUE;
		for (int i = 2; i < cMatrix.length; ++i) {
			if (cMatrix[i][1] != NoEdge && cMatrix[i][1] < min) {  //�б�
				//����r1�����һ����δѡ�񣬿϶�����·����
				min = cMatrix[i][1];
			}
		}
		result += min;

		//rk ����·���ϵ���С����,����С
		min = Integer.MAX_VALUE;
		int rk = liveNode.get(level - 1);
		for (int j = 1; j < cMatrix[rk].length; ++j) {
			if (cMatrix[rk][j] != NoEdge && cMatrix[rk][j] < min) {  //�б�
				//����rk�ĳ���һ����δѡ�񣬿϶�����·����
				min = cMatrix[rk][j];
			}
		}
		result += min;

		//ʣ��� ��level+1 ��ʼ�����½���ͣ����ȣ�����С
		for (int i : IntStream.range(level, liveNode.size()).toArray()) {

			int rtmp = liveNode.get(i);

			min = Integer.MAX_VALUE;
			for (int j = 1; j < cMatrix[rtmp].length; ++j) {
				if (cMatrix[rtmp][j] != NoEdge && cMatrix[rtmp][j] < min) {    //�б�
					min = cMatrix[rtmp][j];
				}
			}
			result += min;
		}

		//ʣ�µĵ���½���ͣ���ȣ�����С
		for (int j : IntStream.range(level, liveNode.size()).toArray()) {

			int rtmp = liveNode.get(j);

			min = Integer.MAX_VALUE;
			for (int i = 1; i < cMatrix.length; ++i) {
				if (cMatrix[i][rtmp] != NoEdge && cMatrix[i][rtmp] < min) {    //�б�
					min = cMatrix[i][rtmp];
				}
			}
			result += min;
		}

		result++;   //����ȡ��
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
	 * ���㲿�ֽ���½硣
	 * �����Ǵ���Գƾ��󣬹ʲ�ȡȡ������Сֵ���㷨
	 *
	 * @param liveNode ���е�����
	 * @param level    ��ǰȷ���ĳ��еĸ���.
	 * @param cMatrix  �ڽӾ��󣬵�0�У�0�в���
	 * @throws IllegalArgumentException ��������
	 */
	public int computeSymLB(Vector<Integer> liveNode, int level, int[][] cMatrix) {
		//TODO
		int result = countSelectedPaths(liveNode, level, cMatrix);
		if (level == liveNode.size()) {
			return result;
		}


		if (level != 1) {//r1 ����·���ϵ���С��ȣ�����С
			int min = Integer.MAX_VALUE;
			for (int j = 1; j < cMatrix[1].length; ++j) {
				if (cMatrix[1][j] != NoEdge && cMatrix[1][j] < min && j != liveNode.get(1)) {  //�б��Ҳ���·����
					min = cMatrix[1][j];
				}
			}
			result += min;

			//rk ����·���ϵ���С����,����С
			min = Integer.MAX_VALUE;
//            for (int j = 1; j < cMatrix[level].length; ++j) {
//                if (cMatrix[level][j] != NoEdge && cMatrix[level][j] < min && j != liveNode.get(level - 2)) {  //�б��Ҳ���·����
//                    min = cMatrix[level][j];
//                }
//            }
			int rk = liveNode.get(level - 1);
			for (int j = 1; j < cMatrix[rk].length; ++j) {
				if (cMatrix[rk][j] != NoEdge && cMatrix[rk][j] < min && j != liveNode.get(level - 2)) {  //�б��Ҳ���·����
					min = cMatrix[rk][j];
				}
			}
			result += min;
		} else {
			int min1 = Integer.MAX_VALUE;
			int min2 = Integer.MAX_VALUE;
			for (int j = 1; j < cMatrix[1].length; ++j) {
				if (cMatrix[1][j] != NoEdge && cMatrix[1][j] < min1) {    //�б�
					min2 = min1;
					min1 = cMatrix[1][j];
				} else if (cMatrix[1][j] != NoEdge && cMatrix[1][j] < min2) {
					min2 = cMatrix[1][j];
				}
			}
			result += min1;
			result += min2;
		}

		//ʣ��� ��level+1 ��ʼ�����½���ͣ����ȣ�����С
		for (int i : IntStream.range(level, liveNode.size()).toArray()) {

			int rtmp = liveNode.get(i);

			int min1 = Integer.MAX_VALUE;
			int min2 = Integer.MAX_VALUE;
			for (int j = 1; j < cMatrix[rtmp].length; ++j) {
				if (cMatrix[rtmp][j] != NoEdge && cMatrix[rtmp][j] < min1) {    //�б�
					min2 = min1;
					min1 = cMatrix[rtmp][j];
				} else if (cMatrix[rtmp][j] != NoEdge && cMatrix[rtmp][j] < min2) {
					min2 = cMatrix[rtmp][j];
				}
			}
			result += min1;
			result += min2;
		}

		result++;   //����ȡ��
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
	 * ����TSP�������С���۵�·��.
	 *
	 * @param cMatrix �ڽӾ��󣬵�0�У�0�в���
	 * @param n       ���и���.
	 * @throws IllegalArgumentException ��������
	 */
	public int bb4TSP(int[][] cMatrix, int n) {
		//�����ʼ�ڵ�
		Vector<Integer> liveNode = new Vector<>();//��������
		for (int i = 1; i <= n; i++) liveNode.add(i);

		//�����ҵ�һ������Ϊ�Ͻ�
		int[] x = new int[n + 1];
		for (int i = 0; i <= n; i++)
			x[i] = i;
		backtrack(2, n, cMatrix, 0, x);

		if (minCost != Integer.MAX_VALUE) {  //���ڲ����˷���ߣ�����Ͻ�������Ϊ0�������һ���ⶼû�У�ֱ���˳�
			int level = 1;//0-level�ĳ������Ѿ��źõ�

			int lcost = computeLB(liveNode, level, cMatrix); //���۵��½�
//            int lcost = computeSymLB(liveNode, level, cMatrix); //���۵��½�

			HeapNode cNode = new HeapNode(liveNode, lcost, level);  //���ڵ�
			priorHeap.add(cNode);

			while (level != n + 1 && !priorHeap.isEmpty()) {
				//TODO
				//�ο����ȶ��У���ͣ��չ�ڵ�,ѡȡ��һ���ڵ�
				cNode = priorHeap.remove();
				level = cNode.level;
				liveNode = new Vector<>(cNode.liveNode);

//                if (level == n) { //���ȶ��г�������Ҷ�ӣ���ôһ���ǽ�
//                    bestH = new Vector<>(cNode.liveNode);
//                    setMinCost(cNode.lcost);
//                    break;
//                }

				for (int j = level; j < n; ++j) {
					if (cMatrix[liveNode.get(level - 1)][liveNode.get(j)] != NoEdge) {
						int tmp = liveNode.get(level);  //level�ǵ���һ��
						liveNode.set(level, liveNode.get(j));
						liveNode.set(j, tmp);

						level++;
						lcost = computeLB(liveNode, level, cMatrix);
//                        lcost = computeSymLB(liveNode, level, cMatrix);
						if (lcost < getMinCost()) {
							cNode = new HeapNode(new Vector<>(liveNode), lcost, level);  //�����½ڵ�
							priorHeap.add(cNode);

							if (level == n) { //����µĵ���Ҷ�ӣ���ô���½�
								bestH = new Vector<>(cNode.liveNode);
								setMinCost(cNode.lcost);
							}
						}
						level--;

						tmp = liveNode.get(level);  //�ָ��ֳ�
						liveNode.set(level, liveNode.get(j));
						liveNode.set(j, tmp);
					}
				}

				level++;    //��ǰ���һ�ж��Ƿ�Ҷ�ڵ�
			}
		}

		//���������
		System.out.print("The best path: " + bestH.toString());

		return minCost;
	}

	/**
	 * ����ֻ�����Ѹ�����һ�����н⼴����Ϊ�Ͻ�
	 * ��Ϊ���������в�������ȫͼ������û�취̰��ÿ������С��ֵ���õ���С���Ͻ�
	 * ��Ϊ�����Ļ����ޱ�����������������ʵ��䷴����������
	 *
	 * @param i  ��ʼ���
	 * @param n  ��������
	 * @param a  �ڽӾ���
	 * @param cc ��ǰcost
	 * @param x  ��ǰ��������
	 */
	public void backtrack(int i, int n, int[][] a, int cc, int[] x) {
		if (i > n) {
			//�õ�һ���⣬���µ�ǰ��Сֵ minCost�����浱ǰ�⣬Ȼ���˳�
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
