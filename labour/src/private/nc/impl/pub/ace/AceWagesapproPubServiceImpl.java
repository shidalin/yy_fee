package nc.impl.pub.ace;

import nc.bs.fee.wagesappro.ace.bp.AceWagesapproInsertBP;
import nc.bs.fee.wagesappro.ace.bp.AceWagesapproUpdateBP;
import nc.bs.fee.wagesappro.ace.bp.AceWagesapproDeleteBP;
import nc.bs.fee.wagesappro.ace.bp.AceWagesapproSendApproveBP;
import nc.bs.fee.wagesappro.ace.bp.AceWagesapproUnSendApproveBP;
import nc.bs.fee.wagesappro.ace.bp.AceWagesapproApproveBP;
import nc.bs.fee.wagesappro.ace.bp.AceWagesapproUnApproveBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceWagesapproPubServiceImpl {
	// 新增
	public AggWagesapproVO[] pubinsertBills(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggWagesapproVO> transferTool = new BillTransferTool<AggWagesapproVO>(
					clientFullVOs);
			// 调用BP
			AceWagesapproInsertBP action = new AceWagesapproInsertBP();
			AggWagesapproVO[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AceWagesapproDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggWagesapproVO[] pubupdateBills(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggWagesapproVO> transferTool = new BillTransferTool<AggWagesapproVO>(
					clientFullVOs);
			AceWagesapproUpdateBP bp = new AceWagesapproUpdateBP();
			AggWagesapproVO[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggWagesapproVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggWagesapproVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggWagesapproVO> query = new BillLazyQuery<AggWagesapproVO>(
					AggWagesapproVO.class);
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
	public AggWagesapproVO[] pubsendapprovebills(
			AggWagesapproVO[] clientFullVOs, AggWagesapproVO[] originBills)
			throws BusinessException {
		AceWagesapproSendApproveBP bp = new AceWagesapproSendApproveBP();
		AggWagesapproVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public AggWagesapproVO[] pubunsendapprovebills(
			AggWagesapproVO[] clientFullVOs, AggWagesapproVO[] originBills)
			throws BusinessException {
		AceWagesapproUnSendApproveBP bp = new AceWagesapproUnSendApproveBP();
		AggWagesapproVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggWagesapproVO[] pubapprovebills(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceWagesapproApproveBP bp = new AceWagesapproApproveBP();
		AggWagesapproVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

	public AggWagesapproVO[] pubunapprovebills(AggWagesapproVO[] clientFullVOs,
			AggWagesapproVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceWagesapproUnApproveBP bp = new AceWagesapproUnApproveBP();
		AggWagesapproVO[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

}