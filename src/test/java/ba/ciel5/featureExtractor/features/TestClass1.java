package ba.ciel5.featureExtractor.features;

/**
 * Created on 17.04.2016.
 *
 * @author tobias.meier
 */
public class TestClass1 {

    private final static String TESTCLASS1 = "//\n" +
            "// Source code recreated from a .class file by IntelliJ IDEA\n" +
            "// (powered by Fernflower decompiler)\n" +
            "//\n" +
            "\n" +
            "import java.util.Arrays;\n" +
            "\n" +
            "interface test extends hope {\n" +
            "   private void method()\n" +
            "}\n"+
            "public class MyClass implements test {\n" +
            "    // this is a single line comment\n" +
            "    private String name;\n" +
            "    private int id;\n" +
            "    boolean[] primes = new boolean[10000]; // quite a big array!\n" +
            "\n" +
            "    public MyClass(String name) {\n" +
            "        this.name = name;\n" +
            "        this.updateId();\n" +
            "    }\n" +
            "\n" +
            "   /**\n" +
            "    * This method reads and parses command line arguments\n" +
            "    *\n" +
            "    * @param args command line arguments.\n" +
            "    * @throws ParseException On input error.\n" +
            "    * @see ParseException\n" +
            "    */\n" +
            "    protected boolean updateId() {\n" +
            "        boolean didUpdate = false;\n" +
            "        int newId = this.id;\n" +
            "        if(this.name.equals(\"foo\")) {\n" +
            "            newId = 42;\n" +
            "        } else if(this.name.startsWith(\"test\")) {\n" +
            "            String testName = this.name.replaceFirst(\"test\", \"\");\n" +
            "            newId = testName.hashCode() * 100;\n" +
            "        } else {\n" +
            "            newId = this.name.hashCode();\n" +
            "        }\n" +
            "\n" +
            "        if(newId != this.id) {\n" +
            "            this.id = newId;\n" +
            "            didUpdate = true;\n" +
            "        }\n" +
            "\t   \t\n" +
            "        return didUpdate;\n" +
            "    }\n" +
            "\t\n" +
            "    public void fillSieve() {\n" +
            "        Arrays.fill(this.primes, true);\n" +
            "        this.primes[0] = this.primes[1] = false;\n" +
            "\n" +
            "        for(int i = 2; i < this.primes.length; ++i) {\n" +
            "            if(this.primes[i]) {\n" +
            "                for(int j = 2; i * j < this.primes.length; ++j) {\n" +
            "                    this.primes[i * j] = false;\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "         \n" +
            "    }\n" +
            "                       \n" +
            "    public boolean isPrime(int n) {\n" +
            "        getId();\n" +
            "        return this.primes[n];\n" +
            "    }\n" +
            "\n" +
            "    public int getId() {\n" +
            "        return this.id;\n" +
            "    }\n" +
            "\n" +
            "    public String getName() {\n" +
            "        return this.name;\n" +
            "    }\n" +
            "\n" +
            "    public void setName(String name) {\n" +
            "        this.name = name;\n" +
            "        this.updateId();\n" +
            "    }\n" +
            "    public void func1() {\n" +
            "    }\n" +
            "}\n" +
            "public class SecondClass extends asdf { }\n" +
            "";

    public static String getTestCode() {
        return TESTCLASS1;
    }
}
