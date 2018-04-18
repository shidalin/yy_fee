package nc.vo.fee.withholding;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggWithholdingVOMeta extends AbstractBillMeta{
	
	public AggWithholdingVOMeta(){
		this.init();
	}
	
	private void init() {
		this.setParent(nc.vo.fee.withholding.WithholdingVO.class);
	}
}