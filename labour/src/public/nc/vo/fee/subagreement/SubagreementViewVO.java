package nc.vo.fee.subagreement;

import nc.vo.pubapp.pattern.model.entity.view.AbstractDataView;
import nc.vo.pubapp.pattern.model.meta.entity.view.DataViewMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.view.IDataViewMeta;

/**
 * 人工分包协议视图VO
 * 
 * @author shidalin
 * 
 */
public class SubagreementViewVO extends AbstractDataView {

	public SubagreementViewVO() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IDataViewMeta getMetaData() {
		// TODO Auto-generated method stub
		return DataViewMetaFactory.getInstance().getBillViewMeta(
				AggSubagreementVO.class);
	}


}
