/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * @author huberto
 */
public class HTTPClient {

    private String cookies;
    private HttpClient client = HttpClientBuilder.create().build();
    private final String USER_AGENT = "Mozilla/5.0";
    
    void sendPost(String url, List<NameValuePair> postParams) 
        throws Exception {
        
        //System.out.println(postParams);

	HttpPost post = new HttpPost(url);

	// add header
	post.setHeader("Host", "localhost");
	post.setHeader("User-Agent", USER_AGENT);
	post.setHeader("Accept", 
             "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	post.setHeader("Accept-Language", "en-US,en;q=0.5");
	post.setHeader("Cookie", getCookies());
	post.setHeader("Connection", "keep-alive");
	post.setHeader("Referer", "http://localhost/exehdager-teste/index.php/ci_login");
	post.setHeader("Content-Type", "application/x-www-form-urlencoded");

	post.setEntity(new UrlEncodedFormEntity(postParams));
        
        

	HttpResponse response = client.execute(post);

	int responseCode = response.getStatusLine().getStatusCode();

	//System.out.println("\nSending 'POST' request to URL : " + url);
	//System.out.println("Post parameters : " + postParams);
	//System.out.println("Response Code : " + responseCode);

	BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

	StringBuffer result = new StringBuffer();
	String line = "";
	while ((line = rd.readLine()) != null) {
		result.append(line);
	}

	//System.out.println(result.toString());

  }

  String GetPageContent(String url, List<NameValuePair> postParams) throws Exception {

	HttpPost request = new HttpPost(url);

	request.setHeader("User-Agent", USER_AGENT);
	request.setHeader("Accept",
		"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	request.setHeader("Accept-Language", "en-US,en;q=0.5");
        
        if (postParams != null)
            request.setEntity(new UrlEncodedFormEntity(postParams));

	HttpResponse response = client.execute(request);
	int responseCode = response.getStatusLine().getStatusCode();

	//System.out.println("\nSending 'GET' request to URL : " + url);
	//System.out.println("Response Code : " + responseCode);

	BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

	StringBuffer result = new StringBuffer();
	String line = "";
	while ((line = rd.readLine()) != null) {
		result.append(line);
	}

	// set cookies
	setCookies(response.getFirstHeader("Set-Cookie") == null ? "" : 
                     response.getFirstHeader("Set-Cookie").toString());

	return result.toString();

  }  

  public String getCookies() {
	return cookies;
  }

  public void setCookies(String cookies) {
	this.cookies = cookies;
  }

}

