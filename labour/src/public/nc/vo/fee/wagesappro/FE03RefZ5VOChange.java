package nc.vo.fee.wagesappro;

import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.ISubagreementMaintain;
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
public class FE03RefZ5VOChange implements IChangeVOAdjust {


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
			AggregatedValueObject[] arg0, AggregatedValueObject[] arg1,
			ChangeVOAdjustContext arg2) throws BusinessException {
		return arg1;
	}

	@Override
	public AggregatedValueObject[] batchAdjustBeforeChange(
			AggregatedValueObject[] arg0, ChangeVOAdjustContext arg1)
			throws BusinessException {

		return arg0;
	}

}
