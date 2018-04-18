package nc.vo.fee.wagesappro;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.fee.wagesappro.WagesapproVO")

public class AggWagesapproVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggWagesapproVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public WagesapproVO getParentVO(){
	  	return (WagesapproVO)this.getParent();
	  }
	  
}