package nc.impl.pub.ace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.fee.subagreement.ace.bp.AceSubagreementDeleteBP;
import nc.bs.fee.subagreement.ace.bp.AceSubagreementInsertBP;
import nc.bs.fee.subagreement.ace.bp.AceSubagreementUpdateBP;
import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.SchemeBillQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.util.mmf.framework.db.SqlInUtil;
import nc.vo.ct.ar.entity.AggCtArVO;
import nc.vo.fee.subagreement.AggSubagreementVO;
import nc.vo.fee.subagreement.SubagreementVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.ArrayUtils;

public abstract class AceSubagreementPubServiceImpl {

	// 新增
	public AggSubagreementVO[] pubinsertBills(AggSubagreementVO[] vos)
			throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggSubagreementVO> transferTool = new BillTransferTool<AggSubagreementVO>(
					vos);
			AggSubagreementVO[] mergedVO = transferTool.getClientFullInfoBill();

			// 调用BP
			AceSubagreementInsertBP action = new AceSubagreementInsertBP();
			AggSubagreementVO[] retvos = action.insert(mergedVO);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggSubagreementVO[] vos)
			throws BusinessException {
		try {
			// 加锁 比较ts
			BillTransferTool<AggSubagreementVO> transferTool = new BillTransferTool<AggSubagreementVO>(
					vos);
			AggSubagreementVO[] fullBills = transferTool
					.getClientFullInfoBill();
			AceSubagreementDeleteBP deleteBP = new AceSubagreementDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggSubagreementVO[] pubupdateBills(AggSubagreementVO[] vos)
			throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggSubagreementVO> transTool = new BillTransferTool<AggSubagreementVO>(
					vos);
			// 补全前台VO
			AggSubagreementVO[] fullBills = transTool.getClientFullInfoBill();
			// 获得修改前vo
			AggSubagreementVO[] originBills = transTool.getOriginBills();
			// 调用BP
			AceSubagreementUpdateBP bp = new AceSubagreementUpdateBP();
			AggSubagreementVO[] retBills = bp.update(fullBills, originBills);
			// 构造返回数据
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggSubagreementVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggSubagreementVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggSubagreementVO> query = new BillLazyQuery<AggSubagreementVO>(
					AggSubagreementVO.class);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	/**
	 * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

	public Map<String, String> queryPsnDeptByUser(String pk_user)
			throws BusinessException {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("	select t1.pk_psndoc, t2.pk_dept	");
		stringBuffer.append("	  from sm_user t1	");
		stringBuffer.append("	 inner join bd_psnjob t2	");
		stringBuffer.append("	    on t1.pk_psndoc = t2.pk_psndoc	");
		stringBuffer.append("	 where nvl(t1.dr, 0) = 0	");
		stringBuffer.append("	   and nvl(t2.dr, 0) = 0	");
		stringBuffer.append("	   and t1.enablestate = 2	");
		stringBuffer.append("	   and t1.cuserid = ?	");
		String querySql = stringBuffer.toString();
		SQLParameter sqlParameter = new SQLParameter();
		sqlParameter.addParam(pk_user);
		Map result = (Map) NCLocator.getInstance().lookup(IUAPQueryBS.class)
				.executeQuery(querySql, sqlParameter, new MapProcessor());
		return result;
	}

	public AggSubagreementVO[] queryRef(IQueryScheme queryScheme)
			throws BusinessException {
		// TODO Auto-generated method stub
		SchemeBillQuery<AggSubagreementVO> schemeBillQuery = new SchemeBillQuery<AggSubagreementVO>(
				AggSubagreementVO.class);
		AggSubagreementVO[] aggs = schemeBillQuery.query(queryScheme, null);
		if (!ArrayUtils.isEmpty(aggs)) {
			// 除去表体信息，解决拉单单表显示界面主表数据重复的情况
			for (AggSubagreementVO agg : aggs) {
				agg.setChildrenVO(null);
			}
		}
		return aggs;
	}

	/**
	 * 查询预提数据
	 * 
	 * @param year
	 * @param mouth
	 * @return
	 * @throws BusinessException
	 */
	public AggSubagreementVO[] queryForFE02(String year, String mouth)
			throws BusinessException {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("	select *	");
		stringBuffer.append("	  from fee_subagreement t	");
		stringBuffer.append("	 where nvl(t.dr, 0) = 0	");
		stringBuffer.append("	   and t.activemny > 0	");
		stringBuffer
				.append("	   and to_date(substr(t.constartdate, 0, 7),'yyyy-mm') <= "
						+ "to_date('"
						+ year
						+ "-"
						+ mouth
						+ "','yyyy-mm')"
						+ "	");
		stringBuffer
				.append("	   and  to_date( substr(t.conenddate, 0, 7),'yyyy-mm') >= "
						+ "to_date('"
						+ year
						+ "-"
						+ mouth
						+ "','yyyy-mm')"
						+ "	and t.pk_org = '"
						//增加按当前登录公司进行数据过滤
						+ WorkbenchEnvironment.getInstance().getLoginUser()
								.getPk_org() + "'");
		String querySql = stringBuffer.toString();
		List hvolist = (List) NCLocator
				.getInstance()
				.lookup(IUAPQueryBS.class)
				.executeQuery(querySql,
						new BeanListProcessor(SubagreementVO.class));
		if (hvolist != null && hvolist.size() > 0) {
			ArrayList<AggSubagreementVO> result = new ArrayList<AggSubagreementVO>();
			for (Object obj : hvolist) {
				AggSubagreementVO agg = new AggSubagreementVO();
				agg.setParentVO((SubagreementVO) obj);
				result.add(agg);
			}
			return result.toArray(new AggSubagreementVO[0]);
		}
		return null;
	}

	public String queryProjectPK(String code) throws BusinessException {
		String querySql = "select t.pk_project from bd_project t where nvl(t.dr,0)=0 and t.enablestate = 2 and t.project_code = ?";
		SQLParameter sqlParameter = new SQLParameter();
		sqlParameter.addParam(code);
		Object result = NCLocator.getInstance().lookup(IUAPQueryBS.class)
				.executeQuery(querySql, sqlParameter, new ColumnProcessor());
		return result == null ? "" : result.toString();
	}

	public Map<String, String> getCodeByCustomerpk(ArrayList<String> params)
			throws BusinessException {
		// TODO Auto-generated method stub
		HashMap<String, String> hashMap = new HashMap<String, String>();
		String querySql = "select t.pk_customer,t.name from bd_customer t where nvl(t.dr,0)=0 and t.enablestate = 2 and t.pk_customer  ";
		SqlInUtil sqlInUtil = new SqlInUtil(params.toArray(new String[0]));
		String inSql = sqlInUtil.getInSql();
		ArrayList list = (ArrayList) NCLocator.getInstance()
				.lookup(IUAPQueryBS.class)
				.executeQuery(querySql + inSql, new ArrayListProcessor());
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				Object[] objs = (Object[]) obj;
				hashMap.put(objs[0] == null ? "" : objs[0].toString(),
						objs[1] == null ? "" : objs[1].toString());
			}
		}
		return hashMap;
	}

	public void writeBackForWithholding(
			ArrayList<SubagreementVO> writeBackList, String[] fieldNames)
			throws BusinessException {
		BaseDAO baseDAO = new BaseDAO();
		baseDAO.updateVOArray(writeBackList.toArray(new SubagreementVO[0]),
				fieldNames);
	}

	public AggCtArVO[] querRefVOS(IQueryScheme queryScheme)
			throws BusinessException {
		// TODO Auto-generated method stub
		nc.impl.pub.ace.ArQueryForGatherbillBP arQueryForGatherbillBP = new nc.impl.pub.ace.ArQueryForGatherbillBP();
		return arQueryForGatherbillBP.query(queryScheme);
	}

}
