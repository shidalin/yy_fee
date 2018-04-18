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
	// 新增
	public AggWithholdingVO[] pubinsertBills(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggWithholdingVO> transferTool = new BillTransferTool<AggWithholdingVO>(
					clientFullVOs);
			// 调用BP
			AceWithholdingInsertBP action = new AceWithholdingInsertBP();
			AggWithholdingVO[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AceWithholdingDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggWithholdingVO[] pubupdateBills(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggWithholdingVO> transferTool = new BillTransferTool<AggWithholdingVO>(
					clientFullVOs);
			AceWithholdingUpdateBP bp = new AceWithholdingUpdateBP();
			AggWithholdingVO[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
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
	 * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

	// 提交
	public AggWithholdingVO[] pubsendapprovebills(
			AggWithholdingVO[] clientFullVOs, AggWithholdingVO[] originBills)
			throws BusinessException {
		AceWithholdingSendApproveBP bp = new AceWithholdingSendApproveBP();
		AggWithholdingVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public AggWithholdingVO[] pubunsendapprovebills(
			AggWithholdingVO[] clientFullVOs, AggWithholdingVO[] originBills)
			throws BusinessException {
		AceWithholdingUnSendApproveBP bp = new AceWithholdingUnSendApproveBP();
		AggWithholdingVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggWithholdingVO[] pubapprovebills(AggWithholdingVO[] clientFullVOs,
			AggWithholdingVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceWithholdingApproveBP bp = new AceWithholdingApproveBP();
		AggWithholdingVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

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
	 * 批量更新
	 * 
	 * @param vos
	 * @param fields
	 */
	public void updateVOByFields(AggWithholdingVO[] vos, UFBoolean value)
			throws BusinessException {
		BaseDAO baseDAO = new BaseDAO();
		// 需要更新的字段
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
		// 后台更新数据
		itf.updateVOByFields(vos, value);
		// 发送数据到会计平台
		// IFipMessageService lookup = NCLocator.getInstance().lookup(
		// IFipMessageService.class);
		ArrayList<FipMessageVO> arrayList = new ArrayList<FipMessageVO>();
		for (AggWithholdingVO agg : vos) {
			WithholdingVO hvo = agg.getParentVO();
			// 构造数据1
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
			// 来源金额
			reVO.setFreedef3(hvo.getWithholdingmny() == null ? "" : hvo
					.getWithholdingmny().toString());
			// 来源单据号
			reVO.setFreedef1(hvo.getVbillcode());
			// 构造数据2
			FipMessageVO fipMessageVO = new FipMessageVO();
			fipMessageVO.setMessagetype(messageType);
			fipMessageVO.setBillVO(agg);
			fipMessageVO.setMessageinfo(reVO);
			arrayList.add(fipMessageVO);
			NCLocator.getInstance().lookup(IFipMessageService.class)
					.sendMessage(fipMessageVO);
			// 后台查询数据 表 fip_relation
		}
		// FipMessageVO[] fmvos = arrayList.toArray(new FipMessageVO[0]);
		// lookup.sendMessages(fmvos);
	}

	/**
	 * 查询历史预提记录，最后一次预提日期和累计历史预提金额
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