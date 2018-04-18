package nc.bs.fee.wagesappro.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * ��׼���������BP
 */
public class AceWagesapproSendApproveBP {
	/**
	 * ������
	 * 
	 * @param vos
	 *            ����VO����
	 * @param script
	 *            ���ݶ����ű�����
	 * @return �����ĵ���VO����
	 */

	public AggWagesapproVO[] sendApprove(AggWagesapproVO[] clientBills,
			AggWagesapproVO[] originBills) {
		for (AggWagesapproVO clientFullVO : clientBills) {
			clientFullVO.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.COMMIT.value());
			clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// ���ݳ־û�
		AggWagesapproVO[] returnVos = new BillUpdate<AggWagesapproVO>().update(
				clientBills, originBills);
		return returnVos;
	}
}
