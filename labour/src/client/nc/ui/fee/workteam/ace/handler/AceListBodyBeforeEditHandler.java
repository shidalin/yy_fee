package nc.ui.fee.workteam.ace.handler;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.IWorkteamMaintain;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.ui.pubapp.uif2app.model.BatchBillTableModel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.fee.workteam.WorkteamVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * �����б����༭ǰ�¼�
 * 
 * @author shidalin
 * 
 */
public class AceListBodyBeforeEditHandler implements
		IAppEventHandler<CardBodyBeforeEditEvent> {

	private BatchBillTableModel model;

	@Override
	public void handleAppEvent(CardBodyBeforeEditEvent e) {
		e.setReturnValue(Boolean.TRUE);
		String key = e.getKey();
		int row = e.getRow();
		if ("pleader".equals(key)) {
			UFBoolean isleader = (UFBoolean) e.getBillCardPanel()
					.getBodyValueAt(row, "isleader");
			if (isleader != null && isleader.booleanValue()) {
				ShowStatusBarMsgUtil.showErrorMsg("��ʾ",
						"����ȡ����ѡ�Ƿ���鳤,��ѡ�Ƿ���鳤������ѡ���鳤", e.getContext());
				e.setReturnValue(Boolean.FALSE);
				return;
			}
			// ���鳤������֯����
			UIRefPane ref = (UIRefPane) e.getBillCardPanel().getBodyItem(key)
					.getComponent();
			// ע�⣺����ȡֵȡ�����ǽ�����ʾֵ����Ϊ��֯����
			String pk_org = e.getBillCardPanel().getBillModel()
					.getValueAt(row, "pk_org") == null ? "" : e
					.getBillCardPanel().getBillModel()
					.getValueAt(row, "pk_org").toString();
			ref.getRefModel()
					.setWherePart(
							" pk_org = ( select t.pk_org from org_orgs t where nvl(t.dr,0)=0 and t.code = '"
									+ pk_org + "' )");
			ref.setPk_org(pk_org);
		}
		if ("isleader".equals(key)) {
			UFBoolean isleader = (UFBoolean) e.getBillCardPanel()
					.getBodyValueAt(row, "isleader");
			if (isleader == null || !isleader.booleanValue()) {
				// ��ѡ�Ƿ���鳤����ʾ����հ��鳤��ֵ
				int showOkCancelDlg = MessageDialog.showOkCancelDlg(null, "��ʾ",
						"��ѡ�Ƿ���鳤����հ��鳤��ֵ��");
				// ȡ����������ֱ�ӷ���
				if (showOkCancelDlg != 1) {
					e.setReturnValue(Boolean.FALSE);
					return;
				} else {
					// e.getBillCardPanel().setBodyValueAt(null, e.getRow(),
					// "pleader");
					// e.getBillCardPanel().setBodyValueAt(UFBoolean.TRUE,
					// e.getRow(), e.getKey());
					e.getBillCardPanel().getBillModel()
							.setValueAt(null, e.getRow(), "pleader");
					e.getBillCardPanel().getBillModel()
							.setValueAt(UFBoolean.TRUE, e.getRow(), "isleader");
					e.setReturnValue(Boolean.FALSE);
					((WorkteamVO) model.getSelectedData())
							.setIsleader(UFBoolean.TRUE);
					((WorkteamVO) model.getSelectedData()).setPleader(null);
				}
			} else {
				// ȡ����ѡ֮ǰУ��������Ƿ�����
				int selectedRow = e.getBillCardPanel().getBillTable()
						.getSelectedRow();
				WorkteamVO selectVO = (WorkteamVO) e
						.getBillCardPanel()
						.getBillModel()
						.getBodyValueRowVO(selectedRow,
								WorkteamVO.class.getName());
				try {
					// ���ú�̨У��
					NCLocator
							.getInstance()
							.lookup(IWorkteamMaintain.class)
							.queryReferencingInfo(new WorkteamVO[] { selectVO });
				} catch (BusinessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					ExceptionUtils.wrappException(e1);
					ShowStatusBarMsgUtil.showErrorMsg("ȡ�����鳤��־ʧ��",
							e1.getMessage(), e.getContext());
					// return;
				}
			}
		}
	}

	public BatchBillTableModel getModel() {
		return model;
	}

	public void setModel(BatchBillTableModel model) {
		this.model = model;
	}

}
