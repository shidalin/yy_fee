/**
 *
 */
package nc.bs.gl.voucher.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.UserExit;
import nc.bs.gl.voucher.VoucherBO;
import nc.bs.logging.Logger;
import nc.gl.glconst.systemtype.SystemtypeConst;
import nc.itf.bd.pub.IBDMetaDataIDConst;
import nc.itf.gl.pub.GLStartCheckUtil;
import nc.itf.gl.pub.IFreevaluePub;
import nc.itf.gl.voucher.IVoucher;
import nc.itf.glcom.para.GLParaAccessor;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.org.IAccountingBookPubService;
import nc.pubitf.org.ICloseAccQryPubServicer;
import nc.pubitf.uapbd.MeasdocUtil;
import nc.vo.bd.account.AccAssVO;
import nc.vo.bd.account.AccountVO;
import nc.vo.bd.material.MaterialVO;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.bd.pub.BDCacheQueryUtil;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.fip.external.FipSaveResultVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.fipub.freevalue.Module;
import nc.vo.fipub.utils.StrTools;
import nc.vo.gateway60.accountbook.AccountBookUtil;
import nc.vo.gateway60.itfs.AccountUtilGL;
import nc.vo.gateway60.itfs.CalendarUtilGL;
import nc.vo.gateway60.itfs.Currency;
import nc.vo.gateway60.itfs.ICurrtypeConst;
import nc.vo.gl.aggvoucher.MDVoucher;
import nc.vo.gl.pubvoucher.DetailVO;
import nc.vo.gl.pubvoucher.VoucherVO;
import nc.vo.gl.vatdetail.VatDetailVO;
import nc.vo.glcom.ass.AssVO;
import nc.vo.glcom.tools.GLPubProxy;
import nc.vo.org.AccountingBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * TODO �ӿ�/�๦��˵����ʹ��˵�����ӿ��Ƿ�Ϊ�������������ʹ���ߣ����Ƿ��̰߳�ȫ�ȣ���
 * </p>
 * 
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li> <br>
 * <br>
 * 
 * @see
 * @author gbh
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-8-9 ����02:12:45
 */
public class VoucherPrepareService4Fip {
	public static String[] amountFields = new String[] {
			"debitquantity", "debitamount", "localdebitamount", "groupdebitamount", "globaldebitamount", "creditquantity", "creditamount", "localcreditamount", "groupcreditamount", "globalcreditamount", "price", "excrate2", "excrate3", "excrate4"
	};

	/**
	 * ����ӻ��ƽ̨��������ƾ֤
	 * 
	 * @param saveResultVO
	 * @return
	 * @throws BusinessException
	 */
	public Collection<FipSaveResultVO> saveRTVoucher(Collection<FipSaveResultVO> saveResultVOs) throws BusinessException {
		List<FipSaveResultVO> fipsaveList = new ArrayList<FipSaveResultVO>();
		if (null != saveResultVOs) {
			VoucherVO voucher = null;
			List<VoucherVO> voucherlist = new ArrayList<VoucherVO>();
			List<VoucherVO> voucherListtmp = new ArrayList<VoucherVO>();
			Map<String, VoucherVO> vouchermap = new HashMap<String, VoucherVO>();
			int k = 0;
			
			for (FipSaveResultVO obj : saveResultVOs) {
				Object vo = obj.getBillVO();
				// ��Ԫ����VOת��Ϊƾ֤VO��ת�������аѽ��Ϊ�յ��ֶ��Զ���0
				voucher = (VoucherVO) assembleVoucher4GL(vo, obj, false,true);
				voucherListtmp.add(voucher);
			}
			// ���Ӽ���¼��������
			checkDetail(voucherListtmp.toArray(new VoucherVO[0]));
			// ���ƾ֤
			for (VoucherVO voucher2 : voucherListtmp) {
				// �������
				checkRtVoucher(voucher2);
				if (voucher2.getDetail() == null || voucher2.getDetail().isEmpty()) {
					vouchermap.put("" + k++, null);
					continue;
				} else {
					vouchermap.put("" + k++, voucher2);
					// ���ɸ�������ID
					createFreevalueIDs(voucher2);
					voucherlist.add(voucher2);
				}
			}
			// ����ƾ֤
			new VoucherBO().saveRtVoucher(voucherlist);
			FipSaveResultVO[] fips = new FipSaveResultVO[saveResultVOs.size()];
			saveResultVOs.toArray(fips);
			FipSaveResultVO result = null;
			int m = 0;
			for (FipSaveResultVO obj : saveResultVOs) {
				Object vo = obj.getBillVO();
				VoucherVO vouchertemp = vouchermap.get("" + m++);
				vo = assembleVoucher4Fip(vouchertemp);
				result = new FipSaveResultVO();
				result.setBillVO(vo);
				result.setMessageinfo(processMessage(obj.getMessageinfo(), vouchertemp));
				fipsaveList.add(result);
			}
		}
		return fipsaveList;
	}

	/**
	 * ����ӻ��ƽ̨��������ƾ֤
	 * 
	 * @param saveResultVO
	 * @return
	 * @throws BusinessException
	 */
	public FipSaveResultVO saveMDVoucher(FipSaveResultVO saveResultVO) throws BusinessException {
		FipSaveResultVO result = null;
		if (null != saveResultVO) {
			result = new FipSaveResultVO();
			Object vo = saveResultVO.getBillVO();
			// ��Ԫ����VOת��Ϊƾ֤VO��ת�������аѽ��Ϊ�յ��ֶ��Զ���0
			VoucherVO voucher = (VoucherVO) assembleVoucher4GL(vo, saveResultVO, true,true);
			// �������
			checkDetail(voucher);
			checkMdVoucher(voucher);
			if (voucher.getDetail() == null || voucher.getDetail().isEmpty()) {
				result.setBillVO(null);
				result.setMessageinfo(processMessage(saveResultVO.getMessageinfo(), voucher));
				return result;
			}
			String pk_prepared = voucher.getPk_prepared();
			//����Ƶ���Ϊ�գ�������Ϊ��ǰ������
			if(StringUtils.isEmpty(pk_prepared)){
				String userId = InvocationInfoProxy.getInstance().getUserId();
				if(userId == null || UserExit.DEFAULT_USERID_VALUE.equals(userId)){
					//�����ǰû�в����ˣ���̨������ߵ��ݴ������ɣ�������ΪĬ���Ƶ���
					voucher.setPk_prepared(SystemtypeConst.NC_USER);
				}else{
					voucher.setPk_prepared(userId);
				}
			}
			// ���ɸ�������ID
			createFreevalueIDs(voucher);
			// ����ƾ֤
			new VoucherBO().save(voucher, true);
			//Ϊ�˽��ͬʱ����ܶ���ƾ֤����ƾ֤У��ʧ�ܣ�����֮ǰƾ֤��û�л�������
			//NCLocator.getInstance().lookup(IVoucher.class).save_RequiresNew(voucher, true);
			vo = assembleVoucher4Fip(voucher);
			result.setBillVO(vo);
			result.setMessageinfo(processMessage(saveResultVO.getMessageinfo(), voucher));
		}
		return result;
	}

	/**
	 * ����˵����
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param voucher
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	@SuppressWarnings("unchecked")
	private void createFreevalueIDs(VoucherVO voucher) throws BusinessException {
		Vector<DetailVO> detailvec = voucher.getDetail();// checkVoucher�Ѿ��������������ﲻ���п�
		IFreevaluePub ip = NCLocator.getInstance().lookup(IFreevaluePub.class);
		for (DetailVO detailVO : detailvec) {
			if (detailVO.getAss() != null) {
				detailVO.setAssid(ip.getAssID(detailVO.getAss(), Boolean.TRUE,voucher.getPk_group(), Module.GL));
			} else {
				detailVO.setAssid(null);
			}
		}
	}

	/**
	 * ����˵����
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param voucher
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	@SuppressWarnings("unchecked")
	public void checkDetail(VoucherVO voucher) throws BusinessException {
		Vector<DetailVO> detailvec = voucher.getDetail();// checkVoucher�Ѿ��������������ﲻ���п�
		// ��¼������õ���Ŀ��Ϣ��������ͳһ��ȡ
		HashMap<String, AccountVO> accmap = new HashMap<String, AccountVO>();
		{
			// ��ʼ����Ŀ��Ϣ
			ArrayList<String> accpks = new ArrayList<String>();
			for (DetailVO detailVO : detailvec) {
				if (detailVO != null && detailVO.getPk_accasoa() == null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0006")/* @res "��¼��Ŀû�����ݣ�" */);
				}
				if (detailVO.getExplanation() == null || "".equals(detailVO.getExplanation())) {
					detailVO.setExplanation(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0007")/* @res "��ϵͳ����ƾ֤" */);
				}
				if (detailVO != null && detailVO.getPk_accasoa() != null && !accpks.contains(detailVO.getPk_accasoa()))
					accpks.add(detailVO.getPk_accasoa());
				detailVO.setPk_detail(null);
			}
			if (!accpks.isEmpty()) {
				String[] pk_subjs = new String[accpks.size()];
				accpks.toArray(pk_subjs);
				AccountVO[] accvos = AccountUtilGL.queryByPks(pk_subjs, voucher.getPrepareddate().toStdString());
				// 1���жϺ����˲�����Ӧ�Ŀ�Ŀ���Ƿ����Ŀ�Ŀ�Ŀ��һ��
				// 2����һ�µ�����£�����¼����Ӧ�Ŀ�Ŀ���ݱ���ת���ɺ����˲�����Ӧ�Ŀ�Ŀ��Ŀ�Ŀ
				// -----------------------------
				if (accvos != null && accvos.length > 0) {
					String accountingBookPK = voucher.getPk_accountingbook();
					AccountingBookVO accountingBookVO = AccountBookUtil.getAccountingBookVOByPrimaryKey(accountingBookPK);
					
					if(accountingBookVO == null){
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0056")/* @res "û���ҵ���Ӧ�Ĳ�������˲�" */);
					}
					
					String voucherAccChartPK = accountingBookVO.getPk_curraccchart();
					ArrayList<String> accountcodes = new ArrayList<String>();
					HashMap<String, String> pk2code = new HashMap<String, String>();
					for (AccountVO accountVO : accvos) {
						if (accountVO == null)
							continue;
						String pkAccasoa = accountVO.getPk_accasoa();
						if (!voucherAccChartPK.equals(accountVO.getPk_currentchart())) {
							String code = accountVO.getCode();
							pk2code.put(pkAccasoa, code);
							if (!accountcodes.contains(code))
								accountcodes.add(code);
						} else {
							accmap.put(pkAccasoa, accountVO);
						}
						accpks.remove(pkAccasoa);
					}
					if (!accountcodes.isEmpty()) {
						HashMap<String, String> code2pk = new HashMap<String, String>();
						accvos = AccountUtilGL.queryAccountVosByCodes(accountingBookPK, accountcodes.toArray(new String[0]), voucher.getPrepareddate().toStdString());
						if (accvos != null) {
							for (AccountVO accountVO : accvos) {
								if (accountVO == null)
									continue;
								code2pk.put(accountVO.getCode(), accountVO.getPk_accasoa());
								accmap.put(accountVO.getPk_accasoa(), accountVO);
							}
						}
						DetailVO tempDetail = null;
						String code = "";
						String pkAccasoa = null;
						String newPK = null;
						for (int i = 0; i < detailvec.size(); i++) {
							tempDetail = detailvec.get(i);
							if (tempDetail == null)
								continue;
							pkAccasoa = tempDetail.getPk_accasoa();
							if (pkAccasoa == null)
								continue;
							code = pk2code.get(pkAccasoa);
							if (code != null) {
								newPK = code2pk.get(code);
								if (newPK == null) {
									throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0049",null,new String[]{code})/* ��Ŀ{0}�ڵ�ǰ�����˲��Ŀ�Ŀ���ﲻ���ڣ��������ݡ� */);
								} else {
									tempDetail.setPk_accasoa(newPK);
								}
							}
						}
					}
				}
				if (!accpks.isEmpty())
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0050")/* @res "���ֿ�Ŀ������ϵ�����Ƿ������������Ҳ�����Ӧ��¼��" */ + accpks);
			}
		}
		// ��ʽ�������ֶ�
		formatNumberDetail(voucher, accmap);
		// �������ϼơ�������Ҫ�ڸ�ʽ����ɺ���У���ҪΪ�˷�ֹ0.235+0.235=0.47����ʽ���Ժ�0.24+0.24<>0.47�����
		computeBalance(voucher, accmap);
		// ȥ�����������Ϊ0�ķ�¼��
		removeZeroDetail(voucher);
		// ���ɸ�������
		generateFreevalueVO(voucher, accmap);
		GLVoucherCashFlowPlus.createCashFlowItems(voucher, accmap);
	}
	
	public void checkDetail(VoucherVO[] voucher) throws BusinessException {
		//Map<pk_accountingbook+PreparedDate+pk_accasoa, AccountVO>
		Map<String, AccountVO> accPkMap = new HashMap<String, AccountVO>();
		//Map<pk_accountingbook+PreparedDate+Code, AccountVO>
		Map<String, AccountVO> accCodeMap = new HashMap<String, AccountVO>();
		
		for (int k = 0; voucher != null && k < voucher.length; k ++) {
			Vector<DetailVO> detailvec = voucher[k].getDetail();// checkVoucher�Ѿ��������������ﲻ���п�
			// ��¼������õ���Ŀ��Ϣ��������ͳһ��ȡ
			HashMap<String, AccountVO> accmap = new HashMap<String, AccountVO>();
			{
				// ��ʼ����Ŀ��Ϣ
				ArrayList<String> accpks = new ArrayList<String>();
				for (DetailVO detailVO : detailvec) {
					if (detailVO != null && detailVO.getPk_accasoa() == null) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0006")/* @res "��¼��Ŀû�����ݣ�" */);
					}
					if (detailVO.getExplanation() == null || "".equals(detailVO.getExplanation())) {
						detailVO.setExplanation(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0007")/* @res "��ϵͳ����ƾ֤" */);
					}
					if (detailVO != null && detailVO.getPk_accasoa() != null && !accpks.contains(detailVO.getPk_accasoa()))
						accpks.add(detailVO.getPk_accasoa());
					detailVO.setPk_detail(null);
				}
				if (!accpks.isEmpty()) {
					String[] pk_subjs = new String[accpks.size()];
					accpks.toArray(pk_subjs);
					String accountingBookPK = voucher[k].getPk_accountingbook();
					String prepareddate = voucher[k].getPrepareddate().toStdString();
					Vector<AccountVO> accVec = new Vector<AccountVO>();
					Vector<String> pkVec = new Vector<String>();
					// accMap��û�еĵ�����ȥ���²�ѯ
					for (int subj = 0; subj < pk_subjs.length; subj ++) {
						AccountVO acVo = accPkMap.get(accountingBookPK+prepareddate+pk_subjs[subj]);
						if (acVo != null) {
							accVec.add(acVo);
						} else {
							pkVec.add(pk_subjs[subj]);
						}
					}
					AccountVO[] accvos = null;
					if (pkVec != null && pkVec.size() != 0) {
						accvos = AccountUtilGL.queryByPks(pkVec.toArray(new String[0]), prepareddate);
						for (int acc = 0; accvos != null && acc < accvos.length; acc ++) {
							accVec.add(accvos[acc]);
							accPkMap.put(accountingBookPK+prepareddate+accvos[acc].getPk_accasoa(), accvos[acc]);
						}
					}
					accvos = accVec.toArray(new AccountVO[0]);
					// 1���жϺ����˲�����Ӧ�Ŀ�Ŀ���Ƿ����Ŀ�Ŀ�Ŀ��һ��
					// 2����һ�µ�����£�����¼����Ӧ�Ŀ�Ŀ���ݱ���ת���ɺ����˲�����Ӧ�Ŀ�Ŀ��Ŀ�Ŀ
					// -----------------------------
					if (accvos != null && accvos.length > 0) {
						AccountingBookVO accountingBookVO = AccountBookUtil.getAccountingBookVOByPrimaryKey(accountingBookPK);
						
						if(accountingBookVO == null){
							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0056")/* @res "û���ҵ���Ӧ�Ĳ�������˲�" */);
						}
						
						String voucherAccChartPK = accountingBookVO.getPk_curraccchart();
						if(StringUtils.isEmpty(voucherAccChartPK)) {
							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0056")/* @res "û���ҵ���Ӧ�Ĳ�������˲�" */);
						}
						
						ArrayList<String> accountcodes = new ArrayList<String>();
						HashMap<String, String> pk2code = new HashMap<String, String>();
						for (AccountVO accountVO : accvos) {
							if (accountVO == null)
								continue;
							String pkAccasoa = accountVO.getPk_accasoa();
							if (!voucherAccChartPK.equals(accountVO.getPk_currentchart())) {
								String code = accountVO.getCode();
								pk2code.put(pkAccasoa, code);
								if (!accountcodes.contains(code))
									accountcodes.add(code);
							} else {
								accmap.put(pkAccasoa, accountVO);
							}
							accpks.remove(pkAccasoa);
						}
						if (!accountcodes.isEmpty()) {
							HashMap<String, String> code2pk = new HashMap<String, String>();
							accVec = new Vector<AccountVO>();
							Vector<String> codeVec = new Vector<String>();
							for (int code = 0; code < accountcodes.size(); code ++) {
								AccountVO accountVO = accCodeMap.get(accountingBookPK+prepareddate+accountcodes.get(code));
								if (accountVO != null) {
									accVec.add(accountVO);
								} else {
									codeVec.add(accountcodes.get(code));
								}
							}
							if (codeVec != null && codeVec.size() != 0) {
								accvos = AccountUtilGL.queryAccountVosByCodes(accountingBookPK, codeVec.toArray(new String[0]), prepareddate);
								for (int acc = 0; accvos != null && acc < accvos.length; acc ++) {
									accVec.add(accvos[acc]);
									accCodeMap.put(accountingBookPK+prepareddate+accvos[acc].getCode(), accvos[acc]);
								}
							}
							accvos = accVec.toArray(new AccountVO[0]);
							if (accvos != null) {
								for (AccountVO accountVO : accvos) {
									if (accountVO == null)
										continue;
									code2pk.put(accountVO.getCode(), accountVO.getPk_accasoa());
									accmap.put(accountVO.getPk_accasoa(), accountVO);
								}
							}
							DetailVO tempDetail = null;
							String code = "";
							String pkAccasoa = null;
							String newPK = null;
							for (int i = 0; i < detailvec.size(); i++) {
								tempDetail = detailvec.get(i);
								if (tempDetail == null)
									continue;
								pkAccasoa = tempDetail.getPk_accasoa();
								if (pkAccasoa == null)
									continue;
								code = pk2code.get(pkAccasoa);
								if (code != null) {
									newPK = code2pk.get(code);
									if (newPK == null) {
										throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0006")/* @res "��¼��Ŀû�����ݣ�" */);
									} else {
										tempDetail.setPk_accasoa(newPK);
									}
								}
							}
						}
					}
					if (!accpks.isEmpty())
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0050")/* @res "���ֿ�Ŀ������ϵ�����Ƿ������������Ҳ�����Ӧ��¼��" */ + accpks);
				}
			}
			// ������
			dealWithPrice(voucher[k], accmap);
			// ��ʽ�������ֶ�
			formatNumberDetail(voucher[k], accmap);
			// �������ϼơ�������Ҫ�ڸ�ʽ����ɺ���У���ҪΪ�˷�ֹ0.235+0.235=0.47����ʽ���Ժ�0.24+0.24<>0.47�����
			computeBalance(voucher[k], accmap);
			// ȥ�����������Ϊ0�ķ�¼��
			removeZeroDetail(voucher[k]);
			// ���ɸ�������
			generateFreevalueVO(voucher[k], accmap);
			GLVoucherCashFlowPlus.createCashFlowItems(voucher[k], accmap);
		}
		
	}
	
	private void dealWithPrice(VoucherVO voucher, HashMap<String, AccountVO> accountMap) throws BusinessException {
		Vector<DetailVO> detaillist = voucher.getDetail();// checkVoucher�Ѿ��������������ﲻ���п�
		UFDouble zero = UFDouble.ZERO_DBL;
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
					if (detaillist.elementAt(i).getLocaldebitamount().equals(UFDouble.ZERO_DBL) && detaillist.elementAt(i).getLocalcreditamount().equals(UFDouble.ZERO_DBL)) {
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
		
	}

	/**
	 * ���ɸ���������ϣ�����Ŀ����
	 * 
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param voucher
	 * @param accmap
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	@SuppressWarnings("unchecked")
	private void generateFreevalueVO(VoucherVO voucher, HashMap<String, AccountVO> accmap) throws BusinessException {
		Vector<DetailVO> detailvec = voucher.getDetail();
		for (DetailVO detailVO : detailvec) {
			HashMap<String, AssVO> assmap = new HashMap<String, AssVO>();
			AssVO[] ass = detailVO.getAss();
			if (ass != null) {
				for (int i = 0; i < ass.length; i++) {
					assmap.put(ass[i].getPk_Checktype(), ass[i]);
				}
			}
			AccountVO accvo = accmap.get(detailVO.getPk_accasoa());
			if (accvo != null) {
				ArrayList<AssVO> asslist = new ArrayList<AssVO>();
				AccAssVO[] accass = accvo.getAccass();
				if (accass == null) {
					detailVO.setAss(null);
				} else {
					for (int i = 0; i < accass.length; i++) {
						AssVO assvo = assmap.get(accass[i].getPk_entity());
						if (assvo == null) {
							assvo = new AssVO();
							assvo.setPk_Checktype(accass[i].getPk_entity());
						}
						asslist.add(assvo);
					}
					if (!asslist.isEmpty())
						detailVO.setAss(asslist.toArray(new AssVO[0]));
					else
						detailVO.setAss(null);
				}
			} else {
				if (detailVO.getPk_accasoa() != null && detailVO.getPk_accasoa().trim().length() > 0)
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0008")/* @res "���������Ҳ�����Ӧ�Ŀ�Ŀ������ϵ��������" */+ detailVO.getPk_accasoa());
			}
		}
		createFreevalueIDs(voucher);

	}

	/**
	 * �򵥵�У��
	 * 
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param voucher
	 * @see
	 * @since V6.0
	 */
	@SuppressWarnings("unchecked")
	private void checkMdVoucher(VoucherVO voucher) throws BusinessException {
		if (voucher.m_pk_vouchertype == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0010")/* @res "�����ƾ֤����û��ƾ֤�����Ϣ��" */);
		}
		if (voucher.getPk_accountingbook() != null) {
			String accbookenableDate = NCLocator.getInstance().lookup(IAccountingBookPubService.class).queryAccountBookPeriodByAccountingBookID(voucher.getPk_accountingbook());
			if (accbookenableDate == null || "".equals(accbookenableDate)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0051")/* @res "���˺����˲�δ����" */ );
			}
		}
	}

	/**
	 * �򵥵�У��
	 * 
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param voucher
	 * @see
	 * @since V6.0
	 */
	@SuppressWarnings("unchecked")
	private void checkRtVoucher(VoucherVO voucher) throws BusinessException {
		if (voucher.getPk_accountingbook() != null) {
			String accbookenableDate = AccountBookUtil.getAccPeriodSchemePKByAccountingbookPk(voucher.getPk_accountingbook());
			if (accbookenableDate == null || "".equals(accbookenableDate)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0051")/* @res "���˺����˲�δ����" */);
			}
		}
		
	}

	/**
	 * �������ϼ�
	 * 
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param voucher
	 * @param accmap2
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	@SuppressWarnings("unchecked")
	private void computeBalance(VoucherVO voucher, HashMap<String, AccountVO> accmap) throws BusinessException {
		Vector<DetailVO> detailvec = voucher.getDetail();// checkVoucher�Ѿ��������������ﲻ���п�
		UFDouble debitTotal = UFDouble.ZERO_DBL;
		UFDouble groupdebitTotal = UFDouble.ZERO_DBL;
		UFDouble globaldebitTotal = UFDouble.ZERO_DBL;
		UFDouble creditTotal = UFDouble.ZERO_DBL;
		UFDouble groupcreditTotal = UFDouble.ZERO_DBL;
		UFDouble globalcreditTotal = UFDouble.ZERO_DBL;

		// ���������ܺͣ�ͬʱ���м�¼���һ����Ϊ�����Ŀ�ķ�¼λ��
		int balanceIndex = -1;
		DetailVO detailVO = null;
		for (int i = 0; i < detailvec.size(); i++) {
			detailVO = detailvec.elementAt(i);
			AccountVO accvo = accmap.get(detailVO.getPk_accasoa());
			if (accvo != null) {
				// ����ʱ�������Ŀ���������
				if (accvo.getOutflag() == null || !accvo.getOutflag().booleanValue()) {
					debitTotal = debitTotal.add(detailVO.getLocaldebitamount());
					groupdebitTotal = groupdebitTotal.add(detailVO.getGroupdebitamount() == null ? UFDouble.ZERO_DBL : detailVO.getGroupdebitamount());
					globaldebitTotal = globaldebitTotal.add(detailVO.getGlobaldebitamount() == null ? UFDouble.ZERO_DBL : detailVO.getGlobaldebitamount());
					creditTotal = creditTotal.add(detailVO.getLocalcreditamount());
					groupcreditTotal = groupcreditTotal.add(detailVO.getGroupcreditamount() == null ? UFDouble.ZERO_DBL : detailVO.getGroupcreditamount());
					globalcreditTotal = globalcreditTotal.add(detailVO.getGlobalcreditamount() == null ? UFDouble.ZERO_DBL : detailVO.getGlobalcreditamount());
					balanceIndex = i;
				}
			} else {
				if (detailVO.getPk_accasoa() != null && detailVO.getPk_accasoa().trim().length() > 0)
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0008")/* @res "���������Ҳ�����Ӧ�Ŀ�Ŀ������ϵ��������" */+ detailVO.getPk_accasoa());
			}
		}

		UFDouble remainder = debitTotal.sub(creditTotal);
		UFDouble groupremainder = groupdebitTotal.sub(groupcreditTotal);
		UFDouble globleremainder = globaldebitTotal.sub(globalcreditTotal);
		if (!remainder.equals(UFDouble.ZERO_DBL)) {
			// ����ϼƲ����
			if (balanceIndex >= 0) {
				// ������һ���Ǳ����Ŀ��VO
				DetailVO balanceVo = detailvec.elementAt(balanceIndex);
				// ����
				if (balanceVo.getDirection()) {
					// �跽
					balanceVo.setLocaldebitamount(balanceVo.getLocaldebitamount().sub(remainder));
					debitTotal = creditTotal;
				} else {
					// ����
					balanceVo.setLocalcreditamount(balanceVo.getLocalcreditamount().add(remainder));
					creditTotal = debitTotal;
				}
			}
		}
		if (!groupremainder.equals(UFDouble.ZERO_DBL)) {
			// ����ϼƲ����
			if (balanceIndex >= 0) {
				// ������һ���Ǳ����Ŀ��VO
				DetailVO balanceVo = detailvec.elementAt(balanceIndex);
				// ����
				if (balanceVo.getDirection()) {
					// �跽
					balanceVo.setGroupdebitamount((balanceVo.getGroupdebitamount() == null ? UFDouble.ZERO_DBL : balanceVo.getGroupdebitamount()).sub(remainder));
					groupdebitTotal = groupcreditTotal;
				} else {
					// ����
					balanceVo.setGroupcreditamount((balanceVo.getGroupcreditamount() == null ? UFDouble.ZERO_DBL : balanceVo.getGroupcreditamount()).add(remainder));
					groupcreditTotal = groupdebitTotal;
				}
			}
		}
		if (!globleremainder.equals(UFDouble.ZERO_DBL)) {
			// ����ϼƲ����
			if (balanceIndex >= 0) {
				// ������һ���Ǳ����Ŀ��VO
				DetailVO balanceVo = detailvec.elementAt(balanceIndex);
				// ����
				if (balanceVo.getDirection()) {
					// �跽
					balanceVo.setGlobaldebitamount((balanceVo.getGlobaldebitamount() == null ? UFDouble.ZERO_DBL : balanceVo.getGlobaldebitamount()).sub(remainder));
					globaldebitTotal = globalcreditTotal;
				} else {
					// ����
					balanceVo.setGlobalcreditamount((balanceVo.getGlobalcreditamount() == null ? UFDouble.ZERO_DBL : balanceVo.getGlobalcreditamount()).add(remainder));
					globalcreditTotal = globaldebitTotal;
				}
			}
		}
		voucher.setTotaldebit(debitTotal);
		voucher.setTotalcredit(creditTotal);
		voucher.setTotaldebitgroup(groupdebitTotal);
		voucher.setTotalcreditgroup(groupcreditTotal);
		voucher.setTotaldebitglobal(globaldebitTotal);
		voucher.setTotalcreditglobal(globalcreditTotal);
	}

	/**
	 * Ԫ����ƾ֤��GLƾ֤�Ļ���ת��
	 * 
	 * @param vo
	 * @param saveResultVO
	 * @return
	 */
	private Object assembleVoucher4GL(Object vo, FipSaveResultVO saveResultVO, boolean ismdVoucher,boolean setControlFlag) throws BusinessException {
		VoucherVO vouchervo = null;
		if (null != vo && vo instanceof MDVoucher) {
			MDVoucher mdVoucher = (MDVoucher) vo;
			vouchervo = fip2gl(mdVoucher,setControlFlag);
			FipRelationInfoVO messageVO = saveResultVO.getMessageinfo();
			processVoucher(vouchervo, messageVO, ismdVoucher);
			return vouchervo;
		} else if (null != vo && vo instanceof VoucherVO) {
			vouchervo = (VoucherVO) vo;
			generalNextMonth(vouchervo, ismdVoucher);
		}
		return vouchervo;
	}

	/**
	 * Ԫ����ƾ֤��GLƾ֤�Ļ���ת��
	 * 
	 * @param vo
	 * @param saveResultVO
	 * @return
	 */
	private Object assembleVoucher4Fip(VoucherVO vo) {
		if (null != vo) {
			VoucherVO voucher = (VoucherVO) vo;
			MDVoucher vouchervo = gl2fip(voucher);
			return vouchervo;
		}
		return null;
	}

	/**
	 * �ӹ�ƾ֤
	 * 
	 * @param vouchervo
	 * @param messageVO
	 * @param ismdVoucher ��ʽƾ֤
	 */
	public static void processVoucher(VoucherVO vouchervo, FipRelationInfoVO messageVO, boolean ismdVoucher) throws BusinessException {
		vouchervo.setPk_accountingbook(messageVO.getPk_org());
		vouchervo.setPk_group(messageVO.getPk_group());
		vouchervo.setPk_prepared(vouchervo.getPk_prepared());
		vouchervo.setPrepareddate(null == vouchervo.getPrepareddate() ? messageVO.getBusidate() : vouchervo.getPrepareddate());

		// hurh ȡ�Ƶ����ڶ�Ӧ�ķ���Ȼ�ڼ䣬��Ϊƾ֤���ڼ�
		AccountCalendar calendar = CalendarUtilGL.getAccountCalendarByAccountBook(vouchervo.getPk_accountingbook());
		try {
			calendar.setDate(vouchervo.getPrepareddate());
		} catch (InvalidAccperiodExcetion e) {
			Logger.error(e.getMessage(), e);
		}
		vouchervo.setPeriod(calendar.getMonthVO().getAccperiodmth());
		vouchervo.setYear(calendar.getYearVO().getPeriodyear());
		generalNextMonth(vouchervo, ismdVoucher);
		vouchervo.setPk_system(StringUtils.isEmpty(messageVO.getPk_system()) ? "GL" : messageVO.getPk_system());
		vouchervo.setVoucherkind(vouchervo.getVoucherkind() == null ? 0 : vouchervo.getVoucherkind());
		if (vouchervo.getDetail() != null && vouchervo.getDetail(0) != null) {
			vouchervo.setExplanation(StringUtils.isEmpty(vouchervo.getDetail(0).getExplanation()) ? nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0007")/* @res "��ϵͳ����ƾ֤" */: vouchervo.getDetail(0).getExplanation());
		}
	}

	private static void generalNextMonth(VoucherVO vouchervo, boolean ismdVoucher)
			throws BusinessException, InvalidAccperiodExcetion {
		
		if (ismdVoucher) {
			AccountCalendar calendar = CalendarUtilGL.getAccountCalendarByAccountBook(vouchervo.getPk_accountingbook());
			try {
				calendar.setDate(vouchervo.getPrepareddate());
			} catch (InvalidAccperiodExcetion e) {
				Logger.error(e.getMessage(), e);
			}
			UFBoolean isNextMonth = UFBoolean.FALSE;
			try {
				isNextMonth= SysinitAccessor.getInstance().getParaBoolean(vouchervo.getPk_accountingbook(), "GL130");
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
				isNextMonth = UFBoolean.FALSE;;
			}
			if (isNextMonth != null && isNextMonth.booleanValue()) {
				
				ICloseAccQryPubServicer qryPubServicer = NCLocator.getInstance().lookup(ICloseAccQryPubServicer.class);
				boolean closed = false;
				try {
					closed = qryPubServicer.isCloseByAccountBookId(vouchervo.getPk_accountingbook(), vouchervo.getYear() + "-" + vouchervo.getPeriod());
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
					closed = false;
				}
				if (closed) {
				   AccperiodVO[] accps =  calendar.getYearVOsOfCurrentScheme();
				   List<String> yearperiodList = new LinkedList<String>(); 
				   //�ڼ䴦��Ĳ���
				   
				   for (int i = 0; accps != null && i < accps.length; i ++) {
					   for (int j = 1; j <= accps[i].getPeriodnum(); j ++) {
						   String yearmont = accps[i].getPeriodyear() + "-" + (j < 10 ? "0"+j : ""+j);
						   if (yearmont.compareTo(vouchervo.getYear() + "-" + vouchervo.getPeriod()) > 0) {
							   yearperiodList.add(yearmont);
						   }
					   }
				   }
				  String yearmonth = "";
				  try {
					  for (int i = 0; i < yearperiodList.size(); i ++) {
						closed = qryPubServicer.isCloseByAccountBookId(vouchervo.getPk_accountingbook(), yearperiodList.get(i));
						if (!closed) {
							yearmonth = yearperiodList.get(i);
							break;
						}
					  }
				   } catch (BusinessException e) {
						Logger.error(e.getMessage(), e);
						closed = true;
				   }
				   if (closed) {
					   throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0052")/* @res "��һ���δ������ڼ䣬�޷���������ƾ֤" */);
				   } else {
					   String[] yearmonts = yearmonth.split("-");
					   int count = GLPubProxy.getRemoteInitBalance().isBuiltByGlOrgBook(vouchervo.getPk_accountingbook(), yearmonts[0]);
					   if (count == 0)
							throw new nc.vo.gateway60.pub.GlBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0053",null,new String[]{yearmonts[0]})/* @res "���{0}δ���ˣ��޷���������ƾ֤��" */);
					   calendar.set(yearmonts[0], yearmonts[1]);
					   vouchervo.setPeriod(yearmonts[1]);
					   vouchervo.setYear(yearmonts[0]);
					   vouchervo.setPrepareddate(calendar.getMonthVO().getBegindate());
				   }
				}
			}
		}
	}

	/**
	 * ����ƾ֤���Ը�����Ϣ�������
	 * 
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param messageinfo
	 * @param voucher
	 * @return
	 * @see
	 * @since V6.0
	 */
	private FipRelationInfoVO processMessage(FipRelationInfoVO messageinfo, VoucherVO voucher) {
		if (voucher != null) {
			messageinfo.setBusidate(voucher.getPrepareddate());
			messageinfo.setPk_billtype("C0");
			messageinfo.setPk_group(voucher.getPk_group());
			messageinfo.setPk_operator(voucher.getPk_prepared());
			messageinfo.setPk_org(voucher.getPk_accountingbook());
			messageinfo.setPk_system("GL");
			messageinfo.setRelationID(voucher.getPk_voucher());
			messageinfo.setFreedef1(getVoucherNoStr(voucher.getNo()));
			messageinfo.setFreedef2(voucher.getExplanation());
			messageinfo.setFreedef3("" + (voucher.getTotaldebit().abs().compareTo(voucher.getTotalcredit().abs()) > 0 ? voucher.getTotaldebit() : voucher.getTotalcredit()));
		}
		return messageinfo;
	}
	
	private static final int maxLength = 8;
	 private String getVoucherNoStr(Integer voucherNo)
	    {
	        StringBuffer rtStr = new StringBuffer();
	        if(voucherNo == null)
	        	voucherNo = 0;
	        String string = voucherNo.toString();
	        for(int i = string.length(); i < maxLength; i++)
	            rtStr.append("0");

	        rtStr.append(string);
	        return rtStr.toString();
	    }
	
//	/**
//	 * �����Ŀ���⣬������ƾ֤�ų�ʼ����8λ
//	 */
//	private int maxLength = 8;
//	private String getVoucherNoStr(Integer voucherNo) {
//		
//		StringBuffer rtStr = new StringBuffer();
//		if(voucherNo == null) {
//			return null;
//		}else {
//			String string = voucherNo.toString();
//			for(int i=string.length();i<maxLength;i++) {
//				rtStr.append("0");
//			} 
//			rtStr.append(string);
//		}
//		return rtStr.toString();
//	}

	/**
	 * 
	 * ����˵����
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param mdVoucher
	 * @return
	 * @see
	 * @since V6.0
	 */
	public static VoucherVO fip2gl(Object Voucher,boolean setControlFlag) {
		MDVoucher mdVoucher = null;
		if (Voucher instanceof MDVoucher) {
			mdVoucher = (MDVoucher) Voucher;
		} else if (Voucher instanceof VoucherVO) {
			return (VoucherVO) Voucher;
		}
		VoucherVO voucherVO = new VoucherVO();
		voucherVO.setAddclass(mdVoucher.getAddclass());
		voucherVO.setAttachment(mdVoucher.getAttachment());
		voucherVO.setContrastflag(mdVoucher.getContrastflag());
		voucherVO.setDeleteclass(mdVoucher.getDeleteclass());
		voucherVO.setDetailmodflag(mdVoucher.getDetailmodflag());
		voucherVO.setDiscardflag(mdVoucher.getDiscardflag());
		voucherVO.setErrmessage(mdVoucher.getErrmessage());

		voucherVO.setExplanation(mdVoucher.getExplanation());
		voucherVO.setFree1(mdVoucher.getFree1());
		voucherVO.setFree10(mdVoucher.getFree10());

		voucherVO.setFree2(mdVoucher.getFree2());
		voucherVO.setFree3(mdVoucher.getFree3());
		voucherVO.setFree4(mdVoucher.getFree4());
		voucherVO.setFree5(mdVoucher.getFree5());
		voucherVO.setFree6(mdVoucher.getFree6());
		voucherVO.setFree7(mdVoucher.getFree7());
		voucherVO.setFree8(mdVoucher.getFree8());
		voucherVO.setFree9(mdVoucher.getFree9());
		voucherVO.setModifyclass(mdVoucher.getModifyclass());
		voucherVO.setModifyflag(mdVoucher.getModifyflag());
		voucherVO.setNo(mdVoucher.getNum());
		voucherVO.setPeriod(mdVoucher.getPeriod());
		voucherVO.setPk_casher(mdVoucher.getPk_casher());
		voucherVO.setPk_checked(mdVoucher.getPk_checked());
		voucherVO.setPk_manager(mdVoucher.getPk_manager());
		voucherVO.setPk_prepared(mdVoucher.getPk_prepared());
		voucherVO.setPk_setofbook(mdVoucher.getPk_setofbook());
		voucherVO.setPk_system(mdVoucher.getPk_system());
		voucherVO.setPk_vouchertype(mdVoucher.getPk_vouchertype());
		voucherVO.setPrepareddate(mdVoucher.getPrepareddate());
		voucherVO.setSignflag(mdVoucher.getSignflag());
		voucherVO.setTallydate(mdVoucher.getTallydate());
		voucherVO.setTotalcredit(mdVoucher.getTotalcredit());
		voucherVO.setTotaldebit(mdVoucher.getTotaldebit());
		voucherVO.setVoucherkind(mdVoucher.getVoucherkind());
		voucherVO.setYear(mdVoucher.getYear());
		voucherVO.setSigndate(mdVoucher.getSigndate());

		voucherVO.setCheckeddate(mdVoucher.getCheckeddate());
		voucherVO.setCreator(mdVoucher.getCreator());
		voucherVO.setTempsaveflag(mdVoucher.getTempsaveflag());

		voucherVO.setFipInfo(mdVoucher.getFipInfo());
		/*
		 * String[] names = mdVoucher.getAttributeNames(); for (String name : names) { voucherVO.setAttributeValue(name, mdVoucher.getAttributeValue(name)); }
		 */
		voucherVO.setFipInfo(mdVoucher.getFipInfo());
//		voucherVO.setControlFlag(mdVoucher.getModifyflag());
		voucherVO.setAttributeValue("aggdetails", mdVoucher.getAggdetails());
		if(setControlFlag) {
			voucherVO.setControlFlag(mdVoucher.getEditflag());
		}
		return voucherVO;
	}

	/**
	 * 
	 * ����˵����
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param mdVoucher
	 * @return
	 * @see
	 * @since V6.0
	 */
	private MDVoucher gl2fip(VoucherVO voucher) {
		MDVoucher voucherVO = new MDVoucher();
		String[] names = voucherVO.getAttributeNames();
		for (String name : names) {
			voucherVO.setAttributeValue(name, voucher.getAttributeValue(name));
		}
		voucherVO.setFipInfo(voucher.getFipInfo());
		voucherVO.setControlFlag(voucher.getEditflag());
		return voucherVO;
	}

	/**
	 * ȥ��Ϊ0�ķ�¼
	 * 
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param voucher
	 * @see
	 * @since V6.0
	 */
	@SuppressWarnings("unchecked")
	private void removeZeroDetail(VoucherVO voucher) throws BusinessException{
		
		//���ŷ�˱����Ƿ�����
		boolean checkEURStart = GLStartCheckUtil.checkEURStart(voucher.getPk_group());
		
		Vector<DetailVO> detailList = voucher.getDetail();
		Vector<DetailVO> subList = new Vector<DetailVO>();
		for (DetailVO detailVO : detailList) {
			if (detailVO.getLocaldebitamount().equals(UFDouble.ZERO_DBL) && detailVO.getLocalcreditamount().equals(UFDouble.ZERO_DBL) && isGroupNull(voucher.getPk_group(),detailVO) && isGlobeNull(detailVO)) {
				// ����Ϊ0��ֻ��������Ч��¼������������¼
				if (detailVO.getDebitquantity().equals(UFDouble.ZERO_DBL) && detailVO.getCreditquantity().equals(UFDouble.ZERO_DBL)) {
					// ���н��Ϊ0����Ч��¼
					// ���������ŷ�˱�������Ҫ����˰����Ƿ�Ϊ�գ����Ϊ������˵��������Ϊ����ͨ��
					if (checkEURStart) {
						VatDetailVO vatdetail = detailVO.getVatdetail();
						if (vatdetail == null) {
							continue;
						} else {
							UFDouble moneyamount = vatdetail.getMoneyamount();
							if (moneyamount == null
									|| UFDouble.ZERO_DBL.equals(moneyamount)) {
								continue;
							}
						}
					} else {
						continue;
					}
				} else {
					// ��������ƾ֤
					voucher.setVoucherkind(3);
				}
			}
			subList.add(detailVO);
		}
		voucher.setDetail(subList);
	}

	private boolean isGroupNull(String pk_group,DetailVO detailVO) {
		boolean startGroupCurr = Currency.isStartGroupCurr(pk_group);
		if(!startGroupCurr) {
			return true;
		}
		if(detailVO.getGroupdebitamount().equals(UFDouble.ZERO_DBL) && detailVO.getGroupcreditamount().equals(UFDouble.ZERO_DBL)) {
			return true;
		}
		return false;
	}
	
	private boolean isGlobeNull(DetailVO detailVO) {
		boolean startGlobalCurr = Currency.isStartGlobalCurr();
		if(!startGlobalCurr) {
			return true;
		}
		if(detailVO.getGlobaldebitamount().equals(UFDouble.ZERO_DBL) && detailVO.getGlobalcreditamount().equals(UFDouble.ZERO_DBL)) {
			return true;
		}
		return false;
	}
	
	/**
	 * ��ʽ�������������ۣ�Ϊ�յ������ֶβ�0
	 * 
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * ֱ�����ɾ��Ȼ�������
	 * 
	 * @param voucher
	 * @param accmap
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	@SuppressWarnings("unchecked")
	private void formatNumberDetail(VoucherVO voucher, HashMap<String, AccountVO> accmap) throws BusinessException {
		Vector<DetailVO> detailList = voucher.getDetail();// checkVoucher�Ѿ��������������ﲻ���п�
		UFDouble value = null;
		Integer digit = 0;
		// ��ʹ��ͳһ��getAttributeValue��ֱ��ʹ��get���� ��ԭ��:get��Ч�ʱ�getAttributeValue�ߵĶ�
		for (DetailVO detailVO : detailList) {
			// �ж��Ƿ����������Ŀ
			AccountVO accvo = accmap.get(detailVO.getPk_accasoa());
			if (accvo != null && accvo.getEnablestate() == IPubEnumConst.ENABLESTATE_ENABLE) {
				if (accvo.getUnit() == null) {
					detailVO.setDebitquantity(UFDouble.ZERO_DBL);
					detailVO.setCreditquantity(UFDouble.ZERO_DBL);
				}
			} else if (accvo != null && accvo.getEnablestate() != IPubEnumConst.ENABLESTATE_ENABLE) {
				throw new BusinessException(accvo.getCode() + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("20021005", "UPP20021005-000632")/* @res "��Ŀ�ѱ���档" */);
			} else {
				if (detailVO.getPk_accasoa() != null && detailVO.getPk_accasoa().trim().length() > 0)
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("glpubprivate_0", "02002003-0008")/* @res "���������Ҳ�����Ӧ�Ŀ�Ŀ������ϵ��������" */+ detailVO.getPk_accasoa());
			}
			value = detailVO.getDebitquantity();
			if (value != null) {
				if (!value.equals(UFDouble.ZERO_DBL)) {
					digit = getQuantityPrecision(detailVO.getAss(), voucher.getPk_accountingbook(), detailVO.getPk_accasoa(), voucher.getPrepareddate().toStdString());
					if (digit == null)
						digit = 4;
					value = value.setScale(Math.abs(digit), UFDouble.ROUND_HALF_UP);
					detailVO.setDebitquantity(value);
				}
			} else {
				value = UFDouble.ZERO_DBL;
				detailVO.setDebitquantity(value);
			}
			value = detailVO.getCreditquantity();
			if (value != null) {
				if (!value.equals(UFDouble.ZERO_DBL)) {
					digit = getQuantityPrecision(detailVO.getAss(), voucher.getPk_accountingbook(), detailVO.getPk_accasoa(), voucher.getPrepareddate().toStdString());
					if (digit == null)
						digit = 4;
					value = value.setScale(Math.abs(digit), UFDouble.ROUND_HALF_UP);
					detailVO.setCreditquantity(value);
				}
			} else {
				value = UFDouble.ZERO_DBL;
				detailVO.setCreditquantity(value);
			}
			value = detailVO.getDebitamount();
			if (value != null) {
				if (!value.equals(UFDouble.ZERO_DBL)) {
//					digit = Currency.getCurrDigit(detailVO.getPk_currtype());
//					if (digit == null)
//						digit = 2;
					value = Currency.formatByCurrType(detailVO.getPk_currtype(),value);
					detailVO.setDebitamount(value);
				}
			} else {
				value = UFDouble.ZERO_DBL;
				detailVO.setDebitamount(value);
			}
			value = detailVO.getCreditamount();
			if (value != null) {
				if (!value.equals(UFDouble.ZERO_DBL)) {
//					digit = Currency.getCurrDigit(detailVO.getPk_currtype());
//					if (digit == null)
//						digit = 2;
					value = Currency.formatByCurrType(detailVO.getPk_currtype(),value);
					detailVO.setCreditamount(value);
				}
			} else {
				value = UFDouble.ZERO_DBL;
				detailVO.setCreditamount(value);
			}
			value = detailVO.getLocaldebitamount();
			if (value != null) {
				if (!value.equals(UFDouble.ZERO_DBL)) {
//					digit = Currency.getCurrDigit(Currency.getLocalCurrPK(voucher.getPk_accountingbook()));
//					if (digit == null)
//						digit = 2;
					value = Currency.formatByCurrType(Currency.getLocalCurrPK(voucher.getPk_accountingbook()),value);
					detailVO.setLocaldebitamount(value);
				}
			} else {
				value = UFDouble.ZERO_DBL;
				detailVO.setLocaldebitamount(value);
			}
			value = detailVO.getLocalcreditamount();
			if (value != null) {
				if (!value.equals(UFDouble.ZERO_DBL)) {
//					digit = Currency.getCurrDigit(Currency.getLocalCurrPK(voucher.getPk_accountingbook()));
//					if (digit == null)
//						digit = 2;
					value = Currency.formatByCurrType(Currency.getLocalCurrPK(voucher.getPk_accountingbook()),value);
					detailVO.setLocalcreditamount(value);
				}
			} else {
				value = UFDouble.ZERO_DBL;
				detailVO.setLocalcreditamount(value);
			}
			value = detailVO.getGroupdebitamount();
			if (value != null) {
				if (!value.equals(UFDouble.ZERO_DBL)) {
//					digit = Currency.getCurrDigit(Currency.getGroupCurrpk(voucher.getPk_group()));
//					if (digit == null)
//						digit = 2;
					value = Currency.formatByCurrType(Currency.getGroupCurrpk(voucher.getPk_group()),value);
					detailVO.setGroupdebitamount(value);
				}
			} else {
				value = UFDouble.ZERO_DBL;
				detailVO.setGroupdebitamount(value);
			}
			value = detailVO.getGroupcreditamount();
			if (value != null) {
				if (!value.equals(UFDouble.ZERO_DBL)) {
//					digit = Currency.getCurrDigit(Currency.getGroupCurrpk(voucher.getPk_group()));
//					if (digit == null)
//						digit = 2;
					value = Currency.formatByCurrType(Currency.getGroupCurrpk(voucher.getPk_group()),value);
					detailVO.setGroupcreditamount(value);
				}
			} else {
				value = UFDouble.ZERO_DBL;
				detailVO.setGroupcreditamount(value);
			}
			value = detailVO.getGlobaldebitamount();
			if (value != null) {
				if (!value.equals(UFDouble.ZERO_DBL)) {
//					digit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));
//					if (digit == null)
//						digit = 2;
					value = Currency.formatByCurrType(Currency.getGlobalCurrPk(null),value);
					detailVO.setGlobaldebitamount(value);
				}
			} else {
				value = UFDouble.ZERO_DBL;
				detailVO.setGlobaldebitamount(value);
				digit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));
			}
			value = detailVO.getGlobalcreditamount();
			if (value != null) {
				if (!value.equals(UFDouble.ZERO_DBL)) {
//					digit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));
//					if (digit == null)
//						digit = 2;
					value = Currency.formatByCurrType(Currency.getGlobalCurrPk(null),value);
					detailVO.setGlobalcreditamount(value);
				}
			} else {
				value = UFDouble.ZERO_DBL;
				detailVO.setGlobalcreditamount(value);
			}
			value = detailVO.getPrice();
			if (value != null) {
				if (!value.equals(UFDouble.ZERO_DBL)) {
					int[] unitDigitAndRoundType = GLParaAccessor.getPricePrecision(detailVO.getPk_currtype());
					value = value.setScale(unitDigitAndRoundType[0],unitDigitAndRoundType[1]);
					detailVO.setPrice(value);
				}
			} else {
				value = UFDouble.ZERO_DBL;
				detailVO.setPrice(value);
			}
		}
	}
		
    public Integer getQuantityPrecision(AssVO[] assVOs,String pk_accountingbook, String pk_accasoa, String stddate) {
    	String pk_unit = getMaterialUnit(assVOs);
    	if(StringUtils.isNotEmpty(pk_unit)) {
    		return getQuantityDigitByUnit(pk_unit);
    	}else {
    		return getQuantityPrecision(pk_accountingbook,pk_accasoa,stddate);
    	}
    }
    
    /**
     * 
     * ��ȡ��������
     * <p>�޸ļ�¼��</p>
     * @param pk_accasoa
     * @param stddate
     * @return
     * @see 
     * @since V6.0
     * @hurh
     */
    public Integer getQuantityPrecision(String pk_accountingbook, String pk_accasoa, String stddate){
    	Integer precision = ICurrtypeConst.CURRTYPE_DEFAULT_DIGIT;
    	if(pk_accountingbook == null || pk_accasoa == null)
    		return precision;
		try {
			AccountVO account = AccountUtilGL.queryAccountVosByPks(new String[]{pk_accasoa}, stddate, false)[0];
			if(account == null){
				return precision;
			} else if(account.getUnit()!=null){
				Integer[] amountDigits = MeasdocUtil.getInstance().getPrecisionByPks(new String[]{account.getUnit()});
				if(amountDigits != null && amountDigits.length > 0 && amountDigits[0] != null){
					precision = amountDigits[0];
				}
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		return precision;
    }
	
	/**
     * ���ݸ��������ȡ���ϵ���������λ
     * @param assVOs
     * @return
     */
    public String getMaterialUnit(AssVO[] assVOs) {
    	String pk_checkvalue = null;
    	if(assVOs != null && assVOs.length >0) {
    		for(AssVO assVo:assVOs) {
    			if(IBDMetaDataIDConst.MATERIAL_V.equals(assVo.getM_classid())) {
    				pk_checkvalue = assVo.getPk_Checkvalue();
    				break;
    			}
    		}
    	}
    	//��������ϸ��������������ϵ���������λ
    	if(StringUtils.isNotEmpty(pk_checkvalue) && !StrTools.NULL.equals(pk_checkvalue)) {
    		try {
    			Object[] measdoc = BDCacheQueryUtil.queryVOsByIDs(MaterialVO.class, MaterialVO.PK_MATERIAL, 
    					new String[]{pk_checkvalue}, new String[]{MaterialVO.PK_MEASDOC});
    			MaterialVO[] materialVOs = (MaterialVO[]) measdoc;
    			String pk_measdoc = materialVOs[0].getPk_measdoc();
    			return pk_measdoc;
    		} catch (BusinessException e) {
    			Logger.error(e);
    		}
    	}
    	return null;
    }
    
    /**
     * 
     * ��ȡ��������
     * <p>�޸ļ�¼��</p>
     * @param pk_accasoa
     * @param stddate
     * @return
     * @see 
     * @since V6.0
     * @hurh
     */
    public Integer getQuantityDigitByUnit(String pk_unit){
    	Integer precision = ICurrtypeConst.CURRTYPE_DEFAULT_DIGIT;
    	if(pk_unit == null)
    		return precision;
		try {
			Integer[] amountDigits = MeasdocUtil.getInstance().getPrecisionByPks(new String[]{pk_unit});
			if(amountDigits != null && amountDigits.length > 0 && amountDigits[0] != null){
				precision = amountDigits[0];
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		return precision;
    }
}