/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.ics.remsig.impl;

import commons.Settings;
import static cz.muni.ics.remsig.impl.CertificateManagerImplTest.CONFIG_FILE;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Security;
import java.sql.Connection;
import java.util.Properties;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import static org.hamcrest.core.IsNot.not;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Document;

/**
 *
 * @author miroslav
 */
public class SignerImplIT {
        
    private Properties configuration;   
    private final static org.slf4j.Logger logger
            = LoggerFactory.getLogger(CertificateManagerImplTest.class);
    private IDatabaseTester databaseTester;
    private IDatabaseConnection dbUnitConnection;
    private Connection connection;
    private java.sql.Statement statement;
    private String initXmlDoc = Settings.initXmlDoc;     
    private ITable expectedTable = null;
    public TestManager testManager= new TestManager();
    SignerImpl signer;
    
    public static final Properties config = TestManager.prepareConfigFile(TestManager.CONFIG_FILE_TEST);
    
    String emptyPdf = config.getProperty("testEmptyPdf");
    String pdf = config.getProperty("testPdf");
    String notAPdf = config.getProperty("testNotAPdf");
    String privateKeyFileName = config.getProperty("testPrivateKey");
    
    
    org.w3c.dom.Document testDocument1 = null;
    org.w3c.dom.Document testDocument2 = null;
    org.w3c.dom.Document testDocument3 = null;
    org.w3c.dom.Document testDocument4 = null;
    org.w3c.dom.Document testDocument5 = null;
    org.w3c.dom.Document testDocument6 = null;
    org.w3c.dom.Document testDocument7 = null;
    org.w3c.dom.Document testDocument8 = null;
    org.w3c.dom.Document testDocument9 = null;
    org.w3c.dom.Document testDocument10 = null;
    org.w3c.dom.Document testDocument11 = null;
    org.w3c.dom.Document testDocument12 = null;
    org.w3c.dom.Document testDocument13 = null;
    org.w3c.dom.Document testDocument14 = null;
    org.w3c.dom.Document testDocument15 = null;
    org.w3c.dom.Document testDocument16 = null;
    org.w3c.dom.Document testDocument17 = null;
    org.w3c.dom.Document testDocument18 = null;
    
    
    Person anderson = new Person(1);
    Person bobaFet = new Person(2);
    Person cyril = new Person(3);
    Person daryl = new Person(4);
    Person eva = new Person(5);
    Person frank = new Person(6);
    Person gregor = new Person(7);
    Person helena = new Person(8);
    Person igor = new Person(9);
    Person jane = new Person(10);
    
    String andersonDefPass = "123456";
    String bobaFetDefPass = "bobaFet1";
    String cyrilDefPass = "654321";
    String darylDefPass = "g8r4d2";
    String evaDefPass = "strongpassword";
    String frankDefPass = "frankrulles";
    String gregorDefPass = "password";
    String helenaDefPass = "";
    String igorDefPass = "forever";
    String janeDefPass = "h86fds";
    
    int andersonCerId = 1104;
    int bobaFetCerId = 1105 ;
    int cyrilCerId = 1106 ;
    
    
    
    
    public SignerImplIT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws RemSigException, ClassNotFoundException, DataSetException, FileNotFoundException, Exception {
        configuration = new Properties();
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            configuration.load(input);
        } catch (FileNotFoundException ex) {
            throw new RemSigException(
                    "Configuration properties file not found",
                    ex, ErrorCode.ERROR258EN);
        } catch (IOException ex) {
            throw new RemSigException(
                    "Error while loading configuration properties",
                    ex, ErrorCode.ERROR259EN);
        }
        databaseTester = new JdbcDatabaseTester(Settings.dbDriverClass,
                Settings.dbConnectionUrl, Settings.dbUserName, Settings.dbPassword);
        IDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).
                build(new FileInputStream(initXmlDoc));
        
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.setDataSet(dataSet);
        databaseTester.setSetUpOperation(DatabaseOperation.REFRESH);
        databaseTester.onSetup();
        dbUnitConnection = databaseTester.getConnection();
        connection = dbUnitConnection.getConnection();
        
        statement = connection.createStatement();
        expectedTable = dataSet.getTable("credentials");
        
        
    }
    
    @After
    public void tearDown() throws Exception {
        //databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        //databaseTester.onTearDown();
    }

    private String defaultSignatureAnderson = "M9AUrRzg57v9N9Y+IndnxbMGCJ7+0Qi6d"
            + "2e8KrC7ZGCBC05o6iAMQlJLltdJnzTVyCjHXpM2mc7erD6/IHcpYG+QLpKjN8Bo7n"
            + "EtSkkJTTlWlCyBRq9lh/VKoQSyzJAiSA/RjnDm2hPzA61fcE7l9p7fICA0UqCiDCgT"
            + "k/Oom3Hx0faVDMlfh5x/pmw7AtdjCpn/9KI0h0DFNhpuUOkr3sR0bM66udPXscBKl1"
            + "mbckBoU8mczgOROy+F10eUkD3h+5wmWoJnYi5xj4ZD/CS3mkXQRY7Vb/X79K8tQvXH"
            + "rC9hUcC3yJMYEUNtx82uY8Lgxv5j8uN9XN4NY+PkMnLUVjb5KeUAI7iVQ+ueDT8J"
            + "7+z1gNvpRkIjSwne+IXDCu9hCopgJO/oiMcxqZrlZjiiBz2+58a92TWh6tnWJESSj7"
            + "IA3RIP18EQFWhbA0PI6/lDf02KE15mlJnOCKnhRAP4K1yTeKfzmamh5cn5w+lu9sZ6"
            + "WKaoMkq/kjowx1QfXfxa+CxCHy56zUAJSuxVHXm8T5cMmkd4nHYHe9WMYHQFgxLz3"
            + "QOgLQsGvW2f2VHfBNc0xcoJR3D1QvihC5TxOzkNtx8RAhsmJF5lTul1Yr0ajgJ+B6"
            + "sIYQs302o8+sjMmoqggcm4NUlOtVlOjpE1orlsoIxbEcYY8GL6VFZpdc6yRA4=";
    private String defaultSignatureBobaFet = "RFqKc42myIHCakQQB/bbjaDFEGc/Bfxf6Sl"
            + "+AmJ3KVPOs/DOShiaFR1FoiE+2eKsD8PMJBzGMfo6N5rmjnfARhHBkaA5ixd+k93F+"
            + "57sfNT5qrnhYnJ1oM7g5w9RV8qCAISeD0/KOLoMmJePlDiUAkr2nMAFxFgf4ydAiZs"
            + "kdeYjpstisMKkQtNHo3Z9gyWOEXndxz7Jb+bNZQJ6bBcjsTVtnfe/Fh2vjr2yXKZAU"
            + "ak+RRHh41AvLkvv+P3I/JvsT+g7Oko0OyAI/0EkcureR78bwATUSGQSw8t7mftnVE"
            + "NTxLQsvQPfYpUAvWK0lQGb1fkxIEDjYjJFfYmf9RY+jtXxqtEA4r8s+08Kd83DzNpg"
            + "xwA18crVG1MZwKFIY6sITqi3auwVCwWDlPC0QhSify1NUIUQ8FoL82vJe4yTxm2o2l8"
            + "o4QFL2UDveQDdj1FjpLD1TBgC6uH6+wqzo1fiXdmF3N9yPJZcrsQAmd7i/QuauRY/Zp"
            + "n1oQ4g5hQ6thDPKWpQ3MDgcs8lgUr6ic3S4mAr3Jwm/8PlP/6ZXZYxirftg4KWTlTRh"
            + "1ot4ou2nsctvBH6ZAYE+hbWL3qbLzD95jgnmhiP3JSRc8qmw6lT9q9HHL6XPGXs4oJJq"
            + "8Og4KH3r12Z25fEJ59IVYo6PNccudPNqHvvrCUMCNrkkdwhrDA=";
    private String defaultSignatureCyril = "ZhpIL9dez66G5iayoue3aQgfm1sp3wNBOdWugS3"
            + "qGGDSOlPStcbqrPCL9J3MTyeU1+6kqxEXr7xnOcwG1T3Vbnjn2/VyQVqThYs1ILjlAZ"
            + "TV2Y+gXsEDjsPMvbeNJGzhLeu04YruRjps9jN7E1u9Gi6QjVfLS1XhIJx3uzqeGUumC"
            + "AJAX3Rnto56/hfmQOAhcQIdGbz9ujn7ccE+giRR1Co0UUUnHejCBe/TJNB1YZOLtQw8"
            + "r1zZEg9EZ2SNAXCFjzxaFKbDUZGCOFQxbh38Twy7HNbJb91ns/JkaW2PCyYj2/OpSQA"
            + "xFZWb39xtJ/fqv5YxCysKuYfKlj5jM6vsdXfbH9Yq4LQdJqMXd0IUpXast/nF0EZz1B"
            + "01gCMlDm2Qrh+hj5/+Cq9PQLuzz/L7kVEMdftOCC/bSOuFjiCe4OerPIZPyzK3R527b"
            + "4Nl8TtTNLRJxdrnhFjUHF0zzGNNvNvjfV3JM7XlE7gneTxj1bOjtDf1W2QnF6xx3z3"
            + "N6zAhLTo9Lt+36dmLMslbeN11KmhXQVXKQLfw/6EAWNXRloHKvCcH6/fjuJdz7S7Y8"
            + "SHjZBKV2gl2eUliUh6p+IphufzaxRmDEA7zEH99mbPPB2E0vten8Ta8Fgnqan8kIFm8"
            + "pPlMEDqh1HBGWrDLykrozCOS/jYCXnwSjat2tyNsM20=";

    /**
     * Test of sign method, of class SignerImpl.
     */
    @Test
    public void testSign_4args() throws Exception {        
        docInit();
        configurationSetUp();
        try {
            testDocument1 = signer.sign(null, andersonDefPass, "signData",andersonCerId);            
            testDocument2 = signer.sign(anderson, null, "signData",andersonCerId);
            testDocument3 = signer.sign(anderson, andersonDefPass, null,andersonCerId);
            testDocument4 = signer.sign(null, null, "signData",andersonCerId);
            testDocument5 = signer.sign(null, andersonDefPass, null,andersonCerId);
            testDocument6 = signer.sign(anderson, null, null,andersonCerId);
            testDocument7 = signer.sign(null, null, null,andersonCerId);
            Document[] allDoc = new Document[]{testDocument1, testDocument2, testDocument3, testDocument4, testDocument5, testDocument6, testDocument7};
        if ( 0 != testManager.chceckDocuments(allDoc, 0, 0)) {
            fail("Input null value passed");
        }                
        } catch (NullPointerException e) {
            fail("uncaught nullPointerException");
        }        
        try {
            testDocument1 = signer.sign(anderson, andersonDefPass, "data", cyrilCerId);
            testDocument2 = signer.sign(bobaFet, andersonDefPass, "data", andersonCerId);
            testDocument3 = signer.sign(anderson, bobaFetDefPass, "data", andersonCerId);
            assertNull(testManager.extractElementFromXmlDoc(testDocument1, "signature"));
            assertNull(testManager.extractElementFromXmlDoc(testDocument2, "signature"));
            assertNull(testManager.extractElementFromXmlDoc(testDocument3, "signature"));                
        } catch (Exception e) {                        
        }
        testDocument1 = signer.sign(anderson, andersonDefPass, "dataToSign", andersonCerId);        
        String signedData = testManager.extractElementFromXmlDoc(testDocument1, "signature");
            
        testDocument2 = signer.sign(anderson, andersonDefPass, "trainRules", andersonCerId);
        String expectedAnd = testManager.extractElementFromXmlDoc(testDocument2, "signature");
        
        testDocument3 = signer.sign(bobaFet, bobaFetDefPass, "are", bobaFetCerId);        
        String expectedBob = testManager.extractElementFromXmlDoc(testDocument3, "signature");
        
        testDocument4 = signer.sign(cyril, cyrilDefPass, "forEveryOne", cyrilCerId);        
        String expectedCyr = testManager.extractElementFromXmlDoc(testDocument4, "signature");
        
        assertEquals(expectedAnd, defaultSignatureAnderson);
        assertEquals(expectedBob, defaultSignatureBobaFet);
        assertEquals(expectedCyr, defaultSignatureCyril);
        assertThat(signedData, not(defaultSignatureAnderson));
    }
    /**
     * Testing sign method with 3 arguments if you are not using preset value it 
     * is necessary to set value testIsUsingDefaultCertificate to false  
     * Test of sign method, of class SignerImpl.
     */

    private void configurationSetUp() throws BeansException {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        signer = new SignerImpl(configuration);
        signer.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());
    }

    
    private boolean testIsusingDefaultCertificates = true; // todo 89 change to properites ?
    
    @Test
    public void testSign_3args() throws Exception {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        signer = new SignerImpl(configuration);
        signer.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        
        TestManager managerTest = new TestManager();
        Security.addProvider(new BouncyCastleProvider());
        
        try {
            testDocument1 = signer.sign(anderson, andersonDefPass, null);
            testDocument2 = signer.sign(null, andersonDefPass, "randomdata");
            testDocument3 = signer.sign(anderson, null, "randomdata");
            testDocument4 = signer.sign(anderson, null, null);
            testDocument5 = signer.sign(null, null, "randomdata");
            testDocument6 = signer.sign(null, null, null);
            Document[] allDoc = new Document[]{testDocument1, testDocument2, testDocument3, testDocument4, testDocument5, testDocument6};
            if ( 0 != testManager.chceckDocuments(allDoc, 0, 0))
            {
                fail("Input null value passed");
            }
        } catch (NullPointerException e) {
//            fail("Uncaught nullPointer exception" +e.getMessage());
        }
        
        
        
        if (testIsusingDefaultCertificates) {
            testDocument1 = signer.sign(anderson, andersonDefPass, "trainRules");            
            String expectedAnd = managerTest.extractElementFromXmlDoc(testDocument1, "signature");
            
            testDocument2 = signer.sign(bobaFet, bobaFetDefPass, "are");            
            String expectedBob = managerTest.extractElementFromXmlDoc(testDocument2, "signature");
            
            testDocument3 = signer.sign(cyril, cyrilDefPass, "forEveryOne");            
            String expectedCyr = managerTest.extractElementFromXmlDoc(testDocument3, "signature");
            
            assertEquals("Signature doesn't corresnd with preset", defaultSignatureAnderson, expectedAnd);
            assertEquals("Signature doesn't corresnd with preset", defaultSignatureBobaFet, expectedBob);
            assertEquals("Signature doesn't corresnd with preset", defaultSignatureCyril, expectedCyr);
            
        }
        
        try {
            testDocument10 = signer.sign(anderson, evaDefPass, evaDefPass);
            assertNull(testManager.extractElementFromXmlDoc(testDocument10, "signature"));
                
        } catch (Exception e) {
        }
        
        testDocument7 =  signer.sign(anderson, andersonDefPass, "trainRules");
        String andersonSig = managerTest.extractElementFromXmlDoc(testDocument7, "signature");
        
        testDocument4 =  signer.sign(anderson, andersonDefPass, "Gibberish");
        String diffData = managerTest.extractElementFromXmlDoc(testDocument4, "signature");
        
        
        testDocument5 =  signer.sign(bobaFet, bobaFetDefPass, "somethingelse");
        String diffDataB = managerTest.extractElementFromXmlDoc(testDocument5, "signature");
        
        assertThat(defaultSignatureAnderson, not(diffData));
        assertThat(defaultSignatureBobaFet, not(diffDataB));
        
        testDocument6 =  signer.sign(anderson, andersonDefPass, "trainRules");
        String originalData = managerTest.extractElementFromXmlDoc(testDocument6, "signature");
        assertEquals(andersonSig, originalData);
        
        
        
        
    }
    
    
    private String defaultSignatureAnderson7 = "M9AUrRzg57v9N9Y+IndnxbMGCJ7+0Qi6d"
            + "2e8KrC7ZGCBC05o6iAMQlJLltdJnzTVyCjHXpM2mc7erD6/IHcpYG+QLpKjN8Bo7n"
            + "EtSkkJTTlWlCyBRq9lh/VKoQSyzJAiSA/RjnDm2hPzA61fcE7l9p7fICA0UqCiDCgT"
            + "k/Oom3Hx0faVDMlfh5x/pmw7AtdjCpn/9KI0h0DFNhpuUOkr3sR0bM66udPXscBKl1"
            + "mbckBoU8mczgOROy+F10eUkD3h+5wmWoJnYi5xj4ZD/CS3mkXQRY7Vb/X79K8tQvXH"
            + "rC9hUcC3yJMYEUNtx82uY8Lgxv5j8uN9XN4NY+PkMnLUVjb5KeUAI7iVQ+ueDT8J"
            + "7+z1gNvpRkIjSwne+IXDCu9hCopgJO/oiMcxqZrlZjiiBz2+58a92TWh6tnWJESSj7"
            + "IA3RIP18EQFWhbA0PI6/lDf02KE15mlJnOCKnhRAP4K1yTeKfzmamh5cn5w+lu9sZ6"
            + "WKaoMkq/kjowx1QfXfxa+CxCHy56zUAJSuxVHXm8T5cMmkd4nHYHe9WMYHQFgxLz3"
            + "QOgLQsGvW2f2VHfBNc0xcoJR3D1QvihC5TxOzkNtx8RAhsmJF5lTul1Yr0ajgJ+B6"
            + "sIYQs302o8+sjMmoqggcm4NUlOtVlOjpE1orlsoIxbEcYY8GL6VFZpdc6yRA4=";
    private String defaultSignatureBobaFet7 = "RFqKc42myIHCakQQB/bbjaDFEGc/Bfxf6Sl"
            + "+AmJ3KVPOs/DOShiaFR1FoiE+2eKsD8PMJBzGMfo6N5rmjnfARhHBkaA5ixd+k93F+"
            + "57sfNT5qrnhYnJ1oM7g5w9RV8qCAISeD0/KOLoMmJePlDiUAkr2nMAFxFgf4ydAiZs"
            + "kdeYjpstisMKkQtNHo3Z9gyWOEXndxz7Jb+bNZQJ6bBcjsTVtnfe/Fh2vjr2yXKZAU"
            + "ak+RRHh41AvLkvv+P3I/JvsT+g7Oko0OyAI/0EkcureR78bwATUSGQSw8t7mftnVE"
            + "NTxLQsvQPfYpUAvWK0lQGb1fkxIEDjYjJFfYmf9RY+jtXxqtEA4r8s+08Kd83DzNpg"
            + "xwA18crVG1MZwKFIY6sITqi3auwVCwWDlPC0QhSify1NUIUQ8FoL82vJe4yTxm2o2l8"
            + "o4QFL2UDveQDdj1FjpLD1TBgC6uH6+wqzo1fiXdmF3N9yPJZcrsQAmd7i/QuauRY/Zp"
            + "n1oQ4g5hQ6thDPKWpQ3MDgcs8lgUr6ic3S4mAr3Jwm/8PlP/6ZXZYxirftg4KWTlTRh"
            + "1ot4ou2nsctvBH6ZAYE+hbWL3qbLzD95jgnmhiP3JSRc8qmw6lT9q9HHL6XPGXs4oJJq"
            + "8Og4KH3r12Z25fEJ59IVYo6PNccudPNqHvvrCUMCNrkkdwhrDA=";
    private String defaultSignatureCyril7 = "ZhpIL9dez66G5iayoue3aQgfm1sp3wNBOdWugS3"
            + "qGGDSOlPStcbqrPCL9J3MTyeU1+6kqxEXr7xnOcwG1T3Vbnjn2/VyQVqThYs1ILjlAZ"
            + "TV2Y+gXsEDjsPMvbeNJGzhLeu04YruRjps9jN7E1u9Gi6QjVfLS1XhIJx3uzqeGUumC"
            + "AJAX3Rnto56/hfmQOAhcQIdGbz9ujn7ccE+giRR1Co0UUUnHejCBe/TJNB1YZOLtQw8"
            + "r1zZEg9EZ2SNAXCFjzxaFKbDUZGCOFQxbh38Twy7HNbJb91ns/JkaW2PCyYj2/OpSQA"
            + "xFZWb39xtJ/fqv5YxCysKuYfKlj5jM6vsdXfbH9Yq4LQdJqMXd0IUpXast/nF0EZz1B"
            + "01gCMlDm2Qrh+hj5/+Cq9PQLuzz/L7kVEMdftOCC/bSOuFjiCe4OerPIZPyzK3R527b"
            + "4Nl8TtTNLRJxdrnhFjUHF0zzGNNvNvjfV3JM7XlE7gneTxj1bOjtDf1W2QnF6xx3z3"
            + "N6zAhLTo9Lt+36dmLMslbeN11KmhXQVXKQLfw/6EAWNXRloHKvCcH6/fjuJdz7S7Y8"
            + "SHjZBKV2gl2eUliUh6p+IphufzaxRmDEA7zEH99mbPPB2E0vten8Ta8Fgnqan8kIFm8"
            + "pPlMEDqh1HBGWrDLykrozCOS/jYCXnwSjat2tyNsM20=";

    /**
     * Test of signPKCS7 method, of class SignerImpl.
     *
     * 
     */
    @Test
    public void testSignPKCS7_5args() throws Exception {
        docInit();
        configurationSetUp();
        String profile = "cnb_01";
        String dataToSign = "something" ;
        

        // testing written in unworking code
        try {
            testDocument1 =  signer.signPKCS7(null, andersonDefPass,dataToSign, profile, andersonCerId);
            testDocument2 = signer.signPKCS7(anderson, null,dataToSign, profile, andersonCerId);
            testDocument3 = signer.signPKCS7(anderson, andersonDefPass,null, profile, andersonCerId);
            testDocument4 = signer.signPKCS7(anderson, andersonDefPass,dataToSign, null, andersonCerId);
            testDocument5 = signer.signPKCS7(anderson, andersonDefPass,dataToSign, profile, cyrilCerId);
            testDocument6 = signer.signPKCS7(anderson, andersonDefPass,null, null, andersonCerId);            
            testDocument7 = signer.signPKCS7(anderson, null,dataToSign, null, andersonCerId);
            testDocument8 = signer.signPKCS7(null, andersonDefPass,dataToSign, null, andersonCerId);
            testDocument9 = signer.signPKCS7(null, null,dataToSign, profile, andersonCerId);
            testDocument10 = signer.signPKCS7(null, andersonDefPass,null, profile, andersonCerId);
            testDocument11 = signer.signPKCS7(anderson, null,null, profile, andersonCerId);
            testDocument12 = signer.signPKCS7(null, null,null, profile, andersonCerId);
            testDocument13 = signer.signPKCS7(null, null,dataToSign, null, andersonCerId);
            testDocument14 = signer.signPKCS7(anderson, null,null, null, andersonCerId);            
            testDocument15 = signer.signPKCS7(anderson, null,null, null, cyrilCerId);
            testDocument16 = signer.signPKCS7(null, null,null, null, andersonCerId);
           
            
        } catch (NullPointerException e) {
//            fail("uncaught nullpointer exception");
        }
        Document[] testSubjects = new Document[]{testDocument1,testDocument2,testDocument3,testDocument4,testDocument5,
            testDocument6, testDocument7, testDocument8, testDocument9, testDocument10, testDocument11, testDocument12,
            testDocument13, testDocument14,testDocument15, testDocument16};
            
            if ( 0 != testManager.chceckDocuments(testSubjects, 0, 0)){
                fail("some documents were signed with null data");
            }
        
        try {
                testDocument1 = signer.signPKCS7(anderson, andersonDefPass,dataToSign, profile, cyrilCerId);
                testDocument2 = signer.signPKCS7(anderson, bobaFetDefPass,dataToSign, profile, andersonCerId);
                testDocument3 = signer.signPKCS7(cyril, andersonDefPass,dataToSign, profile, andersonCerId);
                testDocument4 = signer.signPKCS7(bobaFet, bobaFetDefPass,dataToSign, "unknown", andersonCerId);
                testDocument5 = signer.signPKCS7(anderson, cyrilDefPass,dataToSign, profile, cyrilCerId);
                testDocument6 = signer.signPKCS7(anderson, bobaFetDefPass,dataToSign, profile, andersonCerId);
                
                assertNull(testManager.extractElementFromXmlDoc(testDocument1, "pkcs7"));
                assertNull(testManager.extractElementFromXmlDoc(testDocument2, "pkcs7"));
                assertNull(testManager.extractElementFromXmlDoc(testDocument3, "pkcs7"));
                assertNull(testManager.extractElementFromXmlDoc(testDocument4, "pkcs7"));
                assertNull(testManager.extractElementFromXmlDoc(testDocument5, "pkcs7"));
                assertNull(testManager.extractElementFromXmlDoc(testDocument6, "pkcs7"));
                
        } catch (Exception e) {
            
            
        }
        try {
            testDocument1 = signer.signPKCS7(anderson, andersonDefPass,dataToSign, "cnb_01", andersonCerId);
            testDocument2 = signer.signPKCS7(bobaFet, bobaFetDefPass,dataToSign, "ceskaposta_01", bobaFetCerId);
            testDocument3 = signer.signPKCS7(anderson, cyrilDefPass,dataToSign, "cnb_01", cyrilCerId);
            
            String outputAnderson =  testManager.extractElementFromXmlDoc(testDocument1, "pkcs7");
            String outputBobaFet  =  testManager.extractElementFromXmlDoc(testDocument2, "pkcs7");
            String outputCyril    =  testManager.extractElementFromXmlDoc(testDocument3, "pkcs7");
            
            assertEquals(defaultSignatureAnderson7, outputAnderson);
            assertEquals(defaultSignatureBobaFet7, outputBobaFet);
            assertEquals(defaultSignatureCyril7, outputCyril);
            
            assertThat(defaultSignatureAnderson7, not(outputCyril));
            assertThat(defaultSignatureAnderson7, not(outputBobaFet));
            assertThat(defaultSignatureCyril7, not(outputBobaFet));
            
            
        } catch (Exception e) {
            fail(e.getMessage());
        }
                
    }

    /**
     * Test of signPKCS7 method, of class SignerImpl.
     */
    @Test
    public void testSignPKCS7_4args() throws Exception {
        docInit();
        configurationSetUp();
        String data = "data";
        String dataToSign = "ok";
        String profile = "ceskaposta_01";
        try {
            testDocument1 = signer.signPKCS7(null, andersonDefPass, data, profile);
            testDocument2 = signer.signPKCS7(anderson, null, data, profile);
            testDocument3 = signer.signPKCS7(anderson, andersonDefPass, null, profile);
            testDocument4 = signer.signPKCS7(anderson, andersonDefPass, data, null);
            testDocument5 = signer.signPKCS7(null, null, data, profile);
            testDocument6 = signer.signPKCS7(null, andersonDefPass, null, profile);
            testDocument7 = signer.signPKCS7(null, andersonDefPass, data, null);
            testDocument8 = signer.signPKCS7(anderson, null, null, profile);
            testDocument9 = signer.signPKCS7(anderson, null, data, null);
            testDocument10 = signer.signPKCS7(anderson, andersonDefPass, null, null);
            testDocument11 = signer.signPKCS7(null, null, null, profile);
            testDocument12 = signer.signPKCS7(null, null, data, null);
            testDocument13 = signer.signPKCS7(anderson, null, null, null);
            testDocument14 = signer.signPKCS7(null, null, null, null);
            
                    
        } catch (NullPointerException e) {
            fail("Uncaught NullPointerException");
        }
        Document[] testSubjects = new Document[]{testDocument1,testDocument2,testDocument3,testDocument4,testDocument5,
            testDocument6, testDocument7, testDocument8, testDocument9, testDocument10, testDocument11, testDocument12,
            testDocument13, testDocument14};
            
            if ( 0 != testManager.chceckDocuments(testSubjects, 0, 0)){
                fail("some documents were signed with null data");
            }
            
            
            try {
                testDocument1 = signer.signPKCS7(anderson, bobaFetDefPass,dataToSign, profile);
                testDocument2 = signer.signPKCS7(bobaFet, bobaFetDefPass,dataToSign, profile);
                testDocument3 = signer.signPKCS7(cyril, andersonDefPass,dataToSign, profile);
                testDocument4 = signer.signPKCS7(bobaFet, bobaFetDefPass,dataToSign, "unknown");
                
                
                assertNull(testManager.extractElementFromXmlDoc(testDocument1, "pkcs7"));
                assertNull(testManager.extractElementFromXmlDoc(testDocument2, "pkcs7"));
                assertNull(testManager.extractElementFromXmlDoc(testDocument3, "pkcs7"));
                assertNull(testManager.extractElementFromXmlDoc(testDocument4, "pkcs7"));
                
        } catch (Exception e) {
            
            
        }
        try {
            testDocument1 = signer.signPKCS7(anderson, andersonDefPass,dataToSign, "cnb_01");
            testDocument2 = signer.signPKCS7(bobaFet, bobaFetDefPass,dataToSign, "ceskaposta_01");
            testDocument3 = signer.signPKCS7(anderson, cyrilDefPass,dataToSign, "cnb_01");
            
            String outputAnderson =  testManager.extractElementFromXmlDoc(testDocument1, "pkcs7");
            String outputBobaFet  =  testManager.extractElementFromXmlDoc(testDocument2, "pkcs7");
            String outputCyril    =  testManager.extractElementFromXmlDoc(testDocument3, "pkcs7");
            
            assertEquals(defaultSignatureAnderson7, outputAnderson);
            assertEquals(defaultSignatureBobaFet7, outputBobaFet);
            assertEquals(defaultSignatureCyril7, outputCyril);
            
            assertThat(defaultSignatureAnderson7, not(outputCyril));
            assertThat(defaultSignatureAnderson7, not(outputBobaFet));
            assertThat(defaultSignatureCyril7, not(outputBobaFet));
            
            
        } catch (Exception e) {
            fail(e.getMessage());
        }
            
            
            
            
            
        
    }

    /**
     * Test of signPdf method, of class SignerImpl.
     */
    @Test
    public void testSignPdf_4args() throws Exception {
        docInit();
        configurationSetUp();
        
        signer.prepareWaterMark();                
        byte[] emptypdf = testManager.loadFileBytes(emptyPdf);
        byte[] testData = testManager.loadFileBytes(pdf);
        byte[] notPdf = testManager.loadFileBytes(notAPdf);
        
        try {
                testDocument1 = signer.signPdf(null, andersonDefPass, emptypdf, andersonCerId);
                testDocument2 = signer.signPdf(anderson, null, emptypdf, andersonCerId);
                testDocument3 = signer.signPdf(anderson, andersonDefPass, null, andersonCerId);
                testDocument4 = signer.signPdf(null, null, emptypdf, andersonCerId);
                testDocument5 = signer.signPdf(anderson, null, null, andersonCerId);
                testDocument6 = signer.signPdf(null, null, null, andersonCerId);
        } catch (NullPointerException e) {
            //fail("uncaught nullpointerexception was thrown");
            
        }
        Document[] testSubjects = new Document[]{testDocument1,testDocument2,testDocument3,testDocument4,testDocument5,
            testDocument6};
            
            if ( 0 != testManager.chceckDocuments(testSubjects, 0, 0)){
                fail("some documents were signed with null data");
            }
        
        
        try {
                testDocument1 = signer.signPdf(anderson, bobaFetDefPass, emptypdf, andersonCerId);
                testDocument2 = signer.signPdf(anderson, andersonDefPass, emptypdf, bobaFetCerId);
                testDocument3 = signer.signPdf(bobaFet, andersonDefPass, emptypdf, andersonCerId);
                testDocument4 = signer.signPdf(anderson, bobaFetDefPass, emptypdf, bobaFetCerId);
                testDocument5 = signer.signPdf(anderson, andersonDefPass, notPdf, andersonCerId);
                assertNull(testManager.extractElementFromXmlDoc(testDocument1, "signature"));
                assertNull(testManager.extractElementFromXmlDoc(testDocument2, "signature"));
                assertNull(testManager.extractElementFromXmlDoc(testDocument3, "signature"));
                assertNull(testManager.extractElementFromXmlDoc(testDocument4, "signature"));
                assertNull(testManager.extractElementFromXmlDoc(testDocument5, "signature"));
        } catch (Exception e) {
        }
        
        testDocument1 = signer.signPdf(anderson, andersonDefPass, testData, andersonCerId);
        String signatureA = testManager.extractElementFromXmlDoc(testDocument1, "signature");
                
        testDocument2 = signer.signPdf(bobaFet, bobaFetDefPass, emptypdf, bobaFetCerId);
        String signatureB = testManager.extractElementFromXmlDoc(testDocument2, "signature");
        
        testDocument3 = signer.signPdf(cyril, cyrilDefPass, testData, cyrilCerId);
        String signatureC = testManager.extractElementFromXmlDoc(testDocument2, "signature");
        
        if (signatureA == null ){
            fail("Signature wasn't created with anderson cer on " + pdf );
        }
        if (signatureB == null ){
            fail("Signature wasn't created with bobaFet cer on " + emptyPdf );
        }
        if (signatureC == null ){
            fail("Signature wasn't created with Cyril cer on " + pdf);
        }
        
        
    }
    
    
    /**
     * Test of signPdf method, of class SignerImpl.
     */
    @Test
    public void testSignPdf_3args() throws Exception {
        docInit();
        configurationSetUp();
        signer.prepareWaterMark();       
        
        byte[] emptypdf = testManager.loadFileBytes(emptyPdf);
        byte[] testData = testManager.loadFileBytes(pdf);
        byte[] notPdf = testManager.loadFileBytes(notAPdf);
        try {
            testDocument1 = signer.signPdf(null, andersonDefPass, emptypdf);
            testDocument2 = signer.signPdf(anderson, null, emptypdf);
            testDocument3 = signer.signPdf(anderson, andersonDefPass, null);
            testDocument4 = signer.signPdf(null, null, emptypdf);
            testDocument5 = signer.signPdf(anderson, null, null);
            testDocument6 = signer.signPdf(null, andersonDefPass, null);
            testDocument7 = signer.signPdf(null, null, null);
        } catch (NullPointerException e) {
            fail("Uncaught nullpointerException");
        }
        Document[] testSubjects = new Document[]{testDocument1,testDocument2,testDocument3,testDocument4,testDocument5,
            testDocument6, testDocument7};
            if ( 0 != testManager.chceckDocuments(testSubjects, 0, 0)){
                fail("some documents were signed with null data");
            }
        
        try {
            docInit();
            testDocument1 = signer.signPdf(cyril, andersonDefPass, testData);
            testDocument2 = signer.signPdf(anderson, bobaFetDefPass, testData);
            testDocument3 = signer.signPdf(anderson, andersonDefPass, notPdf);
            testDocument4 = signer.signPdf(anderson, bobaFetDefPass, notPdf);
            assertNull(testManager.extractElementFromXmlDoc(testDocument1, "signature"));
            assertNull(testManager.extractElementFromXmlDoc(testDocument2, "signature"));
            assertNull(testManager.extractElementFromXmlDoc(testDocument3, "signature"));
            assertNull(testManager.extractElementFromXmlDoc(testDocument4, "signature"));
            
        } catch (Exception e) {
        }
        testDocument1 = signer.signPdf(anderson, andersonDefPass, testData);
        String signatureA = testManager.extractElementFromXmlDoc(testDocument1, "signature");
                
        testDocument2 = signer.signPdf(bobaFet, bobaFetDefPass, emptypdf);
        String signatureB = testManager.extractElementFromXmlDoc(testDocument2, "signature");
        
        testDocument3 = signer.signPdf(cyril, cyrilDefPass, testData);
        String signatureC = testManager.extractElementFromXmlDoc(testDocument2, "signature");
        
        if (signatureA == null ){
            fail("Signature wasn't created with anderson cer on " + pdf );
        }
        if (signatureB == null ){
            fail("Signature wasn't created with bobaFet cer on " + emptyPdf );
        }
        if (signatureC == null ){
            fail("Signature wasn't created with Cyril cer on " + pdf);
        }
    }

    /**
     * Test of createSignature method, of class SignerImpl.
     */
    @Test
    public void testCreateSignature() throws Exception {
        docInit();
        configurationSetUp();
        PrivateKey privateKey =  testManager.loadPrivateKey(privateKeyFileName);
        
        try {
            byte[] sig1 = signer.createSignature(privateKey, null);
            byte[] sig2 = signer.createSignature(null, "something");
            byte[] sig3 = signer.createSignature(null, null);
            if ((sig1 != sig2) != (sig3 != null)) {
                fail("values with null passed");
            }
        } catch (NullPointerException e) {
//             fail("Uncaught NullPointerException was thrown");
        }  
        byte[] a = signer.createSignature(privateKey, "abcde");
        byte[] b = signer.createSignature(privateKey, "abcde");
        if (a == null || b == null) {
            fail("signature wasnt created");
        }
    }
    public void docInit() {
        testDocument1 = null;
        testDocument2 = null;
        testDocument3 = null;
        testDocument4 = null;
        testDocument5 = null;
        testDocument6 = null;
        testDocument7 = null;
        testDocument8 = null;
        testDocument9 = null;
        testDocument10 = null;
        testDocument11 = null;
        testDocument12 = null;
        testDocument13 = null;
        testDocument14 = null;
        testDocument15 = null;
        testDocument16 = null;
        testDocument17 = null;
        testDocument18 = null;
    }
}
