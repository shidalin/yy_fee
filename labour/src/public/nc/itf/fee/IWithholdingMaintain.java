package nc.itf.fee;

import java.util.Map;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public interface IWithholdingMaintain {

	public void delete(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException;

	public AggWithholdingVO[] insert(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException;

	public AggWithholdingVO[] update(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException;

	public AggWithholdingVO[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggWithholdingVO[] save(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException;

	public AggWithholdingVO[] unsave(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException;

	public AggWithholdingVO[] approve(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException;

	public AggWithholdingVO[] unapprove(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException;

	public void updateVOByFields(AggWithholdingVO[] vos, UFBoolean value)
			throws BusinessException;

	public void affirm(AggWithholdingVO[] vos, int messageType, UFBoolean value)
			throws BusinessException;

	public Map<String, Object> queryWithholdingRecord(String pk_subagrement)
			throws BusinessException;
}
