package nc.ui.fee.subagreement.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.fee.subagreement.AggSubagreementVO;
import nc.itf.fee.ISubagreementMaintain;

/**
 * ʾ�����ݵĲ�������
 * 
 * @author author
 * @version tempProject version
 */
public class AceSubagreementMaintainProxy implements IDataOperationService,
		IQueryService ,ISingleBillService<AggSubagreementVO>{
	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		ISubagreementMaintain operator = NCLocator.getInstance().lookup(
				ISubagreementMaintain.class);
		AggSubagreementVO[] vos = operator.insert((AggSubagreementVO[]) value);
		return vos;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		ISubagreementMaintain operator = NCLocator.getInstance().lookup(
				ISubagreementMaintain.class);
		AggSubagreementVO[] vos = operator.update((AggSubagreementVO[]) value);
		return vos;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		// Ŀǰ��ɾ�����������������������pubapp��֧�ִ����������ִ��ɾ������
		// ���ݵ�ɾ��ʵ����ʹ�õ��ǣ�ISingleBillService<AggSingleBill>��operateBill
		ISubagreementMaintain operator = NCLocator.getInstance().lookup(
				ISubagreementMaintain.class);
		operator.delete((AggSubagreementVO[]) value);
		return value;
	}
	
	@Override
	public AggSubagreementVO operateBill(AggSubagreementVO bill) throws Exception {
		ISubagreementMaintain operator = NCLocator.getInstance().lookup(
				ISubagreementMaintain.class);
		operator.delete(new AggSubagreementVO[] { bill });
		return bill;
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		ISubagreementMaintain query = NCLocator.getInstance().lookup(
				ISubagreementMaintain.class);
		return query.query(queryScheme);
	}

}
