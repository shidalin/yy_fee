package nc.ui.fee.withholding.ace.ref;

import nc.ui.pubapp.billref.dest.DefaultBillDataLogic;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.vo.pubapp.AppContext;

public class FE02RefFE01TransferBillDataLogic extends DefaultBillDataLogic {
	@Override
	public void doTransferAddLogic(Object selectedData) {
		// 1.��䵥��������Ϣ
		if (selectedData != null) {
			AggWithholdingVO agg = (AggWithholdingVO) selectedData;
			agg.getParentVO().setPk_billtypecode("FE02");
			agg.getParentVO().setPk_billtypeid(
					nc.vo.am.common.util.BillTypeUtils.getPKByCode("FE02"));
			agg.getParentVO().setDbilldate(
					AppContext.getInstance().getBusiDate());
		}
		// �����������ڽ�����
		super.doTransferAddLogic(selectedData);
	}
}
