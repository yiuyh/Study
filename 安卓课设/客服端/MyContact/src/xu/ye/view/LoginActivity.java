package xu.ye.view;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import xu.ye.R;

public class LoginActivity extends Activity {
	public static String uid = "-1";
	String BaseUrl = "http://10.0.2.2:8080/WebTest/DemoServletJson.do?";

	// 输入账户编辑框
	private EditText accountEt;
	// 输入密码编辑框
	private EditText pwdEt;
	// 登录按钮
	private Button login;
	// 用户账户和密码
	private String name, pwd;
	private SharedPreferences pref;
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			Message msg = new Message();
			Bundle data = new Bundle();// 存放数据
			String ret = "";
			try {
				// Android 模拟器访问 pc，pc的默认地址 10.0.2.2 192.168.43.149:8080
				String BaseUrl = "http://10.0.2.2:8080//WebTest/DemoServletJson.do?";
				// String BaseUrl="http://192.168.0.101:8080/MyService/MyServlet.do?";
				String tt = BaseUrl + "action=getall&username=" + name + "&password=" + pwd;
				ret = doGet(tt, "UTF-8");// string

			} catch (Exception e) {
				e.printStackTrace();
			}

			String json = ret;
			int rett = -1;

			try {
				JSONObject job = null;
				job = new JSONObject(json);
				rett = job.getInt("ret");
				uid = job.getString("uid");
				Data mapp = new Data();
				mapp.setA(uid);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (rett == 1) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "账号密码错误，请重新输入", Toast.LENGTH_SHORT).show();
					}
				});
			} else {
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();

						Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("one", name);
						bundle.putString("two", pwd);
						intent.putExtras(bundle);
						intent.putExtra("uid", uid);
						startActivity(intent);
						LoginActivity.this.finish();
					}
				});
			}
		}
	};

//	public void initContent(String json) {
//	    List<View> list = new ArrayList<View>();//读取json对象
//		
//		if (json != null && json.length() > 0) {
//			JSONObject jsonObject = null;
//			try {
//				jsonObject = new JSONObject(json);  //把传进来的字符串转换为json对象。
//				System.out.println("json"+json);
//				uid = jsonObject.getString("ret");   //获得用户的id
//				System.out.println("uid "+uid);
//				
//				int user_id = Integer.valueOf(uid);
//				System.out.println("========="+user_id);
//				System.out.println("========="+user);
//				if(user_id >= 0) {    //如果用户的id大于等于零，则代表登录成功。
//					Toast toast =Toast.makeText(this,"登录成功",Toast.LENGTH_SHORT);
//					toast.show();//显示消息
//					Intent intent = new Intent(LoginActivity.this,
//							HomeTabHostActivity.class);
//					intent.putExtra("user",Integer.toString(user_id));//把用户id、用户账号、用户密码放到intent中传到下一个界面。
//					intent.putExtra("user_account", user);
//					intent.putExtra("user_password",user_password.getText().toString());
//					startActivity(intent);
//				}
//				else
//				{
//					Toast toast =Toast.makeText(this,"账号或密码错误",Toast.LENGTH_SHORT);
//					toast.show();//显示消息
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//  }

	Handler handler = new Handler();

	public static String doGet(String url, String encode) throws Exception {
		StringBuilder sb = new StringBuilder();// 存字符串
		HttpClient client = new DefaultHttpClient();// 创建httpclient对象；创建请求方法的实例（httpget/httpost），并制定请求url；
		HttpResponse response = client.execute(new HttpGet(url));// 调用httpclient的execute
		HttpEntity entity = response.getEntity();// 调用httpresponse的getEntry（）方法，包装了服务器的相应内容，相当于http报文的实体
		if (entity != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), encode), 8192);// InputStreamReader把字节流转为字符流,bufferedReader带缓冲的字符输入流

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
		}
		return sb.toString();// 返回string
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		accountEt = (EditText) findViewById(R.id.account);
		pwdEt = (EditText) findViewById(R.id.password);
		Button foundtype = (Button) findViewById(R.id.login);
		// foundtype.setOnClickListener(new ButtonListener());
		foundtype.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				name = accountEt.getText().toString();
				pwd = pwdEt.getText().toString();
				SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);

				// 获得sp的编辑器
				SharedPreferences.Editor ed = sp.edit();

				// 以键值对的显示将用户名和密码保存到sp中
				ed.putString("name", name);
				ed.putString("pwd", pwd);
				// 提交用户名和密码
				ed.commit();
				Intent intent = new Intent();
				new Thread(runnable).start();
			}
		});

	}
}
