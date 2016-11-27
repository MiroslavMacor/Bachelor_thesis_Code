package cz.muni.ics.remsig.common;

/**
 *
 * @author miroslav
 */
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import cz.muni.ics.remsig.impl.TestManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

//import cz.muni.ics.remsig.impl.TestManager;

public class PerformanceTest {

    public PerformanceTest() {
    }
        
	//THIS IS WORKING DIRECTORY
        TestManager testManager = new TestManager();
        
        
        String directoryPathToxmlFile = "/home/miroslav/Documents/toDelete/80/input/"; 
        String directoryPathToxmlFilea = "/home/miroslav/Documents/toDelete/80/integrationTest/input/correctData/"; 
        String generateRequest = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "generateRequest.xml",false);;
        
        String importCertificate = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "importCertificate.xml",false);
        String importPKCS12 = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "importPkcs12.xml",false);
        //String importPKCS12 = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "importPkcs12.xml",false);
        //String importPKCS12 = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "importPkcs12b.xml",false);
        
        String listCertificatesWithStatus = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "listPersonsCerts.xml",false);
        String listAllCertificatesWithStatus = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "listAllCerts.xml",false);
        String exportPKCS12 = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "exportPkcs12.xml",false);
        String checkPassword = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "checkPassword.xml",false);
        String changePassword = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "changePasswordB.xml",false);
        String changePasswordB = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "changePassword.xml",false);
        String changeCertificateStatus = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "changeStatus.xml",false);
        String changeCertificateStatusB = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "changeStatusB.xml",false);
        String sign = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "sign.xml",false);
        String signNoId = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "signNoId.xml",false);
        String signPKCS7 = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "signPkcs7.xml",false);
        String signPdf = testManager.convertToStringFromXmlFile(directoryPathToxmlFile+ "signPdf.xml",false);
                
        //TestManager.exportXml(); 
        /**
         * For post method
         * @param typeOfCycle What repeat cycle is used can be for or while
         * @param numberOfIterations how many times cycle is run
         * @param methodName postMethodName as stated in Url e.g. https::/localhost:8443/RemSig/sign sing == methodName
         * @param postData
         * @return time it took for all post request to be sent
         */
        public long runTest(String typeOfCycle, int numberOfIterations, String methodName,String postData) throws Exception
        {
            long time = 0;
            long startTime;
            long endTime;
            startTime = System.nanoTime();
            URL sslUrl = new URL("https://localhost:8443/RemSig/"+methodName);
            SSLSocketFactory ssl_factory = this.establishConnection(sslUrl, true);
            
            switch(typeOfCycle.toLowerCase()) {
                
            
            case "for":{
                //startTime = System.nanoTime();

		
                for (int i = 0; i < numberOfIterations; i++) {
                    try {
                        sendPost(methodName, postData);
                        //post(methodName,sslUrl,ssl_factory);
                    } catch (Exception e) {
                        System.out.println(e.getMessage() +i);
                    }

                }
            endTime = System.nanoTime();
            time =  endTime - startTime;
            break;
            }
            case "while":{
                    startTime = System.nanoTime();
                    int zero=0;
                    while (zero <numberOfIterations) {
                        try {
                        sendPost(methodName, postData);
                    } catch (Exception e) {
                        System.out.print(e.getMessage());
                    }
                        
                    ++zero;
                
                    }
                    endTime = System.nanoTime();
                    time =  endTime - startTime;
            }
            
            break;
        default:
            System.out.println("typeOfCycle needs to be for or while");
    }
            
            
        return time;
        }
        /**
         * For testing sequence of methods 
         * @param typeOfCycle What repeat cycle is used can be for or while
         * @param numberOfIterations how many times cycle is run
         * @param methodNames list of methods to be run. If methodNames.size == 5
         * and numberOfIterations is 5 there will be 25 post methods 
         * @param postDataInSequence postRequest
         * @return time it took for all post request to be sent
         */
        public long runTest(String typeOfCycle, int numberOfIterations, ArrayList<String> methodNames,ArrayList<String> postDataInSequence) throws MalformedURLException, Exception
        {
            long time = 0;
            long startTime;
            long endTime;
            startTime = System.nanoTime();
          //  URL sslUrl = new URL("https://localhost:8443/RemSig/"+"generateRequest");
           // HttpsURLConnection ssl_con = this.establishConnection(sslUrl, true);
            switch(typeOfCycle.toLowerCase()) {
                
            case "for":{
                

		
                for (int i = 0; i < numberOfIterations; i++) {
                    try {
                        for (int j = 0; j < methodNames.size(); j++) {
                            sendPost(methodNames.get(j), postDataInSequence.get(j));
                            //post(ssl_con, generateRequest);
                            
                            
                        }
                    } catch (Exception e) {
                        System.out.print(e.getMessage());
                    }

                }
            endTime = System.nanoTime();
            time =  endTime - startTime;
            break;
            }
            case "while":{
                    startTime = System.nanoTime();
                    int i=0;
                    while (i <numberOfIterations) {
                        try {
                            int j = 0;
                            while (j< methodNames.size()){
                                
                               sendPost(methodNames.get(j), postDataInSequence.get(j));
                               ++j;
                            }
                            j=0;
                    } catch (Exception e) {
                        System.out.print(e.getMessage());
                    }
                        
                    ++i;
                
                    }
                    endTime = System.nanoTime();
                    time =  endTime - startTime;
            }
            
            break;
        default:
            System.out.println("typeOfCycle needs to be for or while");
    }
            
            
        return time;
        }
        
	public static void main(String[] args) throws Exception {

		PerformanceTest http = new PerformanceTest();
                TestManager testManager = new TestManager();
                
                int numberOfRepetion = 50;
                String cycleUsed= "for";// for or while
                long timeElapsed = 0;
                
                //timeElapsed = http.runTest(cycleUsed, numberOfRepetion, "checkPassword",http.checkPassword);
                System.out.println("all done in time =" +timeElapsed/1000000000);
               
                ArrayList<String> postMethods = new ArrayList<>();
                ArrayList<String> postData = new ArrayList<>();
                
                postMethods.add("changeCertificateStatus");
                postMethods.add("changeCertificateStatus");
                
                postData.add(http.changeCertificateStatus);
                postData.add(http.changeCertificateStatusB);
            
                
                timeElapsed = http.runTest(cycleUsed, numberOfRepetion, postMethods, postData);
                System.out.println("all done in time =" +timeElapsed/1000000000);
                 
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
		URL sslUrl = new URL("https://localhost:8443/ForkTesting/Fork/"+methodName);
                //URL sslUrl = new URL("https://localhost:8443/RemSig/"+methodName);
                
           
                    postSpecification(sslUrl, factory, postData);
           
            
                
            
		//postSpecification(sslUrl, factory, postData);
	}
        
        
        public void postSpecification(URL sslUrl,SSLSocketFactory factory,String postData ) throws IOException
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
		//System.out.println(response.toString());
            
            
        }
        
        public void exportStringAsXml(String input, String outputFilename)
        {
            try(  PrintWriter out = new PrintWriter("/home/miroslav/Documents/toDelete/80/" + outputFilename )  ){
                    out.println(input);
            }
            catch(FileNotFoundException e)
            {
                System.out.println(e.getMessage());
            }
            
        } public void generateXml() throws FileNotFoundException
        {
            // export pkcs file might need some work getting id
            String output = testManager.loadFile("/home/miroslav/Documents/toDelete/80/deletePersonal/testdata.pdf");
            try(  PrintWriter out = new PrintWriter("/home/miroslav/Documents/toDelete/80/" + "output/pdf.txt")  ){
                    out.println(output);
            }
            
        }
        public SSLSocketFactory establishConnection(URL sslUrl, boolean pkcs12Keystore) throws Exception {
                
		
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
		
                //HttpsURLConnection ssl_con = (HttpsURLConnection) sslUrl.openConnection();

		//ssl_con.setSSLSocketFactory(factory);
                return factory;
		//postSpecification(sslUrl, factory, postData);
	}
        
        
        public void post(String postData,URL sslUrl, SSLSocketFactory factory ) throws IOException
        {
                HttpsURLConnection ssl_con = (HttpsURLConnection) sslUrl.openConnection();

		ssl_con.setSSLSocketFactory(factory);

                
		ssl_con.setRequestMethod("POST");

		ssl_con.setDoOutput(true);

		try (DataOutputStream wr = new DataOutputStream(ssl_con.getOutputStream())) {			
                        wr.writeBytes(postData);
		}
		int responseCode = ssl_con.getResponseCode();

		StringBuilder response = new StringBuilder();
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(ssl_con.getInputStream()))) {
			String inputLine;
                        
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
                System.out.println(response);
		
            
            
        }
        
        
}