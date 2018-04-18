package nc.bs.fee.workteam.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.fee.workteam.WorkteamVO;

public class AceWorkteamBP {

	public WorkteamVO[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<WorkteamVO>(WorkteamVO.class).query(querySheme,
				null);
	}
}
