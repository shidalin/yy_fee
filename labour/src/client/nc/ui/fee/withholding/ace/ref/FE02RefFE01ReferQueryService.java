package nc.ui.fee.withholding.ace.ref;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.ISubagreementMaintain;
import nc.ui.pubapp.uif2app.query2.model.IRefQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.fee.subagreement.AggSubagreementVO;

public class FE02RefFE01ReferQueryService implements IRefQueryService {

	public FE02RefFE01ReferQueryService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		ISubagreementMaintain queryService = (ISubagreementMaintain) NCLocator
				.getInstance().lookup(ISubagreementMaintain.class);
		AggSubagreementVO[] aggs = queryService.queryRef(queryScheme);
		return aggs;
	}

	@Override
	public Object[] queryByWhereSql(String arg0) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}
}
