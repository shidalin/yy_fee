package nc.vo.fee.subagreement;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 *   此处添加累的描述信息
 * </p>
 *  创建日期:2017-12-6
 * @author YONYOU NC
 * @version NCPrj ??
 */
 
public class SubagreementVO extends SuperVO {
	
/**
*主键
*/
public java.lang.String pk_subagreement;
/**
*集团
*/
public java.lang.String pk_group;
/**
*组织
*/
public java.lang.String pk_org;
/**
*组织多版本
*/
public java.lang.String pk_org_v;
/**
*单据类型编码
*/
public java.lang.String pk_billtypecode;
/**
*单据类型
*/
public java.lang.String pk_billtypeid;
/**
*单据号
*/
public java.lang.String vbillcode;
/**
*合同编号
*/
public java.lang.String contratcno;
/**
*服务卡号
*/
public java.lang.String serviceno;
/**
*分包合同名称
*/
public java.lang.String subcontractname;
/**
*签约日期
*/
public UFDate signdate;
/**
*合同开始日期
*/
public UFDate constartdate;
/**
*合同结束日期
*/
public UFDate conenddate;
/**
*原始合同开始日期
*/
public UFDate srcconstartdate;
/**
*原始合同结束日期
*/
public UFDate srcconenddate;
/**
*合同总天数
*/
public java.lang.Integer contotaldays;
/**
*已预提天数
*/
public java.lang.Integer useddays;
/**
*剩余可预提天数
*/
public java.lang.Integer activedays;
/**
*已预提金额
*/
public nc.vo.pub.lang.UFDouble usedmny;
/**
*剩余可预提金额
*/
public nc.vo.pub.lang.UFDouble activemny;
/**
*班组
*/
public java.lang.String pk_teamwork;
/**
*部门
*/
public java.lang.String pk_dept;
/**
*人员
*/
public java.lang.String pk_psndoc;
/**
*合同金额
*/
public nc.vo.pub.lang.UFDouble contractmny;
/**
*概述
*/
public java.lang.String overview;
/**
*单据日期
*/
public UFDate dbilldate;
/**
*创建人
*/
public java.lang.String creator;
/**
*创建时间
*/
public UFDateTime creationtime;
/**
*最后修改人
*/
public java.lang.String modifier;
/**
*最后修改时间
*/
public UFDateTime modifiedtime;
/**
*备注
*/
public java.lang.String vmemo;
/**
*来源单据号
*/
public java.lang.String vsourcecode;
/**
*来源单据
*/
public java.lang.String csourceid;
/**
*来源单据类型
*/
public java.lang.String csourcetypecode;
/**
*自定义项1
*/
public java.lang.String vdef1;
/**
*自定义项2
*/
public java.lang.String vdef2;
/**
*自定义项3
*/
public java.lang.String vdef3;
/**
*自定义项4
*/
public java.lang.String vdef4;
/**
*自定义项5
*/
public java.lang.String vdef5;
/**
*自定义项6
*/
public java.lang.String vdef6;
/**
*自定义项7
*/
public java.lang.String vdef7;
/**
*自定义项8
*/
public java.lang.String vdef8;
/**
*自定义项9
*/
public java.lang.String vdef9;
/**
*自定义项10
*/
public java.lang.String vdef10;
/**
*自定义项11
*/
public java.lang.String vdef11;
/**
*自定义项12
*/
public java.lang.String vdef12;
/**
*自定义项13
*/
public java.lang.String vdef13;
/**
*自定义项14
*/
public java.lang.String vdef14;
/**
*自定义项15
*/
public java.lang.String vdef15;
/**
*自定义项16
*/
public java.lang.String vdef16;
/**
*自定义项17
*/
public java.lang.String vdef17;
/**
*自定义项18
*/
public java.lang.String vdef18;
/**
*自定义项19
*/
public java.lang.String vdef19;
/**
*自定义项20
*/
public java.lang.String vdef20;
/**
*时间戳
*/
public UFDateTime ts;
    
    
/**
* 属性 pk_subagreement的Getter方法.属性名：主键
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getPk_subagreement() {
return this.pk_subagreement;
} 

/**
* 属性pk_subagreement的Setter方法.属性名：主键
* 创建日期:2017-12-6
* @param newPk_subagreement java.lang.String
*/
public void setPk_subagreement ( java.lang.String pk_subagreement) {
this.pk_subagreement=pk_subagreement;
} 
 
/**
* 属性 pk_group的Getter方法.属性名：集团
*  创建日期:2017-12-6
* @return nc.vo.org.GroupVO
*/
public java.lang.String getPk_group() {
return this.pk_group;
} 

/**
* 属性pk_group的Setter方法.属性名：集团
* 创建日期:2017-12-6
* @param newPk_group nc.vo.org.GroupVO
*/
public void setPk_group ( java.lang.String pk_group) {
this.pk_group=pk_group;
} 
 
/**
* 属性 pk_org的Getter方法.属性名：组织
*  创建日期:2017-12-6
* @return nc.vo.org.OrgVO
*/
public java.lang.String getPk_org() {
return this.pk_org;
} 

/**
* 属性pk_org的Setter方法.属性名：组织
* 创建日期:2017-12-6
* @param newPk_org nc.vo.org.OrgVO
*/
public void setPk_org ( java.lang.String pk_org) {
this.pk_org=pk_org;
} 
 
/**
* 属性 pk_org_v的Getter方法.属性名：组织多版本
*  创建日期:2017-12-6
* @return nc.vo.vorg.OrgVersionVO
*/
public java.lang.String getPk_org_v() {
return this.pk_org_v;
} 

/**
* 属性pk_org_v的Setter方法.属性名：组织多版本
* 创建日期:2017-12-6
* @param newPk_org_v nc.vo.vorg.OrgVersionVO
*/
public void setPk_org_v ( java.lang.String pk_org_v) {
this.pk_org_v=pk_org_v;
} 
 
/**
* 属性 pk_billtypecode的Getter方法.属性名：单据类型编码
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getPk_billtypecode() {
return this.pk_billtypecode;
} 

/**
* 属性pk_billtypecode的Setter方法.属性名：单据类型编码
* 创建日期:2017-12-6
* @param newPk_billtypecode java.lang.String
*/
public void setPk_billtypecode ( java.lang.String pk_billtypecode) {
this.pk_billtypecode=pk_billtypecode;
} 
 
/**
* 属性 pk_billtypeid的Getter方法.属性名：单据类型
*  创建日期:2017-12-6
* @return nc.vo.pub.billtype.BilltypeVO
*/
public java.lang.String getPk_billtypeid() {
return this.pk_billtypeid;
} 

/**
* 属性pk_billtypeid的Setter方法.属性名：单据类型
* 创建日期:2017-12-6
* @param newPk_billtypeid nc.vo.pub.billtype.BilltypeVO
*/
public void setPk_billtypeid ( java.lang.String pk_billtypeid) {
this.pk_billtypeid=pk_billtypeid;
} 
 
/**
* 属性 vbillcode的Getter方法.属性名：单据号
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVbillcode() {
return this.vbillcode;
} 

/**
* 属性vbillcode的Setter方法.属性名：单据号
* 创建日期:2017-12-6
* @param newVbillcode java.lang.String
*/
public void setVbillcode ( java.lang.String vbillcode) {
this.vbillcode=vbillcode;
} 
 
/**
* 属性 contratcno的Getter方法.属性名：合同编号
*  创建日期:2017-12-6
* @return nc.vo.pmpub.project.ProjectHeadVO
*/
public java.lang.String getContratcno() {
return this.contratcno;
} 

/**
* 属性contratcno的Setter方法.属性名：合同编号
* 创建日期:2017-12-6
* @param newContratcno nc.vo.pmpub.project.ProjectHeadVO
*/
public void setContratcno ( java.lang.String contratcno) {
this.contratcno=contratcno;
} 
 
/**
* 属性 serviceno的Getter方法.属性名：服务卡号
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getServiceno() {
return this.serviceno;
} 

/**
* 属性serviceno的Setter方法.属性名：服务卡号
* 创建日期:2017-12-6
* @param newServiceno java.lang.String
*/
public void setServiceno ( java.lang.String serviceno) {
this.serviceno=serviceno;
} 
 
/**
* 属性 subcontractname的Getter方法.属性名：分包合同名称
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getSubcontractname() {
return this.subcontractname;
} 

/**
* 属性subcontractname的Setter方法.属性名：分包合同名称
* 创建日期:2017-12-6
* @param newSubcontractname java.lang.String
*/
public void setSubcontractname ( java.lang.String subcontractname) {
this.subcontractname=subcontractname;
} 
 
/**
* 属性 signdate的Getter方法.属性名：签约日期
*  创建日期:2017-12-6
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getSigndate() {
return this.signdate;
} 

/**
* 属性signdate的Setter方法.属性名：签约日期
* 创建日期:2017-12-6
* @param newSigndate nc.vo.pub.lang.UFDate
*/
public void setSigndate ( UFDate signdate) {
this.signdate=signdate;
} 
 
/**
* 属性 constartdate的Getter方法.属性名：合同开始日期
*  创建日期:2017-12-6
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getConstartdate() {
return this.constartdate;
} 

/**
* 属性constartdate的Setter方法.属性名：合同开始日期
* 创建日期:2017-12-6
* @param newConstartdate nc.vo.pub.lang.UFDate
*/
public void setConstartdate ( UFDate constartdate) {
this.constartdate=constartdate;
} 
 
/**
* 属性 conenddate的Getter方法.属性名：合同结束日期
*  创建日期:2017-12-6
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getConenddate() {
return this.conenddate;
} 

/**
* 属性conenddate的Setter方法.属性名：合同结束日期
* 创建日期:2017-12-6
* @param newConenddate nc.vo.pub.lang.UFDate
*/
public void setConenddate ( UFDate conenddate) {
this.conenddate=conenddate;
} 
 
/**
* 属性 srcconstartdate的Getter方法.属性名：原始合同开始日期
*  创建日期:2017-12-6
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getSrcconstartdate() {
return this.srcconstartdate;
} 

/**
* 属性srcconstartdate的Setter方法.属性名：原始合同开始日期
* 创建日期:2017-12-6
* @param newSrcconstartdate nc.vo.pub.lang.UFDate
*/
public void setSrcconstartdate ( UFDate srcconstartdate) {
this.srcconstartdate=srcconstartdate;
} 
 
/**
* 属性 srcconenddate的Getter方法.属性名：原始合同结束日期
*  创建日期:2017-12-6
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getSrcconenddate() {
return this.srcconenddate;
} 

/**
* 属性srcconenddate的Setter方法.属性名：原始合同结束日期
* 创建日期:2017-12-6
* @param newSrcconenddate nc.vo.pub.lang.UFDate
*/
public void setSrcconenddate ( UFDate srcconenddate) {
this.srcconenddate=srcconenddate;
} 
 
/**
* 属性 contotaldays的Getter方法.属性名：合同总天数
*  创建日期:2017-12-6
* @return java.lang.Integer
*/
public java.lang.Integer getContotaldays() {
return this.contotaldays;
} 

/**
* 属性contotaldays的Setter方法.属性名：合同总天数
* 创建日期:2017-12-6
* @param newContotaldays java.lang.Integer
*/
public void setContotaldays ( java.lang.Integer contotaldays) {
this.contotaldays=contotaldays;
} 
 
/**
* 属性 useddays的Getter方法.属性名：已预提天数
*  创建日期:2017-12-6
* @return java.lang.Integer
*/
public java.lang.Integer getUseddays() {
return this.useddays;
} 

/**
* 属性useddays的Setter方法.属性名：已预提天数
* 创建日期:2017-12-6
* @param newUseddays java.lang.Integer
*/
public void setUseddays ( java.lang.Integer useddays) {
this.useddays=useddays;
} 
 
/**
* 属性 activedays的Getter方法.属性名：剩余可预提天数
*  创建日期:2017-12-6
* @return java.lang.Integer
*/
public java.lang.Integer getActivedays() {
return this.activedays;
} 

/**
* 属性activedays的Setter方法.属性名：剩余可预提天数
* 创建日期:2017-12-6
* @param newActivedays java.lang.Integer
*/
public void setActivedays ( java.lang.Integer activedays) {
this.activedays=activedays;
} 
 
/**
* 属性 usedmny的Getter方法.属性名：已预提金额
*  创建日期:2017-12-6
* @return nc.vo.pub.lang.UFDouble
*/
public nc.vo.pub.lang.UFDouble getUsedmny() {
return this.usedmny;
} 

/**
* 属性usedmny的Setter方法.属性名：已预提金额
* 创建日期:2017-12-6
* @param newUsedmny nc.vo.pub.lang.UFDouble
*/
public void setUsedmny ( nc.vo.pub.lang.UFDouble usedmny) {
this.usedmny=usedmny;
} 
 
/**
* 属性 activemny的Getter方法.属性名：剩余可预提金额
*  创建日期:2017-12-6
* @return nc.vo.pub.lang.UFDouble
*/
public nc.vo.pub.lang.UFDouble getActivemny() {
return this.activemny;
} 

/**
* 属性activemny的Setter方法.属性名：剩余可预提金额
* 创建日期:2017-12-6
* @param newActivemny nc.vo.pub.lang.UFDouble
*/
public void setActivemny ( nc.vo.pub.lang.UFDouble activemny) {
this.activemny=activemny;
} 
 
/**
* 属性 pk_teamwork的Getter方法.属性名：班组
*  创建日期:2017-12-6
* @return nc.vo.fee.workteam.WorkteamVO
*/
public java.lang.String getPk_teamwork() {
return this.pk_teamwork;
} 

/**
* 属性pk_teamwork的Setter方法.属性名：班组
* 创建日期:2017-12-6
* @param newPk_teamwork nc.vo.fee.workteam.WorkteamVO
*/
public void setPk_teamwork ( java.lang.String pk_teamwork) {
this.pk_teamwork=pk_teamwork;
} 
 
/**
* 属性 pk_dept的Getter方法.属性名：部门
*  创建日期:2017-12-6
* @return nc.vo.org.DeptVO
*/
public java.lang.String getPk_dept() {
return this.pk_dept;
} 

/**
* 属性pk_dept的Setter方法.属性名：部门
* 创建日期:2017-12-6
* @param newPk_dept nc.vo.org.DeptVO
*/
public void setPk_dept ( java.lang.String pk_dept) {
this.pk_dept=pk_dept;
} 
 
/**
* 属性 pk_psndoc的Getter方法.属性名：人员
*  创建日期:2017-12-6
* @return nc.vo.bd.psn.PsndocVO
*/
public java.lang.String getPk_psndoc() {
return this.pk_psndoc;
} 

/**
* 属性pk_psndoc的Setter方法.属性名：人员
* 创建日期:2017-12-6
* @param newPk_psndoc nc.vo.bd.psn.PsndocVO
*/
public void setPk_psndoc ( java.lang.String pk_psndoc) {
this.pk_psndoc=pk_psndoc;
} 
 
/**
* 属性 contractmny的Getter方法.属性名：合同金额
*  创建日期:2017-12-6
* @return nc.vo.pub.lang.UFDouble
*/
public nc.vo.pub.lang.UFDouble getContractmny() {
return this.contractmny;
} 

/**
* 属性contractmny的Setter方法.属性名：合同金额
* 创建日期:2017-12-6
* @param newContractmny nc.vo.pub.lang.UFDouble
*/
public void setContractmny ( nc.vo.pub.lang.UFDouble contractmny) {
this.contractmny=contractmny;
} 
 
/**
* 属性 overview的Getter方法.属性名：概述
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getOverview() {
return this.overview;
} 

/**
* 属性overview的Setter方法.属性名：概述
* 创建日期:2017-12-6
* @param newOverview java.lang.String
*/
public void setOverview ( java.lang.String overview) {
this.overview=overview;
} 
 
/**
* 属性 dbilldate的Getter方法.属性名：单据日期
*  创建日期:2017-12-6
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getDbilldate() {
return this.dbilldate;
} 

/**
* 属性dbilldate的Setter方法.属性名：单据日期
* 创建日期:2017-12-6
* @param newDbilldate nc.vo.pub.lang.UFDate
*/
public void setDbilldate ( UFDate dbilldate) {
this.dbilldate=dbilldate;
} 
 
/**
* 属性 creator的Getter方法.属性名：创建人
*  创建日期:2017-12-6
* @return nc.vo.sm.UserVO
*/
public java.lang.String getCreator() {
return this.creator;
} 

/**
* 属性creator的Setter方法.属性名：创建人
* 创建日期:2017-12-6
* @param newCreator nc.vo.sm.UserVO
*/
public void setCreator ( java.lang.String creator) {
this.creator=creator;
} 
 
/**
* 属性 creationtime的Getter方法.属性名：创建时间
*  创建日期:2017-12-6
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getCreationtime() {
return this.creationtime;
} 

/**
* 属性creationtime的Setter方法.属性名：创建时间
* 创建日期:2017-12-6
* @param newCreationtime nc.vo.pub.lang.UFDateTime
*/
public void setCreationtime ( UFDateTime creationtime) {
this.creationtime=creationtime;
} 
 
/**
* 属性 modifier的Getter方法.属性名：最后修改人
*  创建日期:2017-12-6
* @return nc.vo.sm.UserVO
*/
public java.lang.String getModifier() {
return this.modifier;
} 

/**
* 属性modifier的Setter方法.属性名：最后修改人
* 创建日期:2017-12-6
* @param newModifier nc.vo.sm.UserVO
*/
public void setModifier ( java.lang.String modifier) {
this.modifier=modifier;
} 
 
/**
* 属性 modifiedtime的Getter方法.属性名：最后修改时间
*  创建日期:2017-12-6
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getModifiedtime() {
return this.modifiedtime;
} 

/**
* 属性modifiedtime的Setter方法.属性名：最后修改时间
* 创建日期:2017-12-6
* @param newModifiedtime nc.vo.pub.lang.UFDateTime
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.modifiedtime=modifiedtime;
} 
 
/**
* 属性 vmemo的Getter方法.属性名：备注
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVmemo() {
return this.vmemo;
} 

/**
* 属性vmemo的Setter方法.属性名：备注
* 创建日期:2017-12-6
* @param newVmemo java.lang.String
*/
public void setVmemo ( java.lang.String vmemo) {
this.vmemo=vmemo;
} 
 
/**
* 属性 vsourcecode的Getter方法.属性名：来源单据号
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVsourcecode() {
return this.vsourcecode;
} 

/**
* 属性vsourcecode的Setter方法.属性名：来源单据号
* 创建日期:2017-12-6
* @param newVsourcecode java.lang.String
*/
public void setVsourcecode ( java.lang.String vsourcecode) {
this.vsourcecode=vsourcecode;
} 
 
/**
* 属性 csourceid的Getter方法.属性名：来源单据
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getCsourceid() {
return this.csourceid;
} 

/**
* 属性csourceid的Setter方法.属性名：来源单据
* 创建日期:2017-12-6
* @param newCsourceid java.lang.String
*/
public void setCsourceid ( java.lang.String csourceid) {
this.csourceid=csourceid;
} 
 
/**
* 属性 csourcetypecode的Getter方法.属性名：来源单据类型
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getCsourcetypecode() {
return this.csourcetypecode;
} 

/**
* 属性csourcetypecode的Setter方法.属性名：来源单据类型
* 创建日期:2017-12-6
* @param newCsourcetypecode java.lang.String
*/
public void setCsourcetypecode ( java.lang.String csourcetypecode) {
this.csourcetypecode=csourcetypecode;
} 
 
/**
* 属性 vdef1的Getter方法.属性名：自定义项1
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef1() {
return this.vdef1;
} 

/**
* 属性vdef1的Setter方法.属性名：自定义项1
* 创建日期:2017-12-6
* @param newVdef1 java.lang.String
*/
public void setVdef1 ( java.lang.String vdef1) {
this.vdef1=vdef1;
} 
 
/**
* 属性 vdef2的Getter方法.属性名：自定义项2
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef2() {
return this.vdef2;
} 

/**
* 属性vdef2的Setter方法.属性名：自定义项2
* 创建日期:2017-12-6
* @param newVdef2 java.lang.String
*/
public void setVdef2 ( java.lang.String vdef2) {
this.vdef2=vdef2;
} 
 
/**
* 属性 vdef3的Getter方法.属性名：自定义项3
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef3() {
return this.vdef3;
} 

/**
* 属性vdef3的Setter方法.属性名：自定义项3
* 创建日期:2017-12-6
* @param newVdef3 java.lang.String
*/
public void setVdef3 ( java.lang.String vdef3) {
this.vdef3=vdef3;
} 
 
/**
* 属性 vdef4的Getter方法.属性名：自定义项4
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef4() {
return this.vdef4;
} 

/**
* 属性vdef4的Setter方法.属性名：自定义项4
* 创建日期:2017-12-6
* @param newVdef4 java.lang.String
*/
public void setVdef4 ( java.lang.String vdef4) {
this.vdef4=vdef4;
} 
 
/**
* 属性 vdef5的Getter方法.属性名：自定义项5
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef5() {
return this.vdef5;
} 

/**
* 属性vdef5的Setter方法.属性名：自定义项5
* 创建日期:2017-12-6
* @param newVdef5 java.lang.String
*/
public void setVdef5 ( java.lang.String vdef5) {
this.vdef5=vdef5;
} 
 
/**
* 属性 vdef6的Getter方法.属性名：自定义项6
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef6() {
return this.vdef6;
} 

/**
* 属性vdef6的Setter方法.属性名：自定义项6
* 创建日期:2017-12-6
* @param newVdef6 java.lang.String
*/
public void setVdef6 ( java.lang.String vdef6) {
this.vdef6=vdef6;
} 
 
/**
* 属性 vdef7的Getter方法.属性名：自定义项7
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef7() {
return this.vdef7;
} 

/**
* 属性vdef7的Setter方法.属性名：自定义项7
* 创建日期:2017-12-6
* @param newVdef7 java.lang.String
*/
public void setVdef7 ( java.lang.String vdef7) {
this.vdef7=vdef7;
} 
 
/**
* 属性 vdef8的Getter方法.属性名：自定义项8
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef8() {
return this.vdef8;
} 

/**
* 属性vdef8的Setter方法.属性名：自定义项8
* 创建日期:2017-12-6
* @param newVdef8 java.lang.String
*/
public void setVdef8 ( java.lang.String vdef8) {
this.vdef8=vdef8;
} 
 
/**
* 属性 vdef9的Getter方法.属性名：自定义项9
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef9() {
return this.vdef9;
} 

/**
* 属性vdef9的Setter方法.属性名：自定义项9
* 创建日期:2017-12-6
* @param newVdef9 java.lang.String
*/
public void setVdef9 ( java.lang.String vdef9) {
this.vdef9=vdef9;
} 
 
/**
* 属性 vdef10的Getter方法.属性名：自定义项10
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef10() {
return this.vdef10;
} 

/**
* 属性vdef10的Setter方法.属性名：自定义项10
* 创建日期:2017-12-6
* @param newVdef10 java.lang.String
*/
public void setVdef10 ( java.lang.String vdef10) {
this.vdef10=vdef10;
} 
 
/**
* 属性 vdef11的Getter方法.属性名：自定义项11
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef11() {
return this.vdef11;
} 

/**
* 属性vdef11的Setter方法.属性名：自定义项11
* 创建日期:2017-12-6
* @param newVdef11 java.lang.String
*/
public void setVdef11 ( java.lang.String vdef11) {
this.vdef11=vdef11;
} 
 
/**
* 属性 vdef12的Getter方法.属性名：自定义项12
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef12() {
return this.vdef12;
} 

/**
* 属性vdef12的Setter方法.属性名：自定义项12
* 创建日期:2017-12-6
* @param newVdef12 java.lang.String
*/
public void setVdef12 ( java.lang.String vdef12) {
this.vdef12=vdef12;
} 
 
/**
* 属性 vdef13的Getter方法.属性名：自定义项13
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef13() {
return this.vdef13;
} 

/**
* 属性vdef13的Setter方法.属性名：自定义项13
* 创建日期:2017-12-6
* @param newVdef13 java.lang.String
*/
public void setVdef13 ( java.lang.String vdef13) {
this.vdef13=vdef13;
} 
 
/**
* 属性 vdef14的Getter方法.属性名：自定义项14
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef14() {
return this.vdef14;
} 

/**
* 属性vdef14的Setter方法.属性名：自定义项14
* 创建日期:2017-12-6
* @param newVdef14 java.lang.String
*/
public void setVdef14 ( java.lang.String vdef14) {
this.vdef14=vdef14;
} 
 
/**
* 属性 vdef15的Getter方法.属性名：自定义项15
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef15() {
return this.vdef15;
} 

/**
* 属性vdef15的Setter方法.属性名：自定义项15
* 创建日期:2017-12-6
* @param newVdef15 java.lang.String
*/
public void setVdef15 ( java.lang.String vdef15) {
this.vdef15=vdef15;
} 
 
/**
* 属性 vdef16的Getter方法.属性名：自定义项16
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef16() {
return this.vdef16;
} 

/**
* 属性vdef16的Setter方法.属性名：自定义项16
* 创建日期:2017-12-6
* @param newVdef16 java.lang.String
*/
public void setVdef16 ( java.lang.String vdef16) {
this.vdef16=vdef16;
} 
 
/**
* 属性 vdef17的Getter方法.属性名：自定义项17
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef17() {
return this.vdef17;
} 

/**
* 属性vdef17的Setter方法.属性名：自定义项17
* 创建日期:2017-12-6
* @param newVdef17 java.lang.String
*/
public void setVdef17 ( java.lang.String vdef17) {
this.vdef17=vdef17;
} 
 
/**
* 属性 vdef18的Getter方法.属性名：自定义项18
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef18() {
return this.vdef18;
} 

/**
* 属性vdef18的Setter方法.属性名：自定义项18
* 创建日期:2017-12-6
* @param newVdef18 java.lang.String
*/
public void setVdef18 ( java.lang.String vdef18) {
this.vdef18=vdef18;
} 
 
/**
* 属性 vdef19的Getter方法.属性名：自定义项19
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef19() {
return this.vdef19;
} 

/**
* 属性vdef19的Setter方法.属性名：自定义项19
* 创建日期:2017-12-6
* @param newVdef19 java.lang.String
*/
public void setVdef19 ( java.lang.String vdef19) {
this.vdef19=vdef19;
} 
 
/**
* 属性 vdef20的Getter方法.属性名：自定义项20
*  创建日期:2017-12-6
* @return java.lang.String
*/
public java.lang.String getVdef20() {
return this.vdef20;
} 

/**
* 属性vdef20的Setter方法.属性名：自定义项20
* 创建日期:2017-12-6
* @param newVdef20 java.lang.String
*/
public void setVdef20 ( java.lang.String vdef20) {
this.vdef20=vdef20;
} 
 
/**
* 属性 生成时间戳的Getter方法.属性名：时间戳
*  创建日期:2017-12-6
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getTs() {
return this.ts;
}
/**
* 属性生成时间戳的Setter方法.属性名：时间戳
* 创建日期:2017-12-6
* @param newts nc.vo.pub.lang.UFDateTime
*/
public void setTs(UFDateTime ts){
this.ts=ts;
} 
     
    @Override
    public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("fee.subagreement");
    }
   }
    