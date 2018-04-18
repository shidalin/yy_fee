package nc.bs.fee.subagreement.ace.bp;

import nc.bs.fee.subagreement.plugin.bpplugin.SubagreementPluginPoint;
import nc.vo.fee.subagreement.AggSubagreementVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;

/**
 * 标准单据删除BP
 */
public class AceSubagreementDeleteBP {

	public void delete(AggSubagreementVO[] bills) {

		DeleteBPTemplate<AggSubagreementVO> bp = new DeleteBPTemplate<AggSubagreementVO>(
				SubagreementPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggSubagreementVO> processer) {
		// TODO 前规则
		IRule<AggSubagreementVO> rule = null;
		rule = new nc.bs.fee.subagreement.ace.rule.FE01DeleteBeforeRule();
		processer.addBeforeRule(rule);

	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggSubagreementVO> processer) {
		// TODO 后规则
		IRule<AggSubagreementVO> rule = null;
		rule = new nc.bs.fee.subagreement.ace.rule.FE01DeleteAfterRule();
		processer.addAfterRule(rule);
	}
}
