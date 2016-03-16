package com.ngc123.emoface.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;


public class HttpUtils {
    final public static String endpoint = "http://ngc123.sinaapp.com";
    public JSONObject[] joarray;
    public static void postLoc(String username,double latitude,double longitude){
        try {
            HashMap<String,Object> dataLoc = new HashMap<String,Object>();
            dataLoc.put("username",URLEncoder.encode(username,"UTF-8"));
            dataLoc.put("latitude",latitude);
            dataLoc.put("longitude",longitude);
            postHttp(dataLoc,"/index.php/Upload/location.html");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * @param data 中的中文要用utf-8储存
     * @param urlStr
     */
    public static String postHttp(HashMap<String,Object> data,String urlStr){
        String result = "none";
        try{
            Log.d("5v","start");
            URL url = new URL(endpoint+urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            // 设置请求的方式  
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间  
            urlConnection.setReadTimeout(5000);  
            urlConnection.setConnectTimeout(5000);  
            // 传递的数据
            String dataStr = "";
            Set set = data.entrySet();
            Iterator itertator = set.iterator();
            while(itertator.hasNext()){
                Map.Entry mapentry = (Map.Entry)itertator.next();
                dataStr += (String)mapentry.getKey()+"="+mapentry.getValue();
                if(itertator.hasNext()){
                   dataStr += "&"; 
                }
            }
            
            urlConnection.setRequestProperty("Connection", "keep-alive");  
            // 设置请求的头  
            urlConnection.setRequestProperty("Content-Type",  
                    "application/x-www-form-urlencoded");  
            // 设置请求的头  
            urlConnection.setRequestProperty("Content-Length",  
                    String.valueOf(dataStr.getBytes().length));  
            // 设置请求的头  
            urlConnection  
                    .setRequestProperty("User-Agent",  
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");  
  
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出  
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入  
                                            //setDoInput的默认值就是true  
            //获取输出流  
            OutputStream os = urlConnection.getOutputStream();  
            os.write(dataStr.getBytes());  
            os.flush();
            Log.d("5v", urlConnection.getResponseCode()+"s");
            
            if (urlConnection.getResponseCode() == 200) {  
                // 获取响应的输入流对象  
                InputStream is = urlConnection.getInputStream();  
                // 创建字节输出流对象  
                ByteArrayOutputStream baos = new ByteArrayOutputStream();  
                // 定义读取的长度  
                int len = 0;  
                // 定义缓冲区  
                byte buffer[] = new byte[1024];  
                // 按照缓冲区的大小，循环读取  
                while ((len = is.read(buffer)) != -1) {  
                    // 根据读取的长度写入到os对象中  
                    baos.write(buffer, 0, len);  
                }  
                // 释放资源  
                is.close();  
                baos.close();  
                // 返回字符串  
                result = new String(baos.toByteArray());

                Log.d("5v",result);
                
                // 通过runOnUiThread方法进行修改主线程的控件内容  
            }
        }catch(Exception e){
            Log.d("5v",e.toString());
        }
        return result;
    }
    
     
    
    public static String readJSONFeed(String URL){
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }else{
                Log.e("JSON","Fail to download file");
            }
        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
