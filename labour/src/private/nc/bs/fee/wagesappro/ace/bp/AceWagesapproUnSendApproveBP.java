package nc.bs.fee.wagesappro.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AceWagesapproUnSendApproveBP {

	public AggWagesapproVO[] unSend(AggWagesapproVO[] clientBills,
			AggWagesapproVO[] originBills) {
		// 把VO持久化到数据库中
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggWagesapproVO> update = new BillUpdate<AggWagesapproVO>();
		AggWagesapproVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggWagesapproVO[] clientBills) {
		for (AggWagesapproVO clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
