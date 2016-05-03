package ba.ciel5.featureExtractor.utils;

import org.apache.commons.cli.*;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

/**
 * Created on 05.04.2016.
 * Reads config file for FeatureExtractor
 * @author tobias.meier
 */
public class Config {

    //Config Options
    private static final String REPOSITORYSECTION = "REPOSITORY";
    private static final String DEFAULTSECTION = "DEFAULT";

    private String repositoryName;
    private Integer partitions;
    private Integer maxNgramSize;

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
        repositoryName = iniFileParser.get(REPOSITORYSECTION, "name", String.class);
        partitions = iniFileParser.get(DEFAULTSECTION, "partitions", Integer.class);
        maxNgramSize = iniFileParser.get(DEFAULTSECTION, "maxNgramSize", Integer.class);

        if ( repositoryName == null )
            throw new IOException("Repository name not found in config");
        if ( partitions == null )
            partitions=250;
        if ( maxNgramSize == null )
            maxNgramSize=5;
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

    public Integer getMaxNgramSize() {
        return maxNgramSize;
    }
}
