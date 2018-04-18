package nc.itf.fee;

import java.util.ArrayList;
import java.util.Map;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.ct.ar.entity.AggCtArVO;
import nc.vo.fee.subagreement.AggSubagreementVO;
import nc.vo.fee.subagreement.SubagreementVO;
import nc.vo.pub.BusinessException;

public interface ISubagreementMaintain {

	public void delete(AggSubagreementVO[] vos) throws BusinessException;

	public AggSubagreementVO[] insert(AggSubagreementVO[] vos)
			throws BusinessException;

	public AggSubagreementVO[] update(AggSubagreementVO[] vos)
			throws BusinessException;

	public AggSubagreementVO[] query(IQueryScheme queryScheme)
			throws BusinessException;

	public AggSubagreementVO[] queryRef(IQueryScheme queryScheme)
			throws BusinessException;

	/**
	 * 根据用户主键查询用户关联的人员及部门信息
	 * 
	 * @param pk_user
	 * @return
	 */
	public Map<String, String> queryPsnDeptByUser(String pk_user)
			throws BusinessException;

	public AggSubagreementVO[] queryForFE02(String year, String mouth)
			throws BusinessException;

	public String queryProjectPK(String Code) throws BusinessException;

	public Map<String, String> getCodeByCustomerpk(ArrayList<String> params)
			throws BusinessException;

	public void writeBackForWithholding(
			ArrayList<SubagreementVO> writeBackList, String[] fieldNames)
			throws BusinessException;

	public AggCtArVO[] queryRefVOS(IQueryScheme queryScheme)
			throws BusinessException;
}
