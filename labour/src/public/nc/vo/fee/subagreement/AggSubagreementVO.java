package nc.vo.fee.subagreement;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.fee.subagreement.SubagreementVO")

public class AggSubagreementVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggSubagreementVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public SubagreementVO getParentVO(){
	  	return (SubagreementVO)this.getParent();
	  }
	  
}