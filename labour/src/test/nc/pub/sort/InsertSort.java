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
	 * ��������
	 * 
	 * @param params
	 * @return
	 */
	public static Integer[] insertSort(Integer[] params) {
		int j;
		for (int p = 1; p < params.length; p++) {
			// ���ȽϵĶ���ÿ�ζ���temp����ǰp+1��Ԫ���е���ȷλ�ã�����Ƚ�
			Integer temp = params[p];
			// ѭ���Ƚ�ǰP��������С�����飬����Ƚ�
			for (j = p; j > 0 && temp.compareTo(params[j - 1]) < 0; j--) {
				// ����ǰ��һ��
				params[j] = params[j - 1];
				// С������һ��
				params[j - 1] = temp;
			}

		}
		return params;
	}
}
