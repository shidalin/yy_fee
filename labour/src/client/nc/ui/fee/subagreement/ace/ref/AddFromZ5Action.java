package nc.ui.fee.subagreement.ace.ref;

import java.awt.event.ActionEvent;

import nc.ui.pub.pf.PfUtilClient;
import nc.ui.pubapp.uif2app.actions.AbstractReferenceAction;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.UIState;
import nc.vo.fee.subagreement.AggSubagreementVO;

/**
 * 参照其他收合同按钮
 * 
 * @author shidalin
 * 
 */
public class AddFromZ5Action extends AbstractReferenceAction {

	public AddFromZ5Action() {
		this.setBtnName("选择合同");
		this.setCode("addFromZ5Action");
	}

	private BillForm editor;

	private BillManageModel model;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		PfUtilClient.childButtonClicked(getSourceBillType(), getModel()
				.getContext().getPk_group(), getModel().getContext()
				.getPk_loginUser(), "FE01", getModel().getContext()
				.getEntranceUI(), null, null);
		if (PfUtilClient.isCloseOK()) {
			AggSubagreementVO[] vos = (AggSubagreementVO[]) PfUtilClient
					.getRetVos();
			this.getTransferViewProcessor().processBillTransfer(vos);
		}

	}

	public BillForm getEditor() {
		return this.editor;
	}

	public BillManageModel getModel() {
		return this.model;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		return this.model.getUiState() == UIState.NOT_EDIT;
	}
}
