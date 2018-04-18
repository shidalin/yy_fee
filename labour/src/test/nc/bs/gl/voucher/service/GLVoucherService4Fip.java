package nc.bs.gl.voucher.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.gl.voucher.RtVoucherDMO;
import nc.bs.gl.voucher.VoucherBO;
import nc.bs.logging.Logger;
import nc.bs.mw.sqltrans.TempTable;
import nc.gl.utils.GLSqlUtil;
import nc.itf.bd.country.ICountryQryService;
import nc.itf.gl.pub.GLStartCheckUtil;
import nc.itf.gl.pub.ICashFlowCase;
import nc.itf.gl.pub.IFreevaluePub;
import nc.itf.gl.voucher.IDetail;
import nc.jdbc.framework.ConnectionFactory;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.fip.external.IBillReflectorExService;
import nc.pubitf.fip.external.IDesBillService;
import nc.pubitf.fip.external.IDesBillSumService;
import nc.pubitf.uapbd.IAccChartPubService;
import nc.pubitf.uapbd.IAccountPubService;
import nc.vo.bd.accessor.IBDData;
import nc.vo.bd.account.AccountVO;
import nc.vo.bd.countryzone.CountryZoneExVO;
import nc.vo.bd.countryzone.CountryZoneVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fip.external.FipBillSumRSVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.external.FipSaveResultVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.fipub.freevalue.GlAssVO;
import nc.vo.fipub.freevalue.Module;
import nc.vo.fipub.freevalue.accountbook.proxy.AccountBookUtil;
import nc.vo.gateway60.itfs.AccountUtilGL;
import nc.vo.gl.aggvoucher.MDDetail;
import nc.vo.gl.aggvoucher.MDVoucher;
import nc.vo.gl.cashflowcase.CashflowcaseVO;
import nc.vo.gl.pubvoucher.DetailVO;
import nc.vo.gl.pubvoucher.OperationResultVO;
import nc.vo.gl.pubvoucher.UserDataVO;
import nc.vo.gl.pubvoucher.VoucherVO;
import nc.vo.gl.vatdetail.BusinessCodeEnum;
import nc.vo.gl.vatdetail.DirectionEnum;
import nc.vo.gl.vatdetail.VatDetailVO;
import nc.vo.gl.vatdetail.VoucherkindEnum;
import nc.vo.glcom.ass.AssVO;
import nc.vo.org.SetOfBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.voucher.fip.Aggcombinscheme;
import nc.vo.voucher.fip.CombinschemeVO;
import nc.vo.voucher.fip.DetailVOComparator;
import nc.vo.voucher.fip.Expaccount;
import nc.vo.voucher.fip.SchemeConst;
import nc.vo.voucher.fip.SumRuleProxy;

import org.apache.commons.lang.StringUtils;

/**
 * 凭证——会计平台调用实现类
 * 
 * @author zhaozh
 * 
 */
public class GLVoucherService4Fip implements IDesBillService, IBillReflectorExService, IDesBillSumService {

	private String pk_sumrule;

	private FipSaveResultVO confirmBill(FipSaveResultVO saveResultVO) throws BusinessException {
		// 先检查总账是否启用
		if(!GLStartCheckUtil.checkGLStart(saveResultVO.getMessageinfo().getPk_group())){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0048")/* @res "总账产品未启用！" */);
		}
		FipSaveResultVO result = new VoucherPrepareService4Fip().saveMDVoucher(saveResultVO);
		return result;
	}

	private UFBoolean isRollbackable(FipRelationInfoVO vo) throws BusinessException {
		return UFBoolean.FALSE;
	}

	public UFBoolean needConfirm() {
		return UFBoolean.TRUE;
	}

	@Override
	public Collection<FipSaveResultVO> confirmBill(Collection<FipSaveResultVO> voarray) throws BusinessException {
		ArrayList<FipSaveResultVO> rs = null;
		if (voarray != null && !voarray.isEmpty()) {
			rs = new ArrayList<FipSaveResultVO>();
			for (FipSaveResultVO fipSaveResultVO : voarray) {
				rs.add(confirmBill(fipSaveResultVO));
			}
		}
		return rs;
	}

	@Override
	public void deleteBill(Collection<FipRelationInfoVO> voarray) throws BusinessException {
		try {
			if (voarray != null && !voarray.isEmpty()) {
				Map<String, String> pk_voucher2org = new HashMap<String, String>();
				String[] pk_vouchers = new String[voarray.size()];
				int i = 0;
				for (FipRelationInfoVO fipRelationInfoVO : voarray) {
					// deleteBill(fipRelationInfoVO);
					pk_vouchers[i] = fipRelationInfoVO.getRelationID();
					i++;
					pk_voucher2org.put(fipRelationInfoVO.getRelationID(), AccountBookUtil.getPk_orgByAccountBookPk(fipRelationInfoVO.getPk_org()));
				}
				// 会计平台没有是否已生成总账凭证的标记，查询实时凭证
				RtVoucherDMO rtVoucherDmo = new RtVoucherDMO();
				VoucherVO[] rtVouchers = rtVoucherDmo.queryByPks(pk_vouchers);

				HashMap<String, String> rtVoucherPks = new HashMap<String, String>();
				HashMap<String, String> voucherPks = new HashMap<String, String>();
				if (rtVouchers != null && rtVouchers.length > 0) {
					for (VoucherVO rtVO : rtVouchers) {
						rtVoucherPks.put(rtVO.getPk_voucher(), rtVO.getPk_voucher());
					}
				}

				for (String pk : pk_vouchers) {
					if (rtVoucherPks.get(pk) == null) {
						voucherPks.put(pk, pk);
					}
				}

				// 删除实时凭证
				VoucherBO voucherBO = new VoucherBO();
				voucherBO.deleteRtVouchers(rtVoucherPks.keySet().toArray(new String[0]));
				// 删除总账凭证
				OperationResultVO[] result = voucherBO.deleteByPks(voucherPks.keySet().toArray(new String[0]), true, pk_voucher2org);

				// hurh
				if (result != null) {
					for (int j = 0; j < result.length; j++) {
						switch (result[j].m_intSuccess) {
							case 0:
								break;
							case 1:
								break;
							case 2:
								throw new BusinessException(result[j].m_strDescription);
							default:
						}
					}
				}
			}
		} catch (Exception e1) {
			Logger.error(e1.getMessage(), e1);
			throw new BusinessException(e1.getMessage(), e1);
		}
	}

	@Override
	public Collection<UFBoolean> isRollbackable(Collection<FipRelationInfoVO> vo) throws BusinessException {
		ArrayList<UFBoolean> rs = null;
		if (vo != null && !vo.isEmpty()) {
			rs = new ArrayList<UFBoolean>();
			for (FipRelationInfoVO fipRelationInfoVO : vo) {
				rs.add(isRollbackable(fipRelationInfoVO));
			}
		}
		return rs;
	}

	@Override
	public Collection<FipSaveResultVO> saveBill(Collection<FipSaveResultVO> voarray) throws BusinessException {
		if (voarray != null && !voarray.isEmpty()) {
			return new VoucherPrepareService4Fip().saveRTVoucher(voarray);
		}
		return null;
	}

	/*
	 * queryBillByRelations方法在IBillReflectorService中的实现
	 * 
	 * @see nc.pubitf.fip.external.IBillReflectorService#queryBillByRelations(java.util.Collection)
	 */
	@Override
	public Collection<FipExtendAggVO> queryBillByRelations(Collection<FipRelationInfoVO> relationvos) throws BusinessException {
		ArrayList<FipExtendAggVO> rs = null;
		if (relationvos != null && !relationvos.isEmpty()) {
			ArrayList<String> pklist = new ArrayList<String>();
			for (FipRelationInfoVO fipRelationInfoVO : relationvos) {
				pklist.add(fipRelationInfoVO.getRelationID());
			}
			VoucherVO[] vos = new VoucherBO().queryByPks(pklist.toArray(new String[0]));
			if (vos != null && vos.length > 0) {
				rs = new ArrayList<FipExtendAggVO>();
				FipExtendAggVO tempvo = null;
				for (int i = 0; i < vos.length; i++) {
					tempvo = new FipExtendAggVO();
					// 这里应该转成MDVoucher返回
					tempvo.setBillVO(vos[i]);
					tempvo.setRelationID(vos[i].getPk_voucher());
					rs.add(tempvo);
				}
			}
		}
		return rs;
	}

	@Override
	public Collection<FipBillSumRSVO> querySumBill(Collection<FipBillSumRSVO> relationvos, String pk_sumrule) throws BusinessException {
		this.pk_sumrule = pk_sumrule;
		ArrayList<FipBillSumRSVO> rs = new ArrayList<FipBillSumRSVO>();
		boolean combinRelation = false;
		Object voucherobj = null;
		FipRelationInfoVO fiprelVo = null;
		String pk_group = "";
		if (relationvos != null) {
			FipBillSumRSVO[] fipbill = new FipBillSumRSVO[relationvos.size()];
			fipbill = relationvos.toArray(fipbill);
			if (fipbill[0].getRelationvos() != null) {
				combinRelation = true;
				pk_group = fipbill[0].getRelationvos()[0].getPk_group();
			} else {
				voucherobj = fipbill[0].getBillVO();
				fiprelVo = fipbill[0].getMessageinfo();
				pk_group = fiprelVo.getPk_group();
			}
		}
		if (combinRelation) {
			Collection<VoucherVO> vouchers = null;
			ConcurrentHashMap<String, VoucherVO> mergedHead = null;
			ConcurrentHashMap<String, List<String>> groupMap = new ConcurrentHashMap<String, List<String>>();
			ConcurrentHashMap<String, List<FipRelationInfoVO>> relationMap = new ConcurrentHashMap<String, List<FipRelationInfoVO>>();
			int groupIndex = 0;// 分组号
			for (FipBillSumRSVO fipBillSumRSVO : relationvos) {
				groupIndex++;
				String groupKey = getVoucherNoStr(groupIndex);
				FipRelationInfoVO[] rels = fipBillSumRSVO.getRelationvos();
				for (int i = 0; rels != null && i < rels.length; i++) {
					if (null == groupMap.get(groupKey)) {
						List<String> keys = new ArrayList<String>();
						groupMap.put(groupKey, keys);
						keys.add(rels[i].getRelationID());
					} else {
						groupMap.get(groupKey).add(rels[i].getRelationID());
					}
					if (null == relationMap.get(groupKey)) {
						List<FipRelationInfoVO> relations = new ArrayList<FipRelationInfoVO>();
						relationMap.put(groupKey, relations);
						relations.add(rels[i]);
					} else {
						relationMap.get(groupKey).add(rels[i]);
					}
				}
			}
			Connection con = null;
			String tempTable = null;
			try {
				// 创建临时表
				con = ConnectionFactory.getConnection();
				tempTable = createTempTable(con);
				((CrossDBConnection) con).setAddTimeStamp(false);
				prepareTempTable(tempTable, groupMap);
				// debugTemp(session,tempTable);
				mergedHead = mergeHead(tempTable, groupMap);
				vouchers = processSumData(tempTable, groupMap, relationMap, mergedHead, pk_group);
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			} finally {
				try {
					if (con != null)
						con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Logger.error(e.getMessage(), e);
				}
			}
			String userId = InvocationInfoProxy.getInstance().getUserId();
			for (VoucherVO voucher : vouchers) {
				FipBillSumRSVO rsvo = new FipBillSumRSVO();
				rsvo.setBillVO(voucher);
				rsvo.setRelationvos(relationMap.get(voucher.getUserData().toString().trim()).toArray(new FipRelationInfoVO[0]));
				rsvo.setMessageinfo(makeInfo(voucher));
				rs.add(rsvo);
				
				//如果制单人为空，则设置为当前操作人
				if(!StringUtils.isEmpty(userId) && StringUtils.isEmpty(voucher.getPk_prepared())){
					voucher.setPk_prepared(userId);
				}
			}
		} else {
			try {
				VoucherVO vouchervo = VoucherPrepareService4Fip.fip2gl(voucherobj,false);
				VoucherPrepareService4Fip.processVoucher(vouchervo, fiprelVo, true);
				new VoucherPrepareService4Fip().checkDetail(vouchervo);
				VoucherVO voucherNewVo = mergeDetailAndGetGLVo(vouchervo);
				FipBillSumRSVO rsvo = new FipBillSumRSVO();
				rsvo.setBillVO(voucherNewVo);
				rsvo.setRelationvos(tranVoucherToFipRelationInfo(voucherNewVo));
				rsvo.setMessageinfo(makeInfo(voucherNewVo));
				rs.add(rsvo);
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			}
		}
		return rs;
	}
	
	private int maxLength = 8;
    private String getVoucherNoStr(Integer groupNo)
    {
        StringBuffer rtStr = new StringBuffer();
        if(groupNo == null)
            return null;
        String string = groupNo.toString();
        for(int i = string.length(); i < maxLength; i++)
            rtStr.append("0");

        rtStr.append(string);
        return rtStr.toString();
    }

	private FipRelationInfoVO[] tranVoucherToFipRelationInfo(VoucherVO voucherVo) {
		return null;

	}

	// 合并表头数据
	private ConcurrentHashMap<String, VoucherVO> mergeHead(String tempTable, ConcurrentHashMap<String, List<String>> groupMap) throws BusinessException {
		ConcurrentHashMap<String, List<VoucherVO>> headVoucher = new ConcurrentHashMap<String, List<VoucherVO>>();
		ConcurrentHashMap<String, VoucherVO> vouchers = new ConcurrentHashMap<String, VoucherVO>();
		String sql = "select b.groupid as groupid,pk_group, pk_org,pk_accountingbook,pk_vouchertype," + "max(year) as year, max(period) as period, 0 as no, max(prepareddate) as prepareddate, null as pk_system," + " null as tallydate, sum(attachment) as attachment, max(pk_prepared) as pk_prepared, null as checker, null as casher, "
				+ " null as manager, min(signflag) as signflag,  min(modifyflag) as modifyflag, 'N' as discardflag, max(addclass) as addclass," + " max(deleteclass) as deleteclass, min(DETAILMODFLAG) as detailModflag, null as pk_voucher, " + " null as pk_billtype ";
		String where = "from gl_rtvoucher a inner join " + tempTable + " b on a.pk_voucher = b.pk_voucher";
		String groupby = " group by groupid,pk_group,pk_org,pk_accountingbook,pk_vouchertype ";
		Connection conn = null;
		Statement smt = null;
		try {
			conn = ConnectionFactory.getConnection();
			smt = conn.createStatement();
			ResultSet rs = smt.executeQuery(sql + where + groupby);
			while (rs.next()) {
				VoucherVO vo = new VoucherVO();
				String groupid = rs.getString("groupid");
				vo.setUserData(rs.getString("groupid"));
				vo.setPk_group(rs.getString("pk_group"));
				vo.setPk_org(rs.getString("pk_org"));
				vo.setPk_accountingbook(rs.getString("pk_accountingbook"));
				vo.setPk_vouchertype(rs.getString("pk_vouchertype"));
				vo.setYear(rs.getString("year"));
				vo.setPeriod(rs.getString("period"));
				vo.setPrepareddate(new UFDate(new UFDateTime(rs.getString("prepareddate")).getMillis()));
				vo.setAttachment(rs.getInt("attachment"));
				vo.setPk_prepared(rs.getString("pk_prepared"));
				vo.setSignflag(UFBoolean.valueOf(rs.getString("signflag")));
				vo.setModifyflag(rs.getString("modifyflag"));
				vo.setDiscardflag(UFBoolean.valueOf("discardflag"));
				vo.setAddclass(rs.getString("addclass"));
				vo.setDeleteclass(rs.getString("deleteclass"));
				vo.setDetailmodflag(UFBoolean.valueOf(rs.getString("detailModflag")));
				// vo.setFreevalue1(rs.getString("freevalue1"));
				// vo.setFreevalue2(rs.getString("freevalue2"));
				// vo.setFreevalue3(rs.getString("freevalue3"));
				// vo.setFreevalue4(rs.getString("freevalue4"));
				// vo.setFreevalue5(rs.getString("freevalue5"));
				if (vo.getVoucherkind() == null)
					vo.setVoucherkind(0);
				if (null == headVoucher.get(groupid)) {
					List<VoucherVO> grouphead = new ArrayList<VoucherVO>();
					grouphead.add(vo);
					headVoucher.put(groupid, grouphead);
				} else {
					headVoucher.get(groupid).add(vo);
				}
			}
			vouchers = combinSystem(headVoucher, groupMap, tempTable);
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			try {
				if (smt != null)
					smt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return vouchers;
	}

	/**
	 * 合并不同来源系统单据
	 * 
	 * @param headVoucher
	 * @param groupMap
	 * @return
	 * @throws BusinessException
	 */
	private ConcurrentHashMap<String, VoucherVO> combinSystem(ConcurrentHashMap<String, List<VoucherVO>> headVoucher, ConcurrentHashMap<String, List<String>> groupMap, String tempTable) throws BusinessException {
		Set<String> groups = headVoucher.keySet();
		ConcurrentHashMap<String, VoucherVO> vouchersMap = new ConcurrentHashMap<String, VoucherVO>();
		ConcurrentHashMap<String, String> systems = getSystems(groupMap, tempTable);
		for (String groupid : groups) {
			List<VoucherVO> vouchers = headVoucher.get(groupid);
			if (vouchers.size() == 1) {
				if (!StringUtils.isEmpty(systems.get(groupid))) {
					vouchers.get(0).setPk_system(systems.get(groupid));
					vouchersMap.put(groupid, vouchers.get(0));
				} else {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0004")/* @res "来源系统为空，请检查" */);
				}
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0047")/* @res "数据中因为有些字段不同，有不能合并成一张凭证的情况，请检查下面的字段是否相同：集团，组织，财务核算账簿，凭证类别。" */);
			}
		}
		return vouchersMap;
	}

	private ConcurrentHashMap<String, String> getSystems(ConcurrentHashMap<String, List<String>> groupMap, String tempTable) throws BusinessException {
		// hurh 60 性能优化，一次查询
		List<String> allList = new LinkedList<String>();
		for (String groupid : groupMap.keySet()) {
			allList.addAll(groupMap.get(groupid));
		}
		ConcurrentHashMap<String, String> systemMap = querySystems(allList, tempTable);

		return systemMap;
	}

	/**
	 * hurh 60性能优化
	 */
	@SuppressWarnings("unchecked")
	private ConcurrentHashMap<String, String> querySystems(List<String> list, String tempTable) throws BusinessException {
		ConcurrentHashMap<String, String> systems = null;
		String sql = null;
		try {
			sql = "select distinct groupid,pk_system from gl_rtvoucher a inner join " + tempTable + " b on a.pk_voucher = b.pk_voucher where " + GLSqlUtil.buildInSql("a.pk_voucher", list);
			BaseDAO dao = new BaseDAO();
			systems = (ConcurrentHashMap<String, String>) dao.executeQuery(sql, new ResultSetProcessor() {

				private static final long serialVersionUID = 1L;

				public Object handleResultSet(ResultSet rs) throws SQLException {
					ConcurrentHashMap<String, String> resultMap = new ConcurrentHashMap<String, String>();
					String groupid = null;
					String pk_system = null;
					String str = null;
					while (rs.next()) {
						groupid = rs.getString("groupid");
						pk_system = rs.getString("pk_system");
						if (groupid != null && pk_system != null) {
							str = resultMap.get(groupid);
							if (str == null) {
								str = pk_system.trim();
							} else {
								str = str + "," + pk_system.trim();
							}
							resultMap.put(groupid, str);
						}
					}
					for (String s : resultMap.keySet()) {
						str = resultMap.get(s);
						if (str == null || "".equals(str)) {
							continue;
						}
						str = str.substring(0, str.length() - 1);
					}
					return resultMap;
				}
			});
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
			throw new BusinessException(e.getMessage(), e);
		}
		return systems;
	}

	private void debugTemp(JdbcSession session, String tempTable) {
		Connection conn = session.getConnection();
		String sql = "select pk_voucher,groupid from " + tempTable;
		Statement smt = null;
		try {
			smt = conn.createStatement();
			ResultSet rst = smt.executeQuery(sql);
			while (rst.next()) {
				String pk_voucher = rst.getString(1);
				String groupid = rst.getString(2);
				Logger.error("groupid:" + groupid + ",pk_voucher:" + pk_voucher + "\n");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Logger.error(e.getMessage(), e);
		} finally {
			try {
				if (smt != null)
					smt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	private FipRelationInfoVO makeInfo(VoucherVO voucher) {
		FipRelationInfoVO infoVO = new FipRelationInfoVO();
		// infoVO.setRelationID(relationID);
		infoVO.setPk_billtype("C0");
		infoVO.setPk_system(voucher.getPk_system());
		infoVO.setPk_group(voucher.getPk_group());
		infoVO.setPk_org(voucher.getPk_org());
		return infoVO;
	}

	private FipRelationInfoVO makeInfo(MDVoucher voucher) {
		FipRelationInfoVO infoVO = new FipRelationInfoVO();
		// infoVO.setRelationID(relationID);
		infoVO.setPk_billtype("C0");
		infoVO.setPk_system(voucher.getPk_system());
		infoVO.setPk_group(voucher.getPk_group());
		infoVO.setPk_org(voucher.getPk_org());
		return infoVO;
	}

	private Collection<VoucherVO> processSumData(String tempTable, ConcurrentHashMap<String, List<String>> groupMap, ConcurrentHashMap<String, List<FipRelationInfoVO>> relationMap, ConcurrentHashMap<String, VoucherVO> vouchers, String pk_group) throws BusinessException {
		boolean isSumAccasoa = false;
		boolean isSumExplation = false;
		boolean isPriceExplation = false;
		boolean needorder = true;
		String combinType = "";
		Expaccount[] exps = null;
		List<String> exppks = null;
		CombinschemeVO parent = null;
		Aggcombinscheme scheme = null;
		
		Collection<VoucherVO> values = vouchers.values();
		VoucherVO voucherVO = values.toArray(new VoucherVO[0])[0];
		String pk_accountingbook = voucherVO.getPk_accountingbook();
		
		if (pk_sumrule != null && !"".equals(pk_sumrule)) {
			scheme = SumRuleProxy.getQryService().queryByID(pk_sumrule);
		} else {
			if(vouchers != null && vouchers.size()>0) {

				if(values != null && values.size()>0) {
					
					scheme = SumRuleProxy.getQryService().queryByPkAccountingBook(pk_accountingbook);
					
					if(scheme == null){
						SetOfBookVO setOfBookVO = AccountBookUtil.getSetOfBookVOByPk_accountingBook(pk_accountingbook);
						if(setOfBookVO != null){
							scheme = SumRuleProxy.getQryService().queryByPkAccountingBook(setOfBookVO.getPk_setofbook());
						}
					}
				}
				
			}
			if (scheme == null) {
				scheme = new Aggcombinscheme();
				CombinschemeVO schevo = new CombinschemeVO();
				schevo.setIscombinsameacc(UFBoolean.TRUE);
				schevo.setIscombindifexp(UFBoolean.FALSE);
				schevo.setDetailorder(SchemeConst.ORDERTYPE_D);
				schevo.setCombintype(SchemeConst.COMBINTYPE_DC);
				schevo.setIscombindifdirection(UFBoolean.FALSE);
				scheme.setParentVO(schevo);
			}
		}
		parent = (CombinschemeVO) scheme.getParentVO();
		// 科目与辅助相同时合并
		isSumAccasoa = null != parent.getIscombinsameacc() && parent.getIscombinsameacc().booleanValue();
		// 摘要不同时合并
		isSumExplation = null != parent.getIscombindifexp() && parent.getIscombindifexp().booleanValue();
		
		// 单价不同时合并
		isPriceExplation = null != parent.getIscombindifprice() && parent.getIscombindifprice().booleanValue();
		
		// 合并方式
		combinType = parent.getCombintype();
		needorder = !SchemeConst.ORDERTYPE_NO.equals(parent.getDetailorder());
		exppks = new ArrayList<String>();
		exps = (Expaccount[]) scheme.getChildrenVO();
		if (null != exps && exps.length > 0) {
			for (Expaccount expaccount : exps) {
				exppks.add(expaccount.getPk_accasoa());
			}
		}

		if(exppks.size() > 0) {
			IBDData[] datas = AccountUtilGL.getDocByPksAndDate(exppks.toArray(new String[0]) ,voucherVO.getPrepareddate().toLocalString());
			Set<String> accCodes = new HashSet<String>();
			if(datas != null && datas.length >0) {
				for(IBDData data : datas){
					if(data!=null)
						accCodes.add(data.getCode());
				}
			}
			if(accCodes.size() >0) {
				datas = AccountUtilGL.getEndDocByCodesVersion(voucherVO.getPk_accountingbook(), accCodes.toArray(new String[0]),voucherVO.getPrepareddate().toLocalString());
				exppks.clear();
				if(datas != null && datas.length >0) {
					for(IBDData data : datas){
						if(data!=null)
							exppks.add(data.getPk());
					}
				}
			}
		}
		
		List<VoucherVO> vos = new ArrayList<VoucherVO>();
		Connection conn = null;
		Statement stm = null;
		ConcurrentHashMap<String, Vector<DetailVO>> detailsMap = new ConcurrentHashMap<String, Vector<DetailVO>>();
		try {
			//检查欧盟报表是否启用
			boolean checkEURStart = GLStartCheckUtil.checkEURStart(pk_group);
			
			conn = ConnectionFactory.getConnection();
			stm = conn.createStatement();
			((CrossDBConnection) conn).setAddTimeStamp(false);
			String[] sqls = getSumSql(tempTable,checkEURStart);
			String commonSql = getCommonSql(tempTable,checkEURStart);
			String selectSql = sqls[0];
			String whereSql = sqls[1];
			String groupSql = sqls[2];
			String orderSql = sqls[3];
			if (isSumExplation) {
				selectSql = selectSql.replaceFirst("a.explanation", "max(a.explanation)");
				groupSql = groupSql.replaceFirst(",a.explanation,", ",");
			}
			
			if (isPriceExplation) {
				selectSql = selectSql.replaceFirst("price", "sum(price)");
				groupSql = groupSql.replaceFirst(", price,", ",");
			}
			
			Vector<DetailVO> unsumlist = new Vector<DetailVO>();
			Vector<DetailVO> sumlist = new Vector<DetailVO>();
			if (isSumAccasoa) {
				// 例外科目不合并
				String condition = sqls[1];
				if (null != exppks && exppks.size() != 0) {
					condition += " and " + SqlUtils.getInStr("a.pk_accasoa", exppks, true);
					condition = commonSql + condition;
					if (needorder)
						condition += orderSql;
					ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(condition),checkEURStart);
					mixDetail(unsumlist, groupMapFromResultSet);
					detailsMap = merge(detailsMap, groupMapFromResultSet);
				}
				String sql = "";
				// 非例外科目合并
				condition = whereSql;
				if (null != exppks && exppks.size() != 0) {
					String temp = SqlUtils.getInStr(" a.pk_accasoa", exppks, true);
					condition += " and " + temp.replaceFirst("in", "not in");
				}
				//是否借贷合并  如果借贷合并需要判断  如果借贷不一致是否合并
				boolean isCOMBINTYPE_DC = false;
				
				if (combinType.equals(SchemeConst.COMBINTYPE_D)) {
					if(checkEURStart) {//如果启用了欧盟报表，则需要将借贷方全部是0的数据查询出来
						sql += condition + " and (debitquantity<>0 or localdebitamount<>0 or groupdebitamount<>0 or globaldebitamount<>0 or (localdebitamount=0 and localcreditamount=0)) ";
					}else {
						sql += condition + " and (debitquantity<>0 or localdebitamount<>0 or groupdebitamount<>0 or globaldebitamount<>0) ";
					}
					String condition2 = condition + " and (creditquantity<>0 or localcreditamount<>0 or groupcreditamount<>0 or globalcreditamount<>0) ";
					condition2 = commonSql + condition2;
					if (needorder)
						condition2 += orderSql;
					ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(condition2),checkEURStart);
					mixDetail(unsumlist, groupMapFromResultSet);
					detailsMap = merge(detailsMap, groupMapFromResultSet);
					isCOMBINTYPE_DC = false;
				} else if (combinType.equals(SchemeConst.COMBINTYPE_C)) {
					if(checkEURStart) {
						sql += condition + " and (creditquantity<>0 or localcreditamount<>0 or groupcreditamount<>0 or globalcreditamount<>0 or (localcreditamount=0 and localdebitamount=0)) ";
					}else {
						sql += condition + " and (creditquantity<>0 or localcreditamount<>0 or groupcreditamount<>0 or globalcreditamount<>0) ";
					}
					String condition2 = condition + " and (debitquantity<>0 or localdebitamount<>0 or groupdebitamount<>0 or globaldebitamount<>0) ";
					condition2 = commonSql + condition2;
					if (needorder)
						condition2 += orderSql;
					ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(condition2),checkEURStart);
					mixDetail(unsumlist, groupMapFromResultSet);
					detailsMap = merge(detailsMap, groupMapFromResultSet);
					isCOMBINTYPE_DC = false;
				} else if (combinType.equals(SchemeConst.COMBINTYPE_DC)) {
					sql += condition;
					
					boolean is_sum_dire = false;
					if (parent != null && parent.getIscombindifdirection() != null) {
						is_sum_dire = parent.getIscombindifdirection().booleanValue();
					}
					//借贷方向不一致则不进行汇总，需要分别将借方、贷方合并
					if(!is_sum_dire) {
						isCOMBINTYPE_DC = true;
					}else {//借贷方向不一致  进行汇总  则走原来逻辑
						isCOMBINTYPE_DC = false;
					}
				}
				
				//如果是借方合并、贷方合并 则走原来逻辑
				if(!isCOMBINTYPE_DC) {
					String wherepart = sql;
					sql = selectSql + sql + groupSql + orderSql;
					ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
					mixDetail(sumlist, groupMapFromResultSet);
					StringBuffer fromcon = new StringBuffer();
					fromcon.append("from gl_rtdetail a inner join " + tempTable + " b on a.pk_voucher = b.pk_voucher ");
					
					//如果启用了欧盟报表产品
					if(checkEURStart) {
						fromcon.append(" LEFT OUTER JOIN GL_VATDETAIL vat ON a.PK_DETAIL = vat.pk_detail ");
					}
					
					catCashFlowCaseForSUM2(sumlist, groupSql.substring(6), fromcon.toString(), wherepart);
					detailsMap = merge(detailsMap, groupMapFromResultSet);
				}else {//如果是借贷合并  并且借贷方向不一致 进行汇总
					
					//合并借方
					sql = selectSql + condition + " and (debitquantity<>0 or localdebitamount<>0 or groupdebitamount<>0 or globaldebitamount<>0) " + groupSql + orderSql;
					
					ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
					mixDetail(sumlist, groupMapFromResultSet);
					detailsMap = merge(detailsMap, groupMapFromResultSet);
					
					//合并贷方
					sql = selectSql + condition + " and (creditquantity<>0 or localcreditamount<>0 or groupcreditamount<>0 or globalcreditamount<>0) " + groupSql + orderSql;

					groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
					mixDetail(sumlist, groupMapFromResultSet);
					detailsMap = merge(detailsMap, groupMapFromResultSet);
					
					if(checkEURStart) {
						//合并借贷方全部是0的数据
						sql = selectSql + condition + " and (creditquantity=0 and debitquantity=0 and localcreditamount=0 and localdebitamount=0) " + groupSql + orderSql;
						groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
						mixDetail(sumlist, groupMapFromResultSet);
						detailsMap = merge(detailsMap, groupMapFromResultSet);
					}
					
					StringBuffer fromcon = new StringBuffer();
					fromcon.append("from gl_rtdetail a inner join " + tempTable + " b on a.pk_voucher = b.pk_voucher ");
					
					//如果启用了欧盟报表产品
					if(checkEURStart) {
						fromcon.append(" LEFT OUTER JOIN GL_VATDETAIL vat ON a.PK_DETAIL = vat.pk_detail ");
					}
					catCashFlowCaseForSUM2(sumlist, groupSql.substring(6), fromcon.toString(), condition);
				}
			} else {
				String sql = "";
				String condition = sqls[1];
				if (exppks != null && exppks.size() != 0) {
					// 例外科目合并
					condition += " and " + SqlUtils.getInStr("a.pk_accasoa", exppks, true);
					
					//是否借贷合并  如果借贷合并需要判断  如果借贷不一致是否合并
					boolean isCOMBINTYPE_DC = false;
					
					if (combinType.equals(SchemeConst.COMBINTYPE_D)) {
						if(checkEURStart) {
							sql += condition + " and (debitquantity<>0 or localdebitamount<>0 or groupdebitamount<>0 or globaldebitamount<>0 or (localdebitamount =0 and localcreditamount=0)) ";
						}else {
							sql += condition + " and (debitquantity<>0 or localdebitamount<>0 or groupdebitamount<>0 or globaldebitamount<>0) ";
						}
						String condition2 = condition + " and (creditquantity<>0 or localcreditamount<>0 or groupcreditamount<>0 or globalcreditamount<>0) ";
						condition2 = commonSql + condition2;
						if (needorder)
							condition2 += orderSql;
						ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(condition2),checkEURStart);
						mixDetail(unsumlist, groupMapFromResultSet);
						detailsMap = merge(detailsMap, groupMapFromResultSet);
						isCOMBINTYPE_DC = false;
					} else if (combinType.equals(SchemeConst.COMBINTYPE_C)) {
						if(checkEURStart) {
							sql += condition + " and (creditquantity<>0 or localcreditamount<>0 or groupcreditamount<>0  or globalcreditamount<>0 or (localcreditamount=0 and localdebitamount=0)) ";
						}else {
							sql += condition + " and (creditquantity<>0 or localcreditamount<>0 or groupcreditamount<>0  or globalcreditamount<>0) ";
						}
						String condition2 = condition + " and (debitquantity<>0 or localdebitamount<>0 or groupdebitamount<>0 or globaldebitamount<>0) ";
						condition2 = commonSql + condition2;
						if (needorder)
							condition2 += orderSql;
						ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(condition2),checkEURStart);
						mixDetail(unsumlist, groupMapFromResultSet);
						detailsMap = merge(detailsMap, groupMapFromResultSet);
						isCOMBINTYPE_DC = false;
					} else if (combinType.equals(SchemeConst.COMBINTYPE_DC)) {
						sql += condition;
						
						boolean is_sum_dire = false;
						if (parent != null && parent.getIscombindifdirection() != null) {
							is_sum_dire = parent.getIscombindifdirection().booleanValue();
						}
						//借贷方向不一致则不进行汇总，需要分别将借方、贷方合并
						if(!is_sum_dire) {
							isCOMBINTYPE_DC = true;
						}else {//借贷方向不一致  进行汇总  则走原来逻辑
							isCOMBINTYPE_DC = false;
						}
					}
					
					//如果是借方合并、贷方合并 则走原来逻辑
					if(!isCOMBINTYPE_DC) {
						sql = selectSql + whereSql + groupSql + orderSql;
						ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
						mixDetail(sumlist, groupMapFromResultSet);
						StringBuffer fromcon = new StringBuffer();
						fromcon.append("from gl_rtdetail a inner join " + tempTable + " b on a.pk_voucher = b.pk_voucher");
						//如果启用了欧盟报表产品
						if(checkEURStart) {
							fromcon.append(" LEFT OUTER JOIN GL_VATDETAIL vat ON a.PK_DETAIL = vat.pk_detail ");
						}
						catCashFlowCaseForSUM2(sumlist, groupSql.substring(6), fromcon.toString(), whereSql);
						detailsMap = merge(detailsMap, groupMapFromResultSet);
					}else {//如果是借贷合并  并且借贷方向不一致 进行汇总
						
						//合并借方
						sql = selectSql + condition + " and (debitquantity<>0 or localdebitamount<>0 or groupdebitamount<>0 or globaldebitamount<>0) " + groupSql + orderSql;
						
						ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
						mixDetail(sumlist, groupMapFromResultSet);
						detailsMap = merge(detailsMap, groupMapFromResultSet);
						
						//合并贷方
						sql = selectSql + condition + " and (creditquantity<>0 or localcreditamount<>0 or groupcreditamount<>0 or globalcreditamount<>0) " + groupSql + orderSql;

						groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
						mixDetail(sumlist, groupMapFromResultSet);
						detailsMap = merge(detailsMap, groupMapFromResultSet);
						
						if(checkEURStart) {
							//需要将借贷方全部是0的数据查询出来
							sql = selectSql + condition + " and (debitquantity=0 and creditquantity=0 and localdebitamount=0 and localcreditamount=0) " + groupSql + orderSql;

							groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
							mixDetail(sumlist, groupMapFromResultSet);
							detailsMap = merge(detailsMap, groupMapFromResultSet);
						}
						
						StringBuffer fromcon = new StringBuffer();
						fromcon.append("from gl_rtdetail a inner join " + tempTable + " b on a.pk_voucher = b.pk_voucher ");
						
						//如果启用了欧盟报表产品
						if(checkEURStart) {
							fromcon.append(" LEFT OUTER JOIN GL_VATDETAIL vat ON a.PK_DETAIL = vat.pk_detail ");
						}
						catCashFlowCaseForSUM2(sumlist, groupSql.substring(6), fromcon.toString(), condition);
					}
				}
				String condition1 = null;
				if (exppks != null && exppks.size() != 0)
					condition1 = condition .replaceFirst("in", "not in");
				else
					condition1 = condition;
				sql = commonSql+condition1;
//				sql = selectSql + condition1 + groupSql + orderSql;
				ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
				mixDetail(unsumlist, groupMapFromResultSet);
				detailsMap = merge(detailsMap, groupMapFromResultSet);
			}
			catCashFlowCase(unsumlist);
			vos = constructVouchers(detailsMap, relationMap, vouchers, parent);
		} catch (SQLException e) {
			Logger.error(e);
			throw new BusinessException(e);

		} finally {
			try {
				if (stm != null)
					stm.close();
				if (conn != null)
					conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return vos;
	}

	private void mixDetail(Vector<DetailVO> vos, Map<String, Vector<DetailVO>> groupmap) {
		if (groupmap != null && !groupmap.isEmpty()) {
			Collection<Vector<DetailVO>> values = groupmap.values();
			for (Vector<DetailVO> vector : values) {
				if (vector != null)
					vos.addAll(vector);
			}
		}
	}

	private void catCashFlowCase(Vector<DetailVO> vos) throws BusinessException {
		if (vos == null || vos.size() == 0)
			return;
		String[] pk_details = new String[vos.size()];
		HashMap<String, String> map_detailpk_curr = new HashMap<String, String>();
		for (int i = 0; i < pk_details.length; i++) {
			pk_details[i] = vos.elementAt(i).getPk_detail();
			map_detailpk_curr.put(pk_details[i], vos.elementAt(i).getPk_currtype());
		}
		ICashFlowCase cashflowproxy = (ICashFlowCase) NCLocator.getInstance().lookup(ICashFlowCase.class.getName());
		// 如果不是总帐凭证，那么这个接口读出来的CashflowcaseVO里有可能没有币种信息，需要补上
		CashflowcaseVO[] cashflowcasevos = cashflowproxy.queryByPKRtDetails(pk_details);
		if (cashflowcasevos != null && cashflowcasevos.length > 0) {
			HashMap<String, ArrayList<CashflowcaseVO>> pk_cashflow_map = new HashMap<String, ArrayList<CashflowcaseVO>>();
			ArrayList<CashflowcaseVO> volist = null;
			for (int i = 0; i < cashflowcasevos.length; i++) {
				if (cashflowcasevos[i] != null && cashflowcasevos[i].getM_pk_currtype() == null)
					cashflowcasevos[i].setM_pk_currtype(map_detailpk_curr.get(cashflowcasevos[i].getPk_detail()));
				volist = pk_cashflow_map.get(cashflowcasevos[i].getPk_detail());
				if (volist == null) {
					volist = new ArrayList<CashflowcaseVO>();
					pk_cashflow_map.put(cashflowcasevos[i].getPk_detail(), volist);
				}
				volist.add(cashflowcasevos[i]);
			}
			for (int i = 0; i < vos.size(); i++) {
				volist = pk_cashflow_map.get(vos.elementAt(i).getPk_detail());
				if (volist != null && volist.size() > 0) {
					CashflowcaseVO[] detailcashvos = new CashflowcaseVO[volist.size()];
					volist.toArray(detailcashvos);
					UserDataVO udvo = new UserDataVO();
					udvo.setDatatype("现金流量"/*-=notranslate=-*/);
					udvo.setUserdata(detailcashvos);
					Vector<UserDataVO> v = vos.elementAt(i).getOtheruserdata();
					if (v == null)
						v = new Vector<UserDataVO>();
					v.addElement(udvo);
					vos.elementAt(i).setOtheruserdata(v);
				}
			}
		}
	}

	private void catCashFlowCaseForSUM2(Vector<DetailVO> sumlist, String bystr, String fromcon, String sumsqlwherepart) throws SQLException, BusinessException {
		if (sumlist == null || sumlist.isEmpty())
			return;
		String querysql = "select a.pk_detail,min(a.pk_detail) over(partition " + bystr + ") as groupid " + fromcon + "" + (sumsqlwherepart == null ? "" : sumsqlwherepart);
//		String tablename = TempTableUtils.getTempTablename("detailgroup");
		//因为临时表过多，修改成固定临时表
		String tablename = "T_DETAILGROUP";
		
		String pkfield = "pk_detail";
		String groupfield = "groupid";
		tablename = TempTableUtils.createTempTable(tablename, " " + pkfield + " varchar(40), " + groupfield + " varchar(40) ", null);
		Connection con = null;
		Statement stmt = null;

		try {
			String insertsql = "insert into " + tablename + " (" + pkfield + "," + groupfield + ") (" + querysql + ")";
			con = ConnectionFactory.getConnection();
			stmt = con.createStatement();
			stmt.executeUpdate(insertsql);
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new SQLException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("fidap", "UPPfidap-000160")/* @res "查询分录信息出现异常：" */+ ex.getMessage());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		// 获得现金流量信息
		String[] tableinfo = new String[] {
				tablename, pkfield, groupfield
		};
		CashflowcaseVO[] cashvo = null;
		try {
			ICashFlowCase cashflowproxy = (ICashFlowCase) NCLocator.getInstance().lookup(ICashFlowCase.class.getName());
			cashvo = cashflowproxy.querySumCashflow4Fip(tableinfo);
		} finally {
			// 临时表使用完后要销毁，不然可能造成表泄漏
			// gbh
//			if (tablename != null)
//				TempTableUtils.dropTempTable(tablename);
		}

		if (cashvo != null) {
			HashMap<String, List<CashflowcaseVO>> cashmap = new HashMap<String, List<CashflowcaseVO>>();
			for (CashflowcaseVO cashflowcaseVO : cashvo) {
				List<CashflowcaseVO> list = cashmap.get(cashflowcaseVO.getPk_detail());
				if (list == null) {
					list = new ArrayList<CashflowcaseVO>();
					cashmap.put(cashflowcaseVO.getPk_detail(), list);
				}
				list.add(cashflowcaseVO);
			}
			ArrayList<String> extGroup = new ArrayList<String>();// 需要展开不汇总的分组，这是由于该组分录合计为0可是又有现金流量造成的
			int listsize = sumlist.size();
			for (int k = 0; k < listsize; k++) {
				DetailVO detailVO = sumlist.elementAt(k);
				List<CashflowcaseVO> list = cashmap.get(detailVO.getPk_detail());
				if (list != null && !list.isEmpty()) {
					// 如果不是总帐凭证，那么这个接口读出来的CashflowcaseVO里有可能没有币种信息，需要补上
					CashflowcaseVO[] cashflowcasevos = new CashflowcaseVO[list.size()];
					list.toArray(cashflowcasevos);
					if (cashflowcasevos != null) {
						for (int i = 0; i < cashflowcasevos.length; i++) {
							if (cashflowcasevos[i] != null && cashflowcasevos[i].getM_pk_currtype() == null)
								cashflowcasevos[i].setM_pk_currtype(detailVO.getPk_currtype());
						}
						UserDataVO udvo = new UserDataVO();
						udvo.setDatatype("现金流量"/*-=notranslate=-*/);
						udvo.setUserdata(cashflowcasevos);
						Vector<UserDataVO> v = detailVO.getOtheruserdata();
						if (v == null)
							v = new Vector<UserDataVO>();
						v.addElement(udvo);
						// 金额的校验，有现金流量的分录不允许出现借贷合计为0的情况，因为借贷合计为0的分录可能被删除，会缺失部分现金流量数据
						if (detailVO.getLocalcreditamount().equals(detailVO.getLocaldebitamount())) {
							// 借贷相等的分录合计金额有可能为0，但是该分录有现金流量的话不允许删除
							if (detailVO.getLocalcreditamount().equals(UFDouble.ZERO_DBL)) {
								if (detailVO.getDebitquantity().equals(detailVO.getCreditquantity())) {
									if (detailVO.getDebitquantity().equals(UFDouble.ZERO_DBL)) {
										// 当借贷都为0的时候，该分录的分组重新计算，不允许合并
										extGroup.add(detailVO.getPk_detail());
									} else {
										// 都不为0时，把该分录拆分为1借1贷，现金流量放在借方
										DetailVO detail1 = (DetailVO) detailVO.clone();
										detailVO.setCreditquantity(UFDouble.ZERO_DBL);
										detailVO.setCreditamount(UFDouble.ZERO_DBL);
										detailVO.setFraccreditamount(UFDouble.ZERO_DBL);
										detailVO.setLocalcreditamount(UFDouble.ZERO_DBL);
										detail1.setDebitquantity(UFDouble.ZERO_DBL);
										detail1.setDebitamount(UFDouble.ZERO_DBL);
										detail1.setFracdebitamount(UFDouble.ZERO_DBL);
										detail1.setLocaldebitamount(UFDouble.ZERO_DBL);
										sumlist.addElement(detail1);
										detailVO.setOtheruserdata(v);
									}
								} else {
									detailVO.setOtheruserdata(v);
								}
							} else {
								// 都不为0时，把该分录拆分为1借1贷，现金流量放在借方
								DetailVO detail1 = (DetailVO) detailVO.clone();
								detailVO.setCreditquantity(UFDouble.ZERO_DBL);
								detailVO.setCreditamount(UFDouble.ZERO_DBL);
								detailVO.setFraccreditamount(UFDouble.ZERO_DBL);
								detailVO.setLocalcreditamount(UFDouble.ZERO_DBL);
								detail1.setDebitquantity(UFDouble.ZERO_DBL);
								detail1.setDebitamount(UFDouble.ZERO_DBL);
								detail1.setFracdebitamount(UFDouble.ZERO_DBL);
								detail1.setLocaldebitamount(UFDouble.ZERO_DBL);
								sumlist.addElement(detail1);
								detailVO.setOtheruserdata(v);
							}
						} else
							detailVO.setOtheruserdata(v);
					}
					cashmap.remove(detailVO.getPk_detail());
				}
			}
			Vector<DetailVO> details = null;
			if (!extGroup.isEmpty()) {
				// 对需要展开的分组进行展开
				try {
					IDetail detailprox = (IDetail) NCLocator.getInstance().lookup(IDetail.class.getName());
					details = detailprox.queryByCondition(" pk_detail in (select " + pkfield + " from " + tablename + " where " + SqlUtils.getInStr(groupfield, extGroup, true) + ")");
					catCashFlowCase(details);
					sumlist.addAll(details);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			}
			if (!cashmap.isEmpty()) {
				throw new BusinessException("补现金流量出错，分录和现金流量表的分组不一致造成部分现金流量没有归属。"/*-=notranslate=-*/);
			}
		}
		Logger.debug("补现金流量完成");
	}

	private ConcurrentHashMap<String, Vector<DetailVO>> merge(ConcurrentHashMap<String, Vector<DetailVO>> detailsMap, ConcurrentHashMap<String, Vector<DetailVO>> detailsMap1) {
		ConcurrentHashMap<String, Vector<DetailVO>> result = null;
		if (detailsMap.size() == 0)
			result = detailsMap1;
		else {
			Set<String> gd = detailsMap.keySet();
			Set<String> gc = detailsMap1.keySet();
			Set<String> gdc = new HashSet<String>();
			if(gc!=null){
				for(String str : gd){
					gdc.add(str);
				}
				for(String str : gc){
					gdc.add(str);
				}
			}
			for (String groupid : gdc) {
				if(detailsMap1.get(groupid) != null && detailsMap1.get(groupid).size()>0) {
					if(detailsMap.get(groupid)==null){
						detailsMap.put(groupid, new Vector<DetailVO>());
					}
					detailsMap.get(groupid).addAll(detailsMap1.get(groupid));
				}
			}
			result = detailsMap;
		}
		return result;
	}

	private String getCommonSql(String tempTable,boolean checkEURStart) {
		StringBuilder select = new StringBuilder();
		select.append("select ").append("a." + MDDetail.PK_DETAIL+" as "+MDDetail.PK_DETAIL).append(",a.").append(MDDetail.NOV+" as "+MDDetail.NOV).append(",a.").append(MDDetail.OPPOSITESUBJ+" as "+MDDetail.OPPOSITESUBJ).append(",a.").append(MDDetail.PK_INNERORG+" as "+MDDetail.PK_INNERORG).append(",a.").append(MDDetail.BANKACCOUNT+" as "+MDDetail.BANKACCOUNT).append(",a.").append(MDDetail.PK_VOUCHER+" as "+MDDetail.PK_VOUCHER).append(",a.").append(MDDetail.PK_ORG+" as "+MDDetail.PK_ORG).append(",a.").append(MDDetail.MODIFYFLAG+" as "+MDDetail.MODIFYFLAG).append(",a.").append(MDDetail.RECIEPTCLASS+" as "+MDDetail.RECIEPTCLASS).append(",a.")
				.append(MDDetail.DEBITAMOUNT+" as "+MDDetail.DEBITAMOUNT).append(",a.").append(MDDetail.DEBITQUANTITY+" as "+MDDetail.DEBITQUANTITY).append(",a.").append(MDDetail.LOCALDEBITAMOUNT+" as "+MDDetail.LOCALDEBITAMOUNT).append(",a.").append(MDDetail.GROUPDEBITAMOUNT+" as "+MDDetail.GROUPDEBITAMOUNT).append(",a.").append(MDDetail.GLOBALDEBITAMOUNT+" as "+MDDetail.GLOBALDEBITAMOUNT).append(",a.").append(MDDetail.CREDITAMOUNT+" as "+MDDetail.CREDITAMOUNT).append(",a.").append(MDDetail.CREDITQUANTITY+" as "+MDDetail.CREDITQUANTITY).append(",a.").append(MDDetail.LOCALCREDITAMOUNT+" as "+MDDetail.LOCALCREDITAMOUNT).append(",a.").append(
						MDDetail.GROUPCREDITAMOUNT+" as "+MDDetail.GROUPCREDITAMOUNT).append(",a.").append(MDDetail.GLOBALCREDITAMOUNT+" as "+MDDetail.GLOBALCREDITAMOUNT).append(",a.").append(MDDetail.PK_ACCOUNTINGBOOK).append(" as ").append(MDDetail.PK_ACCOUNTINGBOOK).append(",a.").append(MDDetail.PK_VOUCHERTYPEV).append(" as ").append(MDDetail.PK_VOUCHERTYPEV).append(" ,a.").append(MDDetail.YEARV).append(" as ").append(MDDetail.YEARV).append(",a.")
				.append(MDDetail.PERIODV).append(" as ").append(MDDetail.PERIODV).append(" ,a.").append(MDDetail.EXPLANATION).append(" ,a.").append(MDDetail.PK_ACCASOA).append(" as ").append(MDDetail.PK_ACCASOA).append(",a.").append(MDDetail.ASSID).append(" as ").append(MDDetail.ASSID).append(",a.").append(MDDetail.PK_CURRTYPE).append(" as ").append(MDDetail.PK_CURRTYPE).append("  ,a.").append(
						MDDetail.PRICE).append(" as ").append(MDDetail.PRICE).append(" ,a.").append(MDDetail.EXCRATE2).append(" as ").append(MDDetail.EXCRATE2).append(" ,a.").append(MDDetail.EXCRATE3).append(" as ").append(MDDetail.EXCRATE3).append(" ,a.").append(MDDetail.EXCRATE4).append(" as ").append(MDDetail.EXCRATE4).append(" ,a.").append(MDDetail.CHECKSTYLE).append(" as ").append(
						MDDetail.CHECKSTYLE).append(" ,a.").append(MDDetail.CHECKNO).append(" as ").append(MDDetail.CHECKNO).append(" ,a.").append(MDDetail.CHECKDATE).append(" as ").append(MDDetail.CHECKDATE).append(" ,a.").append(MDDetail.BILLTYPE).append(" as ").append(MDDetail.BILLTYPE).append(" ,a.").append(MDDetail.INNERBUSNO).append(" as ").append(MDDetail.INNERBUSNO).append(" ,a.").append(
						MDDetail.INNERBUSDATE).append(" as ").append(MDDetail.INNERBUSDATE).append(" ,a.").append(MDDetail.BUSIRECONNO).append(" as ").append(MDDetail.BUSIRECONNO).append(" ,a.").append(MDDetail.PK_UNIT).append(" as ").append(MDDetail.PK_UNIT).append(" ,a.").append(MDDetail.PK_UNIT_V).append(" as ").append(MDDetail.PK_UNIT_V).append(" ,a.").append(MDDetail.PK_VOUCHERTYPEV).append(" as ").append(MDDetail.PK_VOUCHERTYPEV).append(" ,a.").append(MDDetail.YEARV)
				.append(" as ").append(MDDetail.YEARV).append(",a.").append(MDDetail.PERIODV).append(" as ").append(MDDetail.PERIODV).append(" ,a.").append(MDDetail.EXPLANATION).append(" ,a.").append(MDDetail.PK_ACCASOA).append(" as ").append(MDDetail.PK_ACCASOA).append(",a.").append(MDDetail.ASSID).append(" as ").append(MDDetail.ASSID).append(",a.").append(MDDetail.PK_CURRTYPE).append(" as ").append(
						MDDetail.PK_CURRTYPE).append("  ,a.").append(MDDetail.PRICE).append(" as ").append(MDDetail.PRICE).append(" ,a.").append(MDDetail.EXCRATE2).append(" as ").append(MDDetail.EXCRATE2).append(" ,a.").append(MDDetail.EXCRATE3).append(" as ").append(MDDetail.EXCRATE3).append(" ,a.").append(MDDetail.EXCRATE4).append(" as ").append(MDDetail.EXCRATE4).append(" ,a.").append(
						MDDetail.CHECKSTYLE).append(" as ").append(MDDetail.CHECKSTYLE).append(" ,a.").append(MDDetail.CHECKNO).append(" as ").append(MDDetail.CHECKNO).append(" ,a.").append(MDDetail.CHECKDATE).append(" as ").append(MDDetail.CHECKDATE).append(" ,a.").append(MDDetail.BILLTYPE).append(" as ").append(MDDetail.BILLTYPE).append(" ,a.").append(MDDetail.INNERBUSNO).append(" as ").append(
						MDDetail.INNERBUSNO).append(" ,a.").append(MDDetail.INNERBUSDATE).append(" as ").append(MDDetail.INNERBUSDATE).append(" ,a.").append(MDDetail.BUSIRECONNO).append(" as ").append(MDDetail.BUSIRECONNO).append(" ,a.").append(MDDetail.NETBANKFLAG).append(" as ").append(MDDetail.NETBANKFLAG).append(" ,").append(MDDetail.VERIFYDATE).append(" as ").append(MDDetail.VERIFYDATE).append(" ,").append(MDDetail.VERIFYNO).append(" as ").append(MDDetail.VERIFYNO).append(" ,").append(getDefSql()).append(" b.groupid as groupid ");
		
		//如果启用了欧盟报表则添加欧盟报表条件
		if(checkEURStart) {
			select.append(" , vat.").append(VatDetailVO.PK_VATCOUNTRY).append(" , vat.").append(VatDetailVO.PK_TAXCODE).append(" , vat.").append(VatDetailVO.PK_SUPPLIERVATCODE).append(" , vat.").append(VatDetailVO.PK_CLIENTVATCODE);
			select.append(" , vat.").append(VatDetailVO.BUSINESSCODE).append(" , vat.").append(VatDetailVO.MONEYAMOUNT).append(" , vat.").append(VatDetailVO.TAXAMOUNT);
			select.append(" ,vat.").append(VatDetailVO.PK_RECEIVECOUNTRY).append(" as ").append(VatDetailVO.PK_RECEIVECOUNTRY);
		}
		
		select.append(" from gl_rtdetail a inner join ").append(tempTable).append(" b on a.pk_voucher = b.pk_voucher");
        
        //左外关联欧盟分录表
        if(checkEURStart) {
        	select.append(" LEFT OUTER JOIN GL_VATDETAIL vat ON a.PK_DETAIL = vat.pk_detail ");
        	select.append(" and  vat.").append(VatDetailVO.VOUCHERKIND).append("='").append(VoucherkindEnum.TMPVOUCHER.getEnumValue().getValue()).append("' ");
        }
		
		return select.toString();
	}

	private List<VoucherVO> constructVouchers(ConcurrentHashMap<String, Vector<DetailVO>> detailsMap, ConcurrentHashMap<String, List<FipRelationInfoVO>> relationMap, ConcurrentHashMap<String, VoucherVO> vouchers, CombinschemeVO parent) {
		List<VoucherVO> vos = new ArrayList<VoucherVO>();
		if (null != detailsMap) {
			Set<String> keyset = detailsMap.keySet();
			if (keyset != null) {
				
				List<String> keyList = Arrays.asList(keyset.toArray(new String[0]));
				
				Collections.sort(keyList);
				
				Map<String,Set<String>> pk_accasoaMap = new HashMap<String,Set<String>>(); 
				//批量查询辅助核算，和科目信息
				Map<String,AssVO[]> assidMap = getAssid_AccasoaMap(detailsMap,pk_accasoaMap);
				
				//批量获取会计科目，按科目表版本查询
				Map<String,Map<String,AccountVO>> groupAccountMap = getAccountMap(vouchers,pk_accasoaMap);
				
				for (String groupid : keyList) {
					VoucherVO vo = vouchers.get(groupid);
					Vector<DetailVO> details = detailsMap.get(groupid);
					if (null != details) {
						Map<String, AccountVO> accountMap = groupAccountMap.get(groupid);
						details = combinDetails(vo, details, parent,assidMap,accountMap);
					}
					vo.setDetail(details);
					if (vo.getNumDetails() > 0) {
						// 表头摘要
						if (vo.getDetail(0) != null)
							vo.setExplanation(vo.getDetail(0).getExplanation());
						// 数量调整凭证标志
						boolean isQuantityAdjust = false;
						// 处理合计金额和行号
						UFDouble td = UFDouble.ZERO_DBL;
						UFDouble tc = UFDouble.ZERO_DBL;
						for (int i = 0; i < vo.getNumDetails(); i++) {
							if (!isQuantityAdjust) {
								isQuantityAdjust = UFDouble.ZERO_DBL.equals(vo.getDetail(i).getLocaldebitamount().add(vo.getDetail(i).getLocalcreditamount())) && !UFDouble.ZERO_DBL.equals(vo.getDetail(i).getDebitquantity().add(vo.getDetail(i).getCreditquantity()));
							}
							td = td.add(vo.getDetail(i).getLocaldebitamount());
							tc = tc.add(vo.getDetail(i).getLocalcreditamount());
							vo.getDetail(i).setDetailindex(i + 1);
							
							vo.getDetail(i).setPrepareddate(vo.getPrepareddate());
							
							DetailVO detailVo = vo.getDetail(i);
							
							//设置欧盟报表信息
							VatDetailVO vatDetailVo = detailVo.getVatdetail();
							
							if(vatDetailVo != null) {
								UFDouble taxAmount = null;
								if (detailVo.getLocaldebitamount() != null && !UFDouble.ZERO_DBL.equals(detailVo.getLocaldebitamount())) {
									// 发生方向为借方
									vatDetailVo.setDirection(DirectionEnum.DEBIT.getEnumValue().getValue());
									taxAmount = detailVo.getLocaldebitamount();
								} else {
									vatDetailVo.setDirection(DirectionEnum.CREDIT.getEnumValue().getValue());
									taxAmount = detailVo.getLocalcreditamount();
								}
								
								vatDetailVo.setTaxamount(taxAmount);								
								vatDetailVo.setPrepareddate(vo.getPrepareddate());
								
							}
							
							if(vo.getDetail(i).getCashFlow() != null && vo.getDetail(i).getCashFlow().length > 0){
								// hurh 通过单据携带现金流量，这个标志必须设置，否则在启用自动分析参数时，现金流量会因自动分析被覆盖
								vo.setHasCashflowModified(true);
							}
						}
						if (isQuantityAdjust)
							vo.setVoucherkind(3);
						vo.setTotalcredit(td);
						vo.setTotaldebit(tc);
					}
					vos.add(vo);
				}
			}
		}
		return vos;
	}
	
	private Map<String,Map<String,AccountVO>> getAccountMap(ConcurrentHashMap<String, VoucherVO> voucherMap,Map<String,Set<String>> pk_accasoaMap) {
		
		Set<String> keySet = voucherMap.keySet();
		String[] keys = keySet.toArray(new String[0]);		
		String pk_accountingbook = voucherMap.get(keys[0]).getPk_accountingbook();
		
		Map<String,Map<String,AccountVO>> rtMap = new HashMap<String, Map<String,AccountVO>>();
		
		try {
			String[] chartVersions = getAccChartService().getAccchartVersionByPkaccountingbook(pk_accountingbook);
			
			// 对版本按照降序排序，方便转化时使用
			Arrays.sort(chartVersions, new Comparator<String>(){

				@Override
				public int compare(String o1, String o2) {
					return o2.compareTo(o1);
				}

			});
			
			//用于存储科目版本对应的groupid 容器
			Map<String,List<String>> versionGroupMap = new HashMap<String, List<String>>();
			for(String groupKey:keySet) {
				VoucherVO voucherVo = voucherMap.get(groupKey);
				UFDate prepareddate = voucherVo.getPrepareddate();
				String stddate = prepareddate.toStdString();
				
				//所属版本
				String ownVersion = null;				
				for(String chartVersion : chartVersions){
					if(stddate.compareTo(chartVersion) >= 0){ // 遇到第一个比stddate小的，就是stddate对应的最新版本
						ownVersion = chartVersion;
						break;
					}
				}
				
				if(versionGroupMap.containsKey(ownVersion)) {
					List<String> list = versionGroupMap.get(ownVersion);
					list.add(groupKey);
				}else {
					List<String> list = new ArrayList<String>();
					list.add(groupKey);
					versionGroupMap.put(ownVersion, list);
				}
			}
			
			//循环所有科目版本，进行批量查询
			Set<String> versionSet = versionGroupMap.keySet();
			for(String charVersion:versionSet) {
				List<String> groupList = versionGroupMap.get(charVersion);
				Set<String> pk_accasoaSet = new HashSet<String>();
				for(String groupId:groupList) {
					if(pk_accasoaMap.get(groupId)!=null)
					pk_accasoaSet.addAll(pk_accasoaMap.get(groupId));
				}
				AccountVO[] accvos = AccountUtilGL.queryAccountVosByPks(pk_accasoaSet.toArray(new String[0]), charVersion, false);
				Map<String,AccountVO> accountMap = new HashMap<String,AccountVO>();
				for(AccountVO accountVo:accvos) {
					accountMap.put(accountVo.getPk_accasoa(), accountVo);
				}
				
				for(String groupId:groupList) {
					rtMap.put(groupId, accountMap);
				}
			}
		} catch (BusinessException e) {
			Logger.error(e);
		}
		
		return rtMap;
	}
	
	/**
	 *
	 * 科目表对外服务接口
	 * <p>修改记录：</p>
	 * @return
	 * @see
	 * @since V6.0
	 */
	/** 科目表对外服务接口 */
	private IAccChartPubService accChartService = null;
	
	private IAccChartPubService getAccChartService() {
		if(accChartService == null){
			accChartService = NCLocator.getInstance().lookup(IAccChartPubService.class);
		}
		return accChartService;
	}
	
	
	/**
	 * 批量处理辅助核算，将科目关系信息pk分组
	 * @param detailsMap
	 * @param pk_accasoaMap
	 * @return
	 */
	private Map<String,AssVO[]> getAssid_AccasoaMap(ConcurrentHashMap<String, Vector<DetailVO>> detailsMap,Map<String,Set<String>> pk_accasoaMap) {
		
		Set<String> assidSet = new HashSet<String>();
		if(detailsMap != null && detailsMap.size() >0) {
			Set<String> keySet = detailsMap.keySet();
			for(String key:keySet) {
				Vector<DetailVO> vector = detailsMap.get(key);
				for(DetailVO detailVo:vector) {
					if(detailVo == null)
						continue;
					if(StringUtils.isNotEmpty(detailVo.getAssid())) {
						assidSet.add(detailVo.getAssid());
					}
					//将会计科目信息分录
					if(pk_accasoaMap.containsKey(key)) {
						Set<String> set = pk_accasoaMap.get(key);
						set.add(detailVo.getPk_accasoa());
					}else {
						Set<String> set = new HashSet<String>();
						set.add(detailVo.getPk_accasoa());
						pk_accasoaMap.put(key, set);
					}
				}
			}
		}
		Map<String,AssVO[]> assMap = new HashMap<String,AssVO[]>();
		try{
			IFreevaluePub freevalue = (IFreevaluePub) NCLocator.getInstance().lookup(IFreevaluePub.class.getName());
			
			if(assidSet.size() >0) {
				GlAssVO[] glAssVOs = freevalue.queryAllByIDs(assidSet.toArray(new String[0]),null,Module.GL);
				if(glAssVOs != null && glAssVOs.length>0) {
					for(int i=0;i<glAssVOs.length;i++) {
						String assID = glAssVOs[i].getAssID();
						assMap.put(assID, glAssVOs[i].getAssVos());
					}
				}
			}
		}catch(Exception e) {
			Logger.error("补充辅助核算信息失败，但不影响实时凭证生成。", e);
		}
		return assMap;
	}

	private Vector<DetailVO> combinDetails(VoucherVO voucherVo, Vector<DetailVO> detaillist, CombinschemeVO parent,Map<String,AssVO[]> assidMap,Map<String, AccountVO> accountMap) {
		boolean is_sum_dire = false;
		if (parent != null && parent.getIscombindifdirection() != null) {
			is_sum_dire = parent.getIscombindifdirection().booleanValue();
		}
		
		UFDouble zero = UFDouble.ZERO_DBL;
		for (int i = detaillist.size() - 1; i >= 0; i--) {
			DetailVO detail = detaillist.elementAt(i);

			if ((!detail.getLocaldebitamount().equals(UFDouble.ZERO_DBL) || !detail.getGroupdebitamount().equals(UFDouble.ZERO_DBL) || !detail.getGlobaldebitamount().equals(UFDouble.ZERO_DBL)) 
					&& (!detail.getLocalcreditamount().equals(UFDouble.ZERO_DBL) || !detail.getGroupcreditamount().equals(UFDouble.ZERO_DBL) || !detail.getGlobalcreditamount().equals(UFDouble.ZERO_DBL))) {
				// 借方贷方都不为0
				if (is_sum_dire) {
					// 借贷方向不一致的时候合并，合并结果保证本币为正
					UFDouble amount = detail.getDebitamount().sub(detail.getCreditamount());
					UFDouble quantity = detail.getDebitquantity().sub(detail.getCreditquantity());
					UFDouble localamount = detail.getLocaldebitamount().sub(detail.getLocalcreditamount());
					UFDouble groupdebitamount = detail.getGroupdebitamount().sub(detail.getGroupcreditamount());
					UFDouble globaldebitamount = detail.getGlobaldebitamount().sub(detail.getGlobalcreditamount());
					if (localamount.compareTo(zero) > 0 || groupdebitamount.compareTo(zero) > 0 || globaldebitamount.compareTo(zero) > 0) {
						// 余额在借方
						detaillist.elementAt(i).setDebitquantity(quantity);
						detaillist.elementAt(i).setDebitamount(amount);
						detaillist.elementAt(i).setLocaldebitamount(localamount);
						detaillist.elementAt(i).setGroupdebitamount(groupdebitamount);
						detaillist.elementAt(i).setGlobaldebitamount(globaldebitamount);
						detaillist.elementAt(i).setCreditquantity(zero);
						detaillist.elementAt(i).setCreditamount(zero);
						detaillist.elementAt(i).setLocalcreditamount(zero);
						detaillist.elementAt(i).setGroupcreditamount(zero);
						detaillist.elementAt(i).setGlobalcreditamount(zero);
					} else if (localamount.compareTo(zero) < 0 || groupdebitamount.compareTo(zero) < 0 || globaldebitamount.compareTo(zero) < 0) {
						// 余额在贷方
						detaillist.elementAt(i).setDebitquantity(zero);
						detaillist.elementAt(i).setDebitamount(zero);
						detaillist.elementAt(i).setLocaldebitamount(zero);
						detaillist.elementAt(i).setGroupdebitamount(zero);
						detaillist.elementAt(i).setGlobaldebitamount(zero);
						detaillist.elementAt(i).setCreditquantity(quantity.multiply(-1));
						detaillist.elementAt(i).setCreditamount(amount.multiply(-1));
						detaillist.elementAt(i).setLocalcreditamount(localamount.multiply(-1));
						detaillist.elementAt(i).setGroupcreditamount(groupdebitamount.multiply(-1));
						detaillist.elementAt(i).setGlobalcreditamount(globaldebitamount.multiply(-1));
					} else {
						// 借贷相抵，判断有无数量，有就是数量核算分录，没有删除
						if (quantity.compareTo(zero) == 0)
							detaillist.remove(i);
						else {
							if (quantity.compareTo(zero) > 0) {
								// 借方
								detaillist.elementAt(i).setDebitquantity(quantity);
								detaillist.elementAt(i).setDebitamount(amount);
								detaillist.elementAt(i).setLocaldebitamount(localamount);
								detaillist.elementAt(i).setGroupdebitamount(groupdebitamount);
								detaillist.elementAt(i).setGlobaldebitamount(globaldebitamount);
								detaillist.elementAt(i).setCreditquantity(zero);
								detaillist.elementAt(i).setCreditamount(zero);
								detaillist.elementAt(i).setLocalcreditamount(zero);
								detaillist.elementAt(i).setGroupcreditamount(zero);
								detaillist.elementAt(i).setGlobalcreditamount(zero);
							} else {
								// 余额在贷方
								detaillist.elementAt(i).setDebitquantity(zero);
								detaillist.elementAt(i).setDebitamount(zero);
								detaillist.elementAt(i).setLocaldebitamount(zero);
								detaillist.elementAt(i).setGroupdebitamount(zero);
								detaillist.elementAt(i).setGlobaldebitamount(zero);
								detaillist.elementAt(i).setCreditquantity(quantity.multiply(-1));
								detaillist.elementAt(i).setCreditamount(amount.multiply(-1));
								detaillist.elementAt(i).setLocalcreditamount(localamount.multiply(-1));
								detaillist.elementAt(i).setGroupcreditamount(groupdebitamount.multiply(-1));
								detaillist.elementAt(i).setGlobalcreditamount(globaldebitamount.multiply(-1));
							}
						}
					}
				} else {
					// 借贷不合并，则拆分成2条分录
					DetailVO detail1 = (DetailVO) detail.clone();
					detaillist.elementAt(i).setCreditquantity(zero);
					detaillist.elementAt(i).setCreditamount(zero);
					detaillist.elementAt(i).setLocalcreditamount(zero);
					detaillist.elementAt(i).setGroupcreditamount(zero);
					detaillist.elementAt(i).setGlobalcreditamount(zero);
					detail1.setDebitquantity(zero);
					detail1.setDebitamount(zero);
					detail1.setLocaldebitamount(zero);
					detail1.setGroupdebitamount(zero);
					detail1.setGlobaldebitamount(zero);
					detaillist.addElement(detail1);
				}
			} else {
				// 借方贷方至少有一个为0
				if ((detail.getLocaldebitamount().equals(UFDouble.ZERO_DBL) && detail.getGroupdebitamount().equals(UFDouble.ZERO_DBL) && detail.getGlobaldebitamount().equals(UFDouble.ZERO_DBL)) 
						&& (detail.getLocalcreditamount().equals(UFDouble.ZERO_DBL) && detail.getGroupcreditamount().equals(UFDouble.ZERO_DBL) && detail.getGlobalcreditamount().equals(UFDouble.ZERO_DBL))) {
					if (detail.getDebitquantity().equals(UFDouble.ZERO_DBL) && detail.getCreditquantity().equals(UFDouble.ZERO_DBL)) {
						// 借贷金额，数量都为0的分录删除
						//如果有VAT信息，则需要判断vat计税金额是否为空
						VatDetailVO vatdetail = detail.getVatdetail();
						if(vatdetail != null) {
							UFDouble moneyamount = vatdetail.getMoneyamount();
							if(moneyamount == null || UFDouble.ZERO_DBL.equals(moneyamount)) {
								detaillist.remove(i);
							}
						}else  {
							detaillist.remove(i);
						}
					} else {
						// 数量调整凭证
					}
				}
			}
		}
		if (detaillist.size() > 0) {

			// 判断数量核算
			for (int i = detaillist.size() - 1; i >= 0; i--) {
				AccountVO accvo = accountMap.get(detaillist.elementAt(i).getPk_accasoa());
				if (accvo != null) {
					if (accvo.getUnit() == null) {
						// 非数量核算科目，清空数量和单价
						detaillist.elementAt(i).setDebitquantity(zero);
						detaillist.elementAt(i).setCreditquantity(zero);
						detaillist.elementAt(i).setPrice(zero);
						// 如果清理完数量核算以后，借贷金额都为0，则删除该分录
						if (detaillist.elementAt(i).getLocaldebitamount().equals(UFDouble.ZERO_DBL) && detaillist.elementAt(i).getGroupdebitamount().equals(UFDouble.ZERO_DBL) && detaillist.elementAt(i).getGlobaldebitamount().equals(UFDouble.ZERO_DBL)
								&& detaillist.elementAt(i).getLocalcreditamount().equals(UFDouble.ZERO_DBL) && detaillist.elementAt(i).getGroupcreditamount().equals(UFDouble.ZERO_DBL) && detaillist.elementAt(i).getGlobalcreditamount().equals(UFDouble.ZERO_DBL)) {
							VatDetailVO vatdetail = detaillist.elementAt(i).getVatdetail();
							if(vatdetail != null) {
								UFDouble moneyamount = vatdetail.getMoneyamount();
								if(moneyamount == null || UFDouble.ZERO_DBL.equals(moneyamount)) {
									detaillist.remove(i);
								}
							}else {
								detaillist.remove(i);
							}
						}
					} else {
						// 数量核算科目，计算单价
						if (!UFDouble.ZERO_DBL.equals(detaillist.elementAt(i).getDebitquantity())) {
							detaillist.elementAt(i).setPrice(detaillist.elementAt(i).getDebitamount().div(detaillist.elementAt(i).getDebitquantity()));
						} else if (!UFDouble.ZERO_DBL.equals(detaillist.elementAt(i).getCreditquantity())) {
							detaillist.elementAt(i).setPrice(detaillist.elementAt(i).getCreditamount().div(detaillist.elementAt(i).getCreditquantity()));
						} else {
							detaillist.elementAt(i).setPrice(zero);
						}
					}
				}
			}

			if (detaillist.size() > 0) {
				String orderbyno = SchemeConst.ORDERTYPE_NO;
				if (parent != null) {
					orderbyno = parent.getDetailorder();
				}
				// 排序
				if (orderbyno != null && !orderbyno.equals(SchemeConst.ORDERTYPE_NO)) {
					Vector<DetailVO> creditVec = new Vector<DetailVO>();
					Vector<DetailVO> debitVec = new Vector<DetailVO>();
					boolean orderbyCode = false;
					if (parent != null && parent.getIsorderbycode() != null) {
						orderbyCode = parent.getIsorderbycode().booleanValue();
					}
					if (orderbyCode) {
						// 按科目编码排序
						for (int i = 0; i < detaillist.size(); i++) {
							AccountVO accvo = accountMap.get(detaillist.elementAt(i).getPk_accasoa());
							if (accvo != null) {
								detaillist.elementAt(i).setAccsubjcode(accvo.getCode());
							}
						}
						Collections.sort(detaillist, new DetailVOComparator());
					}
					for (int i = 0; i < detaillist.size(); i++) {
						DetailVO tmpVo = (DetailVO) detaillist.get(i);
						if (tmpVo.getDirection()) {
							// 借方：
							// if (mergeHeadVo.getSortBySubjFlag().booleanValue()) {
							// int index = findInsertPosition(debitVec, tmpVo);
							// debitVec.insertElementAt(tmpVo, index);
							// } else {
							debitVec.addElement(tmpVo);
							// }
						} else {
							// 贷方：
							// if (mergeHeadVo.getSortBySubjFlag().booleanValue()) {
							// int index = findInsertPosition(creditVec, tmpVo);
							// creditVec.insertElementAt(tmpVo, index);
							// } else {
							creditVec.addElement(tmpVo);
							// }
						}
					}
					detaillist.clear();
					if (orderbyno.equals(SchemeConst.ORDERTYPE_C)) {
						detaillist.addAll(creditVec);
						detaillist.addAll(debitVec);
					} else {
						detaillist.addAll(debitVec);
						detaillist.addAll(creditVec);
					}
				}
			}
		}
		if (detaillist.size() > 0) {
			// 辅助核算
			for (int i = 0; i < detaillist.size(); i++) {
				if (detaillist.get(i).getAssid() != null
						&& detaillist.get(i).getAssid().trim().length() > 0
						&& detaillist.get(i).getAss() == null) {
					AssVO[] assVOs = assidMap.get(detaillist.get(i).getAssid());
					detaillist.get(i).setAss(assVOs);
				}
			}
		}
		return detaillist;
	}

	private ConcurrentHashMap<String, Vector<DetailVO>> getGroupMapFromResultSet(ResultSet rs,boolean checkEURStart) throws BusinessException{
		ConcurrentHashMap<String, Vector<DetailVO>> detailsMap = new ConcurrentHashMap<String, Vector<DetailVO>>();
		try {
			while (rs.next()) {
				DetailVO detailVO = new DetailVO();
				detailVO.setPk_org(rs.getString("pk_org"));
				detailVO.setPk_detail(rs.getString("pk_detail"));
				detailVO.setBankaccount(rs.getString("bankaccount")); // hurh
				detailVO.setModifyflag(rs.getString("modifyflag"));
				detailVO.setRecieptclass(rs.getString("recieptclass"));
				BigDecimal debitamount = rs.getBigDecimal("debitamount");
				detailVO.setDebitamount(debitamount == null ? null : new UFDouble(debitamount));
				BigDecimal debitquantity = rs.getBigDecimal("debitquantity");
				detailVO.setDebitquantity(debitquantity == null ? null : new UFDouble(debitquantity));
				BigDecimal localdebitamount = rs.getBigDecimal("localdebitamount");
				detailVO.setLocaldebitamount(localdebitamount == null ? null : new UFDouble(localdebitamount));
				BigDecimal groupdebitamount = rs.getBigDecimal("groupdebitamount");
				detailVO.setGroupdebitamount(groupdebitamount == null ? null : new UFDouble(groupdebitamount));
				BigDecimal globaldebitamount = rs.getBigDecimal("globaldebitamount");
				detailVO.setGlobaldebitamount(globaldebitamount == null ? null : new UFDouble(globaldebitamount));
				BigDecimal creditamount = rs.getBigDecimal("creditamount");
				detailVO.setCreditamount(creditamount == null ? null : new UFDouble(creditamount));
				BigDecimal creditquantity = rs.getBigDecimal("creditquantity");
				detailVO.setCreditquantity(creditquantity == null ? null : new UFDouble(creditquantity));
				BigDecimal localcreditamount = rs.getBigDecimal("localcreditamount");
				detailVO.setLocalcreditamount(localcreditamount == null ? null : new UFDouble(localcreditamount));
				BigDecimal groupcreditamount = rs.getBigDecimal("groupcreditamount");
				detailVO.setGroupcreditamount(groupcreditamount == null ? null : new UFDouble(groupcreditamount));
				BigDecimal globalcreditamount = rs.getBigDecimal("globalcreditamount");
				detailVO.setGlobalcreditamount(globalcreditamount == null ? null : new UFDouble(globalcreditamount));
				detailVO.setPk_accountingbook(rs.getString("pk_accountingbook"));
				detailVO.setPk_unit(rs.getString("pk_unit"));
				detailVO.setPk_unit_v(rs.getString("pk_unit_v"));
				detailVO.setPk_vouchertype(rs.getString("pk_vouchertypev"));
				detailVO.setYear(rs.getString("yearv"));
				detailVO.setPeriod(rs.getString("periodv"));
				detailVO.setExplanation(rs.getString("explanation"));
				detailVO.setPk_accasoa(rs.getString("pk_accasoa"));
				detailVO.setAssid(rs.getString("assid"));
				detailVO.setPk_currtype(rs.getString("pk_currtype"));
				BigDecimal price = rs.getBigDecimal("price");
				detailVO.setPrice(price == null ? null : new UFDouble(price));
				BigDecimal excrate2 = rs.getBigDecimal("excrate2");
				detailVO.setExcrate2(excrate2 == null ? null : new UFDouble(excrate2));
				BigDecimal excrate3 = rs.getBigDecimal("excrate3");
				detailVO.setExcrate3(excrate3 == null ? null : new UFDouble(excrate3));
				BigDecimal excrate4 = rs.getBigDecimal("excrate4");
				detailVO.setExcrate4(excrate4 == null ? null : new UFDouble(excrate4));
				detailVO.setCheckstyle(rs.getString("checkstyle"));
				detailVO.setCheckno(rs.getString("checkno"));
				String checkdate = rs.getString("checkdate");
				//业务日期 V6.3
				detailVO.setVerifydate(rs.getString("verifydate"));
				detailVO.setVerifyno(rs.getString("verifyno"));
				
				detailVO.setCheckdate(checkdate == null ? null : new UFDate(checkdate));
				detailVO.setBilltype(rs.getString("billtype"));
				detailVO.setInnerbusno(rs.getString("innerbusno"));
				detailVO.setInnerbusdate(rs.getString("innerbusdate"));
				detailVO.setBusireconno(rs.getString("busireconno"));
				detailVO.setNetbankflag(rs.getString("netbankflag"));
				detailVO.setFreevalue1(rs.getString("freevalue1"));
				detailVO.setFreevalue2(rs.getString("freevalue2"));
				detailVO.setFreevalue3(rs.getString("freevalue3"));
				detailVO.setFreevalue4(rs.getString("freevalue4"));
				detailVO.setFreevalue5(rs.getString("freevalue5"));
				detailVO.setFreevalue6(rs.getString("freevalue6"));
				detailVO.setFreevalue7(rs.getString("freevalue7"));
				detailVO.setFreevalue8(rs.getString("freevalue8"));
				detailVO.setFreevalue9(rs.getString("freevalue9"));
				detailVO.setFreevalue10(rs.getString("freevalue10"));
				detailVO.setFreevalue11(rs.getString("freevalue11"));
				detailVO.setFreevalue12(rs.getString("freevalue12"));
				detailVO.setFreevalue13(rs.getString("freevalue13"));
				detailVO.setFreevalue14(rs.getString("freevalue14"));
				detailVO.setFreevalue15(rs.getString("freevalue15"));
				detailVO.setFreevalue16(rs.getString("freevalue16"));
				detailVO.setFreevalue17(rs.getString("freevalue17"));
				detailVO.setFreevalue18(rs.getString("freevalue18"));
				detailVO.setFreevalue19(rs.getString("freevalue19"));
				detailVO.setFreevalue20(rs.getString("freevalue20"));
				detailVO.setFreevalue21(rs.getString("freevalue21"));
				detailVO.setFreevalue22(rs.getString("freevalue22"));
				detailVO.setFreevalue23(rs.getString("freevalue23"));
				detailVO.setFreevalue24(rs.getString("freevalue24"));
				detailVO.setFreevalue25(rs.getString("freevalue25"));
				detailVO.setFreevalue26(rs.getString("freevalue26"));
				detailVO.setFreevalue27(rs.getString("freevalue27"));
				detailVO.setFreevalue28(rs.getString("freevalue28"));
				detailVO.setFreevalue29(rs.getString("freevalue29"));
				detailVO.setFreevalue30(rs.getString("freevalue30"));
				//如果启用了欧盟产品
				if(checkEURStart) {
					VatDetailVO vatDetailVo = getVatDetailVo(rs);
					detailVO.setVatdetail(vatDetailVo);
				}
				
				String groupid = rs.getString("groupid");
				detailVO.setUserData(groupid);
				Vector<DetailVO> vector = detailsMap.get(groupid);
				if (null == vector) {
					vector = new Vector<DetailVO>();
					detailsMap.put(groupid, vector);
				}
				vector.add(detailVO);
			}
		} catch (SQLException e1) {
			Logger.error(e1.getMessage(), e1);
			throw new BusinessException(e1.getMessage());
		}
		return detailsMap;
	}
	
	private VatDetailVO getVatDetailVo(ResultSet rs) throws SQLException {
		
		boolean checkNull = false;
		
		String pk_vatcountry = rs.getString(VatDetailVO.PK_VATCOUNTRY);	
		String pk_taxcode = rs.getString(VatDetailVO.PK_TAXCODE);
		String pk_suppliervatcode = rs.getString(VatDetailVO.PK_SUPPLIERVATCODE);
		String pk_clientvatcode = rs.getString(VatDetailVO.PK_CLIENTVATCODE);
		String businesscode = rs.getString(VatDetailVO.BUSINESSCODE);
		String pk_receivecountry = rs.getString(VatDetailVO.PK_RECEIVECOUNTRY);
		
		if(StringUtils.isNotEmpty(pk_vatcountry)) {
			checkNull = true;
		}else if(StringUtils.isNotEmpty(pk_taxcode)) {
			checkNull = true;
		}else if(StringUtils.isNotEmpty(pk_suppliervatcode)) {
			checkNull = true;
		}else if(StringUtils.isNotEmpty(pk_clientvatcode)) {
			checkNull = true;
		}else if(StringUtils.isNotEmpty(businesscode)) {
			checkNull = true;
		}else if(StringUtils.isNotEmpty(pk_receivecountry)) {
			checkNull = true;
		}
		
		if(checkNull) {
			BigDecimal moneyAmount = rs.getBigDecimal(VatDetailVO.MONEYAMOUNT);
			UFDouble moneyamountDouble = moneyAmount == null?null:new UFDouble(moneyAmount);
			BigDecimal taxAmount = rs.getBigDecimal(VatDetailVO.TAXAMOUNT);
			UFDouble taxAmountDouble = taxAmount == null ? null : new UFDouble(taxAmount);
			
			VatDetailVO vatDetailVo = new VatDetailVO();
			vatDetailVo.setPk_vatcountry(pk_vatcountry);
			vatDetailVo.setPk_taxcode(pk_taxcode);
			vatDetailVo.setPk_suppliervatcode(pk_suppliervatcode);
			vatDetailVo.setPk_clientvatcode(pk_clientvatcode);
			vatDetailVo.setBusinesscode(businesscode);
			vatDetailVo.setMoneyamount(moneyamountDouble);
			vatDetailVo.setTaxamount(taxAmountDouble);
			vatDetailVo.setPk_receivecountry(pk_receivecountry);
			
			vatDetailVo.setPk_detail(rs.getString("pk_detail"));
			vatDetailVo.setExplanation(rs.getString("explanation"));
			vatDetailVo.setPk_accasoa(rs.getString("pk_accasoa"));
			vatDetailVo.setPk_accountingbook(rs.getString("pk_accountingbook"));
			vatDetailVo.setPk_org(rs.getString("pk_org"));
			vatDetailVo.setPk_unit(rs.getString("pk_unit"));
			vatDetailVo.setVoucherkind(VoucherkindEnum.VOUCHER.getEnumValue().getValue());
			
			return vatDetailVo;
		}
		return null;
	}

	public String[] getSumSql(String tempTable,boolean checkEURStart) {
		String[] rst = new String[4];
		StringBuilder select = new StringBuilder();
		select.append("select ").append(" min(a.pk_detail) as ").append(MDDetail.PK_DETAIL).append(",")
				.append(" null as ")
				.append(MDDetail.NOV)
				.append(",")
				.append(" null as ")
				.append(MDDetail.OPPOSITESUBJ)
				.append(",")
				.append(" null as ")
				.append(MDDetail.PK_INNERORG)
				.append(",")
				// .append(" null as ").append(MDDetail.BANKACCOUNT).append(",")
				// hurh 银行账号是一个合并维度
				.append(MDDetail.BANKACCOUNT).append(",").append(" null as ").append(MDDetail.PK_VOUCHER).append(",").append(" min(a.").append(MDDetail.PK_ORG).append(") as ").append(MDDetail.PK_ORG).append(",").append(" min(").append(MDDetail.MODIFYFLAG).append(") as ").append(MDDetail.MODIFYFLAG).append(",").append(" min(").append(MDDetail.RECIEPTCLASS).append(") as ").append(
						MDDetail.RECIEPTCLASS).append(",").append(" sum(").append(MDDetail.DEBITAMOUNT).append(") as ").append(MDDetail.DEBITAMOUNT).append(",").append(" sum(").append(MDDetail.DEBITQUANTITY).append(") as ").append(MDDetail.DEBITQUANTITY).append(",").append(" sum(").append(MDDetail.LOCALDEBITAMOUNT).append(") as ").append(MDDetail.LOCALDEBITAMOUNT).append(",").append(" sum(")
				.append(MDDetail.GROUPDEBITAMOUNT).append(") as ").append(MDDetail.GROUPDEBITAMOUNT).append(",").append(" sum(").append(MDDetail.GLOBALDEBITAMOUNT).append(") as ").append(MDDetail.GLOBALDEBITAMOUNT).append(",").append(" sum(").append(MDDetail.CREDITAMOUNT).append(") as ").append(MDDetail.CREDITAMOUNT).append(",").append(" sum(").append(MDDetail.CREDITQUANTITY).append(") as ")
				.append(MDDetail.CREDITQUANTITY).append(",").append(" sum(").append(MDDetail.LOCALCREDITAMOUNT).append(") as ").append(MDDetail.LOCALCREDITAMOUNT).append(",").append(" sum(").append(MDDetail.GROUPCREDITAMOUNT).append(") as ").append(MDDetail.GROUPCREDITAMOUNT).append(",").append(" sum(").append(MDDetail.GLOBALCREDITAMOUNT).append(") as ").append(MDDetail.GLOBALCREDITAMOUNT)
				.append(",a.").append(MDDetail.PK_ACCOUNTINGBOOK).append(" as ").append(MDDetail.PK_ACCOUNTINGBOOK).append(",a.").append(MDDetail.PK_UNIT).append(" as ").append(MDDetail.PK_UNIT).append(",a.").append(MDDetail.PK_UNIT_V).append(" as ").append(MDDetail.PK_UNIT_V).append(",a.").append(MDDetail.PK_VOUCHERTYPEV).append(" as ").append(MDDetail.PK_VOUCHERTYPEV).append(" ,").append(MDDetail.YEARV).append(" as ").append(MDDetail.YEARV).append(",").append(MDDetail.PERIODV)
				.append(" as ").append(MDDetail.PERIODV).append(" ,a.").append(MDDetail.EXPLANATION +" as "+MDDetail.EXPLANATION).append(" ,a.").append(MDDetail.PK_ACCASOA).append(" as ").append(MDDetail.PK_ACCASOA).append(",").append(MDDetail.ASSID).append(" as ").append(MDDetail.ASSID).append(",").append(MDDetail.PK_CURRTYPE).append(" as ").append(MDDetail.PK_CURRTYPE).append("  ,").append(MDDetail.PRICE).append(" as ")
				.append(MDDetail.PRICE).append(" ,").append(MDDetail.EXCRATE2).append(" as ").append(MDDetail.EXCRATE2).append(" ,").append(MDDetail.EXCRATE3).append(" as ").append(MDDetail.EXCRATE3).append(" ,").append(MDDetail.EXCRATE4).append(" as ").append(MDDetail.EXCRATE4).append(" ,").append(MDDetail.CHECKSTYLE).append(" as ").append(MDDetail.CHECKSTYLE).append(" ,").append(
						MDDetail.CHECKNO).append(" as ").append(MDDetail.CHECKNO).append(" ,").append(MDDetail.CHECKDATE).append(" as ").append(MDDetail.CHECKDATE).append(" ,").append(MDDetail.BILLTYPE).append(" as ").append(MDDetail.BILLTYPE).append(" ,").append(MDDetail.INNERBUSNO).append(" as ").append(MDDetail.INNERBUSNO).append(" ,").append(MDDetail.INNERBUSDATE).append(" as ").append(
						MDDetail.INNERBUSDATE).append(" ,").append(MDDetail.BUSIRECONNO).append(" as ").append(MDDetail.BUSIRECONNO).append(" ,").append(MDDetail.NETBANKFLAG).append(" as ").append(MDDetail.NETBANKFLAG).append(" ,").append(MDDetail.VERIFYDATE).append(" as ").append(MDDetail.VERIFYDATE).append(" ,").append(MDDetail.VERIFYNO).append(" as ").append(MDDetail.VERIFYNO).append(" ,").append(getDefSql()).append(" b.groupid as groupid ");
						
				//如果启用了欧盟报表则添加欧盟报表条件
		        if(checkEURStart) {
		        	select.append(" , vat.").append(VatDetailVO.PK_VATCOUNTRY).append(" , vat.").append(VatDetailVO.PK_TAXCODE).append(" , vat.").append(VatDetailVO.PK_SUPPLIERVATCODE).append(" , vat.").append(VatDetailVO.PK_CLIENTVATCODE);
		        	select.append(" , vat.").append(VatDetailVO.BUSINESSCODE).append(" , sum(vat.").append(VatDetailVO.MONEYAMOUNT).append(") as "+VatDetailVO.MONEYAMOUNT+" , sum(vat.").append(VatDetailVO.TAXAMOUNT).append(") as "+VatDetailVO.TAXAMOUNT);
		        	
		        	select.append(" ,vat.").append(VatDetailVO.PK_RECEIVECOUNTRY).append(" as ").append(VatDetailVO.PK_RECEIVECOUNTRY);
		        	
		        }
						
		        select.append(" from gl_rtdetail a inner join ").append(tempTable).append(" b on a.pk_voucher = b.pk_voucher");
		        
		        //左外关联欧盟分录表
		        if(checkEURStart) {
		        	select.append(" LEFT OUTER JOIN GL_VATDETAIL vat ON a.PK_DETAIL = vat.pk_detail ");
		        	select.append(" and  vat.").append(VatDetailVO.VOUCHERKIND).append("='").append(VoucherkindEnum.TMPVOUCHER.getEnumValue().getValue()).append("' ");
		        }
		        
		// 构建查询条件
		StringBuilder where = new StringBuilder();
		where.append(" where ").append(" a.dr = 0 ");
		
		// 构建分组条件
		// 分组信息包含：摘要（条件选择）、科目、辅助核算、币种、单价、汇率2、汇率3、汇率4、
		// 结算方式、票据编号、票据日期、票据类型、内部交易号、内部交易日期、协同号、自定义项
		StringBuilder group = new StringBuilder();
		group.append(" group by b.groupid,a.pk_accountingbook,a.pk_unit,a.pk_unit_v,pk_vouchertypev,yearv,periodv,a.explanation,").append(" a.pk_accasoa, assid, pk_currtype, price, excrate2, excrate3,excrate4,bankaccount,checkstyle, checkno,").append(" checkdate, billtype, innerbusno, innerbusdate, busireconno,netbankflag, verifydate, verifyno, ").append(" freevalue1, freevalue2, freevalue3, freevalue4, freevalue5, freevalue6, ").append(
				" freevalue7, freevalue8, freevalue9, freevalue10, freevalue11, freevalue12,").append(" freevalue13, freevalue14, freevalue15, freevalue16, freevalue17, freevalue18,").append(" freevalue19, freevalue20, freevalue21, freevalue22, freevalue23, freevalue24, ").append(" freevalue25, freevalue26, freevalue27, freevalue28, freevalue29, freevalue30 ");
		
		//添加欧盟报表分组条件
		if(checkEURStart) {
			group.append(" , vat.").append(VatDetailVO.PK_VATCOUNTRY).append(" , vat.").append(VatDetailVO.PK_TAXCODE).append(" , vat.").append(VatDetailVO.PK_SUPPLIERVATCODE).append(" , vat.").append(VatDetailVO.PK_CLIENTVATCODE);
			group.append(" , vat.").append(VatDetailVO.BUSINESSCODE).append(" , vat.").append(VatDetailVO.PK_RECEIVECOUNTRY);
		}
		
		// 排序
		StringBuilder order = new StringBuilder();
		order.append(" order by groupid ");
		rst[0] = select.toString();
		rst[1] = where.toString();
		rst[2] = group.toString();
		rst[3] = order.toString();
		// rst.append(select).append(where).append(group).append(order);
		return rst;
	}

	// 组合自定义项
	private StringBuilder getDefSql() {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < 31; i++) {
			String def = "freevalue" + i;
			sb.append(def).append(" as ").append(def).append(" ,");
		}
		return sb;
	}

	private void prepareTempTable(String tempTable, ConcurrentHashMap<String, List<String>> groupMap) {
		String sql = "insert into " + tempTable + " (pk_voucher,groupid) values (?,?)";
		PreparedStatement stm = null;
		Connection conn = null;
		Collection<String> groupids = groupMap.keySet();
		try {
			conn = ConnectionFactory.getConnection();
			((CrossDBConnection) conn).setAddTimeStamp(false);
			stm = conn.prepareStatement(sql);
			for (String groupid : groupids) {
				Collection<String> keys = groupMap.get(groupid);
				for (String key : keys) {
					stm.setString(1, key);
					stm.setString(2, groupid);
					stm.addBatch();
				}
			}
			stm.executeBatch();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} finally {
			try {
				if (stm != null) {
					stm.close();
				}
			} catch (Exception e) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
			}
		}
	}

	private String createTempTable(Connection con) throws SQLException {
		String tempTable;
		String tableName = "tmp_gl_fipsum";
		String columns = "pk_voucher char(20),pk_detail char(20), groupid varchar(20)";
		tempTable = new TempTable().createTempTable(con, tableName, columns, null);
		return tempTable;
	}

	@Override
	public Collection<FipExtendAggVO> queryTempBillByRelations(Collection<FipRelationInfoVO> relationvos) throws BusinessException {
		ArrayList<FipExtendAggVO> rs = null;
		if (relationvos != null && !relationvos.isEmpty()) {
			ArrayList<String> pklist = new ArrayList<String>();
			if(relationvos != null){
				for (FipRelationInfoVO fipRelationInfoVO : relationvos) {
					pklist.add(fipRelationInfoVO.getRelationID());
				}
			}
			VoucherVO[] vos = new VoucherBO().queryRTByPks(pklist.toArray(new String[0]));
			if (vos != null && vos.length > 0) {
				rs = new ArrayList<FipExtendAggVO>();
				FipExtendAggVO tempvo = null;
				for (int i = 0; i < vos.length; i++) {
					tempvo = new FipExtendAggVO();
					// 这里应该转成MDVoucher返回
					tempvo.setBillVO(vos[i]);
					tempvo.setRelationID(vos[i].getPk_voucher());
					rs.add(tempvo);
				}
			}
		}
		return rs;
	}

	/**
	 * 合并分录
	 */
	private VoucherVO mergeDetailAndGetGLVo(VoucherVO rtHeadVo) throws Exception {
		// 借方总金额
		UFDouble totalDebitMny = UFDouble.ZERO_DBL;
		UFDouble groupdebitTotal = UFDouble.ZERO_DBL;
		UFDouble globaldebitTotal = UFDouble.ZERO_DBL;
		// 贷方总金额
		UFDouble totalCreditMny = UFDouble.ZERO_DBL;
		UFDouble groupcreditTotal = UFDouble.ZERO_DBL;
		UFDouble globalcreditTotal = UFDouble.ZERO_DBL;

		// 总账VO
		VoucherVO retVo = (VoucherVO) rtHeadVo.clone();
		retVo.setDetails(null);
		// 摘要
		String strRemark = null;
		// 控制条件
		String strControlFlag = rtHeadVo.getModifyflag();
		DetailVO[] mddetail = rtHeadVo.getDetails();
		DetailVO[] details = null;
		if (mddetail == null) {
			return null;
		}
		boolean isSumAccasoa = false;
		boolean isSumExplation = false;
		boolean needorder = true;
		boolean is_sum_dire = true;
		String combinType = "";
		Expaccount[] exps = null;
		List<String> exppks = null;
		CombinschemeVO parent = null;
		try {
			// 读取该模板的默认方案
			Aggcombinscheme scheme = null;
			if (pk_sumrule != null && !"".equals(pk_sumrule)) {
				scheme = SumRuleProxy.getQryService().queryByID(pk_sumrule);
			} else {
				scheme = SumRuleProxy.getQryService().queryByPkAccountingBook(rtHeadVo.getPk_accountingbook());
				
				if(scheme == null){
					SetOfBookVO setOfBookVO = AccountBookUtil.getSetOfBookVOByPk_accountingBook(rtHeadVo.getPk_accountingbook());
					if(setOfBookVO != null){
						scheme = SumRuleProxy.getQryService().queryByPkAccountingBook(setOfBookVO.getPk_setofbook());
					}
				}
				
				if (scheme == null || scheme.getParentVO() == null) {
					scheme = new Aggcombinscheme();
					CombinschemeVO schevo = new CombinschemeVO();
					schevo.setIscombinsameacc(UFBoolean.TRUE);
					schevo.setIscombindifexp(UFBoolean.TRUE);
					schevo.setDetailorder(SchemeConst.ORDERTYPE_NO);
					schevo.setCombintype(SchemeConst.COMBINTYPE_DC);
					schevo.setIscombindifdirection(UFBoolean.FALSE);
					scheme.setParentVO(schevo);
				}
			}
			parent = (CombinschemeVO) scheme.getParentVO();

			// 科目与辅助相同时合并
			isSumAccasoa = null != parent.getIscombinsameacc() && parent.getIscombinsameacc().booleanValue();
			// 摘要不同时合并
			isSumExplation = null != parent.getIscombindifexp() && parent.getIscombindifexp().booleanValue();
			// 合并方式
			combinType = parent.getCombintype();
			// 排序
			needorder = !parent.getDetailorder().equals(SchemeConst.ORDERTYPE_NO);
			// 借贷方向
			is_sum_dire = null != parent.getIscombindifdirection() && parent.getIscombindifdirection().booleanValue();

			// 例外科目
			exppks = new ArrayList<String>();
			exps = (Expaccount[]) scheme.getChildrenVO();
			if (null != exps && exps.length > 0) {
				for (Expaccount expaccount : exps) {
					exppks.add(expaccount.getPk_accasoa());
				}
			}
			
			if(exppks.size() > 0) {
				IBDData[] datas = AccountUtilGL.getDocByPksAndDate(exppks.toArray(new String[0]) ,rtHeadVo.getPrepareddate().toLocalString());
				Set<String> accCodes = new HashSet<String>();
				if(datas != null && datas.length >0) {
					for(IBDData data : datas){
						if(data!=null)
							accCodes.add(data.getCode());
					}
				}
				if(accCodes.size() >0) {
					datas = AccountUtilGL.getEndDocByCodesVersion(rtHeadVo.getPk_accountingbook(), accCodes.toArray(new String[0]),rtHeadVo.getPrepareddate().toLocalString());
					exppks.clear();
					if(datas != null && datas.length >0) {
						for(IBDData data : datas){
							if(data!=null)
								exppks.add(data.getPk());
						}
					}
				}
			}
			
			//检查欧盟报表是否启用
			boolean checkEURStart = GLStartCheckUtil.checkEURStart(rtHeadVo.getPk_group());
			
			//查询所有的报税国 
			CountryZoneVO[] countryVOs = NCLocator.getInstance().lookup(ICountryQryService.class).queryAll();
			Map<String,CountryZoneVO> countryMap = new HashMap<String, CountryZoneVO>();
			if(countryVOs != null && countryVOs.length>0) {
				for (CountryZoneVO countryZoneVO : countryVOs) {
					countryMap.put(countryZoneVO.getPk_country(), countryZoneVO);
				}
			}
			
			// 合并分录
			ArrayList result = new ArrayList();
			for (int i = 0; i < mddetail.length; i++) {
				DetailVO rtvb = mddetail[i];
				boolean hasMerged = false;
				for (int j = 0; j < result.size(); j++) {
					DetailVO detail = (DetailVO) result.get(j);
					
					if (GLVoucherMerge.canMerge(rtvb, detail, isSumAccasoa, is_sum_dire, exppks, combinType, isSumExplation,checkEURStart)) {
						hasMerged = true;
						// 分录号
						detail.setDetailindex(Integer.valueOf(detail.getDetailindex().intValue() + 1));
						// 借方数量
						detail.setDebitquantity(detail.getDebitquantity().add(rtvb.getDebitquantity() == null ? UFDouble.ZERO_DBL : rtvb.getDebitquantity()));
						// 原币借发生额
						detail.setDebitamount(detail.getDebitamount().add(rtvb.getDebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getDebitamount()));
						// 本币借发生额
						detail.setLocaldebitamount(detail.getLocaldebitamount().add(rtvb.getLocaldebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getLocaldebitamount()));
						// 集团本币借发生额
						detail.setGroupdebitamount(detail.getGroupdebitamount().add(rtvb.getGroupdebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getGroupdebitamount()));
						// 全局本币借发生额
						detail.setGlobaldebitamount(detail.getGlobaldebitamount().add(rtvb.getGlobaldebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getGlobaldebitamount()));
						// 贷方数量
						detail.setCreditquantity(detail.getCreditquantity().add(rtvb.getCreditquantity() == null ? UFDouble.ZERO_DBL : rtvb.getCreditquantity()));
						// 原币贷发生额
						detail.setCreditamount(detail.getCreditamount().add(rtvb.getCreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getCreditamount()));
						// 本币贷发生额
						detail.setLocalcreditamount(detail.getLocalcreditamount().add(rtvb.getLocalcreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getLocalcreditamount()));
						// 本币贷发生额
						detail.setGroupcreditamount(detail.getGroupcreditamount().add(rtvb.getGroupcreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getGroupcreditamount()));
						// 本币贷发生额
						detail.setGlobalcreditamount(detail.getGlobalcreditamount().add(rtvb.getGlobalcreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getGlobalcreditamount()));
						// 修改现金流量
						detail = GLVoucherMerge.mergeUserDataVO(detail, rtvb);
						
						//处理欧盟
						if(checkEURStart) {
							detail = GLVoucherMerge.mergeEurData(detail, rtvb);
						}
						break;
					}
				}
				if (!hasMerged) {
					// 将rtvb新增到result之中
					DetailVO detail = new DetailVO();

					// 公司编码
					detail.setPk_org(rtvb.getPk_org());
					// 分录号
					detail.setDetailindex(Integer.valueOf(0));
					// 科目编码
					detail.setPk_accasoa(rtvb.getPk_accasoa());
					// 辅助核算表识
					detail.setAssid(rtvb.getAssid());
					// 辅助核算组合
					detail.setAss(rtvb.getAss());
					// 摘要内容
					detail.setExplanation(rtvb.getExplanation());
					// 币种主键
					detail.setPk_currtype(rtvb.getPk_currtype());
					// 对方科目
					detail.setOppositesubj(null);
					// 单价
					detail.setPrice(UFDouble.ZERO_DBL);
					// 汇率1
					detail.setExcrate1(rtvb.getExcrate1());
					// 汇率2
					detail.setExcrate2(rtvb.getExcrate2());
					// 借方数量
					detail.setDebitquantity(rtvb.getDebitquantity() == null ? UFDouble.ZERO_DBL : rtvb.getDebitquantity());
					// 原币借发生额
					detail.setDebitamount(rtvb.getDebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getDebitamount());
					// 辅币借发生额
					detail.setFracdebitamount(rtvb.getFracdebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getFracdebitamount());
					// 本币借发生额
					detail.setLocaldebitamount(rtvb.getLocaldebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getLocaldebitamount());
					// 集团本币借发生额
					detail.setGroupdebitamount(rtvb.getGroupdebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getGroupdebitamount());
					// 全局本币借发生额
					detail.setGlobaldebitamount(rtvb.getGlobaldebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getGlobaldebitamount());
					// 贷方数量
					detail.setCreditquantity(rtvb.getCreditquantity() == null ? UFDouble.ZERO_DBL : rtvb.getCreditquantity());
					// 原币贷发生额
					detail.setCreditamount(rtvb.getCreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getCreditamount());
					// 辅币贷发生额
					detail.setFraccreditamount(rtvb.getFraccreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getFraccreditamount());
					// 本币贷发生额
					detail.setLocalcreditamount(rtvb.getLocalcreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getLocalcreditamount());
					// 本币贷发生额
					detail.setGroupcreditamount(rtvb.getGroupcreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getGroupcreditamount());
					// 本币贷发生额
					detail.setGlobalcreditamount(rtvb.getGlobalcreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getGlobalcreditamount());
					// 修改标志
					detail.setModifyflag(rtvb.getModifyflag());
					// 单据处理类
					detail.setRecieptclass(null);
					// 结算方式
					detail.setCheckstyle(rtvb.getCheckstyle());
					// 票据编号
					detail.setCheckno(rtvb.getCheckno());
					// 票据日期
					detail.setCheckdate(rtvb.getCheckdate() == null ? null : rtvb.getCheckdate());
					// 票据类型
					// detail.setFree3(rtvb.getFree3() == null ? null : rtvb.getFree3());
					// 帐户
					detail.setBankaccount(rtvb.getBankaccount());
					// 协同号
					detail.setBusireconno(rtvb.getBusireconno());
					// 业务关联号
					detail.setPk_sourcepk(rtvb.getPk_sourcepk());
					detail.setPk_org_v(rtvb.getPk_org_v());
					detail.setOtheruserdata(rtvb.getOtheruserdata());
					detail.setBilltype(rtvb.getBilltype());
					detail.setExcrate3(rtvb.getExcrate3());
					detail.setExcrate4(rtvb.getExcrate4());
					detail.setNetbankflag(rtvb.getNetbankflag());
					detail.setPk_system(rtvb.getPk_system());
					
					detail.setPk_unit_v(rtvb.getPk_unit_v());
					detail.setPk_unit(rtvb.getPk_unit());
					
					//v63 核销日期
					detail.setVerifyno(rtvb.getVerifyno());
					detail.setVerifydate(rtvb.getVerifydate());
					
					//如果启用了欧盟报表，需要重置交易代码
					if(checkEURStart) {
						resetVatBusiCode(countryMap,rtvb);
					}
					
					//欧盟信息
					detail.setVatdetail(rtvb.getVatdetail());
					/** ****************************新增的扩展选项****************************** */
					detail = createDetailExt(detail, rtvb);
					
					/** ************************************************************************ */
					result.add(detail);
				}
			}
			// 取科目
			HashMap<String, AccountVO> subjmap = new HashMap<String, AccountVO>();
			Vector<String> pk_subjs = new Vector<String>();
			// 合并分录的借贷方的数量、金额
			if (is_sum_dire) {
				for (int i = 0; i < result.size(); i++) {
					DetailVO mergedDetail = (DetailVO) result.get(i);
					pk_subjs.addElement(mergedDetail.getPk_accasoa());
					UFDouble debit = mergedDetail.getLocaldebitamount().add(mergedDetail.getLocaldebitamount());
					UFDouble groupdebit = mergedDetail.getGroupdebitamount().add(mergedDetail.getGroupdebitamount());
					UFDouble glbaldebit = mergedDetail.getGlobaldebitamount().add(mergedDetail.getGlobaldebitamount());
					UFDouble credit = mergedDetail.getLocalcreditamount().add(mergedDetail.getLocalcreditamount());
					UFDouble groupcredit = mergedDetail.getGroupcreditamount().add(mergedDetail.getGroupcreditamount());
					UFDouble glbalcredit = mergedDetail.getGroupcreditamount().add(mergedDetail.getGroupcreditamount());

					if (debit != null && credit != null) {
						// fgj2001-12-04
						if (debit.compareTo(credit) > 0) {
							if (!debit.equals(UFDouble.ZERO_DBL)) {
								// 借方数量
								mergedDetail.setDebitquantity(mergedDetail.getDebitquantity().sub(mergedDetail.getCreditquantity()));
								// 原币借发生额
								mergedDetail.setDebitamount(mergedDetail.getDebitamount().sub(mergedDetail.getCreditamount()));
								// 辅币借发生额
								mergedDetail.setFracdebitamount(mergedDetail.getFracdebitamount().sub(mergedDetail.getFraccreditamount()));
								// 本币借发生额
								mergedDetail.setLocaldebitamount(mergedDetail.getLocaldebitamount().sub(mergedDetail.getLocalcreditamount()));
								// 集团本币借发生额
								mergedDetail.setGroupdebitamount(mergedDetail.getGroupdebitamount().sub(mergedDetail.getGroupcreditamount()));
								// 全局本币借发生额
								mergedDetail.setGlobaldebitamount(mergedDetail.getGlobaldebitamount().sub(mergedDetail.getGlobalcreditamount()));
								// 贷方数量
								mergedDetail.setCreditquantity(UFDouble.ZERO_DBL);
								// 原币贷发生额
								mergedDetail.setCreditamount(UFDouble.ZERO_DBL);
								// 辅币贷发生额
								mergedDetail.setFraccreditamount(UFDouble.ZERO_DBL);
								// 本币贷发生额
								mergedDetail.setLocalcreditamount(UFDouble.ZERO_DBL);
								// 本币贷发生额
								mergedDetail.setGroupcreditamount(UFDouble.ZERO_DBL);
								// 本币贷发生额
								mergedDetail.setGlobalcreditamount(UFDouble.ZERO_DBL);

							}
						} else if (debit.compareTo(credit) < 0) {
							if (!credit.equals(UFDouble.ZERO_DBL)) {
								// 贷方数量
								mergedDetail.setCreditquantity(mergedDetail.getCreditquantity().sub(mergedDetail.getDebitquantity()));
								// 原币贷发生额
								mergedDetail.setCreditamount(mergedDetail.getCreditamount().sub(mergedDetail.getDebitamount()));
								// 辅币贷发生额
								mergedDetail.setFraccreditamount(mergedDetail.getFraccreditamount().sub(mergedDetail.getFracdebitamount()));
								// 本币贷发生额
								mergedDetail.setLocalcreditamount(mergedDetail.getLocalcreditamount().sub(mergedDetail.getLocaldebitamount()));
								// 本币贷发生额
								mergedDetail.setGroupcreditamount(mergedDetail.getGroupcreditamount().sub(mergedDetail.getGroupdebitamount()));
								// 本币贷发生额
								mergedDetail.setGlobalcreditamount(mergedDetail.getGlobalcreditamount().sub(mergedDetail.getGlobaldebitamount()));
								// 借方数量
								mergedDetail.setDebitquantity(UFDouble.ZERO_DBL);
								// 原币借发生额
								mergedDetail.setDebitamount(UFDouble.ZERO_DBL);
								// 辅币借发生额
								mergedDetail.setFracdebitamount(UFDouble.ZERO_DBL);
								// 本币借发生额
								mergedDetail.setLocaldebitamount(UFDouble.ZERO_DBL);
								// 集团本币借发生额
								mergedDetail.setGroupdebitamount(UFDouble.ZERO_DBL);
								// 全局本币借发生额
								mergedDetail.setGlobaldebitamount(UFDouble.ZERO_DBL);
							}
						} else {
							result.remove(i);
							i--;
						}
					}
				}
			} else {
				for (int i = 0; i < result.size(); i++) {
					DetailVO mergedDetail = (DetailVO) result.get(i);
					pk_subjs.addElement(mergedDetail.getPk_accasoa());
				}
			}
			try {
				if (pk_subjs.size() > 0) {
					String[] pks = new String[pk_subjs.size()];
					pk_subjs.copyInto(pks);
					AccountVO[] accvos = AccountUtilGL.queryAccountVosByPks(pks, rtHeadVo.getPrepareddate().toStdString(), false);
					for (int i = 0; i < accvos.length; i++) {
						subjmap.put(accvos[i].getPk_accasoa(), accvos[i]);
					}
				}
			} catch (Exception e) {
				Logger.error("取科目出错，不影响数据", e);
			}
			DetailVO[] resultVO = new DetailVO[result.size()];
			details = (DetailVO[]) result.toArray(resultVO);
			
			//将借贷分录分开存储，为了支持借方、贷方排序
			List<DetailVO> creditDetail = new ArrayList<DetailVO>();
			List<DetailVO> debitDetail = new ArrayList<DetailVO>();
			List<DetailVO> zeroDetail = new ArrayList<DetailVO>();
			
			// 加入分录号
			if (details != null && details.length != 0) {
				for (int i = 0; i < details.length; i++) {
					// 获取第一条摘要
					if (i == 0) {
						strRemark = details[i].getExplanation();
					}
					details[i].setDetailindex(Integer.valueOf(i + 1));
					// ********设置位数fgj2001-11-01
					details[i].setModifyflag(details[i].getModifyflag());
					// 判断数量核算
					boolean isQuantity = false;
					String pk_unit = subjmap.get(details[i].getPk_accasoa()).getUnit();
					if (pk_unit != null && !"".equals(pk_unit)) {
						isQuantity = true;
					}
					if (isQuantity) {
						// 借方数量
						details[i].setDebitquantity(details[i].getDebitquantity());
						// 贷方数量
						details[i].setCreditquantity(details[i].getCreditquantity());
						// 单价
						if (!details[i].getDebitquantity().equals(UFDouble.ZERO_DBL)) {
							details[i].setPrice(details[i].getDebitamount() == null ? UFDouble.ZERO_DBL : (details[i].getDebitamount().div(details[i].getDebitquantity())));
						} else if (!details[i].getCreditquantity().equals(UFDouble.ZERO_DBL)) {
							details[i].setPrice(details[i].getCreditamount() == null ? UFDouble.ZERO_DBL : (details[i].getCreditamount().div(details[i].getCreditquantity())));
						} else {
							details[i].setPrice(UFDouble.ZERO_DBL);
						}

					} else {
						// 借方数量
						details[i].setDebitquantity(UFDouble.ZERO_DBL);
						// 贷方数量
						details[i].setCreditquantity(UFDouble.ZERO_DBL);
						// 单价（非空，没有填“0”）
						details[i].setPrice(UFDouble.ZERO_DBL);
					}
					// 计算借贷方综合
					totalCreditMny = totalCreditMny.add(details[i].getLocalcreditamount());
					// 借方本币
					totalDebitMny = totalDebitMny.add(details[i].getLocaldebitamount());
					// 计算借贷方综合
					groupdebitTotal = groupdebitTotal.add(details[i].getGroupdebitamount());
					globaldebitTotal = globaldebitTotal.add(details[i].getGlobaldebitamount());
					// 贷方总金额
					groupcreditTotal = groupcreditTotal.add(details[i].getGroupcreditamount());
					globalcreditTotal = globalcreditTotal.add(details[i].getGlobalcreditamount());
					
					DetailVO detailVo = details[i];					
					detailVo.setPrepareddate(rtHeadVo.getPrepareddate());
					
					// 设置欧盟报表信息
					if (checkEURStart) {
						VatDetailVO vatDetailVo = detailVo.getVatdetail();

						if (vatDetailVo != null) {
							UFDouble taxAmount = null;
							if (detailVo.getLocaldebitamount() != null
									&& !UFDouble.ZERO_DBL.equals(detailVo
											.getLocaldebitamount())) {
								// 发生方向为借方
								vatDetailVo.setDirection(DirectionEnum.DEBIT
										.getEnumValue().getValue());
								taxAmount = detailVo.getLocaldebitamount();
							} else if(detailVo.getLocalcreditamount() != null && !UFDouble.ZERO_DBL.equals(detailVo.getLocalcreditamount())){
								vatDetailVo.setDirection(DirectionEnum.CREDIT
										.getEnumValue().getValue());
								taxAmount = detailVo.getLocalcreditamount();
							} else {//如果报税分录上值为0则取科目的方向    这种情况相当较少，有效率问题再改
									String pk_accasoa = detailVo.getPk_accasoa();
									UFDate prepareddate = detailVo.getPrepareddate();
									AccountVO[] accountVOs = NCLocator.getInstance().lookup(IAccountPubService.class).queryAccountVOsByPks(new String[]{pk_accasoa},prepareddate.toStdString());
									if(accountVOs != null && accountVOs.length>0) {
										Integer balanorient = accountVOs[0].getBalanorient();
										if(balanorient != null)
										vatDetailVo.setDirection(balanorient.toString());
									}
							}
							vatDetailVo.setTaxamount(taxAmount);
						}
					}
					
					if(details[i].getCashFlow() != null && details[i].getCashFlow().length > 0){
						// hurh 设置现金流量已分析标志，避免自动分析时覆盖
						retVo.setHasCashflowModified(true);
					}
					
					//排序
					if (needorder) {
						if (detailVo.getLocaldebitamount() != null
								&& !UFDouble.ZERO_DBL.equals(detailVo
										.getLocaldebitamount())) {
							debitDetail.add(detailVo);
						} else if (detailVo.getLocalcreditamount() != null
								&& !UFDouble.ZERO_DBL.equals(detailVo
										.getLocalcreditamount())) {
                            creditDetail.add(detailVo);
						}else {
							zeroDetail.add(detailVo);
						}
					}
				}
				if (totalCreditMny.doubleValue() > totalDebitMny.doubleValue()) {
					totalDebitMny = totalCreditMny;
				} else {
					totalCreditMny = totalDebitMny;
				}
			}
			
			//排序后汇总分录
			List<DetailVO> rtDetail = new ArrayList<DetailVO>();
			if (needorder) {
				if (parent.getDetailorder().equals(SchemeConst.ORDERTYPE_D)) {
					if (debitDetail != null && debitDetail.size() > 0) {
						rtDetail.addAll(debitDetail);
					}

					if (creditDetail != null && creditDetail.size() > 0) {
						rtDetail.addAll(creditDetail);
					}

					if (zeroDetail != null && zeroDetail.size() > 0) {
						rtDetail.addAll(zeroDetail);
					}

				} else if (parent.getDetailorder().equals(
						SchemeConst.ORDERTYPE_C)) {
					if (creditDetail != null && creditDetail.size() > 0) {
						rtDetail.addAll(creditDetail);
					}

					if (debitDetail != null && debitDetail.size() > 0) {
						rtDetail.addAll(debitDetail);
					}

					if (zeroDetail != null && zeroDetail.size() > 0) {
						rtDetail.addAll(zeroDetail);
					}
				} else {
					if (debitDetail != null && debitDetail.size() > 0) {
						rtDetail.addAll(debitDetail);
					}

					if (creditDetail != null && creditDetail.size() > 0) {
						rtDetail.addAll(creditDetail);
					}

					if (zeroDetail != null && zeroDetail.size() > 0) {
						rtDetail.addAll(zeroDetail);
					}
				}
				//重置分录号
				DetailVO[] detailVOs = rtDetail.toArray(new DetailVO[0]);
				if(detailVOs != null && detailVOs.length>0) {
					for (int i = 0; i < detailVOs.length; i++) {
						detailVOs[i].setDetailindex(Integer.valueOf(i+1));
					}
				}
				// 子表合并结束
				retVo.setDetails(detailVOs);
			}else {
				// 子表合并结束
				retVo.setDetails(details);
			}

			// 转换主表数据
			// 附单数
			retVo.setAttachment(rtHeadVo.getAttachment());

			retVo.setDiscardflag(nc.vo.pub.lang.UFBoolean.FALSE);
			// 修改标志
//			retVo.setDetailmodflag(strControlFlag == null ? UFBoolean.TRUE : UFBoolean.valueOf(strControlFlag.substring(0, 1)));
			retVo.setModifyflag(strControlFlag);

			// 凭证编号的处理（非空，不知道时填“0”，会在保存后自动生成。）
			retVo.setNo(Integer.valueOf(0));
			// 会计期间
			retVo.setPeriod(rtHeadVo.getPeriod());
			// 会计年度
			retVo.setYear(rtHeadVo.getYear());
			// 公司
			retVo.setPk_org(rtHeadVo.getPk_org());
			// 凭证类别
			retVo.setPk_vouchertype(rtHeadVo.getPk_vouchertype());
			// 凭证类型(总账要求0)
			retVo.setVoucherkind(Integer.valueOf(0));
			// 制单人
			retVo.setPk_prepared(rtHeadVo.getPk_prepared());
			// 值单日期
			retVo.setPrepareddate(rtHeadVo.getPrepareddate());
			// 系统
			retVo.setPk_system(rtHeadVo.getPk_system());
			// 目标系统
			retVo.setPk_system(rtHeadVo.getPk_system());
			// 摘要
			retVo.setExplanation(strRemark);
			// 2003-05-07增加签字标志

			retVo.setSignflag(nc.vo.pub.lang.UFBoolean.FALSE);
			retVo.setPk_org_v(rtHeadVo.getPk_org_v());
			retVo.setPk_setofbook(rtHeadVo.getPk_setofbook());
			retVo.setFree1(rtHeadVo.getFree1());
			retVo.setFree2(rtHeadVo.getFree2());
			retVo.setFree3(rtHeadVo.getFree3());
			retVo.setFree4(rtHeadVo.getFree4());
			retVo.setFree5(rtHeadVo.getFree5());
			retVo.setFree6(rtHeadVo.getFree6());
			retVo.setFree7(rtHeadVo.getFree7());
			retVo.setFree8(rtHeadVo.getFree8());
			retVo.setFree9(rtHeadVo.getFree9());
			retVo.setFree10(rtHeadVo.getFree10());
			retVo = GLVoucherMerge.dealGLBalance(retVo);
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
			throw new Exception(ex.getMessage(), ex);

		}
		return retVo;
	}
	
	/**
	 * //交易代码 如果收货国、发货国同不在欧盟内 则置空，否则如果有一个在欧盟内，如果不是三角贸易，把交易代码置空 
	 * @param countryMap
	 * @param detailVo
	 */
	private void resetVatBusiCode(Map<String,CountryZoneVO> countryMap,DetailVO detailVo) {
		if(detailVo != null) {
			VatDetailVO vatDetailVo = detailVo.getVatdetail();
			if(vatDetailVo != null) {
				String pk_receivecountry = vatDetailVo.getPk_receivecountry();
				String sendcountryid = vatDetailVo.getSendcountryid();
				if(pk_receivecountry != null && sendcountryid != null) {
					CountryZoneVO receiveVo = countryMap.get(pk_receivecountry);
					CountryZoneVO sendVo = countryMap.get(sendcountryid);
					if(receiveVo != null && sendVo != null) {
						
						if(CountryZoneExVO.getIseucountry(receiveVo).booleanValue()) {//收货国为欧盟
							if(CountryZoneExVO.getIseucountry(sendVo).booleanValue()) {//发货国为欧盟
								if (pk_receivecountry.equals(sendcountryid)) {// 如果发货国、收货国相同  则为D
									vatDetailVo.setBusinesscode(BusinessCodeEnum.D.getEnumValue().getValue());
								}
							}else {//发货国不为欧盟
								//如果不为三角贸易  则需要将交易代码清空
								String businesscode = vatDetailVo.getBusinesscode();
								if(businesscode != null && !BusinessCodeEnum.T.getEnumValue().getValue().equals(businesscode)) {
									vatDetailVo.setBusinesscode(null);
								}
							}
						}else {//收货国不为欧盟
                            if(CountryZoneExVO.getIseucountry(sendVo).booleanValue()) {//发货国为欧盟
                            	//如果不为三角贸易  则需要将交易代码清空
								String businesscode = vatDetailVo.getBusinesscode();
								if(businesscode != null && !BusinessCodeEnum.T.getEnumValue().getValue().equals(businesscode)) {
									vatDetailVo.setBusinesscode(null);
								}
							}else {//发货国不为欧盟
								vatDetailVo.setBusinesscode(null);
							}
						}
					}
				}
			}
		}
	}

	public DetailVO createDetailExt(DetailVO detail, DetailVO rtbv) throws Exception {

		detail.setFreevalue1(rtbv.getFreevalue1());
		detail.setFreevalue2(rtbv.getFreevalue2());
		detail.setFreevalue3(rtbv.getFreevalue3());
		detail.setFreevalue4(rtbv.getFreevalue4());
		detail.setFreevalue5(rtbv.getFreevalue5());
		detail.setFreevalue6(rtbv.getFreevalue6());
		detail.setFreevalue7(rtbv.getFreevalue7());
		detail.setFreevalue8(rtbv.getFreevalue8());
		detail.setFreevalue9(rtbv.getFreevalue9());
		detail.setFreevalue10(rtbv.getFreevalue10());
		detail.setFreevalue11(rtbv.getFreevalue11());
		detail.setFreevalue12(rtbv.getFreevalue12());
		detail.setFreevalue13(rtbv.getFreevalue13());
		detail.setFreevalue14(rtbv.getFreevalue14());
		detail.setFreevalue15(rtbv.getFreevalue15());
		detail.setFreevalue16(rtbv.getFreevalue16());
		detail.setFreevalue17(rtbv.getFreevalue17());
		detail.setFreevalue18(rtbv.getFreevalue18());
		detail.setFreevalue19(rtbv.getFreevalue19());
		detail.setFreevalue20(rtbv.getFreevalue20());
		detail.setFreevalue21(rtbv.getFreevalue21());
		detail.setFreevalue22(rtbv.getFreevalue22());
		detail.setFreevalue23(rtbv.getFreevalue23());
		detail.setFreevalue24(rtbv.getFreevalue24());
		detail.setFreevalue25(rtbv.getFreevalue25());
		detail.setFreevalue26(rtbv.getFreevalue26());
		detail.setFreevalue27(rtbv.getFreevalue27());
		detail.setFreevalue28(rtbv.getFreevalue28());
		detail.setFreevalue29(rtbv.getFreevalue29());
		detail.setFreevalue30(rtbv.getFreevalue30());
		detail.setFree7(rtbv.getFree7());
		detail.setFree8(rtbv.getFree8());
		detail.setFree9(rtbv.getFree9());
		detail.setFree10(rtbv.getFree10());

		return detail;
	}
}
