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
 * ƾ֤�������ƽ̨����ʵ����
 * 
 * @author zhaozh
 * 
 */
public class GLVoucherService4Fip implements IDesBillService, IBillReflectorExService, IDesBillSumService {

	private String pk_sumrule;

	private FipSaveResultVO confirmBill(FipSaveResultVO saveResultVO) throws BusinessException {
		// �ȼ�������Ƿ�����
		if(!GLStartCheckUtil.checkGLStart(saveResultVO.getMessageinfo().getPk_group())){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0048")/* @res "���˲�Ʒδ���ã�" */);
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
				// ���ƽ̨û���Ƿ�����������ƾ֤�ı�ǣ���ѯʵʱƾ֤
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

				// ɾ��ʵʱƾ֤
				VoucherBO voucherBO = new VoucherBO();
				voucherBO.deleteRtVouchers(rtVoucherPks.keySet().toArray(new String[0]));
				// ɾ������ƾ֤
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
	 * queryBillByRelations������IBillReflectorService�е�ʵ��
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
					// ����Ӧ��ת��MDVoucher����
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
			int groupIndex = 0;// �����
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
				// ������ʱ��
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
				
				//����Ƶ���Ϊ�գ�������Ϊ��ǰ������
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

	// �ϲ���ͷ����
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
	 * �ϲ���ͬ��Դϵͳ����
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
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0004")/* @res "��ԴϵͳΪ�գ�����" */);
				}
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0047")/* @res "��������Ϊ��Щ�ֶβ�ͬ���в��ܺϲ���һ��ƾ֤�����������������ֶ��Ƿ���ͬ�����ţ���֯����������˲���ƾ֤���" */);
			}
		}
		return vouchersMap;
	}

	private ConcurrentHashMap<String, String> getSystems(ConcurrentHashMap<String, List<String>> groupMap, String tempTable) throws BusinessException {
		// hurh 60 �����Ż���һ�β�ѯ
		List<String> allList = new LinkedList<String>();
		for (String groupid : groupMap.keySet()) {
			allList.addAll(groupMap.get(groupid));
		}
		ConcurrentHashMap<String, String> systemMap = querySystems(allList, tempTable);

		return systemMap;
	}

	/**
	 * hurh 60�����Ż�
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
		// ��Ŀ�븨����ͬʱ�ϲ�
		isSumAccasoa = null != parent.getIscombinsameacc() && parent.getIscombinsameacc().booleanValue();
		// ժҪ��ͬʱ�ϲ�
		isSumExplation = null != parent.getIscombindifexp() && parent.getIscombindifexp().booleanValue();
		
		// ���۲�ͬʱ�ϲ�
		isPriceExplation = null != parent.getIscombindifprice() && parent.getIscombindifprice().booleanValue();
		
		// �ϲ���ʽ
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
			//���ŷ�˱����Ƿ�����
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
				// �����Ŀ���ϲ�
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
				// �������Ŀ�ϲ�
				condition = whereSql;
				if (null != exppks && exppks.size() != 0) {
					String temp = SqlUtils.getInStr(" a.pk_accasoa", exppks, true);
					condition += " and " + temp.replaceFirst("in", "not in");
				}
				//�Ƿ����ϲ�  �������ϲ���Ҫ�ж�  ��������һ���Ƿ�ϲ�
				boolean isCOMBINTYPE_DC = false;
				
				if (combinType.equals(SchemeConst.COMBINTYPE_D)) {
					if(checkEURStart) {//���������ŷ�˱�������Ҫ�������ȫ����0�����ݲ�ѯ����
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
					//�������һ���򲻽��л��ܣ���Ҫ�ֱ𽫽跽�������ϲ�
					if(!is_sum_dire) {
						isCOMBINTYPE_DC = true;
					}else {//�������һ��  ���л���  ����ԭ���߼�
						isCOMBINTYPE_DC = false;
					}
				}
				
				//����ǽ跽�ϲ��������ϲ� ����ԭ���߼�
				if(!isCOMBINTYPE_DC) {
					String wherepart = sql;
					sql = selectSql + sql + groupSql + orderSql;
					ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
					mixDetail(sumlist, groupMapFromResultSet);
					StringBuffer fromcon = new StringBuffer();
					fromcon.append("from gl_rtdetail a inner join " + tempTable + " b on a.pk_voucher = b.pk_voucher ");
					
					//���������ŷ�˱����Ʒ
					if(checkEURStart) {
						fromcon.append(" LEFT OUTER JOIN GL_VATDETAIL vat ON a.PK_DETAIL = vat.pk_detail ");
					}
					
					catCashFlowCaseForSUM2(sumlist, groupSql.substring(6), fromcon.toString(), wherepart);
					detailsMap = merge(detailsMap, groupMapFromResultSet);
				}else {//����ǽ���ϲ�  ���ҽ������һ�� ���л���
					
					//�ϲ��跽
					sql = selectSql + condition + " and (debitquantity<>0 or localdebitamount<>0 or groupdebitamount<>0 or globaldebitamount<>0) " + groupSql + orderSql;
					
					ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
					mixDetail(sumlist, groupMapFromResultSet);
					detailsMap = merge(detailsMap, groupMapFromResultSet);
					
					//�ϲ�����
					sql = selectSql + condition + " and (creditquantity<>0 or localcreditamount<>0 or groupcreditamount<>0 or globalcreditamount<>0) " + groupSql + orderSql;

					groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
					mixDetail(sumlist, groupMapFromResultSet);
					detailsMap = merge(detailsMap, groupMapFromResultSet);
					
					if(checkEURStart) {
						//�ϲ������ȫ����0������
						sql = selectSql + condition + " and (creditquantity=0 and debitquantity=0 and localcreditamount=0 and localdebitamount=0) " + groupSql + orderSql;
						groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
						mixDetail(sumlist, groupMapFromResultSet);
						detailsMap = merge(detailsMap, groupMapFromResultSet);
					}
					
					StringBuffer fromcon = new StringBuffer();
					fromcon.append("from gl_rtdetail a inner join " + tempTable + " b on a.pk_voucher = b.pk_voucher ");
					
					//���������ŷ�˱����Ʒ
					if(checkEURStart) {
						fromcon.append(" LEFT OUTER JOIN GL_VATDETAIL vat ON a.PK_DETAIL = vat.pk_detail ");
					}
					catCashFlowCaseForSUM2(sumlist, groupSql.substring(6), fromcon.toString(), condition);
				}
			} else {
				String sql = "";
				String condition = sqls[1];
				if (exppks != null && exppks.size() != 0) {
					// �����Ŀ�ϲ�
					condition += " and " + SqlUtils.getInStr("a.pk_accasoa", exppks, true);
					
					//�Ƿ����ϲ�  �������ϲ���Ҫ�ж�  ��������һ���Ƿ�ϲ�
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
						//�������һ���򲻽��л��ܣ���Ҫ�ֱ𽫽跽�������ϲ�
						if(!is_sum_dire) {
							isCOMBINTYPE_DC = true;
						}else {//�������һ��  ���л���  ����ԭ���߼�
							isCOMBINTYPE_DC = false;
						}
					}
					
					//����ǽ跽�ϲ��������ϲ� ����ԭ���߼�
					if(!isCOMBINTYPE_DC) {
						sql = selectSql + whereSql + groupSql + orderSql;
						ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
						mixDetail(sumlist, groupMapFromResultSet);
						StringBuffer fromcon = new StringBuffer();
						fromcon.append("from gl_rtdetail a inner join " + tempTable + " b on a.pk_voucher = b.pk_voucher");
						//���������ŷ�˱����Ʒ
						if(checkEURStart) {
							fromcon.append(" LEFT OUTER JOIN GL_VATDETAIL vat ON a.PK_DETAIL = vat.pk_detail ");
						}
						catCashFlowCaseForSUM2(sumlist, groupSql.substring(6), fromcon.toString(), whereSql);
						detailsMap = merge(detailsMap, groupMapFromResultSet);
					}else {//����ǽ���ϲ�  ���ҽ������һ�� ���л���
						
						//�ϲ��跽
						sql = selectSql + condition + " and (debitquantity<>0 or localdebitamount<>0 or groupdebitamount<>0 or globaldebitamount<>0) " + groupSql + orderSql;
						
						ConcurrentHashMap<String, Vector<DetailVO>> groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
						mixDetail(sumlist, groupMapFromResultSet);
						detailsMap = merge(detailsMap, groupMapFromResultSet);
						
						//�ϲ�����
						sql = selectSql + condition + " and (creditquantity<>0 or localcreditamount<>0 or groupcreditamount<>0 or globalcreditamount<>0) " + groupSql + orderSql;

						groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
						mixDetail(sumlist, groupMapFromResultSet);
						detailsMap = merge(detailsMap, groupMapFromResultSet);
						
						if(checkEURStart) {
							//��Ҫ�������ȫ����0�����ݲ�ѯ����
							sql = selectSql + condition + " and (debitquantity=0 and creditquantity=0 and localdebitamount=0 and localcreditamount=0) " + groupSql + orderSql;

							groupMapFromResultSet = getGroupMapFromResultSet(stm.executeQuery(sql),checkEURStart);
							mixDetail(sumlist, groupMapFromResultSet);
							detailsMap = merge(detailsMap, groupMapFromResultSet);
						}
						
						StringBuffer fromcon = new StringBuffer();
						fromcon.append("from gl_rtdetail a inner join " + tempTable + " b on a.pk_voucher = b.pk_voucher ");
						
						//���������ŷ�˱����Ʒ
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
		// �����������ƾ֤����ô����ӿڶ�������CashflowcaseVO���п���û�б�����Ϣ����Ҫ����
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
					udvo.setDatatype("�ֽ�����"/*-=notranslate=-*/);
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
		//��Ϊ��ʱ����࣬�޸ĳɹ̶���ʱ��
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
			throw new SQLException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("fidap", "UPPfidap-000160")/* @res "��ѯ��¼��Ϣ�����쳣��" */+ ex.getMessage());
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
		// ����ֽ�������Ϣ
		String[] tableinfo = new String[] {
				tablename, pkfield, groupfield
		};
		CashflowcaseVO[] cashvo = null;
		try {
			ICashFlowCase cashflowproxy = (ICashFlowCase) NCLocator.getInstance().lookup(ICashFlowCase.class.getName());
			cashvo = cashflowproxy.querySumCashflow4Fip(tableinfo);
		} finally {
			// ��ʱ��ʹ�����Ҫ���٣���Ȼ������ɱ�й©
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
			ArrayList<String> extGroup = new ArrayList<String>();// ��Ҫչ�������ܵķ��飬�������ڸ����¼�ϼ�Ϊ0���������ֽ�������ɵ�
			int listsize = sumlist.size();
			for (int k = 0; k < listsize; k++) {
				DetailVO detailVO = sumlist.elementAt(k);
				List<CashflowcaseVO> list = cashmap.get(detailVO.getPk_detail());
				if (list != null && !list.isEmpty()) {
					// �����������ƾ֤����ô����ӿڶ�������CashflowcaseVO���п���û�б�����Ϣ����Ҫ����
					CashflowcaseVO[] cashflowcasevos = new CashflowcaseVO[list.size()];
					list.toArray(cashflowcasevos);
					if (cashflowcasevos != null) {
						for (int i = 0; i < cashflowcasevos.length; i++) {
							if (cashflowcasevos[i] != null && cashflowcasevos[i].getM_pk_currtype() == null)
								cashflowcasevos[i].setM_pk_currtype(detailVO.getPk_currtype());
						}
						UserDataVO udvo = new UserDataVO();
						udvo.setDatatype("�ֽ�����"/*-=notranslate=-*/);
						udvo.setUserdata(cashflowcasevos);
						Vector<UserDataVO> v = detailVO.getOtheruserdata();
						if (v == null)
							v = new Vector<UserDataVO>();
						v.addElement(udvo);
						// ����У�飬���ֽ������ķ�¼��������ֽ���ϼ�Ϊ0���������Ϊ����ϼ�Ϊ0�ķ�¼���ܱ�ɾ������ȱʧ�����ֽ���������
						if (detailVO.getLocalcreditamount().equals(detailVO.getLocaldebitamount())) {
							// �����ȵķ�¼�ϼƽ���п���Ϊ0�����Ǹ÷�¼���ֽ������Ļ�������ɾ��
							if (detailVO.getLocalcreditamount().equals(UFDouble.ZERO_DBL)) {
								if (detailVO.getDebitquantity().equals(detailVO.getCreditquantity())) {
									if (detailVO.getDebitquantity().equals(UFDouble.ZERO_DBL)) {
										// �������Ϊ0��ʱ�򣬸÷�¼�ķ������¼��㣬������ϲ�
										extGroup.add(detailVO.getPk_detail());
									} else {
										// ����Ϊ0ʱ���Ѹ÷�¼���Ϊ1��1�����ֽ��������ڽ跽
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
								// ����Ϊ0ʱ���Ѹ÷�¼���Ϊ1��1�����ֽ��������ڽ跽
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
				// ����Ҫչ���ķ������չ��
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
				throw new BusinessException("���ֽ�����������¼���ֽ�������ķ��鲻һ����ɲ����ֽ�����û�й�����"/*-=notranslate=-*/);
			}
		}
		Logger.debug("���ֽ��������");
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
		
		//���������ŷ�˱��������ŷ�˱�������
		if(checkEURStart) {
			select.append(" , vat.").append(VatDetailVO.PK_VATCOUNTRY).append(" , vat.").append(VatDetailVO.PK_TAXCODE).append(" , vat.").append(VatDetailVO.PK_SUPPLIERVATCODE).append(" , vat.").append(VatDetailVO.PK_CLIENTVATCODE);
			select.append(" , vat.").append(VatDetailVO.BUSINESSCODE).append(" , vat.").append(VatDetailVO.MONEYAMOUNT).append(" , vat.").append(VatDetailVO.TAXAMOUNT);
			select.append(" ,vat.").append(VatDetailVO.PK_RECEIVECOUNTRY).append(" as ").append(VatDetailVO.PK_RECEIVECOUNTRY);
		}
		
		select.append(" from gl_rtdetail a inner join ").append(tempTable).append(" b on a.pk_voucher = b.pk_voucher");
        
        //�������ŷ�˷�¼��
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
				//������ѯ�������㣬�Ϳ�Ŀ��Ϣ
				Map<String,AssVO[]> assidMap = getAssid_AccasoaMap(detailsMap,pk_accasoaMap);
				
				//������ȡ��ƿ�Ŀ������Ŀ��汾��ѯ
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
						// ��ͷժҪ
						if (vo.getDetail(0) != null)
							vo.setExplanation(vo.getDetail(0).getExplanation());
						// ��������ƾ֤��־
						boolean isQuantityAdjust = false;
						// ����ϼƽ����к�
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
							
							//����ŷ�˱�����Ϣ
							VatDetailVO vatDetailVo = detailVo.getVatdetail();
							
							if(vatDetailVo != null) {
								UFDouble taxAmount = null;
								if (detailVo.getLocaldebitamount() != null && !UFDouble.ZERO_DBL.equals(detailVo.getLocaldebitamount())) {
									// ��������Ϊ�跽
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
								// hurh ͨ������Я���ֽ������������־�������ã������������Զ���������ʱ���ֽ����������Զ�����������
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
			
			// �԰汾���ս������򣬷���ת��ʱʹ��
			Arrays.sort(chartVersions, new Comparator<String>(){

				@Override
				public int compare(String o1, String o2) {
					return o2.compareTo(o1);
				}

			});
			
			//���ڴ洢��Ŀ�汾��Ӧ��groupid ����
			Map<String,List<String>> versionGroupMap = new HashMap<String, List<String>>();
			for(String groupKey:keySet) {
				VoucherVO voucherVo = voucherMap.get(groupKey);
				UFDate prepareddate = voucherVo.getPrepareddate();
				String stddate = prepareddate.toStdString();
				
				//�����汾
				String ownVersion = null;				
				for(String chartVersion : chartVersions){
					if(stddate.compareTo(chartVersion) >= 0){ // ������һ����stddateС�ģ�����stddate��Ӧ�����°汾
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
			
			//ѭ�����п�Ŀ�汾������������ѯ
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
	 * ��Ŀ��������ӿ�
	 * <p>�޸ļ�¼��</p>
	 * @return
	 * @see
	 * @since V6.0
	 */
	/** ��Ŀ��������ӿ� */
	private IAccChartPubService accChartService = null;
	
	private IAccChartPubService getAccChartService() {
		if(accChartService == null){
			accChartService = NCLocator.getInstance().lookup(IAccChartPubService.class);
		}
		return accChartService;
	}
	
	
	/**
	 * �������������㣬����Ŀ��ϵ��Ϣpk����
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
					//����ƿ�Ŀ��Ϣ��¼
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
			Logger.error("���丨��������Ϣʧ�ܣ�����Ӱ��ʵʱƾ֤���ɡ�", e);
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
				// �跽��������Ϊ0
				if (is_sum_dire) {
					// �������һ�µ�ʱ��ϲ����ϲ������֤����Ϊ��
					UFDouble amount = detail.getDebitamount().sub(detail.getCreditamount());
					UFDouble quantity = detail.getDebitquantity().sub(detail.getCreditquantity());
					UFDouble localamount = detail.getLocaldebitamount().sub(detail.getLocalcreditamount());
					UFDouble groupdebitamount = detail.getGroupdebitamount().sub(detail.getGroupcreditamount());
					UFDouble globaldebitamount = detail.getGlobaldebitamount().sub(detail.getGlobalcreditamount());
					if (localamount.compareTo(zero) > 0 || groupdebitamount.compareTo(zero) > 0 || globaldebitamount.compareTo(zero) > 0) {
						// ����ڽ跽
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
						// ����ڴ���
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
						// �����֣��ж������������о������������¼��û��ɾ��
						if (quantity.compareTo(zero) == 0)
							detaillist.remove(i);
						else {
							if (quantity.compareTo(zero) > 0) {
								// �跽
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
								// ����ڴ���
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
					// ������ϲ������ֳ�2����¼
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
				// �跽����������һ��Ϊ0
				if ((detail.getLocaldebitamount().equals(UFDouble.ZERO_DBL) && detail.getGroupdebitamount().equals(UFDouble.ZERO_DBL) && detail.getGlobaldebitamount().equals(UFDouble.ZERO_DBL)) 
						&& (detail.getLocalcreditamount().equals(UFDouble.ZERO_DBL) && detail.getGroupcreditamount().equals(UFDouble.ZERO_DBL) && detail.getGlobalcreditamount().equals(UFDouble.ZERO_DBL))) {
					if (detail.getDebitquantity().equals(UFDouble.ZERO_DBL) && detail.getCreditquantity().equals(UFDouble.ZERO_DBL)) {
						// �����������Ϊ0�ķ�¼ɾ��
						//�����VAT��Ϣ������Ҫ�ж�vat��˰����Ƿ�Ϊ��
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
						// ��������ƾ֤
					}
				}
			}
		}
		if (detaillist.size() > 0) {

			// �ж���������
			for (int i = detaillist.size() - 1; i >= 0; i--) {
				AccountVO accvo = accountMap.get(detaillist.elementAt(i).getPk_accasoa());
				if (accvo != null) {
					if (accvo.getUnit() == null) {
						// �����������Ŀ����������͵���
						detaillist.elementAt(i).setDebitquantity(zero);
						detaillist.elementAt(i).setCreditquantity(zero);
						detaillist.elementAt(i).setPrice(zero);
						// ������������������Ժ󣬽����Ϊ0����ɾ���÷�¼
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
						// ���������Ŀ�����㵥��
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
				// ����
				if (orderbyno != null && !orderbyno.equals(SchemeConst.ORDERTYPE_NO)) {
					Vector<DetailVO> creditVec = new Vector<DetailVO>();
					Vector<DetailVO> debitVec = new Vector<DetailVO>();
					boolean orderbyCode = false;
					if (parent != null && parent.getIsorderbycode() != null) {
						orderbyCode = parent.getIsorderbycode().booleanValue();
					}
					if (orderbyCode) {
						// ����Ŀ��������
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
							// �跽��
							// if (mergeHeadVo.getSortBySubjFlag().booleanValue()) {
							// int index = findInsertPosition(debitVec, tmpVo);
							// debitVec.insertElementAt(tmpVo, index);
							// } else {
							debitVec.addElement(tmpVo);
							// }
						} else {
							// ������
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
			// ��������
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
				//ҵ������ V6.3
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
				//���������ŷ�˲�Ʒ
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
				// hurh �����˺���һ���ϲ�ά��
				.append(MDDetail.BANKACCOUNT).append(",").append(" null as ").append(MDDetail.PK_VOUCHER).append(",").append(" min(a.").append(MDDetail.PK_ORG).append(") as ").append(MDDetail.PK_ORG).append(",").append(" min(").append(MDDetail.MODIFYFLAG).append(") as ").append(MDDetail.MODIFYFLAG).append(",").append(" min(").append(MDDetail.RECIEPTCLASS).append(") as ").append(
						MDDetail.RECIEPTCLASS).append(",").append(" sum(").append(MDDetail.DEBITAMOUNT).append(") as ").append(MDDetail.DEBITAMOUNT).append(",").append(" sum(").append(MDDetail.DEBITQUANTITY).append(") as ").append(MDDetail.DEBITQUANTITY).append(",").append(" sum(").append(MDDetail.LOCALDEBITAMOUNT).append(") as ").append(MDDetail.LOCALDEBITAMOUNT).append(",").append(" sum(")
				.append(MDDetail.GROUPDEBITAMOUNT).append(") as ").append(MDDetail.GROUPDEBITAMOUNT).append(",").append(" sum(").append(MDDetail.GLOBALDEBITAMOUNT).append(") as ").append(MDDetail.GLOBALDEBITAMOUNT).append(",").append(" sum(").append(MDDetail.CREDITAMOUNT).append(") as ").append(MDDetail.CREDITAMOUNT).append(",").append(" sum(").append(MDDetail.CREDITQUANTITY).append(") as ")
				.append(MDDetail.CREDITQUANTITY).append(",").append(" sum(").append(MDDetail.LOCALCREDITAMOUNT).append(") as ").append(MDDetail.LOCALCREDITAMOUNT).append(",").append(" sum(").append(MDDetail.GROUPCREDITAMOUNT).append(") as ").append(MDDetail.GROUPCREDITAMOUNT).append(",").append(" sum(").append(MDDetail.GLOBALCREDITAMOUNT).append(") as ").append(MDDetail.GLOBALCREDITAMOUNT)
				.append(",a.").append(MDDetail.PK_ACCOUNTINGBOOK).append(" as ").append(MDDetail.PK_ACCOUNTINGBOOK).append(",a.").append(MDDetail.PK_UNIT).append(" as ").append(MDDetail.PK_UNIT).append(",a.").append(MDDetail.PK_UNIT_V).append(" as ").append(MDDetail.PK_UNIT_V).append(",a.").append(MDDetail.PK_VOUCHERTYPEV).append(" as ").append(MDDetail.PK_VOUCHERTYPEV).append(" ,").append(MDDetail.YEARV).append(" as ").append(MDDetail.YEARV).append(",").append(MDDetail.PERIODV)
				.append(" as ").append(MDDetail.PERIODV).append(" ,a.").append(MDDetail.EXPLANATION +" as "+MDDetail.EXPLANATION).append(" ,a.").append(MDDetail.PK_ACCASOA).append(" as ").append(MDDetail.PK_ACCASOA).append(",").append(MDDetail.ASSID).append(" as ").append(MDDetail.ASSID).append(",").append(MDDetail.PK_CURRTYPE).append(" as ").append(MDDetail.PK_CURRTYPE).append("  ,").append(MDDetail.PRICE).append(" as ")
				.append(MDDetail.PRICE).append(" ,").append(MDDetail.EXCRATE2).append(" as ").append(MDDetail.EXCRATE2).append(" ,").append(MDDetail.EXCRATE3).append(" as ").append(MDDetail.EXCRATE3).append(" ,").append(MDDetail.EXCRATE4).append(" as ").append(MDDetail.EXCRATE4).append(" ,").append(MDDetail.CHECKSTYLE).append(" as ").append(MDDetail.CHECKSTYLE).append(" ,").append(
						MDDetail.CHECKNO).append(" as ").append(MDDetail.CHECKNO).append(" ,").append(MDDetail.CHECKDATE).append(" as ").append(MDDetail.CHECKDATE).append(" ,").append(MDDetail.BILLTYPE).append(" as ").append(MDDetail.BILLTYPE).append(" ,").append(MDDetail.INNERBUSNO).append(" as ").append(MDDetail.INNERBUSNO).append(" ,").append(MDDetail.INNERBUSDATE).append(" as ").append(
						MDDetail.INNERBUSDATE).append(" ,").append(MDDetail.BUSIRECONNO).append(" as ").append(MDDetail.BUSIRECONNO).append(" ,").append(MDDetail.NETBANKFLAG).append(" as ").append(MDDetail.NETBANKFLAG).append(" ,").append(MDDetail.VERIFYDATE).append(" as ").append(MDDetail.VERIFYDATE).append(" ,").append(MDDetail.VERIFYNO).append(" as ").append(MDDetail.VERIFYNO).append(" ,").append(getDefSql()).append(" b.groupid as groupid ");
						
				//���������ŷ�˱��������ŷ�˱�������
		        if(checkEURStart) {
		        	select.append(" , vat.").append(VatDetailVO.PK_VATCOUNTRY).append(" , vat.").append(VatDetailVO.PK_TAXCODE).append(" , vat.").append(VatDetailVO.PK_SUPPLIERVATCODE).append(" , vat.").append(VatDetailVO.PK_CLIENTVATCODE);
		        	select.append(" , vat.").append(VatDetailVO.BUSINESSCODE).append(" , sum(vat.").append(VatDetailVO.MONEYAMOUNT).append(") as "+VatDetailVO.MONEYAMOUNT+" , sum(vat.").append(VatDetailVO.TAXAMOUNT).append(") as "+VatDetailVO.TAXAMOUNT);
		        	
		        	select.append(" ,vat.").append(VatDetailVO.PK_RECEIVECOUNTRY).append(" as ").append(VatDetailVO.PK_RECEIVECOUNTRY);
		        	
		        }
						
		        select.append(" from gl_rtdetail a inner join ").append(tempTable).append(" b on a.pk_voucher = b.pk_voucher");
		        
		        //�������ŷ�˷�¼��
		        if(checkEURStart) {
		        	select.append(" LEFT OUTER JOIN GL_VATDETAIL vat ON a.PK_DETAIL = vat.pk_detail ");
		        	select.append(" and  vat.").append(VatDetailVO.VOUCHERKIND).append("='").append(VoucherkindEnum.TMPVOUCHER.getEnumValue().getValue()).append("' ");
		        }
		        
		// ������ѯ����
		StringBuilder where = new StringBuilder();
		where.append(" where ").append(" a.dr = 0 ");
		
		// ������������
		// ������Ϣ������ժҪ������ѡ�񣩡���Ŀ���������㡢���֡����ۡ�����2������3������4��
		// ���㷽ʽ��Ʊ�ݱ�š�Ʊ�����ڡ�Ʊ�����͡��ڲ����׺š��ڲ��������ڡ�Эͬ�š��Զ�����
		StringBuilder group = new StringBuilder();
		group.append(" group by b.groupid,a.pk_accountingbook,a.pk_unit,a.pk_unit_v,pk_vouchertypev,yearv,periodv,a.explanation,").append(" a.pk_accasoa, assid, pk_currtype, price, excrate2, excrate3,excrate4,bankaccount,checkstyle, checkno,").append(" checkdate, billtype, innerbusno, innerbusdate, busireconno,netbankflag, verifydate, verifyno, ").append(" freevalue1, freevalue2, freevalue3, freevalue4, freevalue5, freevalue6, ").append(
				" freevalue7, freevalue8, freevalue9, freevalue10, freevalue11, freevalue12,").append(" freevalue13, freevalue14, freevalue15, freevalue16, freevalue17, freevalue18,").append(" freevalue19, freevalue20, freevalue21, freevalue22, freevalue23, freevalue24, ").append(" freevalue25, freevalue26, freevalue27, freevalue28, freevalue29, freevalue30 ");
		
		//���ŷ�˱����������
		if(checkEURStart) {
			group.append(" , vat.").append(VatDetailVO.PK_VATCOUNTRY).append(" , vat.").append(VatDetailVO.PK_TAXCODE).append(" , vat.").append(VatDetailVO.PK_SUPPLIERVATCODE).append(" , vat.").append(VatDetailVO.PK_CLIENTVATCODE);
			group.append(" , vat.").append(VatDetailVO.BUSINESSCODE).append(" , vat.").append(VatDetailVO.PK_RECEIVECOUNTRY);
		}
		
		// ����
		StringBuilder order = new StringBuilder();
		order.append(" order by groupid ");
		rst[0] = select.toString();
		rst[1] = where.toString();
		rst[2] = group.toString();
		rst[3] = order.toString();
		// rst.append(select).append(where).append(group).append(order);
		return rst;
	}

	// ����Զ�����
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
					// ����Ӧ��ת��MDVoucher����
					tempvo.setBillVO(vos[i]);
					tempvo.setRelationID(vos[i].getPk_voucher());
					rs.add(tempvo);
				}
			}
		}
		return rs;
	}

	/**
	 * �ϲ���¼
	 */
	private VoucherVO mergeDetailAndGetGLVo(VoucherVO rtHeadVo) throws Exception {
		// �跽�ܽ��
		UFDouble totalDebitMny = UFDouble.ZERO_DBL;
		UFDouble groupdebitTotal = UFDouble.ZERO_DBL;
		UFDouble globaldebitTotal = UFDouble.ZERO_DBL;
		// �����ܽ��
		UFDouble totalCreditMny = UFDouble.ZERO_DBL;
		UFDouble groupcreditTotal = UFDouble.ZERO_DBL;
		UFDouble globalcreditTotal = UFDouble.ZERO_DBL;

		// ����VO
		VoucherVO retVo = (VoucherVO) rtHeadVo.clone();
		retVo.setDetails(null);
		// ժҪ
		String strRemark = null;
		// ��������
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
			// ��ȡ��ģ���Ĭ�Ϸ���
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

			// ��Ŀ�븨����ͬʱ�ϲ�
			isSumAccasoa = null != parent.getIscombinsameacc() && parent.getIscombinsameacc().booleanValue();
			// ժҪ��ͬʱ�ϲ�
			isSumExplation = null != parent.getIscombindifexp() && parent.getIscombindifexp().booleanValue();
			// �ϲ���ʽ
			combinType = parent.getCombintype();
			// ����
			needorder = !parent.getDetailorder().equals(SchemeConst.ORDERTYPE_NO);
			// �������
			is_sum_dire = null != parent.getIscombindifdirection() && parent.getIscombindifdirection().booleanValue();

			// �����Ŀ
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
			
			//���ŷ�˱����Ƿ�����
			boolean checkEURStart = GLStartCheckUtil.checkEURStart(rtHeadVo.getPk_group());
			
			//��ѯ���еı�˰�� 
			CountryZoneVO[] countryVOs = NCLocator.getInstance().lookup(ICountryQryService.class).queryAll();
			Map<String,CountryZoneVO> countryMap = new HashMap<String, CountryZoneVO>();
			if(countryVOs != null && countryVOs.length>0) {
				for (CountryZoneVO countryZoneVO : countryVOs) {
					countryMap.put(countryZoneVO.getPk_country(), countryZoneVO);
				}
			}
			
			// �ϲ���¼
			ArrayList result = new ArrayList();
			for (int i = 0; i < mddetail.length; i++) {
				DetailVO rtvb = mddetail[i];
				boolean hasMerged = false;
				for (int j = 0; j < result.size(); j++) {
					DetailVO detail = (DetailVO) result.get(j);
					
					if (GLVoucherMerge.canMerge(rtvb, detail, isSumAccasoa, is_sum_dire, exppks, combinType, isSumExplation,checkEURStart)) {
						hasMerged = true;
						// ��¼��
						detail.setDetailindex(Integer.valueOf(detail.getDetailindex().intValue() + 1));
						// �跽����
						detail.setDebitquantity(detail.getDebitquantity().add(rtvb.getDebitquantity() == null ? UFDouble.ZERO_DBL : rtvb.getDebitquantity()));
						// ԭ�ҽ跢����
						detail.setDebitamount(detail.getDebitamount().add(rtvb.getDebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getDebitamount()));
						// ���ҽ跢����
						detail.setLocaldebitamount(detail.getLocaldebitamount().add(rtvb.getLocaldebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getLocaldebitamount()));
						// ���ű��ҽ跢����
						detail.setGroupdebitamount(detail.getGroupdebitamount().add(rtvb.getGroupdebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getGroupdebitamount()));
						// ȫ�ֱ��ҽ跢����
						detail.setGlobaldebitamount(detail.getGlobaldebitamount().add(rtvb.getGlobaldebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getGlobaldebitamount()));
						// ��������
						detail.setCreditquantity(detail.getCreditquantity().add(rtvb.getCreditquantity() == null ? UFDouble.ZERO_DBL : rtvb.getCreditquantity()));
						// ԭ�Ҵ�������
						detail.setCreditamount(detail.getCreditamount().add(rtvb.getCreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getCreditamount()));
						// ���Ҵ�������
						detail.setLocalcreditamount(detail.getLocalcreditamount().add(rtvb.getLocalcreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getLocalcreditamount()));
						// ���Ҵ�������
						detail.setGroupcreditamount(detail.getGroupcreditamount().add(rtvb.getGroupcreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getGroupcreditamount()));
						// ���Ҵ�������
						detail.setGlobalcreditamount(detail.getGlobalcreditamount().add(rtvb.getGlobalcreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getGlobalcreditamount()));
						// �޸��ֽ�����
						detail = GLVoucherMerge.mergeUserDataVO(detail, rtvb);
						
						//����ŷ��
						if(checkEURStart) {
							detail = GLVoucherMerge.mergeEurData(detail, rtvb);
						}
						break;
					}
				}
				if (!hasMerged) {
					// ��rtvb������result֮��
					DetailVO detail = new DetailVO();

					// ��˾����
					detail.setPk_org(rtvb.getPk_org());
					// ��¼��
					detail.setDetailindex(Integer.valueOf(0));
					// ��Ŀ����
					detail.setPk_accasoa(rtvb.getPk_accasoa());
					// ���������ʶ
					detail.setAssid(rtvb.getAssid());
					// �����������
					detail.setAss(rtvb.getAss());
					// ժҪ����
					detail.setExplanation(rtvb.getExplanation());
					// ��������
					detail.setPk_currtype(rtvb.getPk_currtype());
					// �Է���Ŀ
					detail.setOppositesubj(null);
					// ����
					detail.setPrice(UFDouble.ZERO_DBL);
					// ����1
					detail.setExcrate1(rtvb.getExcrate1());
					// ����2
					detail.setExcrate2(rtvb.getExcrate2());
					// �跽����
					detail.setDebitquantity(rtvb.getDebitquantity() == null ? UFDouble.ZERO_DBL : rtvb.getDebitquantity());
					// ԭ�ҽ跢����
					detail.setDebitamount(rtvb.getDebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getDebitamount());
					// ���ҽ跢����
					detail.setFracdebitamount(rtvb.getFracdebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getFracdebitamount());
					// ���ҽ跢����
					detail.setLocaldebitamount(rtvb.getLocaldebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getLocaldebitamount());
					// ���ű��ҽ跢����
					detail.setGroupdebitamount(rtvb.getGroupdebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getGroupdebitamount());
					// ȫ�ֱ��ҽ跢����
					detail.setGlobaldebitamount(rtvb.getGlobaldebitamount() == null ? UFDouble.ZERO_DBL : rtvb.getGlobaldebitamount());
					// ��������
					detail.setCreditquantity(rtvb.getCreditquantity() == null ? UFDouble.ZERO_DBL : rtvb.getCreditquantity());
					// ԭ�Ҵ�������
					detail.setCreditamount(rtvb.getCreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getCreditamount());
					// ���Ҵ�������
					detail.setFraccreditamount(rtvb.getFraccreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getFraccreditamount());
					// ���Ҵ�������
					detail.setLocalcreditamount(rtvb.getLocalcreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getLocalcreditamount());
					// ���Ҵ�������
					detail.setGroupcreditamount(rtvb.getGroupcreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getGroupcreditamount());
					// ���Ҵ�������
					detail.setGlobalcreditamount(rtvb.getGlobalcreditamount() == null ? UFDouble.ZERO_DBL : rtvb.getGlobalcreditamount());
					// �޸ı�־
					detail.setModifyflag(rtvb.getModifyflag());
					// ���ݴ�����
					detail.setRecieptclass(null);
					// ���㷽ʽ
					detail.setCheckstyle(rtvb.getCheckstyle());
					// Ʊ�ݱ��
					detail.setCheckno(rtvb.getCheckno());
					// Ʊ������
					detail.setCheckdate(rtvb.getCheckdate() == null ? null : rtvb.getCheckdate());
					// Ʊ������
					// detail.setFree3(rtvb.getFree3() == null ? null : rtvb.getFree3());
					// �ʻ�
					detail.setBankaccount(rtvb.getBankaccount());
					// Эͬ��
					detail.setBusireconno(rtvb.getBusireconno());
					// ҵ�������
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
					
					//v63 ��������
					detail.setVerifyno(rtvb.getVerifyno());
					detail.setVerifydate(rtvb.getVerifydate());
					
					//���������ŷ�˱�����Ҫ���ý��״���
					if(checkEURStart) {
						resetVatBusiCode(countryMap,rtvb);
					}
					
					//ŷ����Ϣ
					detail.setVatdetail(rtvb.getVatdetail());
					/** ****************************��������չѡ��****************************** */
					detail = createDetailExt(detail, rtvb);
					
					/** ************************************************************************ */
					result.add(detail);
				}
			}
			// ȡ��Ŀ
			HashMap<String, AccountVO> subjmap = new HashMap<String, AccountVO>();
			Vector<String> pk_subjs = new Vector<String>();
			// �ϲ���¼�Ľ���������������
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
								// �跽����
								mergedDetail.setDebitquantity(mergedDetail.getDebitquantity().sub(mergedDetail.getCreditquantity()));
								// ԭ�ҽ跢����
								mergedDetail.setDebitamount(mergedDetail.getDebitamount().sub(mergedDetail.getCreditamount()));
								// ���ҽ跢����
								mergedDetail.setFracdebitamount(mergedDetail.getFracdebitamount().sub(mergedDetail.getFraccreditamount()));
								// ���ҽ跢����
								mergedDetail.setLocaldebitamount(mergedDetail.getLocaldebitamount().sub(mergedDetail.getLocalcreditamount()));
								// ���ű��ҽ跢����
								mergedDetail.setGroupdebitamount(mergedDetail.getGroupdebitamount().sub(mergedDetail.getGroupcreditamount()));
								// ȫ�ֱ��ҽ跢����
								mergedDetail.setGlobaldebitamount(mergedDetail.getGlobaldebitamount().sub(mergedDetail.getGlobalcreditamount()));
								// ��������
								mergedDetail.setCreditquantity(UFDouble.ZERO_DBL);
								// ԭ�Ҵ�������
								mergedDetail.setCreditamount(UFDouble.ZERO_DBL);
								// ���Ҵ�������
								mergedDetail.setFraccreditamount(UFDouble.ZERO_DBL);
								// ���Ҵ�������
								mergedDetail.setLocalcreditamount(UFDouble.ZERO_DBL);
								// ���Ҵ�������
								mergedDetail.setGroupcreditamount(UFDouble.ZERO_DBL);
								// ���Ҵ�������
								mergedDetail.setGlobalcreditamount(UFDouble.ZERO_DBL);

							}
						} else if (debit.compareTo(credit) < 0) {
							if (!credit.equals(UFDouble.ZERO_DBL)) {
								// ��������
								mergedDetail.setCreditquantity(mergedDetail.getCreditquantity().sub(mergedDetail.getDebitquantity()));
								// ԭ�Ҵ�������
								mergedDetail.setCreditamount(mergedDetail.getCreditamount().sub(mergedDetail.getDebitamount()));
								// ���Ҵ�������
								mergedDetail.setFraccreditamount(mergedDetail.getFraccreditamount().sub(mergedDetail.getFracdebitamount()));
								// ���Ҵ�������
								mergedDetail.setLocalcreditamount(mergedDetail.getLocalcreditamount().sub(mergedDetail.getLocaldebitamount()));
								// ���Ҵ�������
								mergedDetail.setGroupcreditamount(mergedDetail.getGroupcreditamount().sub(mergedDetail.getGroupdebitamount()));
								// ���Ҵ�������
								mergedDetail.setGlobalcreditamount(mergedDetail.getGlobalcreditamount().sub(mergedDetail.getGlobaldebitamount()));
								// �跽����
								mergedDetail.setDebitquantity(UFDouble.ZERO_DBL);
								// ԭ�ҽ跢����
								mergedDetail.setDebitamount(UFDouble.ZERO_DBL);
								// ���ҽ跢����
								mergedDetail.setFracdebitamount(UFDouble.ZERO_DBL);
								// ���ҽ跢����
								mergedDetail.setLocaldebitamount(UFDouble.ZERO_DBL);
								// ���ű��ҽ跢����
								mergedDetail.setGroupdebitamount(UFDouble.ZERO_DBL);
								// ȫ�ֱ��ҽ跢����
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
				Logger.error("ȡ��Ŀ������Ӱ������", e);
			}
			DetailVO[] resultVO = new DetailVO[result.size()];
			details = (DetailVO[]) result.toArray(resultVO);
			
			//�������¼�ֿ��洢��Ϊ��֧�ֽ跽����������
			List<DetailVO> creditDetail = new ArrayList<DetailVO>();
			List<DetailVO> debitDetail = new ArrayList<DetailVO>();
			List<DetailVO> zeroDetail = new ArrayList<DetailVO>();
			
			// �����¼��
			if (details != null && details.length != 0) {
				for (int i = 0; i < details.length; i++) {
					// ��ȡ��һ��ժҪ
					if (i == 0) {
						strRemark = details[i].getExplanation();
					}
					details[i].setDetailindex(Integer.valueOf(i + 1));
					// ********����λ��fgj2001-11-01
					details[i].setModifyflag(details[i].getModifyflag());
					// �ж���������
					boolean isQuantity = false;
					String pk_unit = subjmap.get(details[i].getPk_accasoa()).getUnit();
					if (pk_unit != null && !"".equals(pk_unit)) {
						isQuantity = true;
					}
					if (isQuantity) {
						// �跽����
						details[i].setDebitquantity(details[i].getDebitquantity());
						// ��������
						details[i].setCreditquantity(details[i].getCreditquantity());
						// ����
						if (!details[i].getDebitquantity().equals(UFDouble.ZERO_DBL)) {
							details[i].setPrice(details[i].getDebitamount() == null ? UFDouble.ZERO_DBL : (details[i].getDebitamount().div(details[i].getDebitquantity())));
						} else if (!details[i].getCreditquantity().equals(UFDouble.ZERO_DBL)) {
							details[i].setPrice(details[i].getCreditamount() == null ? UFDouble.ZERO_DBL : (details[i].getCreditamount().div(details[i].getCreditquantity())));
						} else {
							details[i].setPrice(UFDouble.ZERO_DBL);
						}

					} else {
						// �跽����
						details[i].setDebitquantity(UFDouble.ZERO_DBL);
						// ��������
						details[i].setCreditquantity(UFDouble.ZERO_DBL);
						// ���ۣ��ǿգ�û���0����
						details[i].setPrice(UFDouble.ZERO_DBL);
					}
					// ���������ۺ�
					totalCreditMny = totalCreditMny.add(details[i].getLocalcreditamount());
					// �跽����
					totalDebitMny = totalDebitMny.add(details[i].getLocaldebitamount());
					// ���������ۺ�
					groupdebitTotal = groupdebitTotal.add(details[i].getGroupdebitamount());
					globaldebitTotal = globaldebitTotal.add(details[i].getGlobaldebitamount());
					// �����ܽ��
					groupcreditTotal = groupcreditTotal.add(details[i].getGroupcreditamount());
					globalcreditTotal = globalcreditTotal.add(details[i].getGlobalcreditamount());
					
					DetailVO detailVo = details[i];					
					detailVo.setPrepareddate(rtHeadVo.getPrepareddate());
					
					// ����ŷ�˱�����Ϣ
					if (checkEURStart) {
						VatDetailVO vatDetailVo = detailVo.getVatdetail();

						if (vatDetailVo != null) {
							UFDouble taxAmount = null;
							if (detailVo.getLocaldebitamount() != null
									&& !UFDouble.ZERO_DBL.equals(detailVo
											.getLocaldebitamount())) {
								// ��������Ϊ�跽
								vatDetailVo.setDirection(DirectionEnum.DEBIT
										.getEnumValue().getValue());
								taxAmount = detailVo.getLocaldebitamount();
							} else if(detailVo.getLocalcreditamount() != null && !UFDouble.ZERO_DBL.equals(detailVo.getLocalcreditamount())){
								vatDetailVo.setDirection(DirectionEnum.CREDIT
										.getEnumValue().getValue());
								taxAmount = detailVo.getLocalcreditamount();
							} else {//�����˰��¼��ֵΪ0��ȡ��Ŀ�ķ���    ��������൱���٣���Ч�������ٸ�
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
						// hurh �����ֽ������ѷ�����־�������Զ�����ʱ����
						retVo.setHasCashflowModified(true);
					}
					
					//����
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
			
			//�������ܷ�¼
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
				//���÷�¼��
				DetailVO[] detailVOs = rtDetail.toArray(new DetailVO[0]);
				if(detailVOs != null && detailVOs.length>0) {
					for (int i = 0; i < detailVOs.length; i++) {
						detailVOs[i].setDetailindex(Integer.valueOf(i+1));
					}
				}
				// �ӱ�ϲ�����
				retVo.setDetails(detailVOs);
			}else {
				// �ӱ�ϲ�����
				retVo.setDetails(details);
			}

			// ת����������
			// ������
			retVo.setAttachment(rtHeadVo.getAttachment());

			retVo.setDiscardflag(nc.vo.pub.lang.UFBoolean.FALSE);
			// �޸ı�־
//			retVo.setDetailmodflag(strControlFlag == null ? UFBoolean.TRUE : UFBoolean.valueOf(strControlFlag.substring(0, 1)));
			retVo.setModifyflag(strControlFlag);

			// ƾ֤��ŵĴ����ǿգ���֪��ʱ�0�������ڱ�����Զ����ɡ���
			retVo.setNo(Integer.valueOf(0));
			// ����ڼ�
			retVo.setPeriod(rtHeadVo.getPeriod());
			// ������
			retVo.setYear(rtHeadVo.getYear());
			// ��˾
			retVo.setPk_org(rtHeadVo.getPk_org());
			// ƾ֤���
			retVo.setPk_vouchertype(rtHeadVo.getPk_vouchertype());
			// ƾ֤����(����Ҫ��0)
			retVo.setVoucherkind(Integer.valueOf(0));
			// �Ƶ���
			retVo.setPk_prepared(rtHeadVo.getPk_prepared());
			// ֵ������
			retVo.setPrepareddate(rtHeadVo.getPrepareddate());
			// ϵͳ
			retVo.setPk_system(rtHeadVo.getPk_system());
			// Ŀ��ϵͳ
			retVo.setPk_system(rtHeadVo.getPk_system());
			// ժҪ
			retVo.setExplanation(strRemark);
			// 2003-05-07����ǩ�ֱ�־

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
	 * //���״��� ����ջ�����������ͬ����ŷ���� ���ÿգ����������һ����ŷ���ڣ������������ó�ף��ѽ��״����ÿ� 
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
						
						if(CountryZoneExVO.getIseucountry(receiveVo).booleanValue()) {//�ջ���Ϊŷ��
							if(CountryZoneExVO.getIseucountry(sendVo).booleanValue()) {//������Ϊŷ��
								if (pk_receivecountry.equals(sendcountryid)) {// ������������ջ�����ͬ  ��ΪD
									vatDetailVo.setBusinesscode(BusinessCodeEnum.D.getEnumValue().getValue());
								}
							}else {//��������Ϊŷ��
								//�����Ϊ����ó��  ����Ҫ�����״������
								String businesscode = vatDetailVo.getBusinesscode();
								if(businesscode != null && !BusinessCodeEnum.T.getEnumValue().getValue().equals(businesscode)) {
									vatDetailVo.setBusinesscode(null);
								}
							}
						}else {//�ջ�����Ϊŷ��
                            if(CountryZoneExVO.getIseucountry(sendVo).booleanValue()) {//������Ϊŷ��
                            	//�����Ϊ����ó��  ����Ҫ�����״������
								String businesscode = vatDetailVo.getBusinesscode();
								if(businesscode != null && !BusinessCodeEnum.T.getEnumValue().getValue().equals(businesscode)) {
									vatDetailVo.setBusinesscode(null);
								}
							}else {//��������Ϊŷ��
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
