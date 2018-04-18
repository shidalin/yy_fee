package nc.impl.pub.ace;

import java.util.List;
import nc.bs.ct.ar.query.ArQueryForGatherbillUtil;
import nc.impl.pubapp.pattern.rule.plugin.IPluginPoint;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.ct.ar.entity.AggCtArVO;
import nc.vo.ct.constant.SchemeKey;
import nc.vo.ct.util.CtTransBusitypes;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.scmpub.res.billtype.CTBillType;

public class ArQueryForGatherbillBP {
	public AggCtArVO[] query(IQueryScheme queryScheme) {
		AroundProcesser processer = new AroundProcesser((IPluginPoint) null);
		AggCtArVO[] queryVos = nc.impl.pub.ace.ArQueryForGatherbillUtil
				.queryForGatherBill(this.getWholeSql(queryScheme));
		queryVos = (AggCtArVO[]) processer.after(queryVos);
		return queryVos;
	}

	private Object getWholeSql(IQueryScheme queryScheme) {
		List busitypes = (List) queryScheme.get(SchemeKey.CT_BUSITYPES);
		String[] trantypes = CtTransBusitypes.getCtTransByBusitypes(busitypes,
				CTBillType.OtherSale.getCode());
		QuerySchemeProcessor outprocessor = new QuerySchemeProcessor(
				queryScheme);
		String mainTableAlias = outprocessor.getMainTableAlias();
		String ctArB = outprocessor
				.getTableAliasOfAttribute("pk_ct_ar_b.pk_ct_ar_b");
		SqlBuilder partWhr = new SqlBuilder();
		if (trantypes.length > 0) {
			partWhr.append(" and ");
			partWhr.append(mainTableAlias + "." + "vtrantypecode", trantypes);
		}

		partWhr.append(" and " + mainTableAlias + ".blatest = \'Y\' ");
		partWhr.append(" and " + mainTableAlias + "." + "fstatusflag",
				Integer.valueOf(1));
		// partWhr.append(" and ");
		// partWhr.append(" isnull(" + ctArB + ".norigtaxmny,0) >");
		// partWhr.append(" isnull(" + ctArB + ".noritotalgpmny,0)");
		outprocessor.appendWhere(partWhr.toString());
		SqlBuilder wholeSql = new SqlBuilder();
		wholeSql.append("select " + mainTableAlias + ".pk_ct_ar,");
		wholeSql.append(ctArB + ".pk_ct_ar_b");
		wholeSql.append(outprocessor.getFinalFromWhere());
		return wholeSql.toString();
	}
}