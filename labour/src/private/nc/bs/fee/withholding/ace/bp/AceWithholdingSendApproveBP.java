package nc.bs.fee.withholding.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * ��׼���������BP
 */
public class AceWithholdingSendApproveBP {
	/**
	 * ������
	 * 
	 * @param vos
	 *            ����VO����
	 * @param script
	 *            ���ݶ����ű�����
	 * @return �����ĵ���VO����
	 */

	public AggWithholdingVO[] sendApprove(AggWithholdingVO[] clientBills,
			AggWithholdingVO[] originBills) {
		for (AggWithholdingVO clientFullVO : clientBills) {
			clientFullVO.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.COMMIT.value());
			clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// ���ݳ־û�
		AggWithholdingVO[] returnVos = new BillUpdate<AggWithholdingVO>().update(
				clientBills, originBills);
		return returnVos;
	}
}
