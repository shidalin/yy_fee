package nc.bs.fee.wagesappro.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据送审的BP
 */
public class AceWagesapproSendApproveBP {
	/**
	 * 送审动作
	 * 
	 * @param vos
	 *            单据VO数组
	 * @param script
	 *            单据动作脚本对象
	 * @return 送审后的单据VO数组
	 */

	public AggWagesapproVO[] sendApprove(AggWagesapproVO[] clientBills,
			AggWagesapproVO[] originBills) {
		for (AggWagesapproVO clientFullVO : clientBills) {
			clientFullVO.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.COMMIT.value());
			clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// 数据持久化
		AggWagesapproVO[] returnVos = new BillUpdate<AggWagesapproVO>().update(
				clientBills, originBills);
		return returnVos;
	}
}
