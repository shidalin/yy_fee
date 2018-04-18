package nc.bs.fee.withholding.ace.bp;

import nc.bs.fee.withholding.plugin.bpplugin.WithholdingPluginPoint;
import nc.vo.fee.withholding.AggWithholdingVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;

/**
 * ��׼����ɾ��BP
 */
public class AceWithholdingDeleteBP {

	public void delete(AggWithholdingVO[] bills) {

		DeleteBPTemplate<AggWithholdingVO> bp = new DeleteBPTemplate<AggWithholdingVO>(
				WithholdingPluginPoint.DELETE);
		// ����ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggWithholdingVO> processer) {
		// TODO ǰ����
		IRule<AggWithholdingVO> rule = null;
		rule = new nc.bs.fee.withholding.ace.rule.DeleteCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggWithholdingVO> processer) {
		// TODO ����� 
		IRule<AggWithholdingVO> rule = null;
		rule = new  nc.bs.fee.withholding.ace.rule.DeleteWriteBackRule();
		processer.addAfterRule(rule);
	}
}
