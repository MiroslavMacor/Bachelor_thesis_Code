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
import java.util.Properties;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
/**
 *
 * @author miroslav
 */
public class IntegrationTest {
    public IntegrationTest(){
    }    
    Properties config = TestManager.prepareConfigFile(TestManager.CONFIG_FILE_TEST);
    final String pathTotestFilesDirectory = config.getProperty("testFilesDirectory");
    final String serverAddress = config.getProperty("serverAddress");
    final String p12KeyFile = config.getProperty("pathToP12Keystore");
    final String p12KeyPassword = config.getProperty("p12Pass");
    final String defaultKeystore = config.getProperty("pathDefaultKeystore");
    final String defaultKeystorePass = config.getProperty("defaultKeystorePass");
    final String trustStore = config.getProperty("pathToJKSTruststore");
    final String trustStorePass = config.getProperty("trustoreJksPass");    
    
    ArrayList<String> allMethods = loadAllMethods();
    TestManager testManager = new TestManager();
    Logger log = TestManager.setUpLogger(IntegrationTest.class);        
    
    public static void main(String[] args) throws Exception {                
        IntegrationTest integrationTest = new IntegrationTest();
        
        integrationTest.executeTest();
    }
    public void executeTest(){
        // testing with null parameters
        ArrayList<String> nullFiles =  listFilesForFolder(new File(pathTotestFilesDirectory+"null/"));        
        runTest(nullFiles, true);
        
        //testing with incorrect input parameters
        ArrayList<String> incorrectInputFiles =  listFilesForFolder(new File(pathTotestFilesDirectory+"incorrectData/"));
        runTest(incorrectInputFiles, true);
        // testing with correct data
        ArrayList<String> correctData =  listFilesForFolder(new File(pathTotestFilesDirectory+"correctData/"));        
        runTest(nullFiles, false);
        
        
    }
    
    public void runTest(ArrayList<String> inputFolder, boolean isSupposedToReturnError) 
    {   
        try {
            for (String file : inputFolder) {
                String currentMethod = getMethodName(file);
                String currentPostData =  testManager.convertToStringFromXmlFile(file);            
                if(currentMethod != null){
                    URL sslUrl = new URL(serverAddress + currentMethod);
                    HttpsURLConnection con = establishConnection(sslUrl,true);                                    
                    String response = post(con, currentPostData);
                    
                    if (response == null)
                    {
                        log.warn("Server did not return any response to request "
                                +currentMethod +" with data from file: "+ file);                        
                    }else if(!response.contains("error") && isSupposedToReturnError)
                    {
                        log.warn("Server did not return error with incorrect" +
                                "input data on method: "+ currentMethod+ " with file"
                                + file + " the response was " + response);                        
                    }else if (response.contains("error")){
                        log.warn("Server did not return error with incorrect" +
                                "input data on method: "+ currentMethod+ " with file"
                                + file + " the response was " + response);
                    }                    
                    else{
                        log.info("Server response: "+ response);
                    }
                }

            }
        
        } catch (Exception e) {
            log.error("Test cause exception" + e.getMessage());            
        }
    }
    
    
     public HttpsURLConnection establishConnection(URL sslUrl, boolean pkcs12Keystore) throws Exception {
                
		
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
                KeyStore keyStore;
                String pKeyPassword;
                String pKeyFile;
                if (pkcs12Keystore) {
                     keyStore = KeyStore.getInstance("PKCS12");
                     pKeyFile = p12KeyFile;
                     pKeyPassword = p12KeyPassword;                     
                }else{
                     keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                     pKeyFile = defaultKeystore;
                     pKeyPassword = defaultKeystorePass;
                }
                    
		
		try (InputStream keyInput = new FileInputStream(pKeyFile)) {
			keyStore.load(keyInput, pKeyPassword.toCharArray());
		}
                
                KeyStore keyStoreTest = KeyStore.getInstance(KeyStore.getDefaultType());		
                String keyStoreTestFile = trustStore;
                String keyStoreTestPassword = trustStorePass;
		try (InputStream keyInput1 = new FileInputStream(keyStoreTestFile)) {
			keyStoreTest.load(keyInput1, keyStoreTestPassword.toCharArray());
		}
                
                TrustManagerFactory tmf = 
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStoreTest);
		keyManagerFactory.init(keyStore, pKeyPassword.toCharArray());

		SSLContext context = SSLContext.getInstance("TLS");
		context.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(),
                        new SecureRandom());
                
		SSLSocketFactory factory = context.getSocketFactory();
		
                HttpsURLConnection ssl_con = (HttpsURLConnection) sslUrl.openConnection();

		ssl_con.setSSLSocketFactory(factory);                
                return ssl_con;		
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
            ArrayList<String> allFiles = new ArrayList<>();
            
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
        ArrayList<String> result = new ArrayList<>();
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
