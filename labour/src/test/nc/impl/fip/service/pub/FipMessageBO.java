/**
 *
 */
package nc.impl.fip.service.pub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.BillTypeCacheKey;
import nc.bs.pf.pub.PfDataCache;
import nc.impl.fip.message.FipMessageImpl;
import nc.impl.fip.messagelogs.FipMessageLogsProxy;
import nc.impl.fip.operatinglog.FipOperatingLogProxy;
import nc.impl.fip.relation.FipRelationProxy;
import nc.impl.fip.sumrelation.FipSumRelationProxy;
import nc.itf.fip.cache.memcache.FipConfigCache;
import nc.itf.fip.message.IFipMessage;
import nc.itf.fip.messageprocess.MessageProcessCenter;
import nc.itf.fip.opreatinglog.IOperatingLog;
import nc.itf.fip.pub.FipBillVOAdapter;
import nc.itf.fip.pub.FipInterfaceCenter;
import nc.itf.fip.pub.formula.FipFormulaParser;
import nc.itf.fip.relation.IFipRelation;
import nc.itf.fip.sumrelation.IFipSumRelation;
import nc.itf.fip.trans.BOTranslateCenter;
import nc.pub.fip.pubtools.FipBSCacheCleaner;
import nc.pubitf.fip.external.IBillReflectorService;
import nc.pubitf.fip.external.IBillSumService;
import nc.pubitf.fip.external.IFipMessageHook;
import nc.vo.fip.config.ConfigVO;
import nc.vo.fip.config.ProcessConfigVO;
import nc.vo.fip.external.FipBillSumRSVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.messagelogs.MessageLogVO;
import nc.vo.fip.operatinglogs.OperatingFlagEmu;
import nc.vo.fip.operatinglogs.OperatingLogVO;
import nc.vo.fip.pub.SqlTools;
import nc.vo.fip.relation.FipRelationVO;
import nc.vo.fip.service.FipBasicRelationVO;
import nc.vo.fip.service.FipExtRelationVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipMsgResultVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.fip.sumrelation.SumRelationVO;
import nc.vo.fip.trans.FipTransVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

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
 * @since V6.0 ����ʱ�䣺2010-3-25 ����02:42:45
 */
public class FipMessageBO {
	public FipMsgResultVO[] sendMessages(FipMessageVO[] fipmessagevos) throws BusinessException {
		FipBSCacheCleaner.clearAllCache();
		FipMsgResultVO[] rs = null;
		if (fipmessagevos != null && fipmessagevos.length > 0) {
			long start_time = System.currentTimeMillis();
			// �����Ϣ�ĺϷ���
			checkMessages(fipmessagevos);
			// ��¼��Ϣ��־
			logMessages(fipmessagevos);

			Logger.debug("#FIP DEBUG#" + "��ʼ���ƽ̨��ش���");
			Vector<IFipMessageHook> messagehookvec = null;
			{
				// need added ע����Ϣ�����ӿ�FipMessageVO nc.pubitf.fip.external.IFipMessageHook.beforeDispose(FipMessageVO vo) throws BusinessException
				messagehookvec = new Vector<IFipMessageHook>();
				try {
					// BusiTransVO[] bst = null;// TaskRedirectProxy.queryBusiTransVO(null, "dap", "delete");
					// if (bst != null && bst.length > 0) {
					// for (int i = 0; i < bst.length; i++) {
					// Logger.debug("#FIP DEBUG#" + "���ƽ̨������Ϣע��ӿڣ�ģ�飺" + bst[i].note + "��ʵ���ࣺ" + bst[i].className);
					// IFipMessageHook m_bstclass = (IFipMessageHook) NewObjectService.newInstance(bst[i].note, bst[i].className);
					// messagehookvec.addElement(m_bstclass);
					// }
					// }
				} catch (ClassCastException e) {
					Logger.error("nc.pubitf.fip.external.IFipMessageHook����ʧ�ܣ�ע���ʵ����û��ʵ��nc.pubitf.fip.external.IFipMessageHook�ӿڣ�������", e);
				} catch (NoClassDefFoundError e) {
					Logger.error("nc.pubitf.fip.external.IFipMessageHookʵ��û���ҵ�������û�а�װ��ص�ϵͳģ�飬������", e);
				} catch (Exception e) {
					Logger.error("nc.pubitf.fip.external.IFipMessageHookʵ����ʧ�ܣ���ʼ��ʱ�����쳣��������", e);
				}
				if (messagehookvec != null && messagehookvec.size() > 0) {
					for (int i = 0; i < messagehookvec.size(); i++) {
						messagehookvec.elementAt(i).beforeDispose(fipmessagevos);
					}
				}
			}

			// ������Ϣ
			rs = processMessages(fipmessagevos);

			{
				// ע����Ϣ�����ӿ�void nc.pubitf.fip.external.IFipMessageHook.afterDisPose(FipMessageVO vo) throws BusinessException
				if (messagehookvec != null && messagehookvec.size() > 0) {
					// ���������Է�װ�����⣬afterDisPose�ĵ���˳��Ӧ����beforeDispose�ĵ���˳�����෴��
					for (int i = messagehookvec.size(); i > 0; i--) {
						messagehookvec.elementAt(i - 1).afterDisPose(fipmessagevos);
					}
				}
			}
			Logger.debug("#FIP DEBUG#" + "���ƽ̨��ش������");
			Logger.debug("#FIP DEBUG#" + "���ƽ̨��ش�������ʱ��" + (System.currentTimeMillis() - start_time));
		}
		return rs;
	}

	/**
	 * ������Ϣ��������Ϊ��
	 * 
	 * @param fipmessagevo
	 * @return
	 * @throws BusinessException
	 */
	private FipMsgResultVO[] processMessages(FipMessageVO[] fipmessagevo) throws BusinessException {
		FipMsgResultVO[] rs = null;
		ArrayList<FipMessageVO> addlist = new ArrayList<FipMessageVO>();
		ArrayList<FipMessageVO> dellist = new ArrayList<FipMessageVO>();
		for (int i = 0; i < fipmessagevo.length; i++) {
			if (fipmessagevo[i].getMessagetype() == FipMessageVO.MESSAGETYPE_ADD) {
				addlist.add(fipmessagevo[i]);
			} else if (fipmessagevo[i].getMessagetype() == FipMessageVO.MESSAGETYPE_DEL) {
				dellist.add(fipmessagevo[i]);
			}
		}
		if (!addlist.isEmpty() && !dellist.isEmpty()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0038")/* @res "���ƽ̨���治֧�����Ӻ�ɾ����һ��ҵ���д�����ֿ����ͻ��ƽ̨��" */);
		} else {
			if (!addlist.isEmpty()) {
				List<FipMsgResultVO> list = addMessageArray(addlist);
				if (list != null && !list.isEmpty()) {
					rs = new FipMsgResultVO[list.size()];
					list.toArray(rs);
				}
			} else if (!dellist.isEmpty()) {
				deleteMessageArray(dellist);
			}
		}
		return rs;
	}

	/**
	 * �����Ϣ�ĺϷ���
	 * 
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param fipmessagevos
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private void checkMessages(FipMessageVO[] fipmessagevos) throws BusinessException {
		for (FipMessageVO fipmessagevo : fipmessagevos) {
			FipRelationInfoVO infovo = fipmessagevo.getMessageinfo();
			if (infovo == null)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0039")/* @res "Messageinfo������Ϊ��" */);
			if (infovo.getPk_group() == null)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0040")/* @res "���Ų�����Ϊ��" */);
			if (infovo.getPk_org() == null)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0041")/* @res "��֯������Ϊ��" */);
			if (infovo.getPk_system() == null)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0042")/* @res "��Դϵͳ������Ϊ��" */);
			if (infovo.getPk_billtype() == null)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0043")/* @res "�������Ͳ�����Ϊ��" */);
			if (infovo.getPk_operator() == null)
				infovo.setPk_operator(InvocationInfoProxy.getInstance().getUserId());
			if (infovo.getPk_operator() == null)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0044")/* @res "����Ա������Ϊ��" */);
			if (infovo.getBusidate() == null)
				infovo.setBusidate(new UFDate());
			if (fipmessagevo.getMessagetype() == FipMessageVO.MESSAGETYPE_ADD) {
				if (infovo.getFreedef3() == null || infovo.getFreedef3().trim().length() == 0) {
					infovo.setFreedef3("0.00");
				} else {
					new UFDouble(infovo.getFreedef3());
				}
			}
		}
	}

	/**
	 * ��¼��Ϣ��־
	 * 
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param fipmessagevos
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private void logMessages(FipMessageVO[] fipmessagevos) throws BusinessException {
		// ���ȣ��ȼ�¼һ����־����ʾ����Ϣ�Ѿ������˻��ƽ̨
		MessageLogVO[] logvos = new MessageLogVO[fipmessagevos.length];
		for (int i = 0; i < logvos.length; i++) {
			logvos[i] = fipmessagevos[i].convertToMessageLogVO();
		}
		new FipMessageLogsProxy().insert(logvos);
	}

	/**
	 * ����˵����
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param fipmessagevo
	 * @return
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	// private FipMsgResultVO addMessage(FipMessageVO fipmessagevo) throws BusinessException {
	// FipMsgResultVO rs = null;
	// FipExtRelationVO[] dmvs = null;
	// ProcessConfigVO[] addmsg = null;
	// {
	// // need added ͨ��ƽ̨���ã��������ݣ�����/����/��ʽ����ȷ�����ݵ�����ϵͳ������/���λ��/�ɱ�
	// // need added ��ʽ���ɡ������� ����������ĵ����������Ӧ�÷����ˣ���Ӧ�ü���������
	// ProcessConfigVO[] vos = processByConfig(fipmessagevo);
	// if (vos != null) {
	// Vector<ProcessConfigVO> vec = new Vector<ProcessConfigVO>();
	// for (int i = 0; i < vos.length; i++) {
	// if (!vos[i].isGenerateflag())
	// continue;
	// if (vos[i].getGeneratemode() == 1)
	// continue;
	// vec.addElement(vos[i]);
	// }
	// if (vec.size() > 0) {
	// addmsg = new ProcessConfigVO[vec.size()];
	// vec.copyInto(addmsg);
	// }
	// }
	// }
	// if (addmsg == null) {
	// // û����Ҫ��ʽ���ɵĵ���
	// return null;
	// }
	// for (int j = 0; j < addmsg.length; j++) {
	// {
	// // ����������
	// long s_time = System.currentTimeMillis();
	// dmvs = MessageProcessCenter.processMessage(fipmessagevo, addmsg[j], "fip", "nc.itf.fip.messageprocess.DefaultMessageProcessor");
	// long e_time = System.currentTimeMillis();
	// Logger.debug("#FIP DEBUG#" + "�������������ʱ��" + (e_time - s_time));
	// }
	// {
	// if (dmvs != null && dmvs.length > 0) {
	// for (int i = 0; i < dmvs.length; i++) {
	// if (dmvs == null)
	// continue;
	// FipMsgResultVO vo = addMessageAndSaveBill(dmvs[i], fipmessagevo);
	// if (rs == null)
	// rs = vo;
	// else {
	// rs.addRelations(vo.getRelations());
	// }
	// }
	// } else {
	// throw new BusinessException("ƽ̨����������Ŀ�굥�ݣ�������������û��������Ч��Ŀ������֯��");
	// }
	// }
	// }
	//
	// return rs;
	// }

	/**
	 * ����˵����
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param fipRelationVO
	 * @param fipmessagevo
	 * @return
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private FipMsgResultVO addMessageAndSaveBill(FipExtRelationVO fipRelationVO, FipMessageVO fipmessagevo) throws BusinessException {
		if (fipRelationVO == null)
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0045")/* @res "����Ĳ���fipRelationVO�����������̸ò���������Ϊ�ա�" */);
		FipMsgResultVO rs = null;
		FipBasicRelationVO relation = null;
		{
			// ��һ������������Ҫ�������ɵĵ��ݷŵ����ܴ������
			if (fipRelationVO.getIssum() != null && fipRelationVO.getIssum().booleanValue()) {
				// ��Ҫ���ܵ����ݣ�����ȴ����ܵ���Ϣ��
				FipSumRelationProxy ip = new FipSumRelationProxy();
				ip.insert(new SumRelationVO[] { fipRelationVO.convertToSumRelationVO() });
				return rs;
			} else {
				// ����Ҫ���ܵ����ݣ��������������
			}
		}
		OperatingLogVO oplogvo = null;
		IOperatingLog ip = new FipOperatingLogProxy();
		// {
		// // �ڶ���������Ϣ���뵽����״̬���У����Ϊδ����
		// oplogvo = fipRelationVO.convertToOperatingLogVO();
		// oplogvo.setOperateflag(0);
		// }
		{
			// ������������Ϣ����Ϊ������
			// oplogvo.setOperateflag(1);
			// ���е��ݵ�ת��
			FipTransVO transvo = new FipTransVO();
			transvo.setDatavo(fipmessagevo.getBillVO());
			transvo.setMessagevo(fipRelationVO);
			List<FipTransVO> rslist = BOTranslateCenter.translate(transvo);
			if (rslist == null || rslist.isEmpty()) {
				// ��ƾ֤ģ��Ϊ��ʱ���ɣ��������ɵ�ʱ�򣬲�����������ת����Ľ��
				return rs;
			}
			// ���Ĳ�������ת����ĵ���
			FipTransVO rsvo = rslist.get(0);
			if (rsvo.getMessagevo().getOpmessage() == null) {
				if (rsvo.getIsEffect() != null && rsvo.getIsEffect().booleanValue()) {
					// Ӱ�����ҵ��
					List<OperatingLogVO> volist = new FipMessageImpl().saveDesBill_RequiresNew(rslist);
					if (volist != null && !volist.isEmpty())
						oplogvo = volist.get(0);
				} else {
					try {
						IFipMessage ip1 = NCLocator.getInstance().lookup(IFipMessage.class);
						List<OperatingLogVO> volist = ip1.saveDesBill_RequiresNew(rslist);
						if (volist != null && !volist.isEmpty())
							oplogvo = volist.get(0);
					} catch (BusinessException e) {
						Logger.error("", e);
						oplogvo = fipRelationVO.convertToOperatingLogVO();
						oplogvo.setOpmessage(e.getMessage());
						oplogvo.setOperateflag(OperatingFlagEmu.FLAG_no_process);
					}
				}
			} else {
				oplogvo = rsvo.getMessagevo().convertToOperatingLogVO();
				oplogvo.setOperateflag(OperatingFlagEmu.FLAG_no_process);
			}
		}
		if (oplogvo == null) {
			// �ϼƽ��Ϊ0��û�б��浥��
		} else {
			if (oplogvo.getOpmessage() == null)
				relation = FipBasicRelationVO.convertFromOperatingVO(oplogvo);
			if (oplogvo.getDes_relationid() != null) {
				if (oplogvo.getOperateflag() != OperatingFlagEmu.FLAG_confirmed) {
					ip.insert(new OperatingLogVO[] { oplogvo });
				} else {
					IFipRelation ip1 = new FipRelationProxy();
					ip1.insert(new FipRelationVO[] { relation.convertToRelationVO() });
				}
			}
		}
		rs = new FipMsgResultVO();
		rs.addRelation(relation);
		return rs;
	}

	private ProcessConfigVO[] processByConfig(FipMessageVO fipmessagevo) throws BusinessException {
		// ���ݻ��ƽ̨���ý��д���
		ProcessConfigVO[] processedvos = null;
		ConfigVO[] vos = FipConfigCache.getCacheData(fipmessagevo.getMessageinfo().getPk_group(), fipmessagevo.getMessageinfo().getPk_billtype(), true); // �Զ�������Ҫ�ļ�¼
		if (vos != null && vos.length > 0) {
			ArrayList<ProcessConfigVO> list = new ArrayList<ProcessConfigVO>();
			for (int i = 0; i < vos.length; i++) {
				ConfigVO configVO = vos[i];
				BilltypeVO billType = PfDataCache.getBillType(new BillTypeCacheKey().buildBilltype(configVO.getSrc_billtype()).buildPkGroup(fipmessagevo.getMessageinfo().getPk_group()));
				if (billType == null)
					continue;
				ProcessConfigVO processConfigVO = new ProcessConfigVO();
				processConfigVO.setGeneratemode(configVO.getGeneratemode());
				processConfigVO.setIssum(configVO.getIssum() == null ? false : configVO.getIssum().booleanValue());
				processConfigVO.setGenerateflag(false);
				UFBoolean isaccount = billType.getIsaccount();// �Ƿ��ͻ��ƽ̨
				if (isaccount != null && isaccount.booleanValue()) {
					if (configVO.getLeachformula() == null || configVO.getLeachformula().trim().length() == 0) {
						processConfigVO.setGenerateflag(configVO.getGenerateflag() == null ? false : configVO.getGenerateflag() == 1);
					} else {
						// ��Ҫ�жϹ�ʽ��ֵ
						boolean gf = configVO.getGenerateflag() == null ? false : configVO.getGenerateflag() == 1;
						Object billvo = fipmessagevo.getBillVO();
						int loopcount = 1;
						int rowcount = FipBillVOAdapter.getChildCount(billvo);
						loopcount = rowcount > 0 ? rowcount : 1;
						for (int j = 0; j < loopcount; j++) {
							Object o = FipFormulaParser.getInstance().getParseValue(null, null, configVO.getLeachformula(), billvo, null, j);
							if (o != null) {
								if (UFBoolean.valueOf(o.toString()).booleanValue()) {
									processConfigVO.setGenerateflag(true);
									break;
								}
							}
						}
						if (!gf) {
							processConfigVO.setGenerateflag(!processConfigVO.isGenerateflag());
						}
					}
				}
				processConfigVO.setPk_group(configVO.getPk_group());
				processConfigVO.setSrc_billtype(configVO.getSrc_billtype());
				processConfigVO.setDes_billtype(configVO.getDes_billtype());
				processConfigVO.setPk_system(billType.getSystemcode());
				processConfigVO.setModulecode(processConfigVO.getPk_system());
				list.add(processConfigVO);
			}
			if (!list.isEmpty()) {
				processedvos = new ProcessConfigVO[list.size()];
				list.toArray(processedvos);
			}
		} else {
			Logger.debug("ƽ̨������û�й��ڸõ������͵ľ������ã������޷�ͨ�����ƽ̨�������ε����ε��ݣ�������ƽ̨��ƽ̨���ã��������ͣ�" + fipmessagevo.getMessageinfo().getPk_billtype());
		}
		return processedvos;
	}

	/**
	 * ����ɾ����Ϣ
	 * 
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param fipmessagevo
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private ArrayList<FipMsgResultVO> deleteMessageArray(ArrayList<FipMessageVO> fipmessagevo) throws BusinessException {
		HashMap<String, List<FipMessageVO>> billmap = new HashMap<String, List<FipMessageVO>>();
		ArrayList<FipMsgResultVO> rs = null;
		// �ֵ������ͽ���ɾ��
		for (FipMessageVO fipMessageVO2 : fipmessagevo) {
			String pkBilltype = fipMessageVO2.getMessageinfo().getPk_billtype();
			List<FipMessageVO> list = billmap.get(pkBilltype);
			if (list == null) {
				list = new ArrayList<FipMessageVO>();
				billmap.put(pkBilltype, list);
			}
			list.add(fipMessageVO2);
		}
		Set<Entry<String, List<FipMessageVO>>> entrySet = billmap.entrySet();
		for (Entry<String, List<FipMessageVO>> entry : entrySet) {
			deleteMessagePerType(entry.getKey(), entry.getValue());
		}
		return rs;
	}

	/**
	 * ����������ɾ����Ϣ
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param billtypecode
	 * @param list
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private void deleteMessagePerType(String billtypecode, List<FipMessageVO> list) throws BusinessException {
		// ��Ϊ60��һ��ĵ�����Ч��һ����Ч����֧�ֶ����Ч�����Է���ЧҲ��һ�Σ�����Ч��ͬʱɾ���������ɹ���Ŀ�굥��
		if (list == null || list.isEmpty())
			return;
		ArrayList<String> idlist = new ArrayList<String>();
		for (FipMessageVO fipMessageVO2 : list) {
			FipRelationInfoVO vo = fipMessageVO2.getMessageinfo();
			if (!idlist.contains(vo.getRelationID()))
				idlist.add(vo.getRelationID());
		}
		int size = idlist.size();
		if (size == 0)
			return;
		// ��ΪUAP����ÿ��ֻ�ܶ�10���¼�����Խ��з���
		int loopstep = 5000;
		int loopcount = (size - 1) / loopstep + 1;
		for (int i = 0; i < loopcount; i++) {
			int fromIndex = i * loopstep;
			int toIndex = (i == loopcount - 1 ? size : (fromIndex + loopstep));
			List<String> subList = idlist.subList(fromIndex, toIndex);// ��Ҫע��toIndexָ���ǽ�ֹλ�ã������λ�ò�����toIndex�Լ���Ҳ����˵��������0-499����toIndex��Ҫ��500
			String wherepart = OperatingLogVO.SRC_BILLTYPE + "='" + billtypecode + "' and " + SqlTools.getInStr(OperatingLogVO.SRC_RELATIONID, subList, true);
			deleteSumRelationByCon(wherepart);
			deleteOperatinglogByCon(wherepart);
			deleteRelationByCon(wherepart);
		}
	}

	private void deleteSumRelationByCon(String con) throws BusinessException {
		IFipSumRelation ip2 = new FipSumRelationProxy();
		SumRelationVO[] sumvos = ip2.queryByWhere(con);
		if (sumvos != null) {
			ip2.delete(sumvos);
		}
	}

	private void deleteOperatinglogByCon(String con) throws BusinessException {
		IOperatingLog ip = new FipOperatingLogProxy();
		OperatingLogVO[] oplogs = ip.queryByWhere(con);
		if (oplogs != null) {
			ip.delete(oplogs);
		}
	}

	private void deleteRelationByCon(String con) throws BusinessException {
		IFipRelation ip1 = new FipRelationProxy();
		FipRelationVO[] relations = ip1.queryByWhere(con);
		if (relations != null) {
			ip1.deleteReal(relations);
		}
	}

	/**
	 * ����������Ϣ
	 * 
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param fipmessagevo
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private List<FipMsgResultVO> addMessageArray(List<FipMessageVO> fipmessagevo) throws BusinessException {
		ArrayList<FipMsgResultVO> rs = new ArrayList<FipMsgResultVO>();
		// ���һ�ν��յ���Ϣ���࣬ȫ��ת���Ļ����ܺľ��ڴ���Դ�����Ե���Ϣ������500�����������
		int size = fipmessagevo.size();
		int loopstep = 500;
		int loopcount = (size - 1) / loopstep + 1;
		for (int i = 0; i < loopcount; i++) {
			int fromIndex = i * loopstep;
			int toIndex = i == loopcount - 1 ? size : fromIndex + loopstep;
			List<FipMessageVO> subList = fipmessagevo.subList(fromIndex, toIndex);// ��Ҫע��toIndexָ���ǽ�ֹλ�ã������λ�ò�����toIndex�Լ���Ҳ����˵��������0-499����toIndex��Ҫ��500
			rs.addAll(addMessageOneStep(subList));
		}
		return rs;
	}

	/**
	 * С���������Ϣ����Ҫ��ֹ�ڴ����
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param fipmessagelist
	 * @return
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private List<FipMsgResultVO> addMessageOneStep(List<FipMessageVO> fipmessagelist) throws BusinessException {
		// check billvo
		complementBillVO(fipmessagelist);

		// List<List<FipExtRelationVO>> msgrelationlist = new ArrayList<List<FipExtRelationVO>>();
		// // ���ƽ̨���ú���������
		// for (FipMessageVO fipmessagevo : fipmessagelist) {
		// // ͨ��ƽ̨���ã��������ݣ�����/����/��ʽ����ȷ�����ݵ�����ϵͳ������/���λ��/�ɱ�
		// List<FipExtRelationVO> list = new ArrayList<FipExtRelationVO>();
		// ProcessConfigVO[] vos = processByConfig(fipmessagevo);
		// if (vos != null) {
		// ArrayList<ProcessConfigVO> vec = new ArrayList<ProcessConfigVO>();
		// for (int i = 0; i < vos.length; i++) {
		// // ��ʽ���ɡ������� �������������ֱ�����ɣ�������
		// if (!vos[i].isGenerateflag())
		// continue;
		// if (vos[i].getGeneratemode() == 1)
		// continue;
		// vec.add(vos[i]);
		// }
		// if (!vec.isEmpty()) {
		// // ����ֱ�����ɵĵ��ݣ������������
		// for (ProcessConfigVO processConfigVO : vec) {
		// FipExtRelationVO[] dmvs = null;
		// {
		// // ����������
		// long s_time = System.currentTimeMillis();
		// dmvs = MessageProcessCenter.processMessage(fipmessagevo, processConfigVO, "fip", "nc.itf.fip.messageprocess.DefaultMessageProcessor");
		// long e_time = System.currentTimeMillis();
		// Logger.debug("#FIP DEBUG#" + "�������������ʱ��" + (e_time - s_time));
		// }
		// {
		// if (dmvs != null && dmvs.length > 0) {
		// for (int i = 0; i < dmvs.length; i++) {
		// if (dmvs[i] == null)
		// continue;
		// list.add(dmvs[i]);
		// }
		// } else {
		// throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0046")/* @res "ƽ̨����������Ŀ�굥�ݣ�������������û��������Ч��Ŀ������֯��" */+ "[" + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017pub_0", "01017pub-2-0002")/* @res "��Դ���ݱ���" */+ ":" + processConfigVO.getSrc_billtype() + "->"
		// + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017pub_0", "01017pub-2-0003")/* @res "Ŀ�굥�ݱ���" */+ ":" + processConfigVO.getDes_billtype() + "]");
		// // throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0046")/* @res "ƽ̨����������Ŀ�굥�ݣ�������������û��������Ч��Ŀ������֯��" */+ processConfigVO.getSrc_billtype() + "->" + processConfigVO.getDes_billtype());
		// }
		// }
		// }
		// }
		// }
		// msgrelationlist.add(list);
		// }

		return addMessageAndDesBill(processConfigAndOrgRule(fipmessagelist, null), fipmessagelist);
	}

	/**
	 * ���ƽ̨���ú���������
	 * 
	 * @param fipmessagelist
	 * @param desinfos
	 * @return
	 * @throws BusinessException
	 */
	public List<List<FipExtRelationVO>> processConfigAndOrgRule(List<FipMessageVO> fipmessagelist, FipRelationInfoVO[] desinfos) throws BusinessException {
		List<List<FipExtRelationVO>> msgrelationlist = new ArrayList<List<FipExtRelationVO>>();
		// ���ƽ̨���ú���������
		for (FipMessageVO fipmessagevo : fipmessagelist) {
			// ͨ��ƽ̨���ã��������ݣ�����/����/��ʽ����ȷ�����ݵ�����ϵͳ������/���λ��/�ɱ�
			List<FipExtRelationVO> list = new ArrayList<FipExtRelationVO>();
			ProcessConfigVO[] vos = processByConfig(fipmessagevo);
			if (vos != null) {
				ArrayList<ProcessConfigVO> vec = new ArrayList<ProcessConfigVO>();
				Set<String> desset = null;
				if (desinfos != null) {
					desset = new HashSet<String>();
					for (FipRelationInfoVO desinfo : desinfos) {
						desset.add(desinfo.getPk_billtype());
					}
				}
				for (int i = 0; i < vos.length; i++) {
					// ��ʽ���ɡ������� �������������ֱ�����ɣ�������
					if (!vos[i].isGenerateflag())
						continue;
					if (vos[i].getGeneratemode() == 1)
						continue;
					if (desset == null || desset.contains(vos[i].getDes_billtype()))
						vec.add(vos[i]);
				}
				if (!vec.isEmpty()) {
					// ����ֱ�����ɵĵ��ݣ������������
					for (ProcessConfigVO processConfigVO : vec) {
						FipExtRelationVO[] dmvs = null;
						{
							// ����������
							long s_time = System.currentTimeMillis();
							dmvs = MessageProcessCenter.processMessage(fipmessagevo, processConfigVO, "fip", "nc.itf.fip.messageprocess.DefaultMessageProcessor");
							long e_time = System.currentTimeMillis();
							Logger.debug("#FIP DEBUG#" + "�������������ʱ��" + (e_time - s_time));
						}
						{
							if (dmvs != null && dmvs.length > 0) {
								for (int i = 0; i < dmvs.length; i++) {
									if (dmvs[i] == null)
										continue;
									list.add(dmvs[i]);
								}
							} else {
								throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0046")/* @res "ƽ̨����������Ŀ�굥�ݣ�������������û��������Ч��Ŀ������֯��" */+ "[" + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017pub_0", "01017pub-2-0002")/* @res "��Դ���ݱ���" */+ ":" + processConfigVO.getSrc_billtype() + "->"
										+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017pub_0", "01017pub-2-0003")/* @res "Ŀ�굥�ݱ���" */+ ":" + processConfigVO.getDes_billtype() + "]");
								// throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0046")/* @res "ƽ̨����������Ŀ�굥�ݣ�������������û��������Ч��Ŀ������֯��" */+ processConfigVO.getSrc_billtype() + "->" + processConfigVO.getDes_billtype());
							}
						}
					}
				}
			}
			msgrelationlist.add(list);
		}
		return msgrelationlist;
	}

	private void complementBillVO(List<FipMessageVO> fipmessagelist) throws BusinessException {
		// find incomplete data
		List<FipMessageVO> list = new ArrayList<FipMessageVO>();
		for (FipMessageVO fipmessagevo : fipmessagelist) {
			if (fipmessagevo.getBillVO() == null)
				list.add(fipmessagevo);
		}
		if (!list.isEmpty()) {
			// complete incomplete data
			// group by pk_group and pk_billtype
			HashMap<String, List<FipMessageVO>> map = new HashMap<String, List<FipMessageVO>>();
			for (FipMessageVO fipMessageVO : list) {
				FipRelationInfoVO messageinfo = fipMessageVO.getMessageinfo();
				String key = "" + messageinfo.getPk_group() + messageinfo.getPk_billtype();
				List<FipMessageVO> list2 = map.get(key);
				if (list2 == null) {
					list2 = new ArrayList<FipMessageVO>();
					map.put(key, list2);
				}
				list2.add(fipMessageVO);
			}
			if (!map.isEmpty()) {
				Collection<List<FipMessageVO>> values = map.values();
				// complete each pk_group and pk_billtype
				for (List<FipMessageVO> list2 : values) {
					if (list2 == null || list2.isEmpty())
						continue;
					FipMessageVO firstelement = list2.get(0);
					String src_group = firstelement.getMessageinfo().getPk_group();
					String src_billtype = firstelement.getMessageinfo().getPk_billtype();
					IBillReflectorService ib = FipInterfaceCenter.getBillReflectorService(src_group, src_billtype);
					if (ib == null)
						throw new BusinessException(FipInterfaceCenter.getNoInterfaceFoundMessage(src_billtype, "IBillReflectorService"));
					List<FipRelationInfoVO> infolist = new ArrayList<FipRelationInfoVO>();
					for (FipMessageVO fipMessageVO : list2) {
						infolist.add(fipMessageVO.getMessageinfo());
					}

					Collection<FipExtendAggVO> queryBillByRelations = ib.queryBillByRelations(infolist);

					if (queryBillByRelations != null) {
						HashMap<String, FipExtendAggVO> map1 = new HashMap<String, FipExtendAggVO>();
						for (FipExtendAggVO fipExtendAggVO : queryBillByRelations) {
							map1.put(fipExtendAggVO.getRelationID(), fipExtendAggVO);
						}
						for (FipMessageVO fipMessageVO : list2) {
							FipExtendAggVO fipExtendAggVO = map1.get(fipMessageVO.getMessageinfo().getRelationID());
							if (fipExtendAggVO != null) {
								fipMessageVO.setBillVO(fipExtendAggVO.getBillVO());
								if (fipExtendAggVO.getMessageinfo() != null)
									fipMessageVO.setMessageinfo(fipExtendAggVO.getMessageinfo());
							}
						}
					}
				}
			}
		}
	}

	/**
	 * ����˵����
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param msgrelationlist
	 *            ÿ����Ϣ�������������
	 * @param fipmessagelist
	 *            �������Ϣ
	 * @return
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private List<FipMsgResultVO> addMessageAndDesBill(List<List<FipExtRelationVO>> msgrelationlist, List<FipMessageVO> fipmessagelist) throws BusinessException {
		List<FipMsgResultVO> rs = new ArrayList<FipMsgResultVO>();
		ArrayList<SumRelationVO> sumlist = new ArrayList<SumRelationVO>();// �������ɵ�
		List<FipTransVO> translist = new ArrayList<FipTransVO>();// ��Ҫ�����
		List<FipTransVO> errlist = new ArrayList<FipTransVO>();// ת�����̳����
		List<FipTransVO> effectlist = new ArrayList<FipTransVO>();// ����ʧ��Ӱ��ҵ�����̵�
		List<FipTransVO> uneffectlist = new ArrayList<FipTransVO>();// ����ʧ�ܲ�Ӱ��ҵ�����̵�
		int size = msgrelationlist.size();
		// ������Ϣ���������ò��ת��Ŀ�굥��
		for (int i = 0; i < size; i++) {
			List<FipExtRelationVO> list = msgrelationlist.get(i);
			FipMessageVO fipMessageVO = fipmessagelist.get(i);
			if (list == null || list.isEmpty())
				continue;
			for (FipExtRelationVO fipExtRelationVO : list) {
				if (fipExtRelationVO.getIssum() != null && fipExtRelationVO.getIssum().booleanValue()) {
					// �������ɵ�
					sumlist.add(fipExtRelationVO.convertToSumRelationVO());
				} else {
					// ��Ҫ����ĵ���
					FipTransVO transvo = new FipTransVO();
					transvo.setDatavo(fipMessageVO.getBillVO());
					transvo.setMessagevo(fipExtRelationVO);
					translist.add(transvo);
				}
			}
		}
		if (!translist.isEmpty()) {
			List<FipTransVO> transrslist = new ArrayList<FipTransVO>();
			for (FipTransVO transvo : translist) {
				List<FipTransVO> rslist = BOTranslateCenter.translate(transvo);
				if (rslist == null || rslist.isEmpty()) {
					// ��ƾ֤ģ��Ϊ��ʱ���ɣ��������ɵ�ʱ�򣬲�����������ת����Ľ��
					continue;
				}
				for (FipTransVO fipTransVO : rslist) {
					transrslist.add(fipTransVO);
				}
			}
			for (FipTransVO rsvo : transrslist) {
				// ����ת���ɹ��ģ�������ϢΪ��
				if (rsvo.getMessagevo().getOpmessage() == null) {
					if (rsvo.getIsEffect() != null && rsvo.getIsEffect().booleanValue()) {
						// Ӱ�����ҵ��
						effectlist.add(rsvo);
					} else {
						uneffectlist.add(rsvo);
					}
				} else {
					errlist.add(rsvo);
				}
			}
		}

		List<OperatingLogVO> oplist = new ArrayList<OperatingLogVO>();// ��¼��ʱ����
		List<FipRelationVO> relist = new ArrayList<FipRelationVO>();// ��¼��ʽ����
		if (!effectlist.isEmpty()) {
			// Ӱ�����ҵ��ģ�ֱ�ӱ��棬����ʧ�����׳��쳣
			List<OperatingLogVO> volist = new FipMessageImpl().saveDesBill_RequiresNew(effectlist);
			if (volist != null) {
				for (OperatingLogVO oplogvo : volist) {
					if (oplogvo.getOperateflag() != OperatingFlagEmu.FLAG_confirmed) {
						if (oplogvo.getDes_relationid() != null)
							oplist.add(oplogvo);
					} else {
						if (oplogvo.getDes_relationid() != null)
							relist.add(FipBasicRelationVO.convertFromOperatingVO(oplogvo).convertToRelationVO());
					}
				}
			}
		}
		if (!errlist.isEmpty()) {
			// ת�����̳����
			for (FipTransVO fipTransVO : errlist) {
				OperatingLogVO vo = fipTransVO.getMessagevo().convertToOperatingLogVO();
				vo.setOperateflag(OperatingFlagEmu.FLAG_no_process);
				oplist.add(vo);
			}
		}
		if (!uneffectlist.isEmpty()) {
			// ��Ӱ�����ҵ��ģ�ÿһ��������������б��棬ʧ�ܵĻ���¼��Ϣ
			IFipMessage ip1 = NCLocator.getInstance().lookup(IFipMessage.class);
			for (FipTransVO fipTransVO : uneffectlist) {
				OperatingLogVO vo = null;
				try {
					List<FipTransVO> rslist = new ArrayList<FipTransVO>();
					rslist.add(fipTransVO);
					List<OperatingLogVO> volist = ip1.saveDesBill_RequiresNew(rslist);
					if (volist != null && !volist.isEmpty())
						vo = volist.get(0);
				} catch (BusinessException e) {
					Logger.error("", e);
					vo = fipTransVO.getMessagevo().convertToOperatingLogVO();
					vo.setOpmessage(e.getMessage());
					vo.setOperateflag(OperatingFlagEmu.FLAG_no_process);
				}
				if (vo == null)
					continue;
				if (vo.getOperateflag() != OperatingFlagEmu.FLAG_confirmed) {
					if (vo.getDes_relationid() != null)
						oplist.add(vo);
				} else {
					if (vo.getDes_relationid() != null)
						relist.add(FipBasicRelationVO.convertFromOperatingVO(vo).convertToRelationVO());
				}
			}
		}
		if (!sumlist.isEmpty()) {
			FipSumRelationProxy ip = new FipSumRelationProxy();
			ip.insert(sumlist.toArray(new SumRelationVO[0]));
		}
		if (!oplist.isEmpty()) {
			FipOperatingLogProxy ip = new FipOperatingLogProxy();
			ip.insert(oplist.toArray(new OperatingLogVO[0]));
		}
		if (!relist.isEmpty()) {
			FipRelationProxy ip = new FipRelationProxy();
			ip.insert(relist.toArray(new FipRelationVO[0]));
		}
		return rs;
	}

	/**
	 * ����˵����
	 * <p>
	 * �޸ļ�¼��
	 * </p>
	 * 
	 * @param fipmessagevo
	 * @see
	 * @since V6.0
	 */
	// @SuppressWarnings("unchecked")
	// private void deleteMessage(FipMessageVO fipmessagevo) throws BusinessException {
	// // ��Ϊ60��һ��ĵ�����Ч��һ����Ч����֧�ֶ����Ч�����Է���ЧҲ��һ�Σ�����Ч��ͬʱɾ���������ɹ���Ŀ�굥��
	// if (fipmessagevo == null)
	// return;
	// if (fipmessagevo.getMessageinfo() == null)
	// return;
	// String sqlpart = "";
	// sqlpart = sqlpart + " src_relationid='" + fipmessagevo.getMessageinfo().getRelationID() + "' and src_billtype='" + fipmessagevo.getMessageinfo().getPk_billtype() + "' ";
	// IFipSumRelation ip2 = new FipSumRelationProxy();
	// SumRelationVO[] sumvos = ip2.queryByWhere(sqlpart);
	// if (sumvos != null) {
	// ip2.delete(sumvos);
	// }
	// IOperatingLog ip = new FipOperatingLogProxy();
	// OperatingLogVO[] oplogs = ip.queryByWhere(sqlpart);
	// if (oplogs != null) {
	// ip.delete(oplogs);
	// }
	// IFipRelation ip1 = new FipRelationProxy();
	// FipRelationVO[] relations = ip1.queryByWhere(sqlpart);
	// if (relations != null) {
	// ip1.deleteReal(relations);
	// }
	// }

	public void sumSrcBill(FipBasicRelationVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0)
			return;
		String pk_group = vos[0].getDesRelation().getPk_group();
		String src_billtype = vos[0].getSrcRelation().getPk_billtype();
		FipRelationInfoVO desinfo = vos[0].getDesRelation();
		String des_billtype = desinfo.getPk_billtype();
		String des_org = desinfo.getPk_org();
		IBillSumService ip = FipInterfaceCenter.getBillSumService(pk_group, src_billtype);
		if (ip == null)
			throw new BusinessException(FipInterfaceCenter.getNoInterfaceFoundMessage(src_billtype, "IBillSumService"));
		ArrayList<FipRelationInfoVO> grouplist = new ArrayList<FipRelationInfoVO>();
		for (int i = 0; i < vos.length; i++) {
			grouplist.add(vos[i].getSrcRelation());
		}
		ArrayList<Collection<FipRelationInfoVO>> sumgroup = new ArrayList<Collection<FipRelationInfoVO>>();
		sumgroup.add(grouplist);
		FipQueryBO fipQueryBO = new FipQueryBO();
		String[] usingFields = fipQueryBO.queryUsingFields(src_billtype, des_billtype, pk_group, des_org);
		String[] factors = fipQueryBO.queryTemplateBusiTypes(src_billtype, des_billtype, pk_group, des_org);
		Collection<FipBillSumRSVO> sumBills = ip.querySumBill(sumgroup, usingFields, UFBoolean.valueOf(factors != null));
		if (sumBills != null && !sumBills.isEmpty()) {
			ArrayList<String> groupidlist = new ArrayList<String>();
			for (FipBillSumRSVO fipBillSumRSVO : sumBills) {
				FipExtRelationVO fipRelationVO = new FipExtRelationVO();
				FipMessageVO fipmessagevo = new FipMessageVO();
				fipRelationVO.setSrcRelation(fipBillSumRSVO.getMessageinfo());
				fipRelationVO.setDesRelation((FipRelationInfoVO) desinfo.clone());
				fipRelationVO.setIssum(UFBoolean.FALSE);
				fipmessagevo.setMessageinfo(fipBillSumRSVO.getMessageinfo());
				fipmessagevo.setBillVO(fipBillSumRSVO.getBillVO());
				fipmessagevo.setMessagetype(FipMessageVO.MESSAGETYPE_ADD);
				addMessageAndSaveBill(fipRelationVO, fipmessagevo);
				// ���·����
				FipRelationInfoVO[] relationvos = fipBillSumRSVO.getRelationvos();
				if (relationvos != null) {
					String relationID = fipBillSumRSVO.getMessageinfo().getRelationID();
					groupidlist.add(relationID);
					updateSumRelation(relationID, relationvos, desinfo);
					// ArrayList<String> pklist = new ArrayList<String>();
					// for (int i = 0; i < relationvos.length; i++) {
					// FipRelationInfoVO fipRelationInfoVO = relationvos[i];
					// if (fipRelationInfoVO == null || pklist.contains(fipRelationInfoVO.getRelationID()))
					// continue;
					// pklist.add(fipRelationInfoVO.getRelationID());
					// }
					// String wherepart = SqlTools.getInStr(SumRelationVO.SRC_RELATIONID, pklist, true) + " and " + SumRelationVO.DES_BILLTYPE + "='" + desinfo.getPk_billtype() + "' and " + SumRelationVO.DES_ORG + "='" + desinfo.getPk_org() + "' ";
					// String sql = "update " + SumRelationVO.getDefaultTableName() + " set " + SumRelationVO.GROUPID + " ='" + relationID + "' where " + wherepart;
					// int upcount = baseDAO.executeUpdate(sql);
					// if (upcount != pklist.size())
					// throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0047")/* @res "���������Ѿ������˸��£���ˢ�����ݡ�" */);
					// sql = "update " + OperatingLogVO.getDefaultTableName() + " set " + OperatingLogVO.SUMFLAG + " ='Y' where " + OperatingLogVO.SRC_RELATIONID + "='" + relationID + "' and " + OperatingLogVO.DES_BILLTYPE + "='" + desinfo.getPk_billtype() + "' and " + OperatingLogVO.DES_ORG + "='" + desinfo.getPk_org() + "' ";
					// baseDAO.executeUpdate(sql);
					// sql = "update " + FipRelationVO.getDefaultTableName() + " set " + FipRelationVO.SUMFLAG + " ='Y' where " + FipRelationVO.SRC_RELATIONID + "='" + relationID + "' and " + FipRelationVO.DES_BILLTYPE + "='" + desinfo.getPk_billtype() + "' and " + FipRelationVO.DES_ORG + "='" + desinfo.getPk_org() + "' ";
					// baseDAO.executeUpdate(sql);
				}
			}
		}
	}

	private void updateSumRelation(String relationID, FipRelationInfoVO[] relationvos, FipRelationInfoVO desinfo) throws BusinessException {
		BaseDAO baseDAO = new BaseDAO();
		ArrayList<String> pklist = new ArrayList<String>();
		for (int i = 0; i < relationvos.length; i++) {
			FipRelationInfoVO fipRelationInfoVO = relationvos[i];
			if (fipRelationInfoVO == null || pklist.contains(fipRelationInfoVO.getRelationID()))
				continue;
			pklist.add(fipRelationInfoVO.getRelationID());
		}
		String wherepart = SqlTools.getInStr(SumRelationVO.SRC_RELATIONID, pklist, true) + " and " + SumRelationVO.DES_BILLTYPE + "='" + desinfo.getPk_billtype() + "' and " + SumRelationVO.DES_ORG + "='" + desinfo.getPk_org() + "' ";
		String sql = "update " + SumRelationVO.getDefaultTableName() + " set " + SumRelationVO.GROUPID + " ='" + relationID + "' where " + wherepart;
		int upcount = baseDAO.executeUpdate(sql);
		if (upcount != pklist.size())
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1017prv_0", "01017prv-0047")/* @res "���������Ѿ������˸��£���ˢ�����ݡ�" */);
		sql = "update " + OperatingLogVO.getDefaultTableName() + " set " + OperatingLogVO.SUMFLAG + " ='Y' where " + OperatingLogVO.SRC_RELATIONID + "='" + relationID + "' and " + OperatingLogVO.DES_BILLTYPE + "='" + desinfo.getPk_billtype() + "' and " + OperatingLogVO.DES_ORG + "='" + desinfo.getPk_org() + "' ";
		baseDAO.executeUpdate(sql);
		sql = "update " + FipRelationVO.getDefaultTableName() + " set " + FipRelationVO.SUMFLAG + " ='Y' where " + FipRelationVO.SRC_RELATIONID + "='" + relationID + "' and " + FipRelationVO.DES_BILLTYPE + "='" + desinfo.getPk_billtype() + "' and " + FipRelationVO.DES_ORG + "='" + desinfo.getPk_org() + "' ";
		baseDAO.executeUpdate(sql);
	}

	// public void saveBill(List<FipTransVO> rslist, FipExtRelationVO fipRelationVO, OperatingLogVO oplogvo, FipBasicRelationVO relation) throws BusinessException {
	// {
	// FipTransVO rsvo = rslist.get(0);
	// // ���Ĳ�������ת����ĵ���
	// ArrayList<FipSaveResultVO> volist = new ArrayList<FipSaveResultVO>();
	// IDesBillService ib = FipInterfaceCenter.getDesBillService(fipRelationVO.getDesRelation().getPk_group(), fipRelationVO.getDesRelation().getPk_billtype());
	// if (ib == null)
	// throw new BusinessException(FipInterfaceCenter.getNoInterfaceFoundMessage(fipRelationVO.getDesRelation().getPk_billtype(), "IDesBillService"));
	// FipSaveResultVO savevo = new FipSaveResultVO();
	// savevo.setBillVO(rsvo.getDatavo());
	// savevo.setMessageinfo(rsvo.getMessagevo().getDesRelation());
	// volist.add(savevo);
	// if (ib.needConfirm() != null && ib.needConfirm().booleanValue()) {
	// // need added ���������ж��Ƿ���Ҫ�Զ�ȷ��
	// // ���Ŀ��ϵͳû��ע��ϲ�Ŀ�굥�ݽӿڣ�����ΪĿ�굥��û��δȷ�ϵ�������״̬���򵥾��Զ�ȷ��
	// // �Զ�ȷ�ϵĵ��ݣ����������ϵ��ɾ������״̬��
	// boolean autoconfirm = rsvo.getIsAutoConfirm().booleanValue();
	// if (autoconfirm) {
	// oplogvo.setOperateflag(OperatingFlagEmu.FLAG_confirmed);
	// // ip.update(new OperatingLogVO[] {
	// // oplogvo
	// // });
	// Collection<FipSaveResultVO> rsvo3 = ib.confirmBill(volist);
	// fipRelationVO.setDesRelation(rsvo3.iterator().next().getMessageinfo());
	// IFipRelation ip1 = new FipRelationProxy();
	// ip1.insert(new FipRelationVO[] {
	// fipRelationVO.convertToRelationVO()
	// });
	// // ip.delete(new OperatingLogVO[] {
	// // oplogvo
	// // });
	// relation = fipRelationVO; // �н����ת�������ɹ�����ϵ
	// } else {
	// // ��Ҫȷ�ϵĵ��ݣ��ȱ��棬Ȼ��ȷ��
	// Collection<FipSaveResultVO> rsvo2 = ib.saveBill(volist);
	// fipRelationVO.setDesRelation(rsvo2.iterator().next().getMessageinfo());
	// // ���岽��������Ϣ����״̬����ļ�¼������Ŀ�굥�ݵ���Ϣ��ͬʱ���¸ü�¼Ϊ���ת����δȷ��״̬
	// // String oldpk = oplogvo.getPk_operatinglog();
	// oplogvo = fipRelationVO.convertToOperatingLogVO();
	// oplogvo.setOperateflag(OperatingFlagEmu.FLAG_saved);
	// // oplogvo.setPk_operatinglog(oldpk);
	// // ip.update(new OperatingLogVO[] {
	// // oplogvo
	// // });
	// relation = fipRelationVO; // �н����ת�������ɹ�����ϵ
	// }
	// } else {
	// // ����Ҫȷ�ϵĵ��ݣ�ֱ�ӱ������Ч���ݣ����������ϵ����ɾ������״̬��
	// oplogvo.setOperateflag(OperatingFlagEmu.FLAG_confirmed);
	// Collection<FipSaveResultVO> rsvo2 = ib.confirmBill(volist);
	// fipRelationVO.setDesRelation(rsvo2.iterator().next().getMessageinfo());
	// IFipRelation ip1 = new FipRelationProxy();
	// ip1.insert(new FipRelationVO[] {
	// fipRelationVO.convertToRelationVO()
	// });
	// // ip.delete(new OperatingLogVO[] {
	// // oplogvo
	// // });
	// relation = fipRelationVO; // �н����ת�������ɹ�����ϵ
	// }
	// }
	// // if (oplogvo != null && oplogvo.getOperateflag() < 10) {
	// // ip.insert(new OperatingLogVO[] {
	// // oplogvo
	// // });
	// // }
	// // if (relation != null) {
	// // rs = new FipMsgResultVO();
	// // rs.addRelation(relation);
	// // }
	// // return rs;
	// }
}