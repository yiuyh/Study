package xu.ye.view;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import xu.ye.R;

public class UserInfoActivity extends Activity{
	private TextView name;
    private TextView mima;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    Intent intent;
	String uid = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
 
        name = (TextView) findViewById(R.id.u_name);
        mima = (TextView) findViewById(R.id.u_mima);
        btn1 = (Button) findViewById(R.id.xiugai);
        btn2 = (Button) findViewById(R.id.returnback);
        intent = getIntent();
		uid = intent.getStringExtra("uid");

        btn2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(UserInfoActivity.this, LoginActivity.class);//
				UserInfoActivity.this.startActivity(intent);
				
			}});
        btn3=(Button) findViewById(R.id.mycontact);
        btn3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new Thread(runnable).start();
				Intent intent = new Intent();
				intent.setClass(UserInfoActivity.this, HomeTabHostActivity.class);//��ת���µĻ����
				UserInfoActivity.this.startActivity(intent);
				
			}});
        Intent intent1 = getIntent();//获取Intent对象
 
        //取出对key中的值
        String name1 = intent1.getStringExtra("one");
        String mima1 = intent1.getStringExtra("two");
        //设置到对的控件中
        name.setText("用户名为："+ "< "+ name1 + " >");
        mima.setText("密码为 ："+  mima1  + " ");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updata();
            }
        });
    }
 
    private void updata() {
        Intent intent2 = new Intent(this,SetpasswordActivity.class);
        startActivity(intent2);
        finish();
    }
    Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		Bundle data = msg.getData();
    		String json = data.getString("value");
    	}
    };
    	
    Runnable runnable = new Runnable() {
    	@Override
    	public void run() {
    		Message msg = new Message();
			Bundle data = new Bundle();//存放数据
			String ret = "";
			try {
				// Android 模拟器访问 pc，pc的默认地址 10.0.2.2 、、10.0.2.2:8080//192.168.43.149:8080
				String BaseUrl ="http://10.0.2.2:8080/WebTest/DemoServletJson.do?";
				//String BaseUrl="http://192.168.0.101:8080/MyService/MyServlet.do?";
				ret = doGet(BaseUrl + "action=search&uid="+uid, "UTF-8");//string 
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			data.putString("value", ret);//键值对的形式
			msg.setData(data);
		
			handler.sendMessage(msg);//安排一个带数据的message对象到队列等待更新
			
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
    		while ((line = reader.readLine()) != null) {
    			sb.append(line + "\n");
    		}
    		reader.close();
    	}
    	return sb.toString();
    }
 
}
