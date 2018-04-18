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
 * 参照其他收合同按钮
 * 
 * @author shidalin
 * 
 */
public class AddFromFE01Action extends NCAction {

	public AddFromFE01Action() {
		this.setBtnName("选择人工分包协议");
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
				"查询条件选择框", "请选择预提年月信息", year, mouth, 5);
		if (selectInfo != null && Arrays.asList(selectInfo) != null) {
			// 后台查询分包协议数据
			AggSubagreementVO[] srcVOs = NCLocator
					.getInstance()
					.lookup(ISubagreementMaintain.class)
					.queryForFE02(selectInfo[0].toString(),
							selectInfo[1].toString());
			if (srcVOs == null || srcVOs.length == 0) {
				ExceptionUtils.wrappBusinessException("没有匹配的数据，请重新查询");
			}
			processDate(srcVOs, selectInfo[0].toString(),
					selectInfo[1].toString());
			// 刷新界面数据，并提示消息
			// 刷新界面并提示
			this.getDataManager().refresh();
			this.showQueryInfo();
		}
		// else {
		// ExceptionUtils.wrappBusinessException("请输入正确的月份");
		// }
	}

	protected void showQueryInfo() {
		int size = 0;
		if (this.getModel() instanceof BillManageModel) {
			size = ((BillManageModel) this.getModel()).getData().size();
		}

		if (size >= 0) {
			ShowStatusBarMsgUtil.showStatusBarMsg("参照人工分包协议生单成功,请重新查询数据", this
					.getModel().getContext());
		}

	}

	// 后台处理
	private void processDate(AggSubagreementVO[] srcVOs, String year,
			String mouth) throws BusinessException {
		Integer[] mouthFor31 = new Integer[] { 1, 3, 5, 7, 8, 10, 12 };
		if (!ArrayUtils.isEmpty(srcVOs)) {
			// 单据转换规则转换数据
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
				// 下游单据数据封装
				AggWithholdingVO agg = (AggWithholdingVO) destvo;
				agg.getParentVO().setPk_billtypecode("FE02");
				agg.getParentVO().setPk_billtypeid(
						nc.vo.am.common.util.BillTypeUtils.getPKByCode("FE02"));
				agg.getParentVO().setDbilldate(
						AppContext.getInstance().getBusiDate());
				// 处理预提金额
				AggSubagreementVO srcvo = srcpk2srcvo.get(agg.getParentVO()
						.getCsourceid());
				// 合同开始日期
				UFDate constartdate = srcvo.getParentVO().getConstartdate();
				constartdate = getFormatDate(constartdate);
				// 合同结束日期
				UFDate conenddate = srcvo.getParentVO().getConenddate();
				conenddate = getFormatDate(conenddate);
				// 合同总人天,逻辑改为 包头包尾
				int totalDays = UFDate.getDaysBetween(constartdate, conenddate) + 1;
				// 合同剩余可预提金额
				UFDouble contractmny = srcvo.getParentVO().getContractmny();
				// 查询历史预提情况，最后一次预提日期和累计预提金额
				// 这边应该调整为记录每一次的预提日期，增加预提日期字段
				Map<String, Object> map = NCLocator
						.getInstance()
						.lookup(IWithholdingMaintain.class)
						.queryWithholdingRecord(
								agg.getParentVO().getCsourceid());
				// 当前输入信息
				Integer inputMonth = Integer.parseInt(mouth);
				Integer inputYear = Integer.parseInt(year);
				// 当输入月实际天数
				UFDate inputdate = getCurrentInputDays(mouthFor31, inputMonth,
						inputYear);
				if (map == null || map.keySet().size() == 0
						|| map.get("withholdingdate") == null) {
					// 一次都没有预提,计算合同开始日期到当前月底
					if (inputMonth == conenddate.getMonth()
							&& inputYear == conenddate.getYear()) {
						// 一次未提，且是合同结束月份，直接就是合同金额
						agg.getParentVO().setWithholdingmny(contractmny);
						if (UFDouble.ZERO_DBL.compareTo(agg.getParentVO()
								.getWithholdingmny()) >= 0) {
							continue;
						}
						// 设置预提日期
						agg.getParentVO().setWithholdingdate(conenddate);
						// 设置预提开始日期
						agg.getParentVO().setStartdate(constartdate);
						// 设置预提结束日期
						agg.getParentVO().setEnddate(conenddate);
						// 设置预提天数
						Integer withholdingdays = UFDate.getDaysBetween(agg
								.getParentVO().getStartdate(), agg
								.getParentVO().getEnddate()) + 1;
						agg.getParentVO().setWithholdingdays(withholdingdays);
						// 回写信息
						SubagreementVO writeBackVO = srcpk2srcvo.get(
								agg.getParentVO().getCsourceid()).getParentVO();
						// 已预提天数
						writeBackVO.setUseddays(writeBackVO.getUseddays()
								+ withholdingdays);
						// 剩余可预提天数
						writeBackVO.setActivedays(writeBackVO.getActivedays()
								- withholdingdays);
						// 已预提金额
						writeBackVO.setUsedmny(writeBackVO.getUsedmny().add(
								contractmny));
						// 剩余可预提金额
						writeBackVO.setActivemny(writeBackVO.getActivemny()
								.sub(contractmny));
						writeBackList.add(writeBackVO);
					} else {
						// 设置预提开始日期
						agg.getParentVO().setStartdate(constartdate);
						// 设置预提结束日期
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
						// 设置预提天数
						agg.getParentVO().setWithholdingdays(withholdingDays);
						// 回写信息
						SubagreementVO writeBackVO = srcpk2srcvo.get(
								agg.getParentVO().getCsourceid()).getParentVO();
						// 已预提天数
						writeBackVO
								.setUseddays((writeBackVO.getUseddays() == null ? 0
										: writeBackVO.getUseddays())
										+ withholdingDays);
						// 剩余可预提天数
						writeBackVO.setActivedays(writeBackVO.getActivedays()
								- withholdingDays);
						// 已预提金额
						writeBackVO.setUsedmny(writeBackVO.getUsedmny().add(
								withholdingmny));
						// 剩余可预提金额
						writeBackVO.setActivemny(writeBackVO.getActivemny()
								.sub(withholdingmny));
						writeBackList.add(writeBackVO);
					}
				} else {
					// 最后一次预提日期
					UFDate lastBilldate = map.get("withholdingdate") == null ? null
							: new UFDate(map.get("withholdingdate").toString());
					// 历史累计预提金额
					UFDouble lastSumMny = new UFDouble(map
							.get("withholdingmny").toString());
					// 1.如果是最后一个月，直接等于合同金额-历史累计预提金额
					if (inputMonth == conenddate.getMonth()
							&& inputYear == conenddate.getYear()) {
						UFDouble withholdingmny = agg.getParentVO()
								.getContractmny().sub(lastSumMny);
						agg.getParentVO().setWithholdingmny(withholdingmny);
						agg.getParentVO().setWithholdingdate(conenddate);
						if (UFDouble.ZERO_DBL.compareTo(withholdingmny) >= 0) {
							continue;
						}
						// 设置预提开始日期
						agg.getParentVO().setStartdate(
								lastBilldate.getDateAfter(1));
						// 设置预提结束日期
						agg.getParentVO().setEnddate(conenddate);
						// 设置预提天数
						int withholdingDays = UFDate.getDaysBetween(agg
								.getParentVO().getStartdate(), agg
								.getParentVO().getEnddate()) + 1;
						agg.getParentVO().setWithholdingdays(withholdingDays);
						// 回写信息
						SubagreementVO writeBackVO = srcpk2srcvo.get(
								agg.getParentVO().getCsourceid()).getParentVO();
						// 已预提天数
						writeBackVO.setUseddays(writeBackVO.getUseddays()
								+ withholdingDays);
						// 剩余可预提天数
						writeBackVO.setActivedays(writeBackVO.getActivedays()
								- withholdingDays);
						// 已预提金额
						writeBackVO.setUsedmny(writeBackVO.getUsedmny().add(
								withholdingmny));
						// 剩余可预提金额
						writeBackVO.setActivemny(writeBackVO.getActivemny()
								.sub(withholdingmny));
						writeBackList.add(writeBackVO);
					} else {
						// 2.前期未计提天数=当前日期 - 最后一次预提日期
						// 设置预提开始日期
						agg.getParentVO().setStartdate(
								lastBilldate.getDateAfter(1));
						// 设置预提结束日期
						agg.getParentVO().setEnddate(inputdate);
						int withholdingDays = UFDate.getDaysBetween(agg
								.getParentVO().getStartdate(), agg
								.getParentVO().getEnddate()) + 1;
						agg.getParentVO().setWithholdingdays(withholdingDays);
						// 预提金额
						UFDouble withholdingmny = contractmny.div(new UFDouble(totalDays),2)
								.multiply(new UFDouble(withholdingDays),2);
						if (UFDouble.ZERO_DBL.compareTo(withholdingmny) >= 0) {
							continue;
						}
						agg.getParentVO().setWithholdingmny(withholdingmny);
						agg.getParentVO().setWithholdingdate(inputdate);
						// 回写信息
						SubagreementVO writeBackVO = srcpk2srcvo.get(
								agg.getParentVO().getCsourceid()).getParentVO();
						// 已预提天数
						writeBackVO.setUseddays(writeBackVO.getUseddays()
								+ withholdingDays);
						// 剩余可预提天数
						writeBackVO.setActivedays(writeBackVO.getActivedays()
								- withholdingDays);
						// 已预提金额
						writeBackVO.setUsedmny(writeBackVO.getUsedmny().add(
								withholdingmny));
						// 剩余可预提金额
						writeBackVO.setActivemny(writeBackVO.getActivemny()
								.sub(withholdingmny));
						writeBackList.add(writeBackVO);
					}
				}

				opdata.add((AggWithholdingVO) destvo);
			}
			// 调用后台脚本，保存目标数据
			if (opdata.size() > 0) {
				NCLocator
						.getInstance()
						.lookup(IPFBusiAction.class)
						.processBatch("SAVEBASE", "FE02",
								opdata.toArray(new AggWithholdingVO[0]), null,
								null, null);
			}
			// 回写上游已预提金额，注意删除操作也是需要回写的
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
			// 闰年29，平年28
			// 能被4整除不能被100整除或者能被400整除
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
 * 查询输入框
 * 
 */
class QueryInputDialog extends UIDialog {

	public QueryInputDialog() {
		super();
		initContentPanel();
	}

	/**
	 * 初始化主面板
	 */
	private void initContentPanel() {

		JPanel jPanel = new JPanel();
		// 添加下拉框
		jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		jPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JLabel labelForYear = new JLabel("预提年份:");
		jPanel.add(labelForYear);
		comboBoxForYear = new JComboBox();
		UFDate busiDate = AppContext.getInstance().getBusiDate();
		int year = busiDate.getYear();
		int month = busiDate.getMonth();
		for (int i = year - 5; i < (year + 5); i++) {
			comboBoxForYear.addItem(i);
		}
		jPanel.add(comboBoxForYear);
		JLabel jLabelForMouth = new JLabel("预提月份");
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
		return "查询条件输入框";
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
			ExceptionUtils.wrappBusinessException("请选择预提年份");
		}
		this.setYear(Integer.parseInt(selectedItemForYear.toString()));

		Object selectedItemForMouth = this.comboBoxForMouth.getSelectedItem();
		if (selectedItemForMouth == null) {
			ExceptionUtils.wrappBusinessException("请选择预提月份");
		}
		this.setMouth(Integer.parseInt(selectedItemForMouth.toString()));
		// 关闭标志
		this.setCloseOK(UFBoolean.TRUE);
	}

	public UFBoolean getCloseOK() {
		return this.closeOK;
	}

	public void setCloseOK(UFBoolean closeOK) {
		this.closeOK = closeOK;
	}

}
