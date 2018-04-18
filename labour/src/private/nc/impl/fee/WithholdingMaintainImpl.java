package nc.impl.fee;

import java.util.Map;

import nc.impl.pub.ace.AceWithholdingPubServiceImpl;
import nc.itf.fee.IWithholdingMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class WithholdingMaintainImpl extends AceWithholdingPubServiceImpl
		implements IWithholdingMaintain {

	@Override
	public void delete(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggWithholdingVO[] insert(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggWithholdingVO[] update(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggWithholdingVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggWithholdingVO[] save(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWithholdingVO[] unsave(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWithholdingVO[] approve(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggWithholdingVO[] unapprove(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	@Override
	public void updateVOByFields(AggWithholdingVO[] vos, UFBoolean value)
			throws BusinessException {
		try {
			super.updateVOByFields(vos, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtils.marsh(e);
		}

	}

	@Override
	public void affirm(AggWithholdingVO[] vos, int messageType, UFBoolean value)
			throws BusinessException {
		try {
			super.affirm(vos, messageType, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtils.marsh(e);
		}

	}

	@Override
	public Map<String, Object> queryWithholdingRecord(String pk_subagrement)
			throws BusinessException {
		// TODO Auto-generated method stub
		try {
			return super.queryWithholdingRecord(pk_subagrement);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtils.marsh(e);
		}
		return null;
	}
}
