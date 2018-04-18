package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.fee.withholding.plugin.bpplugin.WithholdingPluginPoint;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.itf.fee.IWithholdingMaintain;

public class N_FE02_DELETE extends AbstractPfAction<AggWithholdingVO> {

	@Override
	protected CompareAroundProcesser<AggWithholdingVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggWithholdingVO> processor = new CompareAroundProcesser<AggWithholdingVO>(
				WithholdingPluginPoint.SCRIPT_DELETE);
		// TODO 在此处添加前后规则
		return processor;
	}

	@Override
	protected AggWithholdingVO[] processBP(Object userObj,
			AggWithholdingVO[] clientFullVOs, AggWithholdingVO[] originBills) {
		IWithholdingMaintain operator = NCLocator.getInstance().lookup(
				IWithholdingMaintain.class);
		try {
			operator.delete(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return clientFullVOs;
	}

}
