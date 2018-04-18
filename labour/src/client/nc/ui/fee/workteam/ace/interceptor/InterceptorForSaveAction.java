package nc.ui.fee.workteam.ace.interceptor;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.Action;

import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Collections;

import nc.ui.pubapp.uif2app.model.BatchBillTableModel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.ActionInterceptor;
import nc.vo.fee.workteam.WorkteamVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * 保存按钮拦截器，保存前校验,没有勾选是否班组长，班组长必输
 * 
 * @author shidalin
 * 
 */
public class InterceptorForSaveAction implements ActionInterceptor {

	private BatchBillTableModel model;

	public InterceptorForSaveAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean afterDoActionFailed(Action arg0, ActionEvent arg1,
			Throwable arg2) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void afterDoActionSuccessed(Action arg0, ActionEvent arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean beforeDoAction(Action arg0, ActionEvent e) {
//		Object selectedData = model.getSelectedData();
		Object[] addObjs = model.getCurrentSaveObject().getAddObjs();
		Object[] updObjs = model.getCurrentSaveObject().getUpdObjs();
		ArrayList<WorkteamVO> arrayList = new ArrayList<WorkteamVO>();
		Collections.addAll(arrayList, updObjs);
		Collections.addAll(arrayList, addObjs);
		if (arrayList == null || arrayList.size() == 0) {
			return true;
		}
		StringBuffer message = new StringBuffer();
		for (Object selectData : arrayList) {
			WorkteamVO vo = (WorkteamVO) selectData;
			if (vo.getIsleader() != null && vo.getIsleader().booleanValue()) {
				continue;
			} else {
				String pleader = vo.getPleader();
				if (pleader == null || "".equals(pleader)) {
					message.append(vo.getName() + ",");
				}
			}
		}
		if (message.length() > 0) {
			String errorMessage = "没有勾选是否班组长，班组长字段不能为空，错误行姓名：\n" + message;
			ShowStatusBarMsgUtil.showErrorMsg("保存失败", errorMessage,
					model.getContext());
			ExceptionUtils.wrappBusinessException(errorMessage);
			return false;
		}
		return true;
	}

	public BatchBillTableModel getModel() {
		return model;
	}

	public void setModel(BatchBillTableModel model) {
		this.model = model;
	}

}
