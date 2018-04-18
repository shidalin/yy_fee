package nc.ui.fee.withholding.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.funcnode.ui.AbstractFunclet;
import nc.itf.gl.voucher.IVoucherConst;
import nc.pub.util.ModuleIsAbleUtil;
import nc.ui.pub.link.FipBillLinkQueryCenter;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.NCAction;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;

/**
 * 联查凭证
 * 
 * 
 * @version 2012-11-27 上午07:02:23
 * @author 符利强
 */
public class LinkVoucherAction extends NCAction {

	private static final long serialVersionUID = 2648016433755219475L;
	private BillManageModel model;
	private BillForm editor;

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	public LinkVoucherAction() {
		super();
		super.setBtnName("联查凭证");
		super.setCode("linkVoucherAction");
		super.putValue(Action.SHORT_DESCRIPTION, "联查凭证");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		Object value = getModel().getSelectedData();

		AggregatedValueObject aggVO = (AggregatedValueObject) value;

		if (aggVO == null || aggVO.getParentVO() == null) {
			throw new BusinessException("未选中单据，无法联查凭证");
		}

		// 判断会计平台是否启用
		if (!ModuleIsAbleUtil.isFIPEnable((String) aggVO.getParentVO()
				.getAttributeValue("pk_group"))) {
			throw new BusinessException("会计平台没有启用，无法联查凭证");
		}

		CircularlyAccessibleValueObject hvo = (CircularlyAccessibleValueObject) aggVO
				.getParentVO();

		FipRelationInfoVO infovo = new FipRelationInfoVO();
		String billType = (String) hvo.getAttributeValue("pk_billtypecode");
		infovo.setPk_billtype(billType);
		infovo.setRelationID(hvo.getPrimaryKey());

		// 来源方查目标方
		FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(
				(AbstractFunclet) getModel().getContext().getEntranceUI(),
				infovo);

	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getSelectedOperaDatas() == null
				|| getModel().getSelectedOperaDatas().length == 0) {
			// 一行都没有，不亮
			return false;
		}
		// else if (getModel().getSelectedOperaDatas().length == 1) {
		// // 审批通过的单据才能制证
		// AggregatedValueObject aggvo = (AggregatedValueObject) getModel()
		// .getSelectedData();
		// if (aggvo == null) {
		// return false;
		// }
		// SuperVO parentVO = (SuperVO) aggvo.getParentVO();
		// UFBoolean bisMakeVoucher = (UFBoolean) parentVO
		// .getAttributeValue(FirstBillHVO.ISMAKEVOUCHER);
		// if (bisMakeVoucher != null && bisMakeVoucher.booleanValue())
		// return true;
		// return false;
		// }
		else {
			// 多选，不亮
			return false;
		}
	}
}
