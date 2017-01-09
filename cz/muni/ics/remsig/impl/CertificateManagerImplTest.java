/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.ics.remsig.impl;

import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_EXPIRED;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_NOT_YET_VALID;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_PASSWORD_RESET;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_REVOKED;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_SUSPENDED;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_VALID;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dbunit.DBTestCase;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Document;
import javax.xml.transform.TransformerException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.springframework.beans.BeansException;


/**
 *
 * @author Miroslav
 */
public class CertificateManagerImplTest extends DBTestCase {

    private CertificateManagerImpl manager;
    private Properties configuration;
    public static final String CONFIG_FILE = TestManager.CONFIG_FILE;    
    private final static org.slf4j.Logger logger
            = LoggerFactory.getLogger(CertificateManagerImplTest.class);
    private IDatabaseTester databaseTester;
    private IDatabaseConnection dbUnitConnection;
    private Connection connection;
    private java.sql.Statement statement;
    
    Properties config = TestManager.prepareConfigFile(TestManager.CONFIG_FILE_TEST);
    final String pathTotestFilesDirectory = config.getProperty("testFilesDirectory");
    final String serverAddress = config.getProperty("serverAddress");
    final String p12KeyFile = config.getProperty("pathToP12Keystore");
    final String p12KeyPassword = config.getProperty("p12Pass");
    final String defaultKeystore = config.getProperty("pathDefaultKeystore");
    
    final String dbDriverClass = config.getProperty("dbDriverClass");
    final String dbConnectionUrl = config.getProperty("dbConnectionUrl");
    final String dbUserName = config.getProperty("dbUserName");
    final String dbPassword =  config.getProperty("dbPass");
    final String applicationContext = config.getProperty("applicationContextPath");
    
    final String Cert1P12FilePath = config.getProperty("testCert1P12FilePath");
    final String Cert2P12FilePath = config.getProperty("testCert2P12FilePath");
    final String Cert3P12FilePath = config.getProperty("testCert3P12FilePath");
    final String Cert4P12FilePath = config.getProperty("testCert4P12FilePath");
    final String Cert5P12FilePath = config.getProperty("testCert5P12FilePath");
    final String Cert6P12FilePath = config.getProperty("testCert6P12FilePath");
    final String Cert1P12Pass = config.getProperty("testCert1P12Pass");
    final String Cert2P12Pass = config.getProperty("testCert2P12Pass");
    final String Cert3P12Pass = config.getProperty("testCert3P12Pass");
    final String Cert4P12Pass = config.getProperty("testCert4P12Pass");
    final String Cert5P12Pass = config.getProperty("testCert5P12Pass");
    final String Cert6P12Pass = config.getProperty("testCert6P12Pass");
    
    
    TestManager testManager  = new TestManager();

    org.w3c.dom.Document testDocument1 = null;
    org.w3c.dom.Document testDocument2 = null;
    org.w3c.dom.Document testDocument3 = null;
    org.w3c.dom.Document testDocument4 = null;
    org.w3c.dom.Document testDocument5 = null;
    org.w3c.dom.Document testDocument6 = null;
    org.w3c.dom.Document testDocument7 = null;
    org.w3c.dom.Document testDocument8 = null;
    org.w3c.dom.Document testDocument9 = null;
    
    
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
    
    int andersonCerId = 0;
    int bobaFetCerId = 0 ;
    int cyrilCerId = 0 ;
    

    private String initXmlDoc = config.getProperty("initialDatabaseInXml"); // dataset
    
    private String workingDatabase = config.getProperty("workingDatabaseInXml");
    private ITable expectedTable = null;
    @Override
    protected IDataSet getDataSet() throws Exception {

        return new FlatXmlDataSetBuilder().build(new FileInputStream(initXmlDoc));

    }
    

    public CertificateManagerImplTest() {

    }

    public CertificateManagerImplTest(String name) {
        super(name);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS,
                dbDriverClass); // in case of trouble this need to be changedcom.mysql.jdbc.Driver

        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL,
                dbConnectionUrl);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME,
                dbUserName);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD,
                dbPassword);           
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    @Before
    @Override
    public void setUp() throws Exception {
        

        getSetUpOperation();

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
        
        databaseTester = new JdbcDatabaseTester(dbDriverClass,
                dbConnectionUrl, dbUserName, dbPassword){
            @Override
            public IDatabaseConnection getConnection() throws Exception {
                IDatabaseConnection connection = super.getConnection();
                connection.getConfig().setProperty(
                        DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                        new MySqlDataTypeFactory());
                return connection;
            }
        };
                
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
    
    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
    }

    @After
    @Override
    public void tearDown() throws Exception {
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.onTearDown();
 

    }

   
    /**
     * Test of generateRequest method, of class CertificateManagerImpl.
     * @throws java.lang.Exception
     */
    @Test
    public void testGenerateRequest() throws Exception {
        docInit();        
        // one way to clear database 
        databaseTester.setSetUpOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.onSetup();
        
        configurationSetUp();

        Person person = new Person(5);
        String password = "weakpassword";
        int numberOfInsertedRecords = 5; 
        
        try {                                  
            testDocument5 = manager.generateRequest(person, password);
            testDocument1 = manager.generateRequest(new Person(83512), "K845F234J98");
            testDocument2 = manager.generateRequest(new Person(7), "mIKp443SDJI");
            testDocument3 = manager.generateRequest(new Person(6), password);
            testDocument4 = manager.generateRequest(person, password);
            try {
                manager.generateRequest(null, "somepass");
                manager.generateRequest(person, null);
                manager.generateRequest(null, null);
            } catch (NullPointerException e) {
                fail("UncaughtNullPointerException"+ e.getMessage());
            }

            ResultSet r = statement.executeQuery("SELECT COUNT(*) AS rowcount FROM credentials");
            r.next();
            int count = r.getInt("rowcount");

            assertEquals("Inserting into empty database failed either somerecords "
                    + "werent inserted or some null value were"
                    + "expected count" + numberOfInsertedRecords + "row value " + count, numberOfInsertedRecords, count);

            IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(workingDatabase));
            databaseTester.setDataSet(dataSet);
            databaseTester.setSetUpOperation(DatabaseOperation.REFRESH);
            databaseTester.onSetup();
            r = statement.executeQuery("SELECT COUNT(*) AS newRowCount FROM credentials");
            r.next();
            int finalRowCount = r.getInt("newRowCount") + numberOfInsertedRecords;
            // inserting additional request
            
            testDocument5 = manager.generateRequest(person, "greatPass");
            testDocument6 = manager.generateRequest(new Person(83512), "K845F234J98");
            testDocument7 = manager.generateRequest(new Person(8), "mIKp443SDJI");
            manager.generateRequest(new Person(9), "greatPass");
            manager.generateRequest(person, "greatPass");
            try {
                manager.generateRequest(null, "somepass");
                manager.generateRequest(person, null);
                manager.generateRequest(null, null);
            } catch (NullPointerException e) {
            }
            r = statement.executeQuery("SELECT COUNT(*) AS rowcount FROM credentials");
            r.next();
            count = r.getInt("rowcount");
            assertEquals(finalRowCount, count);
            r = statement.executeQuery("SELECT userID,private_key,request,pubkey_hash,salt, key_backup"
                    + " FROM credentials");
                    
            String[] dataBaseEntry = new String[]{"userID", "private_key",
                "pubkey_hash", "salt", "key_backup"};
            testManager.chcekNullValuesOfDatabase(r, dataBaseEntry);
            r.close();

        } catch (RemSigException ex) {
            System.out.println("remsign exception was thrown while generating request" + ex);
        }
    }
    @Test
    public void testImportPKCS12() throws Exception {
        docInit();
                
        configurationSetUp();        
        /**
         * Standard for loading p12 files 
         * In order to test multiple p12 certificates it is nessacery to set proper 
         * name and pass to certificates they are not included in any files
         * 
         */        
            // 1 and 2 are the same certificates saved in differnt location 
        String p12Certificate1 = testManager.loadPKCS12(Cert1P12FilePath);
        String p12Certificate2 = testManager.loadPKCS12(Cert2P12FilePath);
        
        // In case of multiple certificates to test
        
        String p12Certificate3 = testManager.loadPKCS12(Cert3P12FilePath);
        String p12Certificate4 = testManager.loadPKCS12(Cert4P12FilePath);
        String p12Certificate5 = testManager.loadPKCS12(Cert5P12FilePath);
        String p12Certificate6 = testManager.loadPKCS12(Cert6P12FilePath);
        
        String p12passToCer3 = Cert3P12Pass;
        String p12passToCer4 = Cert4P12Pass;
        String p12passToCer5 = Cert5P12Pass;
        String p12passToCer6 = Cert6P12Pass;
        
        String p12passToCer1 = Cert1P12Pass;
        String p12passToCer2 = Cert2P12Pass;

        /** 
         * Test with one working certificate 
         */
        testDocument1 =  manager.importPKCS12(anderson, p12Certificate1,
                andersonDefPass, p12passToCer1);
        testDocument2 =  manager.importPKCS12(bobaFet, p12Certificate2,
                bobaFetDefPass, p12passToCer2);
        
        int doc1 = Integer.parseInt(testManager.extractElementFromXmlDoc(
                testDocument1, "requestId"));
        int doc2 = Integer.parseInt(testManager.extractElementFromXmlDoc(
                testDocument2, "requestId"));
        
        
        ResultSet r = statement.executeQuery("SELECT userId, certificate, private_key,"
                + " dn, serial, issuer, expiration_from, expiration_to, pubkey_hash,"
                + " key_backup, salt, chain FROM credentials WHERE (userId = 1 AND"
                + " id = "+ doc1 +") OR (userId = 2 AND id = "+ doc2 +") ");

        String[] dataBaseEntry = new String[]{"userId", "certificate",
            "private_key", "dn", "serial", "issuer", "expiration_from",
            "expiration_to", "pubkey_hash", "key_backup", "salt","chain"};
        testManager.chcekNullValuesOfDatabase(r, dataBaseEntry);
        r.close();

        try {
                manager.importPKCS12(anderson, p12passToCer2, null, p12passToCer2);
                manager.importPKCS12(null, p12passToCer2, andersonDefPass, p12passToCer2);
                manager.importPKCS12(anderson, null, andersonDefPass, p12passToCer2);
                manager.importPKCS12(anderson, p12passToCer2, andersonDefPass, p12passToCer2);
                manager.importPKCS12(anderson, p12passToCer2, andersonDefPass, null);
                manager.importPKCS12(null, p12passToCer2, null, p12passToCer2);
                manager.importPKCS12(anderson, null, andersonDefPass, null);
                manager.importPKCS12(null, null, null, null);
        } catch (Exception e) {
            fail("Uncaucght nullPointerException" +e);
        }
            
        ResultSet r1 = statement.executeQuery("SELECT certificate,dn, serial, issuer,"
                                    + "expiration_from, expiration_to"
                                    + " FROM credentials WHERE (userId = 1 AND id = "
                + doc1 +") OR (userId = 2 AND id = "+ doc2 +") ");

        String[] dataBaseEntry1 = new String[]{"certificate", "dn", "serial", "issuer",
                            "expiration_from", "expiration_to"};
        testManager.chcekValuesOfTwoDatabaseEntries(r1, dataBaseEntry1);
        r1.close();
        
        testDocument3 = manager.importPKCS12(cyril, p12Certificate3, cyrilDefPass, p12passToCer3);
        testDocument4 =  manager.importPKCS12(daryl, p12Certificate4, darylDefPass, p12passToCer4);
        testDocument5 =  manager.importPKCS12(eva, p12Certificate5, evaDefPass, p12passToCer5);
        testDocument6 =  manager.importPKCS12(frank, p12Certificate6, frankDefPass, p12passToCer6);
        
        int doc3 = Integer.parseInt(testManager.extractElementFromXmlDoc(testDocument3, "requestId"));
        int doc4 = Integer.parseInt(testManager.extractElementFromXmlDoc(testDocument4, "requestId"));
        int doc5 = Integer.parseInt(testManager.extractElementFromXmlDoc(testDocument5, "requestId"));
        int doc6 = Integer.parseInt(testManager.extractElementFromXmlDoc(testDocument6, "requestId"));
        
        ResultSet r2= statement.executeQuery("SELECT userId, certificate, private_key, dn, serial, issuer,"
				+ "expiration_from, expiration_to, pubkey_hash, key_backup, salt,"
				+ "chain FROM credentials  WHERE (userId = 1 AND id = "+ doc1 +") OR (userId = 2 AND id = "+ doc2 +") OR "
                + "(userId = 3 AND id = "+ doc3 +") OR (userId = 4 AND id = "+ doc4 +") OR"
                + "(userId = 5 AND id = "+ doc5 +") OR (userId = 4 AND id = "+ doc4 +")");

        testManager.chcekNullValuesOfDatabase(r2, dataBaseEntry1);
        
        ResultSet r3= statement.executeQuery("SELECT certificate,dn, serial, issuer,"
                                    + "expiration_from, expiration_to"
                                    + " FROM credentials WHERE (userId = 1 AND id = "+ doc1 +") OR (userId = 2 AND id = "+ doc2 +") OR "
                + "(userId = 3 AND id = "+  doc3 +") OR (userId = 4 AND id = "+ doc4 +") OR"
                + "(userId = 5 AND id = "+ doc5 +") OR (userId = 4 AND id = "+ doc4 +")");

        String[] dataBaseEntry3 = new String[]{"certificate", "dn", "serial", "issuer",
                            "expiration_from", "expiration_to"};
        
        testManager.chcekNonEqualValuesOfDatabase(r3, dataBaseEntry3,5);
        r2.close();
        r3.close();
                
        try {
            testDocument1 =  manager.importPKCS12(anderson, p12Certificate1, bobaFetDefPass, p12passToCer1);
            testDocument1 =  manager.importPKCS12(anderson, "notfile", andersonDefPass, p12passToCer1);
            testDocument1 =  manager.importPKCS12(anderson, p12Certificate1, andersonDefPass, "wrongpass");
            testDocument1 =  manager.importPKCS12(bobaFet, p12Certificate1, andersonDefPass, p12passToCer1);
            testDocument1 =  manager.importPKCS12(anderson, "notfile", andersonDefPass, "wrongpass");
            testDocument1 =  manager.importPKCS12(anderson, "notfile", cyrilDefPass, "wrongpass");
            
        } catch (Exception e) {            
        }
    
    }
    
    /**
     * This test works under assumption that in database already exist 3 records 
     * of imported p12 certificates, Method generateXmlForSetUp() generates 
     * xml database entry necessary for this test
     * @throws Exception 
     */
    @Test
    public void testExportPKCS12() throws Exception    {
        docInit();        
        setUpId();
        configurationSetUp();        
      
        try {
            testDocument1 = manager.exportPKCS12(null, andersonCerId, andersonDefPass, Cert1P12Pass);
            testDocument2 = manager.exportPKCS12(anderson, andersonCerId, null, Cert1P12Pass);
            testDocument3 = manager.exportPKCS12(anderson, andersonCerId, andersonDefPass, null);
            testDocument4 = manager.exportPKCS12(null, andersonCerId, null, Cert1P12Pass);
            testDocument5 = manager.exportPKCS12(null, andersonCerId, andersonDefPass, null);
            testDocument6 = manager.exportPKCS12(anderson, andersonCerId, null, null);
            testDocument7 = manager.exportPKCS12(null, andersonCerId, null, null);
            Document[] allDoc = new Document[]{testDocument1,testDocument2,testDocument3, testDocument4, testDocument5, testDocument6,testDocument7};
            if (0 != testManager.chceckDocuments(allDoc, 0, 0))
            {
                fail("some key was uploaded with null values");
            }
        } catch (NullPointerException e) {
            fail("Uncaught nullPointerException");
        }
        
        try {
            testDocument1 = manager.exportPKCS12(anderson, andersonCerId, andersonDefPass, Cert1P12Pass);
            testDocument2 = manager.exportPKCS12(anderson, bobaFetCerId, andersonDefPass, Cert1P12Pass);
            testDocument3 = manager.exportPKCS12(anderson, andersonCerId, cyrilDefPass, Cert1P12Pass);
            testDocument4 = manager.exportPKCS12(anderson, andersonCerId, andersonDefPass, Cert3P12Pass);
            testDocument5 = manager.exportPKCS12(bobaFet, bobaFetCerId, andersonDefPass, Cert1P12Pass);
            testDocument6 = manager.exportPKCS12(anderson, andersonCerId, cyrilDefPass, Cert3P12Pass);            
            testDocument7 = manager.exportPKCS12(bobaFet, cyrilCerId, cyrilDefPass, Cert3P12Pass);
            assertNull(testManager.extractElementFromXmlDoc(testDocument1, "pkcs12"));
            assertNull(testManager.extractElementFromXmlDoc(testDocument2, "pkcs12"));
            assertNull(testManager.extractElementFromXmlDoc(testDocument3, "pkcs12"));
            assertNull(testManager.extractElementFromXmlDoc(testDocument4, "pkcs12"));
            assertNull(testManager.extractElementFromXmlDoc(testDocument5, "pkcs12"));
            assertNull(testManager.extractElementFromXmlDoc(testDocument6, "pkcs12"));
            assertNull(testManager.extractElementFromXmlDoc(testDocument7, "pkcs12"));
            
        } catch (Exception e) {
            
        }
        
        try {
            testDocument1 = manager.exportPKCS12(anderson, andersonCerId, andersonDefPass, Cert1P12Pass);
            testDocument2 = manager.exportPKCS12(bobaFet, bobaFetCerId, bobaFetDefPass, Cert2P12Pass);
            testDocument3 = manager.exportPKCS12(cyril, cyrilCerId, cyrilDefPass, Cert3P12Pass);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        try {
            testManager.exportDocIntoXml(config.getProperty("testOutputxml1"), testDocument1);
            testManager.exportDocIntoXml(config.getProperty("testOutputxml2"), testDocument2);
            testManager.exportDocIntoXml(config.getProperty("testOutputxml3"), testDocument3);
        
            databaseTester.setSetUpOperation(DatabaseOperation.DELETE_ALL);
            databaseTester.onSetup();

            } catch (Exception ex) {
                fail(ex.getMessage());
            }

        String doc1p12 = testManager.extractElementFromXmlDoc(testDocument1, "pkcs12");
        String doc2p12 = testManager.extractElementFromXmlDoc(testDocument2, "pkcs12");
        String doc3p12 = testManager.extractElementFromXmlDoc(testDocument3, "pkcs12");

        assertNotNull(doc1p12);
        assertNotNull(doc2p12);
        assertNotNull(doc3p12);
    }
    
    @Test
    public void testCheckPassword() throws Exception {
        docInit();
        setUpId();        
        configurationSetUp();
        
        try {
            manager.checkPassword(anderson, andersonCerId, andersonDefPass);
            manager.checkPassword(bobaFet, bobaFetCerId, bobaFetDefPass);
            manager.checkPassword(cyril, cyrilCerId, cyrilDefPass);
            try {
                    manager.checkPassword(anderson, andersonCerId, darylDefPass);
                    manager.checkPassword(anderson, andersonCerId, bobaFetDefPass);
                    manager.checkPassword(anderson, andersonCerId, cyrilDefPass);
                    manager.checkPassword(bobaFet, bobaFetCerId, andersonDefPass);
                    manager.checkPassword(bobaFet, bobaFetCerId, darylDefPass);
                    manager.checkPassword(bobaFet, bobaFetCerId, cyrilDefPass);
                    manager.checkPassword(cyril, cyrilCerId, andersonDefPass);
                    manager.checkPassword(cyril, cyrilCerId, bobaFetDefPass);
                    manager.checkPassword(cyril, cyrilCerId, darylDefPass);                    
                    fail("Password that werent same passed");
            } catch (RemSigException e) {
                
            }
            
        } catch (Exception e) {
            fail("password that was supposed to be same didnt passed ");
        }
        try {
            manager.checkPassword(anderson, andersonCerId, null);
            manager.checkPassword(null, andersonCerId, andersonDefPass);
            manager.checkPassword(null, andersonCerId, andersonDefPass);
          
        } catch (NullPointerException e) {
//            fail("Uncaught null pointer exception");
        }
    }

    /**
     * This test is dependent on working of method check password
     * Test of changePassword method, of class CertificateManagerImpl.
     * @throws java.lang.Exception
     */
    @Test
    public void testChangePassword() throws Exception {
        docInit();
        setUpId();
        configurationSetUp();        
        try {
            manager.changePassword(eva, andersonCerId, andersonDefPass, andersonDefPass);
            manager.changePassword(anderson, bobaFetCerId, andersonDefPass, andersonDefPass);
            manager.changePassword(anderson, andersonCerId, igorDefPass, igorDefPass);
            manager.changePassword(anderson, 0, andersonDefPass, igorDefPass);

            manager.changePassword(bobaFet, bobaFetCerId, bobaFetDefPass, gregorDefPass);
            manager.changePassword(bobaFet, cyrilCerId, bobaFetDefPass, gregorDefPass);
            manager.changePassword(bobaFet, bobaFetCerId, cyrilDefPass, gregorDefPass);
            fail("Some problem with autentication passwords that wasnt supposed to match "
                    + "or id passed autentication when it was expected to fail");
        } catch (Exception e) {
        }
        try {
            manager.changePassword(null, andersonCerId, andersonDefPass, andersonDefPass);
            manager.changePassword(anderson, bobaFetCerId, andersonDefPass, andersonDefPass);
            manager.changePassword(anderson, andersonCerId, null, igorDefPass);
            manager.changePassword(anderson, 0, andersonDefPass, igorDefPass);
            manager.changePassword(null, bobaFetCerId, null, gregorDefPass);
            manager.changePassword(null, cyrilCerId, null, null);
            manager.changePassword(null, 0, null, null);
             
        } catch (NullPointerException e) {
            fail("nullPointer exception was not caught"+e);
        }
        try {
            manager.changePassword(anderson, andersonCerId, andersonDefPass, igorDefPass);
            manager.changePassword(bobaFet, bobaFetCerId, bobaFetDefPass, gregorDefPass);

            manager.checkPassword(anderson, andersonCerId, igorDefPass);
            manager.checkPassword(bobaFet, bobaFetCerId, gregorDefPass);
            manager.checkPassword(cyril, cyrilCerId, cyrilDefPass);
        } catch (RemSigException e) {
            fail(e.getMessage());
        }
    }

    /**
     * 
     * Test of resetPassword method, of class CertificateManagerImpl.
     */
    @Test
    public void testResetPassword() throws Exception {
        docInit();
        setUpId();
        configurationSetUp();
        String outputFileResetPass = config.getProperty("testResetpass");
        try {
             manager.resetPassword(null, andersonCerId);
             
        } catch (NullPointerException e) {
            fail("Uncaught nullPointerException");
            
        }
        try {
            manager.checkPassword(anderson, andersonCerId, andersonDefPass);
            testDocument2 = manager.resetPassword(anderson, andersonCerId);
            testManager.exportDocIntoXml(outputFileResetPass, testDocument2);
            
            manager.checkPassword(anderson, andersonCerId, andersonDefPass);
            fail("password didnt change");
        } catch (RemSigException | TransformerException | NullPointerException e) {
        }
        
        
        try {
            manager.checkPassword(anderson, andersonCerId, andersonDefPass);
            testDocument1 = manager.resetPassword(anderson, andersonCerId);
            testManager.exportDocIntoXml(outputFileResetPass, testDocument1);
            String newPass = testManager.extractElementFromXmlDoc(testDocument1, "password");
            manager.checkPassword(anderson, andersonCerId, newPass);
            
            manager.checkPassword(anderson, andersonCerId, andersonDefPass);
        } catch (Exception e) {
            fail("failed to reset password ");
        }
        
    }

    private void configurationSetUp() throws BeansException {
        ApplicationContext ac = new ClassPathXmlApplicationContext(applicationContext);
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());
    }
   
     @Test
    public void testChangeCertificateStatus() throws Exception {
        docInit();
        setUpId();
        configurationSetUp();   
        
        String rule = "userId ="+bobaFet.getId()+" AND id = "+bobaFetCerId+"";
        int temp = -1;
        String test = "a";
        try {
             if ( null != (testManager.extractOneCollumFromDatabase("state", "credentials", rule)))
                     {
                         fail("inicial status should be null");
                     }
            manager.changeCertificateStatus(null, bobaFetCerId, STATE_SUSPENDED);
        } catch (NullPointerException e) {
            fail("unCaught nullPointerException"); 
        }

        // testing standard changing of the certificate status
        try {
            manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_SUSPENDED);
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_SUSPENDED);

            manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_VALID);
            test = testManager.extractOneCollumFromDatabase("state", "credentials", rule);
            assertNull(test);

            manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_SUSPENDED);
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_SUSPENDED);

            manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_EXPIRED);
            test = testManager.extractOneCollumFromDatabase("state", "credentials", rule);
            assertNull(test);

            manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_SUSPENDED);
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_SUSPENDED);

            manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_NOT_YET_VALID);
            test = testManager.extractOneCollumFromDatabase("state", "credentials", rule);
            assertNull(test);

             
        } catch (NullPointerException e) {
            fail("failed to change status as expected");
        }
        // testing if status does change after certificate is revoked
        try {
            manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_REVOKED);
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_REVOKED);

            manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_SUSPENDED);
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_REVOKED);

            manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_VALID);
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_REVOKED);


            manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_EXPIRED);
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_REVOKED);

            manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_PASSWORD_RESET);
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_REVOKED);             

        } catch (RemSigException | NumberFormatException | NullPointerException e) {
            fail("exception was thrown" +e);
        }
        temp = -1;

        rule = "userId ="+cyril.getId()+" AND id = "+cyrilCerId +"";
        if ( null != (testManager.extractOneCollumFromDatabase("state", "credentials", rule)))
               {
                   fail("inicial status should be null");
               }
        // testing if certificate status changes after password is reseted
        try {             
            manager.changeCertificateStatus(cyril, cyrilCerId, STATE_PASSWORD_RESET);                     
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_PASSWORD_RESET);

            manager.changeCertificateStatus(cyril, cyrilCerId, STATE_SUSPENDED);
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_PASSWORD_RESET);

            manager.changeCertificateStatus(cyril, cyrilCerId, STATE_VALID);
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_PASSWORD_RESET);


            manager.changeCertificateStatus(cyril, cyrilCerId, STATE_REVOKED);
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_PASSWORD_RESET);

            manager.changeCertificateStatus(cyril, cyrilCerId, STATE_NOT_YET_VALID); 
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_PASSWORD_RESET);             

            manager.changeCertificateStatus(cyril, cyrilCerId, STATE_EXPIRED); 
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, STATE_PASSWORD_RESET);

        } catch (RemSigException | NumberFormatException e) {
            fail("exception was thrown" +e);
        }
        // testing if certificate status doesnt change to something weird
        try {             
            int init = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            manager.changeCertificateStatus(cyril, cyrilCerId, 59);                     
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, init);

            manager.changeCertificateStatus(cyril, cyrilCerId, -6);
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, init);
            manager.changeCertificateStatus(cyril, cyrilCerId, Integer.MAX_VALUE+1);                     
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, init);
            manager.changeCertificateStatus(cyril, cyrilCerId, Integer.MIN_VALUE -1);                     
            temp = Integer.parseInt(testManager.extractOneCollumFromDatabase("state", "credentials", rule));
            assertEquals(temp, init);

        }catch(NumberFormatException | RemSigException e)
        {
        }        
    }
    
    @Test
    public void testListCertificatesWithStatus() throws Exception {
        docInit();
        setUpId();
        configurationSetUp();   
        
        try {
            manager.listCertificatesWithStatus(null, 1);
        } catch (NullPointerException e) {
            fail("unreported nullPpointerException");
        }
        
        statement.execute("UPDATE credentials SET state= null WHERE userId="+
                anderson.getId()+" AND id= "+andersonCerId);
        statement.execute("UPDATE credentials SET state= null, expiration_from = 1480075200"
                + " WHERE userId="+ bobaFet.getId()+" AND id= "+bobaFetCerId);
        statement.execute("UPDATE credentials SET state= null, expiration_to = 1453161600"
                + " WHERE userId="+ cyril.getId()+" AND id= "+cyrilCerId);
        
        testDocument1 = manager.listCertificatesWithStatus(anderson, STATE_VALID);
        testDocument2 = manager.listCertificatesWithStatus(bobaFet, STATE_NOT_YET_VALID );
        testDocument3 = manager.listCertificatesWithStatus(cyril, STATE_EXPIRED);
        testManager.exportDocIntoXml(config.getProperty("testOutputListCert1"), testDocument1);
        testManager.exportDocIntoXml(config.getProperty("testOutputListCert2"), testDocument2);
        testManager.exportDocIntoXml(config.getProperty("testOutputListCert3"), testDocument3);
        
        // chain and certificate is modified in extractMultipleColuumsFromDatabase there is 
        // extra line seperator at the end
        String[] xmlExtraction = new String[]{"dn","issuer","serialNumber",
                "expirationFrom","expirationTo","certificatePEM","chainPEM"}; 
        String[] databaseExtraction = new String[]{"dn","issuer","serial","expiration_From",
            "expiration_To","certificate","chain"}; 
        
        String ruleA = "id ="+andersonCerId+" AND userId ="+anderson.getId();
        String ruleB = "id ="+bobaFetCerId+" AND userId ="+bobaFet.getId();
        String ruleC = "id ="+cyrilCerId+" AND userId ="+cyril.getId();
        try {
            ArrayList<String> dataFromDoc = extractMultipleElementsFromDoc(xmlExtraction, testDocument1);
            ArrayList<String> dataFromDatabase = extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleA);

            for (int i = 0; i < dataFromDoc.size(); i++) {
                assertEquals("at element "+databaseExtraction[i] ,dataFromDoc.get(i), dataFromDatabase.get(i));
            }

            dataFromDoc = extractMultipleElementsFromDoc(xmlExtraction, testDocument2);
            dataFromDatabase = extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleB);

            for (int i = 0; i < dataFromDoc.size(); i++) {
                assertEquals("at element "+databaseExtraction[i] ,dataFromDoc.get(i), dataFromDatabase.get(i));
            }

            dataFromDoc = extractMultipleElementsFromDoc(xmlExtraction, testDocument3);
            dataFromDatabase = extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleC);

            for (int i = 0; i < dataFromDoc.size(); i++) {
                assertEquals("at element "+databaseExtraction[i] ,dataFromDoc.get(i), dataFromDatabase.get(i));
            }
            
        } catch (Exception e) {
            fail( e.getMessage());
        }
        
        try {
            statement.execute("UPDATE credentials SET state= "+STATE_REVOKED+" WHERE userId="+ anderson.getId()+" AND id= "+andersonCerId);
            statement.execute("UPDATE credentials SET state= "+STATE_SUSPENDED+", expiration_from = 1453161600 WHERE userId="+ bobaFet.getId()+" AND id= "+bobaFetCerId);
            statement.execute("UPDATE credentials SET state= "+STATE_PASSWORD_RESET+", expiration_to = 1480075200 WHERE userId="+ cyril.getId()+" AND id= "+cyrilCerId);
            testDocument4 = manager.listCertificatesWithStatus(anderson, STATE_REVOKED);
            testDocument5 = manager.listCertificatesWithStatus(bobaFet, STATE_SUSPENDED );
            testDocument6 = manager.listCertificatesWithStatus(cyril, STATE_PASSWORD_RESET);
            
            
            ArrayList<String> dataFromDoc = extractMultipleElementsFromDoc(xmlExtraction, testDocument4);
            ArrayList<String> dataFromDatabase = extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleA);

            for (int i = 0; i < dataFromDoc.size(); i++) {
                assertEquals("at element "+databaseExtraction[i] ,dataFromDoc.get(i), dataFromDatabase.get(i));
            }

            dataFromDoc = extractMultipleElementsFromDoc(xmlExtraction, testDocument5);
            dataFromDatabase = extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleB);

            for (int i = 0; i < dataFromDoc.size(); i++) {
                assertEquals("at element "+databaseExtraction[i] ,dataFromDoc.get(i), dataFromDatabase.get(i));
            }

            dataFromDoc = extractMultipleElementsFromDoc(xmlExtraction, testDocument6);
            dataFromDatabase = extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleC);

            for (int i = 0; i < dataFromDoc.size(); i++) {
                assertEquals("at element "+databaseExtraction[i] ,dataFromDoc.get(i), dataFromDatabase.get(i));
            }
            
        } catch (SQLException | RemSigException e) {
            fail("there was error listing certificate " + e);
        }
    }
    /**
     *Removes blank space from end of inputfile
     * Right now is used for certificate and chain 
     * @param input file to be removed
     * @return input - one lineSeparator 
     */
    public String changeEndCertificate(String input){
        String toBefound = "END CERTIFICATE-----"+System.lineSeparator();
        String replaceWith = "END CERTIFICATE-----";
        
        return input.replace(toBefound, replaceWith);        
    }
    /**
     * Takes string array and extract
     * @param elements
     * @param doc
     * @return 
     */
    public ArrayList<String> extractMultipleElementsFromDoc(String[] elements, Document doc){
        ArrayList<String> result = new ArrayList<>();
        for (String element : elements) {
            result.add(testManager.extractElementFromXmlDoc(doc, element));
        }        
        return result;
    }
    public ArrayList<String> extractMultipleCollumsFromDatabase(String[] dataToBeExtracted,String databaseName,String rule)
    {
        ArrayList<String> result = new ArrayList<>();
        String temp = null;
        for (String  rowName : dataToBeExtracted ) {
             try {
            ResultSet r = statement.executeQuery("SELECT " +rowName+""
                    + " FROM "+databaseName+" "
                    + "WHERE ("+rule+")");
            r.next();
            temp = r.getString(rowName);
                 if (rowName.equals("chain") || rowName.equals("certificate")) {
                     temp = changeEndCertificate(temp);
                 }
            result.add(temp);
        } catch (SQLException ex) {
            System.out.println("Failed to load data "+rowName+" from "+ databaseName+" ");
        }   
        }
        return result;
    }
    
    @Test
    public void testListAllCertificatesWithStatus() throws Exception {
        docInit();
        setUpId();
        configurationSetUp();
        String ruleA = "userId ="+anderson.getId() +" AND id = " + andersonCerId+"";
        String ruleB = "userId ="+bobaFet.getId() +" AND id = " + bobaFetCerId+"";
        String ruleC = "userId ="+cyril.getId() +" AND id = " + cyrilCerId+"";
        
        ArrayList<String> databaseAnderson ;
        ArrayList<String> databaseCyril ;
        ArrayList<String> databaseBobaFet ;
        
        
        String[] xmlExtraction = new String[]{"dn","issuer","serialNumber",
                "expirationFrom","expirationTo","certificatePEM","chainPEM"};
        String[] databaseExtraction = new String[]{"dn","issuer","serial",
                "expiration_From","expiration_To","certificate","chain"}; 
       
        databaseAnderson = testManager.extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleA);        
        databaseBobaFet = testManager.extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleB);
        databaseCyril = testManager.extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleC);

        statement.execute("UPDATE credentials SET state= "+STATE_REVOKED+" WHERE userId="+ anderson.getId()+" AND id= "+andersonCerId);
        statement.execute("UPDATE credentials SET state= "+STATE_SUSPENDED+" WHERE userId="+ bobaFet.getId()+" AND id= "+bobaFetCerId);
        statement.execute("UPDATE credentials SET state= "+STATE_REVOKED+" WHERE userId="+ cyril.getId()+" AND id= "+cyrilCerId);
        testDocument1 = manager.listAllCertificatesWithStatus(STATE_REVOKED);
        testDocument2 = manager.listAllCertificatesWithStatus(STATE_SUSPENDED);
        

        ArrayList<String[]> revokedData = testManager.parseXmlDoc(testDocument1, xmlExtraction);
        ArrayList<String[]> suspendedData = testManager.parseXmlDoc(testDocument2, xmlExtraction);


        if (revokedData.size() == 2) {
            databaseAnderson = testManager.extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleA);
            databaseCyril = testManager.extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleC);
            databaseBobaFet = testManager.extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleB);
            assertArrayEquals(databaseAnderson.toArray(), revokedData.get(0));
            assertArrayEquals(databaseCyril.toArray(), revokedData.get(1));
            assertArrayEquals(databaseBobaFet.toArray(), suspendedData.get(0));

        }
        statement.execute("UPDATE credentials SET state= "+"null"+" WHERE userId="+ anderson.getId()+" AND id= "+andersonCerId);
        statement.execute("UPDATE credentials SET state= "+"null"+" WHERE userId="+ bobaFet.getId()+" AND id= "+bobaFetCerId);
        statement.execute("UPDATE credentials SET state= "+"null"+" WHERE userId="+ cyril.getId()+" AND id= "+cyrilCerId);

        testDocument3 = manager.listAllCertificatesWithStatus(STATE_VALID);
        
        ArrayList<String[]> validData = testManager.parseXmlDoc(testDocument3, xmlExtraction);
        assertArrayEquals(databaseAnderson.toArray(), validData.get(0));
        assertArrayEquals(databaseBobaFet.toArray(), validData.get(1));
        assertArrayEquals(databaseCyril.toArray(), validData.get(2));

        statement.execute("UPDATE credentials SET state= "+STATE_PASSWORD_RESET+" WHERE userId="+ anderson.getId()+" AND id= "+andersonCerId);
        statement.execute("UPDATE credentials SET state= null, expiration_from = 1480075200 WHERE userId="+ bobaFet.getId()+" AND id= "+bobaFetCerId);
        statement.execute("UPDATE credentials SET state= null, expiration_to = 1453161600 WHERE userId="+ cyril.getId()+" AND id= "+cyrilCerId);

        testDocument4 = manager.listAllCertificatesWithStatus(STATE_PASSWORD_RESET);
        testDocument5 = manager.listAllCertificatesWithStatus(STATE_NOT_YET_VALID);
        testDocument6 = manager.listAllCertificatesWithStatus(STATE_EXPIRED);

        databaseAnderson = testManager.extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleA);        
        databaseBobaFet = testManager.extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleB);
        databaseCyril = testManager.extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleC);

        ArrayList<String[]> resetData = testManager.parseXmlDoc(testDocument4, xmlExtraction);
        ArrayList<String[]> notYetValidData = testManager.parseXmlDoc(testDocument5, xmlExtraction);
        ArrayList<String[]> expiredData = testManager.parseXmlDoc(testDocument6, xmlExtraction);

        assertArrayEquals(databaseAnderson.toArray(), resetData.get(0));
        assertArrayEquals(databaseBobaFet.toArray(), notYetValidData.get(0));
        assertArrayEquals(databaseCyril.toArray(), expiredData.get(0));
    }

    /**
     * Test of uploadPrivateKey method, of class CertificateManagerImpl.
     */
    @Test
    public void testUploadPrivateKey() throws Exception {
        docInit();
        setUpId();
        configurationSetUp();  
        
        byte[] differentData = new byte[]{4,2,3,4,1,23,4,1};        
        byte[] privateKeyA = testManager.loadFileBytes(config.getProperty("testPrivateKey1Path"));
        byte[] privateKeyB = testManager.loadFileBytes(config.getProperty("testPrivateKey2Path"));
        byte[] privateKeyC = testManager.loadFileBytes(config.getProperty("testPrivateKey3Path"));

        testDocument1 = manager.resetPassword(anderson, andersonCerId);
        byte[] passHashA = testManager.extractElementFromXmlDoc(testDocument1, "password").getBytes();

        testDocument2 = manager.resetPassword(bobaFet,bobaFetCerId);
        byte[] passHashB = testManager.extractElementFromXmlDoc(testDocument2, "password").getBytes();
        
        String ruleA = "userId ="+anderson.getId() +" AND id = " + andersonCerId+"";
        String ruleB = "userId ="+bobaFet.getId() +" AND id = " + bobaFetCerId+"";
        String ruleC = "userId ="+cyril.getId() +" AND id = " + cyrilCerId+"";
        
        try {
            testDocument3 = manager.uploadPrivateKey(null, anderson, cyrilCerId, differentData);
            testDocument4 = manager.uploadPrivateKey(differentData, anderson, cyrilCerId, null);
            testDocument5 = manager.uploadPrivateKey(null, anderson, cyrilCerId, null);
            testDocument6 = manager.uploadPrivateKey(differentData, null, cyrilCerId, differentData);
            testDocument7 =manager.uploadPrivateKey(null, null, cyrilCerId, differentData);
            testDocument8 =manager.uploadPrivateKey(null, null, cyrilCerId, null);
        } catch (NullPointerException e) {
            fail("uncaught null pointer exception");
        }
        Document[] allDoc = new Document[]{testDocument3, testDocument4, testDocument5, testDocument6,testDocument7,testDocument8};
        if (0 != testManager.chceckDocuments(allDoc, 0, 0))
        {
            fail("some key was uploaded with null values");
        }
        
        try {            
            
            String originalKey = testManager.extractOneCollumFromDatabase("private_key" , "credentials", ruleA);
            
            testDocument2 = manager.uploadPrivateKey(privateKeyA, anderson, cyrilCerId, passHashA);
            testDocument3 = manager.uploadPrivateKey(privateKeyA, anderson, andersonCerId, differentData);
            testDocument4 = manager.uploadPrivateKey(privateKeyA, anderson, bobaFetCerId, differentData);
            
            String newPrivateKey = testManager.extractOneCollumFromDatabase("private_key" , "credentials", ruleA);
            assertEquals(originalKey, newPrivateKey);
            
        } catch (RemSigException e) {
        }
        
            byte[] originalAnderson = testManager.extractOneCollumFromDatabase("private_key" , "credentials", ruleA).getBytes();
            byte[] originalBobaFet = testManager.extractOneCollumFromDatabase("private_key" , "credentials", ruleB).getBytes();
            byte[] originalCyril = testManager.extractOneCollumFromDatabase("private_key" , "credentials", ruleC).getBytes();
            
            testDocument5 = manager.uploadPrivateKey(privateKeyA, anderson, andersonCerId, passHashA);
            testDocument6 = manager.uploadPrivateKey(privateKeyB, bobaFet, bobaFetCerId, passHashB);
            testDocument7 = manager.uploadPrivateKey(privateKeyC, cyril, cyrilCerId, differentData);
            
            byte[] uploadedAnderson = testManager.extractOneCollumFromDatabase("private_key" , "credentials", ruleA).getBytes();
            byte[] uploadedBobaFet = testManager.extractOneCollumFromDatabase("private_key" , "credentials", ruleB).getBytes();
            byte[] uploadedCyril = testManager.extractOneCollumFromDatabase("private_key" , "credentials", ruleC).getBytes();
            try {
            assertThat("Key wasnt changed",originalAnderson, not(uploadedAnderson));
            assertThat("Key wasnt changed",originalBobaFet, not(uploadedBobaFet));
            assertThat("Key wasnt changed",originalCyril, is(uploadedCyril));
        } catch (Error e) {
            fail(e.getMessage());
        }
    }

    /**
     * Only testing expected outcome since nothing else can be properly tested
     * Test of stats method, of class CertificateManagerImpl.
     */
        
    @Test
    public void testStats() throws Exception {
        docInit();
        setUpId();
        configurationSetUp();   
        testDocument1 = manager.stats();        
        String[] xmlExtraction = new String[]{"userId","id","dn","issuer","serialNumber",
                "expirationFrom","expirationTo"};
        String[] dataToBeExtracted = new String[]{"userId","id","dn","issuer","serial","expiration_from",
            "expiration_to"}; 
        
        ArrayList<String[]> xmlData =  testManager.parseXmlDoc(testDocument1,xmlExtraction);
        
        String ruleA = "userId ="+anderson.getId() +" AND id = " + andersonCerId+"";
        String ruleB = "userId ="+bobaFet.getId()  +" AND id = "+bobaFetCerId+"";        
        String ruleC = "userId ="+cyril.getId()    +" AND id = "+cyrilCerId+"";
        
        ArrayList<String> databaseAndersonData = testManager.extractMultipleCollumsFromDatabase(dataToBeExtracted, "credentials", ruleA);
        ArrayList<String> databaseBobaFetData = testManager.extractMultipleCollumsFromDatabase(dataToBeExtracted, "credentials", ruleB);
        ArrayList<String> databaseCyrilData = testManager.extractMultipleCollumsFromDatabase(dataToBeExtracted, "credentials", ruleC);
        
        Object[] databaseAnderson = databaseAndersonData.toArray();
        Object[] databaseBobaFet = databaseBobaFetData.toArray();
        Object[] databaseCyril = databaseCyrilData.toArray();
        assertArrayEquals(databaseAnderson, xmlData.get(0));
        assertArrayEquals(databaseBobaFet, xmlData.get(1));
        assertArrayEquals(databaseCyril, xmlData.get(2));
        
    }
    @Test
    public void testImportCertificate() throws Exception {        
        docInit();
        setUpId();
        configurationSetUp();   
        
        String ruleA = "userId ="+anderson.getId() +" AND id = " + andersonCerId+"";
        String ruleB = "userId ="+bobaFet.getId()  +" AND id = "+bobaFetCerId+"";
        String ruleC = "userId ="+cyril.getId()    +" AND id = "+cyrilCerId+"";
        
        String certchainA = testManager.extractOneCollumFromDatabase("chain", "credentials", ruleA);
        String certchainB = testManager.extractOneCollumFromDatabase("chain", "credentials", ruleB);
        String certchainC = testManager.extractOneCollumFromDatabase("chain", "credentials", ruleC);        
        
        FileInputStream first  = new FileInputStream(config.getProperty("testCert1Pem"));
        FileInputStream second = new FileInputStream(config.getProperty("testCert2Pem"));
        FileInputStream third  = new FileInputStream(config.getProperty("testCert3Pem"));
        
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate firstCert = (X509Certificate) cf.generateCertificate(first);
        X509Certificate secondCert = (X509Certificate) cf.generateCertificate(second);
        X509Certificate thirdCert = (X509Certificate) cf.generateCertificate(third);
        
        try {
            testDocument1 = manager.importCertificate(anderson, null, certchainA);
            testDocument2 = manager.importCertificate(null, firstCert, certchainA);
            testDocument3 = manager.importCertificate(anderson, firstCert, null);
            testDocument4 = manager.importCertificate(null, null, certchainA);
            testDocument5 = manager.importCertificate(null, firstCert, null);
            testDocument6 = manager.importCertificate(anderson, null, null);
            testDocument7 = manager.importCertificate(null, null, null);
            
            Document[] allDoc = new Document[]{testDocument1, testDocument2,
                testDocument3, testDocument4, testDocument5, testDocument6,testDocument7};
            if (0 != testManager.chceckDocuments(allDoc, 0, 0))
            {
                fail("some key was uploaded with null values");
            }

        } catch (NullPointerException e) {
            fail("Unreported nullPinterException");
        }
        try {
            testDocument1 = manager.importCertificate(anderson, firstCert, certchainC);
            testDocument2 = manager.importCertificate(bobaFet, firstCert, certchainA);
            testDocument3 = manager.importCertificate(cyril, thirdCert, certchainA);            
        } catch (Exception e) {
            
        }
        try {
            testDocument1 = manager.importCertificate(anderson, firstCert, certchainA);
            testDocument2 = manager.importCertificate(bobaFet, secondCert, certchainB);
            testDocument3 = manager.importCertificate(cyril, thirdCert, certchainC);
            
            int doc1 = Integer.parseInt(testManager.extractElementFromXmlDoc(testDocument1, "Id"));
            int doc2 = Integer.parseInt(testManager.extractElementFromXmlDoc(testDocument2, "Id"));
            int doc3 = Integer.parseInt(testManager.extractElementFromXmlDoc(testDocument2, "Id"));
            
            ruleA = "userId ="+anderson.getId() +" AND id = " +doc1+"";
            ruleB = "userId ="+bobaFet.getId()  +" AND id = "+doc2+"";
            ruleC = "userId ="+cyril.getId()    +" AND id = "+doc3+"";
            
            
            String[] dataToBeExtracted = new String[]{"dn","issuer","serial","expiration_From",
            "expiration_To","certificate","chain"};
            
            ArrayList<String> databaseAndersonData = testManager.extractMultipleCollumsFromDatabase(dataToBeExtracted, "credentials", ruleA);
            ArrayList<String> databaseBobaFetData = testManager.extractMultipleCollumsFromDatabase(dataToBeExtracted, "credentials", ruleB);
            ArrayList<String> databaseCyrilData = testManager.extractMultipleCollumsFromDatabase(dataToBeExtracted, "credentials", ruleC);
        
            for (int i = 0; i < databaseAndersonData.size(); i++) {
                assertNotNull(databaseAndersonData.get(i));
                assertNotNull(databaseBobaFetData.get(i));
                assertNotNull(databaseCyrilData.get(i));
            }
            
                        
        } catch (Exception e) {
            fail("Did not imported certificate " +e.getMessage());
        }
    }
     
    private void docInit() {
        testDocument1 = null;
        testDocument2 = null;
        testDocument3 = null;
        testDocument4 = null;
        testDocument5 = null;
        testDocument6 = null;
        testDocument7 = null;
        testDocument8 = null;
        testDocument9 = null;
    }
    public void setUpId()
    {
                
        try {
            ResultSet r1 = statement.executeQuery("SELECT id"
                                    + " FROM credentials WHERE (userId = "+ anderson.getId() + " AND NOT (serial <=> NULL))");
            r1.next();
            andersonCerId = r1.getInt("id");
            r1 = statement.executeQuery("SELECT id"
                                        + " FROM credentials WHERE (userId = "+ bobaFet.getId() + " AND NOT (serial <=> NULL))");
            r1.next();
            bobaFetCerId = r1.getInt("id");

            r1 = statement.executeQuery("SELECT id"
                                        + " FROM credentials WHERE (userId = "+ cyril.getId() + " AND NOT (serial <=> NULL))");
            r1.next();
            cyrilCerId = r1.getInt("id");

            r1.close();
        
            
        } catch (SQLException e) {
            fail("failed to get certificate id from database ");
        }
    }
}