package org.commons.util;

import org.commons.exceptions.PropertiesUtilException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

public class PropertiesHandler {
    private ClassLoader classLoader;
    private String logFileSourceName = StringUtil.EMPTY_STRING;
    private Properties properties = new Properties();
    private String absolutePropertiesFilePath;
    private static final Logger log = Logger.getLogger(PropertiesHandler.class);

    PropertiesHandler() {
        this.classLoader = PropertiesHandler.class.getClassLoader();
    }

    void init(String logFileSourceName, ClassLoader classLoader) {
        this.logFileSourceName = logFileSourceName;
        try {
            this.classLoader = classLoader;
            this.properties = this.loadConfig(logFileSourceName, classLoader);
        } catch (PropertiesUtilException e) {
            log.error(e.getMessage());
        }
    }

    void init(String logFileSourceName) {
        this.logFileSourceName = logFileSourceName;
        try {
            this.properties = this.loadConfig(logFileSourceName);
        } catch (PropertiesUtilException e) {
            log.error(e.getMessage());
        }
    }

    public PropertiesHandler(String logFileSourceName, ClassLoader classLoader) {
        init(logFileSourceName, classLoader);
    }

    public PropertiesHandler(String logFileSourceName, Class clazz) {
        init(logFileSourceName, clazz.getClassLoader());
    }

    public PropertiesHandler(String logFileSourceName) {
        init(logFileSourceName);
    }

    public PropertiesHandler(File propertiesFile, Class clazz) throws PropertiesUtilException {
        classLoader = clazz.getClassLoader();
        logFileSourceName = propertiesFile.getName();
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            throw new PropertiesUtilException(PropertiesUtilException.CONFIG_FILE_ERROR + e);
        }
    }

    public String getLogFileSourceName() {
        return logFileSourceName;
    }

    public Properties getProperties() {
        return properties;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getAbsolutePropertiesFilePath() {
        return absolutePropertiesFilePath;
    }

    /**
     * loadConfig metodo encargado de devolver un property determinado dado su nombre
     *
     * @param logFileSourceName String con el nombre del property a ser cargado
     * @return Properties con la informacion solicitada
     * @throws PropertiesUtilException en caso de no conseguir el archivo o de ocurrir otro error
     */
    public Properties loadConfig(String logFileSourceName) throws PropertiesUtilException {
        Properties properties = new Properties();
        URL resource;
        try {
            log.info("Attempting to load properties for file: " + logFileSourceName);
            InputStream inputstream;
            logFileSourceName = URLDecoder.decode(logFileSourceName, "UTF-8");
            log.info("\tSearching directly from absolute path: " + logFileSourceName);
            inputstream = new java.io.FileInputStream((new File(logFileSourceName)));
            resource = (new File(logFileSourceName)).toURL();
            if (resource != null) {
                absolutePropertiesFilePath = resource.getPath();
            }
            log.info("\tFile: " + logFileSourceName + " found on classpath");
            properties.load(inputstream);
            inputstream.close();
        } catch (IOException ioe) {
            log.info("\tFile: " + logFileSourceName + " NOT found on classpath");
            throw new PropertiesUtilException(PropertiesUtilException.IOEXCEPTION_ERROR + ioe);
        } catch (Exception e) {
            log.info("\tFile: " + logFileSourceName + " NOT found on classpath");
            throw new PropertiesUtilException(PropertiesUtilException.CONFIG_FILE_ERROR + e);
        } catch (Throwable t) {
            log.info("\tFile: " + logFileSourceName + " NOT found on classpath");
            throw new PropertiesUtilException(PropertiesUtilException.ERROR + t);
        }
        return properties;
    }

    /**
     * loadConfig metodo encargado de devolver un property determinado dado su nombre
     *
     * @param logFileSourceName String con el nombre del property a ser cargado
     * @param clazz             Clase contenida por el ClassLoader responsable por la carga de clases
     * @return Properties con la informacion solicitada
     * @throws PropertiesUtilException en caso de no conseguir el archivo o de ocurrir otro error
     */
    public Properties loadConfig(String logFileSourceName, Class clazz) throws PropertiesUtilException {
        return loadConfig(logFileSourceName, clazz.getClassLoader());
    }

    /**
     * loadConfig metodo encargado de devolver un property determinado dado su nombre
     *
     * @param logFileSourceName String con el nombre del property a ser cargado
     * @param cl                ClassLoader objeto responsable por la carga de clases
     * @return Properties con la informacion solicitada
     * @throws PropertiesUtilException en caso de no conseguir el archivo o de ocurrir otro error
     */
    public Properties loadConfig(String logFileSourceName, ClassLoader cl) throws PropertiesUtilException {
        Properties properties = new Properties();
        URL resource;

        if (StringUtil.isNullOrEmpty(logFileSourceName)) {
            throw new PropertiesUtilException(PropertiesUtilException.CONFIG_FILE_NAME_EMPTY);
        }
        try {
            log.info("Attempting to load properties for file: " + logFileSourceName);
            log.info("\tSearching thru classloader (1): " + cl.toString());
            InputStream inputstream = cl.getResourceAsStream(logFileSourceName);
            resource = cl.getResource(logFileSourceName);
            if (resource != null) {
                absolutePropertiesFilePath = resource.getPath();
            }
            if (inputstream == null) {
                log.info("\tSearching thru classloader (2): " + getClassLoader().toString());
                inputstream = getClassLoader().getResourceAsStream(logFileSourceName);
                resource = getClassLoader().getResource(logFileSourceName);
                if (resource != null) {
                    absolutePropertiesFilePath = resource.getPath();
                }
                if (inputstream == null) {
                    log.info("\tSearching thru classloader (3): " + getClassLoader().getParent().toString());
                    inputstream = getClassLoader().getParent().getResourceAsStream(logFileSourceName);
                    resource = getClassLoader().getParent().getResource(logFileSourceName);
                    if (resource != null) {
                        absolutePropertiesFilePath = resource.getPath();
                    }
                    if (inputstream == null) {
                        log.info("\tSearching thru classloader (4): " + ClassLoader.getSystemClassLoader().toString());
                        inputstream = ClassLoader.getSystemClassLoader().getResourceAsStream(logFileSourceName);
                        resource = ClassLoader.getSystemClassLoader().getResource(logFileSourceName);
                        if (resource != null) {
                            absolutePropertiesFilePath = resource.getPath();
                        }
                        if (inputstream == null) {
                            log.info("\tSearching directly from absolute path (5): " + logFileSourceName);
                            inputstream = new java.io.FileInputStream((new File(logFileSourceName)));
                            resource = (new File(logFileSourceName)).toURL();
                            if (resource != null) {
                                absolutePropertiesFilePath = resource.getPath();
                            }
                        }
                    }
                }
            }
            if (inputstream != null) {
                log.info("\tFile: " + logFileSourceName + " found on classpath");
                properties.load(inputstream);
                inputstream.close();
            } else {
                log.info("\tFile: " + logFileSourceName + " NOT found on classpath");
            }
        } catch (IOException ioe) {
            throw new PropertiesUtilException(PropertiesUtilException.IOEXCEPTION_ERROR + ioe);
        } catch (Exception e) {
            throw new PropertiesUtilException(PropertiesUtilException.CONFIG_FILE_ERROR + e);
        } catch (Throwable t) {
            throw new PropertiesUtilException(PropertiesUtilException.ERROR + t);
        }
        return properties;
    }

}
