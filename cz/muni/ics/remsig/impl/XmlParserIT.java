/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.ics.remsig.impl;

import cz.muni.ics.remsig.common.XmlParser;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    Properties config = TestManager.prepareConfigFile(TestManager.CONFIG_FILE_TEST);    
    final String testGenerateRequest = config.getProperty("testGenerateRequest");
    final String testStats = config.getProperty("testStats");
    
    
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        testDocument1 = null;
        testDocument2 = null;
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
            testDocument1 = loadDoc(testStats);
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
            testDocument1 = loadDoc(testStats);
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
        
        testDocument1 = loadDoc(testGenerateRequest);
        xmlParser.setInputDocument(testDocument1);
        assertEquals("2354",(xmlParser.getValueByXPath("/remsig/requestId")));        
        try {
            String incorrectData = xmlParser.getValueByXPath("/remsig/wrong/path/away");
            assertTrue(incorrectData.isEmpty());             
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
        String xml = testManager.convertToStringFromXmlFile(testGenerateRequest);
        testDocument1 = loadDoc(testGenerateRequest);
        try {
            xmlParser.convertStringToDocument(xml);
            String[] xmlExtraction = new String[]{"requestId","certificateRequest","subjectKey",
                    "operationId"}; 

            ArrayList<String> dataFromDocEx = testManager.extractMultipleElementsFromDoc(xmlExtraction, testDocument1);
            ArrayList<String> dataFromDocOr = testManager.extractMultipleElementsFromDoc(xmlExtraction, xmlParser.getInputDocument());
            assertEquals(dataFromDocEx, dataFromDocOr);
        
            XmlParser xmlParserIncorrectData = new XmlParser();
            xmlParserIncorrectData.convertStringToDocument("NonParsableData");
            assertThat(xmlParserIncorrectData.getInputDocument(), not(xmlParser.getInputDocument()));            
            
        }catch (Exception e){
            fail(e.getMessage());
        }
    }

    /**
     * Test of convertDocumentToString method, of class XmlParser.
     */
    @Test
    public void testConvertDocumentToString() throws Exception {
        XmlParser xmlParser = new XmlParser();
        try {
            xmlParser.convertDocumentToString(null);
        } catch (NullPointerException e) {
            fail("Uncaught NullPointerException"+ e.getMessage());
        }
        String xml = testManager.convertToStringFromXmlFile(testGenerateRequest);
        testDocument1 = loadDoc(testGenerateRequest);        
        testDocument2 = loadDoc(testStats);
        try {
            assertEquals(xml, xmlParser.convertDocumentToString(testDocument1));
            assertThat(xml, not(xmlParser.convertDocumentToString(testDocument2)));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of prepareNewRemSigDocument method, of class XmlParser.
     */
    @Test
    public void testPrepareNewRemSigDocument() throws Exception {
        XmlParser xmlParser = new XmlParser();        
        testDocument1 = null;
        assertEquals(xmlParser.getOutputDocument(),testDocument1);
        xmlParser.prepareNewRemSigDocument();
        assertThat(xmlParser.getOutputDocument(),not(testDocument1));        
    }

    /**
     * Test of createRemSigElement method, of class XmlParser.
     */
    @Test
    public void testCreateRemSigElement()  {
        XmlParser xmlParser = new XmlParser();
        String name = "userId";
        String value = "25";
        String parentXPath = "/remsig";
        try {
            xmlParser.createRemSigElement(null, value, parentXPath);
            xmlParser.createRemSigElement(name, null, parentXPath);
            xmlParser.createRemSigElement(name, value, null);
            xmlParser.createRemSigElement(null, value, null);
            xmlParser.createRemSigElement(null, null, parentXPath);
            xmlParser.createRemSigElement(name, null, null);
            xmlParser.createRemSigElement(null, null, null);
        } catch (NullPointerException e) {
            fail("Uncaught null pointer exception + "+ e.getMessage());
        } catch (RemSigException ex) {            
        }
        
        try {
            xmlParser.prepareNewRemSigDocument();        
            xmlParser.createRemSigElement(name, value, parentXPath);
            String result =testManager.extractElementFromXmlDoc(xmlParser.getOutputDocument(), "userId");
            assertEquals(result, value);
            assertThat(result, not("somethingElse"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of createRemSigAttribute method, of class XmlParser.
     */
    @Test
    public void testCreateRemSigAttribute() throws Exception {
        XmlParser xmlParser = new XmlParser();
        String name = "userId";
        String value = "25";
        String parentXPath = "/remsig";
        String result = "";
        
        try {
            xmlParser.createRemSigAttribute(null, name, value);
            xmlParser.createRemSigAttribute(parentXPath, null, value);
            xmlParser.createRemSigAttribute(parentXPath, name, null);
            xmlParser.createRemSigAttribute(null, name, null);
            xmlParser.createRemSigAttribute(null, null, value);
            xmlParser.createRemSigAttribute(parentXPath, null, null);
            xmlParser.createRemSigAttribute(null, null, null);
        } catch (NullPointerException e) {
            fail("Uncaught null pointer exception + "+ e.getMessage());
        } catch (RemSigException ex) {            
        }
        try {
            xmlParser.prepareNewRemSigDocument(); 
            xmlParser.createRemSigAttribute(parentXPath, name, value);
            result = testManager.extractAttributeFromDoc(xmlParser.getOutputDocument(), "userId","remsig");            
        } catch (Exception e) {
            fail(e.getMessage());
        }
            assertEquals(value, result);
            assertThat(result, not("somethingElse"));            
    }

    /**
     * Test of createStyleSheet method, of class XmlParser.
     */
    @Test
    public void testCreateStyleSheet() {        
        XmlParser xmlParser = new XmlParser();        
        try {
            xmlParser.createStyleSheet();
        } catch (NullPointerException e) {
            fail("Uncaught null pointer exception + "+ e.getMessage());
        }
        try {
            xmlParser.prepareNewRemSigDocument();
            xmlParser.createStyleSheet();
            
        }   catch (RemSigException ex) {            
        }   catch (Exception e){
            fail(e.getMessage());
        }
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
