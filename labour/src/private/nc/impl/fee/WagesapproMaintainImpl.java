package nc.impl.fee;

import nc.impl.pub.ace.AceWagesapproPubServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.itf.fee.IWagesapproMaintain;
import nc.vo.pub.BusinessException;

public class WagesapproMaintainImpl extends AceWagesapproPubServiceImpl
		implements IWagesapproMaintain {

	@Override
	public void delete(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggWagesapproVO[] insert(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggWagesapproVO[] update(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggWagesapproVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggWagesapproVO[] save(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWagesapproVO[] unsave(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWagesapproVO[] approve(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWagesapproVO[] unapprove(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

}
