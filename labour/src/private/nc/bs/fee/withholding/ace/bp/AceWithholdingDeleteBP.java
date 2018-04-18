package nc.bs.fee.withholding.ace.bp;

import nc.bs.fee.withholding.plugin.bpplugin.WithholdingPluginPoint;
import nc.vo.fee.withholding.AggWithholdingVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;

/**
 * 标准单据删除BP
 */
public class AceWithholdingDeleteBP {

	public void delete(AggWithholdingVO[] bills) {

		DeleteBPTemplate<AggWithholdingVO> bp = new DeleteBPTemplate<AggWithholdingVO>(
				WithholdingPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggWithholdingVO> processer) {
		// TODO 前规则
		IRule<AggWithholdingVO> rule = null;
		rule = new nc.bs.fee.withholding.ace.rule.DeleteCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggWithholdingVO> processer) {
		// TODO 后规则 
		IRule<AggWithholdingVO> rule = null;
		rule = new  nc.bs.fee.withholding.ace.rule.DeleteWriteBackRule();
		processer.addAfterRule(rule);
	}
}
