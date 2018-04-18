/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package nc.bs.pf.change;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import nc.bs.logging.Logger;
import nc.bs.pf.change.BillSplitter;
import nc.md.data.access.DASFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IAttribute;
import nc.md.model.IBean;
import nc.md.model.type.IType;
import nc.md.util.MDUtil;
import nc.uap.pf.metadata.PfMetadataTools;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pf.change.ChangeVOAdjustContext;
import nc.vo.pf.change.IChangeVOAdjust;
import nc.vo.pf.change.PfBillMappingUtil;
import nc.vo.pf.change.SplitItemVO;
import nc.vo.pf.change.UserDefineFunction;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.formulaset.FormulaParseFather;
import nc.vo.pub.formulaset.VarryVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.uap.pf.PFBusinessException;

public class BillMappingConvertor {
	protected String m_strDate = null;
	protected String m_strOperator = null;
	protected String m_strGroup;
	protected String m_strNowTime;
	protected UFDateTime m_strBuziTime;
	protected UFDate m_strBuziDate;
	protected String m_sourceBilltype = null;
	protected String m_destBilltype;
	protected String m_destTranstype;
	private String[][] sMoves = (String[][]) null;
	private String[][] sAssigns = (String[][]) null;
	private String[] sFormulas = null;
	private UserDefineFunction[] uFunctions = null;
	private String frontClass = null;
	private String backClass = null;
	private String reserveFrontClass = null;
	private String reserveBackClass = null;
	protected FormulaParseFather formulaParse;
	protected ArrayList<SplitItemVO> alSplitVO = null;

	public BillMappingConvertor(FormulaParseFather fp) {
		this.formulaParse = fp;
	}

	public FormulaParseFather getFormulaParse() {
		return this.formulaParse;
	}

	public ArrayList<SplitItemVO> getSplitVOList() {
		if (this.alSplitVO == null) {
			this.alSplitVO = new ArrayList();
		}

		return this.alSplitVO;
	}

	public String getFrontClass() {
		return this.frontClass;
	}

	public void setFrontClass(String frontClass) {
		this.frontClass = frontClass;
	}

	public String getBackClass() {
		return this.backClass;
	}

	public void setBackClass(String backClass) {
		this.backClass = backClass;
	}

	public void setMoveRules(String[][] mRules) {
		this.sMoves = mRules;
	}

	public void setAssignRules(String[][] aRules) {
		this.sAssigns = aRules;
	}

	public void setFormulaRules(String[] fRules) {
		this.sFormulas = fRules;
	}

	public void setUserDefineFunctions(UserDefineFunction[] uFuncs) {
		this.uFunctions = uFuncs;
	}

	public String[][] getMoveRules() {
		return this.sMoves;
	}

	public String[][] getAssignRules() {
		return this.sAssigns;
	}

	public String[] getFormulaRules() {
		return this.sFormulas;
	}

	public String getSourceBilltype() {
		return this.m_sourceBilltype;
	}

	public void setSourceBilltype(String sourceBilltype) {
		this.m_sourceBilltype = sourceBilltype;
	}

	public String getDestBilltype() {
		return this.m_destBilltype;
	}

	public void setDestBilltype(String billtype) {
		this.m_destBilltype = billtype;
	}

	public void setSysGroup(String corpCode) {
		this.m_strGroup = corpCode;
	}

	public void setSysTime(String time) {
		this.m_strNowTime = time;
	}

	public void setBuziTime(UFDateTime time) {
		this.m_strBuziTime = time;
	}

	public void setBuziDate(UFDate date) {
		this.m_strBuziDate = date;
	}

	public void setSysDate(String sysDate) {
		this.m_strDate = sysDate;
	}

	public void setSysOperator(String sysOperator) {
		this.m_strOperator = sysOperator;
	}

	public boolean isCheck(CircularlyAccessibleValueObject souce,
			CircularlyAccessibleValueObject target) {
		target.setStatus(souce.getStatus());
		return true;
	}

	private IChangeVOAdjust getAfterClassImpl() throws BusinessException {
		if (this.getBackClass() != null
				&& this.getBackClass().trim().length() != 0) {
			try {
				Class ex = Class.forName(this.getBackClass());
				Object afterClassInstance = ex.newInstance();
				return (IChangeVOAdjust) afterClassInstance;
			} catch (Exception arg2) {
				Logger.error(arg2.getMessage(), arg2);
				throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("busitype", "busitypehint-000017",
								(String) null,
								new String[] { arg2.getMessage() }));
			}
		} else {
			return null;
		}
	}

	private IChangeVOAdjust getBeforeClassImpl() throws BusinessException {
		if (this.getFrontClass() != null
				&& this.getFrontClass().trim().length() != 0) {
			try {
				Class ex = Class.forName(this.getFrontClass());
				Object afterClassInstance = ex.newInstance();
				return (IChangeVOAdjust) afterClassInstance;
			} catch (Exception arg2) {
				Logger.error(arg2.getMessage(), arg2);
				throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("busitype", "busitypehint-000018",
								(String) null,
								new String[] { arg2.getMessage() }));
			}
		} else {
			return null;
		}
	}

	private IChangeVOAdjust getReserveClassImpl(String className)
			throws BusinessException {
		if (StringUtil.isEmptyWithTrim(className)) {
			return null;
		} else {
			try {
				Class ex = Class.forName(className);
				Object instance = ex.newInstance();
				return (IChangeVOAdjust) instance;
			} catch (Exception arg3) {
				Logger.error(arg3.getMessage(), arg3);
				throw new PFBusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("busitype", "busitypehint-000019",
								(String) null,
								new String[] { arg3.getMessage() }));
			}
		}
	}

	private void execFormulasMetaToMeta(AggregatedValueObject sourceVO,
			AggregatedValueObject targetVO) throws BusinessException {
		String[] formulas = this.sFormulas;
		if (formulas != null && formulas.length != 0) {
			for (int i = 0; i < formulas.length; ++i) {
				Logger.debug("**执行公式交换:" + formulas[i]);
				this.getFormulaParse().setExpress(formulas[i]);
				VarryVO varry = this.getFormulaParse().getVarry();
				if (varry != null) {
					this.execFormulaMetaToMeta2(sourceVO, targetVO, varry);
				}
			}

		}
	}

	private void execFormulasMetaToMetaBatch(AggregatedValueObject[] sourceVOs,
			AggregatedValueObject[] targetVOs) throws BusinessException {
		String[] formulas = this.sFormulas;
		if (formulas != null && formulas.length != 0) {
			for (int i = 0; i < formulas.length; ++i) {
				Logger.debug("**执行公式交换:" + formulas[i]);
				this.getFormulaParse().setExpress(formulas[i]);
				VarryVO varry = this.getFormulaParse().getVarry();
				if (varry != null) {
					this.execFormulaMetaToMeta2Batch(sourceVOs, targetVOs,
							varry);
				}
			}

		}
	}

	private boolean canChangeBatch(AggregatedValueObject[] sourceVOs,
			AggregatedValueObject[] targetVOs, VarryVO varry) {
		for (int j = 0; j < (varry.getVarry() == null ? 0
				: varry.getVarry().length); ++j) {
			String var = varry.getVarry()[j];
			int start = 0;

			for (int end = sourceVOs.length; start < end; ++start) {
				Object value = this.getVarValueFromMetadata(sourceVOs[start],
						var);
				if (value == null) {
					value = this.getVarValueFromMetadata(targetVOs[start], var);
				}

				if (value instanceof Object[]
						&& (Object[]) ((Object[]) value) != null
						&& ((Object[]) ((Object[]) value)).length > 1) {
					return false;
				}
			}
		}

		return true;
	}

	private void execFormulaMetaToMeta2Batch(AggregatedValueObject[] sourceVOs,
			AggregatedValueObject[] targetVOs, VarryVO varry)
			throws BusinessException {
		String destPath = varry.getFormulaName();
		NCObject ncObj = NCObject.newInstance(targetVOs[0]);
		boolean canBatch = this.canChangeBatch(sourceVOs, targetVOs, varry);
		if (canBatch
				&& (!this.isCollectionOfPath(destPath, ncObj.getRelatedBean()) || !this
						.isCollectionOfSrc(varry))) {
			this.parseMetaFormulaVarBatch(sourceVOs, targetVOs, varry);
			IType arg13 = ncObj.getRelatedBean().getAttributeByPath(destPath)
					.getDataType();
			Object[] realValues = null;
			Object[] valueObjs = this.getFormulaParse().getValueO();
			realValues = new Object[targetVOs.length];

			for (int errStr = 0; errStr < targetVOs.length; ++errStr) {
				if (valueObjs.length != 1 || varry.getVarry() != null
						&& varry.getVarry().length != 0) {
					realValues[errStr] = this.getValueByIType(
							valueObjs[errStr], arg13);
				} else {
					realValues[errStr] = this.getValueByIType(valueObjs[0],
							arg13);
				}
			}

			String arg14 = this.getFormulaParse().getError();
			if (!StringUtil.isEmptyWithTrim(arg14)) {
				throw new PFBusinessException(arg14);
			}

			int i = 0;

			for (int end = targetVOs.length; i < end; ++i) {
				NCObject _ncObj = NCObject.newInstance(targetVOs[i]);
				_ncObj.setAttributeValue(destPath, realValues[i]);
			}
		} else {
			for (int iType = 0; iType < sourceVOs.length; ++iType) {
				this.execFormulaMetaToMeta2(sourceVOs[iType], targetVOs[iType],
						varry);
			}
		}

	}

	private void execFormulaMetaToMeta2(AggregatedValueObject sourceVO,
			AggregatedValueObject targetVO, VarryVO varry)
			throws BusinessException {
		this.parseMetaFormulaVar(sourceVO, targetVO, varry);
		String destPath = varry.getFormulaName();
		NCObject ncObj = NCObject.newInstance(targetVO);
		IType iType = ncObj.getRelatedBean().getAttributeByPath(destPath)
				.getDataType();
		Object realValue = null;
		if (this.isCollectionOfPath(destPath, ncObj.getRelatedBean())
				&& this.isCollectionOfSrc(varry)) {
			Object[] arg10 = this.getFormulaParse().getValueO();

			for (int i = 0; i < (arg10 == null ? 0 : arg10.length); ++i) {
				arg10[i] = this.getValueByIType(arg10[i], iType);
			}

			realValue = arg10;
		} else {
			Object errStr = this.getFormulaParse().getValueAsObject();
			realValue = this.getValueByIType(errStr, iType);
		}

		String arg9 = this.getFormulaParse().getError();
		if (!StringUtil.isEmptyWithTrim(arg9)) {
			throw new PFBusinessException(arg9);
		} else {
			ncObj.setAttributeValue(destPath, realValue);
		}
	}

	private boolean isCollectionOfSrc(VarryVO varry) {
		if (varry.getVarry() != null && varry.getVarry().length > 0) {
			String[] arr$ = varry.getVarry();
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				String var = arr$[i$];
				Object tmpObj = this.getFormulaParse().getJepParser()
						.getVariables().get(var);
				if (tmpObj instanceof List) {
					return true;
				}
			}
		}

		return false;
	}

	private void parseMetaFormulaVarBatch(AggregatedValueObject[] sourceVOs,
			AggregatedValueObject[] targetVOs, VarryVO varry) {
		Logger.debug("**开始执行元数据公式");
		NCObject[] sourceObjs = new NCObject[sourceVOs.length];
		NCObject[] targetObjs = new NCObject[targetVOs.length];
		int j = 0;

		for (int var = sourceObjs.length; j < var; ++j) {
			sourceObjs[j] = NCObject.newInstance(sourceVOs[j]);
		}

		for (j = 0; j < (varry.getVarry() == null ? 0 : varry.getVarry().length); ++j) {
			String arg14 = varry.getVarry()[j];
			ArrayList values = new ArrayList();
			List sourceValues = null;
			int end;
			if (PfBillMappingUtil.isSystemEnvField(arg14)) {
				Object arg15 = this.getEnvParamValue(arg14);
				end = 0;

				for (int arg16 = sourceObjs.length; end < arg16; ++end) {
					values.add(arg15);
				}
			} else {
				sourceValues = DASFacade.getAttributeValue(
						new String[] { arg14 }, sourceObjs);
				int index;
				if (sourceValues == null || sourceValues.size() == 0) {
					index = 0;

					for (end = targetObjs.length; index < end; ++index) {
						targetObjs[index] = NCObject
								.newInstance(targetVOs[index]);
					}

					sourceValues = DASFacade.getAttributeValue(
							new String[] { arg14 }, targetObjs);
				}

				index = 0;

				for (end = sourceObjs.length; index < end
						&& sourceValues != null; ++index) {
					Object[] temp_sourceValue = (Object[]) sourceValues
							.get(index);
					Object sourceValue = null;
					if (temp_sourceValue != null
							&& temp_sourceValue.length != 0) {
						sourceValue = temp_sourceValue[0];
					}

					if (sourceValue instanceof Object[]) {
						Object temp = null;
						if (sourceValue != null
								&& ((Object[]) ((Object[]) sourceValue)).length == 1) {
							temp = ((Object[]) ((Object[]) sourceValue))[0];
						}

						values.add(temp);
					} else {
						values.add(sourceValue);
					}
				}
			}

			this.getFormulaParse().addVariable(arg14, values);
		}

	}

	private void parseMetaFormulaVar(AggregatedValueObject sourceVO,
			AggregatedValueObject targetVO, VarryVO varry) {
		Logger.debug("**开始执行元数据公式");

		for (int j = 0; j < (varry.getVarry() == null ? 0
				: varry.getVarry().length); ++j) {
			String var = varry.getVarry()[j];
			Object value = this.getVarValueFromMetadata(sourceVO, var);
			if (value == null) {
				value = this.getVarValueFromMetadata(targetVO, var);
			}

			this.getFormulaParse().addVariable(var, value);
			Logger.debug("**变量var=" + var + ";赋值value=" + value);
		}

	}

	private void execFieldsMetaToMetaBatch(AggregatedValueObject[] sourceVOs,
			AggregatedValueObject[] targetVOs) {
		if (this.sMoves != null && this.sMoves.length != 0) {
			NCObject[] targetNcObjs = new NCObject[targetVOs.length];
			NCObject[] sourceNcObjs = new NCObject[sourceVOs.length];
			int value = 0;

			int noSysEnvPathsMap;
			for (noSysEnvPathsMap = targetVOs.length; value < noSysEnvPathsMap; ++value) {
				targetNcObjs[value] = NCObject.newInstance(targetVOs[value]);
			}

			value = 0;

			for (noSysEnvPathsMap = sourceVOs.length; value < noSysEnvPathsMap; ++value) {
				sourceNcObjs[value] = NCObject.newInstance(sourceVOs[value]);
			}

			Object arg19 = null;
			HashMap arg18 = new HashMap();
			HashMap sysEnvPathsMap = new HashMap();

			for (int syskeys = 0; syskeys < this.sMoves.length; ++syskeys) {
				if (PfBillMappingUtil.isSystemEnvField(this.sMoves[syskeys][1])) {
					sysEnvPathsMap.put(this.sMoves[syskeys][0],
							this.sMoves[syskeys][1]);
				} else {
					arg18.put(this.sMoves[syskeys][0], this.sMoves[syskeys][1]);
				}
			}

			Set arg20 = sysEnvPathsMap.keySet();
			Iterator destAttributes = arg20.iterator();

			int j;
			IType iType;
			while (destAttributes.hasNext()) {
				String srcAttributes = (String) destAttributes.next();
				String varValueArr = (String) sysEnvPathsMap.get(srcAttributes);
				Object start = this.getEnvParamValue(varValueArr);
				NCObject[] end = targetNcObjs;
				int varValues = targetNcObjs.length;

				for (j = 0; j < varValues; ++j) {
					NCObject k = end[j];
					iType = k.getRelatedBean()
							.getAttributeByPath(srcAttributes).getDataType();
					if (start != null) {
						this.doMeta2Meta(k, start, iType, srcAttributes);
					}
				}
			}

			String[] arg21 = (String[]) arg18.keySet().toArray(new String[0]);
			ArrayList arg23 = new ArrayList();
			int arg22 = 0;

			int arg24;
			for (arg24 = arg21.length; arg22 < arg24; ++arg22) {
				arg23.add(arg18.get(arg21[arg22]));
			}

			List arg25 = DASFacade.getAttributeValue(
					(String[]) arg23.toArray(new String[0]), sourceNcObjs);
			arg24 = 0;

			for (int arg26 = targetNcObjs == null ? 0 : targetNcObjs.length; arg24 < arg26; ++arg24) {
				Object[] arg27 = (Object[]) arg25.get(arg24);
				j = 0;

				for (int arg28 = arg21.length; j < arg28; ++j) {
					iType = targetNcObjs[arg24].getRelatedBean()
							.getAttributeByPath(arg21[j]).getDataType();
					Object valueObj = arg27[j];
					if (valueObj != null) {
						this.doMeta2Meta(targetNcObjs[arg24], valueObj, iType,
								arg21[j]);
					}
				}
			}

		}
	}

	private void execFieldsMetaToMeta(AggregatedValueObject sourceVO,
			AggregatedValueObject targetVO) {
		if (this.sMoves != null && this.sMoves.length != 0) {
			NCObject ncObj = NCObject.newInstance(targetVO);
			NCObject sourceObj = NCObject.newInstance(sourceVO);
			Object value = null;
			HashMap noSysEnvPathsMap = new HashMap();
			HashMap sysEnvPathsMap = new HashMap();

			for (int syskeys = 0; syskeys < this.sMoves.length; ++syskeys) {
				if (PfBillMappingUtil.isSystemEnvField(this.sMoves[syskeys][1])) {
					sysEnvPathsMap.put(this.sMoves[syskeys][0],
							this.sMoves[syskeys][1]);
				} else {
					noSysEnvPathsMap.put(this.sMoves[syskeys][0],
							this.sMoves[syskeys][1]);
				}
			}

			Set arg15 = sysEnvPathsMap.keySet();
			Iterator destAttributes = arg15.iterator();

			while (destAttributes.hasNext()) {
				String srcAttributes = (String) destAttributes.next();
				String varValues = (String) sysEnvPathsMap.get(srcAttributes);
				IType i = ncObj.getRelatedBean()
						.getAttributeByPath(srcAttributes).getDataType();
				Object end = this.getEnvParamValue(varValues);
				if (end != null) {
					this.doMeta2Meta(ncObj, end, i, srcAttributes);
				}
			}

			String[] arg16 = (String[]) noSysEnvPathsMap.keySet().toArray(
					new String[0]);
			ArrayList arg18 = new ArrayList();
			int arg17 = 0;

			int arg19;
			for (arg19 = arg16.length; arg17 < arg19; ++arg17) {
				arg18.add(noSysEnvPathsMap.get(arg16[arg17]));
			}

			Object[] arg20 = this.getVarValueFromMetadataBatch(sourceObj,
					(String[]) arg18.toArray(new String[0]));
			arg19 = 0;

			for (int arg21 = arg16.length; arg19 < arg21; ++arg19) {
				IType iType = ncObj.getRelatedBean()
						.getAttributeByPath(arg16[arg19]).getDataType();
				Object valueObj = arg20[arg19];
				if (valueObj != null) {
					this.doMeta2Meta(ncObj, valueObj, iType, arg16[arg19]);
				}
			}

		}
	}

	private void doMeta2Meta(NCObject ncObj, Object valueObj, IType iType,
			String name) {
		if (PfMetadataTools.isObjectArray(valueObj)) {
			int len = Array.getLength(valueObj);
			Object[] objArray = new Object[len];

			for (int j = 0; j < len; ++j) {
				objArray[j] = this.getValueByIType(Array.get(valueObj, j),
						iType);
			}

			if (this.isCollectionOfPath(name, ncObj.getRelatedBean())) {
				ncObj.setAttributeValue(name, objArray);
			} else {
				ncObj.setAttributeValue(name, Array.get(objArray, 0));
			}
		} else {
			ncObj.setAttributeValue(name, this.getValueByIType(valueObj, iType));
		}

	}

	private void execAssignsForMetadata(AggregatedValueObject targetVO) {
		if (this.sAssigns != null && this.sAssigns.length != 0) {
			NCObject ncObj = NCObject.newInstance(targetVO);

			for (int i = 0; i < this.sAssigns.length; ++i) {
				Logger.debug(" 执行赋值：" + this.sAssigns[i][0] + "->"
						+ this.sAssigns[i][1]);
				Object valueObj = this.sAssigns[i][1];
				if (PfBillMappingUtil.isSystemEnvField(this.sAssigns[i][1])) {
					valueObj = this.getEnvParamValue(this.sAssigns[i][1]);
				}

				IAttribute attr = ncObj.getRelatedBean().getAttributeByPath(
						this.sAssigns[i][0]);
				if (attr == null) {
					Logger.error("取路径错误，请检查VO交换路径的配置，实体："
							+ ncObj.getRelatedBean().getDisplayName()
							+ "###错误的属性路径：：" + this.sAssigns[i][0]);
				} else {
					ncObj.setAttributeValue(this.sAssigns[i][0],
							this.getValueByIType(valueObj, attr.getDataType()));
				}
			}

		}
	}

	private Object getValueByIType(Object valueObj, IType iType) {
		if (valueObj == null) {
			return null;
		} else {
			Object objTarget = iType.valueOf(valueObj);
			return objTarget;
		}
	}

	public AggregatedValueObject[] retChangeBusiVOs(String srcBillOrTranstype,
			String destBillOrTranstype, AggregatedValueObject[] sorceVOs)
			throws BusinessException {
		if (sorceVOs == null) {
			return null;
		} else {
			AggregatedValueObject[] originalVOs = sorceVOs;
			ChangeVOAdjustContext adjustContext = new ChangeVOAdjustContext();
			adjustContext.setSrcBilltype(srcBillOrTranstype);
			adjustContext.setDestBilltype(destBillOrTranstype);
			adjustContext.clearSplitList().addAll(this.getSplitVOList());
			IChangeVOAdjust reserveBeforeClass = this.getReserveClassImpl(this
					.getReserveFrontClass());
			if (reserveBeforeClass != null) {
				sorceVOs = reserveBeforeClass.batchAdjustBeforeChange(sorceVOs,
						adjustContext);
				if (sorceVOs == null) {
					return null;
				}
			}

			IChangeVOAdjust beforeClassImpl = this.getBeforeClassImpl();
			if (beforeClassImpl != null) {
				sorceVOs = beforeClassImpl.batchAdjustBeforeChange(sorceVOs,
						adjustContext);
				if (sorceVOs == null) {
					return null;
				}
			}

			BillSplitter pfSplit = new BillSplitter();
			sorceVOs = pfSplit.splitBeforProc(sorceVOs, adjustContext);
			AggregatedValueObject[] tagVOs = new AggregatedValueObject[0];
			tagVOs = this.metadataToMetadataBatch(srcBillOrTranstype,
					destBillOrTranstype, sorceVOs, tagVOs);
			tagVOs = pfSplit.splitAfterProc(sorceVOs, tagVOs, adjustContext);
			IChangeVOAdjust afterClassImpl = this.getAfterClassImpl();
			if (afterClassImpl != null) {
				tagVOs = afterClassImpl.batchAdjustAfterChange(sorceVOs,
						tagVOs, adjustContext);
			}

			IChangeVOAdjust reserveAfterClass = this.getReserveClassImpl(this
					.getReserveBackClass());
			if (reserveAfterClass != null) {
				tagVOs = reserveAfterClass.batchAdjustAfterChange(sorceVOs,
						tagVOs, adjustContext);
			}

			pfSplit.clearSplitChange(originalVOs, adjustContext);
			return tagVOs;
		}
	}

	public AggregatedValueObject[] retChangeBusiVOs(String srcBillOrTranstype,
			String destBillOrTranstype, AggregatedValueObject[] sorceVOs,
			int classifyMode) throws BusinessException {
		if (sorceVOs == null) {
			return null;
		} else {
			AggregatedValueObject[] originalVOs = sorceVOs;
			ChangeVOAdjustContext adjustContext = new ChangeVOAdjustContext();
			adjustContext.setSrcBilltype(srcBillOrTranstype);
			adjustContext.setDestBilltype(destBillOrTranstype);
			adjustContext.clearSplitList().addAll(this.getSplitVOList());
			IChangeVOAdjust reserveBeforeClass = this.getReserveClassImpl(this
					.getReserveFrontClass());
			if (reserveBeforeClass != null) {
				sorceVOs = reserveBeforeClass.batchAdjustBeforeChange(sorceVOs,
						adjustContext);
				if (sorceVOs == null) {
					return null;
				}
			}

			IChangeVOAdjust beforeClassImpl = this.getBeforeClassImpl();
			if (beforeClassImpl != null) {
				sorceVOs = beforeClassImpl.batchAdjustBeforeChange(sorceVOs,
						adjustContext);
				if (sorceVOs == null) {
					return null;
				}
			}

			BillSplitter pfSplit = new BillSplitter();
			sorceVOs = pfSplit.splitBeforProc(sorceVOs, adjustContext);
			AggregatedValueObject[] tagVOs = new AggregatedValueObject[0];
			tagVOs = this.metadataToMetadataBatch(srcBillOrTranstype,
					destBillOrTranstype, sorceVOs, tagVOs, classifyMode);
			tagVOs = pfSplit.splitAfterProc(sorceVOs, tagVOs, adjustContext);
			IChangeVOAdjust afterClassImpl = this.getAfterClassImpl();
			if (afterClassImpl != null) {
				tagVOs = afterClassImpl.batchAdjustAfterChange(sorceVOs,
						tagVOs, adjustContext);
			}

			IChangeVOAdjust reserveAfterClass = this.getReserveClassImpl(this
					.getReserveBackClass());
			if (reserveAfterClass != null) {
				tagVOs = reserveAfterClass.batchAdjustAfterChange(sorceVOs,
						tagVOs, adjustContext);
			}

			pfSplit.clearSplitChange(originalVOs, adjustContext);
			return tagVOs;
		}
	}

	private AggregatedValueObject[] metadataToMetadataBatch(
			String srcBillOrTranstype, String destBillOrTranstype,
			AggregatedValueObject[] sorceVOs, AggregatedValueObject[] tagVOs)
			throws BusinessException {
		tagVOs = PfBillMappingUtil.initDestBillVOs(srcBillOrTranstype,
				destBillOrTranstype, sorceVOs, this);

		for (int i = 0; i < sorceVOs.length; ++i) {
			AggregatedValueObject arg9999 = sorceVOs[i];
			AggregatedValueObject targetVO = tagVOs[i];
			this.execAssignsForMetadata(targetVO);
		}

		this.execFieldsMetaToMetaBatch(sorceVOs, tagVOs);
		this.execFormulasMetaToMetaBatch(sorceVOs, tagVOs);
		return tagVOs;
	}

	private AggregatedValueObject[] metadataToMetadataBatch(
			String srcBillOrTranstype, String destBillOrTranstype,
			AggregatedValueObject[] sorceVOs, AggregatedValueObject[] tagVOs,
			int classifyMode) throws BusinessException {
		tagVOs = PfBillMappingUtil.initDestBillVOs(srcBillOrTranstype,
				destBillOrTranstype, sorceVOs, this, classifyMode);

		for (int i = 0; i < sorceVOs.length; ++i) {
			AggregatedValueObject arg9999 = sorceVOs[i];
			AggregatedValueObject targetVO = tagVOs[i];
			this.execAssignsForMetadata(targetVO);
		}

		this.execFieldsMetaToMetaBatch(sorceVOs, tagVOs);
		this.execFormulasMetaToMetaBatch(sorceVOs, tagVOs);
		return tagVOs;
	}

	public static boolean isParentField(String key) {
		return key == null ? false : key.startsWith("H_");
	}

	public static boolean isChildField(String key) {
		return key == null ? false : key.startsWith("B_");
	}

	private boolean isCollectionOfPath(String strPath, IBean bean) {
		IAttribute attr = null;
		int npos = strPath.indexOf(46);
		if (npos < 0) {
			attr = bean.getAttributeByName(strPath);
			return MDUtil.isCollectionType(attr.getDataType());
		} else {
			String[] paths = strPath.split("\\.");
			StringBuffer newPath = new StringBuffer();

			for (int j = 0; j < paths.length; ++j) {
				newPath.append(paths[j]);
				attr = bean.getAttributeByPath(newPath.toString());
				if (MDUtil.isCollectionType(attr.getDataType())) {
					return true;
				}

				newPath.append(".");
			}

			return false;
		}
	}

	private Object getEnvParamValue(String srcField) {
		Object valueObj = null;
		if (srcField.equals("SYSDATE")) {
			valueObj = this.m_strDate;
		} else if (srcField.equals("BUZIDATE")) {
			valueObj = this.m_strBuziDate;
		} else if (srcField.equals("SYSOPERATOR")) {
			valueObj = this.m_strOperator;
		} else if (srcField.equals("SYSGROUP")) {
			valueObj = this.m_strGroup;
		} else if (srcField.equals("SYSTIME")) {
			valueObj = this.m_strNowTime;
		} else if (srcField.equals("BUZITIME")) {
			valueObj = this.m_strBuziTime;
		} else if (srcField.equals("NCSYSUSER")) {
			valueObj = "NC_USER0000000000000";
		} else if (srcField.equals("DESTBILLTYPE")) {
			valueObj = this.m_destBilltype;
		} else if (srcField.equals("DESTTRANSTYPE")) {
			valueObj = this.m_destTranstype;
		}

		return valueObj;
	}

	private Object getVarValueFromMetadata(AggregatedValueObject sourceVO,
			String srcPath) {
		Object valueObj = null;
		if (PfBillMappingUtil.isSystemEnvField(srcPath)) {
			valueObj = this.getEnvParamValue(srcPath);
		} else {
			NCObject ncObj = NCObject.newInstance(sourceVO);
			valueObj = ncObj.getAttributeValue(srcPath);
			if (valueObj != null && !valueObj.equals("")
					&& valueObj instanceof String) {
				valueObj = valueObj.toString();
			}
		}

		return valueObj;
	}

	private Object[] getVarValueFromMetadataBatch(
			AggregatedValueObject[] sourceVOs, String srcPath) {
		Object[] results = new Object[sourceVOs.length];
		if (PfBillMappingUtil.isSystemEnvField(srcPath)) {
			Object ncObjs = this.getEnvParamValue(srcPath);
			Arrays.fill(results, ncObjs);
		} else {
			NCObject[] arg9 = new NCObject[sourceVOs.length];
			int valueObjArr = 0;

			int start;
			for (start = sourceVOs.length; valueObjArr < start; ++valueObjArr) {
				arg9[valueObjArr] = NCObject
						.newInstance(sourceVOs[valueObjArr]);
			}

			List arg10 = DASFacade.getAttributeValue(new String[] { srcPath },
					arg9);
			start = 0;

			for (int end = results.length; start < end; ++start) {
				Object[] objs = (Object[]) arg10.get(start);
				Object result = objs[0];
				if (result != null && !result.equals("")
						&& result instanceof String) {
					result = result.toString();
				}

				results[start] = result;
			}
		}

		return results;
	}

	private Object[] getVarValueFromMetadataBatch(NCObject sourceObj,
			String[] srcPaths) {
		Object[] valueObjs = sourceObj.getAttributeValue(srcPaths);
		return valueObjs;
	}

	private Object getVarValueFromMetadata2(NCObject sourceObj, String srcPath) {
		Object valueObj = null;
		if (PfBillMappingUtil.isSystemEnvField(srcPath)) {
			valueObj = this.getEnvParamValue(srcPath);
		} else {
			valueObj = sourceObj.getAttributeValue(srcPath);
			if (valueObj != null && !valueObj.equals("")
					&& valueObj instanceof String) {
				valueObj = valueObj.toString();
			}
		}

		return valueObj;
	}

	public void initFormulaParse() {
		Logger.debug(">>>AbstractConversion.initFormulaParse() called");
		if (this.uFunctions != null) {
			for (int i = 0; i < this.uFunctions.length; ++i) {
				String className = this.uFunctions[i].getClassName();
				String methodName = this.uFunctions[i].getMethodName();
				Class returnType = this.uFunctions[i].getReturnType();
				Logger.debug(">>>className=" + className);
				Logger.debug(">>>methodName=" + methodName);
				Logger.debug(">>>returnType=" + returnType);
				this.getFormulaParse().setSelfMethod(className, methodName,
						returnType, this.uFunctions[i].getArgTypes(), false);
			}

		}
	}

	public static String getFieldName(String key) {
		String fieldname = null;
		if (!key.startsWith("H_") && !key.startsWith("B_")) {
			fieldname = key;
		} else {
			fieldname = key.substring(2);
		}

		return fieldname;
	}

	protected void setValueToVo(Object valueObj,
			CircularlyAccessibleValueObject billvo, String fieldName,
			Class fieldType) {
		if (valueObj != null) {
			if (fieldType != null) {
				Object oTarget = valueObj;
				if (fieldType.equals(String.class)) {
					String dblTarget = valueObj.toString();
					oTarget = dblTarget;
				}

				if (fieldType.equals(UFDouble.class)) {
					if (valueObj instanceof UFDouble) {
						oTarget = (UFDouble) valueObj;
					} else {
						UFDouble dblTarget2 = new UFDouble(valueObj.toString());
						oTarget = dblTarget2;
					}
				}

				if (fieldType.equals(Integer.class)) {
					if (valueObj instanceof Integer) {
						oTarget = (Integer) valueObj;
					} else {
						Integer dblTarget1 = new Integer(valueObj.toString());
						oTarget = dblTarget1;
					}
				}

				if (fieldType.equals(UFBoolean.class)) {
					UFBoolean dblTarget4 = UFBoolean.valueOf(valueObj
							.toString());
					oTarget = dblTarget4;
				}

				if (fieldType.equals(UFDate.class)) {
					if (valueObj instanceof UFDate) {
						oTarget = (UFDate) valueObj;
					} else {
						UFDate dblTarget3 = new UFDate(valueObj.toString());
						oTarget = dblTarget3;
					}
				}

				billvo.setAttributeValue(fieldName, oTarget);
			}
		}
	}

	protected Class getFieldType(CircularlyAccessibleValueObject billvo,
			String fieldName) {
		Class cl = null;

		try {
			cl = billvo.getClass().getDeclaredField("m_" + fieldName).getType();
		} catch (NoSuchFieldException arg7) {
			try {
				cl = billvo.getClass().getDeclaredField(fieldName).getType();
			} catch (NoSuchFieldException arg5) {
				cl = String.class;
			} catch (Exception arg6) {
				Logger.warn(arg6.getMessage(), arg6);
			}
		} catch (Exception arg8) {
			Logger.warn(arg8.getMessage(), arg8);
		}

		return cl;
	}

	public void setDestTranstype(String transtype) {
		this.m_destTranstype = transtype;
	}

	public String getReserveFrontClass() {
		return this.reserveFrontClass;
	}

	public void setReserveFrontClass(String reserveFrontClass) {
		this.reserveFrontClass = reserveFrontClass;
	}

	public String getReserveBackClass() {
		return this.reserveBackClass;
	}

	public void setReserveBackClass(String reserveBackClass) {
		this.reserveBackClass = reserveBackClass;
	}
}