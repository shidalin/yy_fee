/**
 * 
 */
package nc.impl.fip.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import nc.itf.fip.message.IFipMessage;
import nc.itf.fip.pub.FipInterfaceCenter;
import nc.pubitf.fip.external.IDesBillService;
import nc.pubitf.fip.external.IDesBillSumService;
import nc.vo.fip.external.FipBillSumRSVO;
import nc.vo.fip.external.FipSaveResultVO;
import nc.vo.fip.operatinglogs.OperatingFlagEmu;
import nc.vo.fip.operatinglogs.OperatingLogVO;
import nc.vo.fip.service.FipBasicRelationVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.fip.trans.FipTransVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * <p>
 * TODO 接口/类功能说明，使用说明（接口是否为服务组件，服务使用者，类是否线程安全等）。
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 * 
 * @see
 * @author gbh
 * @version V6.0
 * @since V6.0 创建时间：2011-5-21 下午03:55:31
 */
public class FipMessageImpl implements IFipMessage {

	/*
	 * saveDesBill_RequiresNew方法在FipMessageImpl中的实现
	 * 
	 * @see nc.itf.fip.message.IFipMessage#saveDesBill_RequiresNew(nc.vo.fip.trans.FipTransVO)
	 */
	@Override
	public List<OperatingLogVO> saveDesBill_RequiresNew(List<FipTransVO> rslist) throws BusinessException {
		if (rslist == null || rslist.isEmpty())
			return null;
		List<OperatingLogVO> volist = new ArrayList<OperatingLogVO>();
		// 按目标单据分组
		HashMap<String, List<FipTransVO>> billtypemap = new HashMap<String, List<FipTransVO>>();
		for (FipTransVO fipTransVO : rslist) {
			if (fipTransVO.getDatavo() == null) {
				// 过滤掉合计金额为0或没有生成凭证的，如果想让该类凭证进平台日志或提示，则抛异常即可
				// throw new BusinessException("单据" + fipTransVO.getMessagevo().getSrcRelation().getFreedef1() + "没有生成凭证，可能是因为合计金额为0。");
				continue;
			}
			String pkBilltype = fipTransVO.getMessagevo().getDesRelation().getPk_billtype();
			List<FipTransVO> list = billtypemap.get(pkBilltype);
			if (list == null) {
				list = new ArrayList<FipTransVO>();
				billtypemap.put(pkBilltype, list);
			}
			list.add(fipTransVO);
		}
		Collection<List<FipTransVO>> values = billtypemap.values();
		if (values != null && !values.isEmpty()) {
			for (List<FipTransVO> list : values) {
				if (list == null || list.isEmpty())
					continue;
				List<OperatingLogVO> savelist = saveDesBill(list);
				if (savelist != null && !savelist.isEmpty())
					volist.addAll(savelist);
			}
		}
		return volist;
	}

	/**
	 * 保存目标单据，并返回关联关系（同一批次只能处理相同目标单据类型的数据）
	 * 
	 * <p>
	 * 修改记录：
	 * </p>
	 * 
	 * @param rslist
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private List<OperatingLogVO> saveDesBill(List<FipTransVO> rslist) throws BusinessException {
		if (rslist == null || rslist.isEmpty())
			return null;

		String pk_group = null;
		String pk_billtype = null;
		for (FipTransVO fipTransVO : rslist) {
			FipBasicRelationVO messagevo = fipTransVO.getMessagevo();
			FipRelationInfoVO desRelation = messagevo.getDesRelation();
			if (desRelation != null) {
				pk_group = desRelation.getPk_group();
				pk_billtype = desRelation.getPk_billtype();
				if (pk_group != null && pk_billtype != null)
					break;
			}
		}

		IDesBillService ib = FipInterfaceCenter.getDesBillService(pk_group, pk_billtype);
		if (ib == null)
			throw new BusinessException(FipInterfaceCenter.getNoInterfaceFoundMessage(pk_billtype, "IDesBillService"));

		List<FipTransVO> confirmlist = new ArrayList<FipTransVO>();// 正式单据
		List<FipTransVO> savelist = new ArrayList<FipTransVO>();// 临时单据
		if (ib.needConfirm() != null && ib.needConfirm().booleanValue()) {
			// 是否需要自动确认
			for (FipTransVO fipTransVO : rslist) {
				UFBoolean isAutoConfirm = fipTransVO.getIsAutoConfirm();
				boolean autoconfirm = isAutoConfirm == null ? false : isAutoConfirm.booleanValue();
				if (autoconfirm)
					confirmlist.add(fipTransVO);
				else
					savelist.add(fipTransVO);
			}
		} else {
			confirmlist = rslist;
		}

		List<OperatingLogVO> rs = new ArrayList<OperatingLogVO>();
		if (!confirmlist.isEmpty()) {
			// 正式单据 直接生成正式单据的，如果目标单据有合并服务，则在保存前需要做一次合并
			IDesBillSumService desBillSumService = FipInterfaceCenter.getDesBillSumService(pk_group, pk_billtype);
			if (desBillSumService != null) {
				for (FipTransVO vo : confirmlist) {
					if (vo.getDatavo() == null) {
						continue;
					}
					FipBillSumRSVO svo = new FipBillSumRSVO();
					svo.setBillVO(vo.getDatavo());
					svo.setMessageinfo(vo.getMessagevo().getDesRelation());
					ArrayList<FipBillSumRSVO> volist = new ArrayList<FipBillSumRSVO>();
					volist.add(svo);
					Collection<FipBillSumRSVO> sumvolist = desBillSumService.querySumBill(volist, null);
					if (sumvolist != null && !sumvolist.isEmpty()) {
						FipBillSumRSVO ssvo = sumvolist.iterator().next();
						if (ssvo != null) {
							vo.setDatavo(ssvo.getBillVO());
							vo.getMessagevo().setDesRelation(ssvo.getMessageinfo());
						} else {
							vo.setDatavo(null);
						}
					} else {
						vo.setDatavo(null);
					}
				}
			}
			List<FipSaveResultVO> volist = new ArrayList<FipSaveResultVO>();
			List<FipBasicRelationVO> msglist = new ArrayList<FipBasicRelationVO>();
			for (FipTransVO vo : confirmlist) {
				FipBasicRelationVO messagevo = vo.getMessagevo();
				if (vo.getDatavo() == null) {
					// 过滤掉合计金额为0或没有生成凭证的，如果想让该类凭证进平台日志或提示，则抛异常即可
					// throw new BusinessException("单据" + messagevo.getSrcRelation().getFreedef1() + "没有生成凭证，可能是因为合计金额为0。");
					// OperatingLogVO oplogvo = messagevo.convertToOperatingLogVO();
					// oplogvo.setOperateflag(OperatingFlagEmu.FLAG_error_on_generate);
					// oplogvo.setOpmessage("单据" + messagevo.getSrcRelation().getFreedef1() + "没有生成凭证，可能是因为合计金额为0。");
					// rs.add(oplogvo);
				} else {
					FipSaveResultVO savevo = new FipSaveResultVO();
					savevo.setBillVO(vo.getDatavo());
					savevo.setMessageinfo(messagevo.getDesRelation());
					savevo.getMessageinfo().setPk_system(messagevo.getSrcRelation().getPk_system());
					volist.add(savevo);
					msglist.add(messagevo);
				}
			}
			Collection<FipSaveResultVO> rsvo2 = ib.confirmBill(volist);
			int index = 0;
			for (FipSaveResultVO fipSaveResultVO : rsvo2) {
				FipBasicRelationVO messagevo = msglist.get(index);
				index++;
				if (fipSaveResultVO == null || fipSaveResultVO.getBillVO() == null || fipSaveResultVO.getMessageinfo() == null) {
					// 过滤掉合计金额为0或没有生成凭证的，如果想让该类凭证进平台日志或提示，则抛异常即可
					// throw new BusinessException("单据" + messagevo.getSrcRelation().getFreedef1() + "没有生成凭证，可能是因为合计金额为0。");
					// OperatingLogVO oplogvo = messagevo.convertToOperatingLogVO();
					// oplogvo.setOperateflag(OperatingFlagEmu.FLAG_error_on_generate);
					// oplogvo.setOpmessage("单据" + messagevo.getSrcRelation().getFreedef1() + "没有生成凭证，可能是因为合计金额为0。");
					// rs.add(oplogvo);
				} else {
					messagevo.setDesRelation(fipSaveResultVO.getMessageinfo());
					OperatingLogVO oplogvo = messagevo.convertToOperatingLogVO();
					oplogvo.setOperateflag(OperatingFlagEmu.FLAG_confirmed);
					rs.add(oplogvo);
				}
			}
		}

		if (!savelist.isEmpty()) {
			// 临时单据 临时单据不需要特殊处理，直接保存即可
			List<FipSaveResultVO> volist = new ArrayList<FipSaveResultVO>();
			List<FipBasicRelationVO> msglist = new ArrayList<FipBasicRelationVO>();
			for (FipTransVO vo : savelist) {
				FipBasicRelationVO messagevo = vo.getMessagevo();
				if (vo.getDatavo() == null) {
					// 过滤掉合计金额为0或没有生成凭证的，如果想让该类凭证进平台日志或提示，则抛异常即可
					// throw new BusinessException("单据" + messagevo.getSrcRelation().getFreedef1() + "没有生成凭证，可能是因为合计金额为0。");
					// OperatingLogVO oplogvo = messagevo.convertToOperatingLogVO();
					// oplogvo.setOperateflag(OperatingFlagEmu.FLAG_error_on_generate);
					// oplogvo.setOpmessage("单据" + messagevo.getSrcRelation().getFreedef1() + "没有生成凭证，可能是因为合计金额为0。");
					// rs.add(oplogvo);
				} else {
					FipSaveResultVO savevo = new FipSaveResultVO();
					savevo.setBillVO(vo.getDatavo());
					savevo.setMessageinfo(messagevo.getDesRelation());
					savevo.getMessageinfo().setPk_system(messagevo.getSrcRelation().getPk_system());
					volist.add(savevo);
					msglist.add(messagevo);
				}
			}
			if (!volist.isEmpty()) {
				Collection<FipSaveResultVO> rsvo2 = ib.saveBill(volist);
				int index = 0;
				for (FipSaveResultVO fipSaveResultVO : rsvo2) {
					FipBasicRelationVO messagevo = msglist.get(index);
					index++;
					if (fipSaveResultVO == null || fipSaveResultVO.getBillVO() == null || fipSaveResultVO.getMessageinfo() == null) {
						// 过滤掉合计金额为0或没有生成凭证的，如果想让该类凭证进平台日志或提示，则抛异常即可
						// throw new BusinessException("单据" + messagevo.getSrcRelation().getFreedef1() + "没有生成凭证，可能是因为合计金额为0。");
						// OperatingLogVO oplogvo = messagevo.convertToOperatingLogVO();
						// oplogvo.setOperateflag(OperatingFlagEmu.FLAG_error_on_generate);
						// oplogvo.setOpmessage("单据" + messagevo.getSrcRelation().getFreedef1() + "没有生成凭证，可能是因为合计金额为0。");
						// rs.add(oplogvo);
					} else {
						messagevo.setDesRelation(fipSaveResultVO.getMessageinfo());
						OperatingLogVO oplogvo = messagevo.convertToOperatingLogVO();
						oplogvo.setOperateflag(OperatingFlagEmu.FLAG_saved);
						rs.add(oplogvo);
					}
				}
			}
		}

		return rs;
	}

}
