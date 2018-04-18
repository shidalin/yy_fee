package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.fee.wagesappro.plugin.bpplugin.WagesapproPluginPoint;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.itf.fee.IWagesapproMaintain;

public class N_FE03_APPROVE extends AbstractPfAction<AggWagesapproVO> {

	public N_FE03_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggWagesapproVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggWagesapproVO> processor = new CompareAroundProcesser<AggWagesapproVO>(
				WagesapproPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		processor
				.addAfterRule(new nc.bs.fee.wagesappro.ace.rule.ApprovedAfterRule());
		return processor;
	}

	@Override
	protected AggWagesapproVO[] processBP(Object userObj,
			AggWagesapproVO[] clientFullVOs, AggWagesapproVO[] originBills) {
		AggWagesapproVO[] bills = null;
		IWagesapproMaintain operator = NCLocator.getInstance().lookup(
				IWagesapproMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
