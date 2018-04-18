package nc.ui.fee.subagreement.ace.ref;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.ISubagreementMaintain;
import nc.ui.pubapp.uif2app.query2.model.IRefQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.ct.ar.entity.AggCtArVO;
import nc.vo.ct.ar.entity.CtArBVO;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;

import org.apache.commons.lang.ArrayUtils;

/**
 * ��Դ���ݲ�ѯ���� ���ܣ�������Դ���ݲ�ѯ����ӿڣ�ʵ����Դ���ݲ�ѯ
 * 
 * @author shidl
 * 
 */

public class FE01RefZ5ReferQueryService implements IRefQueryService {

	public FE01RefZ5ReferQueryService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		// Ĭ�Ϲ�������
		// 1.��˰�ϼ�>�ۼ�Ӧ�ս��
		// 2.���°汾
		// 3.��Ч
		ISubagreementMaintain queryService = (ISubagreementMaintain) NCLocator
				.getInstance().lookup(ISubagreementMaintain.class);
		QuerySchemeProcessor querySchemeProcessor = new QuerySchemeProcessor(
				queryScheme);
		// �Ѿ����ɵĲ����ظ�����
		String mainTableAlias = querySchemeProcessor.getMainTableAlias();
		// ������û�й������ݣ����ñ�ͷ����Ž��й���
		String whereSql = " and "
				+ " not exists (select t.serviceno from fee_subagreement t where nvl(t.dr,0)=0 and t.serviceno = "+mainTableAlias
				+ ".vbillcode)";
		querySchemeProcessor.appendWhere(whereSql);
		AggCtArVO[] ctvos = queryService.queryRefVOS(queryScheme);
		if (!ArrayUtils.isEmpty(ctvos)) {
			for (AggCtArVO ctvo : ctvos) {
				if (ctvo.getCtArBVO() != null && ctvo.getCtArBVO().length > 1) {
					ctvo.setCtArBVO(new CtArBVO[] { ctvo.getCtArBVO()[0] });
				}
			}
		}
		return ctvos;
	}

	@Override
	public Object[] queryByWhereSql(String arg0) throws Exception {
		// TODO �Զ����ɵķ������
		return null;
	}
}
