package xu.ye.application;

import java.util.List;

import xu.ye.bean.ContactBean;
import xu.ye.service.T9Service;
import android.content.Intent;

public class MyApplication extends android.app.Application {
//Application和Activity,Service一样是Android框架的一个系统组件
	
	private List<ContactBean> contactBeanList;
	//数据的获取和查找
	public List<ContactBean> getContactBeanList() {
		return contactBeanList;
	}
	public void setContactBeanList(List<ContactBean> contactBeanList) {
		this.contactBeanList = contactBeanList;
	}
//真正的Android程序的入口点
	public void onCreate() {
		
		System.out.println("项目启动");
		
		Intent startService = new Intent(MyApplication.this, T9Service.class);
		startService(startService);
	}
}
