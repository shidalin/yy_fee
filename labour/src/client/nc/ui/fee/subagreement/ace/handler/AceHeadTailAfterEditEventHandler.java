package nc.ui.fee.subagreement.ace.handler;

import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.IWorkteamMaintain;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.QueryScheme;
import nc.vo.fee.subagreement.SubagreementItemVO;
import nc.vo.fee.workteam.WorkteamVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.beanutils.BeanUtils;

/**
 * �˹��ְ�Э���ͷ��β�༭���¼�
 * 
 * @author shidalin
 * 
 */
public class AceHeadTailAfterEditEventHandler implements
		IAppEventHandler<CardHeadTailAfterEditEvent> {

	public AceHeadTailAfterEditEventHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		String editkey = e.getKey();
		String value = e.getValue() == null ? "" : e.getValue().toString();
		/**
		 * ��ͬ��ű༭���¼�
		 */
		if ("contratcno".equals(editkey)) {
			UIRefPane refPanel = (UIRefPane) e.getBillCardPanel()
					.getHeadItem(e.getKey()).getComponent();
			// ���񿨺�
			Object serviceno = refPanel.getRefValue("vbillcode");
			e.getBillCardPanel().setHeadItem("serviceno", serviceno);
			// �ְ���ͬ����=�ͻ�+��ַ+��������Ŀʩ��Э���顱
			e.getBillCardPanel().setHeadItem("subcontractname", "");
		}
		// ����༭���¼�,��ȡ�ð��飨���ṹ���ݣ������еİ�����Ϣ���Ƿ�ݹ��ѯ��
		if ("pk_teamwork".equals(editkey)) {
			if (value == null || "".equals(value)) {
				return;
			}
			// �����ѯ����
			// QueryCondition queryConditionByPleader = new QueryCondition(
			// "pleader", "=", new String[] { value });
			QueryScheme querySchemeByPleader = new QueryScheme();
			// ����Ԫ����id
			querySchemeByPleader.put("bean_id",
					"361e6971-a0a7-4a0d-b4c5-90e4ac5aff57");
			FromWhereSQLImpl fromWhereSQLImpl = new FromWhereSQLImpl(
					"fee_workteam", "fee_workteam.pleader = '" + value + "'");
			// ���ò�ѯ��
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put(".", "fee_workteam");
			fromWhereSQLImpl.setAttrpath_alias_map(hashMap);
			((QueryScheme) querySchemeByPleader).put("tablejoin",
					fromWhereSQLImpl);
			try {
				WorkteamVO[] workteamVOs = NCLocator.getInstance()
						.lookup(IWorkteamMaintain.class)
						.query(querySchemeByPleader);
				if (workteamVOs != null && workteamVOs.length > 0) {
					ArrayList<SubagreementItemVO> itemList = new ArrayList<SubagreementItemVO>();
					for (WorkteamVO workteamVO : workteamVOs) {
						SubagreementItemVO subagreementItemVO = new SubagreementItemVO();
						// ����ת��
						BeanUtils
								.copyProperties(subagreementItemVO, workteamVO);
						itemList.add(subagreementItemVO);

					}
					// ��װ��������
					loadBodyData(e.getBillCardPanel(), itemList);
					// ���ر�������
					e.getBillCardPanel()
							.getBillData()
							.setBodyValueVO(
									itemList.toArray(new SubagreementItemVO[0]));
				}
			} catch (BusinessException e1) {
				e1.printStackTrace();
				ExceptionUtils.wrappException(e1);
			} catch (Exception e1) {
				e1.printStackTrace();
				ExceptionUtils.wrappException(e1);
			}
		}

		if ("constartdate".equals(editkey)) {
			String conenddate = e.getBillCardPanel().getHeadItem("conenddate")
					.getValue();
			if (conenddate == null || "".equals(conenddate)) {
				return;
			}
			UFDate constartdate = (UFDate) e.getValue();
			int contractDays = UFDate.getDaysBetween(constartdate, new UFDate(
					conenddate)) + 1;
			e.getBillCardPanel().setHeadItem("contotaldays", contractDays);
			e.getBillCardPanel().setHeadItem("activedays", contractDays);
		}
		if ("conenddate".equals(editkey)) {
			String constartdate = e.getBillCardPanel()
					.getHeadItem("constartdate").getValue();
			if (constartdate == null || "".equals(constartdate)) {
				return;
			}
			UFDate conenddate = (UFDate) e.getValue();
			int contractDays = UFDate.getDaysBetween(new UFDate(constartdate),
					conenddate) + 1;
			e.getBillCardPanel().setHeadItem("contotaldays", contractDays);
			e.getBillCardPanel().setHeadItem("activedays", contractDays);

		}
		// ��ͬ���
		if ("contractmny".equals(editkey)) {
			UFDouble conmy = (UFDouble) e.getValue();
			e.getBillCardPanel().setHeadItem("activemny", conmy);
		}

	}

	/**
	 * ��װ��������
	 * 
	 * @param billCardPanel
	 * @param itemList
	 */
	public void loadBodyData(BillCardPanel billCardPanel,
			ArrayList<SubagreementItemVO> itemList) {
		BillItem contratcno = billCardPanel.getHeadItem("contratcno");
		BillItem serviceno = billCardPanel.getHeadItem("serviceno");
		BillItem pk_subagreement = billCardPanel.getHeadItem("pk_subagreement");
		// ��������ǰɾ����
		for (SubagreementItemVO itemVO : itemList) {
			int i = itemList.indexOf(itemVO);
			itemVO.setCrowno(((Integer) (i * 10 + 10)).toString());
			itemVO.setCsourcetypecode("Z5");
			itemVO.setCsourceid(contratcno.getValueObject() == null ? ""
					: contratcno.getValueObject().toString());
			itemVO.setVsourcecode(serviceno.getValueObject() == null ? ""
					: serviceno.getValueObject().toString());
			itemVO.setPk_subagreement_b(null);
		}
	}
}
