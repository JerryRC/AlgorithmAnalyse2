package Experiment2;

import java.util.Arrays;

public class Back4TSP {

	int NoEdge = -1;
	int bigInt = Integer.MAX_VALUE;
	int[][] a; // �ڽӾ���
	int cc = 0; // �洢��ǰ����
	int bestC = bigInt;// ��ǰ���Ŵ���
	int[] x; // ��ǰ��
	int[] bestX;// ��ǰ���Ž�
	int n = 0; // �������

	public int getBestC(){
		return bestC;
	}

	private void backtrack(int i) {//iΪ��ʼ���
		if (i > n) {
			//TODO
			bestC = cc + a[x[i - 1]][1];
			bestX = x.clone();
		} else {
			//TODO
			for (int j = i; j <= n; ++j) {
				swap(i, j);
				if (check(i)) {
					backtrack(i + 1);
					cc -= a[x[i - 1]][x[i]];
				}
				swap(i, j);
			}
		}

	}

	private void swap(int i, int j) {
		if (i != j) {
			int temp = x[i];
			x[i] = x[j];
			x[j] = temp;
		}
	}

	public boolean check(int pos) {
		//TODO
		if (pos < n
				&& a[x[pos - 1]][x[pos]] != NoEdge
				&& cc + a[x[pos - 1]][x[pos]] < bestC) {

			cc += a[x[pos - 1]][x[pos]];
			return true;
		} else if (pos == n
				&& a[x[pos - 1]][x[pos]] != NoEdge
				&& a[x[pos]][1] != NoEdge
				&& cc + a[x[pos - 1]][x[pos]] + a[x[pos]][1] < bestC) {

			cc = cc + a[x[pos - 1]][x[pos]];
			return true;
		}
		return false;
	}

	public void backtrack4TSP(int[][] b, int num) {
		n = num;
		x = new int[n + 1];
		for (int i = 0; i <= n; i++)
			x[i] = i;
		bestX = new int[n + 1];
		a = b;
		backtrack(2);
		System.out.println("The best path: " + Arrays.toString(bestX));

	}

}
