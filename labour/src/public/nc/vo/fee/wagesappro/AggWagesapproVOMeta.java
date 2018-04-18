package nc.vo.fee.wagesappro;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggWagesapproVOMeta extends AbstractBillMeta{
	
	public AggWagesapproVOMeta(){
		this.init();
	}
	
	private void init() {
		this.setParent(nc.vo.fee.wagesappro.WagesapproVO.class);
		this.addChildren(nc.vo.fee.wagesappro.WagesapproBVO.class);
	}
}