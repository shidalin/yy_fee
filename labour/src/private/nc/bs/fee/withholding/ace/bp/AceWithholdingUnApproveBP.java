package nc.bs.fee.withholding.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AceWithholdingUnApproveBP {

	public AggWithholdingVO[] unApprove(AggWithholdingVO[] clientBills,
			AggWithholdingVO[] originBills) {
		for (AggWithholdingVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggWithholdingVO> update = new BillUpdate<AggWithholdingVO>();
		AggWithholdingVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
