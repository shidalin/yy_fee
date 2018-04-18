package nc.itf.fee;

import nc.impl.pubapp.bill.rewrite.RewritePara;

/**
 * 其他收合同回写参数,增加项目状态字段
 * 
 * @author shidalin
 * 
 */
public class RewriteRaraForZ5 extends RewritePara {

	public RewriteRaraForZ5() {
		// TODO Auto-generated constructor stub
	}

	private String projectStatus;

	public String getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}

}
