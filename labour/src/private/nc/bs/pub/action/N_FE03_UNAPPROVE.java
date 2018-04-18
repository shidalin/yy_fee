package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UnapproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.fee.wagesappro.plugin.bpplugin.WagesapproPluginPoint;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.itf.fee.IWagesapproMaintain;

public class N_FE03_UNAPPROVE extends AbstractPfAction<AggWagesapproVO> {

	@Override
	protected CompareAroundProcesser<AggWagesapproVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggWagesapproVO> processor = new CompareAroundProcesser<AggWagesapproVO>(
				WagesapproPluginPoint.UNAPPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UnapproveStatusCheckRule());

		return processor;
	}

	@Override
	protected AggWagesapproVO[] processBP(Object userObj,
			AggWagesapproVO[] clientFullVOs, AggWagesapproVO[] originBills) {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AggWagesapproVO[] bills = null;
		try {
			IWagesapproMaintain operator = NCLocator.getInstance()
					.lookup(IWagesapproMaintain.class);
			bills = operator.unapprove(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
