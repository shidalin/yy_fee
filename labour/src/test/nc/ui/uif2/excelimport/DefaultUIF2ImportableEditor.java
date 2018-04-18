package nc.ui.uif2.excelimport;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;

import nc.itf.trade.excelimport.ExportDataInfo;
import nc.itf.trade.excelimport.IImportableEditor;
import nc.itf.trade.excelimport.ImportableInfo;
import nc.ui.pub.bill.BillData;
import nc.ui.trade.excelimport.InputItem;
import nc.ui.trade.excelimport.InputItemCreator;
import nc.ui.trade.excelimport.convertor.DefaultDataConvertor;
import nc.ui.trade.excelimport.convertor.IEditorData2AggVOConvertor;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.ActionInterceptor;
import nc.ui.uif2.editor.IBillCardPanelEditor;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ExtendedAggregatedValueObject;
import nc.vo.trade.excelimport.processor.IVOProcessor;


/**
 * UIF2对支持导入/导出功能编辑器的适配，对于只通过菜单进行导出/导入操作的节点，仅继承该类即可。
 * 
 * @author lkp
 *
 */
public  class DefaultUIF2ImportableEditor implements IImportableEditor {
 
	private NCAction saveAction = null;
	
	private NCAction cancelAction = null;
	
	private NCAction addAction = null;
									
	private Exception ex;    //保存过程中通过拦截器将异常抛出 
	
	private IVOProcessor processor;// VO处理器
	
	private IBillCardPanelEditor billcardPanelEditor = null;
	
	private AbstractUIAppModel appModel = null;
	
	private IEditorData2AggVOConvertor dataConvertor = null;

	protected NCAction createAddAction()
	{
		return null;
	}
	
	protected NCAction createSaveAction()
	{
		return null;
	} 
	
	protected NCAction createCancelAction()
	{
		return null;
	}
	
	protected IBillCardPanelEditor createBillCardPanelEditor()
	{
		return null;
	}
	
	protected AbstractUIAppModel createAppModel()
	{
		return null;
	}
	
	/**
	 * 创建VO处理器(用于根据转换规则处理VO数据)
	 * 如果VO不需要进行处理，可以返回 null
	 */
	protected  IVOProcessor createVOProcessor() {
		return null;
	}
	 
	
	/**
	 * 新增操作
	 * 
	 * @see nc.itf.trade.excelimport.IImportableEditor#addNew()
	 */
	public void addNew() {
		try {
			getAddAction().actionPerformed(null);
		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage());
		}
	} 
	
	/**
	 * 设值操作
	 * 
	 * 标准的操作步骤如下：
	 * 1，转型VO为ExtendedAggregatedValueObject
	 * 2，根据转换规则处理VO的相关属性值
	 * 3，将处理后的VO值设置到界面上
	 */
	public void setValue(Object obj) {
		
		ExtendedAggregatedValueObject aggvo = (ExtendedAggregatedValueObject) obj;
		if (getVOProcessor() != null) {
			getVOProcessor().processVO(aggvo);
		}
		setProcessedVO(aggvo);
	}

	/**
	 * 返回VO处理器
	 */
	private IVOProcessor getVOProcessor() {
		
		if (processor == null) {
			processor = createVOProcessor();
		}
		return processor;
	}
	
	/**
	 * 将根据转换规则处理后的参数VO设置到界面上
	 * 默认直接设置到单据模板界面，如有特殊附加需求，重写该方法。
	 * 
	 * @param eavo
	 *            根据转换规则处理后的VO
	 */
	protected  void setProcessedVO(ExtendedAggregatedValueObject eavo) {
		
		if(getBillcardPanelEditor() != null)
		{
			BillData bd = getBillcardPanelEditor().getBillCardPanel().getBillData();
			bd.setImportBillValueVO(eavo);
		}
	}
	
	/**
	 * 取消操作
	 */
	public void cancel() {
		try {
			if(getCancelAction().isEnabled())
				getCancelAction().actionPerformed(new ActionEvent(getCancelAction(),0,null));//防止cancelAction弹框询问
		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage());
		}
	} 

	/**
	 * 保存操作
	 */
	public void save() throws Exception {

		NCAction saveAction = getSaveAction();
		if (saveAction == null)
			return;
		IExceptionHandler handler = getSaveAction().getExceptionHandler();
		saveAction.setExceptionHandler(null);
		if (!saveAction.isEnabled()) {
			return;
		}
		ActionInterceptor origIn = saveAction.getInterceptor();
		ActionInterceptor newIn = new ActionInterceptor() {

			@Override
			public boolean afterDoActionFailed(Action action,
					ActionEvent e, Throwable ex) {
				setEx((Exception) ex);
				return false;
			}

			@Override
			public boolean beforeDoAction(Action action, ActionEvent e) {
				return true;
			}

			@Override
			public void afterDoActionSuccessed(Action action, ActionEvent e) {
			}
		};//add by hulianga 让异常往外抛出来，防止NCAction将异常吞掉
		saveAction.setInterceptor(newIn);
		saveAction.actionPerformed(null);
		if(getEx() != null){
			Exception ex1 = getEx();
			setEx(null);
			saveAction.setInterceptor(origIn);
			throw new BusinessException(ex1.getMessage());
		}
		saveAction.setExceptionHandler(handler);
		saveAction.setInterceptor(origIn);
	}
	
	/**
	 * 返回AddAction对象
	 */
	public NCAction getAddAction() {
		
		if(addAction == null)
			addAction = createAddAction();
		return this.addAction;
	}

	/**
	 * 返回SaveAction对象
	 */
	public NCAction getSaveAction() {
		
		if(this.saveAction ==null)
			saveAction = createSaveAction();
		return this.saveAction;
	}
	
	/**
	 * 返回CancelAction对象
	 */
	public NCAction getCancelAction() {
		
		if(cancelAction == null)
			cancelAction = createCancelAction();
		return this.cancelAction;
	}

	public void setSaveAction(NCAction saveAction) {
		this.saveAction = saveAction;
	}

	public void setCancelAction(NCAction cancelAction) {
		this.cancelAction = cancelAction;
	}

	public void setAddAction(NCAction addAction) {
		this.addAction = addAction;
	}

	public IBillCardPanelEditor getBillcardPanelEditor() {
		
		if(billcardPanelEditor == null)
			billcardPanelEditor = createBillCardPanelEditor();
		return billcardPanelEditor;
	}

	public void setBillcardPanelEditor(IBillCardPanelEditor billcardPanelEditor) {
		this.billcardPanelEditor = billcardPanelEditor;
	}

	@Override
	public ImportableInfo getImportableInfo() {
		return new ImportableInfo();
	}

	/**
	 * 默认实现导出所有表头/表体/表尾，如有导出项有特殊需求，覆盖。
	 * 
	 */
	@Override
	public List<InputItem> getInputItems() {

		if(getBillcardPanelEditor() != null)
		{
			return InputItemCreator.getInputItems(getBillcardPanelEditor().getBillCardPanel().getBillData(), false);
		}
		return null;
	}
 
	/**
	 * 默认导出数据实现
	 * 
	 */
	public ExportDataInfo getValue(List<InputItem> exportItems) {
		
		Object[] vos = getSelectedObject();
		BillData billData = getBillcardPanelEditor().getBillCardPanel().getBillData();
		ExtendedAggregatedValueObject[] aggvos = getDataConvertor().convertDataFromEditorData(billData, vos, exportItems);
		return new ExportDataInfo(beforeExport(aggvos));
	}
	
	protected ExtendedAggregatedValueObject[] beforeExport(ExtendedAggregatedValueObject[] aggvos)
	{
		return aggvos;
	}
	
 
	public JComponent getJComponent() {
		return getBillcardPanelEditor().getBillCardPanel();
	}
	
	protected Object[] getSelectedObject()
	{
		if(getAppModel() instanceof BillManageModel)
		{
			BillManageModel mm = (BillManageModel)getAppModel();
			if(mm.getSelectedOperaDatas() != null)
//				return mm.getSelectedOperaDatas();
				return ImExPortHelper.getNoneLazyLoadingData(mm, getBillcardPanelEditor());
			else
				return new Object[] {mm.getSelectedData()};
		}
		else if(getAppModel() instanceof BatchBillTableModel)
		{
			BatchBillTableModel bm = (BatchBillTableModel)getAppModel();
			if(bm.getSelectedOperaDatas() != null)
				return bm.getSelectedOperaDatas();
			else
				return new Object[] {bm.getSelectedData()};
		}else
			return new Object[] {getAppModel().getSelectedData()};
	}
	
	public AbstractUIAppModel getAppModel() {
		
		if(this.appModel == null)
			appModel = createAppModel();
		return appModel;
	}

	public void setAppModel(AbstractUIAppModel model) {
		this.appModel = model;
	}
	
	public IEditorData2AggVOConvertor getDataConvertor() {
		if (this.dataConvertor == null) {
			this.dataConvertor = new DefaultDataConvertor();
		}
		return dataConvertor;
	}

	public void setDataConvertor(IEditorData2AggVOConvertor dataConvertor) {
		this.dataConvertor = dataConvertor;
	}
	
	public int getExportCount() {
		return 1;
	}

	public Exception getEx() {
		return ex;
	}

	public void setEx(Exception ex) {
		this.ex = ex;
	}
}