package nc.impl.fee;

import nc.bs.fee.subagreement.ace.rule.Z5WriteBackForFE01;
import nc.itf.fee.IZ5WriteBackParaForFE01;
import nc.itf.fee.IZ5WriteBackService;
import nc.vo.pub.BusinessException;

/**
 * �����պ�ͬ��д����ʵ��
 * 
 * @author shidalin
 * 
 */
public class Z5WriteBackServiceImpl implements IZ5WriteBackService {

	public Z5WriteBackServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void writeBackForFE01(IZ5WriteBackParaForFE01[] wbVOs)
			throws BusinessException {
		//�����պ�ͬ��д�߼�ʵ��
		new Z5WriteBackForFE01().writeBack(wbVOs);

	}

}
