package nc.vo.fee.withholding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.ISubagreementMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.util.mmpub.dpub.db.SqlInUtil;
import nc.vo.ct.ar.entity.AggCtArVO;
import nc.vo.ct.ar.entity.CtArVO;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.vo.fee.wagesappro.WagesapproVO;
import nc.vo.pf.change.ChangeVOAdjustContext;
import nc.vo.pf.change.IChangeVOAdjust;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

/**
 * 水平产品交货后处理类
 * 
 * @author shidalin
 * 
 */
public class FE02RefZ5VOChange implements IChangeVOAdjust {

	@Override
	public AggregatedValueObject adjustAfterChange(AggregatedValueObject arg0,
			AggregatedValueObject arg1, ChangeVOAdjustContext arg2)
			throws BusinessException {
		// TODO Auto-generated method stub
		return batchAdjustAfterChange(new AggregatedValueObject[] { arg0 },
				new AggregatedValueObject[] { arg1 }, arg2)[0];
	}

	@Override
	public AggregatedValueObject adjustBeforeChange(AggregatedValueObject arg0,
			ChangeVOAdjustContext arg1) throws BusinessException {
		// TODO Auto-generated method stub
		return batchAdjustBeforeChange(new AggregatedValueObject[] { arg0 },
				arg1)[0];
	}

	@Override
	public AggregatedValueObject[] batchAdjustAfterChange(
			AggregatedValueObject[] srcvos, AggregatedValueObject[] destvos,
			ChangeVOAdjustContext arg2) throws BusinessException {
		HashMap<String, List<AggWithholdingVO>> srccode2agglist = new HashMap<String, List<AggWithholdingVO>>();
		ArrayList<String> srcpkList = new ArrayList<String>();
		for (AggregatedValueObject param : destvos) {
			AggWithholdingVO aggvo = (AggWithholdingVO) param;
			// 合同单据号
			String srccode = aggvo.getParentVO().getServiceno();
			// 分包协议主键
			srcpkList.add(aggvo.getParentVO().getCsourceid());
			if (srccode2agglist.keySet().contains(srccode)) {
				srccode2agglist.get(srccode).add(aggvo);
			} else {
				ArrayList<AggWithholdingVO> arrayList = new ArrayList<AggWithholdingVO>();
				arrayList.add(aggvo);
				srccode2agglist.put(srccode, arrayList);
			}
		}
		String querysql = "select t.* from ct_ar t inner join fee_subagreement t1 on t.pk_ct_ar = t1.csourceid where t1.pk_subagreement ";
		SqlInUtil sqlInUtil = new SqlInUtil(srcpkList.toArray(new String[0]));
		querysql += sqlInUtil.getInSql();
		List<CtArVO> ctArVOlist = (List<CtArVO>) NCLocator.getInstance()
				.lookup(IUAPQueryBS.class)
				.executeQuery(querysql, new BeanListProcessor(CtArVO.class));
		if (ctArVOlist != null && ctArVOlist.size() > 0) {
			HashMap<String, CtArVO> code2ctarvo = new HashMap<String, CtArVO>();
			for (CtArVO ctarvo : ctArVOlist) {
				code2ctarvo.put(ctarvo.getVbillcode(), ctarvo);
			}
			for (String key : srccode2agglist.keySet()) {
				List<AggWithholdingVO> agglist = srccode2agglist.get(key);
				CtArVO ctArVO = code2ctarvo.get(key);
				if (ctArVO == null) {
					continue;
				}				
				if (agglist != null && agglist.size() > 0) {
					for (AggWithholdingVO agg : agglist) {
						if (agg != null) {
							agg.getParentVO().setContratcno(ctArVO.getVdef20());
							agg.getParentVO().setPromanager(ctArVO.getVdef19());
							agg.getParentVO().setProstyle(ctArVO.getVdef1());
							agg.getParentVO().setStylist(ctArVO.getVdef18());
						}
					}
				}
			}
		}
		return destvos;
	}

	@Override
	public AggregatedValueObject[] batchAdjustBeforeChange(
			AggregatedValueObject[] arg0, ChangeVOAdjustContext arg1)
			throws BusinessException {

		return arg0;
	}

}
