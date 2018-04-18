package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.fee.withholding.plugin.bpplugin.WithholdingPluginPoint;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.itf.fee.IWithholdingMaintain;

public class N_FE02_SAVEBASE extends AbstractPfAction<AggWithholdingVO> {

	@Override
	protected CompareAroundProcesser<AggWithholdingVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggWithholdingVO> processor = null;
		AggWithholdingVO[] clientFullVOs = (AggWithholdingVO[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggWithholdingVO>(
					WithholdingPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggWithholdingVO>(
					WithholdingPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		IRule<AggWithholdingVO> rule = null;

		return processor;
	}

	@Override
	protected AggWithholdingVO[] processBP(Object userObj,
			AggWithholdingVO[] clientFullVOs, AggWithholdingVO[] originBills) {

		AggWithholdingVO[] bills = null;
		try {
			IWithholdingMaintain operator = NCLocator.getInstance()
					.lookup(IWithholdingMaintain.class);
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
