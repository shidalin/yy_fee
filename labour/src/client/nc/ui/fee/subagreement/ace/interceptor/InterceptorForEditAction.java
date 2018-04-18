package nc.ui.fee.subagreement.ace.interceptor;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.IWorkteamMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.ActionInterceptor;
import nc.util.mmpub.dpub.db.SqlInUtil;
import nc.vo.fee.subagreement.AggSubagreementVO;
import nc.vo.fee.workteam.WorkteamVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * �޸İ�ť���������������ε��ݲ������޸�
 * 
 * @author shidalin
 * 
 */
public class InterceptorForEditAction implements ActionInterceptor {

	private BillManageModel model;

	public InterceptorForEditAction() {
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
	public boolean beforeDoAction(Action arg0, ActionEvent arg1) {
		Object[] selectedOperaDatas = model.getSelectedOperaDatas();
		if (selectedOperaDatas == null || selectedOperaDatas.length == 0) {
			ExceptionUtils.wrappBusinessException("��ѡ����Ч��������");
			ShowStatusBarMsgUtil.showErrorMsg("��������", "��ѡ����Ч��������",
					model.getContext());
			return false;
		}
		ArrayList<String> arrayList = new ArrayList<String>();
		for (Object selectData : selectedOperaDatas) {
			AggSubagreementVO vo = (AggSubagreementVO) selectData;
			// ����UAP�ӿڽ��в�ѯ
			arrayList.add(vo.getParentVO().getPk_subagreement());
		}
		try {
			String querySql = "select count(*) from fee_withholding t where nvl(t.dr,0)=0 and t.csourceid ";
			SqlInUtil sqlInUtil = new SqlInUtil(
					arrayList.toArray(new String[0]));
			String inSql = sqlInUtil.getInSql();
			querySql += inSql;
			// ���ú�̨У��
			Object result = NCLocator.getInstance().lookup(IUAPQueryBS.class)
					.executeQuery(querySql, new ColumnProcessor());
			if (result != null && Integer.parseInt(result.toString()) > 0) {
				ShowStatusBarMsgUtil.showErrorMsg("�޸�ʧ��", "��ǰ���ݴ������ε��ݣ��������޸�",
						model.getContext());
				return false;
			}

		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtils.wrappException(e);
			ShowStatusBarMsgUtil.showErrorMsg("�޸�ʧ��", e.getMessage(),
					model.getContext());
			return false;
		}
		return true;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
	}

}
