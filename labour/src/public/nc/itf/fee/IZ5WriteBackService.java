package nc.itf.fee;

import nc.vo.pub.BusinessException;

/**
 * 其他收合同回写服务
 * 
 * @author shidalin
 * 
 */
public interface IZ5WriteBackService {

	/**
	 * 人工分包协议回写服务
	 * 
	 * @throws BusinessException
	 */
	public void writeBackForFE01(IZ5WriteBackParaForFE01[] wbVOs)
			throws BusinessException;
}
