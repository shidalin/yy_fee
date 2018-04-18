package nc.impl.pub.ace;

import java.util.HashSet;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.vo.ct.ar.entity.AggCtArVO;
import nc.vo.ct.ar.entity.CtArBVO;
import nc.vo.ct.ar.entity.CtArVO;
import nc.vo.ct.uitl.AggVOUtil;
import nc.vo.ct.uitl.ValueUtil;
import nc.vo.pubapp.pattern.data.IRowSet;

public class ArQueryForGatherbillUtil {
	public static AggCtArVO[] queryForGatherBill(Object wholeSql) {
		DataAccessUtils utils = new DataAccessUtils();
		IRowSet rowset = utils.query(wholeSql.toString());
		HashSet headids = new HashSet();
		HashSet itemids = new HashSet();

		while (rowset.next()) {
			if (!ValueUtil.isEmpty(rowset.getString(0))) {
				headids.add(rowset.getString(0));
			}

			if (!ValueUtil.isEmpty(rowset.getString(1))) {
				itemids.add(rowset.getString(1));
			}
		}

		if (0 != headids.size() && 0 != itemids.size()) {
			CtArVO[] headers = (CtArVO[]) (new VOQuery(CtArVO.class))
					.query((String[]) headids.toArray(new String[headids.size()]));
			CtArBVO[] items = (CtArBVO[]) (new VOQuery(CtArBVO.class))
					.query((String[]) itemids.toArray(new String[itemids.size()]));
			AggCtArVO[] ctVOs = (AggCtArVO[]) ((AggCtArVO[]) AggVOUtil
					.createBillVO(headers, items, AggCtArVO.class));
			return ctVOs;
		} else {
			return null;
		}
	}
}