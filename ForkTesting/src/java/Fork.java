/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//import com.sun.xml.internal.messaging.saaj.util.Base64;
//import org.bouncycastle.util.encoders.Base64;
import org.apache.commons.codec.binary.Base64;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author miroslav
 */
@WebServlet(urlPatterns = {
	Fork.CHANGE_CERT_STATUS,
	Fork.CHANGE_PASSWORD,
	Fork.CHECK_PASSWORD,
	Fork.EXPORT_PKCS12,
	Fork.GEN_REQUEST,
	Fork.IMPORT_CERT,
	Fork.IMPORT_PKCS12,
	Fork.LIST_ALL_CERTS_STATUS,
	Fork.LIST_CERTS,
	Fork.LIST_CERTS_STATUS,
	Fork.LOGS,
	Fork.SIGN,
	Fork.SIGN_PDF,
	Fork.SIGN_PKCS7,
	Fork.STATS,
	Fork.UPLOAD_PRIVATE_KEY,
        
})

public class Fork extends HttpServlet {
        public static final String STATS = "/Fork/stats";
	public static final String LOGS = "/Fork/logs";
	public static final String CHECK_PASSWORD = "/Fork/checkPassword";
	public static final String LIST_CERTS = "/Fork/listCertificates";
	public static final String SIGN = "/Fork/sign";
	public static final String SIGN_PKCS7 = "/Fork/signPKCS7";
	public static final String SIGN_PDF = "/Fork/signPdf";

	public static final String GEN_REQUEST = "/Fork/generateRequest";
	public static final String IMPORT_CERT = "/Fork/importCertificate";
	public static final String IMPORT_PKCS12 = "/Fork/importPKCS12";
	public static final String EXPORT_PKCS12 = "/Fork/exportPKCS12";
	public static final String LIST_CERTS_STATUS = "/Fork/listCertificatesWithStatus";
	public static final String LIST_ALL_CERTS_STATUS = "/Fork/listAllCertificatesWithStatus";
	public static final String CHANGE_CERT_STATUS = "/Fork/changeCertificateStatus";
	public static final String CHANGE_PASSWORD = "/Fork/changePassword";
	public static final String UPLOAD_PRIVATE_KEY = "/Fork/uploadPrivateKey";    
           
        X509Certificate clientCert;
        
        Properties config = prepareConfigFile("/home/miroslav/Documents/Bakalarka/Remsig/test/testConfig/test.properties");
        
        
        String outputDirectory = config.getProperty("forkOutputDirectory");
        
        final String serverAddress = config.getProperty("serverAddress");
        final String serverAddressJava = config.getProperty("serverAddressJava");
        final String p12KeyFile = config.getProperty("pathToP12Keystore");
        final String p12KeyPassword = config.getProperty("p12Pass");
        final String defaultKeystore = config.getProperty("pathDefaultKeystore");
        final String defaultKeystorePass = config.getProperty("defaultKeystorePass");
        final String trustStore = config.getProperty("pathToJKSTruststore");
        final String trustStorePass = config.getProperty("trustoreJksPass");
        final String exportFilesDirectory = config.getProperty("exportFilesDirectory");
        
        
    @Override
    public void init() throws ServletException {
        config = prepareConfigFile("/test/testConfig/test.properties");
        outputDirectory = config.getProperty("forkOutputDirectory");
    }
        
    @Override
    protected void  doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
        response.getOutputStream().write("HELLO Fork class working".getBytes());        
    }
    @Override    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        
        String uniqueID = UUID.randomUUID().toString();
       	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss-");
	Date date = new Date();        
	String fileName =  dateFormat.format(date) + uniqueID;        
        PrintWriter out = response.getWriter();
        
        response.setContentType("text/plain; charset=utf-8");
        // set additional paramaters
        X509Certificate[] certChain = (X509Certificate[]) request.getAttribute(
                "javax.servlet.request.X509Certificate");
        
        if (certChain.length > 0) {
			clientCert = certChain[0];
		}
        ServletContext context = getServletContext();
        
        String urlPath = request.getRequestURI();        
        request.getRequestURI();        
        String methodFromUrl = urlPath.substring(urlPath.lastIndexOf("/")+1,urlPath.length());
        
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String data = buffer.toString();
        
        try {
             // sending data to first server and getting response
            URL sslUrl = new URL(serverAddress + methodFromUrl); // PHP server
            
            //get url and parse it end            
            String responsePHP = sendPost(methodFromUrl, data, sslUrl);
            try{
                PrintWriter originalPost = new PrintWriter(outputDirectory + "input/" + fileName);
                originalPost.println(data);
                originalPost.close();
                PrintWriter originalResponce = new PrintWriter(outputDirectory +"/outputPHP/" + fileName);
                
                originalResponce.close();
                URL sslUrlJava = new URL(serverAddressJava +methodFromUrl); // Javaserver)                
                String responseJava = sendPost(methodFromUrl, data, sslUrlJava);
                PrintWriter responseJavaPrinter = new PrintWriter(outputDirectory +"/outputJava/" + fileName);
                responseJavaPrinter.println(responseJava);
                responseJavaPrinter.close();                                    
            }
            catch(FileNotFoundException e)
            {
                PrintWriter fileNotFound = new PrintWriter(outputDirectory + "error/file" + fileName);
                fileNotFound.println(e + System.lineSeparator() + e.getMessage());
                fileNotFound.close();
            }
            
        } catch (Exception ex) {            
            PrintWriter generalError = new PrintWriter(outputDirectory + "error/all" + fileName);
            generalError.println(ex.getMessage()+ ex.toString());
            generalError.close();
        }
    }
    
    private String sendPost(String methodName,String postData,URL url) throws Exception {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");        
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        String pKeyPassword = defaultKeystorePass;        
        String pKeyFile = defaultKeystore;
        try (InputStream keyInput = new FileInputStream(pKeyFile)) {
                keyStore.load(keyInput, pKeyPassword.toCharArray());
        }
        keyStore.setCertificateEntry("client", clientCert);
        KeyStore keyStoreTest = KeyStore.getInstance(KeyStore.getDefaultType());
        String keyStoreTestPassword = trustStorePass;
        String keyStoreTestFile = trustStore;
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
        return postSpecification(url, factory, postData);
    }
        
        
    public String postSpecification(URL sslUrl,SSLSocketFactory factory,String postData )
            throws IOException{
        HttpsURLConnection ssl_con = (HttpsURLConnection) sslUrl.openConnection();
        ssl_con.setSSLSocketFactory(factory);
        ssl_con.setRequestMethod("POST");

        ssl_con.setDoOutput(
                        true);

        try (DataOutputStream wr = new DataOutputStream(ssl_con.getOutputStream())) {			
                wr.writeBytes(postData);
        }

        StringBuilder responseToUser = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(ssl_con.getInputStream()))) {
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                        responseToUser.append(inputLine);
                }
        }        
        return responseToUser.toString();
    }
    
        
    public boolean filesCheck(String firstFileName, String secondFileName){
        // if there is need for more precise chceck TestManager can get attributes from xml
        //load files
        String first = loadFile(firstFileName);
        String second = loadFile(secondFileName);
        int hashFirst = first.hashCode();
        int hashSecond = second.hashCode();            
        return (hashFirst == hashSecond);
    }
        
    /**
     * Takes filePath and returns its content as string read through bytes 
     * @param filePath path to file
     * @return Content of file
     */
    public String loadFile(String filePath)
    {
        String encodedString = null;
        Base64 base64 = new Base64();
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            byte[] encoded = base64.encode(bytes);
            encodedString = new String(encoded);
        } catch (IOException ex) {
            
        }
        return encodedString;        
    }
    public static Properties prepareConfigFile(String configFile){
        Properties configuration = new Properties();

        try (FileInputStream input = new FileInputStream(configFile)) {
                configuration.load(input);
        } catch (IOException ex)  {
               return null; 
        }
        return configuration;
    }
    static {
	    //for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){

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
}
