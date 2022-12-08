import java.util.*;
import java.io.IOException;
import java.io.File;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class SimTest {

    @Rule public TemporaryFolder sandbox = new TemporaryFolder();

    private void runThenCheckSim(Config c) { runThenCheckSim(c, 500); }
    private void runThenCheckSim(Config c, int wait) {
        try {
            MBTA mbta = new MBTA();
            
            File configFile = sandbox.newFile();

            c.toFile(configFile);
    
            mbta.loadConfig(configFile.getPath());
    
            Log log = new Log();
    
            mbta.runWithWait(log, wait);
    
            mbta.reset();
            mbta.loadConfig(configFile.getPath());
            Verify.verify(mbta, log);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void runAndCheckSim(Config c) { runAndCheckSim(c, 500); }
    private void runAndCheckSim(Config c, int wait) {
        try {
            MBTA mbta = new MBTA();
            
            File configFile = sandbox.newFile();

            c.toFile(configFile);
    
            mbta.loadConfig(configFile.getPath());
    
            Log log = new Log();
    
            mbta.runCheckedWithWait(log, wait);
    
            mbta.reset();
            mbta.loadConfig(configFile.getPath());
            Verify.verify(mbta, log);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test public void singleLineOnePassenger() {
        Config c = new Config();
        c.lines = Map.of("Green", List.of("Medford", "Ball", "Teele", "Lechmere"));
        c.trips = Map.of("Liam", List.of("Medford", "Lechmere"));

        runThenCheckSim(c);
    }

    @Test public void twoLinesTwoPassengers() {
        Config c = new Config();
        c.lines = Map.of(
            "Green", List.of("Medford", "Ball","Harvard", "SMFA"),
            "Red", List.of("Davis", "Harvard", "MIT", "MGH")
        );
        c.trips = Map.of(
            "Liam", List.of("Medford", "Harvard","MGH"),
            "Cece", List.of("Davis", "Harvard", "SMFA")
        );

        runThenCheckSim(c);
    }

    @Test public void harvardKillerTest() {
        Config c = new Config();
        c.lines = Map.of(
            "blue",      List.of("Davis",   "Porter",  "Harvard"),
            "red",       List.of("MIT",     "Central", "Harvard"),
            "magenta",   List.of("Harvard", "Tufts",   "Northeastern"),
            "chartruse", List.of("SMFA",    "Harvard", "Hillbrook"),
            "purple",    List.of("CoOP",    "Harvard", "Charles")
        );
        c.trips = Map.of(
            "liam", List.of(
                "Davis", "Harvard", "MIT", "Harvard", "Davis", "Harvard", "MIT", "Harvard", "Davis", "Harvard", "MIT", "Harvard", "Davis", "Harvard", "MIT", "Harvard", "Davis", "Harvard", "MIT", "Harvard", "Davis", "Harvard"
            )
        );
        for(int i = 0; i < 20; i++) {
            // System.err.printf("Test #%d\n", i);
            runThenCheckSim(c, 10);
        }
    }

    @Test public void harvardStressTest() {
        Config c = new Config();
        c.lines = Map.of(
            "blue",      List.of("Davis",   "Porter",  "Harvard"),
            "red",       List.of("MIT",     "Central", "Harvard"),
            "magenta",   List.of("Harvard", "Tufts",   "Northeastern"),
            "chartruse", List.of("SMFA",    "Harvard", "Hillbrook"),
            "purple",    List.of("CoOP",    "Harvard", "Charles")
        );
        c.trips = Map.of(
            "liam", List.of(
                "Davis", "Harvard", "MIT", "Harvard", "Davis", "Harvard", "MIT", "Harvard", "Davis", "Harvard", "MIT", "Harvard", "Davis", "Harvard", "MIT", "Harvard", "Davis", "Harvard", "MIT", "Harvard", "Davis", "Harvard"
            )
        );

        runAndCheckSim(c, 100);
    }
    
    @Test public void gradescopeElephantTestRep() {

        Config c = Config.fromJsonString("""
            {
                "lines": {
                    "blue":   ["R", "S", "P", "N", "M", "F"],
                    "green":  ["H", "G", "E", "B", "C"],
                    "orange": ["L", "K", "J", "I", "H"],
                    "purple": ["O", "N", "Q", "S", "T", "L"],
                    "red":    ["A", "B", "D", "G", "F"]
                },
                "trips": {
                    "Aardvark": ["R", "S", "T"],
                    "Bear":     ["R", "F", "G", "H"],
                    "Cow":      ["R", "S", "L", "H"],
                    "Dog":      ["A", "B", "G"],
                    "Elephant": ["D", "F", "N", "T"],
                    "Frog":     ["O", "N", "F", "G", "H"],
                    "Giraffe":  ["O", "L", "H"],
                    "Horse":    ["M", "N"],
                    "Iguana":   ["P", "F", "B", "C"],
                    "Jaguar":   ["H", "L"],
                    "Koala":    ["L", "T"],
                    "Lamprey":  ["L", "H", "G", "F", "S", "T"]
                }
            }
        """);

        for (int i = 0; i < 10; i++) {
            System.err.println("TEST # " + i);
            runThenCheckSim(c, 20);
        }
    }

    @Test public void gradescopeElephantTest() {
        Config c = Config.fromJsonString("""
            {
                "lines": {
                    "blue":   ["R", "S", "P", "N", "M", "F"],
                    "green":  ["H", "G", "E", "B", "C"],
                    "orange": ["L", "K", "J", "I", "H"],
                    "purple": ["O", "N", "Q", "S", "T", "L"],
                    "red":    ["A", "B", "D", "G", "F"]
                },
                "trips": {
                    "Aardvark": ["R", "S", "T"],
                    "Bear":     ["R", "F", "G", "H"],
                    "Cow":      ["R", "S", "L", "H"],
                    "Dog":      ["A", "B", "G"],
                    "Elephant": ["D", "F", "N", "T"],
                    "Frog":     ["O", "N", "F", "G", "H"],
                    "Giraffe":  ["O", "L", "H"],
                    "Horse":    ["M", "N"],
                    "Iguana":   ["P", "F", "B", "C"],
                    "Jaguar":   ["H", "L"],
                    "Koala":    ["L", "T"],
                    "Lamprey":  ["L", "H", "G", "F", "S", "T"]
                }
            }
        """);

        runThenCheckSim(c);
    }

    @Test public void lampreyGradescopeTest() {
        Config c = Config.fromJsonString("""
            {
                "lines": {
                  "blue": ["R", "S", "P", "N", "M", "F"],
                  "green": ["H", "G", "E", "B", "C"],
                  "orange": ["L", "K", "J", "I", "H"],
                  "purple": ["O", "N", "Q", "S", "T", "L"],
                  "red": ["A", "B", "D", "G", "F"]
                },
                "trips": {
                  "Aardvark": ["R", "S", "T"],
                  "Bear": ["R", "F", "G", "H"],
                  "Cow": ["R", "S", "L", "H"],
                  "Dog": ["A", "B", "G"],
                  "Elephant": ["D", "F", "N", "T"],
                  "Frog": ["O", "N", "F", "G", "H"],
                  "Giraffe": ["O", "L", "H"],
                  "Horse": ["M", "N"],
                  "Iguana": ["P", "F", "B", "C"],
                  "Jaguar": ["H", "L"],
                  "Koala": ["L", "T"],
                  "Lamprey": ["L", "H", "G", "F", "S", "T"]
                }
              }
        """);

        runThenCheckSim(c);
    }

    @Test public void providedTest() {
        Config c = Config.fromFile(new File(System.getProperty("user.dir"), "sample.json"));

        runThenCheckSim(c);
    }
}
