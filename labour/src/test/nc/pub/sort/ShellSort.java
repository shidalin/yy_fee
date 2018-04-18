package nc.pub.sort;

public class ShellSort {

	/**
	 * ϣ������
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Integer[] a = new Integer[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
		shellSort(a);
		for (Integer i : a) {
			System.out.println(i);
		}
	}

	public static Integer[] shellSort(Integer[] a) {
		int j;
		// ��ֵ���� N/2 ����㹹����ֵ����
		for (int gap = a.length / 2; gap > 0; gap /= 2) {
			// �ڲ�Ϊ��������
			for (int i = gap; i < a.length; i++) {
				Integer temp = a[i];
				// �ڲ���������
				for (j = i; j >= gap && temp.compareTo(a[j - gap]) < 0; j -= gap) {
					a[j] = a[j - gap];
					a[j - gap] = temp;
				}
			}
			for (Integer m : a) {
				System.out.println(m);
			}
			System.out.println("============");
		}
		return a;
	}
}
