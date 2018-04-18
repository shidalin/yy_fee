package nc.impl.fee;

import java.util.ArrayList;
import java.util.Map;

import nc.impl.pub.ace.AceSubagreementPubServiceImpl;
import nc.itf.fee.ISubagreementMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.ct.ar.entity.AggCtArVO;
import nc.vo.fee.subagreement.AggSubagreementVO;
import nc.vo.fee.subagreement.SubagreementVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class SubagreementMaintainImpl extends AceSubagreementPubServiceImpl
		implements ISubagreementMaintain {

	@Override
	public void delete(AggSubagreementVO[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
	}

	@Override
	public AggSubagreementVO[] insert(AggSubagreementVO[] vos)
			throws BusinessException {
		return super.pubinsertBills(vos);
	}

	@Override
	public AggSubagreementVO[] update(AggSubagreementVO[] vos)
			throws BusinessException {
		return super.pubupdateBills(vos);
	}

	@Override
	public AggSubagreementVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public Map<String, String> queryPsnDeptByUser(String pk_user)
			throws BusinessException {
		// TODO Auto-generated method stub
		return super.queryPsnDeptByUser(pk_user);
	}

	@Override
	public AggSubagreementVO[] queryRef(IQueryScheme queryScheme)
			throws BusinessException {
		// TODO Auto-generated method stub
		return super.queryRef(queryScheme);
	}

	@Override
	public AggSubagreementVO[] queryForFE02(String year, String mouth)
			throws BusinessException {
		// TODO Auto-generated method stub
		try {
			return super.queryForFE02(year, mouth);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	@Override
	public String queryProjectPK(String code) throws BusinessException {
		// TODO Auto-generated method stub
		try {
			return super.queryProjectPK(code);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtils.marsh(e);
		}
		return code;
	}

	@Override
	public Map<String, String> getCodeByCustomerpk(ArrayList<String> params)
			throws BusinessException {
		// TODO Auto-generated method stub
		try {
			return super.getCodeByCustomerpk(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	@Override
	public void writeBackForWithholding(ArrayList<SubagreementVO> writeBackList,String[] fieldNames)
			throws BusinessException {
		try {
			super.writeBackForWithholding(writeBackList,fieldNames);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtils.marsh(e);
		}

	}

	@Override
	public AggCtArVO[] queryRefVOS(IQueryScheme queryScheme)
			throws BusinessException {
		// TODO Auto-generated method stub
		return super.querRefVOS(queryScheme);
	}

}
