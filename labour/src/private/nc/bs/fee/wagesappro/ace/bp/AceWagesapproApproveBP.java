package nc.bs.fee.wagesappro.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.fee.wagesappro.AggWagesapproVO;

/**
 * ��׼������˵�BP
 */
public class AceWagesapproApproveBP {

	/**
	 * ��˶���
	 * 
	 * @param vos
	 * @param script
	 * @return
	 */
	public AggWagesapproVO[] approve(AggWagesapproVO[] clientBills,
			AggWagesapproVO[] originBills) {
		for (AggWagesapproVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggWagesapproVO> update = new BillUpdate<AggWagesapproVO>();
		AggWagesapproVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

}
