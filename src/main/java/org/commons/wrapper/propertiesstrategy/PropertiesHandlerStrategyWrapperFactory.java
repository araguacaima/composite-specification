package org.commons.wrapper.propertiesstrategy;

import org.commons.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;


public class PropertiesHandlerStrategyWrapperFactory {

    protected static final Logger log = Logger.getLogger(PropertiesHandlerStrategyWrapperFactory.class);
    public String propertesHandlerStrategyPolicy = StringUtil.EMPTY_STRING;
    public static final String PROPERTIES_HANDLER_STRATEGY_POLICY = "PROPERTIES_HANDLER_STRATEGY_POLICY";
    private PropertiesHandlerStrategyWrapperInterface propertiesHandlerStrategy;
    String applicationName;
    String defaultFileName;

    public PropertiesHandlerStrategyWrapperFactory(String applicationName, String defaultFileName) {
        this.applicationName = applicationName;
        this.defaultFileName = defaultFileName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getDefaultFileName() {
        return defaultFileName;
    }

    public void setDefaultFileName(String defaultFileName) {
        this.defaultFileName = defaultFileName;
    }

    public String getPropertesHandlerStrategyPolicy() {
        return propertesHandlerStrategyPolicy;
    }

    public void setPropertesHandlerStrategyPolicy(String propertesHandlerStrategyPolicy) {
        log.warn("Current properties handler strategy policy was change from: "
                + this.propertesHandlerStrategyPolicy
                + " to "
                + propertesHandlerStrategyPolicy);
        this.propertesHandlerStrategyPolicy = propertesHandlerStrategyPolicy;
    }

    public static PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyWithoutPoliciesNorFiles(String applicationName) {
        log.warn("The default PropertiesHandlerStrategy will be created with no policies nor properties files");
        PropertiesHandlerStrategyWrapperInterface propertiesHandlerStrategy;
        propertiesHandlerStrategy = new PropertiesHandlerStrategyDefault(applicationName);
        return propertiesHandlerStrategy;
    }

    public static PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyWithoutPolicies(String applicationName,
                                                                                                           String label,
                                                                                                           String defaultFileName) {
        log.info("Creating a PropertiesHandlerStrategy without Policies based on label '"
                + label
                + "', application name '"
                + applicationName
                + "' and default file name '"
                + defaultFileName
                + "'");
        PropertiesHandlerStrategyWrapperInterface propertiesHandlerStrategy;
        if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_DB.equals(label)) {
            propertiesHandlerStrategy = new PropertiesHandlerStrategyDB(applicationName);
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_CLASSPATH.equals(label)) {
            propertiesHandlerStrategy = new PropertiesHandlerStrategyClassPath(applicationName, defaultFileName);
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_PATH.equals(label)) {
            propertiesHandlerStrategy = new PropertiesHandlerStrategyPath(applicationName, defaultFileName);
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_FTP.equals(label)) {
            propertiesHandlerStrategy = new PropertiesHandlerStrategyFTP(applicationName);
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_URL.equals(label)) {
            propertiesHandlerStrategy = new PropertiesHandlerStrategyURL(applicationName);
        } else {
            log.warn("Is not possible to create a PropertiesHandlerStrategy based on label '"
                    + label
                    + "'. The default one will be used");
            propertiesHandlerStrategy = new PropertiesHandlerStrategyDefault(applicationName, defaultFileName);
        }
        log.info("A PropertiesHandlerStrategy of type '"
                + label
                + "' ("
                + propertiesHandlerStrategy.getClass().getName()
                + ") has been created!");

        return propertiesHandlerStrategy;
    }

    public PropertiesHandlerStrategyWrapperInterface buildPropertiesHandlerStrategyPolicies(String label) {
        log.info("Building a PropertiesHandlerStrategy Policy based on label '" + label + "'");
        PropertiesHandlerStrategyWrapperInterface propertiesHandlerStrategy;
        if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_DB.equals(label)) {
            propertiesHandlerStrategy = createPropertiesHandlerStrategyDB();
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_CLASSPATH.equals(label)) {
            propertiesHandlerStrategy = createPropertiesHandlerStrategyClassPath();
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_PATH.equals(label)) {
            propertiesHandlerStrategy = createPropertiesHandlerStrategyPath();
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_FTP.equals(label)) {
            propertiesHandlerStrategy = createPropertiesHandlerStrategyFTP();
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_URL.equals(label)) {
            propertiesHandlerStrategy = createPropertiesHandlerStrategyURL();
        } else {
            log.warn("Is not possible to create a PropertiesHandlerStrategy based on label '"
                    + label
                    + "'. The default one will be used");
            propertiesHandlerStrategy = createPropertiesHandlerStrategyDefault();
        }
        log.info("A PropertiesHandlerStrategy of type '"
                + label
                + "' ("
                + propertiesHandlerStrategy.getClass().getName()
                + ") has been created!");
        return propertiesHandlerStrategy;
    }

    public PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyDefault() {
        log.info("Creating a DEFAULT PropertiesHandlerStrategy");
        propertiesHandlerStrategy = new PropertiesHandlerStrategyDefault(applicationName, defaultFileName);
        if (StringUtil.isNullOrEmpty(propertesHandlerStrategyPolicy)) {
            PropertiesHandlerStrategyWrapperInterface nextDB = new PropertiesHandlerStrategyDB(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextClassPath = new PropertiesHandlerStrategyClassPath(
                    applicationName,
                    defaultFileName);
            PropertiesHandlerStrategyWrapperInterface nextPath = new PropertiesHandlerStrategyPath(applicationName,
                    defaultFileName);
            PropertiesHandlerStrategyWrapperInterface nextURL = new PropertiesHandlerStrategyURL(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextFTP = new PropertiesHandlerStrategyFTP(applicationName);
            nextDB.setNext(nextClassPath);
            nextClassPath.setNext(nextPath);
            nextPath.setNext(nextURL);
            nextURL.setNext(nextFTP);
            propertiesHandlerStrategy.setNext(nextDB);
            log.info(
                    "The default chain of responsabilities has loaded because of no strategy policy was found. The current chain of properties' handlers for DEFAULT strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        } else {
            propertiesHandlerStrategy = buildChainOfResponsability();
            log.info(
                    "A configurated chain of responsabilities has been found. The current chain of properties' handlers for DEFAULT strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        }
        return propertiesHandlerStrategy;
    }

    public PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyFTP() {
        log.info("Creating a FTP PropertiesHandlerStrategy");
        propertiesHandlerStrategy = new PropertiesHandlerStrategyFTP(applicationName);
        if (!StringUtil.isNullOrEmpty(propertesHandlerStrategyPolicy)) {
            propertiesHandlerStrategy = buildChainOfResponsability();
            log.info(
                    "A configurated chain of responsabilities has been found. The current chain of properties' handlers for FTP strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        } else {
            log.info(
                    "The default chain of responsabilities has loaded because of no strategy policy was found. The current chain of properties' handlers for FTP strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        }
        return propertiesHandlerStrategy;
    }

    public PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyPath() {
        log.info("Creating a PATH PropertiesHandlerStrategy");
        propertiesHandlerStrategy = new PropertiesHandlerStrategyPath(applicationName);
        if (StringUtil.isNullOrEmpty(propertesHandlerStrategyPolicy)) {
            PropertiesHandlerStrategyWrapperInterface nextURL = new PropertiesHandlerStrategyURL(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextFTP = new PropertiesHandlerStrategyFTP(applicationName);
            nextURL.setNext(nextFTP);
            propertiesHandlerStrategy.setNext(nextURL);
            log.info(
                    "The default chain of responsabilities has loaded because of no strategy policy was found. The current chain of properties' handlers for PATH strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        } else {
            propertiesHandlerStrategy = buildChainOfResponsability();
            log.info(
                    "A configurated chain of responsabilities has been found. The current chain of properties' handlers for PATH strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        }
        return propertiesHandlerStrategy;
    }

    public PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyClassPath() {
        log.info("Creating a CLASSPATH PropertiesHandlerStrategy");
        propertiesHandlerStrategy = new PropertiesHandlerStrategyClassPath(applicationName);
        if (StringUtil.isNullOrEmpty(propertesHandlerStrategyPolicy)) {
            PropertiesHandlerStrategyWrapperInterface nextPath = new PropertiesHandlerStrategyPath(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextURL = new PropertiesHandlerStrategyURL(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextFTP = new PropertiesHandlerStrategyFTP(applicationName);
            nextPath.setNext(nextURL);
            nextURL.setNext(nextFTP);
            propertiesHandlerStrategy.setNext(nextPath);
            log.info(
                    "The default chain of responsabilities has loaded because of no strategy policy was found. The current chain of properties' handlers for CLASSPATH strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        } else {
            propertiesHandlerStrategy = buildChainOfResponsability();
            log.info(
                    "A configurated chain of responsabilities has been found. The current chain of properties' handlers for CLASSPATH strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        }
        return propertiesHandlerStrategy;
    }

    public PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyURL() {
        log.info("Creating an URL PropertiesHandlerStrategy");
        propertiesHandlerStrategy = new PropertiesHandlerStrategyURL(applicationName);
        if (StringUtil.isNullOrEmpty(propertesHandlerStrategyPolicy)) {
            PropertiesHandlerStrategyWrapperInterface nextFTP = new PropertiesHandlerStrategyFTP(applicationName);
            propertiesHandlerStrategy.setNext(nextFTP);
            log.info(
                    "The default chain of responsabilities has loaded because of no strategy policy was found. The current chain of properties' handlers for URL strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        } else {
            propertiesHandlerStrategy = buildChainOfResponsability();
            log.info(
                    "A configurated chain of responsabilities has been found. The current chain of properties' handlers for URL strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        }
        return propertiesHandlerStrategy;

    }

    public PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyDB() {
        log.info("Creating a DB PropertiesHandlerStrategy");
        propertiesHandlerStrategy = new PropertiesHandlerStrategyDB(applicationName);
        if (StringUtil.isNullOrEmpty(propertesHandlerStrategyPolicy)) {

            PropertiesHandlerStrategyWrapperInterface nextClassPath = new PropertiesHandlerStrategyClassPath(
                    applicationName);
            PropertiesHandlerStrategyWrapperInterface nextPath = new PropertiesHandlerStrategyPath(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextURL = new PropertiesHandlerStrategyURL(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextFTP = new PropertiesHandlerStrategyFTP(applicationName);
            nextClassPath.setNext(nextPath);
            nextPath.setNext(nextURL);
            nextURL.setNext(nextFTP);
            propertiesHandlerStrategy.setNext(nextClassPath);
            log.info(
                    "The default chain of responsabilities has loaded because of no strategy policy was found. The current chain of properties' handlers for DB strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        } else {
            propertiesHandlerStrategy = buildChainOfResponsability();
            log.info(
                    "A configurated chain of responsabilities has been found. The current chain of properties' handlers for DB strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        }
        return propertiesHandlerStrategy;
    }

    public PropertiesHandlerStrategyWrapperInterface buildChainOfResponsability() {
        if (!StringUtil.isNullOrEmpty(propertesHandlerStrategyPolicy)) {
            String[] policy = propertesHandlerStrategyPolicy.split(";");
            if (policy.length == 1) {
                policy = propertesHandlerStrategyPolicy.split(",");
            }
            setNext(applicationName, propertiesHandlerStrategy, new ArrayList(Arrays.asList(policy)), defaultFileName);
            return propertiesHandlerStrategy.getNext();
        } else {
            return new PropertiesHandlerStrategyWrapperFactory(applicationName,
                    defaultFileName).createPropertiesHandlerStrategyDefault();
        }
    }

    public static PropertiesHandlerStrategyWrapperInterface buildChainOfResponsability(String applicationName,
                                                                                       String policyString,
                                                                                       String defaultFileName) {
        PropertiesHandlerStrategyWrapperInterface propertiesHandlerStrategy
                = new PropertiesHandlerStrategyWrapperFactory(applicationName,
                defaultFileName).createPropertiesHandlerStrategyDefault();
        if (!StringUtil.isNullOrEmpty(policyString)) {
            String[] policy = policyString.split(";");
            if (policy.length == 1) {
                policy = policyString.split(",");
            }
            setNext(applicationName, propertiesHandlerStrategy, new ArrayList(Arrays.asList(policy)), defaultFileName);
            return propertiesHandlerStrategy.getNext();
        } else {
            return new PropertiesHandlerStrategyWrapperFactory(applicationName,
                    defaultFileName).createPropertiesHandlerStrategyDefault();
        }
    }

    private static PropertiesHandlerStrategyWrapperInterface setNext(String applicationName,
                                                                     PropertiesHandlerStrategyWrapperInterface propertiesHandlerStrategy,
                                                                     ArrayList /*<String>*/ propertiesHandlerStrategyPolicies,
                                                                     String defaultFileName) {
        if (propertiesHandlerStrategy != null) {
            if (propertiesHandlerStrategyPolicies != null && propertiesHandlerStrategyPolicies.size() > 0) {
                PropertiesHandlerStrategyWrapperInterface next
                        = PropertiesHandlerStrategyWrapperFactory.createPropertiesHandlerStrategyWithoutPolicies(
                        applicationName,
                        ((String) propertiesHandlerStrategyPolicies.get(0)).trim(),
                        defaultFileName);
                propertiesHandlerStrategy.setNext(next);
                propertiesHandlerStrategyPolicies.remove(0);
                setNext(applicationName, next, propertiesHandlerStrategyPolicies, defaultFileName);
            }
        }
        return propertiesHandlerStrategy;
    }

    public String getStrategyPolicyChain(PropertiesHandlerStrategyWrapperInterface nextSegment) {
        if (nextSegment != null) {
            return nextSegment.getPropertiesHandlerStrategyName()
                    .concat(getStrategyPolicyChain(nextSegment.getNext()).concat(";"));
        } else {
            return StringUtil.EMPTY_STRING;
        }
    }

}