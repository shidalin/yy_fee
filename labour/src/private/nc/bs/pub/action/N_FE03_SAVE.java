package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.fee.wagesappro.plugin.bpplugin.WagesapproPluginPoint;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.itf.fee.IWagesapproMaintain;

public class N_FE03_SAVE extends AbstractPfAction<AggWagesapproVO> {

	protected CompareAroundProcesser<AggWagesapproVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggWagesapproVO> processor = new CompareAroundProcesser<AggWagesapproVO>(
				WagesapproPluginPoint.SEND_APPROVE);
		// TODO 在此处添加审核前后规则
		IRule<AggWagesapproVO> rule = new CommitStatusCheckRule();
		processor.addBeforeRule(rule);
		return processor;
	}

	@Override
	protected AggWagesapproVO[] processBP(Object userObj,
			AggWagesapproVO[] clientFullVOs, AggWagesapproVO[] originBills) {
		IWagesapproMaintain operator = NCLocator.getInstance().lookup(
				IWagesapproMaintain.class);
		AggWagesapproVO[] bills = null;
		try {
			bills = operator.save(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
