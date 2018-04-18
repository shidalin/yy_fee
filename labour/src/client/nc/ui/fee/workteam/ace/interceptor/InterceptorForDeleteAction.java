package nc.ui.fee.workteam.ace.interceptor;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.IWorkteamMaintain;
import nc.ui.pubapp.uif2app.model.BatchBillTableModel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.ActionInterceptor;
import nc.vo.fee.workteam.WorkteamVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * ɾ����ť��������ɾ��ǰУ�鵱ǰɾ�������Ƿ�����
 * 
 * @author shidalin
 * 
 */
public class InterceptorForDeleteAction implements ActionInterceptor {

	private BatchBillTableModel model;

	public InterceptorForDeleteAction() {
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
		// TODO Auto-generated method stub
		Object[] selectedOperaDatas = model.getSelectedOperaDatas();
		if (selectedOperaDatas == null || selectedOperaDatas.length == 0) {
			ExceptionUtils.wrappBusinessException("��ѡ����Ч��������");
			ShowStatusBarMsgUtil.showErrorMsg("����ʧ��", "��ѡ����Ч��������",
					model.getContext());
			return false;
		}
		ArrayList<WorkteamVO> arrayList = new ArrayList<WorkteamVO>();
		for (Object selectData : selectedOperaDatas) {
			WorkteamVO vo = (WorkteamVO) selectData;
			if (vo.getPk_workteam() == null) {
				continue;
			} else {
				// ����UAP�ӿڽ��в�ѯ
				arrayList.add(vo);
			}
		}
		try {
			// ���ú�̨У��
			NCLocator.getInstance().lookup(IWorkteamMaintain.class)
					.queryReferencingInfo(arrayList.toArray(new WorkteamVO[0]));
			// ValidationFailure validate =
			// BDReferenceChecker.getInstance().validate(vo);
			// if (validate != null) {
			// throw new BusinessException(validate.getMessage());
			// }
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtils.wrappException(e);
			ShowStatusBarMsgUtil.showErrorMsg("ɾ��ʧ��", e.getMessage(),
					model.getContext());
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
