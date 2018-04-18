package nc.impl.pub.ace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.fee.workteam.ace.bp.AceWorkteamBP;
import nc.bs.framework.common.NCLocator;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.util.mmf.framework.db.SqlInUtil;
import nc.vo.fee.subagreement.SubagreementItemVO;
import nc.vo.fee.subagreement.SubagreementVO;
import nc.vo.fee.wagesappro.WagesapproBVO;
import nc.vo.fee.workteam.WorkteamVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

public abstract class AceWorkteamPubServiceImpl extends SmartServiceImpl {
	public WorkteamVO[] pubquerybasedoc(IQueryScheme querySheme)
			throws nc.vo.pub.BusinessException {
		return new AceWorkteamBP().queryByQueryScheme(querySheme);
	}

	/**
	 * ��ѯ�������ü�¼
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	public void queryReferencingInfo(WorkteamVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return;
		}
		ArrayList<String> wtpkList = new ArrayList<String>();
		HashMap<String, WorkteamVO> hashMap = new HashMap<String, WorkteamVO>();
		for (WorkteamVO vo : vos) {
			wtpkList.add(vo.getPk_workteam());
			hashMap.put(vo.getPk_workteam(), vo);
		}
		SqlInUtil sqlInUtil = new SqlInUtil(wtpkList.toArray(new String[0]));
		String inSql = sqlInUtil.getInSql();
		// 1.���鵥�� fee_workteam.pleader
		String querySqlFromWorkteam = "select * from fee_workteam t where nvl(t.dr,0)=0 and t.pleader "
				+ inSql;
		// 2.�˹��ְ�Э������ fee_subagreement.pk_teamwork
		String querySqlFromSubagreement = "select * from fee_subagreement t where nvl(t.dr,0)=0 and t.pk_teamwork "
				+ inSql;
		// 3.�˹��ְ�Э���ӱ� fee_subagreement_b.pleader
		String querySqlFromSubagreementb = "select * from fee_subagreement_b t where nvl(t.dr,0)=0 and t.pleader "
				+ inSql;
		// 4.�˹����������ӱ� fee_wagesapprove_b.pleader
		String querySqlFromWagesapprove = "select * from fee_wagesappro_b t where nvl(t.dr,0)=0 and t.pleader  "
				+ inSql;
		IUAPQueryBS queryBS = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		List<WorkteamVO> listFromWorkTeam = (List) queryBS.executeQuery(
				querySqlFromWorkteam, new BeanListProcessor(WorkteamVO.class));
		List<SubagreementVO> listFromSubagreement = (List) queryBS
				.executeQuery(querySqlFromSubagreement, new BeanListProcessor(
						SubagreementVO.class));
		List<SubagreementItemVO> listFromSubagreementb = (List) queryBS
				.executeQuery(querySqlFromSubagreementb, new BeanListProcessor(
						SubagreementItemVO.class));
		List<WagesapproBVO> listFromWagesapprove = (List) queryBS.executeQuery(
				querySqlFromWagesapprove, new BeanListProcessor(
						WagesapproBVO.class));
		StringBuffer errorMessage = new StringBuffer();
		if (listFromWorkTeam != null && listFromWorkTeam.size() > 0) {
			StringBuffer message = new StringBuffer();
			for (WorkteamVO vo : listFromWorkTeam) {
				WorkteamVO workteamVO = hashMap.get(vo.getPleader());
				message.append("," + workteamVO.getName());
			}
			errorMessage.append("\n�������ݱ����鵥�����ã����ƣ�" + message);
		}
		if (listFromSubagreementb != null && listFromSubagreementb.size() > 0) {
			StringBuffer message = new StringBuffer();
			for (SubagreementItemVO vo : listFromSubagreementb) {
				WorkteamVO workteamVO = hashMap.get(vo.getPleader());
				message.append("," + workteamVO.getName());
			}
			errorMessage.append("\n�������ݱ��˹��ְ�Э���ӱ����ã����ƣ�" + message);
		}
		if (listFromSubagreement != null && listFromSubagreement.size() > 0) {
			StringBuffer message = new StringBuffer();
			for (SubagreementVO vo : listFromSubagreement) {
				WorkteamVO workteamVO = hashMap.get(vo.getPk_teamwork());
				message.append("," + workteamVO.getName());
			}
			errorMessage.append("\n�������ݱ��˹��ְ�Э���������ã����ƣ�" + message);
		}
		if (listFromWagesapprove != null && listFromWagesapprove.size() > 0) {
			StringBuffer message = new StringBuffer();
			for (WagesapproBVO vo : listFromWagesapprove) {
				WorkteamVO workteamVO = hashMap.get(vo.getPleader());
				message.append("," + workteamVO.getName());
			}
			errorMessage.append("\n�������ݱ��˹����������ӱ����ã����ƣ�" + message);
		}

		if (errorMessage.length() > 0) {
			throw new BusinessException("ɾ��ʧ�ܣ�" + errorMessage.toString());
		}
	}
}