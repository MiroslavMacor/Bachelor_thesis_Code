/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.ics.remsig.common;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author miroslav
 */
public class Spliter extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String uniqueID = UUID.randomUUID().toString();
       	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date = new Date();
        
	String fileName =  dateFormat.format(date) + uniqueID;
        
        
        
        
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain; charset=utf-8");
        X509Certificate[] certChain = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        
        X509Certificate clientCert;
        if (certChain.length > 0) {
			clientCert = certChain[0];
		}
        ServletContext context = getServletContext();
        
        String urlPath = request.getServletPath();
        
        //context.
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String data = buffer.toString();
        
       
        try {
             // sending data to first server and getting response
            URL sslUrl = new URL("https://localhost:8443/RemSig/"); // Python server
            
            //get url and parse it end
            String MethodFromUrl = null; // generateRequesst
            String responsePyth = sendPost(MethodFromUrl, data, sslUrl);
            
            
            try{
                
                    PrintWriter originalPost = new PrintWriter("/home/miroslav/Documents/toDelete/82/input/" + fileName);
                    originalPost.println(data);
                    PrintWriter originalResponce = new PrintWriter("/home/miroslav/Documents/toDelete/82/outputPython/" + fileName);
                    originalResponce.println(responsePyth);
                    
                    URL sslUrlJava = new URL("https://localhost:8443/RemSigJava/"); // Javaserver
                    String responseJava = sendPost(MethodFromUrl, data, sslUrlJava);
                    
                    PrintWriter responseJavaO = new PrintWriter("/home/miroslav/Documents/toDelete/82/outputJava/" + fileName);
                    responseJavaO.println(data);
                    
                    response.getOutputStream().write(responsePyth.getBytes());
            }
            catch(FileNotFoundException e)
            {
                //Logger System.out.println(e.getMessage());
            }
            
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Spliter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    
    
    
    
    
    
    private String sendPost(String methodName,String postData,URL url) throws Exception {
                
		
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		KeyStore keyStore = KeyStore.getInstance("PKCS12");

		String pKeyPassword = "123456";
		//String pKeyFile = "sub1-cert.p12";
                String pKeyFile = "right.p12";
		try (InputStream keyInput = new FileInputStream(pKeyFile)) {
			keyStore.load(keyInput, pKeyPassword.toCharArray());
		}
                
                KeyStore keyStoreTest = KeyStore.getInstance(KeyStore.getDefaultType());
		String keyStoreTestPassword = "123456";
                String keyStoreTestFile = "/home/miroslav/Documents/toDelete/73/client.jks";
		try (InputStream keyInput1 = new FileInputStream(keyStoreTestFile)) {
			keyStoreTest.load(keyInput1, keyStoreTestPassword.toCharArray());
		}
                
                TrustManagerFactory tmf = 
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStoreTest);
		keyManagerFactory.init(keyStore, pKeyPassword.toCharArray());

		SSLContext context = SSLContext.getInstance("TLS");
		context.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
                
		SSLSocketFactory factory = context.getSocketFactory();
		//URL sslUrl = new URL("https://localhost:8443/RemSig/"+methodName);               
		return postSpecification(url, factory, postData);
	}
        
        
        public String postSpecification(URL sslUrl,SSLSocketFactory factory,String postData ) throws IOException
        {
                HttpsURLConnection ssl_con = (HttpsURLConnection) sslUrl.openConnection();

		ssl_con.setSSLSocketFactory(factory);
		ssl_con.setRequestMethod("POST");

		ssl_con.setDoOutput(
				true);

		try (DataOutputStream wr = new DataOutputStream(ssl_con.getOutputStream())) {			
                        wr.writeBytes(postData);
		}
		int responseCode = ssl_con.getResponseCode();

		//System.out.println(				"\nSending 'POST' request to URL : " + sslUrl);		
		//System.out.println("Response Code : " + responseCode);

		StringBuilder response = new StringBuilder();
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(ssl_con.getInputStream()))) {
			String inputLine;
                        
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		System.out.println(response.toString());
            return response.toString();
            
        }
        
        public boolean filesCheck(String firstFileName, String secondFileName)
        {
            //load files
            File one = new File(firstFileName);
            File two = new File(secondFileName);
            
            if(one.compareTo(two) != 0 )return false;
                
                 //create hash and chceck that
            
            
            return true;
        }
    
    
    
}
