package nc.itf.fee;

import nc.vo.pub.BusinessException;

/**
 * �����պ�ͬ��д����
 * 
 * @author shidalin
 * 
 */
public interface IZ5WriteBackService {

	/**
	 * �˹��ְ�Э���д����
	 * 
	 * @throws BusinessException
	 */
	public void writeBackForFE01(IZ5WriteBackParaForFE01[] wbVOs)
			throws BusinessException;
}
