package nc.bs.fee.withholding.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AceWithholdingUnSendApproveBP {

	public AggWithholdingVO[] unSend(AggWithholdingVO[] clientBills,
			AggWithholdingVO[] originBills) {
		// 把VO持久化到数据库中
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggWithholdingVO> update = new BillUpdate<AggWithholdingVO>();
		AggWithholdingVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggWithholdingVO[] clientBills) {
		for (AggWithholdingVO clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
