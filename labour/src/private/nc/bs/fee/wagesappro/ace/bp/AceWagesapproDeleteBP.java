package nc.bs.fee.wagesappro.ace.bp;

import nc.bs.fee.wagesappro.plugin.bpplugin.WagesapproPluginPoint;
import nc.vo.fee.wagesappro.AggWagesapproVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * 标准单据删除BP
 */
public class AceWagesapproDeleteBP {

	public void delete(AggWagesapproVO[] bills) {

		DeleteBPTemplate<AggWagesapproVO> bp = new DeleteBPTemplate<AggWagesapproVO>(
				WagesapproPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggWagesapproVO> processer) {
		// TODO 前规则
		IRule<AggWagesapproVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggWagesapproVO> processer) {
		// TODO 后规则

	}
}
