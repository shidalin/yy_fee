package nc.vo.fee.subagreement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.ISubagreementMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.util.mmf.framework.db.SqlInUtil;
import nc.vo.ct.ar.entity.AggCtArVO;
import nc.vo.ct.ar.entity.CtArVO;
import nc.vo.fee.subagreement.AggSubagreementVO;
import nc.vo.fee.subagreement.SubagreementVO;
import nc.vo.pf.change.ChangeVOAdjustContext;
import nc.vo.pf.change.IChangeVOAdjust;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.ArrayUtils;

/**
 * 水平产品交货后处理类
 * 
 * @author shidalin
 * 
 */
public class FE01RefZ5VOChange implements IChangeVOAdjust {

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
			AggregatedValueObject[] srcaggs, AggregatedValueObject[] destaggs,
			ChangeVOAdjustContext arg2) throws BusinessException {
		// 处理 分包合同名称：自动生成，客户+地址+“工程项目施工协议书”
		// 客户 pk_customer
		// 地址 vdef11
		//
		if (!ArrayUtils.isEmpty(srcaggs)) {
			ArrayList<String> arrayList = new ArrayList<String>();
			for (AggregatedValueObject agg : srcaggs) {
				CtArVO parentVO = ((AggCtArVO) agg).getParentVO();
				arrayList.add(parentVO.getPk_customer());
			}
			ISubagreementMaintain itf = NCLocator.getInstance().lookup(
					ISubagreementMaintain.class);
			Map<String, String> codeByCustomerpk = itf
					.getCodeByCustomerpk(arrayList);
			HashMap<String, String> code2subcontractname = new HashMap<String, String>();
			// HashMap<String, String> code2pkproject = new HashMap<String,
			// String>();
			if (codeByCustomerpk != null
					&& codeByCustomerpk.keySet().size() > 0) {
				for (AggregatedValueObject agg : srcaggs) {
					CtArVO parentVO = ((AggCtArVO) agg).getParentVO();
					String customerName = codeByCustomerpk.get(parentVO
							.getPk_customer());
					customerName = customerName == null ? "" : customerName;
					String adress = parentVO.getVdef11();
					adress = adress == null ? "" : adress;
					String subcontractname = customerName + adress
							+ "工程项目施工协议书";
					code2subcontractname.put(parentVO.getVbillcode(),
							subcontractname);
					// code2pkproject.put(parentVO.getVbillcode(),
					// itf.queryProjectPK(parentVO.getVbillcode()));
				}
			}
			for (AggregatedValueObject agg : destaggs) {
				SubagreementVO parentVO = ((AggSubagreementVO) agg)
						.getParentVO();
				String subcontractname = code2subcontractname.get(parentVO
						.getServiceno());
				parentVO.setSubcontractname(subcontractname);
				// 设置合同号
				// parentVO.setContratcno(code2pkproject.get(parentVO
				// .getServiceno()));
				// 设置总人天数
				// Integer contotaldays = UFDate.getDaysBetween(
				// parentVO.getConstartdate(), parentVO.getConenddate())+1;
				// parentVO.setContotaldays(contotaldays);
				// 已预提天数
				parentVO.setUseddays(0);
				// 剩余可预提天数
				// parentVO.setActivedays(contotaldays);
				// 已预提金额
				parentVO.setUsedmny(UFDouble.ZERO_DBL);
				// 剩余可预提金额
				parentVO.setActivemny(parentVO.getContractmny());
			}
		}
		return destaggs;
	}

	@Override
	public AggregatedValueObject[] batchAdjustBeforeChange(
			AggregatedValueObject[] arg0, ChangeVOAdjustContext arg1)
			throws BusinessException {

		return arg0;
	}

}
