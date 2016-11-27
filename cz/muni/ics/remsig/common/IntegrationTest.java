/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.ics.remsig.common;
import cz.muni.ics.remsig.impl.TestManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
/**
 *
 * @author miroslav
 */
public class IntegrationTest {
    public IntegrationTest()
    {}
    String pathTotestFilesDirectory = "/home/miroslav/Documents/toDelete/80/integrationTest/input/";
    ArrayList<String> allMethods = loadAllMethods();
    TestManager testManager = new TestManager();
    public static void main(String[] args) throws Exception {
        
        TestManager testManager = new TestManager();
        PerformanceTest per = new PerformanceTest();
        IntegrationTest integrationTest = new IntegrationTest();
        
        
        File testFilesDirectory = new File(integrationTest.pathTotestFilesDirectory);
        
        //testing methots with some null parameters
        ArrayList<String> nullFiles =  integrationTest.listFilesForFolder(new File(integrationTest.pathTotestFilesDirectory+"null/"));        
        integrationTest.runTest(nullFiles, true);
        
        //testing with incorrect input parameters
        ArrayList<String> incorrectInputFiles =  integrationTest.listFilesForFolder(new File(integrationTest.pathTotestFilesDirectory+"incorrectData/"));
        integrationTest.runTest(incorrectInputFiles, true);
        
        ArrayList<String> correctData =  integrationTest.listFilesForFolder(new File(integrationTest.pathTotestFilesDirectory+"correctData/"));        
        integrationTest.runTest(nullFiles, false);
        

        for (String file : correctData) {
            String currentMethod = integrationTest.getMethodName(file);
            String currentPostData = testManager.convertToStringFromXmlFile(file);
            
            //System.out.println(currentMethod+" "+ currentPostData);
            if(currentMethod != null){

                URL sslUrl = new URL("https://localhost:8443/RemSig/"+currentMethod);

                HttpsURLConnection con = integrationTest.establishConnection(sslUrl,true);
                //System.out.println("" + currentMethod + file);                        
                String response = integrationTest.post(con, currentPostData);
                PrintWriter out = new PrintWriter(integrationTest.pathTotestFilesDirectory+ "output/" +currentMethod + ".xml");
                //out.println(response);
                out.close();
                if (response == null)
                {
                    System.out.println("nullResponse " + currentMethod +" "+ file +" "+ response);                        
                }
                if(!response.contains("error"))
                {
                    System.out.println("NoError " + currentMethod +" "+ file +" "+ response);                        
                }
                System.out.println(response);
            }
                    
        }
                
	}
    public void runTest(ArrayList<String> inputFolder, boolean isSupposedToReturnError) 
    {
        PrintWriter errorLog = null;
        try {
            errorLog = new PrintWriter(pathTotestFilesDirectory + "../output/errorlog.txt");
            
        for (String file : inputFolder) {
            String currentMethod = getMethodName(file);
            String currentPostData =  testManager.convertToStringFromXmlFile(file);
            
            if(currentMethod != null){

                URL sslUrl = new URL("https://localhost:8443/RemSig/"+currentMethod);

                HttpsURLConnection con = establishConnection(sslUrl,true);                                    
                String response = post(con, currentPostData);
                if (response == null)
                {
                    System.out.println("nullResponse " + currentMethod +" "+ file);
                    errorLog.append("no response on " +currentMethod + "inputDataFromFile"+ file);
                }
                if(!response.contains("error") && isSupposedToReturnError)
                {
                    System.out.println("NoError " + currentMethod +" "+ file +" "+ response);
                    errorLog.append("No error mesage with " +currentMethod + "on inputDataFromFile"+ file);
                }
            }
            
        }
        
        } catch (Exception e) {
            errorLog.append("Error mesage with " +"on inputDataFromFile");
            System.out.println(e.getMessage());
        } finally{
            errorLog.write("MIRO");
            errorLog.close();
        }
        
    }
    
    
     public HttpsURLConnection establishConnection(URL sslUrl, boolean pkcs12Keystore) throws Exception {
                
		
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
                KeyStore keyStore;
                String pKeyPassword;
                String pKeyFile;
                if (pkcs12Keystore) {
                     keyStore = KeyStore.getInstance("PKCS12");
                     pKeyPassword = "123456";
                     pKeyFile = "right.p12";
                }else{
                     keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                     pKeyPassword = "123456";
                     pKeyFile = "/home/miroslav/Documents/toDelete/73/.keystore";
                }
                    
		
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
		
                HttpsURLConnection ssl_con = (HttpsURLConnection) sslUrl.openConnection();

		ssl_con.setSSLSocketFactory(factory);
                return ssl_con;
		//postSpecification(sslUrl, factory, postData);
	}
        
        
        public String post(HttpsURLConnection ssl_con,String postData ) throws IOException
        {
                
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
        public ArrayList<String> listFilesForFolder(final File folder) {
            ArrayList<String> allFiles = new ArrayList<String>();
            
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    listFilesForFolder(fileEntry);
                } else {
                    if (!fileEntry.getAbsoluteFile().toString().endsWith("~")) {
                        allFiles.add(fileEntry.getAbsolutePath());
                        //System.out.println(fileEntry.getAbsoluteFile());
                        
                    }
                    
                }
    }
    return allFiles;
}
        
    public String getMethodName(String input)
    {
        
        for (String method : allMethods) {
            if (input.toLowerCase().contains(method.toLowerCase())) {
                return method;
                
            }
            
        }
     return null;   
    }
    public ArrayList<String> loadAllMethods()
    {
        ArrayList<String> result = new ArrayList<String>();
        String generateRequest = "generateRequest";        
        String importCertificate = "importCertificate";
        String importPKCS12 = "importPKCS12";        
        String listCertificatesWithStatus = "listCertificatesWithStatus";
        String listAllCertificatesWithStatus = "listAllCertificatesWithStatus";
        String exportPKCS12 = "exportPKCS12";
        String checkPassword = "checkPassword";
        String changePassword = "changePassword";
        String changeCertificateStatus = "changeCertificateStatus";
        String uploadPrivateKey = "uploadPrivateKey";
        String stats = "stats";
        String signPKCS7 = "importCertificate";
        String signPdf = "signPdf";
        String sign = "sign";   
        result.add(generateRequest);
        result.add(importCertificate);
        result.add(importPKCS12);
        result.add(listAllCertificatesWithStatus);
        result.add(exportPKCS12);
        result.add(checkPassword);
        result.add(changeCertificateStatus);
        result.add(uploadPrivateKey);
        result.add(importCertificate);
        result.add(listCertificatesWithStatus);
        result.add(changePassword);
        result.add(signPKCS7);
        
        result.add(signPdf);
        result.add(sign);
        result.add(stats);
        result.add(sign);
        
        return result;
        
    }

    
}
