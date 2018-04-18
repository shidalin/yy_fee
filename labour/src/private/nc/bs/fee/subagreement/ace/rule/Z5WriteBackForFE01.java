package nc.bs.fee.subagreement.ace.rule;

import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.itf.ct.ar.IArMaintain;
import nc.itf.fee.IZ5WriteBackParaForFE01;
import nc.vo.ct.ar.entity.AggCtArVO;
import nc.vo.ct.ar.entity.CtArVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * �����պ�ͬ��д����
 * 
 * @author shidalin
 * 
 */
public class Z5WriteBackForFE01 {

	public Z5WriteBackForFE01() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * ��д����ʵ��
	 * 
	 * @param wbVOs
	 * @throws BusinessException
	 */
	public void writeBack(IZ5WriteBackParaForFE01[] wbVOs)
			throws BusinessException {
		if (!ArrayUtils.isEmpty(wbVOs)) {
			ArrayList<String> hidList = new ArrayList<String>();
			HashMap<String, String> hashMap = new HashMap<String, String>();
			for (IZ5WriteBackParaForFE01 wbVO : wbVOs) {
				String hid = wbVO.getHID();
				String projectStatus = wbVO.getProjectStatus();
				hashMap.put(hid, projectStatus);
				if (!StringUtils.isEmpty(hid))
					hidList.add(hid);
			}
			AggCtArVO[] aggCtArVOs = NCLocator.getInstance()
					.lookup(IArMaintain.class)
					.queryCtArVoByIds(hidList.toArray(new String[0]));
			if (!ArrayUtils.isEmpty(aggCtArVOs)) {
				// ���º�ͬ״̬-vdef15
				String[] keys = new String[] { "vdef15" };
				ArrayList<CtArVO> arrayList = new ArrayList<CtArVO>();
				for (AggCtArVO viewVO : aggCtArVOs) {
					CtArVO head = viewVO.getParentVO();
					// ������Ŀ״̬
					head.setVdef15(hashMap.get(head.getPk_ct_ar()));
					arrayList.add(head);
				}
				VOUpdate rpUpdate = new VOUpdate();
				rpUpdate.update(arrayList.toArray(new CtArVO[0]), keys);
			}
		}

	}

}
