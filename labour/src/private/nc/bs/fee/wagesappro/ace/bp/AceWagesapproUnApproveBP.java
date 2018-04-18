package nc.bs.fee.wagesappro.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AceWagesapproUnApproveBP {

	public AggWagesapproVO[] unApprove(AggWagesapproVO[] clientBills,
			AggWagesapproVO[] originBills) {
		for (AggWagesapproVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggWagesapproVO> update = new BillUpdate<AggWagesapproVO>();
		AggWagesapproVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
