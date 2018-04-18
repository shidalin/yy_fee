package nc.ui.bill.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.uap.IUAPQueryBS;
import nc.md.model.MetaDataRuntimeException;
import nc.pub.templet.converter.util.TemplateVOConverterUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UITabbedPane;
import nc.ui.pub.beans.UITree;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.layout.TableLayout;
import nc.ui.pub.beans.util.NCOptionPane;
import nc.ui.pub.bill.BillTemplateHelper;
import nc.ui.pub.metadata.CreatMetaDataTree;
import nc.ui.pub.metadata.MetaDataTreeNode;
import nc.ui.pub.metadata.TreeExpansion;
import nc.ui.pub.pftemplate.chooser.TemplateChooser;
import nc.ui.pub.pftemplate.chooser.TemplateChooserConfig;
import nc.ui.pub.pftemplate.chooser.Validator;
import nc.ui.sf.functree.MenuTreeCellRender;
import nc.ui.sf.functree.NCMenuTreeCreator;
import nc.vo.bill.pub.AllBillTypeRefModel;
import nc.vo.database.support.TableManager;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.pftemplate.SystemplateVO;
import nc.vo.pub.templet.translator.BillTranslator;
import nc.vo.sm.funcreg.FuncRegisterVO;
import nc.vo.sm.funcreg.MenuItem;

/**
 * 单据模板管理-数据选择
 * 
 * 创建日期:(2002-9-9 10:04:36)
 */
@SuppressWarnings("serial")
public class DataSourcePanel extends JPanel implements
		java.awt.event.ActionListener, java.awt.event.KeyListener,
		javax.swing.event.TreeSelectionListener {

	final static String MSG_CANCEL = "cancel";

	private DefaultListModel listModelChildTable = null;

	private DefaultListModel listModelParentTable = null;

	private DefaultListModel listModelTables = null;

	private nc.ui.pub.beans.UIButton ivjJButtonChildSelect = null;

	private nc.ui.pub.beans.UIButton ivjJButtonChildUnSelect = null;

	private nc.ui.pub.beans.UIButton ivjJButtonParentSelect = null;

	private nc.ui.pub.beans.UIButton ivjJButtonParentUnSelect = null;

	private nc.ui.pub.beans.UIButton ivjJButtonShowTables = null;

	private JList ivjJListChildTables = null;

	private JList ivjJListParentTables = null;

	private JList ivjJListTables = null;

	private JPanel ivjJPanelTemplate = null;

	private JPanel ivjJPanelTableFilter = null;

	private JPanel ivjJPanelTableSelect = null;

	private JPanel ivjJPanelBillTypeCode = null;

	private UIScrollPane ivjUIScrollPaneNCMenu = null;

	private JPanel ivjJPanelEast = null;

	private JPanel ivjJPanelWest = null;

	private JScrollPane ivjJScrollPaneTables = null;

	private JScrollPane ivjJScrollPaneParentTable = null;

	private JScrollPane ivjJScrollPaneChildTable = null;

	private nc.ui.pub.beans.UIButton ivjUIButtonAdd = null;

	private nc.ui.pub.beans.UIButton ivjUIButtonCopy = null;

	private nc.ui.pub.beans.UIButton ivjUIButtonCopyFrom = null;

	private nc.ui.pub.beans.UIButton ivjUIButtonDel = null;

	private nc.ui.pub.beans.UIButton ivjUIButtonEdit = null;

	private nc.ui.pub.beans.UIButton ivjUIButtonShow = null;

	private nc.ui.pub.beans.UIButton ivjUIButtonAssign = null;

	private nc.ui.pub.beans.UIButton ivjUIButtonShowTemplet = null;

	private nc.ui.pub.beans.UIButton ivjUIButtonImport = null;

	private nc.ui.pub.beans.UIButton ivjUIButtonExport = null;

	private nc.ui.pub.beans.UILabel ivjUILabelBillTypeCode = null;

	private nc.ui.pub.beans.UIPanel ivjUIPanelAction = null;

	private nc.ui.pub.beans.UIRefPane ivjUIRefPaneBillType = null;

	private nc.ui.pub.beans.UIScrollPane ivjUIScrollPaneTreeBillTemplet = null;

	private nc.ui.pub.beans.UITree ivjUITreeBillTemplet = null;

	// tablemanager
	private TableManager tableman = new TableManager();

	// BilltypeCode
	private String billtypeCode = null;

	private String funccode = null;

	private static int DATASOURCE = 0;

	private static int MEATDATA = 1;

	/**
	 * [nodelevel + NodeStr, headVO] if headVO is default templetheadvo then
	 * ["0"+PK_billtypecode,headVO] else if headVO is custom templetHeadVO then
	 * ["1"+billtempletName,headVO]
	 */
	private Hashtable<DefaultMutableTreeNode, BillTempletHeadVO> hashTreeNode = new Hashtable<DefaultMutableTreeNode, BillTempletHeadVO>();

	// 当前节点
	private MutableTreeNode node = null;

	// parent of this panel
	private MainPanel parent = null;

	// 根节点
	DefaultMutableTreeNode root = new DefaultMutableTreeNode("BILLTEMPLET");

	private nc.ui.pub.beans.UICheckBox ivjUICheckBoxFilter = null;

	private nc.ui.pub.beans.UITextField ivjUITextFieldTableFilter = null;

	private String billcaption;

	private String billname;

	private String nodecode = null;

	/**
	 * DataSourcePanel 构造子注解.
	 */
	public DataSourcePanel(MainPanel parent) {
		super();
		this.parent = parent;
		initialize();
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		// set buttons state false at first
		setButtonsState(false);
		Object o = e.getSource();

		// show tables
		if (o == getJButtonShowTables()) {// || o ==
			// getJTextFieldTableFilter()) {
			onButtonActionShowTables();
		}
		// parent table select button
		else if (o == getJButtonParentSelect()) {
			onButtonActionSelectParentTable();
		}
		// parent table unselect button
		else if (o == getJButtonParentUnSelect()) {
			onButtonActionUnSelectParentTable();
		}
		// child table select button
		else if (o == getJButtonChildSelect()) {
			onButtonActionSelectChildTable();
		}
		// child table unselect button
		else if (o == getJButtonChildUnSelect()) {
			onButtonActionUnSelectChildTable();
		}
		// filter checkbox button
		else if (o == getUICheckBoxFilter()) {
			onCheckBoxTableFilter();
		}
		// init billtypecode and show templet
		else if (o == getUIButtonShowTemplet()) {
			onButtonActionShowTemplets();
		} else if (o == getUIButtonShow()) {
			onButtonActionBilltempletShow();
//		} else if (o == getUIButtonAssign()) {
//			onButtonActionAssign();
		}
		// add defaultTemplet and customBilltemplet
		else if (o == getUIButtonAdd()) {
			onButtonActionBilltempletAdd();
		}
		// edit default and custombilltemplet
		else if (o == getUIButtonEdit()) {
			onButtonActionBilltempletEdit();
		}
		// del default and custom billtemplet
		else if (o == getUIButtonDel()) {
			onButtonActionBilltempletDel();
		} else if (o == getUIButtonCopy()) {
			onButtonActionBilltempletCopy();
		} else if (o == getUIButtonCopyFrom()) {
			onButtonActionBilltempletCopyFrom();
		} else if (o == getUIButtonExport()) {
			onButtonActionBilltempletExport();
		} else if (o == getUIButtonImport()) {
			onButtonActionBilltempletImport();
		}
		setButtonsState();
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	public String addChildTable(String parentTableName, String childTableName) {
		if (listModelParentTable.contains(childTableName)) {
			Logger.debug("parentTable=" + parentTableName + "childTable="
					+ childTableName + ",子表和主表不能重复!!");
			return nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
					"UPP_Bill-000369")/*
									 * @res "子表和主表不能重复!"
									 */;
		}
		if (listModelChildTable.contains(childTableName)) {
			Logger.debug(childTableName + "该表已经存在!");
			return childTableName
					+ nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
							"UPP_Bill-000370")/*
											 * @res "该表已经存在!"
											 */;
		}
		try {
			tableman.addChildTable(parentTableName, childTableName);
			listModelChildTable.addElement(childTableName);
			getJListChildTables().setSelectedIndex(0);
		} catch (Exception e) {
			Logger.debug(e);
		}
		return null;
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	public String addParentTable(String parentTableName) {
		if (listModelParentTable.getSize() > 0) {
			return nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
					"UPP_Bill-000371")/*
									 * @res "不支持多主表!"
									 */;
		}
		listModelParentTable.addElement(parentTableName);
		getJListParentTables().setSelectedIndex(0);
		try {
			tableman.addTable(parentTableName);
		} catch (Exception e) {
			Logger.debug(e);
		}
		return null;
	}

	/**
	 * 清除待选择单据模板树 创建日期:(2002-9-10 11:07:20)
	 */
	private void clearTreeNodes() {
		root.removeAllChildren();
		((DefaultTreeModel) getUITreeBillTemplet().getModel()).reload();
		hashTreeNode.clear();
		node = null;
	}

	/**
	 * 创建日期:(2002-9-19 8:45:38)
	 */
	private String delBillTempletVO(String id) {
		try {
			// return BillTempletBO_Client.deleteWithValidate(id);
			return BillTemplateHelper.deleteWithValidate(id);
		} catch (Exception e) {
			Logger.debug("单据模板删除失败!");

		}
		return null;
	}

	/**
	 * 创建日期:(2002-9-9 14:41:44)
	 */
	private BillTempletVO getBillTempletVO(String id, String billTypeCode,
			String metadataclass) {
		// String id = dataPanel.getId();
		BillTempletVO vo = getBillTempletVOFromDB(id);

		// 创建新VO
		if (vo == null) {
			BillTempletHeadVO headvo = new BillTempletHeadVO();
			headvo.setNodecode(nodecode);
			headvo.setBillTempletCaption(billcaption);
			headvo.setMetadataclass(metadataclass);
			headvo.setModulecode(getCurrentHeadVO() == null ? null
					: getCurrentHeadVO().getModulecode());

			vo = new BillTempletVO();
			vo.setParentVO(headvo);

		}
		// null)
		vo.getHeadVO().setPkBillTypeCode(billTypeCode);
		vo.getHeadVO().setPkCorp(getPk_corp());
		return vo;
	}

	private BillTempletVO getBillTempletVOFromDB(String id) {
		BillTempletVO vo = null;
		if (id != null && id.trim().length() > 0) {
			try {
				vo = BillTemplateHelper.findTempletData(id, getPk_corp());
			} catch (Exception e) {
				Logger.debug(e);
			}
		}
		return vo;
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private String getBilltypeCode() {
		// return billtypeCode;
		if (getCurrentHeadVO() != null)
			return getCurrentHeadVO().getPkBillTypeCode();
		else
			return billtypeCode;
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	public void setBilltypeCode(String billtype) {
		billtypeCode = billtype;
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	public BillTempletHeadVO getCurrentHeadVO() {
		if (node == null || hashTreeNode == null)
			return null;
		return (BillTempletHeadVO) hashTreeNode.get(node);
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private BillTempletHeadVO getDefaultHeadVO() {
		if (node == null || hashTreeNode == null)
			return null;
		if (node.getParent() == root)
			return getCurrentHeadVO();
		else
			return (BillTempletHeadVO) hashTreeNode.get(node.getParent());
	}

	/**
	 * 创建日期:(2002-9-19 8:51:23)
	 * 
	 * @return java.lang.String
	 */
	private java.lang.String getId(int type, boolean isCopy) {
		BillTempletHeadVO head = null;
		switch (type) {
		case MainPanel.TEMPLET_ADD_CUSTOM:
			if (isCopy)
				head = getCurrentHeadVO();
			else
				head = getDefaultHeadVO();
			break;
		case MainPanel.TEMPLET_MODIFY_CUSTOM:
			head = getCurrentHeadVO();
			break;
		case MainPanel.TEMPLET_ADD_DEFAULT:
			break;
		case MainPanel.TEMPLET_MODIFY_DEFAULT:
			head = getCurrentHeadVO();
			break;
		case MainPanel.TEMPLET_SHOW_DEFAULT:
			head = getCurrentHeadVO();
			break;
		default:
			return null;
		}
		if (head != null)
			return head.getPrimaryKey();
		else
			return null;
	}

	/**
	 * 返回 JButton4 特性值.
	 * 
	 * @return javax.swing.JButton
	 */
	private nc.ui.pub.beans.UIButton getJButtonChildSelect() {
		if (ivjJButtonChildSelect == null) {
			ivjJButtonChildSelect = new nc.ui.pub.beans.UIButton();
			ivjJButtonChildSelect.setName("JButtonChildSelect");
			ivjJButtonChildSelect.setText(">");
			ivjJButtonChildSelect.setEnabled(false);
			ivjJButtonChildSelect.setMargin(new java.awt.Insets(2, 5, 2, 5));
			ivjJButtonChildSelect
					.setMaximumSize(new java.awt.Dimension(500, 22));
		}
		return ivjJButtonChildSelect;
	}

	/**
	 * 返回 JButton5 特性值.
	 * 
	 * @return javax.swing.JButton
	 */
	private nc.ui.pub.beans.UIButton getJButtonChildUnSelect() {
		if (ivjJButtonChildUnSelect == null) {
			ivjJButtonChildUnSelect = new nc.ui.pub.beans.UIButton();
			ivjJButtonChildUnSelect.setName("JButtonChildUnSelect");
			ivjJButtonChildUnSelect.setText("<");
			ivjJButtonChildUnSelect.setEnabled(false);
			ivjJButtonChildUnSelect.setMargin(new java.awt.Insets(2, 5, 2, 5));
			ivjJButtonChildUnSelect.setMaximumSize(new java.awt.Dimension(500,
					22));
		}
		return ivjJButtonChildUnSelect;
	}

	/**
	 * 选主表按钮.
	 * 
	 * @return javax.swing.JButton
	 */
	private nc.ui.pub.beans.UIButton getJButtonParentSelect() {
		if (ivjJButtonParentSelect == null) {
			ivjJButtonParentSelect = new nc.ui.pub.beans.UIButton();
			ivjJButtonParentSelect.setName("JButtonParentSelect");
			ivjJButtonParentSelect.setText(">");
			ivjJButtonParentSelect.setEnabled(false);
			ivjJButtonParentSelect.setMargin(new java.awt.Insets(2, 5, 2, 5));
			ivjJButtonParentSelect.setMaximumSize(new java.awt.Dimension(500,
					22));
		}
		return ivjJButtonParentSelect;
	}

	/**
	 * 返回 JButton3 特性值.
	 * 
	 * @return javax.swing.JButton
	 */
	private nc.ui.pub.beans.UIButton getJButtonParentUnSelect() {
		if (ivjJButtonParentUnSelect == null) {
			ivjJButtonParentUnSelect = new nc.ui.pub.beans.UIButton();
			ivjJButtonParentUnSelect.setName("JButtonParentUnSelect");
			ivjJButtonParentUnSelect.setText("<");
			ivjJButtonParentUnSelect.setEnabled(false);
			ivjJButtonParentUnSelect.setMargin(new java.awt.Insets(2, 5, 2, 5));
			ivjJButtonParentUnSelect.setMaximumSize(new java.awt.Dimension(500,
					22));
		}
		return ivjJButtonParentUnSelect;
	}

	/**
	 * 列表显示.
	 * 
	 * @return javax.swing.JButton
	 */
	private nc.ui.pub.beans.UIButton getJButtonShowTables() {
		if (ivjJButtonShowTables == null) {
			ivjJButtonShowTables = new nc.ui.pub.beans.UIButton();
			ivjJButtonShowTables.setName("JButtonShowTables");
			ivjJButtonShowTables.setPreferredSize(new java.awt.Dimension(100,
					20));
			ivjJButtonShowTables.setMnemonic('s');
			ivjJButtonShowTables.setText(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("_Bill", "UPP_Bill-000372")/*
															 * @res "列表显示"
															 */
					+ "(s)");
			ivjJButtonShowTables.setMargin(new java.awt.Insets(2, 2, 2, 2));
		}
		return ivjJButtonShowTables;
	}

	private javax.swing.JLabel createJLabel(String text) {
		JLabel ivjJLabel1 = new javax.swing.JLabel();
		ivjJLabel1.setText(text);
		return ivjJLabel1;
	}

	/**
	 * 返回 JList3 特性值.
	 * 
	 * @return javax.swing.JList
	 */
	private javax.swing.JList getJListChildTables() {
		if (ivjJListChildTables == null) {
			ivjJListChildTables = new javax.swing.JList();
		}
		return ivjJListChildTables;
	}

	/**
	 * 返回 JList2 特性值.
	 * 
	 * @return javax.swing.JList
	 */
	private javax.swing.JList getJListParentTables() {
		if (ivjJListParentTables == null) {
			ivjJListParentTables = new javax.swing.JList();
		}
		return ivjJListParentTables;
	}

	/**
	 * 返回 JList1 特性值.
	 * 
	 * @return javax.swing.JList
	 */
	private javax.swing.JList getJListTables() {
		if (ivjJListTables == null) {
			ivjJListTables = new javax.swing.JList();
		}
		return ivjJListTables;
	}

	/**
	 * 已定义模板.
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelTemplate() {
		if (ivjJPanelTemplate == null) {
			ivjJPanelTemplate = new javax.swing.JPanel();
			ivjJPanelTemplate.setName("JPanel15");
			ivjJPanelTemplate.setLayout(new BorderLayout());
			ivjJPanelTemplate.setBorder(BorderFactory.createEmptyBorder(0, 2,
					2, 2));
			getJPanelTemplate().add(
					createJLabel(nc.ui.ml.NCLangRes.getInstance().getStrByID(
							"_Bill", "UPP_Bill-000374")/*
														 * @res "已定义模板"
														 */),
					BorderLayout.NORTH);
			getJPanelTemplate().add(getUIScrollPaneTreeBillTemplet(),
					BorderLayout.CENTER);
		}
		return ivjJPanelTemplate;
	}

	/**
	 * 表过滤.
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelTableFilter() {
		if (ivjJPanelTableFilter == null) {
			ivjJPanelTableFilter = new javax.swing.JPanel();
			ivjJPanelTableFilter.setName("JPanelTableFilter");
			ivjJPanelTableFilter.setLayout(new FlowLayout(FlowLayout.LEFT));
			getJPanelTableFilter().add(getUICheckBoxFilter(),
					getUICheckBoxFilter().getName());
			getJPanelTableFilter().add(getUITextFieldTableFilter(),
					getUITextFieldTableFilter().getName());
			getJPanelTableFilter().add(getJButtonShowTables(),
					getJButtonShowTables().getName());
		}
		return ivjJPanelTableFilter;
	}

	/**
	 * 数据库表选择.
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelTableSelect() {
		if (ivjJPanelTableSelect == null) {
			ivjJPanelTableSelect = new javax.swing.JPanel();
			ivjJPanelTableSelect.setName("JPanelDataSource");
			double border = 2;
			double size[][] = {
					{ border, TableLayout.FILL, 38, 1, TableLayout.FILL, border }, // Columns
					{ border, 20, 20, 20, 100, 20, 20, 20, TableLayout.FILL, 1 } // Rows
			};
			ivjJPanelTableSelect.setLayout(new TableLayout(size));
			ivjJPanelTableSelect.add(createJLabel(nc.ui.ml.NCLangRes
					.getInstance().getStrByID("_Bill", "UPP_Bill-000167")/*
																		 * @res
																		 * "数据库表"
																		 */),
					"1,1,1,1");
			ivjJPanelTableSelect.add(createJLabel(nc.ui.ml.NCLangRes
					.getInstance().getStrByID("_Bill", "UPP_Bill-000001")/*
																		 * @res
																		 * "主表"
																		 */),
					"4,1,4,1");
			ivjJPanelTableSelect.add(getJScrollPaneTables(), "1,2,1,8");
			ivjJPanelTableSelect.add(getJButtonParentSelect(), "2,2,2,2");
			ivjJPanelTableSelect.add(getJButtonParentUnSelect(), "2,3,2,3");
			ivjJPanelTableSelect.add(getJScrollPaneParentTable(), "4,2,4,4");
			ivjJPanelTableSelect.add(createJLabel(nc.ui.ml.NCLangRes
					.getInstance().getStrByID("_Bill", "UPP_Bill-000373")/*
																		 * @res
																		 * "子表"
																		 */),
					"4,5,4,5");
			ivjJPanelTableSelect.add(getJButtonChildSelect(), "2,6,2,6");
			ivjJPanelTableSelect.add(getJButtonChildUnSelect(), "2,7,2,7");
			ivjJPanelTableSelect.add(getJScrollPaneChildTable(), "4,6,4,8");
		}
		return ivjJPanelTableSelect;
	}

	/**
	 * 选择单据类型.
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelBillTypeCode() {
		if (ivjJPanelBillTypeCode == null) {
			ivjJPanelBillTypeCode = new javax.swing.JPanel();
			ivjJPanelBillTypeCode.setName("JPanelBillTypeCode");
			// ivjJPanelBillTypeCode.setBorder(new EtchedBorder());
			ivjJPanelBillTypeCode.setLayout(new FlowLayout(FlowLayout.LEFT));
			getJPanelBillTypeCode().add(getUILabelBillTypeCode(),
					getUILabelBillTypeCode().getName());
			getJPanelBillTypeCode().add(getUIRefPaneBillType(),
					getUIRefPaneBillType().getName());
			getJPanelBillTypeCode().add(getUIButtonShowTemplet(),
					getUIButtonShowTemplet().getName());

		}
		return ivjJPanelBillTypeCode;
	}

	/**
	 * 选择单据模板.
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelEast() {
		if (ivjJPanelEast == null) {
			ivjJPanelEast = new javax.swing.JPanel();
			ivjJPanelEast.setName("JPanelEast");
			ivjJPanelEast.setBorder(new LineBorder(Color.darkGray, 1));
			ivjJPanelEast.setLayout(new BorderLayout());
			if (isDebug())
				getJPanelEast()
						.add(getJPanelBillTypeCode(), BorderLayout.NORTH);
			getJPanelEast().add(getUIPanelAction(), BorderLayout.EAST);
			getJPanelEast().add(getJPanelTemplate(), BorderLayout.CENTER);
		}
		return ivjJPanelEast;
	}

	private UITabbedPane ivjJTabbedPaneSource = null;

	private UITabbedPane getTabbedPaneSource() {
		if (ivjJTabbedPaneSource == null) {
			ivjJTabbedPaneSource = new UITabbedPane();
			ivjJTabbedPaneSource.addTab(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("_Bill", "UPP_Bill-000545")/* "数据库" */,
					getDataSourcePanel());
			ivjJTabbedPaneSource.addTab(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("_Bill", "UPP_Bill-000546")/* "元数据" */,
					getMetaDataPanel());
		}

		return ivjJTabbedPaneSource;
	}

	private JPanel ivjJDatasourcePanel = null;

	/**
	 * 数据库表选择.
	 * 
	 * @return javax.swing.JPanel
	 */

	private JPanel getDataSourcePanel() {
		if (ivjJDatasourcePanel == null) {
			ivjJDatasourcePanel = new javax.swing.JPanel();
			ivjJDatasourcePanel.setName("JPanelWest");
			ivjJDatasourcePanel.setBorder(new LineBorder(Color.darkGray, 1));
			ivjJDatasourcePanel.setLayout(new BorderLayout());
			getDataSourcePanel()
					.add(getJPanelTableFilter(), BorderLayout.NORTH);
			getDataSourcePanel().add(getJPanelTableSelect(),
					BorderLayout.CENTER);
		}
		return ivjJDatasourcePanel;
	}

	private JPanel ivjJMetaDataPanel = null;

	/**
	 * 元数据选择.
	 * 
	 * @return javax.swing.JPanel
	 */

	private JPanel getMetaDataPanel() {
		if (ivjJMetaDataPanel == null) {
			ivjJMetaDataPanel = new JPanel();
			// ivjJMetaDataPanel.setName("JPanelWest");
			// ivjJMetaDataPanel.setBorder(new LineBorder(Color.darkGray, 1));
			ivjJMetaDataPanel.setLayout(new BorderLayout());
			getMetaDataPanel()
					.add(getMetaDataScrollPane(), BorderLayout.CENTER);
		}
		return ivjJMetaDataPanel;
	}

	private JScrollPane ivjMetaDataScrollPane = null;

	private JScrollPane getMetaDataScrollPane() {
		if (ivjMetaDataScrollPane == null) {
			ivjMetaDataScrollPane = new JScrollPane();
			ivjMetaDataScrollPane.setViewportView(getMetaDataTree());
			// ivjJMetaDataPanel.setName("JPanelWest");
			// ivjJMetaDataPanel.setBorder(new LineBorder(Color.darkGray, 1));
			// ivjJMetaDataPanel.setLayout(new BorderLayout());
			// getMetaDataPanel().add(getJPanel4(), BorderLayout.NORTH);
			// getMetaDataPanel().add(getJPanel5(), BorderLayout.CENTER);
		}
		return ivjMetaDataScrollPane;
	}

	private JTree ivjMetaDataTree = null;

	private DefaultTreeModel metadatatreemodel = null;

	private UITree ivjUITreeMenu = null;

	private JTree getMetaDataTree() {
		if (ivjMetaDataTree == null) {
			ivjMetaDataTree = new UITree();

			ivjMetaDataTree.addTreeExpansionListener(new TreeExpansion());

			DefaultTreeModel model = getMetaDataTreeModel();

			ivjMetaDataTree.setModel(model);

			ivjMetaDataTree.setRootVisible(false);
		}
		return ivjMetaDataTree;
	}

	private DefaultTreeModel getMetaDataTreeModel() {
		if (metadatatreemodel == null) {
			metadatatreemodel = CreatMetaDataTree
					.creatMetaDataMoudleTreeModel();
		}

		return metadatatreemodel;
	}

	private javax.swing.JPanel getJPanelWest() {
		if (ivjJPanelWest == null) {
			ivjJPanelWest = new javax.swing.JPanel();
			ivjJPanelWest.setName("JPanelWest");
			ivjJPanelWest.setBorder(new LineBorder(Color.darkGray, 1));
			ivjJPanelWest.setLayout(new BorderLayout());
			if (isDebug())
				getJPanelWest().add(getTabbedPaneSource(), BorderLayout.CENTER);
			else
				getJPanelWest().add(getUIScrollPaneNCMenu(),
						BorderLayout.CENTER);

		}
		return ivjJPanelWest;
	}

	private UIScrollPane getUIScrollPaneNCMenu() {
		if (ivjUIScrollPaneNCMenu == null) {
			ivjUIScrollPaneNCMenu = new UIScrollPane();
			ivjUIScrollPaneNCMenu.setName("ivjUIScrollPaneNCMenu");
			ivjUIScrollPaneNCMenu.setMinimumSize(new java.awt.Dimension(3, 3));
			ivjUIScrollPaneNCMenu.setPreferredSize(new java.awt.Dimension(200,
					469));
			ivjUIScrollPaneNCMenu.setViewportView(getUITreeMenu());
		}
		return ivjUIScrollPaneNCMenu;
	}

	/**
	 * @return
	 */
	private UITree getUITreeMenu() {
		if (ivjUITreeMenu == null) {
			ivjUITreeMenu = new UITree();
			ivjUITreeMenu.setName("NodeTree");
			ivjUITreeMenu.setBounds(0, 0, 160, 120);
			// 设置树选择模式
			ivjUITreeMenu.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);
			// 设置功能树的外观
			ivjUITreeMenu.putClientProperty("JTree.lineStyle", "Angled");
			// 为功能节点树加入选取监听
			ivjUITreeMenu.addTreeSelectionListener(this);
			// nodeTree.setCellRenderer(FuncTreeCreator.getFuncTreeCellRender());
			ivjUITreeMenu.setCellRenderer(new MenuTreeCellRender());

			initNodeTree();
		}
		return ivjUITreeMenu;
	}

	/**
	 * 初始化功能节点树.
	 */
	private void initNodeTree() {
		try {
			getUITreeMenu().setRootVisible(false);
			getUITreeMenu().setShowsRootHandles(true);

			MenuItem[] userPowers = WorkbenchEnvironment.getInstance()
					.getUserPowers();
			DefaultTreeModel treemodel = NCMenuTreeCreator.getInstance()
					.createMenuTree(
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("_bill", "0_bill0016")/*
																	 * @res
																	 * "菜单节点"
																	 */,
							userPowers, null);
			getUITreeMenu().setModel(treemodel);

			// /** 树节点搜索器 */
			// new TreeNodeSearcher(getUITreeMenu()) {
			// protected boolean isMatch(String inputText, Object node) {
			// FuncRegisterVO vo = (FuncRegisterVO) ((DefaultMutableTreeNode)
			// node)
			// .getUserObject();
			// String name = vo.getFun_name();
			// if (name == null)
			// return false;
			// String transName = FuncRegTree.getFunTransStr(vo
			// .getFuncode(), vo.getFun_name());
			// return transName.toLowerCase().indexOf(
			// inputText.toLowerCase()) != -1;
			// }
			// }.setInputHint(NCLangRes.getInstance().getStrByID("smcomm",
			// "UPP1005-000139"))/*
			// * @res "输入名称搜索:"
			// */;

		} catch (Exception ex) {
			Logger.debug(ex);
		}
	}

	/**
	 * 待选择表.
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPaneTables() {
		if (ivjJScrollPaneTables == null) {
			ivjJScrollPaneTables = new javax.swing.JScrollPane();
			ivjJScrollPaneTables.setName("JScrollPane1");
			getJScrollPaneTables().setViewportView(getJListTables());
		}
		return ivjJScrollPaneTables;
	}

	/**
	 * 选择主表.
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPaneParentTable() {
		if (ivjJScrollPaneParentTable == null) {
			ivjJScrollPaneParentTable = new javax.swing.JScrollPane();
			ivjJScrollPaneParentTable.setName("JScrollPane2");
			getJScrollPaneParentTable().setAutoscrolls(true);
			getJScrollPaneParentTable().setViewportView(getJListParentTables());
		}
		return ivjJScrollPaneParentTable;
	}

	/**
	 * 选择子表.
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPaneChildTable() {
		if (ivjJScrollPaneChildTable == null) {
			ivjJScrollPaneChildTable = new javax.swing.JScrollPane();
			ivjJScrollPaneChildTable.setName("JScrollPane3");
			getJScrollPaneChildTable().setAutoscrolls(true);
			getJScrollPaneChildTable().setViewportView(getJListChildTables());
		}
		return ivjJScrollPaneChildTable;
	}

	/**
	 * 创建日期:(2002-9-13 10:18:43)
	 * 
	 * @return java.lang.String
	 */
	private java.lang.String getPk_corp() {
		return parent.getPk_Corp();
	}

	/**
	 * 创建日期:(2002-9-13 9:05:00)
	 * 
	 * @return nc.ui.bill.tools.TableManager
	 */
	public TableManager getTableman() {
		return tableman;
	}

	/**
	 * 返回 UIButtonAdd 特性值.
	 * 
	 * @return nc.ui.pub.beans.UIButton
	 */
	private nc.ui.pub.beans.UIButton getUIButtonAdd() {
		if (ivjUIButtonAdd == null) {
			ivjUIButtonAdd = new nc.ui.pub.beans.UIButton();
			ivjUIButtonAdd.setName("UIButtonAdd");
			ivjUIButtonAdd.setIButtonType(0/** Java默认(自定义) */
			);
			ivjUIButtonAdd.setMnemonic('a');
			ivjUIButtonAdd.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"common", "UC001-0000002")/*
											 * @res "增加"
											 */
					+ "(a)");
			ivjUIButtonAdd.setMaximumSize(new java.awt.Dimension(100, 22));
			ivjUIButtonAdd.setEnabled(false);
			ivjUIButtonAdd.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjUIButtonAdd.setMinimumSize(new java.awt.Dimension(75, 22));
		}
		return ivjUIButtonAdd;
	}

	/**
	 * 返回 UIButtonCopy 特性值.
	 * 
	 * @return nc.ui.pub.beans.UIButton
	 */
	private nc.ui.pub.beans.UIButton getUIButtonCopy() {
		if (ivjUIButtonCopy == null) {
			ivjUIButtonCopy = new nc.ui.pub.beans.UIButton();
			ivjUIButtonCopy.setName("UIButtonCopy");
			ivjUIButtonCopy.setIButtonType(0/** Java默认(自定义) */
			);
			ivjUIButtonCopy.setMnemonic('c');
			ivjUIButtonCopy.setText(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("common", "UC001-0000043")/*
														 * @res "复制"
														 */
					+ "(c)");
			ivjUIButtonCopy.setMaximumSize(new java.awt.Dimension(100, 22));
			ivjUIButtonCopy.setEnabled(false);
			ivjUIButtonCopy.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjUIButtonCopy.setMinimumSize(new java.awt.Dimension(75, 22));
		}
		return ivjUIButtonCopy;
	}

	/**
	 * 返回 UIButtonCopyFrom 特性值.
	 * 
	 * @return nc.ui.pub.beans.UIButton
	 */
	private nc.ui.pub.beans.UIButton getUIButtonCopyFrom() {
		if (ivjUIButtonCopyFrom == null) {
			ivjUIButtonCopyFrom = new nc.ui.pub.beans.UIButton();
			ivjUIButtonCopyFrom.setName("UIButtonCopyFrom");
			ivjUIButtonCopyFrom.setIButtonType(0/** Java默认(自定义) */
			);
			ivjUIButtonCopyFrom.setMnemonic('f');
			ivjUIButtonCopyFrom.setText(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("common", "UC001-0000043")/*
														 * @res "复制"
														 */
					+ "...(f)");
			ivjUIButtonCopyFrom.setMaximumSize(new java.awt.Dimension(100, 22));
			ivjUIButtonCopyFrom
					.setPreferredSize(new java.awt.Dimension(70, 22));
			ivjUIButtonCopyFrom.setEnabled(false);
			ivjUIButtonCopyFrom.setMinimumSize(new java.awt.Dimension(75, 22));
			ivjUIButtonCopyFrom.setMargin(new java.awt.Insets(2, 2, 2, 2));
		}
		return ivjUIButtonCopyFrom;
	}

	/**
	 * 返回 UIButtonDel 特性值.
	 * 
	 * @return nc.ui.pub.beans.UIButton
	 */
	private nc.ui.pub.beans.UIButton getUIButtonDel() {
		if (ivjUIButtonDel == null) {
			ivjUIButtonDel = new nc.ui.pub.beans.UIButton();
			ivjUIButtonDel.setName("UIButtonDel");
			ivjUIButtonDel.setIButtonType(0/** Java默认(自定义) */
			);
			ivjUIButtonDel.setMnemonic('d');
			ivjUIButtonDel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"common", "UC001-0000039")/*
											 * @res "删除"
											 */
					+ "(d)");
			ivjUIButtonDel.setMaximumSize(new java.awt.Dimension(100, 22));
			ivjUIButtonDel.setEnabled(false);
			ivjUIButtonDel.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjUIButtonDel.setMinimumSize(new java.awt.Dimension(75, 22));
		}
		return ivjUIButtonDel;
	}

	/**
	 * 返回 UIButtonEdit 特性值.
	 * 
	 * @return nc.ui.pub.beans.UIButton
	 */
	private nc.ui.pub.beans.UIButton getUIButtonEdit() {
		if (ivjUIButtonEdit == null) {
			ivjUIButtonEdit = new nc.ui.pub.beans.UIButton();
			ivjUIButtonEdit.setName("UIButtonEdit");
			ivjUIButtonEdit.setIButtonType(0/** Java默认(自定义) */
			);
			ivjUIButtonEdit.setMnemonic('e');
			ivjUIButtonEdit.setText(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("common", "UC001-0000045")/*
														 * @res "修改"
														 */
					+ "(e)");
			ivjUIButtonEdit.setMaximumSize(new java.awt.Dimension(100, 22));
			ivjUIButtonEdit.setEnabled(false);
			ivjUIButtonEdit.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjUIButtonEdit.setMinimumSize(new java.awt.Dimension(75, 22));
		}
		return ivjUIButtonEdit;
	}

	/**
	 * 返回 UIButtonEdit 特性值.
	 * 
	 * @return nc.ui.pub.beans.UIButton
	 */
	private nc.ui.pub.beans.UIButton getUIButtonImport() {
		if (ivjUIButtonImport == null) {
			ivjUIButtonImport = new nc.ui.pub.beans.UIButton();
			ivjUIButtonImport.setName("UIButtonImport");
			ivjUIButtonImport.setIButtonType(0/** Java默认(自定义) */
			);
			ivjUIButtonImport.setMnemonic('i');
			ivjUIButtonImport.setText(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("uifactory", "UPPuifactory-000177")/*
																	 * @res "导入"
																	 */
					+ "(i)");
			ivjUIButtonImport.setMaximumSize(new java.awt.Dimension(100, 22));
			ivjUIButtonImport.setEnabled(true);
			ivjUIButtonImport.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjUIButtonImport.setMinimumSize(new java.awt.Dimension(75, 22));
		}
		return ivjUIButtonImport;
	}

	/**
	 * 返回 UIButtonEdit 特性值.
	 * 
	 * @return nc.ui.pub.beans.UIButton
	 */
	private nc.ui.pub.beans.UIButton getUIButtonExport() {
		if (ivjUIButtonExport == null) {
			ivjUIButtonExport = new nc.ui.pub.beans.UIButton();
			ivjUIButtonExport.setName("UIButtonExport");
			ivjUIButtonExport.setIButtonType(0/** Java默认(自定义) */
			);
			ivjUIButtonExport.setMnemonic('x');
			ivjUIButtonExport.setText(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("uifactory", "UPPuifactory-000175")/*
																	 * @res "导出"
																	 */
					+ "(x)");
			ivjUIButtonExport.setMaximumSize(new java.awt.Dimension(100, 22));
			ivjUIButtonExport.setEnabled(false);
			ivjUIButtonExport.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjUIButtonExport.setMinimumSize(new java.awt.Dimension(75, 22));
		}
		return ivjUIButtonExport;
	}

	/**
	 * 返回 UIButtonEdit 特性值.
	 * 
	 * @return nc.ui.pub.beans.UIButton
	 */
	private nc.ui.pub.beans.UIButton getUIButtonShow() {
		if (ivjUIButtonShow == null) {
			ivjUIButtonShow = new nc.ui.pub.beans.UIButton();
			ivjUIButtonShow.setName("UIButtonShow");
			ivjUIButtonShow.setIButtonType(0/** Java默认(自定义) */
			);
			ivjUIButtonShow.setMnemonic('v');
			ivjUIButtonShow.setText(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("common", "UC001-0000021")/*
														 * @res "浏览"
														 */
					+ "(v)");
			ivjUIButtonShow.setMaximumSize(new java.awt.Dimension(100, 22));
			ivjUIButtonShow.setEnabled(false);
			ivjUIButtonShow.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjUIButtonShow.setMinimumSize(new java.awt.Dimension(75, 22));
		}
		return ivjUIButtonShow;
	}

	/**
	 * 返回 UIButtonShowTemplet 特性值.
	 * 
	 * @return nc.ui.pub.beans.UIButton
	 */
//	private nc.ui.pub.beans.UIButton getUIButtonAssign() {
//		if (ivjUIButtonAssign == null) {
//			ivjUIButtonAssign = new nc.ui.pub.beans.UIButton();
//			ivjUIButtonAssign.setName("UIButtonShowTemplet");
//			ivjUIButtonAssign.setIButtonType(0/** Java默认(自定义) */
//			);
//			ivjUIButtonAssign.setMnemonic('g');
//			ivjUIButtonAssign.setText(nc.ui.ml.NCLangRes.getInstance()
//					.getStrByID("_template", "UPP_Template-000524")/*
//																	 * @res
//																	 * "显示模板"
//																	 */
//					+ "(g)");
//			ivjUIButtonAssign.setMaximumSize(new java.awt.Dimension(100, 22));
//			ivjUIButtonAssign.setEnabled(false);
//			ivjUIButtonAssign.setMargin(new java.awt.Insets(2, 2, 2, 2));
//			ivjUIButtonAssign.setMinimumSize(new java.awt.Dimension(75, 22));
//		}
//		return ivjUIButtonAssign;
//	}

	/**
	 * 返回 UIButtonShowTemplet 特性值.
	 * 
	 * @return nc.ui.pub.beans.UIButton
	 */
	private nc.ui.pub.beans.UIButton getUIButtonShowTemplet() {
		if (ivjUIButtonShowTemplet == null) {
			ivjUIButtonShowTemplet = new nc.ui.pub.beans.UIButton();
			ivjUIButtonShowTemplet.setName("UIButtonShowTemplet");
			ivjUIButtonShowTemplet.setIButtonType(0/** Java默认(自定义) */
			);
			ivjUIButtonShowTemplet.setMnemonic('t');
			ivjUIButtonShowTemplet.setText(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("_Bill", "UPP_Bill-000378")/*
															 * @res "显示模板"
															 */
					+ "(t)");
			ivjUIButtonShowTemplet.setPreferredSize(new java.awt.Dimension(122,
					22));
			ivjUIButtonShowTemplet.setMargin(new java.awt.Insets(2, 2, 2, 2));
		}
		return ivjUIButtonShowTemplet;
	}

	/**
	 * 表前缀过滤.
	 * 
	 * @return nc.ui.pub.beans.UICheckBox
	 */
	private nc.ui.pub.beans.UICheckBox getUICheckBoxFilter() {
		if (ivjUICheckBoxFilter == null) {
			ivjUICheckBoxFilter = new nc.ui.pub.beans.UICheckBox();
			ivjUICheckBoxFilter.setName("UICheckBoxFilter");
			ivjUICheckBoxFilter
					.setPreferredSize(new java.awt.Dimension(128, 20));
			ivjUICheckBoxFilter.setText(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("_Bill", "UPP_Bill-000379")/*
															 * @res "表前缀过滤"
															 */);
			ivjUICheckBoxFilter.setMaximumSize(new java.awt.Dimension(95, 22));
			ivjUICheckBoxFilter.setMinimumSize(new java.awt.Dimension(95, 22));
		}
		return ivjUICheckBoxFilter;
	}

	/**
	 * 单据模板标示.
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	private nc.ui.pub.beans.UILabel getUILabelBillTypeCode() {
		if (ivjUILabelBillTypeCode == null) {
			ivjUILabelBillTypeCode = new nc.ui.pub.beans.UILabel();
			ivjUILabelBillTypeCode.setName("UILabelBillTypeCode");
			ivjUILabelBillTypeCode.setPreferredSize(new java.awt.Dimension(125,
					22));
			ivjUILabelBillTypeCode.setText(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("_Bill", "UPP_Bill-000380")/*
															 * @res "单据模板标示"
															 */);
		}
		return ivjUILabelBillTypeCode;
	}

	/**
	 * 操作按钮.
	 * 
	 * @return nc.ui.pub.beans.UIPanel
	 */
	private nc.ui.pub.beans.UIPanel getUIPanelAction() {
		if (ivjUIPanelAction == null) {
			ivjUIPanelAction = new nc.ui.pub.beans.UIPanel();
			ivjUIPanelAction.setName("UIPanel2");
			ivjUIPanelAction.setPreferredSize(new java.awt.Dimension(80, 319));
			ivjUIPanelAction.setLayout(getUIPanel2BoxLayout());
			ivjUIPanelAction.setBorder(BorderFactory.createEmptyBorder(2, 2, 2,
					2));

			getUIPanelAction()
					.add(getUIButtonAdd(), getUIButtonAdd().getName());
			getUIPanelAction().add(getUIButtonEdit(),
					getUIButtonEdit().getName());
			getUIPanelAction()
					.add(getUIButtonDel(), getUIButtonDel().getName());
			getUIPanelAction().add(getUIButtonCopy(),
					getUIButtonCopy().getName());
			getUIPanelAction().add(getUIButtonCopyFrom(),
					getUIButtonCopyFrom().getName());
			getUIPanelAction().add(getUIButtonImport(),
					getUIButtonImport().getName());
			getUIPanelAction().add(getUIButtonExport(),
					getUIButtonExport().getName());
			getUIPanelAction().add(getUIButtonShow(),
					getUIButtonShow().getName());
			// getUIPanelAction().add(getUIButtonAssign(),
			// getUIButtonShow().getName());

			for (int i = 8; i >= 0; i--) {
				getUIPanelAction().add(
						Box.createVerticalStrut(i == 0 ? 24 : 6), i);
			}
		}
		return ivjUIPanelAction;
	}

	/**
	 * 返回 UIPanel2BoxLayout 特性值.
	 * 
	 * @return javax.swing.BoxLayout
	 */
	private javax.swing.BoxLayout getUIPanel2BoxLayout() {
		javax.swing.BoxLayout ivjUIPanel2BoxLayout = new javax.swing.BoxLayout(
				getUIPanelAction(), javax.swing.BoxLayout.Y_AXIS);
		return ivjUIPanel2BoxLayout;
	}

	// 编辑监听
	class HeadValueChangeListener implements ValueChangedListener {
		public void valueChanged(ValueChangedEvent e) {
			onButtonActionShowTemplets();
			setButtonsState();
		}
	}

	/**
	 * 返回 UIRefPaneBillType 特性值.
	 * 
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	private nc.ui.pub.beans.UIRefPane getUIRefPaneBillType() {
		if (ivjUIRefPaneBillType == null) {
			ivjUIRefPaneBillType = new nc.ui.pub.beans.UIRefPane();
			ivjUIRefPaneBillType.setName("UIRefPaneBillType");
			ivjUIRefPaneBillType.setToolTipText(nc.ui.ml.NCLangRes
					.getInstance().getStrByID("_Bill", "UPP_Bill-000380")/*
																		 * @res
																		 * "单据类型参照"
																		 */);
			ivjUIRefPaneBillType.setPreferredSize(new java.awt.Dimension(180,
					22));
			// ivjUIRefPaneBillType.setRefNodeName("单据类型");
			// ivjUIRefPaneBillType.setMaxLength(10);
			ivjUIRefPaneBillType.setTextFieldVisible(true);
			ivjUIRefPaneBillType.setRefModel(new AllBillTypeRefModel());
			ivjUIRefPaneBillType.setReturnCode(true);
			ivjUIRefPaneBillType.setButtonFireEvent(false);
			ivjUIRefPaneBillType.setAutoCheck(false);

			ivjUIRefPaneBillType
					.addValueChangedListener(new HeadValueChangeListener());
		}
		return ivjUIRefPaneBillType;
	}

	/**
	 * 已定义模板.
	 * 
	 * @return nc.ui.pub.beans.UIScrollPane
	 */
	private nc.ui.pub.beans.UIScrollPane getUIScrollPaneTreeBillTemplet() {
		if (ivjUIScrollPaneTreeBillTemplet == null) {
			ivjUIScrollPaneTreeBillTemplet = new nc.ui.pub.beans.UIScrollPane();
			ivjUIScrollPaneTreeBillTemplet.setName("UIScrollPaneTree");
			getUIScrollPaneTreeBillTemplet().setViewportView(
					getUITreeBillTemplet());
			getUIScrollPaneTreeBillTemplet().setAutoscrolls(true);
		}
		return ivjUIScrollPaneTreeBillTemplet;
	}

	/**
	 * 返回 UITextFieldTableFilter 特性值.
	 * 
	 * @return nc.ui.pub.beans.UITextField
	 */
	private nc.ui.pub.beans.UITextField getUITextFieldTableFilter() {
		if (ivjUITextFieldTableFilter == null) {
			ivjUITextFieldTableFilter = new nc.ui.pub.beans.UITextField();
			ivjUITextFieldTableFilter.setName("UITextFieldTableFilter");
			ivjUITextFieldTableFilter.setPreferredSize(new java.awt.Dimension(
					60, 20));
			ivjUITextFieldTableFilter.setEditable(false);
		}
		return ivjUITextFieldTableFilter;
	}

	/**
	 * 返回 UITreeBillTemplet 特性值.
	 * 
	 * @return nc.ui.pub.beans.UITree
	 */
	private nc.ui.pub.beans.UITree getUITreeBillTemplet() {
		if (ivjUITreeBillTemplet == null) {
			ivjUITreeBillTemplet = new nc.ui.pub.beans.UITree();
			ivjUITreeBillTemplet.setName("UITreeBillTemplet");
			ivjUITreeBillTemplet.setBounds(0, 0, 160, 120);
			ivjUITreeBillTemplet.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);
		}
		return ivjUITreeBillTemplet;
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private String gotoNextPage(int type, boolean isCopy) {
		switch (type) {
		case MainPanel.TEMPLET_ADD_CUSTOM:
		case MainPanel.TEMPLET_MODIFY_CUSTOM:
		case MainPanel.TEMPLET_ADD_DEFAULT:
		case MainPanel.TEMPLET_MODIFY_DEFAULT:
		case MainPanel.TEMPLET_SHOW_DEFAULT:
			break;
		default:
			return nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
					"UPP_Bill-000382")/*
									 * @res "请选择模板操作方式!"
									 */;
		}

		String id = getId(type, isCopy);
		if (!isCopy && type == MainPanel.TEMPLET_ADD_CUSTOM) {
			HashSet<String> hm = new HashSet<String>();
			if (hashTreeNode.size() > 0) {
				Enumeration<BillTempletHeadVO> enums = hashTreeNode.elements();
				while (enums.hasMoreElements()) {
					hm.add(enums.nextElement().getPkPubBilltempletHead());
				}
			}
			billcaption = null;
			billname = null;
			String msg = onBilltempletCopy(id, isCopy);
			if (msg != null) {
				if (!MSG_CANCEL.equals(msg))
					showOkDlg(msg);
				return null;
			}
			// if(root.getChildAt(0).getChildCount() <= childcount)
			// return null;
			// getUITreeBillTemplet().expandRow(0);
			TreePath path = null;
			if (hm.size() > 0) {
				for (int i = 0; i < root.getChildCount(); i++) {
					TreeNode pnode = root.getChildAt(i);
					for (int j = 0; j < pnode.getChildCount(); j++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) pnode
								.getChildAt(j);
						BillTempletHeadVO head = (BillTempletHeadVO) hashTreeNode
								.get(node);
						String name;
						String caption;
						if (!hm.contains(head.getPkPubBilltempletHead())) {
							name = head.getBillTempletName();
							caption = head.getBillTempletCaption();
							if ((billcaption == caption || (billcaption != null && billcaption
									.equals(caption)))
									&& (billname == name || (billname != null && billname
											.equals(name)))) {
								path = new TreePath(node.getPath());
								break;
							}

						}
					}
				}
			}

			getUITreeBillTemplet().setSelectionPath(path);

			// int selRow = root.getChildAt(0).getChildCount();
			// if (hm.size() > 0) {
			// TreeNode node = root.getChildAt(0);
			// BillTempletHeadVO head;
			// String name;
			// String caption;
			// for (int i = 0; i < node.getChildCount(); i++) {
			// head = (BillTempletHeadVO) hashTreeNode.get(node.getChildAt(i));
			// if (!hm.contains(head.getPkPubBilltempletHead())) {
			// name = head.getBillTempletName();
			// caption = head.getBillTempletCaption();
			// if ((billcaption == caption || (billcaption != null &&
			// billcaption.equals(caption))) && (billname == name || (billname
			// != null && billname.equals(name)))) {
			// selRow = ++i;
			// break;
			// }
			// }
			// }
			// }
			// getUITreeBillTemplet().setSelectionRow(selRow);
			type = MainPanel.TEMPLET_MODIFY_CUSTOM;
			id = getId(type, isCopy);

		}
		// Logger.debug("id::"+id);
		try {
			String metadataclass = null;
			if (type == MainPanel.TEMPLET_ADD_DEFAULT) {
				if (getTabbedPaneSource().getSelectedIndex() == MEATDATA
						&& getMetaDataTree().getSelectionPath() != null) {
					MetaDataTreeNode node = (MetaDataTreeNode) getMetaDataTree()
							.getSelectionPath().getLastPathComponent();
					if (node != null) {
						if (node.isComponet())
							metadataclass = node.getComponent()
									.getPrimaryBusinessEntity().getFullName();
						else
							return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("_bill", "0_bill0017")/*
																	 * @res
																	 * "请选择元数据组件"
																	 */;
					}
				}
			}

			BillTempletVO billvo = getBillTempletVO(id, getBilltypeCode(),
					metadataclass);
			// translate
			// if (type == MainPanel.TEMPLET_ADD_CUSTOM || type ==
			// MainPanel.TEMPLET_MODIFY_CUSTOM )
			BillTranslator.translate(billvo);
			parent.onButtonNextPage(type, billvo);

		} catch (MetaDataRuntimeException e) {
			Logger.error(e);
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"_bill", "0_bill0018")/* @res "元数据格式错误" */;
		}
		return null;
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private void initBillTypeCode() {
		billtypeCode = null;

		funccode = null;

		String[] billtype = (String[]) getUIRefPaneBillType().getValueObj();

		if (billtype == null || billtype.length == 0) {
			// showOkDlg(nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
			// "UPP_Bill-000383")/*
			// * @res
			// * "单据类型或者非单据类型不合法!"
			// */);
			return;
		}
		billtypeCode = billtype[0].trim();
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private void initConnection() {

		// show Tables Button
		getJButtonShowTables().addActionListener(this);

		// parent table select and unselect button
		getJButtonParentSelect().addActionListener(this);
		getJButtonParentUnSelect().addActionListener(this);

		// child table select and unselect button
		getJButtonChildSelect().addActionListener(this);
		getJButtonChildUnSelect().addActionListener(this);

		// table prefix filter
		getUICheckBoxFilter().addActionListener(this);

		// add keylistener
		// getJTextFieldTableFilter().addKeyListener(this);
		// getJTextFieldTableFilter().addActionListener(this);

		// show templets
		getUIButtonShowTemplet().addActionListener(this);
		// getUIButtonShowTemplet().addKeyListener(this);
//		getUIButtonAssign().addActionListener(this);

		// table list
		getJListTables().addKeyListener(this);

		// billtype textfield
		// getUITextFieldBillType().addKeyListener(this);
		// getUITextFieldBillType().addActionListener(this);

		// add selectListener to tree
		getUITreeBillTemplet().addTreeSelectionListener(this);

		// this listener is used to avoid wrong operation when billtype change.
		// when billtype change,the edit buttons unenablize.
		// getUIRefPaneBillType().getUITextField().addKeyListener(this);
		// getUIRefPaneBillType().getUITextField().addKeyListener(new
		// java.awt.event.KeyAdapter() {
		// public void keyReleased(java.awt.event.KeyEvent e) {
		// String fieldtext = getUIRefPaneBillType().getUITextField().getText();
		// if (!((billtypeCode != null && fieldtext != null) &&
		// billtypeCode.equalsIgnoreCase(fieldtext))) {
		// setButtonsState(false);
		// } else
		// setButtonsState();
		// };
		// });

		// add ,edit,del buttons actionListener
		getUIButtonShow().addActionListener(this);
		getUIButtonAdd().addActionListener(this);
		getUIButtonEdit().addActionListener(this);
		getUIButtonDel().addActionListener(this);
		getUIButtonCopy().addActionListener(this);
		getUIButtonCopyFrom().addActionListener(this);
		getUIButtonExport().addActionListener(this);
		getUIButtonImport().addActionListener(this);
	}

	/**
	 * 初始化类.
	 */
	private void initialize() {
		try {
			setName("DataSourcePanel");
			setLayout(new GridLayout(1, 2, 3, 3));
			setSize(708, 390);
			// if(isDebug())
			add(getJPanelWest(), getJPanelWest().getName());
			add(getJPanelEast(), getJPanelEast().getName());
		} catch (java.lang.Throwable ivjExc) {

		}

		// init listmodels
		initListModels();
		// initialize listeners
		initConnection();
		// init tree
		initTree();

	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private void initListModels() {
		if (listModelTables == null) {
			listModelTables = new DefaultListModel();
			getJListTables().setModel(listModelTables);
			listModelParentTable = new DefaultListModel();
			listModelChildTable = new DefaultListModel();
			getJListParentTables().setModel(listModelParentTable);
			getJListChildTables().setModel(listModelChildTable);
		}
		getJListTables().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getJListParentTables().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		getJListChildTables().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
	}

	/**
	 * 初始化模板数据 创建日期:(2002-9-10 11:07:20)
	 */
	private void initTree() {
		getUITreeBillTemplet().setModel(
				new javax.swing.tree.DefaultTreeModel(root));
		getUITreeBillTemplet().setRootVisible(false);
	}

	private void initTempletTreeByTempletCode(String templetcode) {

		if (templetcode != null) {
			String where = "LOWER(pub_billtemplet.pk_billtypecode)='"
					+ templetcode.toLowerCase() + "'";

			initTempletTreeByWhere(where);
		} else {
			initTempletTreeByWhere(null);
		}
	}

	private void initTempletTreeByFuncCode(String funccode) {

		if (funccode != null) {
			String where = "pk_billtypecode IN (SELECT pk_billtypecode FROM pub_billtemplet,pub_systemplate WHERE pk_billtemplet = templateid and tempstyle = 0 AND pub_systemplate.funnode = '"
					+ funccode
					+ "' AND pub_systemplate.pk_corp in ('"
					+ getPk_corp() + "','@@@@'))";

			initTempletTreeByWhere(where);
		} else {
			initTempletTreeByWhere(null);
		}
	}

	/**
	 * 加载模板数据，构建模板树 创建日期:(2002-9-10 11:07:20)
	 */
	private void initTempletTreeByWhere(String w) {

		clearTreeNodes();

		if (w == null)
			return;

		DefaultMutableTreeNode parent = null;
		DefaultMutableTreeNode child;
		BillTempletHeadVO[] headVOs = null;

		// 从数据库中获取模板类型=billTypeCode，公司=当前公司或0001或@@@@ 的模板或系统模板

		try {
			StringBuffer where = new StringBuffer();

			where.append("(");

			// 得到模板列表数据
			// String where = "(LOWER(pub_billtemplet.pk_billtypecode)='" +
			// billTypeCode.toLowerCase() +
			// "' and (pub_billtemplet.pk_corp in('" + getPk_corp() +
			// "','@@@@','0001') or  pub_billtemplet.bill_templetname='SYSTEM')) ";

			// where =
			// "pk_billtypecode IN (SELECT pk_billtypecode FROM pub_billtemplet,pub_systemplate WHERE pk_billtemplet = templateid and tempstyle = 0 AND pub_systemplate.funnode = '10140PSN' AND pub_systemplate.pk_corp in ('"
			// + getPk_corp() + "','@@@@')) AND pk_corp in ('" + getPk_corp() +
			// "','@@@@')";

			where.append(w);
			where
					.append(" and (pub_billtemplet.pk_corp in('"
							+ getPk_corp()
							+ "','@@@@','0001') or  pub_billtemplet.bill_templetname='SYSTEM')");
			where.append(")");

			// headVOs =
			headVOs = BillTemplateHelper.findTempletWithBillTypeElse(where
					.toString());
			Logger.info("树数据加载成功!");
		} catch (Exception e) {
			Logger.error("树数据加载失败!");
			Logger.error(e.getMessage());
		}

		HashMap<String, DefaultMutableTreeNode> parentNode = new HashMap<String, DefaultMutableTreeNode>();

		// 构建模板数
		if (headVOs != null && headVOs.length > 0) {
			BillTranslator.translateHead(headVOs);
			for (int i = 0; i < headVOs.length; i++) {
				if ("SYSTEM".equals(headVOs[i].getBillTempletName())) {
					billtypeCode = headVOs[i].getPkBillTypeCode();
					String name = NCLangRes4VoTransl.getNCLangRes().getString(
							headVOs[i].getNodecode(),
							headVOs[i].getBillTempletCaption(),
							headVOs[i].getResid() == null ? headVOs[i]
									.getBillTempletCaption() : headVOs[i]
									.getResid());
					String nodeshowname = billtypeCode + " " + name;
					parent = new DefaultMutableTreeNode(nodeshowname);
					hashTreeNode.put(parent, headVOs[i]);
					root.add(parent);
					parentNode.put(billtypeCode, parent);
					// break;
				}
			}
			if (parentNode.size() > 0) {
				for (int i = 0; i < headVOs.length; i++) {
					if (!"SYSTEM".equals(headVOs[i].getBillTempletName())) {
						// String type =
						// nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
						// "UC000-0000404");//"公司";
						// if("0001".equals(headVOs[i].getPkCorp()))
						// type =
						// nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
						// "UC001-0000072");//"集团";
						//
						// String nodeshowname = "(" + type + ")" +
						// headVOs[i].getBillTempletName();
						String name = NCLangRes4VoTransl
								.getNCLangRes()
								.getString(
										headVOs[i].getNodecode(),
										headVOs[i].getBillTempletCaption(),
										headVOs[i].getResid() == null ? headVOs[i]
												.getBillTempletCaption()
												: headVOs[i].getResid());
						child = new DefaultMutableTreeNode(name);
						hashTreeNode.put(child, headVOs[i]);
						billtypeCode = headVOs[i].getPkBillTypeCode();
						parent = parentNode.get(billtypeCode);
						parent.add(child);
					}
				}
			}
		}

		DefaultTreeModel treemodel = new DefaultTreeModel(root);

		getUITreeBillTemplet().setModel(treemodel);
		getUITreeBillTemplet().setRootVisible(false);
		// 选中第一行
		// if (getUITreeBillTemplet().getRowCount() > 0) {
		// getUITreeBillTemplet().setSelectionRow(0);
		// getUITreeBillTemplet().expandRow(0);
		if (root.getChildCount() > 0) {
			for (int i = 0; i < root.getChildCount(); i++) {
				TreePath path = new TreePath(treemodel.getPathToRoot(root
						.getChildAt(i)));
				getUITreeBillTemplet().expandPath(path);
			}
			getUITreeBillTemplet().setSelectionRow(0);
		} else {
			node = null;
		}
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private boolean isDebug() {
		return parent.isDebug();
		// return false;
	}

	/**
	 * 创建日期:(2002-9-19 8:45:38)
	 */
	private boolean isDefaultTempletDeleteEnable(String id, String billtype) {
		// boolean flag = false;
		// try {
		// BillTempletHeadVO[] heads =
		// BillTemplateHelper.findTempletWithBillTypeElse("pub_billtemplet.pk_billtemplet<>'"
		// + id + "' and pub_billtemplet.pk_billtypecode='" + billtype + "'");
		// if (heads == null || heads.length == 0)
		// flag = true;
		// } catch (Exception e) {
		// Logger.debug(e);
		// }
		// return flag;
		return true;
	}

	private boolean isDefaultTempletInsertEnable(BillTempletHeadVO headVO) {
		boolean flag = false;
		try {
			//shidl
			BillTempletHeadVO[] heads = BillTemplateHelper
					.findTempletWithBillTypeElse("pub_billtemplet.bill_templetname='SYSTEM' and pk_corp='@@@@'"
							+ " and pub_billtemplet.pk_billtypecode='"
							+ headVO.getPkBillTypeCode() + "'");
			if (heads == null || heads.length == 0)
				flag = true;
		} catch (Exception e) {
			Logger.debug(e);
		}
		return flag;

	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private boolean isGroup() {
		return true;
	}

	/**
	 * Invoked when a key has been pressed.
	 */
	public void keyPressed(java.awt.event.KeyEvent e) {
		boolean isEnabledP = getJButtonParentSelect().isEnabled();
		boolean isEnabledC = getJButtonChildSelect().isEnabled();
		// set buttons state false at first
		setButtonsState(false);
		Object o = e.getSource();
		if (o == getJListTables()
				&& e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
			if (getJListParentTables().getModel().getSize() > 0) {
				if (isEnabledC)
					onButtonActionSelectChildTable();
			} else if (isEnabledP)
				onButtonActionSelectParentTable();
		}
		setButtonsState();
	}

	/**
	 * Invoked when a key has been released.
	 */
	public void keyReleased(java.awt.event.KeyEvent e) {
	}

	/**
	 * Invoked when a key has been typed. This event occurs when a key press is
	 * followed by a key release.
	 */
	public void keyTyped(java.awt.event.KeyEvent e) {
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	public String onBilltempletAdd(boolean isCopy) {
		parent.showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
				"_template", "UPP_Template-000295")/* "增加模板" */);
		boolean isNull = (root.getChildCount() == 0);
		int type = isNull ? MainPanel.TEMPLET_ADD_DEFAULT
				: MainPanel.TEMPLET_ADD_CUSTOM;

		return gotoNextPage(type, isCopy);
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private String onBilltempletCopy(String templetID, boolean isCopy) {
		if (getBilltypeCode() == null)
			return nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
					"UPP_Bill-000258")/*
									 * @res "没有单据类型!"
									 */;
		if (templetID == null)
			return nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
					"UPP_Bill-000384")/*
									 * @res "没有单据主键!"
									 */;

		// int type = isNull ? MainPanel.TEMPLET_ADD_DEFAULT :
		// MainPanel.TEMPLET_ADD_CUSTOM;
		int type = isCopy ? MainPanel.TEMPLET_ADD_DEFAULT
				: MainPanel.TEMPLET_ADD_CUSTOM;
		BillTempletVO billvo = getBillTempletVO(templetID, getBilltypeCode(),
				null);
		if (isCopy) {
			billvo.getHeadVO().setPkBillTypeCode(null);
			billvo.getHeadVO().setResid(null);
		}

		// if (billvo != null) {
		// BillTranslator.translate(billvo);
		// BillTempletHeadVO head = billvo.getHeadVO();
		// if (head != null) {
		// head.setResid(null);
		// BillStructVO svo = head.getStructvo();
		// if (svo != null) {
		// BillTabVO[] tvos = svo.getBillTabVOs();
		// if (tvos != null) {
		// for (int i = 0; i < tvos.length; i++) {
		// tvos[i].setResid(null);
		// }
		// }
		// }
		// }
		// BillTempletBodyVO[] bodys = billvo.getBodyVO();
		// if (bodys != null) {
		// String cs =
		// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0004288")/*@res
		// "自定义项"*/;
		// String cs1 = nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
		// "UPP_Bill-000501")/*
		// * @res
		// * "自定义项"
		// */;
		// for (int i = 0; i < bodys.length; i++) {
		// bodys[i].setResid(null);
		// bodys[i].setResid_tabname(null);
		//
		// String name = bodys[i].getDefaultshowname();
		// if (name != null && name.startsWith(cs) && name.length() >
		// cs.length()) {
		// bodys[i].setDefaultshowname(cs1 + name.substring(cs.length()));
		// }
		// }
		// }
		// }

		String msg = parent.getBillPanel().onSaveTemplet(billvo, type, "copy");

		if (msg == null) {
			return "cancel";
		}
		if (!msg.equals("true")) {
			// showOkDlg(msg + "模板保存失败!");
			return msg
					+ nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
							"UPP_Bill-000385")/*
											 * @res "模板保存失败!"
											 */;
		}

		refreshBillTempletTree();

		// refresh
		billcaption = billvo.getHeadVO().getBillTempletCaption();
		billname = billvo.getHeadVO().getBillTempletName();
		if (billcaption != null)
			billcaption = billcaption.trim();
		if (billname != null)
			billname = billname.trim();
		return null;
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private void onButtonActionBilltempletAdd() {
		String msg = onBilltempletAdd(false);
		if (msg != null && !MSG_CANCEL.equals(msg))
			showOkDlg(msg);
	}

	/**
	 * 创建日期:(2003-1-20 12:49:31)
	 */
	private void onButtonActionBilltempletCopy() {

		parent.showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
				"_Bill", "UPP_Bill-000061")/* 复制模板 */);

		String templetID = getId(MainPanel.TEMPLET_ADD_CUSTOM, true);
		String msg = onBilltempletCopy(templetID, true);
		if (msg != null && !msg.equals(MSG_CANCEL))
			showOkDlg(msg);
	}

	/**
	 * 
	 * 创建日期:(2003-3-13 13:32:28)
	 */
	private void onButtonActionBilltempletCopyFrom() {
		if (getBilltypeCode() == null)
			return;

		parent.showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
				"_Bill", "UPP_Bill-000061")/* 复制模板 */);

		final String billTypeCode = getBilltypeCode();
		final TemplateChooser templateChooser = new TemplateChooser(this,
				TemplateChooser.Type.BILL, billTypeCode);
		templateChooser.addWarningValidator(new Validator() {

			@Override
			public String validate(TemplateChooserConfig config) {
				String selectedFuncode = templateChooser.getSelectedFuncode();
				if (selectedFuncode == null
						|| !selectedFuncode.equals(billTypeCode)) {
					String msg = nc.ui.ml.NCLangRes.getInstance().getStrByID(
							"_Bill", "UPP_Bill-000386")/*
														 * @res "单据类型发生了改变."
														 */;
					return msg;
				}
				return null;
			}
		});
		if (templateChooser.showModal() == UIDialog.ID_OK) {
			String templetID = templateChooser.getConfiguration()
					.getTemplateID();
			templateChooser.destroy();
			if (templetID == null || templetID.trim().length() == 0)
				return;
			String msg = onBilltempletCopy(templetID, true);
			if (msg != null)
				showOkDlg(msg);
		}
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	public void onButtonActionBilltempletDel() {
		// boolean flag = false;
		BillTempletHeadVO head = getCurrentHeadVO();
		if (head == null)
			return;
		parent.showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID("_template", "UPP_Template-000299")/* 删除模板 */);

		String id = head.getPrimaryKey();
		String billTypeCode = head.getPkBillTypeCode();
		if (id == null || billTypeCode == null) {
			showOkDlg(nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
					"UPP_Bill-000387")/*
									 * @res "当前模板主键或者单据类型不存在,删除无效!"
									 */);
			return;
		}
		if (showOkCancelDlg(nc.ui.ml.NCLangRes.getInstance().getStrByID(
				"_Bill", "UPP_Bill-000254")/*
											 * @res "确定要删除吗?"
											 */, nc.ui.ml.NCLangRes
				.getInstance().getStrByID("_Bill", "UPP_Bill-000034")/*
																	 * @res "提示"
																	 */) != NCOptionPane.OK_OPTION)
			return;
		if (node.getParent() == root
				&& !isDefaultTempletDeleteEnable(id, billTypeCode)) {
			showOkDlg(nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
					"UPP_Bill-000388")/*
									 * @res "该默认模板已经使用,不能删除!"
									 */);
			return;
		}

		// if deleteok then(msg ==nul),else (msg!=null)
		String msg = delBillTempletVO(id);
		if (msg != null) {
			showOkDlg(msg);
			return;
		}
		onShowTemplet();
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	public void onButtonActionBilltempletEdit() {

		parent.showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
				"_template", "UPP_Template-000297")/* 修改模板 */);

		int type = (node.getParent() == root) ? MainPanel.TEMPLET_MODIFY_DEFAULT
				: MainPanel.TEMPLET_MODIFY_CUSTOM;
		String msg = gotoNextPage(type, false);
		if (msg != null)
			showOkDlg(msg);
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	public void onButtonActionBilltempletShow() {

		parent.showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
				"_template", "UPP_Template-000302")/* 预览选定模板 */);

		int type = MainPanel.TEMPLET_SHOW_DEFAULT;
		parent.getBillPanel().setBrowse(true);
		String msg = gotoNextPage(type, false);
		if (msg != null)
			showOkDlg(msg);
		
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private void onButtonActionSelectChildTable() {
		if (listModelTables.getSize() == 0)
			return;
		if (listModelParentTable.getSize() == 0) {
			showOkDlg(nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
					"UPP_Bill-000390")/*
									 * @res "请先选择主表!"
									 */);
			return;
		}
		String parentTableName = listModelParentTable.getElementAt(0)
				.toString();
		String childTableName = getJListTables().getSelectedValue().toString();
		String msg = addChildTable(parentTableName, childTableName);
		if (msg != null)
			showOkDlg(msg);
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private void onButtonActionSelectParentTable() {
		if (listModelTables.getSize() == 0)
			return;
		String msg = addParentTable((String) getJListTables()
				.getSelectedValue());
		if (msg != null)
			showOkDlg(msg);
	}

	/**
	 * show tables by filter. 创建日期:(2002-9-10 11:07:20)
	 */
	private synchronized void onButtonActionShowTables() {
		// clear tablemodel at first
		listModelTables.clear();

		// get PreFilter
		String preFilter;
		if ((preFilter = getUITextFieldTableFilter().getText()) != null
				&& (preFilter = preFilter.trim()).length() > 0)
			preFilter = preFilter.toLowerCase();
		else
			preFilter = null;
		// tableman.readSourceTableNames();
		java.util.ArrayList<String> tablelist = readTableList();
		if (tablelist == null || tablelist.size() == 0)
			return;
		String[] tables = tablelist.toArray(new String[tablelist.size()]);
		// get tablenames by filter
		String tablename;
		for (int i = 0; i < tables.length; i++) {
			tablename = tables[i].toLowerCase().trim();
			if (preFilter != null) {
				if (tablename.startsWith(preFilter))
					listModelTables.addElement(tablename);// vecTableNames.elementAt(i));
			} else
				listModelTables.addElement(tablename);// vecTableNames.elementAt(i));
		}

		// set default listbox selected index
		if (listModelTables.getSize() > 0) {
			getJListTables().requestFocus();
			getJListTables().setSelectedIndex(0);
		}
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	public void onButtonActionShowTemplets() {
		parent.showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
				"_Bill", "UPP_Bill-000382")/*
											 * @res "请选择模板操作方式!"
											 */);

		String[] fieldtexts = (String[]) getUIRefPaneBillType().getValueObj();

		if (!((billtypeCode != null && fieldtexts != null && fieldtexts.length > 0) && billtypeCode
				.equalsIgnoreCase(fieldtexts[0]))) {
			initBillTypeCode();
		}

		getUIRefPaneBillType().getUITextField().requestFocus();

		onShowTemplet();
	}

//	private void onButtonActionAssign() {
//		BillTempletHeadVO head = getCurrentHeadVO();
//		if (head == null)
//			return;
//
//		parent.showHintMessage(nc.ui.ml.NCLangRes.getInstance().getStrByID(
//				"_template", "UPP_Template-000524")/* 分配模板 */);
//
//		String id = head.getPrimaryKey();
//
//		String defaultid = getDefaultHeadVO().getPrimaryKey();
//
//		List<String> nodecode = getNodeCode(defaultid);
//
//		if (nodecode != null) {
//			TemplateAssignDialog dlg = new TemplateAssignDialog(this, "",
//					nodecode.toArray(new String[0]), id,
//					ITemplateStyle.billTemplate);
//
//			dlg.showModal();
//		} else {
//			showOkDlg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
//					"_bill", "0_bill0019")/* @res "没有找到模板对应节点。" */);
//		}
//
//	}

	@SuppressWarnings("unchecked")
	private List<String> getNodeCode(String id) {

		if (id == null)
			return null;

		List<String> findNodeCode = null;

		try {

			IUAPQueryBS name = (IUAPQueryBS) NCLocator.getInstance().lookup(
					IUAPQueryBS.class.getName());

			String where = "templateid=\'" + id
					+ "\' and (tempstyle = 0) AND (templateflag = 'Y')";

			List vos = (List) name.retrieveByClause(SystemplateVO.class, where,
					new String[] { "funnode" });

			if (vos == null || vos.size() == 0)
				return null;

			findNodeCode = new ArrayList<String>();

			// SystemplateVO[] systemplates = new SystemplateVO[col.size()];
			// col.toArray(systemplates);
			// String nodecode = systemplates[0].getFunnode();
			//
			// if (nodecode != null)
			// findNodeCode = nodecode;

			for (int i = 0; i < vos.size(); i++) {
				SystemplateVO vo = (SystemplateVO) vos.get(i);
				findNodeCode.add(vo.getFunnode());

			}

		} catch (Exception e) {
			Logger.error(e.getStackTrace());
		}
		return findNodeCode;
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private void onButtonActionUnSelectChildTable() {
		if (listModelParentTable.getSize() == 0
				|| listModelChildTable.getSize() == 0)
			return;
		tableman.removeChildTableByName(listModelParentTable.getElementAt(0)
				.toString(), getJListChildTables().getSelectedValue()
				.toString());
		listModelChildTable.remove(getJListChildTables().getSelectedIndex());
		if (listModelChildTable.getSize() > 0)
			getJListChildTables().setSelectedIndex(0);
	}

	/**
	 * 清除数据库表选择 创建日期:(2002-9-10 11:07:20)
	 */
	private void onButtonActionUnSelectParentTable() {
		if (listModelParentTable.getSize() > 0) {
			tableman.removeTableByName(listModelParentTable.getElementAt(0)
					.toString());
			listModelParentTable.clear();
			listModelChildTable.clear();
		}
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private void onCheckBoxTableFilter() {
		if (getUICheckBoxFilter().isSelected())
			getUITextFieldTableFilter().setEditable(true);
		else {
			getUITextFieldTableFilter().setEditable(false);
			getUITextFieldTableFilter().setText(null);
		}
	}

	public void onShowTemplet() {
		onShowTemplet(isDebug());
	}

	/**
	 * 显示模板类型下的模板 创建日期:(2002-9-10 11:07:20)
	 */
	public void onShowTemplet(boolean isBillTempletCode) {

		if (isBillTempletCode)
			initTempletTreeByTempletCode(billtypeCode);
		else
			initTempletTreeByFuncCode(funccode);

		// 清除数据库选择
		onButtonActionUnSelectParentTable();

		// 设置默认单据模板ID
		parent.setDefaultID(null);
		if (root.getChildCount() == 0) {
			if (((DefaultMutableTreeNode) getMetaDataTree().getModel()
					.getRoot()).getChildCount() == 1)
				getMetaDataTree().setModel(getMetaDataTreeModel());
			return;
		}

		// refresh tableMan and Tablelist
		BillTempletHeadVO head = getDefaultHeadVO();
		if (head == null)
			return;

		String key = head.getPrimaryKey();
		BillTempletVO vo = null;
		parent.setDefaultID(key);

		// 设置对应源
		if (key != null && key.trim() != null) {
			// 查询模板数据设置单据表
			try {
				vo = BillTemplateHelper.findCardTempletData(key, getPk_corp());
			} catch (Exception e) {
				Logger.debug(e);
			}
			if (vo != null) {
				if (vo.getHeadVO().getBillMetaDataBusinessEntity() != null) {
					getTabbedPaneSource().setSelectedIndex(MEATDATA);

					DefaultMutableTreeNode root = new DefaultMutableTreeNode();
					DefaultMutableTreeNode metadatanode = new DefaultMutableTreeNode(
							vo.getHeadVO().getBillMetaDataBusinessEntity()
									.getDisplayName());
					root.add(metadatanode);

					getMetaDataTree().setModel(new DefaultTreeModel(root));
				} else {
					getTabbedPaneSource().setSelectedIndex(DATASOURCE);
					BillTempletBodyVO[] bodys = vo.getBodyVO();
					ArrayList<String> list = new ArrayList<String>();
					if (bodys != null) {
						for (int i = 0; i < bodys.length; i++) {
							String tableCode = bodys[i].getTableCode();
							if (tableCode != null) {
								tableCode = tableCode.trim();
								if (!list.contains(tableCode)) {
									if (bodys[i].getPos().intValue() == 0)
										list.add(0, tableCode.toLowerCase());
									else
										list.add(tableCode.toLowerCase());
								}
							}
						}
						if (list.size() > 0) {
							String parenttablename = null;
							ArrayList<String> tablelist = readTableList();
							for (int i = 0; i < list.size(); i++) {
								if (parenttablename == null) {
									parenttablename = list.get(i);
									if (tablelist != null
											&& tablelist
													.contains(parenttablename))
										addParentTable(parenttablename);
									else
										parenttablename = null;
								} else {
									String child = (String) list.get(i);
									if (tablelist.contains(child))
										addChildTable(parenttablename, child);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * 创建日期:(2003-1-14 10:40:13)
	 */
	public void refreshBillTempletTree() {
		// refresh Templet tree
		setButtonsState(false);
		onShowTemplet();
		setButtonsState();
	}

	/**
	 * 设置按钮状态 创建日期:(2002-9-10 11:07:20)
	 */
	private void setButtonsState() {
		// at first,set all false in order to escape wrong opteration if
		// exception has happen
		setButtonsState(false);

		boolean isGroup = isGroup();
		boolean isDebug = isDebug();
		boolean isGroupTemplate = isGroupTemplate();
		boolean isNull = (node == null);
		boolean isBilltypeCodeNull = (getBilltypeCode() == null);
		boolean isParent = (node != null && node.getParent() == root);
		boolean hasChild = (node != null && node.getChildCount() > 0);

		// 设置模板操作按钮状态
		getUIButtonAdd().setEnabled(
				!((isNull && !isGroup) || isBilltypeCodeNull));
		getUIButtonEdit()
				.setEnabled(
						((!isParent) || (isDebug && isParent)) && !isNull
								&& !isBilltypeCodeNull
								&& (isGroup || !isGroupTemplate));
		getUIButtonShow().setEnabled(!isNull);
		getUIButtonDel()
				.setEnabled(
						((!isParent) || (isDebug && isParent && !hasChild))
								&& !isNull && !isBilltypeCodeNull
								&& (isGroup || !isGroupTemplate));
//		getUIButtonAssign().setEnabled(
//				((!isParent) || (isDebug && isParent && !hasChild)) && !isNull
//						&& !isBilltypeCodeNull);
		getUIButtonCopy().setEnabled(!isNull && !isBilltypeCodeNull);
		getUIButtonCopyFrom().setEnabled(
				isDebug || (!isNull && !isBilltypeCodeNull));
		getUIButtonExport().setEnabled(
				isDebug || (!isNull && !isBilltypeCodeNull));

		// 设置数据库选择按钮状态
		boolean[] flags = new boolean[] { false, false, false, false };
		if (listModelTables.getSize() > 0) {
			flags[0] = true;
			flags[2] = true;
		}
		if (listModelParentTable.getSize() > 0)
			flags[1] = true;
		if (listModelChildTable.getSize() > 0)
			flags[3] = true;

		boolean isAddEnabled = getUIButtonAdd().isEnabled();
		boolean isEditEnabled = getUIButtonEdit().isEnabled();

		getJButtonParentSelect().setEnabled(
				flags[0] && (isGroup || isDebug) && isAddEnabled
						&& (!isEditEnabled || isParent));
		getJButtonParentUnSelect().setEnabled(
				flags[1] && (isGroup || isDebug) && isAddEnabled
						&& (!isEditEnabled || isParent));
		getJButtonChildSelect().setEnabled(
				flags[2] && (isGroup || isDebug) && isAddEnabled
						&& (!isEditEnabled || isParent));
		getJButtonChildUnSelect().setEnabled(
				flags[3] && (isGroup || isDebug) && isAddEnabled
						&& (!isEditEnabled || isParent));
	}

	private boolean isGroupTemplate() {

		if (getCurrentHeadVO() != null) {
			if ("0001".equals(getCurrentHeadVO().getPkCorp())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 创建日期:(2002-9-10 11:07:20)
	 */
	private void setButtonsState(boolean flag) {
		getUIButtonShow().setEnabled(flag);
		getUIButtonAdd().setEnabled(flag);
		getUIButtonEdit().setEnabled(flag);
//		getUIButtonAssign().setEnabled(flag);
		getUIButtonDel().setEnabled(flag);
		getUIButtonCopy().setEnabled(flag);
		getUIButtonCopyFrom().setEnabled(flag);
		getUIButtonImport().setEnabled(!flag);
		getUIButtonExport().setEnabled(flag);
		getJButtonParentSelect().setEnabled(flag);
		getJButtonParentUnSelect().setEnabled(flag);
		getJButtonChildSelect().setEnabled(flag);
		getJButtonChildUnSelect().setEnabled(flag);
	}

	/**
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	private int showOkCancelDlg(Object message, String title) {
		int result = NCOptionPane.showOptionDialog(this, message, title,
				NCOptionPane.OK_CANCEL_OPTION, NCOptionPane.PLAIN_MESSAGE,
				null, null, null);
		return result;
	}

	/**
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	private void showOkDlg(String msg) {
		NCOptionPane.showMessageDialog(this, msg);
	}

	/**
	 * Called whenever the value of the selection changes.
	 * 
	 * @param e
	 *            the event that characterizes the change.
	 */
	public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
		// unenable all buttons
		if (e.getSource().equals(getUITreeBillTemplet())) {
			setButtonsState(false);
			try {
				if (getUITreeBillTemplet().getSelectionPath() != null) {
					// get current selected node
					node = (MutableTreeNode) getUITreeBillTemplet()
							.getSelectionPath().getLastPathComponent();
				} else {
					node = null;
				}
			} catch (Exception ex) {
				Logger.error(ex.getMessage());
			}

			setButtonsState();
		}

		if (e.getSource().equals(getUITreeMenu())) {
			initBillTypeCode();
			final TreePath path = e.getPath();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			// funccode = null;
			if (node.isLeaf()) {
				FuncRegisterVO funcVO = ((MenuItem) node.getUserObject())
						.getFunction();
				funccode = funcVO.getFuncode();
			}
			refreshBillTempletTree();
		}

	}

	private ArrayList<String> readTableList() {
		try {
			// read tablenames from DATADICTIONARY
			tableman
					.setDataSourceType(TableManager.DataSourceType.DATASOURCE_DATADICTIONARY);
			// tableman.readSourceTableNames();
			java.util.ArrayList<String> tablelist = tableman
					.getSourceTableNames();
			if (tablelist != null && tablelist.size() > 0)
				return tablelist;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}

	public void setBillcaption(String billcaption) {
		this.billcaption = billcaption;
	}

	public void setNodecode(String nodecode) {
		this.nodecode = nodecode;
	}

	private void onButtonActionBilltempletImport() {

		String filePath = getImportFilePath();
		if (filePath != null) {
			BillTempletVO[] importVOs = TemplateVOConverterUtil
					.convertToBTVOs(new String[] { filePath });

			if (importVOs == null) {
				MessageDialog.showErrorDlg(null, nc.ui.ml.NCLangRes
						.getInstance().getStrByID("_Template",
								"UPP_Template-000033")/*
													 * @res "提示"
													 */, nc.ui.ml.NCLangRes
						.getInstance().getStrByID("10100108",
								"UPP10100108-001168")/*
													 * @res
													 * "不允许导入系统模板和非本功能节点系统模板派生的自定义模板"
													 */);
				return;
			}

			if (isDefaultTempletInsertEnable(importVOs[0].getHeadVO())) {

				String msg = parent.getBillPanel().onSaveTemplet(importVOs[0],
						MainPanel.TEMPLET_ADD_DEFAULT, null);

				if (msg == null) {
					return;
				}
			} else {
				showOkDlg(nc.ui.ml.NCLangRes
				.getInstance().getStrByID("_bill",
						"0_bill0032")/**
										* @res "已有该单据类型的系统默认模板!"
											 */);
			}

			// if (!msg.equals("true")) {
			// // showOkDlg(msg + "模板保存失败!");
			// return msg
			// + nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
			// "UPP_Bill-000385")/*
			// * @res "模板保存失败!"
			// */;
			// }
			//
			// refreshBillTempletTree();
			//
			// // refresh
			// billcaption = billvo.getHeadVO().getBillTempletCaption();
			// billname = billvo.getHeadVO().getBillTempletName();
			// if (billcaption != null)
			// billcaption = billcaption.trim();
			// if (billname != null)
			// billname = billname.trim();
			// importQueryTemplet(importQTTVO);
		}

	}

	private void onButtonActionBilltempletExport() {

		BillTempletHeadVO headVO = getCurrentHeadVO();
		if (headVO == null) {
			return;
		}

		String pk = getCurrentHeadVO().getPrimaryKey();
		BillTempletVO vo = getBillTempletVOFromDB(pk);
		String filePath = getExportFilePath(vo);

		if (filePath != null) {
			String[] filePaths = new String[1];
			filePaths[0] = filePath;
			BillTempletVO[] totalVOs = new BillTempletVO[1];
			totalVOs[0] = vo;
			// 将VO转换称qt文件，传递vo和path
			TemplateVOConverterUtil.convertToBTXmlFile(totalVOs, filePaths);
			MessageDialog.showHintDlg(null, nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("10100108", "UPP10100108-000942")/* @res "模板导出" */
			, nc.ui.ml.NCLangRes.getInstance().getStrByID("10100108",
					nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("_bill", "0_bill0033")/* @res "单据模板成功导出到" */)
					+ filePath + " !");
		}

	}

	// 导入选取文件
	private String getImportFilePath() {
		UIFileChooser fileDlg = new UIFileChooser();
		fileDlg.setCurrentDirectory(new java.io.File("."));
		fileDlg.setFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(java.io.File f) {
				return f.getName().toLowerCase().endsWith(".bt")
						|| f.isDirectory();
			}

			public String getDescription() {
				return "bt格式";/*-=notranslate=-*/ 
			}
		});

		if (fileDlg.showOpenDialog(null) != nc.ui.pub.beans.UIFileChooser.APPROVE_OPTION) {
			return null;
		}
		File selectedFile = fileDlg.getSelectedFile();
		if (selectedFile == null)
			return null;
		if (!selectedFile.exists()) {
			MessageDialog.showErrorDlg(null,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("10100108",
							"UPP10100108-000945")/* @res "错误信息" */,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("10100108",
							"UPP10100108-000946")/* @res "该文件不存在" */);
			return null;
		} else {
			return selectedFile.getPath();
		}
	}

	/**
	 * 导出文件***.bt
	 * 
	 * @return
	 */
	private String getExportFilePath(BillTempletVO totalVO) {
		UIFileChooser fileDlg = new UIFileChooser();
		fileDlg.setCurrentDirectory(new java.io.File("."));
		fileDlg.setSelectedFile(new java.io.File(totalVO.getHeadVO()
				.getBillTempletCaption()
				+ ".bt"));
		fileDlg.setFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(java.io.File f) {
				return f.getName().toLowerCase().endsWith(".bt")
						|| f.isDirectory();
			}

			public String getDescription() {
				return "bt格式";/*-=notranslate=-*/ 
			}
		});

		if (fileDlg.showSaveDialog(null) != nc.ui.pub.beans.UIFileChooser.APPROVE_OPTION) {
			return null;
		}

		String filePath = null;
		File selectedFile = fileDlg.getSelectedFile();
		if (selectedFile == null)
			return null;
		if (!selectedFile.exists()) {
			try {
				selectedFile.createNewFile();
				filePath = selectedFile.getPath();
				if (!filePath.endsWith(".bt")) {
					filePath += ".bt";
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			filePath = selectedFile.getPath();
			if (!filePath.endsWith(".bt")) {
				filePath += ".bt";
			}
			return filePath;
		}
		return filePath;
	}
}