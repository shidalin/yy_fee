package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.fee.wagesappro.plugin.bpplugin.WagesapproPluginPoint;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.itf.fee.IWagesapproMaintain;

public class N_FE03_SAVEBASE extends AbstractPfAction<AggWagesapproVO> {

	@Override
	protected CompareAroundProcesser<AggWagesapproVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggWagesapproVO> processor = null;
		AggWagesapproVO[] clientFullVOs = (AggWagesapproVO[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggWagesapproVO>(
					WagesapproPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggWagesapproVO>(
					WagesapproPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		IRule<AggWagesapproVO> rule = null;

		return processor;
	}

	@Override
	protected AggWagesapproVO[] processBP(Object userObj,
			AggWagesapproVO[] clientFullVOs, AggWagesapproVO[] originBills) {

		AggWagesapproVO[] bills = null;
		try {
			IWagesapproMaintain operator = NCLocator.getInstance()
					.lookup(IWagesapproMaintain.class);
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
				bills = operator.update(clientFullVOs, originBills);
			} else {
				bills = operator.insert(clientFullVOs, originBills);
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}
}
