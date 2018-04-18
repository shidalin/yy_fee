package nc.bs.fee.subagreement.ace.bp;

import nc.bs.fee.subagreement.plugin.bpplugin.SubagreementPluginPoint;
import nc.vo.fee.subagreement.AggSubagreementVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;

/**
 * ��׼����ɾ��BP
 */
public class AceSubagreementDeleteBP {

	public void delete(AggSubagreementVO[] bills) {

		DeleteBPTemplate<AggSubagreementVO> bp = new DeleteBPTemplate<AggSubagreementVO>(
				SubagreementPluginPoint.DELETE);
		// ����ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggSubagreementVO> processer) {
		// TODO ǰ����
		IRule<AggSubagreementVO> rule = null;
		rule = new nc.bs.fee.subagreement.ace.rule.FE01DeleteBeforeRule();
		processer.addBeforeRule(rule);

	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggSubagreementVO> processer) {
		// TODO �����
		IRule<AggSubagreementVO> rule = null;
		rule = new nc.bs.fee.subagreement.ace.rule.FE01DeleteAfterRule();
		processer.addAfterRule(rule);
	}
}
