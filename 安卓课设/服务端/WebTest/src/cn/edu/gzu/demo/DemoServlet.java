package cn.edu.gzu.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JOptionPane;

public class DemoServlet extends HttpServlet {
	
	private Connection conn = null; // ���ݿ����Ӷ���
	private String driverName = null; // ���ݿ�������
	private String url = null; // ���ݿ��ַURL
	private String Message = "";



	/**
	 * ���췽��.
	 */
	public DemoServlet() {
		super();
	}

	/**
	 * 
	 * �����������ر����ݿ����Ӷ���
	 * 
	 */  
    public void destroy() {  
        super.destroy();  
        try {  
        	conn.close();  
        }catch(Exception e) {  
            System.out.println("�ر����ݿ����"+e.getMessage());  
        }  
    } 


	/**
	 * 
	 * ��ʼ��������ȡ�����ݿ����Ӷ���
	 * 
	 */   
    public void init(ServletConfig config) throws ServletException  
    {  
        super.init(config);  
        //��Servlet���������
        //driverName=config.getInitParameter("driverName");  
        //url=config.getInitParameter("url");  
        
        //ȫ��contont���������
        driverName=getServletContext().getInitParameter("driverName");  
        url=getServletContext().getInitParameter("url"); 
          
        try {  
            Class.forName(driverName);  
            conn=DriverManager.getConnection(url);  
        } catch(Exception e) {  
            System.out.println("���ݿ����Ӵ���"+e.getMessage());  
        }  
    } 
    
    /**
	 * 
	 * ����Post����ת���� Get��ͳһ����
	 * 
	 */ 
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
		
	}
	
	/**
	 * 
	 * ���� Get����
	 * 
	 */ 
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(); 
		
		// ȡ���û���¼ҳ���ύ������
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		// �жϵ�¼�˺�Ϊ�գ����Զ���ת����¼
		if (username == null || username.trim().length() == 0) {
			Message = "�������û���";  
			session.setAttribute("message", Message);
			response.sendRedirect("index.jsp");
			return;
		}
		// �жϵ�¼����Ϊ��
		if (password == null || password.trim().length() == 0) {
			Message = "����������";  
			session.setAttribute("message", Message);
			response.sendRedirect("index.jsp");
			return;
		}
		// ��ѯ���ݿ����ת����¼������
		try {
			// ��ѯ���ݿ����

			// �򵥣����ǻ��а�ȫ�����ϵ���������������ֶ�ƴ��
			// String Sql = "select * from user where username = '" + username +"'";
			// Statement st = conn.createStatement();
			// ResultSet rs = st.executeQuery(Sql);

			// Ԥ����ģʽ
			String Sql = "select * from user where username = ?";
			PreparedStatement pst = conn.prepareStatement(Sql);
			// �������
			pst.setString(1, username);
			// ִ�в�ѯ
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				if (rs.getString("password").equals(password)) {
					// ��ת��������
					Message = "��¼�ɹ�";  
					session.setAttribute("message", Message);
					session.setAttribute("username", username);
					response.sendRedirect("main.jsp");
				} else {
					Message = "�����";  
					session.setAttribute("message", Message);   
					response.sendRedirect("index.jsp");
				}
			} else {
				Message = "�����ڵ��û���";  
				session.setAttribute("message", Message);    
				response.sendRedirect("index.jsp");
			}

		} catch (Exception e) {
			System.out.println("����" + e.getMessage());
			response.sendRedirect("index.jsp");
		}
	}

}
