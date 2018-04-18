package nc.impl.fee;

import nc.itf.fee.IZ5WriteBackParaForFE01;
import nc.itf.fee.RewriteRaraForZ5;

/**
 * 其他收合同回写参数
 * 
 * @author shidalin
 * 
 */
public class Z5WriteBackParaForFE01Impl implements IZ5WriteBackParaForFE01 {

	private RewriteRaraForZ5 rwPara;

	public Z5WriteBackParaForFE01Impl(RewriteRaraForZ5 rwPara) {
		this.rwPara = rwPara;
	}

	@Override
	public String getBID() {
		// TODO Auto-generated method stub
		return this.rwPara.getCsrcbid();
	}

	@Override
	public String getHID() {
		// TODO Auto-generated method stub
		return this.rwPara.getCsrcid();
	}

	@Override
	public String getProjectStatus() {
		// TODO Auto-generated method stub
		return this.rwPara.getProjectStatus();
	}

}
