package cn.edu.gzu.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class DemoServletJson extends HttpServlet {

	private Connection conn = null; // 数据库连接对象
	private String driverName = null; // 数据库驱动器
	private String url = null; // 数据库地址URL
	private String Message = "";
	private String i="9";

	public DemoServletJson() {
		super();
	}

	public void destroy() {
        super.destroy();  
        try {  
        	conn.close();  
        }catch(Exception e) {  
            System.out.println("关闭数据库错误："+e.getMessage());  
        }  
	}

	public void init() throws ServletException {
        //全局contont里面的设置
        driverName=getServletContext().getInitParameter("driverName");  
        url=getServletContext().getInitParameter("url"); 
          
        try {  
            Class.forName(driverName);  
            conn=DriverManager.getConnection(url);  
        } catch(Exception e) {  
            System.out.println("数据库连接错误："+e.getMessage());  
        }  
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map map=request.getParameterMap();
		String action = request.getParameter("action");
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject jsonObj = new JSONObject(); 
		// 判断登录账号为空，则自动跳转到登录
		try {

			// 预处理模式
			String Sq1=null;
			String Sq2=null;
			PreparedStatement pst=null;
			PreparedStatement pst2=null;
			ResultSet rs=null;
			if (action.equals("backup")) {
				String name1 =new String(request.getParameter("name").getBytes("iso-8859-1"),"utf-8");
				String phone1 =new String(request.getParameter("phone").getBytes("iso-8859-1"),"utf-8");
				String uid1=new String(request.getParameter("uid").getBytes("iso-8859-1"),"utf-8");
				Sq1 = "insert into contact(姓名,电话,UID)values(?,?,?)";
				Sq2 = "select * from contact where 姓名 = ? and 电话= ? and UID= ?";
				pst = conn.prepareStatement(Sq1);
				pst2 = conn.prepareStatement(Sq2);  //查询是否有相同的姓名，电话，有则不添加
				pst2.setString(1, name1);
				pst2.setString(2,phone1);
				pst2.setString(3,uid1);
				rs=pst2.executeQuery();
				boolean HasNextData=rs.next();
				if(HasNextData==false)
				{
					pst.setString(1,name1);
				    pst.setString(2,phone1);	
				    pst.setString(3,uid1);
			        int i= pst.executeUpdate();
				    out.print(""+i);
				}
				else out.print(""+1);
				return;
			}
			if (action.equals("restore")) {
				String uid2=new String(request.getParameter("uid").getBytes("iso-8859-1"),"utf-8");
				String Sq3 = "select * from contact where UID=?";
				pst = conn.prepareStatement(Sq3);
				pst.setString(1,uid2);
				rs = pst.executeQuery();
				out.print(resultSetToJson(rs));
				rs.close();
				return;
			}
		
			if (action.equals("search")) {
				String uid3=new String(request.getParameter("uid").getBytes("iso-8859-1"),"utf-8");
				Sq1= "select * from contact where UID=?";
				pst = conn.prepareStatement(Sq1);
				pst.setString(1, uid3);
				rs = pst.executeQuery();
				out.print(this.resultSetToJson(rs));
				rs.close();
				return;
			}
			if (action.equals("getall")) {
				String username3=new String(request.getParameter("username").getBytes("iso-8859-1"),"utf-8");
				String password3= new String(request.getParameter("password").getBytes("iso-8859-1"),"utf-8");
				Sq1 = "select * from user where username=? and password=?";
				pst = conn.prepareStatement(Sq1);
				pst.setString(1, username3);
				pst.setString(2, password3);
				rs = pst.executeQuery();
				
				if (rs.next()){
					jsonObj.put("ret", 0);
					jsonObj.put("message", "正常登陆");
					jsonObj.put("uid", rs.getString("ID"));
					out.print(jsonObj.toString());

				}else{
					jsonObj.put("ret", 1);
					jsonObj.put("message", "用户名密码错误！");
					out.print(jsonObj.toString());
				}
				rs.close();
				return;
			}
		
		} catch (Exception e) {
			System.out.println("错误：" + e.getMessage());
			jsonObj.put("ret", 2);
			jsonObj.put("message", "系统异常");
			out.print(jsonObj.toString());
			return;
		}
		
	}

	public String resultSetToJson(ResultSet rs) throws SQLException,JSONException  
	{  
	   // json数组  
	   JSONArray array = new JSONArray();  
	    
	   // 获取列数  
	   ResultSetMetaData metaData = rs.getMetaData();  
	   int columnCount = metaData.getColumnCount();  
	    
	   // 遍历ResultSet中的每条数据  
	    while (rs.next()) {  
	        JSONObject jsonObj = new JSONObject();  
	         
	        // 遍历每一列  
	        for (int i = 1; i <= columnCount; i++) {  
	            String columnName =metaData.getColumnLabel(i);  
	            String value = rs.getString(columnName);  
	            jsonObj.put(columnName, value);  
	        }   
	        array.add(jsonObj);   
	    }  
	    
	   return array.toString();  
	}  
}
