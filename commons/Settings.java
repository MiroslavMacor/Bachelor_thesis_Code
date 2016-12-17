/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commons;

import cz.muni.ics.remsig.impl.TestManager;
import java.util.Properties;

/**
 *
 * @author miroslav
 */
public class Settings {    
    public static final String CONFIG_FILE = "/etc/remsig/remsig.properties";
    public static final String CONFIG_FILE_TEST = "/home/miroslav/Documents/Bakalarka/Remsig/test/testConfig/test.properties";
    public static final Properties config = TestManager.prepareConfigFile(TestManager.CONFIG_FILE_TEST);
    // db settings
    public static final String dbDriverClass = config.getProperty("dbDriverClass");
    public static final String dbConnectionUrl = config.getProperty("dbConnectionUrl");
    public static final String dbUserName = config.getProperty("dbUserName");
    public static final String dbPassword =  config.getProperty("dbPass");
    public static final String applicationContext = config.getProperty("applicationContextPath");
    public static final String initXmlDoc = config.getProperty("initialDatabaseInXml");
    
}
