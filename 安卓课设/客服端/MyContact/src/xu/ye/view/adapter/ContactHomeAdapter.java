package xu.ye.view.adapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import xu.ye.R;
import xu.ye.bean.ContactBean;
import xu.ye.view.ui.QuickAlphabeticBar;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class ContactHomeAdapter extends BaseAdapter{//数据适配器
	
	private LayoutInflater inflater;//布局装载器对象
	private List<ContactBean> list;//数据源
	private HashMap<String, Integer> alphaIndexer;// 字母索引 
	private String[] sections; // 存储每个章节  
	private Context ctx; // 上下文  
	
	// 通过构造方法将数据源与数据适配器关联起来
    // context:要使用当前的Adapter的界面对象
	public ContactHomeAdapter(Context context, List<ContactBean> list, QuickAlphabeticBar alpha) {
		
		this.ctx = context;
		this.inflater = LayoutInflater.from(context);
		this.list = list; 
		this.alphaIndexer = new HashMap<String, Integer>();
		this.sections = new String[list.size()];

		for (int i =0; i <list.size(); i++) {
			 // 得到字母 
			String name = getAlpha(list.get(i).getSortKey());
			if(!alphaIndexer.containsKey(name)){ 
				alphaIndexer.put(name, i);
			}
		}
		
		Set<String> sectionLetters = alphaIndexer.keySet();
		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
		Collections.sort(sectionList);// 根据首字母进行排序 
		sections = new String[sectionList.size()];
		sectionList.toArray(sections);

		alpha.setAlphaIndexer(alphaIndexer);
	}

	@Override
	//ListView需要显示的数据数量
	public int getCount() {
		return list.size();
	}

	@Override
	//指定的索引对应的数据项
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	//指定的索引对应的数据项ID
	public long getItemId(int position) {
		return position;
	}
	
	public void remove(int position){
		list.remove(position);
	}
	
	@Override
	//返回每一项的显示内容
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		//如果view未被实例化过，缓存池中没有对应的缓存
		if (convertView == null) {
			// 由于我们只需要将XML转化为View，并不涉及到具体的布局，所以第二个参数通常设置为null
			convertView = inflater.inflate(R.layout.contact_home_list_item, null);
			holder = new ViewHolder();
			holder.qcb = (QuickContactBadge) convertView.findViewById(R.id.qcb);
			
			//对holder的属性进行赋值
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView.findViewById(R.id.number);					
			//通过setTag将convertView与holder关联
			convertView.setTag(holder);
		} else {//如果缓存池中有对应的view缓存，则直接通过getTag取出holder
			holder = (ViewHolder) convertView.getTag();
		}
		// 取出cb对象
		ContactBean cb = list.get(position);
		// 设置控件的数据
		String name = cb.getDisplayName();
		String number = cb.getPhoneNum();
		holder.name.setText(name);
		holder.number.setText(number);
		holder.qcb.assignContactUri(Contacts.getLookupUri(cb.getContactId(), cb.getLookUpKey()));
		if(0 == cb.getPhotoId()){
			holder.qcb.setImageResource(R.drawable.touxiang);
		}else{
			Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, cb.getContactId());
			InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(), uri); 
			Bitmap contactPhoto = BitmapFactory.decodeStream(input);
			holder.qcb.setImageBitmap(contactPhoto);
		}
		 // 当前字母  
		String currentStr = getAlpha(cb.getSortKey());
		// 前面的字母
		String previewStr = (position - 1) >= 0 ? getAlpha(list.get(position - 1).getSortKey()) : " ";
	
		if (!previewStr.equals(currentStr)) {
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentStr);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
		return convertView;
	}
	// ViewHolder用于缓存控件，三个属性分别对应item布局文件的三个控件
	private static class ViewHolder {
		QuickContactBadge qcb;
		TextView alpha;
		TextView name;
		TextView number;
	}
	/** 
     * 获取首字母 
     *  
     * @param str 
     * @return 
     */  
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		 // 正则表达式匹配 
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // 将小写字母转换为大写 
		} else {
			return "#";
		}
	}
}
