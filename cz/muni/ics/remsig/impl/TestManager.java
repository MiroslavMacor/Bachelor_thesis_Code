/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.ics.remsig.impl;

import static com.sun.xml.internal.messaging.saaj.util.Base64.base64Decode;
import cz.muni.ics.remsig.common.IntegrationTest;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_EXPIRED;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_NOT_YET_VALID;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_PASSWORD_RESET;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_REVOKED;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_SUSPENDED;
import static cz.muni.ics.remsig.impl.CertificateManagerImpl.STATE_VALID;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
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
import static org.hamcrest.core.IsNot.not;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;

/**
 *Manages methods that are not tests but tests use  them in some manner 
 *   extract - returns some value 
 *   export - creates some output document 
 *   check - check values 
 *   convert - gets values in one format and returns in other 
 * @author miroslav
 */
public class TestManager {
    private CertificateManagerImpl manager;
    private Properties configuration;
    public static final String CONFIG_FILE = "/etc/remsig/remsig.properties";
    public static final String CONFIG_FILE_TEST = "/home/miroslav/Documents/Bakalarka/Remsig/test/testConfig/test.properties";
    private final static org.slf4j.Logger logger
            = LoggerFactory.getLogger(CertificateManagerImplTest.class);
    private IDatabaseTester databaseTester;
    private IDatabaseConnection dbUnitConnection;
    private Connection connection;
    private java.sql.Statement statement;

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
    

    private String initXmlDoc = "/test/testFiles/NewInitDatabase.xml"; // dataset
    
    private ITable expectedTable = null;
    
    public void databaseInit()   {
        try {
            databaseTester = new JdbcDatabaseTester("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/Remsig?zeroDateTimeBehavior=convertToNull", "root", "");        
        IDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(new FileInputStream(initXmlDoc));
        
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.setDataSet(dataSet);
        
        databaseTester.setSetUpOperation(DatabaseOperation.REFRESH);
        databaseTester.onSetup();
        dbUnitConnection = databaseTester.getConnection();
        connection = dbUnitConnection.getConnection();

       statement = connection.createStatement();
        expectedTable = dataSet.getTable("credentials");
        } catch (Exception e) {
        }
        
    
}
    
    @Before
    public void setUp() throws Exception {

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
        IDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(new FileInputStream(initXmlDoc));
        
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
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.onTearDown();
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
    /**
     * extracts arrayList of first apperence of selected elements from doc
     * @param elements
     * @param doc
     * @return arrayList of values from doc
     */
    public ArrayList<String> extractMultipleElementsFromDoc(String[] elements, org.w3c.dom.Document doc){
        ArrayList<String> result = new ArrayList<>();
        for (String element : elements) {
            result.add(extractElementFromXmlDoc(doc, element));
        }
        
        return result;
              
    
    }/**
     * extracts multiple collums from database depending on rule (userId = 1)
     * @param dataToBeExtracted String[] collums names
     * @param databaseName name of database
     * @param rule of selection criteria 
     * @return 
     */
    public ArrayList<String> extractMultipleCollumsFromDatabase(String[] dataToBeExtracted,String databaseName,String rule)
    {
        databaseInit();
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
            System.out.println("Failed to load data "+rowName+" from "+ databaseName+" "+ ex);
        }   
        }
        return result;
        
    }

    
    /**
     * Extracts selected colum  from database of choice by sql expresion
     * @param dataToBeExported name of the collum
     * @param databaseName name of database, established connection is expected
     * @param rule WHERE clause 
     * @return value in String
     */
    public String extractOneCollumFromDatabase(String dataToBeExported, String databaseName,String rule)
    {
        databaseInit();
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
    
    /**
     *  Setting up id of 3 certificates by a little bit arbitrary rule
     */
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
     * Takes file name of xml file and return its unchanged content as String
     * @param input File name e.g xmlfile.xml     
     * @return Content of xml file converted into string
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public String convertToStringFromXmlFile(String input) throws FileNotFoundException, IOException
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
        
        return xml2String;
    }
    public String convertToStringFromXmlFile(String input, Boolean a) 
    {
        try {
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
        
        return xml2String;
            
        } catch (Exception e) {
        }
        return null;
     
    }
    
    /**
     * Converts content of pre-loaded document into string 
     * @param document to be converted 
     * @return String converted from doc document
     */
    public String convertToStringFromDoc(org.w3c.dom.Document doc)    {
    DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
    LSSerializer lsSerializer = domImplementation.createLSSerializer();
    return lsSerializer.writeToString(doc);   
}
    /**
     * Takes filepath of p12 and returns its content as String
     * @param filePath to p12 file
     * @return convert through Base64 encoding bytes and returns string
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
     * Takes filePath and returns its content as string read through bytes 
     * @param filePath path to file
     * @return Content of file
     */
    public String loadFile(String filePath)
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
     * takes file path and  returns its content  as byte[]
     */
    public byte[] loadFileBytes(String filePath)
    {
        byte[] encoded = null;
         try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            encoded = Base64.encode(bytes);
         }catch(Exception e)
         {}
         return encoded;
    }

    /**
     * Controls by result set if collums in  string[] values are not null
     * @param inRs result set of database to 
     * @param valuesToChceck collums to chceck 
     * @return true if sqlException wasnt thrown
     */
    public boolean chcekNullValuesOfDatabase(ResultSet inRs, String[] valuesToChceck) {
        try {
            while (inRs.next()) {
                for (String databaseEntry : valuesToChceck) {
                    assertNotNull("Values weren't inserted correctly some of them were null", inRs.getObject(databaseEntry));
                }
                //                    ArrayList<String> singleAddress = new ArrayList<String>(); array list declarataion for posiible changes 
            }

        } catch (SQLException e) {
            return false;
        }

        return true;
    }
    /**
     * Check if the selected collums in two of database entries are identical 
     * @param inRs
     * @param valuesToChceck
     * @return 
     */
    public boolean chcekValuesOfTwoDatabaseEntries(ResultSet inRs, String[] valuesToChceck) {
        try {
            LinkedList<Object> set1 = new LinkedList<Object>();
            LinkedList<Object> set2 = new LinkedList<Object>();
            
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
                    
                    
                }
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
    public boolean chcekNonEqualValuesOfDatabase(ResultSet inRs, String[] valuesToChceck, int k) {
        
        try {
            LinkedList<Object> set1 = new LinkedList<Object>();
            LinkedList<Object> set2 = new LinkedList<Object>();
            LinkedList<Object> set3 = new LinkedList<Object>();
            LinkedList<Object> set4 = new LinkedList<Object>();
            LinkedList<Object> set5 = new LinkedList<Object>();
            
            LinkedList<LinkedList<Object>> all = new LinkedList<LinkedList<Object>>();
            
            
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
                            
                    
                }
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
                        

        } catch (SQLException e) {
        }

        return true;
    }
    
    /**
     * Takes input document and creates new output XML document
     * @param outputFilename expects full XML name e.g. file.xml
     * @param inputDoc to be transfered
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    public void exportDocIntoXml(String outputFilename, org.w3c.dom.Document inputDoc) throws TransformerConfigurationException, TransformerException 
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(new File(outputFilename));
        Source input = new DOMSource(inputDoc);
        transformer.transform(input, output);
    }
    
    
    
    /**
     * returns element from doc document converted into string
     * @param docToBeExtracted 
     * @param elementName 
     * @return selected element as string
     */
    
    public String extractElementFromXmlDoc(org.w3c.dom.Document docToBeExtracted, String elementName)
    {
        String  result = null;
        try {

            org.w3c.dom.Document doc = docToBeExtracted;
            // normalize text representation
            doc.getDocumentElement ().normalize ();

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

                }//end of if clause


            }//end of for loop with s var


        }catch (Exception e) {
        
        }catch (Throwable t) {
        t.printStackTrace ();
        }
        
        
        return result;
    }
    public String extractElementFromXmlDoc(String inputFile, String elementName)
    {
        String  result = null;
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(inputFile));
            
            // normalize text representation
            doc.getDocumentElement ().normalize ();

            //doc.getElementById(evaDefPass);
            
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
    public void generateXmlForSetUp() throws RemSigException, Exception
    {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        manager = new CertificateManagerImpl(configuration);
        manager.setJdbcTemplate((JdbcTemplate) ac.getBean("jdbcTemplate"));
        Security.addProvider(new BouncyCastleProvider());
        databaseTester.setSetUpOperation(DatabaseOperation.DELETE_ALL);
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
        
        
        
        String p12Certificate1 = loadPKCS12("test/testFiles/sub1-cert.p12");
        String p12Certificate2 = loadPKCS12("test/testFiles/sub2-cert.p12");
        String p12Certificate3 = loadPKCS12("test/testFiles/sub3-cert.p12");
        
                
        String p12passToCer1 = "123456";
        String p12passToCer2 = "123456";                
        String p12passToCer3 = "123456";
        
        manager.importPKCS12(anderson, p12Certificate1, andersonDefPass, p12passToCer1);
        manager.importPKCS12(bobaFet, p12Certificate2, bobaFetDefPass, p12passToCer2);
        manager.importPKCS12(cyril, p12Certificate1, cyrilDefPass, p12passToCer1);
        
        
        try {
            exportsDatabaseIntoXml("NewInitDatabase.xml");
        } catch (Exception ex) {
        }
                
        
    }
    /**
     * Complex testing of input documents. Method expects ordered array in this 
     * fashion. From first to diffEnd-1 documents that aren't same. From diffEnd 
     * to sameEnd-1 documents that were created differently but are identical
     * and rest null documents
     * 
     * @param testSubjects
     * @param diffEnd equivalent to one before last
     * @param sameEnd 
     * @return number representation of test failing
     */
   public int chceckDocuments(org.w3c.dom.Document[] testSubjects, int diffEnd, int sameEnd) {
        for (int i = 0; i < diffEnd; i++) {
            for (int j = i; j < diffEnd - 1; j++) {
                if (testSubjects[i] == testSubjects[j + 1]) {
                    return 1;
                    // testing data that are expected do be different
                }
            }

        }
        for (int i = diffEnd; i < sameEnd; i++) {
            for (int j = i; j < sameEnd - 1; j++) {
                if (testSubjects[i] != testSubjects[j + 1]) {
                    return 2;
                    // testing data that are expected to be same against each other a
                }
            }
        }
        for (int i = sameEnd; i < testSubjects.length; i++) {
            for (int j = i; j < testSubjects.length - 1; j++) {
                if (testSubjects[i] != testSubjects[j + 1]) {
                    return 3;
                    // null object testing against each other
                }
            }
        }
        return 0;
    }/**
     * sets all testDocument to null value
     */
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
    }
    
    /** 
     * Exports current database data into xml document FileName is first parameter
     * 
     e.g. * exportXml(working.xml);
     * @param fileNameOutput
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
    }
    
/*    public PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
    byte[] clear = Base64.encode(key64.getBytes());
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
    KeyFactory fact = KeyFactory.getInstance("DSA");
    PrivateKey priv = fact.generatePrivate(keySpec);
    Arrays.fill(clear, (byte) 0);
    return priv;
}*/
    
    public PrivateKey loadPrivateKey(String filename)  throws Exception {

    File f = new File(filename);
    FileInputStream fis = new FileInputStream(f);
    DataInputStream dis = new DataInputStream(fis);
    byte[] keyBytes = new byte[(int)f.length()];
    dis.readFully(keyBytes);
    dis.close();

    PKCS8EncodedKeySpec spec =
      new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePrivate(spec);
  }
    public PublicKey getPublic(String filename)throws Exception {

    File f = new File(filename);
    FileInputStream fis = new FileInputStream(f);
    DataInputStream dis = new DataInputStream(fis);
    byte[] keyBytes = new byte[(int)f.length()];
    dis.readFully(keyBytes);
    dis.close();

    X509EncodedKeySpec spec =
      new X509EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePublic(spec);
  }
 
    public ArrayList<String[]> parseXmlDoc(String filename)throws Exception{
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new File(filename));

        ArrayList<String[]> result = new ArrayList<String[]>();

        NodeList nodeList = document.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) nodeList.item(i);
                    if (el.getNodeName().contains("certificate")) {
                        String notQualified = el.getElementsByTagName("notQualified").item(0).getTextContent();
                        String userId = el.getElementsByTagName("userId").item(0).getTextContent();
                        String id = el.getElementsByTagName("id").item(0).getTextContent();
                        String dn = el.getElementsByTagName("dn").item(0).getTextContent();
                        String issuer = el.getElementsByTagName("issuer").item(0).getTextContent();
                        String serialNumber = el.getElementsByTagName("serialNumber").item(0).getTextContent();
                        String expirationFrom = el.getElementsByTagName("expirationFrom").item(0).getTextContent();
                        String expirationTo = el.getElementsByTagName("expirationTo").item(0).getTextContent();

                        String [] partial = new String[]{userId,id,dn,issuer,serialNumber,expirationFrom,expirationTo};
                        result.add(partial);
                        
                    }


            }

        }
        return result;
    }
    /**
     * very easy transofrmation so it returns ArrayList<ArrayList<String>>
     * @param filename
     * @param elementsForExtaction
     * @return
     * @throws Exception 
     */
    public ArrayList<String[]> parseXmlDoc(String filename, String[] elementsForExtaction)throws Exception{
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new File(filename));

        ArrayList<String[]> result = new ArrayList<>();
        String[] partialResult = new String[elementsForExtaction.length];
        String[] partialNull = new String[elementsForExtaction.length];
        ArrayList<String> parResult= new ArrayList<>(elementsForExtaction.length);
        
        NodeList nodeList = document.getElementsByTagName("*");
        int j =0;
        for (int i = 0; i < nodeList.getLength(); i++) {
            
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) nodeList.item(i);
                    if (el.getNodeName().compareTo("certificate") == 0){ 
                        j = 0;
                        for (String element : elementsForExtaction) {
                             partialResult[j] = el.getElementsByTagName(element).item(0).getTextContent();
                            if ("chainPEM".equals(element) || "certificatePEM".equals(element)) {
                                partialResult[j] = changeEndCertificate(partialResult[j]);
                            }
                            ++j;
                            
                        }
                        
                        result.add(partialResult);
                        partialResult = partialNull;
                       
                       
                        
                    }


            }

        }
        return result;
    }
    public ArrayList<String[]> parseXmlDoc(Document document, String[] elementsForExtaction)throws Exception{
        
        ArrayList<String[]> result = new ArrayList<>();        
        String[] partialNull = new String[elementsForExtaction.length];
        ArrayList<String> parResult= new ArrayList<>(elementsForExtaction.length);
        
        NodeList nodeList = document.getElementsByTagName("*");
        
        int j =0;
        for (int i = 0; i < nodeList.getLength(); i++) {
            
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) nodeList.item(i);
                    if (el.getNodeName().compareTo("certificate") == 0){ 
                        j = 0;
                        String[] partialResult = new String[elementsForExtaction.length];
                        for (String element : elementsForExtaction) {
                            
                            
                            partialResult[j] = el.getElementsByTagName(element).item(0).getTextContent();
                            if ("chainPEM".equals(element) || "certificatePEM".equals(element)) {
                                partialResult[j] = changeEndCertificate(partialResult[j]);
                            }
                            
                            
                            ++j;
                            
                        }
                        
                        result.add(partialResult);
                        partialResult = partialNull;
                       
                       
                        
                    }


            }

        }
        return result;
    }

    /**
     * 
     * @param filename
     * @param elementsForExtaction
     * @return
     * @throws Exception
     */
    public ArrayList<ArrayList<String>> parseXmlDocToList(String filename, String[] elementsForExtaction)throws Exception{
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new File(filename));

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        
        NodeList nodeList = document.getElementsByTagName("*");
        int j =0;
        for (int i = 0; i < nodeList.getLength(); i++) {
            
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) nodeList.item(i);
                    if (el.getNodeName().compareTo("certificate") == 0){ // contains("certificate")) {
                        ArrayList<String> parResult= new ArrayList<>();    
                        for (String element : elementsForExtaction) {
                            
                            parResult.add(el.getElementsByTagName(element).item(0).getTextContent());
                            ++j;
                            
                        }
                        
                        result.add(parResult);
                        
                                             
                    }

            }

        }
        return result;
    }
    
    public static Logger setUpLogger(Class inputClasss){
    Logger logger = org.apache.log4j.Logger.getLogger(inputClasss);
    PropertyConfigurator.configure("/home/miroslav/Documents/Bakalarka/"
                + "Remsig/test/cz/muni/ics/remsig/impl/log4j.properties");
                
        logger.debug("this is a debug log message");
        return logger;    
    }
    /**
     * 
     * @return 
     * @throws RemSigException 
     */
    
    /**
     *
     * @param configFile
     * @return
     */
    public static Properties prepareConfigFile(String configFile){
        Properties configuration = new Properties();

        try (FileInputStream input = new FileInputStream(configFile)) {
                configuration.load(input);
        } catch (IOException ex)  {
               return null; 
        }
        return configuration;
    }
    
    
}
