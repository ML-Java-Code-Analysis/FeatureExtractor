package ba.ciel5.featureExtractor.utils;

import org.apache.commons.cli.*;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 05.04.2016.
 * Reads config file for FeatureExtractor
 *
 * @author tobias.meier
 */
public class Config {

    //Config Options
    private static final String REPOSITORYSECTION = "REPOSITORY";
    private static final String DEFAULTSECTION = "DEFAULT";
    private static final String FEATURESSECTION = "FEATURES";
    private static final String DATABASESECTION = "DATABASE";

    private String repositoryName;
    private Integer partitions;
    private String logLevel;
    private String logFilename;
    private Integer maxNGramSize;
    private Integer maxNGramFieldSize;
    private List<String> featureGroups;
    private String databaseDialect;
    private String databaseDriver;
    private String databaseUrl;
    private String databaseUser;
    private String databaseUserPassword;

    //CLI Options
    private String configFile = null;
    private Option configFileOption = OptionBuilder.withArgName("configfile")
            .hasArg()
            .withDescription("use a config file")
            .create("f");

    private Options options = new Options();
    private CommandLineParser parser = new GnuParser();

    /**
     * This method reads and parses command line arguments
     *
     * @param args command line arguments.
     * @throws ParseException On input error.
     * @see ParseException
     */
    public void parse(String[] args) throws ParseException {

        options.addOption(configFileOption);
        CommandLineParser parser = new GnuParser();

        CommandLine line = parser.parse(options, args);
        if (line.hasOption("f")) {
            configFile = line.getOptionValue("f");
        }
    }

    /**
     * This method reads and parses the config file. Assigns the config values to the vars
     *
     * @throws IOException On input error.
     * @see IOException
     */
    public void readConfigFile() throws IOException {
        Wini iniFileParser = new Wini(new File(configFile));

        databaseDialect = iniFileParser.get(DATABASESECTION, "dialect", String.class);
        databaseDriver = iniFileParser.get(DATABASESECTION, "driver", String.class);
        databaseUrl = iniFileParser.get(DATABASESECTION, "url", String.class);
        databaseUser = iniFileParser.get(DATABASESECTION, "user", String.class);
        databaseUserPassword = iniFileParser.get(DATABASESECTION, "userPassword", String.class);

        repositoryName = iniFileParser.get(REPOSITORYSECTION, "name", String.class);

        partitions = iniFileParser.get(DEFAULTSECTION, "partitions", Integer.class);
        logFilename = iniFileParser.get(DEFAULTSECTION, "logFilename", String.class);
        logLevel = iniFileParser.get(DEFAULTSECTION, "logLevel", String.class);

        maxNGramSize = iniFileParser.get(FEATURESSECTION, "maxNGramSize", Integer.class);
        maxNGramFieldSize = iniFileParser.get(FEATURESSECTION, "maxNGramFieldSize", Integer.class);
        String featureGroupsString = iniFileParser.get(FEATURESSECTION, "featureGroups", String.class);

        if ( databaseDialect == null )
            throw new IOException("Database dialect not found in config");
        if ( databaseDriver == null )
            throw new IOException("Database driver not found in config");
        if ( databaseUrl == null )
            throw new IOException("Database URL not found in config");
        if ( databaseUser == null )
            throw new IOException("Database user not found in config");
        if ( databaseUserPassword == null )
            throw new IOException("Database user password not found in config");

        if (repositoryName == null)
            throw new IOException("Repository name not found in config");

        if (partitions == null)
            partitions = 250;
        if ( logFilename == null )
            logFilename = "FeatureExtractor.log";
        if ( logLevel == null )
            logLevel = "INFO";

        if ( featureGroupsString != null ) {
            featureGroups = Arrays.asList(featureGroupsString.split("\\s*,\\s*"));
        }
        if (maxNGramSize == null)
            maxNGramSize = 5;
        if (maxNGramFieldSize == null)
            maxNGramFieldSize = 500;

    }

    public String getConfigFile() {
        return configFile;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public Integer getPartitions() {
        return partitions;
    }

    public Integer getMaxNGramSize() {
        return maxNGramSize;
    }

    public Integer getMaxNGramFieldSize() {
        return maxNGramFieldSize;
    }

    public String getDatabaseDialect() {
        return databaseDialect;
    }

    public String getDatabaseDriver() {
        return databaseDriver;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public String getDatabaseUserPassword() {
        return databaseUserPassword;
    }

    public List<String> getFeatureGroups() {
        return featureGroups;
    }

    public String getLogFilename() {
        return logFilename;
    }

    public String getLogLevel() {
        return logLevel;
    }
}
