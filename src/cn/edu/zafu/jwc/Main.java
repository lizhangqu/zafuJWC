package cn.edu.zafu.jwc;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class Main {
	private static final OkHttpClient client = new OkHttpClient();
	private static String year="2012";//入学年份
	private static String majorNumber="0507";//专业代号
	private static int startClassNumber=6;//起始班级
	private static int startStudentNumber=1;//起始学号
	private static int endClassNumber=8;//结束班级
	private static int endStudentNumber=30;//结束学号
	private static String idCard="lizhangqu0504";//身份证号
	private static boolean hasFound=false;
	public static void main(String[] args) {
		client.setCookieHandler(new CookieManager(new PersistentCookieStore(), CookiePolicy.ACCEPT_ALL));
		String number=null;
		int clazz=0;
		int student=0;
		try {
			for (int i = startClassNumber; i <=endClassNumber; i++) {
				for (int j = startStudentNumber; j <=endStudentNumber; j++) {
					clazz=i;
					student=j;
					number=year+majorNumber+String.format("%02d", clazz)+String.format("%02d", student);
					//拼接学号
					boolean flag=login(number,idCard);
					if(flag){
						System.out.println("找到了，您的学号为："+number);
						hasFound=true;
						break;
					}else{
						System.out.println("正在查找:"+number);
					}
				}
				if(hasFound){
					break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	public static boolean login(String username,String password) throws IOException{
		RequestBody formBody = new FormEncodingBuilder()
        .add("__VIEWSTATE", "dDwyODE2NTM0OTg7Oz429KFYILujcPfYvN3IBCq9kLaO+A==")
        .add("__VIEWSTATEGENERATOR", "92719903")
        .add("txtUserName", username)
        .add("TextBox2", password)
        .add("txtSecretCode", "")
        .add("RadioButtonList1", "学生")
        .add("Button1", "")
        .add("lbLanguage", "")
        .add("hidPdrs", "")
        .add("hidsc", "")
        .build();
		Request login = new Request.Builder()
        .url("http://210.33.60.8/default2.aspx")
        .post(formBody)
        .addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36")
        .addHeader("Referer", "http://210.33.60.8/")
        .addHeader("Host", "210.33.60.8")
        .addHeader("Origin", "http://210.33.60.8")
        .build();
		Response execute = client.newCall(login).execute();
		String result=execute.body().string();
		//返回结果。源码中包含退出则登录成功
		boolean isLoginOldStudent= result!=null&&result.contains("退出");
		//新生第一次登录的界面是修改密码的界面
		boolean isLoginNewStudent=result.contains("确认新密码");
		return isLoginNewStudent||isLoginOldStudent;
	}
	
	
}
