/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.ics.remsig.common;

import cz.muni.ics.remsig.impl.TestManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author miroslav
 */
public class SimplePostToFork {
    public static void main(String[] args) throws Exception {

        
        PerformanceTest http = new PerformanceTest();
        SimplePostToFork simplePost = new SimplePostToFork();
        TestManager testManager = new TestManager();

        simplePost.sendPost("generateRequest", http.generateRequest);
    }
    static {
            //for localhost testing only
            javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                            new javax.net.ssl.HostnameVerifier() {

                                    @Override
                                    public boolean verify(String hostname,
                                                    javax.net.ssl.SSLSession sslSession) {
                                            if (hostname.equals("localhost")) {
                                                    return true;
                                            }
                                            return false;
                                    }
                            });
    }

	
    private void sendPost(String methodName,String postData) throws Exception {
                
		
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
//        URL sslUrl = new URL("https://localhost:8443/RemSig/Fork/"+methodName);               
        URL sslUrl = new URL("https://localhost:8443/RemSig/"+methodName);               
//        URL sslUrl = new URL("https://localhost:8443/ForkTesting/Fork/"+methodName);               
        postSpecification(sslUrl, factory, postData);
        // <url-pattern>/NewServlet</url-pattern>
    }
        
        
    public void postSpecification(URL sslUrl,SSLSocketFactory factory,String postData ) throws IOException
    {
            HttpsURLConnection ssl_con = (HttpsURLConnection) sslUrl.openConnection();
            //HttpsURLConnection ssl_con = (HttpsURLConnection) new URL(("https://localhost:8443/RemSig/"+"generateRequest")).openConnection();// sslUrl.openConnection();;

            ssl_con.setSSLSocketFactory(factory);
            ssl_con.setRequestMethod("POST");
            
            ssl_con.setDoOutput(
                            true);
            

            try (DataOutputStream wr = new DataOutputStream(ssl_con.getOutputStream())) {			
                    wr.writeBytes(postData);
            }
            int responseCode = ssl_con.getResponseCode();

            System.out.println("\nSending 'POST' request to URL : " + sslUrl);		
            System.out.println("Response Code : " + responseCode);
            
            /*
            if(responseCode == 500)
            {
                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(ssl_con.getErrorStream()))) {
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                    }
            }
            System.out.println(response.toString());
            
            }
            */

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(ssl_con.getInputStream()))) {
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                    }
            }
            System.out.println("this is Response" + response.toString()+ "huh");


    }
    
}
