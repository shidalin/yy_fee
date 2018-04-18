package nc.vo.fee.withholding;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.fee.withholding.WithholdingVO")

public class AggWithholdingVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggWithholdingVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public WithholdingVO getParentVO(){
	  	return (WithholdingVO)this.getParent();
	  }
	  
}