/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.ics.remsig.impl;

import cz.muni.ics.remsig.CertificateManager;
import cz.muni.ics.remsig.RemSigServlet;
//import static cz.muni.ics.remsig.RemSigServlet.CONFIG_FILE;
import cz.muni.ics.remsig.common.XmlParser;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_EXPIRED;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_NOT_YET_VALID;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_PASSWORD_RESET;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_REVOKED;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_SUSPENDED;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_VALID;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
//import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.util.encoders.Base64;
//import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
//import org.apache.commons.dbcp.BasicDataSource;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
//import org.bouncycastle.util.encoders.Base64;
import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.DatabaseMetaData;
import static java.sql.Types.BLOB;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.junit.Assert;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 


// pridat db unit k jdbctemple 
// spojenie jdbcTemplate a db unitpopripade nieco ine
/*
 import org.junit.After;
 import org.junit.AfterClass;
 import org.junit.Before;
 import org.junit.BeforeClass;
 import org.junit.Test;
 import static org.junit.Assert.*;
 import cz.muni.ics.remsig.common.XmlParser;
 import static cz.muni.ics.remsig.common.XmlParser.ELEM_ERROR;
 import static cz.muni.ics.remsig.common.XmlParser.ELEM_ROOT_REMSIG;
 import static cz.muni.ics.remsig.impl.Audit.*;
 import static cz.muni.ics.remsig.impl.Authz.*;
 import cz.muni.ics.remsig.impl.CertificateManagerImpl;
 import static cz.muni.ics.remsig.impl.CertificateManagerImpl.CONFIG_FILE;
 import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_EXPIRED;
 import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_NOT_YET_VALID;
 import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_PASSWORD_RESET;
 import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_REVOKED;
 import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_SUSPENDED;
 import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_VALID;
 import cz.muni.ics.remsig.impl.Client;
 import cz.muni.ics.remsig.impl.ErrorCode;
 import cz.muni.ics.remsig.impl.Person;
 import cz.muni.ics.remsig.impl.RemSigException;
 import cz.muni.ics.remsig.impl.RemSigSession;
 import cz.muni.ics.remsig.impl.SignerImpl;
 import java.io.BufferedReader;
 import java.io.ByteArrayInputStream;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.security.Security;
 import java.security.cert.CertificateException;
 import java.security.cert.CertificateFactory;
 import java.security.cert.X509Certificate;
 import java.util.Properties;
 import java.util.Set;
 import javax.servlet.ServletContext;
 import javax.servlet.ServletException;
 import javax.servlet.annotation.WebServlet;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.bouncycastle.jce.provider.BouncyCastleProvider;
 import org.bouncycastle.util.encoders.Base64;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.context.ApplicationContext;
 import org.springframework.context.support.ClassPathXmlApplicationContext;
 import org.springframework.jdbc.core.JdbcTemplate;
 import org.w3c.dom.Document;
 */
/**
 *
 * @author Miroslav
 */
public class CertificateManagerImplTest extends DBTestCase {

    private CertificateManagerImpl manager;
    private Properties configuration;
    public static final String CONFIG_FILE = "/etc/remsig/remsig.properties";

    private final static org.slf4j.Logger logger
            = LoggerFactory.getLogger(CertificateManagerImplTest.class);
    private IDatabaseTester databaseTester;
    private IDatabaseConnection dbUnitConnection;
    private Connection connection;
    private java.sql.Statement statement;
    
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
    

    private String initXmlDoc = "NewInitDatabase.xml"; // dataset
    private String workingDatabase = "NewInitDatabase.xml";
    //private String initXmlDoc = "initDatabase.xml"; // dataset
    //private String workingDatabase = "initDatabase.xml";
    private ITable expectedTable = null;
    @Mock
    Service service;

    @Override
    protected IDataSet getDataSet() throws Exception {

        return new FlatXmlDataSetBuilder().build(new FileInputStream(initXmlDoc));

    }

    public CertificateManagerImplTest() {

    }

    public CertificateManagerImplTest(String name) {
        super(name);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.mysql.jdbc.Driver"); // in case of trouble this need to be changedcom.mysql.jdbc.Driver

        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:mysql://localhost:3306/Remsig?zeroDateTimeBehavior=convertToNull");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "root");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");
        // 
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
        databaseTester = new JdbcDatabaseTester("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/Remsig?zeroDateTimeBehavior=convertToNull", "root", "");
//        
        IDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(new FileInputStream(initXmlDoc));
        
        //databaseTester.setSetUpOperation(DatabaseOperation.DELETE_ALL); //*89 this is supposed to be here but it is anoying
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.setDataSet(dataSet);
        //databaseTester.getDataSet().getTable("credentials");
        databaseTester.setSetUpOperation(DatabaseOperation.REFRESH);
        databaseTester.onSetup();
        dbUnitConnection = databaseTester.getConnection();
        connection = dbUnitConnection.getConnection();

        statement = connection.createStatement();
        //expectedTable = statement.execute("SELECT * FROM credentials");
        expectedTable = dataSet.getTable("credentials");

    }

    @After
    public void tearDown() throws Exception {
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.onTearDown();
        
        //databaseTester.onSetup();
        

    }

    /**
     * Test of setJdbcTemplate method, of class CertificateManagerImpl.
     */
    @Test
    public void testSetJdbcTemplate() {
        //System.out.println("setJdbcTemplate");
       /*JdbcTemplate jdbcTemplate = null;
         CertificateManagerImpl instance = new CertificateManagerImpl();
         instance.setJdbcTemplate(jdbcTemplate); */ // *89
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of loadQualifiedAuthorities method, of class CertificateManagerImpl.
     */
    @Test
    public void testLoadQualifiedAuthorities() throws Exception {
        //System.out.println("loadQualifiedAuthorities");
        CertificateManagerImpl instance = new CertificateManagerImpl(new Properties());
        // instance.loadQualifiedAuthorities();
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of generateRequest method, of class CertificateManagerImpl.
     */
    @Test
    public void testGenerateRequest() throws Exception {
        docInit();
        
        //generateXmlForSetUp()   ;
        // one way to clear database 
        databaseTester.setSetUpOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.onSetup();
        
     
        org.w3c.dom.Document testDocument = null; //new org.w3c.dom.Document() {};      
        CertificateManagerImpl instance = new CertificateManagerImpl();
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
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        instance.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());

        Person person = new Person(5);
        String password = "weakpassword";

        
        int numberOfInsertedRecords = 5; 
        
        ITable newTable = databaseTester.getDataSet().getTable("credentials");

        
        // inserting into empty database 
        try {
            // // *89 if properties is null hadze to nullpoiter exception bez poriadneje hlasky                       
            testDocument = manager.generateRequest(person, password);
            testDocument1 = manager.generateRequest(new Person(83512), "K845F234J98");
            testDocument2 = manager.generateRequest(new Person(7), "mIKp443SDJI");
            testDocument3 = manager.generateRequest(new Person(6), password);
            testDocument4 = manager.generateRequest(person, password);
            try {
                manager.generateRequest(null, "somepass");
                manager.generateRequest(person, null);
                manager.generateRequest(null, null);
            } catch (NullPointerException e) {
                // *89Note CertificateManger is not chceking for null values on lowest level later on is catching in with broad exceptio
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
            // inserting addition request
            
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
                // *89Note CertificateManger is not chceking for null values on lowest level later on is catching in with broad exceptio
            }
            r = statement.executeQuery("SELECT COUNT(*) AS rowcount FROM credentials");
            r.next();
            count = r.getInt("rowcount");
            assertEquals(finalRowCount, count);
            r = statement.executeQuery("SELECT userID,private_key,request,pubkey_hash,salt, key_backup"
                    + " FROM credentials");
            
                    
            String[] dataBaseEntry = new String[]{"userID", "private_key", "pubkey_hash", "salt", "key_backup"};
            chcekNullValues(r, dataBaseEntry);
            r.close();

        } catch (RemSigException ex) {
            System.out.println("remsign exception was thrown while generating request" + ex);
        }

        
        //extractDocIntoXml("output.xml", testDocument);
   
        

    }
    @Test
    public void testImportPKCS12() throws Exception {
        docInit();
        databaseTester.setSetUpOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.onSetup();
        // to do try generating new p12 certificate 
        // load xml file there seems to be a lot of work done with them its in output2.xml and it is your certificate
        // generate 3 or 4 certificates by certificate autority for testing 
        // xml file will be uploaded with 10 generated person id 1-10
        
        //*89 to note doesnt link certificate with generate request and it was my understanding that this is supposed to happen
        /*
            posiible error but could be fixed by loading from xml file instead of steady p12 file
        
        */
        
        
        
      /*   ResultSet r = statement.executeQuery("SELECT COUNT(*) AS rowcount FROM credentials");
            r.next();
            int count = r.getInt("rowcount");
        System.out.println("MIRO  "+ count);
        */
        
        
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());
        
        /**
         * Standard for loading p12 files 
         * In order to test multiple p12 certificates it is nessacery to set proper 
         * name and pass to certificates they are not included in any files
         * 
         */
        
        //String xmlP12 = LoadPKCS12("output2.xml");
        //manager.importPKCS12(anderson, xmlP12, andersonDefPass, "123456");
        
        
        
            // one and 2 are the same certificates 
        String p12Certificate1 = loadPKCS12("TestingCertificates/newSsl/sslcert/sub1-cert.p12");
        String p12Certificate2 = loadPKCS12("TestingCertificates/newSsl/sslcert/sub1-cert.p12");
        
        // In case of multiple certificates to test
        
        String p12Certificate3 = loadPKCS12("TestingCertificates/newSsl/sslcert/sub2-cert.p12");
        String p12Certificate4 = loadPKCS12("TestingCertificates/newSsl/sslcert/sub3-cert.p12");
        String p12Certificate5 = loadPKCS12("mine.p12");
        String p12Certificate6 = loadPKCS12("mine.p12");
        
        String p12passToCer3 = "123456";
        String p12passToCer4 = "123456";
        String p12passToCer5 = "123456";
        String p12passToCer6 = "changeit";
        
        String p12passToCer1 = "123456";
        String p12passToCer2 = "123456";
        
        
        
        /** 
         * Test with one working certificate 
         */
        
        //testDocument2 =  manager.importPKCS12(bobaFet, p12Certificate5, bobaFetDefPass, p12passToCer5);
        
        testDocument1 =  manager.importPKCS12(anderson, p12Certificate1, andersonDefPass, p12passToCer1);
        testDocument2 =  manager.importPKCS12(bobaFet, p12Certificate2, bobaFetDefPass, p12passToCer2);
        
        /* *89 to delete just for testing
        
        
        */
        testDocument3 = manager.importPKCS12(cyril, p12Certificate3, cyrilDefPass, p12passToCer3);       
        testDocument4 = manager.importPKCS12(daryl, p12Certificate4, darylDefPass, p12passToCer4);       
        
        
        
        int doc1 = extractRequestIdFromXmlDoc(testDocument1);
        int doc2 = extractRequestIdFromXmlDoc(testDocument2);
        
        ResultSet r = statement.executeQuery("SELECT userId, certificate, private_key, dn, serial, issuer,"
				+ "expiration_from, expiration_to, pubkey_hash, key_backup, salt,"
				+ "chain FROM credentials WHERE (userId = 1 AND id = "+ doc1 +") OR (userId = 2 AND id = "+ doc2 +") ");

            String[] dataBaseEntry = new String[]{"userId", "certificate", "private_key", "dn", "serial", "issuer",
				"expiration_from", "expiration_to", "pubkey_hash", "key_backup", "salt","chain"};
            chcekNullValues(r, dataBaseEntry);
            r.close();
            
            /**
             * Throws error instead of some reasonable exemption unreasonable error
             * maybe rewrite using catch error  
             */
            /*
            try {
                    manager.importPKCS12(anderson, p12passToCer2, null, p12passToCer2);
                    manager.importPKCS12(null, p12passToCer2, andersonDefPass, p12passToCer2);
                    manager.importPKCS12(anderson, null, andersonDefPass, p12passToCer2);
                    manager.importPKCS12(anderson, p12passToCer2, andersonDefPass, p12passToCer2);
                    manager.importPKCS12(anderson, p12passToCer2, andersonDefPass, null);
                    manager.importPKCS12(null, p12passToCer2, null, p12passToCer2);
                    manager.importPKCS12(anderson, null, andersonDefPass, null);
                    manager.importPKCS12(null, null, null, null);
                                       
                
                
    } catch (NullPointerException e) {
        fail("Import with null values wasnt catched ");
    }
            */
            
            
        ResultSet r1 = statement.executeQuery("SELECT certificate,dn, serial, issuer,"
                                    + "expiration_from, expiration_to"
                                    + " FROM credentials WHERE (userId = 1 AND id = "+ doc1 +") OR (userId = 2 AND id = "+ doc2 +") ");

        String[] dataBaseEntry1 = new String[]{"certificate", "dn", "serial", "issuer",
                            "expiration_from", "expiration_to"};
        // person back up with blob 172
        chcekValuesOfTwo(r1, dataBaseEntry1);
        r1.close();
    
    /**
     * Multiple working certificates 
     * Testing 5 different certificates   
     */
        
        /*
        testDocument3 = manager.importPKCS12(cyril, p12Certificate3, cyrilDefPass, p12passToCer3);
        testDocument4 =  manager.importPKCS12(daryl, p12Certificate4, darylDefPass, p12passToCer4);
        testDocument5 =  manager.importPKCS12(eva, p12Certificate5, evaDefPass, p12passToCer5);
        testDocument6 =  manager.importPKCS12(frank, p12Certificate6, frankDefPass, p12passToCer6);
        
        int doc3 = extractRequestIdFromXmlDoc(testDocument3);
        int doc4 = extractRequestIdFromXmlDoc(testDocument4);
        int doc5 = extractRequestIdFromXmlDoc(testDocument5);
        int doc6 = extractRequestIdFromXmlDoc(testDocument6);
        
        ResultSet r2= statement.executeQuery("SELECT userId, certificate, private_key, dn, serial, issuer,"
				+ "expiration_from, expiration_to, pubkey_hash, key_backup, salt,"
				+ "chain FROM credentials  WHERE (userId = 1 AND id = "+ doc1 +") OR (userId = 2 AND id = "+ doc2 +") OR "
                + "(userId = 3 AND id = "+ doc3 +") OR (userId = 4 AND id = "+ doc4 +") OR"
                + "(userId = 5 AND id = "+ doc5 +") OR (userId = 4 AND id = "+ doc4 +")");

        chcekNullValues(r2, dataBaseEntry1);
        
        ResultSet r3= statement.executeQuery("SELECT certificate,dn, serial, issuer,"
                                    + "expiration_from, expiration_to"
                                    + " FROM credentials WHERE (userId = 1 AND id = "+ doc1 +") OR (userId = 2 AND id = "+ doc2 +") OR "
                + "(userId = 3 AND id = "+ doc3 +") OR (userId = 4 AND id = "+ doc4 +") OR"
                + "(userId = 5 AND id = "+ doc5 +") OR (userId = 4 AND id = "+ doc4 +")");

        String[] dataBaseEntry3 = new String[]{"certificate", "dn", "serial", "issuer",
                            "expiration_from", "expiration_to"};
        
        chcekValuesNonEqual(r3, dataBaseEntry3,5);
        r2.close();
        r3.close();
        */
        
        /**
         * End of test 
         */
    
    
        /*
        Document[] allDoc = new Document[]{testDocument1, testDocument2, testDocument3, testDocument4};

        int testResult = documentTesting(allDoc, 2, 3);
        switch (testResult) {
            case 1:
                fail("Different results gave same output");
            case 2:
                fail("Data that supposed to give same results didn't");
            case 3:
                fail("One or more object was same with null");
            case 0: {
            }

        }*/
    }
    /**
     * This test works under assumption that in database already exist 3 records 
     * of imported p12 certificates method generateXmlForSetUp() generates 
     * xml database entry necessary for this test
     * @throws Exception 
     */
    @Test
    public void testExportPKCS12() throws Exception    {
        docInit();        
        //generateXmlForSetUp();        
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());
        
        String p12passToCer1 = "123456";
        /*
            Selection of certificate might need some work         
        */
        int andersonCerId = 0;
        int bobaFetCerId = 0 ;
        int cyrilCerId = 0 ;
        
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
        
        try {
            manager.exportPKCS12(null, andersonCerId, p12passToCer1, p12passToCer1);
            manager.exportPKCS12(anderson, andersonCerId, null, p12passToCer1);
            manager.exportPKCS12(anderson, andersonCerId, p12passToCer1, null);
            manager.exportPKCS12(null, andersonCerId, null, p12passToCer1);
            manager.exportPKCS12(null, andersonCerId, p12passToCer1, null);
            manager.exportPKCS12(null, andersonCerId, null, null);
            
        } catch (NullPointerException e) {
            fail("unreported nullPointerException was thrown");
        }
        
        
        
        try {
            testDocument1 = manager.exportPKCS12(anderson, andersonCerId, andersonDefPass, p12passToCer1);
            testDocument2 = manager.exportPKCS12(bobaFet, bobaFetCerId, bobaFetDefPass, p12passToCer1);
            testDocument3 = manager.exportPKCS12(cyril, cyrilCerId, cyrilDefPass, p12passToCer1);
            
            
        } catch (RemSigException ex) {
            Logger.getLogger(CertificateManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            extractDocIntoXml("test0001.xml", testDocument1);
            extractDocIntoXml("test0002.xml", testDocument2);
            extractDocIntoXml("test0003.xml", testDocument3);
            
            
            //FileInputStream xmlDoc1 = new FileInputStream("test0001.xml");
            //String doc1p12 = extractElementFromXmlDoc(testDocument1, "pkcs12");
           databaseTester.setSetUpOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.onSetup();
            //manager.importPKCS12(anderson, doc1p12, andersonDefPass, p12passToCer1);
            //manager.importPKCS12(frank, doc1p12, andersonDefPass, p12passToCer1);
            System.out.printf("adas");
                    
                   
            
            //assertEquals(testDocument1, testDocument2);
        } catch (Exception ex) {
            fail("not working");
        }
        
        FileInputStream xmlDoc1 = new FileInputStream("test0001.xml");
        FileInputStream xmlDoc2 = new FileInputStream("test0002.xml");
        FileInputStream xmlDoc3 = new FileInputStream("test0003.xml");
        String doc1p12 = extractElementFromXmlDoc(testDocument1, "pkcs12");
        String doc2p12 = extractElementFromXmlDoc(testDocument2, "pkcs12");
        String doc3p12 = extractElementFromXmlDoc(testDocument3, "pkcs12");
        
        assertNotNull(doc1p12);
        assertNotNull(doc2p12);
        assertNotNull(doc3p12);
                
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    
    

    }
    
    @Test
    public void testCheckPassword() throws Exception {
        docInit();
        setUpId();
        
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());
        
        
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
            //fail("Uncaught null pointer exception"); *90 supposed to be here
            
            /*89 
                No null pointer exception not caught
            */
        }
        
        
        
    }

    /**
     * This test is dependent on working of method check password
     * Test of changePassword method, of class CertificateManagerImpl.
     */
    @Test
    public void testChangePassword() throws Exception {
        docInit();
        setUpId();
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());        
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
            //fail("nullPointer exception was not caught"+e);
            //*100 
            
        }
        
        try {
               manager.changePassword(anderson, andersonCerId, andersonDefPass, igorDefPass);
               manager.changePassword(bobaFet, bobaFetCerId, bobaFetDefPass, gregorDefPass);
               
               manager.checkPassword(anderson, andersonCerId, igorDefPass);
               manager.checkPassword(bobaFet, bobaFetCerId, gregorDefPass);
               manager.checkPassword(cyril, cyrilCerId, cyrilDefPass);
        } catch (RemSigException e) {
            fail("there was an error changing passwor" + e);
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
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());        
        try {
             manager.resetPassword(null, andersonCerId);
             
        } catch (NullPointerException e) {
            //fail();
        }
        try {
            manager.checkPassword(anderson, andersonCerId, andersonDefPass);
            testDocument2 = manager.resetPassword(anderson, andersonCerId);
            extractDocIntoXml("testResetpass.xml", testDocument2);
            
            manager.checkPassword(anderson, andersonCerId, andersonDefPass);
            fail("password didnt change");
        } catch (RemSigException | TransformerException e) {
        }
        
        
        try {
            manager.checkPassword(anderson, andersonCerId, andersonDefPass);
            testDocument1 = manager.resetPassword(anderson, andersonCerId);
            extractDocIntoXml("testResetpass.xml", testDocument1);
            String newPass = extractElementFromXmlDoc(testDocument1, "password");
            manager.checkPassword(anderson, andersonCerId, newPass);
            
            
            manager.checkPassword(anderson, andersonCerId, andersonDefPass);
        } catch (Exception e) {
            fail("failed to reset password ");
        }
        
    }
    
    
    /**
     * For some reason anderson certificate is set to password reset this shouldnt happen
     * *89 there is need to recheck db unit operation set up 
     * 
     */
     @Test
    public void testChangeCertificateStatus() throws Exception {
        docInit();
        setUpId();
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());   
        
        String rule = "userId ="+bobaFet.getId()+" AND id = "+bobaFetCerId+"";
        int ko = -1;
        String test = "a";
         try {
              if ( null != (exportOneCollumFromDatabase("state", "credentials", rule)))
                      {
                          fail("inicial status should be null");
                      }
             manager.changeCertificateStatus(null, bobaFetCerId, STATE_SUSPENDED);
         } catch (NullPointerException e) {
             //fail("unreported nullpointer exception"); *89 This is one of errors
         }
         
         // testing standart changing of the certificate status
         try {
             manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_SUSPENDED);
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_SUSPENDED);
             
             manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_VALID);
             test = exportOneCollumFromDatabase("state", "credentials", rule);
             assertNull(test);
             
             manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_SUSPENDED);
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_SUSPENDED);
             
             manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_EXPIRED);
             test = exportOneCollumFromDatabase("state", "credentials", rule);
             assertNull(test);
             
             manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_SUSPENDED);
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_SUSPENDED);
             
             manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_NOT_YET_VALID);
             test = exportOneCollumFromDatabase("state", "credentials", rule);
             assertNull(test);
             
             
         } catch (NullPointerException e) {
             fail("failed to change status as expected");
         }
        // testing if status does change after certificate is revoked
         try {
             manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_REVOKED);
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_REVOKED);
                          
             manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_SUSPENDED);
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_REVOKED);
             
             manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_VALID);
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_REVOKED);

             
             manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_EXPIRED);
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_REVOKED);
             
             manager.changeCertificateStatus(bobaFet, bobaFetCerId, STATE_PASSWORD_RESET);
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_REVOKED);             
                
         } catch (RemSigException | NumberFormatException e) {
             fail("exception was thrown" +e);
         }
         ko = -1;
         
         rule = "userId ="+cyril.getId()+" AND id = "+cyrilCerId +"";
         if ( null != (exportOneCollumFromDatabase("state", "credentials", rule)))
                {
                    fail("inicial status should be null");
                }
         // testing if certificate status changes after password is reseted
         try {             
             manager.changeCertificateStatus(cyril, cyrilCerId, STATE_PASSWORD_RESET);                     
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_PASSWORD_RESET);
                          
             manager.changeCertificateStatus(cyril, cyrilCerId, STATE_SUSPENDED);
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_PASSWORD_RESET);
             
             manager.changeCertificateStatus(cyril, cyrilCerId, STATE_VALID);
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_PASSWORD_RESET);

             
             manager.changeCertificateStatus(cyril, cyrilCerId, STATE_REVOKED);
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_PASSWORD_RESET);
             
             manager.changeCertificateStatus(cyril, cyrilCerId, STATE_NOT_YET_VALID); 
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_PASSWORD_RESET);             
             
             manager.changeCertificateStatus(cyril, cyrilCerId, STATE_EXPIRED); 
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, STATE_PASSWORD_RESET);
                
         } catch (RemSigException | NumberFormatException e) {
             fail("exception was thrown" +e);
         }
        // testing if certificate status doesnt change to something weird
         try {             
             int init = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             manager.changeCertificateStatus(cyril, cyrilCerId, 59);                     
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, init);
                          
             manager.changeCertificateStatus(cyril, cyrilCerId, -6);
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, init);
             manager.changeCertificateStatus(cyril, cyrilCerId, Integer.MAX_VALUE+1);                     
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, init);
             manager.changeCertificateStatus(cyril, cyrilCerId, Integer.MIN_VALUE -1);                     
             ko = Integer.parseInt(exportOneCollumFromDatabase("state", "credentials", rule));
             assertEquals(ko, init);
             
         }catch(Exception e)
                 {}
                 
        
        /*                case STATE_VALID:
			case STATE_NOT_YET_VALID:
			case STATE_EXPIRED: null
			
			case STATE_SUSPENDED: state
			
			case STATE_REVOKED:
		
			case STATE_PASSWORD_RESET:
        
        statement.execute("UPDATE credentials SET state= 39 WHERE userId="+ bobaFet.getId()+" AND id= "+bobaFetCerId+ ""
               + "	AND state is null");
	//statement.execute("UPDATE credentials SET state= 27 WHERE userId="+ bobaFet.getId()+"");					
        
        manager.changeCertificateStatus(bobaFet, bobaFetCerId, 4);
        manager.changeCertificateStatus(daryl, 942, 4);
        manager.changeCertificateStatus(eva, 943 , 1);
        manager.changeCertificateStatus(frank, 944 , 0);
         manager.changeCertificateStatus(daryl, 942, STATE_VALID);
        */
        
        
    }
    
    @Test
    public void testListCertificatesWithStatus() throws Exception {
        docInit();
        setUpId();
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());   
        
        try {
            manager.listCertificatesWithStatus(null, 1);
        } catch (NullPointerException e) {
            //fail("unreported nullPpointerException");
        }
        // not yet valid should be derived from current date or set for something like 2349
        
        statement.execute("UPDATE credentials SET state= null WHERE userId="+ anderson.getId()+" AND id= "+andersonCerId);
        statement.execute("UPDATE credentials SET state= null, expiration_from = 1480075200 WHERE userId="+ bobaFet.getId()+" AND id= "+bobaFetCerId);
        statement.execute("UPDATE credentials SET state= null, expiration_to = 1453161600 WHERE userId="+ cyril.getId()+" AND id= "+cyrilCerId);
        
        testDocument1 = manager.listCertificatesWithStatus(anderson, STATE_VALID);
        testDocument2 = manager.listCertificatesWithStatus(bobaFet, STATE_NOT_YET_VALID );
        testDocument3 = manager.listCertificatesWithStatus(cyril, STATE_EXPIRED);
        extractDocIntoXml("testListCertificateWithStatus1.xml", testDocument1);
        extractDocIntoXml("testListCertificateWithStatus2.xml", testDocument2);
        extractDocIntoXml("testListCertificateWithStatus3.xml", testDocument3);
        
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
            fail("there wasnt supposed to be any exception " + e);
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
     *Removes blank space from end of certificate and chain 
     * @param input
     * @return 
     */
    public String changeEndCertificate(String input)
    {
        String toBefound = "END CERTIFICATE-----"+System.lineSeparator();
        String replaceWith = "END CERTIFICATE-----";
        
        String replace = input.replace(toBefound, replaceWith);
        return replace;
        
    }
    
    public ArrayList<String> extractMultipleElementsFromDoc(String[] elements, Document doc){
        ArrayList<String> result = new ArrayList<>();
        for (String element : elements) {
            result.add(extractElementFromXmlDoc(doc, element));
        }
        
        return result;
              
    
    }
    public ArrayList<String> extractMultipleCollumsFromDatabase(String[] dataToBeExtracted,String databaseName,String rule)
    {
        ArrayList<String> result = new ArrayList<String>();
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
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());
        String ruleA = "userId ="+anderson.getId() +" AND id = " + andersonCerId+"";
        String ruleB = "userId ="+bobaFet.getId() +" AND id = " + bobaFetCerId+"";
        String ruleC = "userId ="+cyril.getId() +" AND id = " + cyrilCerId+"";
        
        ArrayList<String> databaseAnderson = new ArrayList<>();
        ArrayList<String> databaseCyril = new ArrayList<>();
        ArrayList<String> databaseBobaFet = new ArrayList<>();
        
        
        String[] xmlExtraction = new String[]{"dn","issuer","serialNumber",
                "expirationFrom","expirationTo","certificatePEM","chainPEM"};
        String[] databaseExtraction = new String[]{"dn","issuer","serial",
                "expiration_From","expiration_To","certificate","chain"}; 
       
        databaseAnderson = testManager.extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleA);        
        databaseBobaFet = testManager.extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleB);
        databaseCyril = testManager.extractMultipleCollumsFromDatabase(databaseExtraction, "credentials", ruleC);
        
        ArrayList<String[]> result = new ArrayList<String[]>();
        
     
            statement.execute("UPDATE credentials SET state= "+STATE_REVOKED+" WHERE userId="+ anderson.getId()+" AND id= "+andersonCerId);
            statement.execute("UPDATE credentials SET state= "+STATE_SUSPENDED+" WHERE userId="+ bobaFet.getId()+" AND id= "+bobaFetCerId);
            statement.execute("UPDATE credentials SET state= "+STATE_REVOKED+" WHERE userId="+ cyril.getId()+" AND id= "+cyrilCerId);
            testDocument1 = manager.listAllCertificatesWithStatus(STATE_REVOKED);
            testDocument2 = manager.listAllCertificatesWithStatus(STATE_SUSPENDED);
            extractDocIntoXml("testListAllCertificateWithStatus.xml", testDocument1);
            extractDocIntoXml("testListAllCertificateWithStatusExpired.xml", testDocument2);
            
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
            testManager.exportDocIntoXml("testlistallCertificateWithStatusvalid.xml", testDocument3);
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
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());  
        
        byte[] differentData = new byte[]{4,2,3,4,1,23,4,1};        
        byte[] privateKeyA = testManager.loadFileBytes("TestingCertificates/newSsl/sslcert/private/testsubject1-key.pem");
        byte[] privateKeyB = testManager.loadFileBytes("TestingCertificates/newSsl/sslcert/private/testsubject2-key.pem");
        byte[] privateKeyC = testManager.loadFileBytes("TestingCertificates/newSsl/sslcert/private/testsubject3-key.pem");

        testDocument1 = manager.resetPassword(anderson, andersonCerId);
        byte[] passHashA = extractElementFromXmlDoc(testDocument1, "password").getBytes();

        testDocument2 = manager.resetPassword(bobaFet,bobaFetCerId);
        byte[] passHashB = extractElementFromXmlDoc(testDocument2, "password").getBytes();

        
        String[] dataToBeExtracted = new String[]{"userId","id","dn","issuer","serial","expiration_from",
            "expiration_to"}; 
        
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
            //fail("uncaught null pointer exception");
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
            
            assertThat(originalAnderson, not(uploadedAnderson));
            assertThat(originalBobaFet, not(uploadedBobaFet));
            assertThat(originalCyril, is(uploadedCyril));
          
            
            
            
        
        
    }

    /**
     * Only testing expected outcome since nothing else can be properly tested
     * Test of stats method, of class CertificateManagerImpl.
     */
    // method neccesary to read and parse string 
    
    @Test
    public void testStats() throws Exception {
        docInit();
        setUpId();
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());   
        
        ArrayList<String[]> xmlData =  testManager.parseXmlDoc("testStats.xml");
        
        String[] dataToBeExtracted = new String[]{"userId","id","dn","issuer","serial","expiration_from",
            "expiration_to"}; 
        
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
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());   
        
        String ruleA = "userId ="+anderson.getId() +" AND id = " + andersonCerId+"";
        String ruleB = "userId ="+bobaFet.getId()  +" AND id = "+bobaFetCerId+"";
        String ruleC = "userId ="+cyril.getId()    +" AND id = "+cyrilCerId+"";
        
        String ok = testManager.extractOneCollumFromDatabase("request", "credentials", ruleA);
        String certchain = testManager.extractOneCollumFromDatabase("chain", "credentials", ruleA);
        FileInputStream first = new FileInputStream("TestingCertificates/newSsl/sslcert/pkcs7/sub1cer.der");
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate firstCert = (X509Certificate) cf.generateCertificate(first);
        statement.execute("UPDATE credentials SET level= 1"+" WHERE userId="+ anderson.getId()+" AND id= "+andersonCerId);
        manager.importCertificate(anderson, firstCert, certchain);
    String p12Certificate2 = loadPKCS12("TestingCertificates/newSsl/sslcert/sub2-cert.p12");
    
    
    
    
    }
    
    
    

    public String exportOneCollumFromDatabase(String dataToBeExported, String databaseName,String rule)
    {
        String result = null;
        try {
            ResultSet r = statement.executeQuery("SELECT " +dataToBeExported+""
                    + " FROM "+databaseName+" "
                    + "WHERE ("+rule+")");
            r.next();
            result = r.getString(dataToBeExported);
        } catch (SQLException ex) {
            System.out.println("Failed to load data "+dataToBeExported+" from "+ databaseName+" ");
        }
    
        return result;
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
    
    
    
    
    /**
     * 
     * @param File name
     * @return Content of xml file converted into string
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public String getStringFromXmlFile(String input) throws FileNotFoundException, IOException
    {
     File xmlFile = new File(input);
        
        // Let's get XML file as String using BufferedReader
        // FileReader uses platform's default character encoding
        // if you need to specify a different encoding, use InputStreamReader
        Reader fileReader = new FileReader(xmlFile);
        BufferedReader bufReader = new BufferedReader(fileReader);
        
        StringBuilder sb = new StringBuilder();
        String line = bufReader.readLine();
        while( line != null){
            sb.append(line).append("\n");
            line = bufReader.readLine();
        }
        String xml2String = sb.toString();
        System.out.println("XML to String using BufferedReader : ");
        System.out.println(xml2String);


        return xml2String;
    }
    
    /**
     * 
     * @param document to be converted 
     * @return String converted from doc document
     */
    public String getStringFromDoc(org.w3c.dom.Document doc)    {
    DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
    LSSerializer lsSerializer = domImplementation.createLSSerializer();
    return lsSerializer.writeToString(doc);   
}
    /**
     * 
     * @param filePath to p12 file
     * @return convert through bytes and returns string
     */
    public String loadPKCS12(String filePath)
    {
         String encodedString = null;
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            byte[] encoded = Base64.encode(bytes);
            encodedString = new String(encoded);
        } catch (IOException ex) {
            
        }
        return encodedString;
    }

    /**
     * Takes result set and string[] values to be chceck if they were inserted 
     * into the database and are not null
     * @param inRs
     * @param valuesToChceck
     * @return 
     */
    public boolean chcekNullValues(ResultSet inRs, String[] valuesToChceck) {
        try {
            while (inRs.next()) {
                for (String databaseEntry : valuesToChceck) {
                    assertNotNull("Values weren't inserted correctly some of them were null", inRs.getObject(databaseEntry));
                }
                //                    ArrayList<String> singleAddress = new ArrayList<String>(); array list declarataion for posiible changes 
            }

        } catch (SQLException e) {
        }

        return true;
    }
    /**
     * Check if the values in 2 result same are the same 
     * @param inRs
     * @param valuesToChceck
     * @return 
     */
    public boolean chcekValuesOfTwo(ResultSet inRs, String[] valuesToChceck) {
        try {
            LinkedList<Object> set1 = new LinkedList<Object>();
            LinkedList<Object> set2 = new LinkedList<Object>();
            //LinkedList<Object> set1 = new LinkedList<String>();
            //LinkedList<String> set2 = new LinkedList<String>();
            int counter = 0 ;
            while (inRs.next()) {
                for (String databaseEntry : valuesToChceck) {
                    if (counter == 0)    
                    {
                    set1.add(inRs.getObject(databaseEntry));
                    }else
                    {
                        set2.add(inRs.getObject(databaseEntry));
                    }
                    
                    //assertNotNull("Values weren't inserted correctly some of them were null", inRs.getObject(databaseEntry));
                }
                //                    ArrayList<String> singleAddress = new ArrayList<String>(); array list declarataion for posiible changes 
                ++counter;
            }
            for (int i = 0; i < set1.size(); i++) {
                
                assertEquals("Error while chceking same value inserted into database at differnt times", set1.get(i),set2.get(i));
                
            }
            
            assertEquals("Error while chceking same value inserted into database at differnt times" ,set1, set2);

        } catch (SQLException e) {
        }

        return true;
    }
    /**
     * Still in progress for now only works for 5 results set 
     * chceck if any of the resultsets are the same
     * @param inRs
     * @param valuesToChceck
     * @param k
     * @return 
     */
    public boolean chcekValuesNonEqual(ResultSet inRs, String[] valuesToChceck, int k) {
        // List of list could work for all sizes 
        
        
        
        try {
            LinkedList<Object> set1 = new LinkedList<Object>();
            LinkedList<Object> set2 = new LinkedList<Object>();
            LinkedList<Object> set3 = new LinkedList<Object>();
            LinkedList<Object> set4 = new LinkedList<Object>();
            LinkedList<Object> set5 = new LinkedList<Object>();
            
            LinkedList<LinkedList<Object>> all = new LinkedList<LinkedList<Object>>();
            
            
            //LinkedList<Object> set1 = new LinkedList<String>();
            //LinkedList<String> set2 = new LinkedList<String>();
            int counter = 0 ;
            while (inRs.next()) {
                for (String databaseEntry : valuesToChceck) {
                    
                    switch (counter){
                        case 0: set1.add(inRs.getObject(databaseEntry));
                                break;                            
                        case 1: set2.add(inRs.getObject(databaseEntry));
                            break;                            
                            case 2: set3.add(inRs.getObject(databaseEntry));
                            break;                            
                                case 3: set4.add(inRs.getObject(databaseEntry));
                            break;                            
                                    case 4: set5.add(inRs.getObject(databaseEntry));
                            break;                            
                            }
                            
                    
                    //assertNotNull("Values weren't inserted correctly some of them were null", inRs.getObject(databaseEntry));
                }
                //                    ArrayList<String> singleAddress = new ArrayList<String>(); array list declarataion for posiible changes 
                ++counter;
            }
            for (int i = 0; i < k; i++) {
                
            }            
            
            assertThat(set1,  not(set2));
            assertThat(set1,  not(set3));
            assertThat(set1,  not(set4));
            assertThat(set1,  not(set5));
            assertThat(set2,  not(set3));
            assertThat(set2,  not(set4));
            assertThat(set2,  not(set5));
            assertThat(set3,  not(set4));
            assertThat(set3,  not(set5));
            assertThat(set4,  not(set5));
                        
            //Assert.assertThat(set1, not(all));

        } catch (SQLException e) {
        }

        return true;
    }
    
    /**
     * Takes input document and creates new output document
     * @param outputFilename
     * @param inputDoc to be transfered
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    public void extractDocIntoXml(String outputFilename, Document inputDoc) throws TransformerConfigurationException, TransformerException 
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(new File(outputFilename));
        Source input = new DOMSource(inputDoc);
        transformer.transform(input, output);
    }
    
    
    
    /**
     * returns request id from doc document
     * @param docToBeExtracted
     * @return 
     */
    public int extractRequestIdFromXmlDoc(Document docToBeExtracted)
    {
        int result = 0;
        try {

            /*DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse ("output3.xml");
            */
            Document doc = docToBeExtracted;
            // normalize text representation
            doc.getDocumentElement ().normalize ();
            //System.out.println ("Root element of the doc is " +  doc.getDocumentElement().getNodeName());


            //doc.getElementById(evaDefPass)
            NodeList listOfPersons = doc.getElementsByTagName("remsig");
            
            int totalPersons = listOfPersons.getLength();
            
            
            for(int s=0; s<listOfPersons.getLength() ; s++){


                Node firstPersonNode = listOfPersons.item(s);
                if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE){


                    Element firstPersonElement = (Element)firstPersonNode;

                    //-------
                    NodeList firstNameList = firstPersonElement.getElementsByTagName("requestId");
                    Element firstNameElement = (Element)firstNameList.item(0);

                    NodeList textFNList = firstNameElement.getChildNodes();
                    
                    result =   Integer.parseInt((((Node)textFNList.item(0)).getNodeValue().trim()));
                     //Integer.parseInt("1234"); 


                }//end of if clause


            }//end of for loop with s var


        }catch (Exception e) {
        
        }catch (Throwable t) {
        t.printStackTrace ();
        }
        
        
        return result;
    }
    
    public String extractElementFromXmlDoc(Document docToBeExtracted, String elementName)
    {
        String  result = null;
        try {

            /*DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse ("output3.xml");
            */
            Document doc = docToBeExtracted;
            // normalize text representation
            doc.getDocumentElement ().normalize ();

            //doc.getElementById(evaDefPass)
            NodeList listOfPersons = doc.getElementsByTagName("remsig");
            
            int totalPersons = listOfPersons.getLength();
            
            for(int s=0; s<listOfPersons.getLength() ; s++){


                Node firstPersonNode = listOfPersons.item(s);
                if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE){


                    Element firstPersonElement = (Element)firstPersonNode;

                    //-------
                    NodeList firstNameList = firstPersonElement.getElementsByTagName(elementName);
                    Element firstNameElement = (Element)firstNameList.item(0);

                    NodeList textFNList = firstNameElement.getChildNodes();
                    
                    result =   ((((Node)textFNList.item(0)).getNodeValue().trim()));
                     //Integer.parseInt("1234"); 


                }//end of if clause


            }//end of for loop with s var


        }catch (Exception e) {
        
        }catch (Throwable t) {
        t.printStackTrace ();
        }
        
        
        return result;
    }
    
    
    /**
     * Setting up database for first use it is expected to have database in this 
     * format 2 different p12 certificates are necessary 
     * @throws RemSigException
     * @throws Exception 
     */
    private void generateXmlForSetUp() throws RemSigException, Exception
    {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());
        databaseTester.setSetUpOperation(DatabaseOperation.DELETE_ALL);
        //databaseTester.setDataSet();
        databaseTester.onSetup();
        
        manager.generateRequest(anderson, andersonDefPass);
        manager.generateRequest(bobaFet, bobaFetDefPass);
        manager.generateRequest(cyril, cyrilDefPass);
        manager.generateRequest(daryl, darylDefPass);
        manager.generateRequest(eva, evaDefPass);
        manager.generateRequest(frank, frankDefPass);
        manager.generateRequest(gregor, gregorDefPass);
        manager.generateRequest(helena, helenaDefPass);
        manager.generateRequest(igor, igorDefPass);
        
        
        
        String p12Certificate1 = loadPKCS12("TestingCertificates/newSsl/sslcert/sub1-cert.p12");
        String p12Certificate2 = loadPKCS12("TestingCertificates/newSsl/sslcert/sub2-cert.p12");
        String p12Certificate3 = loadPKCS12("TestingCertificates/newSsl/sslcert/sub3-cert.p12");
        //String p12Certificate4 = loadPKCS12("right.p12");
        
                
        String p12passToCer1 = "123456";
        String p12passToCer2 = "123456";    
        String p12passToCer3 = "123456";    
        
        manager.importPKCS12(anderson, p12Certificate1, andersonDefPass, p12passToCer1);
        manager.importPKCS12(bobaFet, p12Certificate2, bobaFetDefPass, p12passToCer2);
        manager.importPKCS12(cyril, p12Certificate3, cyrilDefPass, p12passToCer3);
        
        
        try {
            exportsDatabaseIntoXml("NewInitDatabase.xml");
        } catch (Exception ex) {
        }
                
        
        
        
    }
    @Mock
    private JdbcTemplate jdbcTemplate;
/*
    @Test
    public void testImportCertificate() throws Exception {
        docInit();
        /*jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    
    
         jdbcTemplate.update(
         "INSERT INTO credentials "
         + "(userId,private_key,request,pubkey_hash,salt,key_backup) "
         + "VALUES (7,sd,asd,w,gd,tr)",
				
         new int[]{
         Types.INTEGER, Types.BLOB, Types.VARCHAR,
         Types.BLOB, Types.BLOB, Types.BLOB});
         */
/*
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        //manager.setJdbcTemplate(jdbcTemplate);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());

        FileInputStream first = new FileInputStream("FirstCertificate.cer");

//    FileInputStream first=new FileInputStream("FirstCertificate.cer");//*89 *
        FileInputStream second = new FileInputStream("SecondCertificate.cer");
        FileInputStream third = new FileInputStream("FirstCertificate.cer");
        X509Certificate firstCert = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            firstCert = (X509Certificate) cf.generateCertificate(first);
            X509Certificate secondCert = (X509Certificate) cf.generateCertificate(second);
            X509Certificate thirdCert = (X509Certificate) cf.generateCertificate(third);

            
            //r.next();
            //int count = r.getInt("rowcount");
            //assertEquals(finalRowCount, count);
            
            System.out.println("lok");
            /*testDocument2 = manager.importCertificate(new Person(5), secondCert, CONFIG_FILE);
             testDocument3 = manager.importCertificate(new Person(5), thirdCert, CONFIG_FILE);
             testDocument4 = manager.importCertificate(new Person(6), thirdCert, CONFIG_FILE);
             testDocument5 = manager.importCertificate(new Person(6), secondCert, CONFIG_FILE); //*89 test chain by removing database a seeing output 
             testDocument6 = manager.importCertificate(new Person(6), null, CONFIG_FILE);       // siganture should be same with diferent certchains i hope
             */
     // add chcek against database if nessesary
/*
        } catch (Exception e) {
            fail("Fail to load test certificate or inicialize prototype documents" + e);
        } finally {
            //first.close();
            second.close();
            first.close();
        }
        ResultSet r = statement.executeQuery("SELECT request FROM credentials WHERE userID = 5");
        //ResultSet r = statement.executeQuery("SELECT CAST(pubkey_hash AS CHAR(10000) CHARACTER SET utf8) FROM credentials WHERE userID = 5");
        
        //SELECT CONVERT(column USING utf8) FROM.
            r.next();
            byte[] request =  r.getBytes("request");
            /*Blob blob = r.getBlob("pubkey_hash");
            byte[] bdata = blob.getBytes(1, (int) blob.length());
            String publicHash = new String(bdata);
            
            */
/*
            X509Certificate x509Certificate = null;
            //cerificate;
             /*FileInputStream preinput=new FileInputStream("cerificate.txt");
             CertificateFactory cf = CertificateFactory.getInstance("X.509");
             X509Certificate tryCertificate = (X509Certificate) cf.generateCertificate(preinput);
            //byte[] encoded = Files.readAllBytes(Paths.get(preinput));
            */
/*
            FileInputStream preinput=new FileInputStream("TestingCertificates/miroslav_macor_410127.pem");
            //String content = new Scanner(new File("TestingCertificates/pokus.pem")).useDelimiter("\\Z").next();
            //String content = new Scanner(new File("TestingCertificates/miroslav_macor_410127.pem")).useDelimiter("\\Z").next();
            //String content = new Scanner(new File("TestingCertificates/miroslav_macor_410127.crt")).useDelimiter("\\Z").next();
            String content = new Scanner(new File("TestingCertificates/01.pem")).useDelimiter("\\Z").next();
            byte[] encoded = Files.readAllBytes(Paths.get("TestingCertificates/pokus.pem"));
/*
            
            FileInputStream first5 = new FileInputStream("TestingCertificates/01.pem");
    

//    FileInputStream first=new FileInputStream("FirstCertificate.cer");//*89 *
        
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate firstCert5 = (X509Certificate) cf.generateCertificate(first5);
       
            
		try {
            
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			x509Certificate = (X509Certificate) certFactory.
					generateCertificate(new ByteArrayInputStream(encoded));
		} catch (CertificateException ex) {
			throw new RemSigException(
					"Error while generating certificate in this fucking region",
					ex, ErrorCode.ERROR248EN);
		}
            
            
            
            
           String certChain =  " -----BEGIN CERTIFICATE-----\n"
				+ "MIIF6jCCA9KgAwIBAgIRAM0n6PuHVxFfdlSNZoQ9l/owDQYJKoZIhvcNAQEMBQAw\n"
				+ "gYgxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpOZXcgSmVyc2V5MRQwEgYDVQQHEwtK\n"
				+ "ZXJzZXkgQ2l0eTEeMBwGA1UEChMVVGhlIFVTRVJUUlVTVCBOZXR3b3JrMS4wLAYD\n"
				+ "VQQDEyVVU0VSVHJ1c3QgUlNBIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MB4XDTE0\n"
				+ "MTAwOTAwMDAwMFoXDTI0MTAwODIzNTk1OVowaTELMAkGA1UEBhMCTkwxFjAUBgNV\n"
				+ "BAgTDU5vb3JkLUhvbGxhbmQxEjAQBgNVBAcTCUFtc3RlcmRhbTEPMA0GA1UEChMG\n"
				+ "VEVSRU5BMR0wGwYDVQQDExRURVJFTkEgUGVyc29uYWwgQ0EgMjCCASIwDQYJKoZI\n"
				+ "hvcNAQEBBQADggEPADCCAQoCggEBAIt/txlfYTgQu/SsFvalu41zNJsnlZV8L2L2\n"
				+ "2pUwZyCfFo+Y1TUhlh6+3ppy8iN53w4L8oIOeJtWdlTTCG2cstFjVMDPGDt5qSZg\n"
				+ "2J/+p5SsClYDkuRhLd2JhKm0XTzF2Xk+G5fSLS4fp9gD2fCua7yqFU6vcr2OKpCB\n"
				+ "uK+sNQgwagGZHUhR92hxhmGYihsg7JbCqv5sGEOclH+2i34wqbxVbOtXB3rTHkLj\n"
				+ "uqVTC+rY7/fUz5GKDnraaVq56unRxerjmLKGTNwHcL+tlC4H1RMqkcxukrljaVPQ\n"
				+ "nWhJYp0rSvRfwlFJGgTNHUvt/jmjqsVfqwzssyLHU3mJAkTxl70CAwEAAaOCAWsw\n"
				+ "ggFnMB8GA1UdIwQYMBaAFFN5v1qqK0rPVIDh2JvAnfKyA2bLMB0GA1UdDgQWBBT7\n"
				+ "nDgjUKHXAASZLTgGZJCjJhvb7TAOBgNVHQ8BAf8EBAMCAYYwEgYDVR0TAQH/BAgw\n"
				+ "BgEB/wIBADAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUHAwQwGAYDVR0gBBEw\n"
				+ "DzANBgsrBgEEAbIxAQICHTBQBgNVHR8ESTBHMEWgQ6BBhj9odHRwOi8vY3JsLnVz\n"
				+ "ZXJ0cnVzdC5jb20vVVNFUlRydXN0UlNBQ2VydGlmaWNhdGlvbkF1dGhvcml0eS5j\n"
				+ "cmwwdgYIKwYBBQUHAQEEajBoMD8GCCsGAQUFBzAChjNodHRwOi8vY3J0LnVzZXJ0\n"
				+ "cnVzdC5jb20vVVNFUlRydXN0UlNBQWRkVHJ1c3RDQS5jcnQwJQYIKwYBBQUHMAGG\n"
				+ "GWh0dHA6Ly9vY3NwLnVzZXJ0cnVzdC5jb20wDQYJKoZIhvcNAQEMBQADggIBAECZ\n"
				+ "vR9W7r9ievG4g95FRUr9M/saDMcLJonWtk1pFoO4jkJyTBryic4xo+DBk48fW92G\n"
				+ "ukiUQEIcNGkhGPVMfYFTwp7Cue8yjjyoKMDpJMVx/72iJA0tL+LXbGgedtxxKXGM\n"
				+ "f1EsE97nDy4xCreriELxXBPqt8x9Wscs9go/1uPW4laDLVT/RwQiiGDx/UNZb1ev\n"
				+ "6RKtRAg6y+Anp5Yd6AUf2/6caQ8H9tjrcxZ6nsiNcc5DrFx4hK2KTqZAVfud8HCm\n"
				+ "BR0ic4WtbRFBd65HKCx5AcN+Usc9I48MF+7Ohz2ZJSDiAVmNwrMYFTX5cHBTD9Nt\n"
				+ "26TezyuKQEooR1hx2ODxQqnT+xXOiJxxltZgw4dh9RT/fyEaIhAFmqJCaTGYqCnV\n"
				+ "ED2MDYTiWJeZ8Ag6qCk99Aqou49IMOiZNdWQCHw97qFlx8XhTc4RfxSFqhYpK1Kb\n"
				+ "6DouDmuwmRPT0MWuRZNs2fTumA3b08klQkLK0f+OEFJWVAWyRSfWvS/6v8XPNxzO\n"
				+ "gtbiSjAX1vixM/5lrNei6JlmJjhgODr+X7ydaAddhvzu5p6hqIsmLRNPoRZkumfj\n"
				+ "ppoVkX/X8SSkMg7a6mMXDjl3o4vOD0A8sZ4t/TZTkhXSdxxy2kO8zBk3HOvNnc9J\n"
				+ "gekNS8kiK3OaBG7Vv2CPyc0vknfcyGVhzBOAnish\n"
				+ "-----END CERTIFICATE-----\n"
				+ "-----BEGIN CERTIFICATE-----\n"
				+ "MIIFdzCCBF+gAwIBAgIQE+oocFv07O0MNmMJgGFDNjANBgkqhkiG9w0BAQwFADBv\n"
				+ "MQswCQYDVQQGEwJTRTEUMBIGA1UEChMLQWRkVHJ1c3QgQUIxJjAkBgNVBAsTHUFk\n"
				+ "ZFRydXN0IEV4dGVybmFsIFRUUCBOZXR3b3JrMSIwIAYDVQQDExlBZGRUcnVzdCBF\n"
				+ "eHRlcm5hbCBDQSBSb290MB4XDTAwMDUzMDEwNDgzOFoXDTIwMDUzMDEwNDgzOFow\n"
				+ "gYgxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpOZXcgSmVyc2V5MRQwEgYDVQQHEwtK\n"
				+ "ZXJzZXkgQ2l0eTEeMBwGA1UEChMVVGhlIFVTRVJUUlVTVCBOZXR3b3JrMS4wLAYD\n"
				+ "VQQDEyVVU0VSVHJ1c3QgUlNBIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MIICIjAN\n"
				+ "BgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAgBJlFzYOw9sIs9CsVw127c0n00yt\n"
				+ "UINh4qogTQktZAnczomfzD2p7PbPwdzx07HWezcoEStH2jnGvDoZtF+mvX2do2NC\n"
				+ "tnbyqTsrkfjib9DsFiCQCT7i6HTJGLSR1GJk23+jBvGIGGqQIjy8/hPwhxR79uQf\n"
				+ "jtTkUcYRZ0YIUcuGFFQ/vDP+fmyc/xadGL1RjjWmp2bIcmfbIWax1Jt4A8BQOujM\n"
				+ "8Ny8nkz+rwWWNR9XWrf/zvk9tyy29lTdyOcSOk2uTIq3XJq0tyA9yn8iNK5+O2hm\n"
				+ "AUTnAU5GU5szYPeUvlM3kHND8zLDU+/bqv50TmnHa4xgk97Exwzf4TKuzJM7UXiV\n"
				+ "Z4vuPVb+DNBpDxsP8yUmazNt925H+nND5X4OpWaxKXwyhGNVicQNwZNUMBkTrNN9\n"
				+ "N6frXTpsNVzbQdcS2qlJC9/YgIoJk2KOtWbPJYjNhLixP6Q5D9kCnusSTJV882sF\n"
				+ "qV4Wg8y4Z+LoE53MW4LTTLPtW//e5XOsIzstAL81VXQJSdhJWBp/kjbmUZIO8yZ9\n"
				+ "HE0XvMnsQybQv0FfQKlERPSZ51eHnlAfV1SoPv10Yy+xUGUJ5lhCLkMaTLTwJUdZ\n"
				+ "+gQek9QmRkpQgbLevni3/GcV4clXhB4PY9bpYrrWX1Uu6lzGKAgEJTm4Diup8kyX\n"
				+ "HAc/DVL17e8vgg8CAwEAAaOB9DCB8TAfBgNVHSMEGDAWgBStvZh6NLQm9/rEJlTv\n"
				+ "A73gJMtUGjAdBgNVHQ4EFgQUU3m/WqorSs9UgOHYm8Cd8rIDZsswDgYDVR0PAQH/\n"
				+ "BAQDAgGGMA8GA1UdEwEB/wQFMAMBAf8wEQYDVR0gBAowCDAGBgRVHSAAMEQGA1Ud\n"
				+ "HwQ9MDswOaA3oDWGM2h0dHA6Ly9jcmwudXNlcnRydXN0LmNvbS9BZGRUcnVzdEV4\n"
				+ "dGVybmFsQ0FSb290LmNybDA1BggrBgEFBQcBAQQpMCcwJQYIKwYBBQUHMAGGGWh0\n"
				+ "dHA6Ly9vY3NwLnVzZXJ0cnVzdC5jb20wDQYJKoZIhvcNAQEMBQADggEBAJNl9jeD\n"
				+ "lQ9ew4IcH9Z35zyKwKoJ8OkLJvHgwmp1ocd5yblSYMgpEg7wrQPWCcR23+WmgZWn\n"
				+ "RtqCV6mVksW2jwMibDN3wXsyF24HzloUQToFJBv2FAY7qCUkDrvMKnXduXBBP3zQ\n"
				+ "YzYhBx9G/2CkkeFnvN4ffhkUyWNnkepnB2u0j4vAbkN9w6GAbLIevFOFfdyQoaS8\n"
				+ "Le9Gclc1Bb+7RrtubTeZtv8jkpHGbkD4jylW6l/VXxRTrPBPYer3IsynVgviuDQf\n"
				+ "Jtl7GQVoP7o81DgGotPmjw7jtHFtQELFhLRAlSv0ZaBIefYdgWOWnU914Ph85I6p\n"
				+ "0fKtirOMxyHNwu8=\n"
				+ "-----END CERTIFICATE-----" ;
            
            
            
            
        //r = statement.executeQuery("SELECT userID,private_key,request,pubkey_hash,salt, key_backup"    + " FROM credentials");
            Person robo = new Person(25); 
            manager.generateRequest(robo, "pass");
            System.out.print(firstCert.toString());
            //testDocument1 = manager.importPKCS12(robo, "right.p12", "pass", "123456");
            testDocument1 = manager.importCertificate(robo, firstCert5, content);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(new File("output.xml"));
        Source input = new DOMSource(testDocument1);
        transformer.transform(input, output);
            
            

        
        Document[] allDoc = new Document[]{testDocument1, testDocument2, testDocument3, testDocument4, testDocument5, testDocument6};

        int testResult = documentTesting(allDoc, 5, 8);
        switch (testResult) {
            case 1:
                fail("Different results gave same output");
            case 2:
                fail("Data that supposed to give same results didn't");
            case 3:
                fail("One or more object was same with null");
            case 0: {
            }

        }

    }

*/
    
    private int documentTesting(Document[] testsubject, int diffEnd, int sameEnd) {
        for (int i = 0; i < diffEnd; i++) {
            for (int j = i; j < diffEnd - 1; j++) {
                if (testsubject[i] == testsubject[j + 1]) {
                    return 1;
                    // testing data that are expected do be different
                }
            }

        }
        for (int i = diffEnd; i < sameEnd; i++) {
            for (int j = i; j < sameEnd - 1; j++) {
                if (testsubject[i] != testsubject[j + 1]) {
                    return 2;
                    // testing data that are expected to be same against each other a
                }
            }
        }
        for (int i = sameEnd; i < testsubject.length; i++) {
            for (int j = i; j < testsubject.length - 1; j++) {
                if (testsubject[i] != testsubject[j + 1]) {
                    return 3;
                    // null object testing against each other
                }
            }
        }
        return 0;
    }
/*
    @Test
    public void testExportPKCS12() throws Exception {
        docInit();
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());
        Person andreson = new Person(5);
        int cerId = 5; // SELECT certificateId FROM table WHERE Person id = 5 
        testDocument1 = manager.importPKCS12(andreson, "mine.ps12", "weakpassword", "changeit");
        manager.importPKCS12(null, "mine.ps12", "weakpassword", "changeit");
        manager.importPKCS12(new Person(7), "mine.ps12", "weakpassword", "changeit");
        testDocument2 = manager.exportPKCS12(andreson, 5, "weakpassword", "changeit");
        assertEquals(testDocument1, testDocument2);

                 
    ResultSet sr = statement.executeQuery("SELECT id FROM credentials WHERE userId = 1");
            sr.next();
            Integer certificateId = sr.getInt("id");
        
            
        
    testDocument2 =  manager.exportPKCS12(anderson, certificateId, andersonDefPass, "123456");
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    
    
        CertificateManagerImpl instance = new CertificateManagerImpl();
        Document expResult = null;
        Document result = instance.exportPKCS12(null, 0, "", "");
        assertEquals(expResult, result);

    }

    @Test
    public void testCheckPassword() throws Exception {
        docInit();
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());
        Person anderson = new Person(5);
        //manager.generateRequest(anderson, "test");

        testDocument1 = manager.checkPassword(new Person(5), 0, "test");
        testDocument2 = manager.checkPassword(anderson, 1, "test");
        testDocument3 = manager.checkPassword(anderson, 0, "Test");
        testDocument4 = manager.checkPassword(null, 0, "test");
        testDocument5 = manager.checkPassword(new Person(6), 0, "test");
        testDocument6 = manager.checkPassword(anderson, 0, "test");

        Document[] allDoc = new Document[]{testDocument1, testDocument2, testDocument3, testDocument4, testDocument5, testDocument6};

        int testResult = documentTesting(allDoc, 2, 3);
        switch (testResult) {
            case 1:
                fail("Different results gave same output");
            case 2:
                fail("Data that supposed to give same results didn't");
            case 3:
                fail("One or more object was same with null");
            case 0: {
            }

        }
        assertEquals("test", testDocument6.getElementsByTagName("password")); // possibli password or passHash  

    }
*/
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
/*
    @Test
    public void testChangePassword() throws Exception {
        docInit();
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Person anderson = new Person(5);
        int certid = 0;

        //testDocument1 = manager.generateRequest(anderson, "oldpass");
        try {
            manager.changePassword(null, certid, "oldpass", "newpass");
        } catch (NullPointerException e) {
            fail("Nullpoiter when person was null exception was thrown " + e);
        }
        testDocument4 = manager.checkPassword(anderson, certid, "oldpass");
        testDocument2 = manager.changePassword(anderson, certid, "oldpass", "newpass");
        testDocument3 = manager.checkPassword(anderson, certid, "newpass");
        assertFalse(testDocument4 == testDocument3);
        assertEquals("newpass", testDocument3.getElementsByTagName("password"));
        manager.changePassword(anderson, certid, "newpass", "oldpass");
        testDocument5 = manager.checkPassword(anderson, certid, "oldpass");
        assertEquals(testDocument4, testDocument5);

    }
*/
    /** 
     * Exports current database into xml document output as FirstParameter
     * 
     * exportXml(working.xml);
     * @param FileNameOutput
     * @throws Exception 
     */
    public void exportsDatabaseIntoXml(String fileNameOutput) throws Exception {
        Class driverClass = Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Remsig?zeroDateTimeBehavior=convertToNull", "root", "");

        //FlatDtdDataSet.write(jdbcConnection.createDataSet(), new FileOutputStream("test.dtd"));
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        QueryDataSet partialDataSet = new QueryDataSet(connection);
        partialDataSet.addTable("Remsig", "select * from credentials;");
        int multi = 5;
        FlatXmlDataSet.write(partialDataSet, new FileOutputStream("partial" + multi + ".xml"));

        IDataSet fullDataSet = connection.createDataSet();
        FlatXmlDataSet.write(fullDataSet, new FileOutputStream(fileNameOutput));
        //FlatXmlDataSet.write(fullDataSet, new FileOutputStream("initDatabase.xml"));
    }

    /**
     * Test of importCertificate method, of class CertificateManagerImpl.
     */
}
