package nc.ui.fee.withholding.ace.ref;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.PfUtilTools;
import nc.itf.fee.ISubagreementMaintain;
import nc.itf.fee.IWithholdingMaintain;
import nc.itf.uap.pf.IPFBusiAction;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.query2.model.IModelDataManager;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.vo.fee.subagreement.AggSubagreementVO;
import nc.vo.fee.subagreement.SubagreementVO;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.ArrayUtils;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;

/**
 * ���������պ�ͬ��ť
 * 
 * @author shidalin
 * 
 */
public class AddFromFE01Action extends NCAction {

	public AddFromFE01Action() {
		this.setBtnName("ѡ���˹��ְ�Э��");
		this.setCode("FE02RefFE01Action");
	}

	private BillForm editor;

	private BillManageModel model;

	private IModelDataManager dataManager;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		UFDate busiDate = AppContext.getInstance().getBusiDate();
		int currentYear = busiDate.getYear();
		Integer[] mouth = new Integer[12];
		Integer[] year = new Integer[10];
		for (int i = 0; i < 12; i++) {
			mouth[i] = i + 1;
		}
		for (int i = 0; i < 10; i++) {
			year[i] = i + currentYear;
		}
		Object[] selectInfo = (Object[]) MessageDialog.showSelectDlg2(null, 0,
				"��ѯ����ѡ���", "��ѡ��Ԥ��������Ϣ", year, mouth, 5);
		if (selectInfo != null && Arrays.asList(selectInfo) != null) {
			// ��̨��ѯ�ְ�Э������
			AggSubagreementVO[] srcVOs = NCLocator
					.getInstance()
					.lookup(ISubagreementMaintain.class)
					.queryForFE02(selectInfo[0].toString(),
							selectInfo[1].toString());
			if (srcVOs == null || srcVOs.length == 0) {
				ExceptionUtils.wrappBusinessException("û��ƥ������ݣ������²�ѯ");
			}
			processDate(srcVOs, selectInfo[0].toString(),
					selectInfo[1].toString());
			// ˢ�½������ݣ�����ʾ��Ϣ
			// ˢ�½��沢��ʾ
			this.getDataManager().refresh();
			this.showQueryInfo();
		}
		// else {
		// ExceptionUtils.wrappBusinessException("��������ȷ���·�");
		// }
	}

	protected void showQueryInfo() {
		int size = 0;
		if (this.getModel() instanceof BillManageModel) {
			size = ((BillManageModel) this.getModel()).getData().size();
		}

		if (size >= 0) {
			ShowStatusBarMsgUtil.showStatusBarMsg("�����˹��ְ�Э�������ɹ�,�����²�ѯ����", this
					.getModel().getContext());
		}

	}

	// ��̨����
	private void processDate(AggSubagreementVO[] srcVOs, String year,
			String mouth) throws BusinessException {
		Integer[] mouthFor31 = new Integer[] { 1, 3, 5, 7, 8, 10, 12 };
		if (!ArrayUtils.isEmpty(srcVOs)) {
			// ����ת������ת������
			AggregatedValueObject[] destVOs = PfUtilTools.runChangeDataAry(
					"FE01", "FE02", srcVOs);
			ArrayList<AggWithholdingVO> opdata = new ArrayList<AggWithholdingVO>();
			HashMap<String, AggSubagreementVO> srcpk2srcvo = new HashMap<String, AggSubagreementVO>();
			for (AggSubagreementVO srcvo : srcVOs) {
				srcpk2srcvo
						.put(srcvo.getParentVO().getPk_subagreement(), srcvo);
			}
			ArrayList<SubagreementVO> writeBackList = new ArrayList<SubagreementVO>();
			for (AggregatedValueObject destvo : destVOs) {
				// ���ε������ݷ�װ
				AggWithholdingVO agg = (AggWithholdingVO) destvo;
				agg.getParentVO().setPk_billtypecode("FE02");
				agg.getParentVO().setPk_billtypeid(
						nc.vo.am.common.util.BillTypeUtils.getPKByCode("FE02"));
				agg.getParentVO().setDbilldate(
						AppContext.getInstance().getBusiDate());
				// ����Ԥ����
				AggSubagreementVO srcvo = srcpk2srcvo.get(agg.getParentVO()
						.getCsourceid());
				// ��ͬ��ʼ����
				UFDate constartdate = srcvo.getParentVO().getConstartdate();
				constartdate = getFormatDate(constartdate);
				// ��ͬ��������
				UFDate conenddate = srcvo.getParentVO().getConenddate();
				conenddate = getFormatDate(conenddate);
				// ��ͬ������,�߼���Ϊ ��ͷ��β
				int totalDays = UFDate.getDaysBetween(constartdate, conenddate) + 1;
				// ��ͬʣ���Ԥ����
				UFDouble contractmny = srcvo.getParentVO().getContractmny();
				// ��ѯ��ʷԤ����������һ��Ԥ�����ں��ۼ�Ԥ����
				// ���Ӧ�õ���Ϊ��¼ÿһ�ε�Ԥ�����ڣ�����Ԥ�������ֶ�
				Map<String, Object> map = NCLocator
						.getInstance()
						.lookup(IWithholdingMaintain.class)
						.queryWithholdingRecord(
								agg.getParentVO().getCsourceid());
				// ��ǰ������Ϣ
				Integer inputMonth = Integer.parseInt(mouth);
				Integer inputYear = Integer.parseInt(year);
				// ��������ʵ������
				UFDate inputdate = getCurrentInputDays(mouthFor31, inputMonth,
						inputYear);
				if (map == null || map.keySet().size() == 0
						|| map.get("withholdingdate") == null) {
					// һ�ζ�û��Ԥ��,�����ͬ��ʼ���ڵ���ǰ�µ�
					if (inputMonth == conenddate.getMonth()
							&& inputYear == conenddate.getYear()) {
						// һ��δ�ᣬ���Ǻ�ͬ�����·ݣ�ֱ�Ӿ��Ǻ�ͬ���
						agg.getParentVO().setWithholdingmny(contractmny);
						if (UFDouble.ZERO_DBL.compareTo(agg.getParentVO()
								.getWithholdingmny()) >= 0) {
							continue;
						}
						// ����Ԥ������
						agg.getParentVO().setWithholdingdate(conenddate);
						// ����Ԥ�Ὺʼ����
						agg.getParentVO().setStartdate(constartdate);
						// ����Ԥ���������
						agg.getParentVO().setEnddate(conenddate);
						// ����Ԥ������
						Integer withholdingdays = UFDate.getDaysBetween(agg
								.getParentVO().getStartdate(), agg
								.getParentVO().getEnddate()) + 1;
						agg.getParentVO().setWithholdingdays(withholdingdays);
						// ��д��Ϣ
						SubagreementVO writeBackVO = srcpk2srcvo.get(
								agg.getParentVO().getCsourceid()).getParentVO();
						// ��Ԥ������
						writeBackVO.setUseddays(writeBackVO.getUseddays()
								+ withholdingdays);
						// ʣ���Ԥ������
						writeBackVO.setActivedays(writeBackVO.getActivedays()
								- withholdingdays);
						// ��Ԥ����
						writeBackVO.setUsedmny(writeBackVO.getUsedmny().add(
								contractmny));
						// ʣ���Ԥ����
						writeBackVO.setActivemny(writeBackVO.getActivemny()
								.sub(contractmny));
						writeBackList.add(writeBackVO);
					} else {
						// ����Ԥ�Ὺʼ����
						agg.getParentVO().setStartdate(constartdate);
						// ����Ԥ���������
						agg.getParentVO().setEnddate(inputdate);
						int withholdingDays = UFDate.getDaysBetween(
								constartdate, inputdate) + 1;
						UFDouble withholdingmny = contractmny.div(new UFDouble(totalDays),2)
								.multiply(new UFDouble(withholdingDays),2);
						if (UFDouble.ZERO_DBL.compareTo(withholdingmny) >= 0) {
							continue;
						}
						agg.getParentVO().setWithholdingmny(withholdingmny);
						agg.getParentVO().setWithholdingdate(inputdate);
						// ����Ԥ������
						agg.getParentVO().setWithholdingdays(withholdingDays);
						// ��д��Ϣ
						SubagreementVO writeBackVO = srcpk2srcvo.get(
								agg.getParentVO().getCsourceid()).getParentVO();
						// ��Ԥ������
						writeBackVO
								.setUseddays((writeBackVO.getUseddays() == null ? 0
										: writeBackVO.getUseddays())
										+ withholdingDays);
						// ʣ���Ԥ������
						writeBackVO.setActivedays(writeBackVO.getActivedays()
								- withholdingDays);
						// ��Ԥ����
						writeBackVO.setUsedmny(writeBackVO.getUsedmny().add(
								withholdingmny));
						// ʣ���Ԥ����
						writeBackVO.setActivemny(writeBackVO.getActivemny()
								.sub(withholdingmny));
						writeBackList.add(writeBackVO);
					}
				} else {
					// ���һ��Ԥ������
					UFDate lastBilldate = map.get("withholdingdate") == null ? null
							: new UFDate(map.get("withholdingdate").toString());
					// ��ʷ�ۼ�Ԥ����
					UFDouble lastSumMny = new UFDouble(map
							.get("withholdingmny").toString());
					// 1.��������һ���£�ֱ�ӵ��ں�ͬ���-��ʷ�ۼ�Ԥ����
					if (inputMonth == conenddate.getMonth()
							&& inputYear == conenddate.getYear()) {
						UFDouble withholdingmny = agg.getParentVO()
								.getContractmny().sub(lastSumMny);
						agg.getParentVO().setWithholdingmny(withholdingmny);
						agg.getParentVO().setWithholdingdate(conenddate);
						if (UFDouble.ZERO_DBL.compareTo(withholdingmny) >= 0) {
							continue;
						}
						// ����Ԥ�Ὺʼ����
						agg.getParentVO().setStartdate(
								lastBilldate.getDateAfter(1));
						// ����Ԥ���������
						agg.getParentVO().setEnddate(conenddate);
						// ����Ԥ������
						int withholdingDays = UFDate.getDaysBetween(agg
								.getParentVO().getStartdate(), agg
								.getParentVO().getEnddate()) + 1;
						agg.getParentVO().setWithholdingdays(withholdingDays);
						// ��д��Ϣ
						SubagreementVO writeBackVO = srcpk2srcvo.get(
								agg.getParentVO().getCsourceid()).getParentVO();
						// ��Ԥ������
						writeBackVO.setUseddays(writeBackVO.getUseddays()
								+ withholdingDays);
						// ʣ���Ԥ������
						writeBackVO.setActivedays(writeBackVO.getActivedays()
								- withholdingDays);
						// ��Ԥ����
						writeBackVO.setUsedmny(writeBackVO.getUsedmny().add(
								withholdingmny));
						// ʣ���Ԥ����
						writeBackVO.setActivemny(writeBackVO.getActivemny()
								.sub(withholdingmny));
						writeBackList.add(writeBackVO);
					} else {
						// 2.ǰ��δ��������=��ǰ���� - ���һ��Ԥ������
						// ����Ԥ�Ὺʼ����
						agg.getParentVO().setStartdate(
								lastBilldate.getDateAfter(1));
						// ����Ԥ���������
						agg.getParentVO().setEnddate(inputdate);
						int withholdingDays = UFDate.getDaysBetween(agg
								.getParentVO().getStartdate(), agg
								.getParentVO().getEnddate()) + 1;
						agg.getParentVO().setWithholdingdays(withholdingDays);
						// Ԥ����
						UFDouble withholdingmny = contractmny.div(new UFDouble(totalDays),2)
								.multiply(new UFDouble(withholdingDays),2);
						if (UFDouble.ZERO_DBL.compareTo(withholdingmny) >= 0) {
							continue;
						}
						agg.getParentVO().setWithholdingmny(withholdingmny);
						agg.getParentVO().setWithholdingdate(inputdate);
						// ��д��Ϣ
						SubagreementVO writeBackVO = srcpk2srcvo.get(
								agg.getParentVO().getCsourceid()).getParentVO();
						// ��Ԥ������
						writeBackVO.setUseddays(writeBackVO.getUseddays()
								+ withholdingDays);
						// ʣ���Ԥ������
						writeBackVO.setActivedays(writeBackVO.getActivedays()
								- withholdingDays);
						// ��Ԥ����
						writeBackVO.setUsedmny(writeBackVO.getUsedmny().add(
								withholdingmny));
						// ʣ���Ԥ����
						writeBackVO.setActivemny(writeBackVO.getActivemny()
								.sub(withholdingmny));
						writeBackList.add(writeBackVO);
					}
				}

				opdata.add((AggWithholdingVO) destvo);
			}
			// ���ú�̨�ű�������Ŀ������
			if (opdata.size() > 0) {
				NCLocator
						.getInstance()
						.lookup(IPFBusiAction.class)
						.processBatch("SAVEBASE", "FE02",
								opdata.toArray(new AggWithholdingVO[0]), null,
								null, null);
			}
			// ��д������Ԥ���ע��ɾ������Ҳ����Ҫ��д��
			if (writeBackList.size() > 0) {
				String[] fieldNames = new String[] { "useddays", "activedays",
						"usedmny", "activemny" };
				NCLocator.getInstance().lookup(ISubagreementMaintain.class)
						.writeBackForWithholding(writeBackList, fieldNames);
			}
		}
	}

	private UFDate getFormatDate(UFDate date) {
		Integer yearParam = date.getYear();
		Integer monthParam = date.getMonth();
		Integer dayParam = date.getDay();
		UFDate result = new UFDate(yearParam.toString() + "-"
				+ monthParam.toString() + "-" + dayParam.toString());
		return result;
	}

	private UFDate getCurrentInputDays(Integer[] mouthFor31,
			Integer currentMonth, Integer currentYear) {
		Integer inputMouthDays = 30;
		if (Arrays.asList(mouthFor31) != null
				&& Arrays.asList(mouthFor31).contains(currentMonth)) {
			inputMouthDays = 31;
		} else if (currentMonth == 2) {
			// ����29��ƽ��28
			// �ܱ�4�������ܱ�100���������ܱ�400����
			if (((currentYear % 4 == 0) && (currentYear % 100 != 0))
					|| (currentYear % 400 == 0)) {
				inputMouthDays = 29;
			} else {
				inputMouthDays = 28;
			}
		}
		UFDate inputMouthLastDate = new UFDate(currentYear.toString() + "-"
				+ currentMonth.toString() + "-" + inputMouthDays.toString());
		return inputMouthLastDate;
	}

	public BillForm getEditor() {
		return this.editor;
	}

	public BillManageModel getModel() {
		return this.model;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public IModelDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	@Override
	protected boolean isActionEnable() {
		return this.model.getUiState() == UIState.NOT_EDIT;
	}

}

/**
 * ��ѯ�����
 * 
 */
class QueryInputDialog extends UIDialog {

	public QueryInputDialog() {
		super();
		initContentPanel();
	}

	/**
	 * ��ʼ�������
	 */
	private void initContentPanel() {

		JPanel jPanel = new JPanel();
		// ���������
		jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		jPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JLabel labelForYear = new JLabel("Ԥ�����:");
		jPanel.add(labelForYear);
		comboBoxForYear = new JComboBox();
		UFDate busiDate = AppContext.getInstance().getBusiDate();
		int year = busiDate.getYear();
		int month = busiDate.getMonth();
		for (int i = year - 5; i < (year + 5); i++) {
			comboBoxForYear.addItem(i);
		}
		jPanel.add(comboBoxForYear);
		JLabel jLabelForMouth = new JLabel("Ԥ���·�");
		jPanel.add(jLabelForMouth);
		comboBoxForMouth = new JComboBox();
		for (int i = 1; i < 13; i++) {
			comboBoxForMouth.addItem(i);
		}
		jPanel.add(comboBoxForMouth);
		this.setContentPane(jPanel);
	}

	private int year;

	private int mouth;

	private JComboBox comboBoxForYear;

	private JComboBox comboBoxForMouth;

	private UFBoolean closeOK = UFBoolean.FALSE;

	@Override
	public String getTitle() {
		return "��ѯ���������";
	}

	public int getYear() {
		return this.year;
	}

	public int getMouth() {

		return this.mouth;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setMouth(int mouth) {
		this.mouth = mouth;
	}

	@Override
	public void closeOK() {
		super.closeOK();
		Object selectedItemForYear = this.comboBoxForYear.getSelectedItem();
		if (selectedItemForYear == null) {
			ExceptionUtils.wrappBusinessException("��ѡ��Ԥ�����");
		}
		this.setYear(Integer.parseInt(selectedItemForYear.toString()));

		Object selectedItemForMouth = this.comboBoxForMouth.getSelectedItem();
		if (selectedItemForMouth == null) {
			ExceptionUtils.wrappBusinessException("��ѡ��Ԥ���·�");
		}
		this.setMouth(Integer.parseInt(selectedItemForMouth.toString()));
		// �رձ�־
		this.setCloseOK(UFBoolean.TRUE);
	}

	public UFBoolean getCloseOK() {
		return this.closeOK;
	}

	public void setCloseOK(UFBoolean closeOK) {
		this.closeOK = closeOK;
	}

}
