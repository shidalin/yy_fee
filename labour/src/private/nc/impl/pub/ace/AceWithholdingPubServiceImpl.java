package nc.impl.pub.ace;

import java.util.ArrayList;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.fee.withholding.ace.bp.AceWithholdingApproveBP;
import nc.bs.fee.withholding.ace.bp.AceWithholdingDeleteBP;
import nc.bs.fee.withholding.ace.bp.AceWithholdingInsertBP;
import nc.bs.fee.withholding.ace.bp.AceWithholdingSendApproveBP;
import nc.bs.fee.withholding.ace.bp.AceWithholdingUnApproveBP;
import nc.bs.fee.withholding.ace.bp.AceWithholdingUnSendApproveBP;
import nc.bs.fee.withholding.ace.bp.AceWithholdingUpdateBP;
import nc.bs.framework.common.NCLocator;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.itf.fee.IWithholdingMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.MapProcessor;
import nc.pubitf.fip.service.IFipMessageService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.vo.fee.withholding.WithholdingVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceWithholdingPubServiceImpl {
	// ����
	public AggWithholdingVO[] pubinsertBills(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		try {
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<AggWithholdingVO> transferTool = new BillTransferTool<AggWithholdingVO>(
					clientFullVOs);
			// ����BP
			AceWithholdingInsertBP action = new AceWithholdingInsertBP();
			AggWithholdingVO[] retvos = action.insert(clientFullVOs);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ��
	public void pubdeleteBills(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		try {
			// ����BP
			new AceWithholdingDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	public AggWithholdingVO[] pubupdateBills(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<AggWithholdingVO> transferTool = new BillTransferTool<AggWithholdingVO>(
					clientFullVOs);
			AceWithholdingUpdateBP bp = new AceWithholdingUpdateBP();
			AggWithholdingVO[] retvos = bp.update(clientFullVOs, originBills);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggWithholdingVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggWithholdingVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggWithholdingVO> query = new BillLazyQuery<AggWithholdingVO>(
					AggWithholdingVO.class);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	/**
	 * ������ʵ�֣���ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// ��ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	}

	// �ύ
	public AggWithholdingVO[] pubsendapprovebills(
			AggWithholdingVO[] clientFullVOs, AggWithholdingVO[] originBills)
			throws BusinessException {
		AceWithholdingSendApproveBP bp = new AceWithholdingSendApproveBP();
		AggWithholdingVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// �ջ�
	public AggWithholdingVO[] pubunsendapprovebills(
			AggWithholdingVO[] clientFullVOs, AggWithholdingVO[] originBills)
			throws BusinessException {
		AceWithholdingUnSendApproveBP bp = new AceWithholdingUnSendApproveBP();
		AggWithholdingVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// ����
	public AggWithholdingVO[] pubapprovebills(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceWithholdingApproveBP bp = new AceWithholdingApproveBP();
		AggWithholdingVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// ����

	public AggWithholdingVO[] pubunapprovebills(
			AggWithholdingVO[] clientFullVOs, AggWithholdingVO[] originBills)
			throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceWithholdingUnApproveBP bp = new AceWithholdingUnApproveBP();
		AggWithholdingVO[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

	/**
	 * ��������
	 * 
	 * @param vos
	 * @param fields
	 */
	public void updateVOByFields(AggWithholdingVO[] vos, UFBoolean value)
			throws BusinessException {
		BaseDAO baseDAO = new BaseDAO();
		// ��Ҫ���µ��ֶ�
		String[] fields = new String[] { "isaffirm" };
		ArrayList<WithholdingVO> headList = new ArrayList<WithholdingVO>();
		for (AggWithholdingVO agg : vos) {
			WithholdingVO parentVO = agg.getParentVO();
			parentVO.setIsaffirm(value);
			headList.add(parentVO);
		}
		baseDAO.updateVOArray(headList.toArray(new WithholdingVO[0]), fields);

	}


	public void affirm(AggWithholdingVO[] vos, int messageType, UFBoolean value)
			throws BusinessException {
		IWithholdingMaintain itf = NCLocator.getInstance().lookup(
				IWithholdingMaintain.class);
		// ��̨��������
		itf.updateVOByFields(vos, value);
		// �������ݵ����ƽ̨
		// IFipMessageService lookup = NCLocator.getInstance().lookup(
		// IFipMessageService.class);
		ArrayList<FipMessageVO> arrayList = new ArrayList<FipMessageVO>();
		for (AggWithholdingVO agg : vos) {
			WithholdingVO hvo = agg.getParentVO();
			// ��������1
			FipRelationInfoVO reVO = new FipRelationInfoVO();
			reVO.setPk_group(hvo.getPk_group());
			reVO.setPk_org(hvo.getPk_org());
			reVO.setRelationID(hvo.getPk_withholding());
			reVO.setPk_system("FEE");
			reVO.setBusidate(hvo.getDbilldate() == null ? AppContext
					.getInstance().getBusiDate() : hvo.getDbilldate());
			reVO.setPk_billtype(hvo.getPk_billtypecode() == null ? "FE02" : hvo
					.getPk_billtypecode());
			reVO.setPk_operator(hvo.getCreator());
			// ��Դ���
			reVO.setFreedef3(hvo.getWithholdingmny() == null ? "" : hvo
					.getWithholdingmny().toString());
			// ��Դ���ݺ�
			reVO.setFreedef1(hvo.getVbillcode());
			// ��������2
			FipMessageVO fipMessageVO = new FipMessageVO();
			fipMessageVO.setMessagetype(messageType);
			fipMessageVO.setBillVO(agg);
			fipMessageVO.setMessageinfo(reVO);
			arrayList.add(fipMessageVO);
			NCLocator.getInstance().lookup(IFipMessageService.class)
					.sendMessage(fipMessageVO);
			// ��̨��ѯ���� �� fip_relation
		}
		// FipMessageVO[] fmvos = arrayList.toArray(new FipMessageVO[0]);
		// lookup.sendMessages(fmvos);
	}

	/**
	 * ��ѯ��ʷԤ���¼�����һ��Ԥ�����ں��ۼ���ʷԤ����
	 * 
	 * @param pk_subagrement
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Object> queryWithholdingRecord(String pk_subagrement)
			throws BusinessException {
		String querySql = "select max(t.withholdingdate) withholdingdate ,sum(t.withholdingmny)  withholdingmny from fee_withholding t  where nvl(t.dr, 0) = 0 and t.csourceid = ? ";
		SQLParameter sqlParameter = new SQLParameter();
		sqlParameter.addParam(pk_subagrement);
		Map map = (Map) NCLocator.getInstance().lookup(IUAPQueryBS.class)
				.executeQuery(querySql, sqlParameter, new MapProcessor());
		return map;
	}

}