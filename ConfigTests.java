import java.util.*;
import java.io.File;
import java.io.IOException;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class ConfigTests {

    public static final File sampleFile = new File(System.getProperty("user.dir"), "sample.json");
    @Rule public TemporaryFolder sandbox = new TemporaryFolder();

    @Test public void entitiesAreSingletons() {
        Passenger p1 = Passenger.make("notaname");
        Passenger p2 = Passenger.make("notaname");

        Train t1 = Train.make("notatrain");
        Train t2 = Train.make("notatrain");

        Station s1 = Station.make("notastation");
        Station s2 = Station.make("notastation");

        assertThat("not physically equal", p1, is(sameInstance(p2)));
        assertThat("not physically equal", t1, is(sameInstance(t2)));
        assertThat("not physically equal", s1, is(sameInstance(s2)));
        
        assertThat("not structurally equal", p1, is(equalTo(p2)));
        assertThat("not structurally equal", t1, is(equalTo(t2)));
        assertThat("not structurally equal", s1, is(equalTo(s2)));
    }

    @Test public void readConfigDoesntCrash() {
        try {
            @SuppressWarnings("unused")
            Config c = Config.fromFile(sampleFile);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test public void readConfigFromString() {
        Config c = Config.fromJsonString("""
            {
                "lines": {"red": ["Davis", "Harvard"]},
                "trips": {"Bob": [ "Park", "Tufts" ]}
            }                
        """);
        assertThat(c.lines, is(equalTo(Map.of("red", List.of("Davis", "Harvard")))));
        assertThat(c.trips, is(equalTo(Map.of("Bob", List.of("Park", "Tufts")))));
    }

    @Test public void writeConfigToString() {
        Config c = new Config();
        c.lines = Map.of("green", List.of("Medford/Tufts", "South Station"));
        c.trips = Map.of("Jesse", List.of("Kendall", "SMFA"));
        String json = c.toJsonString();
        Config new_c = Config.fromJsonString(json);

        assertThat(new_c, is(equalTo(c)));
    }

    @Test public void writeConfigToFile() throws IOException {
        Config c = new Config();
        c.lines = Map.of("orange", List.of("Ruggles", "Assembly"));
        c.trips = Map.of("Keith", List.of("Broadway", "Back Bay"));

        File jsonFile = sandbox.newFile();
        c.toFile(jsonFile);
        Config new_c = Config.fromFile(jsonFile);

        assertThat(new_c, is(equalTo(c)));
    }

    @Test public void readWriteConfigToStringAndFile() throws IOException {
        Config c = Config.fromFile(sampleFile);
        c.lines.put("orange", List.of("Ruggles", "Assembly"));
        c.lines.put("green", List.of("Medford/Tufts", "South Station"));
        c.trips.put("Keith", List.of("Broadway", "Back Bay"));
        c.trips.put("Jesse", List.of("Kendall", "SMFA"));

        String jsonString = c.toJsonString();
        Config c2 = Config.fromJsonString(jsonString);
        
        File jsonFile = sandbox.newFile();
        c2.toFile(jsonFile);
        Config new_c = Config.fromFile(jsonFile);

        assertThat(new_c, is(equalTo(c)));
        assertThat(new_c.toString(), is(equalTo(c.toString())));
    }

    @Test public void weirdStringStressTest() throws IOException {
        Config c = new Config();
        c.lines = Map.of(",,,,::{}{}[[]]][{}{}{]///\"\"\"\\\\\"", List.of("Ruggles", "Assembly"));
        c.trips = Map.of("Keith", List.of("Broadway", """
            "Did you ever hear the tragedy of Darth Plagueis the Wise?"
            "No."
            "I thought not. It's not a story the Jedi would tell you. It's a Sith legend. Darth Plagueis... was a Dark Lord of the Sith so powerful and so wise, he could use the Force to influence the midi-chlorians... to create... life. He had such a knowledge of the dark side, he could even keep the ones he cared about... from dying."
            "He could actually... save people from death?"
            "The dark side of the Force is a pathway to many abilities... some consider to be unnatural."
            "Wh– What happened to him?"
            "He became so powerful, th``e only thing he was afraid of was... losing his power. Which eventually, of course, he did. Unfortunately, he taught his apprentice everything he knew. Then his apprentice killed him in his sleep. It is ironic. He could save others from death, but not himself."
            "Is it possible to learn this power?"
            "Not from a Jedi."
        """));

        File jsonFile = sandbox.newFile();
        c.toFile(jsonFile);
        Config new_c = Config.fromFile(jsonFile);

        assertThat(new_c, is(equalTo(c)));
    }

}
