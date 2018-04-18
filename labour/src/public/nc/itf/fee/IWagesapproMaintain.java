package nc.itf.fee;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.vo.pub.BusinessException;

public interface IWagesapproMaintain {

	public void delete(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException;

	public AggWagesapproVO[] insert(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException;

	public AggWagesapproVO[] update(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException;

	public AggWagesapproVO[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggWagesapproVO[] save(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException;

	public AggWagesapproVO[] unsave(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException;

	public AggWagesapproVO[] approve(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException;

	public AggWagesapproVO[] unapprove(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException;
}
