/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.ics.remsig.impl;

import cz.muni.ics.remsig.common.XmlParser;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import static org.hamcrest.core.IsNot.not;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
//import cz.muni.ics.remsig.impl.TestManager;

/**
 *
 * @author miroslav
 */
public class XmlParserIT {
    
    public XmlParserIT() {
    }
    //XmlParser xmlParser = new XmlParser();
    org.w3c.dom.Document testDocument1 = null;
    org.w3c.dom.Document testDocument2 = null;
    TestManager testManager = new TestManager();
    
    
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
    /**
     * Test of setInputDocument method, of class XmlParser.
     */
    @Test
    public void testGetSetInputDocument() throws Exception
     {
          XmlParser xmlParser = new XmlParser();
         try {
            xmlParser.setInputDocument(null);
         } catch (NullPointerException e) {
             fail("Unreported nullPointerException");
         }
         
         try {
             
            testDocument1 = loadDoc("test/testFiles/testStats.xml");
            xmlParser.setInputDocument(testDocument1);
            assertEquals(testDocument1, xmlParser.getInputDocument());
            assertThat(testDocument1, not(testDocument2));
         } catch (Exception e) {
             fail("failed to set document" + e);
         }
        
        
    }

    

    /**
     * Test of setOutputDocument method, of class XmlParser.
     */
    @Test
    public void testGetSetOutputDocument() {
         XmlParser xmlParser = new XmlParser();
        try {
            xmlParser.setOutputDocument(null);
        } catch (NullPointerException e) {
            fail("Unreported nullPointer Exception");
        }
        try {
            
            testDocument1 = loadDoc("test/testFiles/testStats.xml");
            xmlParser.setOutputDocument(testDocument1);
            assertEquals(testDocument1, xmlParser.getOutputDocument());
            assertThat(testDocument1, not(testDocument2));
         } catch (Exception e) {
             fail("failed to set document" + e);
         }
    }

    /**
     * Test of getValueByXPath method, of class XmlParser.
     */
    @Test
    public void testGetValueByXPath() throws Exception {
        
        XmlParser xmlParser = new XmlParser();
        try {
            xmlParser.getValueByXPath(null);
        } catch (NullPointerException e) {
            fail("Uncaught nullPointerException");
        }
        
        testDocument1 = loadDoc("test/testFiles/testGenerate.xml");
        xmlParser.setInputDocument(testDocument1);
        assertEquals("2354",(xmlParser.getValueByXPath("/remsig/requestId")));
        String a = null;
        try {
             a =xmlParser.getValueByXPath("/remsig/wrong/path/away");
             
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
    }

    /**
     * Test of convertStringToDocument method, of class XmlParser.
     */
    @Test
    public void testConvertStringToDocument() throws Exception {
        
        XmlParser xmlParser = new XmlParser();
        try {
            xmlParser.convertStringToDocument(null);
        } catch (NullPointerException e) {
            fail("Uncaught NullPointerException"+ e.getMessage());
        }
        String xml = testManager.convertToStringFromXmlFile("test/testFiles/testGenerate.xml");
        testDocument1 = loadDoc("test/testFiles/testGenerate.xml");
        try {
        xmlParser.convertStringToDocument(xml);
        String[] xmlExtraction = new String[]{"requestId","certificateRequest","subjectKey",
                "operationId"}; 
       
        ArrayList<String> dataFromDocEx = testManager.extractMultipleElementsFromDoc(xmlExtraction, testDocument1);
        ArrayList<String> dataFromDocOr = testManager.extractMultipleElementsFromDoc(xmlExtraction, xmlParser.getInputDocument());
        assertEquals(dataFromDocEx, dataFromDocOr);
        
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
    }

    /**
     * Test of convertDocumentToString method, of class XmlParser.
     */
    @Test
    public void testConvertDocumentToString() throws Exception {
        System.out.println("convertDocumentToString");
        Document doc = null;
        XmlParser instance = new XmlParser();
        String expResult = "";
        String result = instance.convertDocumentToString(doc);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of prepareNewRemSigDocument method, of class XmlParser.
     */
    @Test
    public void testPrepareNewRemSigDocument() throws Exception {
        System.out.println("prepareNewRemSigDocument");
        XmlParser xmlParser = new XmlParser();
        testDocument1 = null;
        xmlParser.prepareNewRemSigDocument();
        assertEquals(xmlParser.getInputDocument(),testDocument1);
        // TODO review the generated test code and remove the default call to fail.
        
    }

    /**
     * Test of createRemSigElement method, of class XmlParser.
     */
    @Test
    public void testCreateRemSigElement() throws Exception {
        String name = "userId";
        String value = "25";
        String parentXPath = "/remsig";
        XmlParser xmlParser = new XmlParser();
        xmlParser.prepareNewRemSigDocument();        
        xmlParser.createRemSigElement(name, value, parentXPath);
        String result =testManager.extractElementFromXmlDoc(xmlParser.getOutputDocument(), "userId");        
        assertEquals(result, value);
        assertThat(result, not("somethingElse"));
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of createRemSigAttribute method, of class XmlParser.
     */
    @Test
    public void testCreateRemSigAttribute() throws Exception {
        String name = "userId";
        String value = "25";
        String xPath = "/remsig";
        XmlParser xmlParser = new XmlParser();
        xmlParser.prepareNewRemSigDocument(); 
        xmlParser.createRemSigAttribute(xPath, name, value);
        testManager.exportDocIntoXml("testParserCreateRemSigAttribute.xml", xmlParser.getOutputDocument());
        String[] xmlExtraction = new String[]{"dn","issuer","serialNumber",
                "expirationFrom","expirationTo","certificatePEM","chainPEM"};
        ArrayList<String[]>  a =testManager.parseXmlDoc(xmlParser.getOutputDocument(), new String[]{"userID"});       
        assertEquals(value, a.get(0)[0]);
    }

    /**
     * Test of createStyleSheet method, of class XmlParser.
     */
    @Test
    public void testCreateStyleSheet() {
        System.out.println("createStyleSheet");
        XmlParser instance = new XmlParser();
        instance.createStyleSheet();
        //testDocument1.ge
        
    }
    
    public Document loadDoc(String filename) throws Exception
    {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new File(filename));
        return document;
    }
    
    
}
