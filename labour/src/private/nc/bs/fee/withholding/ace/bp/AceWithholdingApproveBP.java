package nc.bs.fee.withholding.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.fee.withholding.AggWithholdingVO;

/**
 * ��׼������˵�BP
 */
public class AceWithholdingApproveBP {

	/**
	 * ��˶���
	 * 
	 * @param vos
	 * @param script
	 * @return
	 */
	public AggWithholdingVO[] approve(AggWithholdingVO[] clientBills,
			AggWithholdingVO[] originBills) {
		for (AggWithholdingVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggWithholdingVO> update = new BillUpdate<AggWithholdingVO>();
		AggWithholdingVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

}
