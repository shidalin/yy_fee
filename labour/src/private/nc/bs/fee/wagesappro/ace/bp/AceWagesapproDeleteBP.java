package nc.bs.fee.wagesappro.ace.bp;

import nc.bs.fee.wagesappro.plugin.bpplugin.WagesapproPluginPoint;
import nc.vo.fee.wagesappro.AggWagesapproVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * ��׼����ɾ��BP
 */
public class AceWagesapproDeleteBP {

	public void delete(AggWagesapproVO[] bills) {

		DeleteBPTemplate<AggWagesapproVO> bp = new DeleteBPTemplate<AggWagesapproVO>(
				WagesapproPluginPoint.DELETE);
		// ����ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggWagesapproVO> processer) {
		// TODO ǰ����
		IRule<AggWagesapproVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggWagesapproVO> processer) {
		// TODO �����

	}
}
