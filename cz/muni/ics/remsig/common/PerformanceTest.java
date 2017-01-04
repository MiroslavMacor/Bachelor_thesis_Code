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
import java.util.Properties;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.log4j.Logger;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.logging.Level;
import javax.management.MBeanServerConnection;

//import cz.muni.ics.remsig.impl.TestManager;

public class PerformanceTest {

    public PerformanceTest() {
    }        
    TestManager testManager = new TestManager(); 
    Properties config = TestManager.prepareConfigFile(TestManager.CONFIG_FILE_TEST);
    final String pathTotestFilesDirectory = config.getProperty("testFilesDirectory");
    final String serverAddress = config.getProperty("serverAddress");
    final String p12KeyFile = config.getProperty("pathToP12Keystore");
    final String p12KeyPassword = config.getProperty("p12Pass");
//    final String defaultKeystore = config.getProperty("pathDefaultKeystore");
//    final String defaultKeystorePass = config.getProperty("defaultKeystorePass");
    final String trustStore = config.getProperty("pathToJKSTruststore");
    final String trustStorePass = config.getProperty("trustoreJksPass");
    final String exportFilesDirectory = config.getProperty("exportFilesDirectory");
        
    //String directoryPathToxmlFile = "/home/miroslav/Documents/toDelete/80/input/"; performanceTestDir
    String directoryPathToxmlFile = config.getProperty("performanceTestDir");
    String generateRequest = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "generateRequest.xml",false);
    String importCertificate = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "importCertificate.xml",false);
    String importPKCS12 = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "importPkcs12.xml",false);
    String listCertificatesWithStatus = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "listPersonsCerts.xml",false);
    String listAllCertificatesWithStatus = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "listAllCerts.xml",false);
    String exportPKCS12 = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "exportPkcs12.xml",false);
    String checkPassword = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "checkPassword.xml",false);
    String changePassword = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "changePasswordB.xml",false);
    String changePasswordB = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "changePassword.xml",false);
    String changeCertificateStatus = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "changeStatus.xml",false);
    String changeCertificateStatusB = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "changeStatusB.xml",false);
    String sign = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "sign.xml",false);
    String signNoId = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "signNoId.xml",false);
    String signPKCS7 = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "signPkcs7.xml",false);
    String signPdf = testManager.convertToStringFromXmlFile(
            directoryPathToxmlFile+ "signPdf.xml",false);
    
    Logger log = TestManager.setUpLogger(IntegrationTest.class);
                
        
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
        URL sslUrl = new URL(serverAddress + methodName);        
        switch(typeOfCycle.toLowerCase()) {
            case "for":{
                startTime = System.nanoTime();
                for (int i = 0; i < numberOfIterations; i++) {
                    try {
                        sendPost(methodName, postData);  
//                        System.out.println("Before " + getCPUUssage());
                    } catch (Exception e) {
                        log.error("Error in interation " + i +" "+ e.getMessage());
                    }
                }
                endTime = System.nanoTime();
                time =  endTime - startTime;
                break;
            }
            case "while":{
                startTime = System.nanoTime();
                int i = 0;
                while (i < numberOfIterations) {
                    try {
                    sendPost(methodName, postData);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                ++i;
                }
                endTime = System.nanoTime();
                time =  endTime - startTime;
                break;
            }        
        default:
            log.info("typeOfCycle needs to be for or while");
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
        switch(typeOfCycle.toLowerCase()) {
            case "for":{
                for (int i = 0; i < numberOfIterations; i++) {                    
                    try {
                        for (int j = 0; j < methodNames.size(); j++) {
                            sendPost(methodNames.get(j), postDataInSequence.get(j));
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
                endTime = System.nanoTime();
                time =  endTime - startTime;
                break;
            }
            case "while":{
                startTime = System.nanoTime();
                int i = 0;
                while (i < numberOfIterations){
                    try {
                        int j = 0;
                        while (j< methodNames.size()){
                           sendPost(methodNames.get(j), postDataInSequence.get(j));
                           ++j;
                        }
                        j=0;
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                ++i;
                }
                endTime = System.nanoTime();
                time =  endTime - startTime;
                break;
            }
            default:
                log.warn("typeOfCycle needs to be for or while");
        }
        return time;
    }        
    public static void main(String[] args){
        
        PerformanceTest http = new PerformanceTest();
        //TestManager testManager = new TestManager();
        //testManager.databaseInit();
        
        http.executeTest();
    }
    private void executeTest() {
        int numberOfRepetion = 100;
        String cycleUsed= "for";// for or while
        long timeElapsed = 0;         
        
        ArrayList<String> postMethods = new ArrayList<>();
        ArrayList<String> postData = new ArrayList<>();

        postMethods.add("changeCertificateStatus");
        postMethods.add("changeCertificateStatus");
        postData.add(changeCertificateStatus);
        postData.add(changeCertificateStatusB);
        
        try {
            MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
            OperatingSystemMXBean osMBean = ManagementFactory.newPlatformMXBeanProxy(
            mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
            
            OperatingSystemMXBean osbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            RuntimeMXBean runbean = (RuntimeMXBean) ManagementFactory.getRuntimeMXBean();
            
            int CPUNumber = 0;
            long prevUpTime = 0;
            long prevProcessTime = 0 ;            
            long nanoBefore = 0; 
            long CPUBefore = 0 ;
            
            double memoryBefore = getMemoryUssageSystem();
            double cpuBefore = getCPUUssage();
            
        if (osMBean != null){
            
            nanoBefore = System.nanoTime();
            CPUBefore = osMBean.getProcessCpuTime();
            CPUNumber = osbean.getAvailableProcessors();
            prevUpTime = runbean.getUptime();
            prevProcessTime = osbean.getProcessCpuTime();            
            System.out.println("systemMemory before = " + memoryBefore + "cpuBeforeSystem = " + cpuBefore );
            
            System.out.println("Before " + getCPUUssage());
            
            
            timeElapsed = runTest(cycleUsed, numberOfRepetion, "sign",sign);
            
            
            double memoryUsed = getMemoryUssageSystem() - memoryBefore;
            double CPUUSed =  getCPUUssage() - cpuBefore;
            System.out.println("systemMemory used = " + memoryUsed + "cpu used = " + CPUUSed );
                    
            log.info("all done in time =" +timeElapsed/1000000000);
            log.info("with memory Usage = "+ getMemoryUssage() + "MB");
            System.out.println("After " + getCPUUssage());
            afterRuntime(osMBean, nanoBefore, CPUBefore);            
            double result = runTimeUpgradeAfter(osbean, runbean, CPUNumber, prevUpTime, prevProcessTime);
            System.out.println("Runtime Upgrade percentage =  " + result);
            
            
//            runTimeUpgradeBefore(osbean, runbean, CPUNumber, prevUpTime, prevUpTime);            
//            System.out.println("Before " + getCPUUssage());
//            timeElapsed = runTest(cycleUsed, numberOfRepetion, postMethods, postData);
//            log.info("all done in time =" +timeElapsed/1000000000);
//            log.info("with memory Usage = "+ getMemoryUssage() + "MB");
//            System.out.println("After " + getCPUUssage());
//            afterRuntime(osMBean, nanoBefore, CPUBefore);
//            runTimeUpgradeBefore(osbean, runbean, CPUNumber, prevUpTime, prevUpTime);
        }
        }catch (Exception e){
            log.error(e);
        }
    }
    
    
    private void sendPost(String methodName,String postData) throws Exception {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        String pKeyPassword = p12KeyPassword;        
        String pKeyFile = p12KeyFile;
        try (InputStream keyInput = new FileInputStream(pKeyFile)) {
                keyStore.load(keyInput, pKeyPassword.toCharArray());
        }

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
        URL sslUrl = new URL("https://localhost:8443/RemSig/"+methodName);
        //URL sslUrl = new URL(serverAddress + methodName);
        postSpecification(sslUrl, factory, postData);
    }   
    public void postSpecification(URL sslUrl,SSLSocketFactory factory,String postData ) throws IOException    {
        HttpsURLConnection ssl_con = (HttpsURLConnection) sslUrl.openConnection();
        ssl_con.setSSLSocketFactory(factory);
        ssl_con.setRequestMethod("POST");
        ssl_con.setDoOutput(
                        true);

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
//        log.info(response.toString());
    }
        
    public void exportStringAsXml(String input, String outputFilename){
        try(  PrintWriter out = new PrintWriter(exportFilesDirectory + outputFilename )  ){
                out.println(input);
        }
        catch(FileNotFoundException e)
        {
            log.error(e.getMessage());
        }
    }
    long temp = 1024L * 1024L;
    public long getMemoryUssage(){     
        Runtime runtime = Runtime.getRuntime();         
        //runtime.gc();
        System.out.println("Total memory " + bytesToMegabytes(Runtime.getRuntime().totalMemory()));
        return bytesToMegabytes(runtime.totalMemory() - runtime.freeMemory());
    }
     private long bytesToMegabytes(long bytes) {
        return bytes / temp;
    }
    public double getCPUUssage(){
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);
        double result = osBean.getSystemCpuLoad();
        //System.out.println("Process load = " + result);
        //System.out.println("System load = "  + osBean.getSystemCpuLoad());
        //System.out.println("Total memory fomr os bean  = "  + bytesToMegabytes(osBean.getTotalPhysicalMemorySize()));
        return result;
    }
    public double getMemoryUssageSystem(){
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);
        System.out.println("FREE memory  = "  + bytesToMegabytes(osBean.getFreePhysicalMemorySize()));
        
        return bytesToMegabytes(osBean.getTotalPhysicalMemorySize() - osBean.getFreePhysicalMemorySize());        
    }
    
    /*
    double  getProcessCpuLoad()
    Returns the "recent cpu usage" for the Java Virtual Machine process.

    long    getProcessCpuTime()
    Returns the CPU time used by the process on which the Java virtual machine is running in nanoseconds.

    double  getSystemCpuLoad()
    Returns the "recent cpu usage" for the whole system.
    */
    double javacpu;
    double uptime;
    private void _getJavaRuntime() {
        OperatingSystemMXBean osbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runbean = (RuntimeMXBean) ManagementFactory.getRuntimeMXBean();
        int nCPUs = osbean.getAvailableProcessors();
        long prevUpTime = runbean.getUptime();
        long prevProcessCpuTime = osbean.getProcessCpuTime();
        try {
         Thread.sleep(500);
        } catch (Exception e) { 
        }
        osbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long upTime = runbean.getUptime();
        long processCpuTime = osbean.getProcessCpuTime();
        if (prevUpTime > 0L && upTime > prevUpTime) {
            long elapsedCpu = processCpuTime - prevProcessCpuTime;
            long elapsedTime = upTime - prevUpTime;
            javacpu = Math.min(99F, elapsedCpu / (elapsedTime * 10000F * nCPUs));
        } else {
            javacpu = 0.001;
        }
        uptime = runbean.getUptime();
    }
    private void runTimeUpgradeBefore(OperatingSystemMXBean osbean, RuntimeMXBean runbean,int nCPUs, long prevUpTime, long prevProcessCpuTime){
        osbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        runbean = (RuntimeMXBean) ManagementFactory.getRuntimeMXBean();
        nCPUs = osbean.getAvailableProcessors();
        prevUpTime = runbean.getUptime();
        prevProcessCpuTime = osbean.getProcessCpuTime();
    }
    private double runTimeUpgradeAfter(OperatingSystemMXBean osbean, RuntimeMXBean runbean,int nCPUs, long prevUpTime, long prevProcessCpuTime){
        osbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long upTime = runbean.getUptime();
        long processCpuTime = osbean.getProcessCpuTime();
        if (prevUpTime > 0L && upTime > prevUpTime) {
            long elapsedCpu = processCpuTime - prevProcessCpuTime;
            long elapsedTime = upTime - prevUpTime;
            javacpu = Math.min(99F, elapsedCpu / (elapsedTime * 10000F * nCPUs));
        } else {
            javacpu = 0.001;
        }
        uptime = runbean.getUptime();
        return javacpu;
    }
   
    
    
    private void runtimeTest() throws IOException{
        MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();

       OperatingSystemMXBean osMBean = ManagementFactory.newPlatformMXBeanProxy(
       mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);

       long nanoBefore = System.nanoTime();
       long cpuBefore = osMBean.getProcessCpuTime();

       // Call an expensive task, or sleep if you are monitoring a remote process

       long cpuAfter = osMBean.getProcessCpuTime();
       long nanoAfter = System.nanoTime();

       long percent;
       if (nanoAfter > nanoBefore)
        percent = ((cpuAfter-cpuBefore)*100L)/
          (nanoAfter-nanoBefore);
       else percent = 0;

       System.out.println("Cpu usage: "+percent+"%");
    }
    
    private long afterRuntime(OperatingSystemMXBean osMBean, long nanoBefore , long cpuBefore){
       long cpuAfter = osMBean.getProcessCpuTime();
       long nanoAfter = System.nanoTime();
       long percent;
       
       if (nanoAfter > nanoBefore)
        percent = ((cpuAfter-cpuBefore)*100L)/
          (nanoAfter-nanoBefore);
       else percent = 0;
       System.out.println("Cpu usage: "+percent+"%");
    
        return percent;
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
}