package nc.pub.sort;

public class InsertSort {

	public InsertSort() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		Integer[] params = new Integer[] { 89, 56, 48, 20 };
		insertSort(params);
		for (Integer param : params) {
			System.out.println(param);
		}
	}

	/**
	 * 插入排序
	 * 
	 * @param params
	 * @return
	 */
	public static Integer[] insertSort(Integer[] params) {
		int j;
		for (int p = 1; p < params.length; p++) {
			// 被比较的对象，每次都把temp放在前p+1个元素中的正确位置，正向比较
			Integer temp = params[p];
			// 循环比较前P个参数的小的数组，逆向比较
			for (j = p; j > 0 && temp.compareTo(params[j - 1]) < 0; j--) {
				// 大数前移一格
				params[j] = params[j - 1];
				// 小数后退一格
				params[j - 1] = temp;
			}

		}
		return params;
	}
}
