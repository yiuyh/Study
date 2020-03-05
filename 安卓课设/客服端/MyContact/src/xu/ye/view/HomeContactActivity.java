package xu.ye.view;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.Groups;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import xu.ye.R;
import xu.ye.bean.ContactBean;
import xu.ye.bean.GroupBean;
import xu.ye.uitl.BaseIntentUtil;
import xu.ye.view.adapter.ContactHomeAdapter;
import xu.ye.view.adapter.MenuListAdapter;
import xu.ye.view.other.SizeCallBackForMenu;
import xu.ye.view.sms.MessageBoxList;
import xu.ye.view.ui.MenuHorizontalScrollView;
import xu.ye.view.ui.QuickAlphabeticBar;

@SuppressLint("NewApi")
public class HomeContactActivity extends Activity {

	private MenuHorizontalScrollView scrollView;
	private ListView menuList;
	private View acbuwaPage;
	private Button menuBtn;
	private MenuListAdapter menuListAdapter;
	private View[] children;
	private LayoutInflater inflater;


	private ContactHomeAdapter adapter;
    private ArrayList<HashMap<String, Object>> datalist;
	private ListView personList;
	private List<ContactBean> list;
	private AsyncQueryHandler asyncQuery;
	private QuickAlphabeticBar alpha;
	private Button addContactBtn;
	private static int i=0;

	private Map<Integer, ContactBean> contactIdMap = null;
	

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		inflater = LayoutInflater.from(this);
		setContentView(inflater.inflate(R.layout.menu_scroll_view, null));



		scrollView = (MenuHorizontalScrollView)findViewById(R.id.mScrollView);
		menuListAdapter = new MenuListAdapter(this, queryGroup());
		menuList = (ListView)findViewById(R.id.menuList);
		menuList.setAdapter(menuListAdapter);


		acbuwaPage = inflater.inflate(R.layout.home_contact_page, null);
		menuBtn = (Button)this.acbuwaPage.findViewById(R.id.btn_popMenu);

		personList = (ListView)this.acbuwaPage.findViewById(R.id.acbuwa_list);

		alpha = (QuickAlphabeticBar)this.acbuwaPage.findViewById(R.id.fast_scroller);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		init();

		menuBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showPopMenu(v);
			}
		});




		View leftView = new View(this);
		leftView.setBackgroundColor(Color.TRANSPARENT);
		children = new View[]{leftView, acbuwaPage};
		scrollView.initViews(children, new SizeCallBackForMenu(this.menuBtn), this.menuList);
		scrollView.setMenuBtn(this.menuBtn);

		addContactBtn = (Button) findViewById(R.id.addContactBtn);
		addContactBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
				Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
				startActivityForResult(intent, 1008);
			}
		});

		startReceiver1();
	}

	private void init(){
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
		String[] projection = { 
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.DATA1,
				"sort_key",
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
		}; // 查询的列
		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(MenuHorizontalScrollView.menuOut == true)
				this.scrollView.clickMenuBtn(HomeContactActivity.this);
			else
				this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}	
















	/**
	 * 数据库异步查询类AsyncQueryHandler
	 * 
	 * @author administrator
	 * 
	 */
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		/**
		 * 查询结束的回调函数
		 */
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				
				contactIdMap = new HashMap<Integer, ContactBean>();
				
				list = new ArrayList<ContactBean>();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					int contactId = cursor.getInt(4);
					Long photoId = cursor.getLong(5);
					String lookUpKey = cursor.getString(6);

					if (contactIdMap.containsKey(contactId)) {
						
					}else{
						
						ContactBean cb = new ContactBean();
						cb.setDisplayName(name);
//					if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
//						cb.setPhoneNum(number.substring(3));
//					} else {
						cb.setPhoneNum(number);
//					}
						cb.setSortKey(sortKey);
						cb.setContactId(contactId);
						cb.setPhotoId(photoId);
						cb.setLookUpKey(lookUpKey);
						list.add(cb);
						
						contactIdMap.put(contactId, cb);
						
					}
				}
				if (list.size() > 0) {
					setAdapter(list);
				}
			}
		}

	}


	private void setAdapter(List<ContactBean> list) {
		adapter = new ContactHomeAdapter(this, list, alpha);
		personList.setAdapter(adapter);
		alpha.init(HomeContactActivity.this);
		alpha.setListView(personList);
		alpha.setHight(alpha.getHeight());
		alpha.setVisibility(View.VISIBLE);
		personList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ContactBean cb = (ContactBean) adapter.getItem(position);
				showContactDialog(lianxiren1, cb, position);
			}
		});
	}


	private String[] lianxiren1 = new String[] { "拨打电话", "发送短信", "查看详细","移动分组","移出群组","删除" };

	//群组联系人弹出页
	private void showContactDialog(final String[] arg ,final ContactBean cb, final int position){
		new AlertDialog.Builder(this).setTitle(cb.getDisplayName()).setItems(arg,
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){

				Uri uri = null;

				switch(which){

				case 0://打电话
					String toPhone = cb.getPhoneNum();
					uri = Uri.parse("tel:" + toPhone);
					Intent it = new Intent(Intent.ACTION_CALL, uri);
					startActivity(it);
					break;

				case 1://发短息

					String threadId = getSMSThreadId(cb.getPhoneNum());
					
					Map<String, String> map = new HashMap<String, String>();
					map.put("phoneNumber", cb.getPhoneNum());
					map.put("threadId", threadId);
					BaseIntentUtil.intentSysDefault(HomeContactActivity.this, MessageBoxList.class, map);
					break;

				case 2:// 查看详细       修改联系人资料

					uri = ContactsContract.Contacts.CONTENT_URI;
					Uri personUri = ContentUris.withAppendedId(uri, cb.getContactId());
					Intent intent2 = new Intent();
					intent2.setAction(Intent.ACTION_VIEW);
					intent2.setData(personUri);
					startActivity(intent2);
					break;

				case 3:// 移动分组

					//					Intent intent3 = null;
					//					intent3 = new Intent();
					//					intent3.setClass(ContactHome.this, GroupChoose.class);
					//					intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
					//					intent3.putExtra("联系人", contactsID);
					//					Log.e("contactsID", "contactsID---"+contactsID);
					//					ContactHome.this.startActivity(intent3);
					break;

				case 4:// 移出群组

					//					moveOutGroup(getRaw_contact_id(contactsID),Integer.parseInt(qzID));
					break;

				case 5:// 删除

					showDelete(cb.getContactId(), position);
					break;
				}
			}
		}).show();
	}

	// 删除联系人方法
	private void showDelete(final int contactsID, final int position) {
		new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("是否删除此联系人")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//源码删除
				Uri deleteUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactsID);
				Uri lookupUri = ContactsContract.Contacts.getLookupUri(HomeContactActivity.this.getContentResolver(), deleteUri);
				if(lookupUri != Uri.EMPTY){
					HomeContactActivity.this.getContentResolver().delete(deleteUri, null, null);
				}
				adapter.remove(position);
				adapter.notifyDataSetChanged();
				Toast.makeText(HomeContactActivity.this, "该联系人已经被删除.", Toast.LENGTH_SHORT).show();
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		}).show();
	}




	/**
	 * 
	 *查询所有群组
	 *返回值List<ContactGroup>
	 */
	public List<GroupBean> queryGroup(){

		List<GroupBean> list=new ArrayList<GroupBean>();

		GroupBean cg_all=new GroupBean();
		cg_all.setId(0);
		cg_all.setName("全部");
		list.add(cg_all);

		Cursor cur = getContentResolver().query(Groups.CONTENT_URI, null, null, null, null); 
		for (cur.moveToFirst();!(cur.isAfterLast());cur.moveToNext()) {
			if(null!=cur.getString(cur.getColumnIndex(Groups.TITLE))&&(!"".equals(cur.getString(cur.getColumnIndex(Groups.TITLE))))){
				GroupBean cg=new GroupBean();
				cg.setId(cur.getInt(cur.getColumnIndex(Groups._ID)));
				cg.setName(cur.getString(cur.getColumnIndex(Groups.TITLE)));
				list.add(cg);
			}
		}   
		cur.close();
		return list;
	}









	private void queryGroupMember(GroupBean gb){

		String[] RAW_PROJECTION = new String[]{ContactsContract.Data.RAW_CONTACT_ID};  

		Cursor cur = getContentResolver().query(ContactsContract.Data.CONTENT_URI,RAW_PROJECTION,  
				ContactsContract.Data.MIMETYPE+" = '"+GroupMembership.CONTENT_ITEM_TYPE  
				+"' AND "+ContactsContract.Data.DATA1+"="+ gb.getId(),     
				null,  
				"data1 asc"); 

		StringBuilder inSelectionBff = new StringBuilder().append(ContactsContract.RawContacts._ID + " IN ( 0");
		while(cur.moveToNext()){
			inSelectionBff.append(',').append(cur.getLong(0));
		}
		cur.close();	
		inSelectionBff.append(')');

		Cursor contactIdCursor =  getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,  
				new String[] { ContactsContract.RawContacts.CONTACT_ID }, inSelectionBff.toString(), null, ContactsContract.Contacts.DISPLAY_NAME+"  COLLATE LOCALIZED asc "); 
		Map<Integer,Integer> map=new HashMap<Integer,Integer>();  
		while (contactIdCursor.moveToNext()) {  
			map.put(contactIdCursor.getInt(0), 1);  
		}  
		contactIdCursor.close(); 

		Set<Integer> set = map.keySet();
		Iterator<Integer> iter = set.iterator();
		List<ContactBean> list=new ArrayList<ContactBean>();
		while(iter.hasNext()){
			Integer key = iter.next();
			list.add(queryMemberOfGroup(key));
		}
		setAdapter(list);
	}

	private ContactBean queryMemberOfGroup(int id){

		ContactBean cb = null;

		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
		String[] projection = { 
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.DATA1,
				"sort_key",
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
		}; // 查询的列
		Cursor cursor = getContentResolver().query(uri, projection, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			list = new ArrayList<ContactBean>();
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				String name = cursor.getString(1);
				String number = cursor.getString(2);
				String sortKey = cursor.getString(3);
				int contactId = cursor.getInt(4);
				Long photoId = cursor.getLong(5);
				String lookUpKey = cursor.getString(6);

				cb = new ContactBean();
				cb.setDisplayName(name);
//				if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
//					cb.setPhoneNum(number.substring(3));
//				} else {
					cb.setPhoneNum(number);
//				}
				cb.setSortKey(sortKey);
				cb.setContactId(contactId);
				cb.setPhotoId(photoId);
				cb.setLookUpKey(lookUpKey);
			}
		}
		cursor.close();
		return cb;
	}


	/**
	 * 数据库异步查询类AsyncQueryHandler
	 * 
	 * @author administrator
	 * 
	 */
	//	private class GroupQueryHandler extends AsyncQueryHandler {
	//
	//		public GroupQueryHandler(ContentResolver cr) {
	//			super(cr);
	//		}
	//
	//		/**
	//		 * 查询结束的回调函数
	//		 */
	//		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
	//			if (cursor != null && cursor.getCount() > 0) {
	//				list = new ArrayList<ContactBean>();
	//				cursor.moveToFirst();
	//				for (int i = 0; i < cursor.getCount(); i++) {
	//					ContentValues cv = new ContentValues();
	//					cursor.moveToPosition(i);
	//					String name = cursor.getString(1);
	//					String number = cursor.getString(2);
	//					String sortKey = cursor.getString(3);
	//					int contactId = cursor.getInt(4);
	//					Long photoId = cursor.getLong(5);
	//					String lookUpKey = cursor.getString(6);
	//
	//					ContactBean cb = new ContactBean();
	//					cb.setDisplayName(name);
	//					if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
	//						cb.setPhoneNum(number.substring(3));
	//					} else {
	//						cb.setPhoneNum(number);
	//					}
	//					cb.setSortKey(sortKey);
	//					cb.setContactId(contactId);
	//					cb.setPhotoId(photoId);
	//					cb.setLookUpKey(lookUpKey);
	//					list.add(cb);
	//				}
	//				if (list.size() > 0) {
	//					setAdapter(list);
	//				}
	//			}
	//		}
	//
	//	}

	public static String[] SMS_COLUMNS = new String[]{  
		"thread_id"
	};
	private String getSMSThreadId(String adddress){
		Cursor cursor = null;  
		ContentResolver contentResolver = getContentResolver();  
		cursor = contentResolver.query(Uri.parse("content://sms"), SMS_COLUMNS, " address like '%" + adddress + "%' ", null, null);  
		String threadId = "";
		if (cursor == null || cursor.getCount() > 0){
			cursor.moveToFirst();
			threadId = cursor.getString(0);
			cursor.close();
			return threadId;
		}else{
			cursor.close();
			return threadId;
		}
	}







	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(1008 == requestCode){
			init();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onDestroy() {
		super.onDestroy();
		stopReceiver1();
	}

	private String ACTION1 = "SET_DEFAULT_SIG";
	private HomeContactActivity.BaseReceiver1 receiver1 = null;
	/**
	 * 打开接收器
	 */
	private void startReceiver1() {
		if(null==receiver1){
			IntentFilter localIntentFilter = new IntentFilter(ACTION1);
			receiver1 = new HomeContactActivity.BaseReceiver1();
			this.registerReceiver(receiver1, localIntentFilter);
		}
	}
	/**
	 * 关闭接收器
	 */
	private void stopReceiver1() {
		if (null != receiver1)
			unregisterReceiver(receiver1);
	}
	public class BaseReceiver1 extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION1)) {

				String str_bean = intent.getStringExtra("groupbean");
				Gson gson = new Gson();
				GroupBean gb = gson.fromJson(str_bean, GroupBean.class);
				if(gb.getId() == 0){

					init();
				}else{

					queryGroupMember(gb);
				}
			}
		}
	}
	 /*向服务端发信息和接收来自服务端的信息并解析*/
	 Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				String json = data.getString("value");
				if (json != null && json.length() > 0&&json.equals("1"))
					{
					Toast.makeText(HomeContactActivity.this,  "备份成功",
							Toast.LENGTH_SHORT).show();
			}
				else if(json.equals("0"))
				{
					Toast.makeText(HomeContactActivity.this, "备份失败", //备份时成功就向客户端发送成功的信息，失败则发送失败的消息
							Toast.LENGTH_SHORT).show();
				}
				else{
				if (json != null && json.length() > 0) {
					JSONArray jsonArray = null;
					try {
						jsonArray = new JSONArray(json);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							// 初始化map数组对象
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("m1", jsonObject.getString("姓名"));
							map.put("m2", jsonObject.getString("电话"));  //解析，获取姓名，电话
							datalist.add(map);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					 if(jsonArray.length()!=0)
						{
							for (int i = 0 ; i<jsonArray.length(); i++){
								writeContact((String) datalist.get(i).get("m1"),(String) datalist.get(i).get("m2"));//进行信息的写入
					}
							   
					 }
		
				}
				
				}
			}
		};
		
		/*添加电话，姓名*/
		 private void writeContact(String name, String phone) {
		        //先查询要添加的号码是否已存在通讯录中, 不存在则添加. 存在则提示用户
		        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phone);
		        ContentResolver resolver = getContentResolver();
		        //从raw_contact表中返回display_name
		        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data.DISPLAY_NAME}, null, null, null);
		        if (cursor == null) {
		            return;
		        }

		        if (cursor.moveToFirst()) {
		            Log.i("nn", "name=" + cursor.getString(0));    
		        } else {
		            uri = Uri.parse("content://com.android.contacts/raw_contacts");
		            ContentValues values = new ContentValues();
		            long contact_id = ContentUris.parseId(resolver.insert(uri, values));
		            //插入data表
		            uri = Uri.parse("content://com.android.contacts/data");
		            //add Name
		            values.put("raw_contact_id", contact_id);
		            values.put(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/name");
		            values.put("data2", name);
		            values.put("data1", name);
		            resolver.insert(uri, values);
		            values.clear();

		            //add Phone
		            values.put("raw_contact_id", contact_id);
		            values.put(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
		            //手机
		            values.put("data2", "2");
		            values.put("data1", phone);
		            resolver.insert(uri, values);
		            values.clear();
		        }
		        cursor.close();
		    }
		 

		 Runnable runnable = new Runnable() {
				@Override
				public void run() {
					Message msg = new Message();
					Bundle data = new Bundle();
					String ret = "";
					try {
						
						// Android 模拟器访问 pc，pc的默认地址 10.0.2.2   192.168.43.149
						String BaseUrl = "http://10.0.2.2:8080/WebTest/DemoServletJson.do?";
						 Data mApp1= new Data();
			             String uid  = mApp1.getA();
						if(i==1)
						 {Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
					        if (cursor == null) {
					            return;
					        }
					        while (cursor.moveToNext()) {
					        	ContactBean p = new ContactBean();
					        	p.setDisplayName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
					           p.setPhoneNum( cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)));
					           p.setContactId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID))));
							   ret=doGet(BaseUrl + "action=backup"+"&name="+p.getDisplayName()+"&phone="+p.getPhoneNum()+"&uid="+uid, "UTF-8");
					   }
						 }
						if(i==2)
						{
							ret=doGet(BaseUrl + "action=restore"+"&uid="+uid, "UTF-8");
						}
					}
					 catch (Exception e) {
							e.printStackTrace();
						}
						data.putString("value", ret);
						msg.setData(data);
						handler.sendMessage(msg);
					}
			};
			
			
			public static String doGet(String url, String encode) throws Exception {
				StringBuilder sb = new StringBuilder();
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(new HttpGet(url));
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(
							entity.getContent(), encode), 8192);

					String line = null;
					while ((line = reader.readLine()) != null&&i==2) {
						sb.append(line+"\n");
					}
					if(i==1)
						sb.append(line);
					reader.close();
				}
				return sb.toString();
			}
			
			public void showPopMenu(View view){
		        PopupMenu menu = new PopupMenu(this,view);
		        menu.getMenuInflater().inflate(R.menu.main_menu,menu.getMenu());
		        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
		            @Override
		            public boolean onMenuItemClick(MenuItem item) {
		                switch (item.getItemId()){
		                    case R.id.backup:
		                    	i=1;
		                    	new Thread(runnable).start();   //按下备份按钮，转向相应操作
		                    	break;
		                        

		                    case R.id.restore:
		                    	i=2;
		                    	new Thread(runnable).start();   //按下恢复按钮，转向相应操作
		                    	break;

		                }
		                return true;
		            }
		        });
		        menu.setOnDismissListener(new PopupMenu.OnDismissListener() {
		            @Override
		            public void onDismiss(PopupMenu menu) {
		              
		            }
		        });
		        menu.show();
		    }
		 
}
