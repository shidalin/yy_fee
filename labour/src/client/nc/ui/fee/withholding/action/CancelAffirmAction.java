package nc.ui.fee.withholding.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.IWithholdingMaintain;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.query2.model.IModelDataManager;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.pub.lang.UFBoolean;

public class CancelAffirmAction extends NCAction {

	public CancelAffirmAction() {
		this.setBtnName("取消确认");
		this.setCode("cancelAffirmAction");
	}

	private BillManageModel model;

	private IModelDataManager dataManager;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] selectedOperaDatas = model.getSelectedOperaDatas();

		IWithholdingMaintain itf = NCLocator.getInstance().lookup(
				IWithholdingMaintain.class);
		ArrayList<AggWithholdingVO> arrayList = new ArrayList<AggWithholdingVO>();
		for (Object obj : selectedOperaDatas) {
			arrayList.add((AggWithholdingVO) obj);
		}

		// 后台更新数据并发送会计平台
		itf.affirm(arrayList.toArray(new AggWithholdingVO[0]),
				FipMessageVO.MESSAGETYPE_DEL, UFBoolean.FALSE);
		// 刷新界面并提示
		this.getDataManager().refresh();
		this.showQueryInfo();

	}

	protected void showQueryInfo() {
		int size = 0;
		if (this.getModel() instanceof BillManageModel) {
			size = ((BillManageModel) this.getModel()).getData().size();
		}

		if (size >= 0) {
			ShowStatusBarMsgUtil.showStatusBarMsg("批量取消发送会计平台操作成功", this
					.getModel().getContext());
		}

	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		Object[] selectedOperaDatas = model.getSelectedOperaDatas();
		if (ArrayUtils.isEmpty(selectedOperaDatas)) {
			return false;
		}
		return true;
	}

	public IModelDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

}
