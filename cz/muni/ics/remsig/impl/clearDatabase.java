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
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.junit.Assert;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 
public class clearDatabase extends DBTestCase {

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
    

    private String initXmlDoc = "/home/miroslav/Documents/toDelete/80/input/performanceTestDatabase.xml";
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

    public clearDatabase(String name) {
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
        //expectedTable = dataSet.getTable("credentials");

    }

    @After
    public void tearDown() throws Exception {
        //databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        //databaseTester.onTearDown();
        
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

}