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

    private void runAndCheckSim(Config c) {
        try {
            MBTA mbta = new MBTA();
            
            File configFile = sandbox.newFile();

            c.toFile(configFile);
    
            mbta.loadConfig(configFile.getPath());
    
            Log log = new Log();
    
            mbta.run(log);
    
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

        runAndCheckSim(c);
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

        // c.toFile(new File ("/Users/liamstrand/Desktop/Tufts/Junior/cs121/p5/small.json"));

        runAndCheckSim(c);
    }

    @Test public void providedTest() {
        Config c = Config.fromFile(new File(System.getProperty("user.dir"), "sample.json"));

        runAndCheckSim(c);
    }
}
