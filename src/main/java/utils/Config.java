package utils;

import org.apache.commons.cli.*;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

/**
 * Created on 05.04.2016.
 *
 * @author tobias.meier
 */
public class Config {

    //Config Options
    private static final String DATABASESECTION = "DATABASE";
    private static final String REPOSITORYSECTION = "REPOSITORY";

    private String databaseEngine;
    private String databaseName;
    private String databaseUser;
    private String databaseUserPassword;
    private String databaseHost;
    private int databasePort;

    private String repositoryName;

    //CLI Options
    private String configFile = null;
    private Option configFileOption = OptionBuilder.withArgName("configfile")
            .hasArg()
            .withDescription("use a config file")
            .create("f");

    private Options options = new Options();
    private CommandLineParser parser = new GnuParser();

    public void parse(String[] args) {
        options.addOption(configFileOption);
        CommandLineParser parser = new GnuParser();

        try {
            CommandLine line = parser.parse(options, args);
            if ( line.hasOption("f") ) {
                configFile = line.getOptionValue("f");
            }
        }
        catch(ParseException e) {
            System.err.println("Parsing failed. Reason: " + e.getMessage());
        }

    }

    public void readConfigFile() {
        try {
            Wini iniFileParser = new Wini(new File(configFile));
            databaseEngine = iniFileParser.get(DATABASESECTION, "database_engine", String.class);
            databaseName = iniFileParser.get(DATABASESECTION, "database_name", String.class);
            databaseUser = iniFileParser.get(DATABASESECTION, "database_user", String.class);
            databaseUserPassword = iniFileParser.get(DATABASESECTION, "database_user_password", String.class);
            databaseHost = iniFileParser.get(DATABASESECTION, "database_host", String.class);
            databasePort = iniFileParser.get(DATABASESECTION, "database_port", int.class);
            repositoryName = iniFileParser.get(REPOSITORYSECTION, "name", String.class);
        } catch (IOException e) {
            System.err.println("Could not read Configfile. Reason: " + e.getMessage());
        }
    }
    public String getConfigFile() {
        return configFile;
    }

    public String getDatabaseEngine() {
        return databaseEngine;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public String getDatabaseUserPassword() {
        return databaseUserPassword;
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public int getDatabasePort() {
        return databasePort;
    }

    public String getRepositoryName() {
        return repositoryName;
    }
}
