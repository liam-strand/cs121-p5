import java.io.File;
import java.io.IOException;
import java.util.*;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;


public class VerifyTests {
    

    public static boolean sim_implemented = true;

    @BeforeClass public static void checkIfSimIsImplemented() {
        try {
            Sim.run_sim(new MBTA(), new Log());
        } catch (UnsupportedOperationException ex) {
            sim_implemented = false;
        }
    }

    @Rule public TemporaryFolder sandbox = new TemporaryFolder();

    private File newFile() {
        try {
            return sandbox.newFile();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test public void noPassengers() {

        MBTA testLine = new MBTA();
        
        List<String> station_names = List.of("Cummings", "SEC", "Tisch", "Granoff");

        testLine.addLine("purple", station_names);
        
        Log testLog = new Log();

        Train t = Train.make("purple");

        Station[] s = station_names.stream().map(str -> Station.make(str)).toArray(Station[]::new);

        testLog.train_moves(t, s[0], s[1]);
        testLog.train_moves(t, s[1], s[2]);
        testLog.train_moves(t, s[2], s[3]);
        testLog.train_moves(t, s[3], s[2]);
        testLog.train_moves(t, s[2], s[1]);
        testLog.train_moves(t, s[1], s[0]);
        testLog.train_moves(t, s[0], s[1]);
        testLog.train_moves(t, s[1], s[2]);

        Verify.verify(testLine, testLog);
    }

    @Test public void trainCollision() {

        MBTA testLine = new MBTA();
        
        List<String> station_names_1 = List.of("Cummings", "SEC", "Tisch", "Granoff");
        List<String> station_names_2 = List.of("Medford", "SEC", "Tisch", "Aidekman");

        testLine.addLine("purple", station_names_1);
        testLine.addLine("pink", station_names_2);
        
        Log log = new Log();

        Train t1 = Train.make("purple");
        Train t2 = Train.make("pink");

        Station[] s1 = station_names_1.stream().map(str -> Station.make(str)).toArray(Station[]::new);
        Station[] s2 = station_names_2.stream().map(str -> Station.make(str)).toArray(Station[]::new);

        log.train_moves(t1, s1[0], s1[1]);
        log.train_moves(t2, s2[0], s2[1]);

        assertThrows(RuntimeException.class, () -> {
            Verify.verify(testLine, log);
        });
    }

    @Test public void verifyGradescopeElephant() {

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
        File f = newFile();
        c.toFile(f);

        MBTA colors = new MBTA();
        colors.loadConfig(f.getAbsolutePath());

        Passenger Dog = Passenger.make("Dog");
        Passenger Jaguar = Passenger.make("Jaguar");
        Passenger Giraffe = Passenger.make("Giraffe");
        Passenger Frog = Passenger.make("Frog");
        Passenger Lamprey = Passenger.make("Lamprey");
        Passenger Aardvark = Passenger.make("Aardvark");
        Passenger Cow = Passenger.make("Cow");
        Passenger Bear = Passenger.make("Bear");
        Passenger Horse = Passenger.make("Horse");
        Passenger Iguana = Passenger.make("Iguana");
        Passenger Elephant = Passenger.make("Elephant");
        Passenger Koala = Passenger.make("Koala");

        Train orange = Train.make("orange");
        Train red = Train.make("red");
        Train green = Train.make("green");
        Train blue = Train.make("blue");
        Train purple = Train.make("purple");

        Station C = Station.make("C");
        Station F = Station.make("F");
        Station A = Station.make("A");
        Station H = Station.make("H");
        Station T = Station.make("T");
        Station N = Station.make("N");
        Station Q = Station.make("Q");
        Station B = Station.make("B");
        Station S = Station.make("S");
        Station M = Station.make("M");
        Station R = Station.make("R");
        Station I = Station.make("I");
        Station P = Station.make("P");
        Station J = Station.make("J");
        Station D = Station.make("D");
        Station K = Station.make("K");
        Station O = Station.make("O");
        Station E = Station.make("E");
        Station L = Station.make("L");
        Station G = Station.make("G");

        Log log = new Log();

        log.passenger_boards(Frog, purple, O);
        log.passenger_boards(Aardvark, blue, R);
        log.passenger_boards(Giraffe, purple, O);
        log.passenger_boards(Dog, red, A);
        log.passenger_boards(Bear, blue, R);
        log.passenger_boards(Lamprey, orange, L);
        log.passenger_boards(Cow, blue, R);
        log.train_moves(red, A, B);
        log.train_moves(purple, O, N);
        log.train_moves(blue, R, S);
        log.passenger_deboards(Frog, purple, N);
        log.train_moves(green, H, G);
        log.train_moves(orange, L, K);
        log.passenger_deboards(Cow, blue, S);
        log.passenger_deboards(Aardvark, blue, S);
        log.passenger_deboards(Dog, red, B);
        log.passenger_boards(Dog, red, B);
        log.train_moves(red, B, D);
        log.train_moves(blue, S, P);
        log.passenger_boards(Elephant, red, D);
        log.passenger_boards(Iguana, blue, P);
        log.train_moves(purple, N, Q);
        log.train_moves(green, G, E);
        log.train_moves(orange, K, J);
        log.train_moves(red, D, G);
        log.train_moves(blue, P, N);
        log.train_moves(purple, Q, S);
        log.passenger_deboards(Dog, red, G);
        log.passenger_boards(Frog, blue, N);
        log.passenger_boards(Aardvark, purple, S);
        log.passenger_boards(Cow, purple, S);
        log.train_moves(green, E, B);
        log.train_moves(orange, J, I);
        log.train_moves(red, G, F);
        log.train_moves(purple, S, T);
        log.passenger_deboards(Elephant, red, F);
        log.train_moves(blue, N, M);
        log.passenger_deboards(Aardvark, purple, T);
        log.passenger_boards(Horse, blue, M);
        log.train_moves(green, B, C);
        log.train_moves(orange, I, H);
        log.passenger_boards(Jaguar, orange, H);
        log.passenger_deboards(Lamprey, orange, H);
        log.train_moves(purple, T, L);
        log.train_moves(red, F, G);
        log.train_moves(blue, M, F);
        log.passenger_deboards(Cow, purple, L);
        log.passenger_boards(Elephant, blue, F);
        log.passenger_deboards(Giraffe, purple, L);
        log.train_moves(green, C, B);
        log.passenger_boards(Koala, purple, L);
        log.train_moves(orange, H, I);
        log.passenger_deboards(Iguana, blue, F);
        log.passenger_deboards(Frog, blue, F);
        log.passenger_deboards(Bear, blue, F);
        log.train_moves(purple, L, T);
        log.train_moves(blue, F, M);
        log.train_moves(red, G, D);
        log.passenger_deboards(Koala, purple, T);
        log.train_moves(green, B, E);
        log.train_moves(orange, I, J);
        log.train_moves(purple, T, S);
        log.train_moves(red, D, B);
        log.train_moves(blue, M, N);
        log.train_moves(orange, J, K);
        log.passenger_deboards(Horse, blue, N);
        log.train_moves(green, E, G);
        log.passenger_deboards(Elephant, blue, N);
        log.train_moves(purple, S, Q);
        log.train_moves(red, B, A);
        log.train_moves(blue, N, P);
        log.train_moves(orange, K, L);
        log.train_moves(green, G, H);
        log.passenger_deboards(Jaguar, orange, L);
        log.passenger_boards(Lamprey, green, H);
        log.passenger_boards(Cow, orange, L);
        log.passenger_boards(Giraffe, orange, L);
        log.train_moves(red, A, B);
        log.train_moves(green, H, G);
        log.train_moves(orange, L, K);
        log.train_moves(blue, P, S);
        log.train_moves(purple, Q, N);
        log.passenger_deboards(Lamprey, green, G);
        log.passenger_boards(Elephant, purple, N);
        log.train_moves(red, B, D);
        log.train_moves(blue, S, R);
        log.train_moves(orange, K, J);
        log.train_moves(green, G, E);
        log.train_moves(purple, N, O);
        log.train_moves(red, D, G);
        log.train_moves(green, E, B);
        log.passenger_boards(Lamprey, red, G);
        log.train_moves(orange, J, I);
        log.train_moves(blue, R, S);
        log.train_moves(purple, O, N);
        log.train_moves(red, G, F);
        log.train_moves(green, B, C);
        log.train_moves(orange, I, H);
        log.train_moves(purple, N, Q);
        log.passenger_boards(Frog, red, F);
        log.passenger_deboards(Lamprey, red, F);
        log.passenger_boards(Bear, red, F);
        log.train_moves(blue, S, P);
        log.passenger_boards(Iguana, red, F);
        log.passenger_deboards(Giraffe, orange, H);
        log.passenger_deboards(Cow, orange, H);
        log.train_moves(green, C, B);
        log.train_moves(orange, H, I);
        log.train_moves(red, F, G);
        log.train_moves(purple, Q, S);
        log.passenger_deboards(Frog, red, G);
        log.train_moves(blue, P, N);
        log.passenger_deboards(Bear, red, G);
        log.train_moves(green, B, E);
        log.train_moves(orange, I, J);
        log.train_moves(purple, S, T);
        log.train_moves(red, G, D);
        log.train_moves(blue, N, M);
        log.passenger_deboards(Elephant, purple, T);
        log.train_moves(green, E, G);
        log.train_moves(purple, T, L);
        log.passenger_boards(Frog, green, G);
        log.train_moves(orange, J, K);
        log.passenger_boards(Bear, green, G);
        log.train_moves(blue, M, F);
        log.train_moves(red, D, B);
        log.passenger_boards(Lamprey, blue, F);
        log.passenger_deboards(Iguana, red, B);
        log.train_moves(green, G, H);
        log.train_moves(orange, K, L);
        log.passenger_deboards(Bear, green, H);
        log.train_moves(purple, L, T);
        log.train_moves(red, B, A);
        log.train_moves(blue, F, M);
        log.passenger_deboards(Frog, green, H);
        log.train_moves(green, H, G);
        log.train_moves(red, A, B);
        log.train_moves(purple, T, S);
        log.train_moves(orange, L, K);
        log.train_moves(blue, M, N);
        log.train_moves(green, G, E);
        log.train_moves(orange, K, J);
        log.train_moves(purple, S, Q);
        log.train_moves(red, B, D);
        log.train_moves(blue, N, P);
        log.train_moves(green, E, B);
        log.train_moves(purple, Q, N);
        log.passenger_boards(Iguana, green, B);
        log.train_moves(orange, J, I);
        log.train_moves(blue, P, S);
        log.train_moves(red, D, G);
        log.passenger_deboards(Lamprey, blue, S);
        log.train_moves(green, B, C);
        log.train_moves(orange, I, H);
        log.passenger_deboards(Iguana, green, C);
        log.train_moves(purple, N, O);
        log.train_moves(red, G, F);
        log.train_moves(blue, S, R);
        log.train_moves(green, C, B);
        log.train_moves(orange, H, I);
        log.train_moves(purple, O, N);
        log.train_moves(blue, R, S);
        log.train_moves(red, F, G);
        log.train_moves(green, B, E);
        log.train_moves(purple, N, Q);
        log.train_moves(red, G, D);
        log.train_moves(orange, I, J);
        log.train_moves(blue, S, P);
        log.train_moves(green, E, G);
        log.train_moves(red, D, B);
        log.train_moves(blue, P, N);
        log.train_moves(purple, Q, S);
        log.train_moves(orange, J, K);
        log.passenger_boards(Lamprey, purple, S);
        log.train_moves(green, G, H);
        log.train_moves(blue, N, M);
        log.train_moves(red, B, A);
        log.train_moves(orange, K, L);
        log.train_moves(purple, S, T);
        log.passenger_deboards(Lamprey, purple, T);

        assertThrows(RuntimeException.class, () -> Verify.verify(colors, log));
    }

    @Test public void test1_1(){
        MBTA mbta = new MBTA();
        
        /*
        * 1_1 -- 1_2 -- 1_3
        *  |      |      |
        * 2_1 -- 2_2 -- 2_3
        *  |      |      |
        * 3_1 -- 3_2 -- 3_3
        */
        
        Station S1_1 = Station.make("S1_1");
        Station S1_2 = Station.make("S1_2");
        Station S1_3 = Station.make("S1_3");
        Station S2_1 = Station.make("S2_1");
        Station S2_2 = Station.make("S2_2");
        Station S2_3 = Station.make("S2_3");
        Station S3_1 = Station.make("S3_1");
        Station S3_2 = Station.make("S3_2");
        Station S3_3 = Station.make("S3_3");
    
        List<String> line1 = List.of("S1_1","S2_1","S3_1","S3_2", "S3_3");
        List<String> line2 = List.of("S3_3","S2_3","S1_3","S1_2", "S1_1");
        List<String> line3 = List.of("S2_1","S2_2","S2_3");
        List<String> line4 = List.of("S3_2","S2_2","S1_2");
        
        Train T1 = Train.make("T1");
        Train T2 = Train.make("T2");
        Train T3 = Train.make("T3");
        Train T4 = Train.make("T4");
        mbta.addLine("T1", line1);
        mbta.addLine("T2", line2);
        mbta.addLine("T3", line3);
        mbta.addLine("T4", line4);
        
        Passenger P1 = Passenger.make("P1");
        Passenger P2 = Passenger.make("P2");
        Passenger P3 = Passenger.make("P3");
        Passenger P4 = Passenger.make("P4");
        Passenger P5 = Passenger.make("P5");

        mbta.addJourney("P1", List.of("S1_1", "S3_3"));
        mbta.addJourney("P2", List.of("S3_3", "S1_1"));
        mbta.addJourney("P3", List.of("S2_1", "S2_3"));
        mbta.addJourney("P4", List.of("S3_2", "S1_2"));
        mbta.addJourney("P5", List.of("S1_1", "S3_2", "S2_2"));
        
        Log log = new Log();
        
        log.passenger_boards(P1, T1, S1_1);
        log.passenger_boards(P2, T2, S3_3);
        log.passenger_boards(P3, T3, S2_1);
        log.passenger_boards(P4, T4, S3_2);
        log.passenger_boards(P5, T1, S1_1);
        log.train_moves(T2, S3_3, S2_3);
        log.train_moves(T2, S2_3, S1_3);
        log.train_moves(T2, S1_3, S1_2);
        log.train_moves(T3, S2_1, S2_2);
        log.train_moves(T3, S2_2, S2_3);
        log.passenger_deboards(P3, T3, S2_3);
        log.train_moves(T4, S3_2, S2_2);
        log.train_moves(T1, S1_1, S2_1);
        log.train_moves(T1, S2_1, S3_1);
        log.train_moves(T1, S3_1, S3_2);
        log.passenger_deboards(P5, T1, S3_2);
        log.train_moves(T1, S3_2, S3_3);
        log.passenger_deboards(P1, T1, S3_3); 
        log.train_moves(T2, S1_2, S1_1);
        log.train_moves(T4, S2_2, S1_2);
        log.passenger_deboards(P2, T2, S1_1);
        log.passenger_deboards(P4, T4, S1_2);
        log.train_moves(T4, S1_2, S2_2);
        log.train_moves(T4, S2_2, S3_2);
        log.passenger_boards(P5, T4, S3_2);
        log.train_moves(T4, S3_2, S2_2);
        log.passenger_deboards(P5, T4, S2_2);
        
        Verify.verify(mbta, log);
    }

    @Test 
    public void test3() {
        MBTA mbta = new MBTA();
        mbta.addLine("Reborn", List.of("Opal Ward", "Peridot Ward", "Onyx Ward", "Obsidia Ward", "Coral Ward", "Jasper Ward",
                                            "Beryl Ward", "Lapis Ward", "Byxbysion Wasteland"));
        mbta.addLine("Azurine", List.of("Coral Ward", "Azurine Island", "Apophyll Island"));
        mbta.addLine("Chrysolia", List.of("Obsidia Ward", "Spinel Town", "Agate City"));
        mbta.addLine("Carnelia", List.of("Agate City", "Agate Circus", "Calcenon City", "Labradorra City"));
        mbta.addLine("Tourmaline", List.of("Peridot Ward", "Tourmaline Desert", "Scrapyard"));

        mbta.addJourney("Vero", List.of("Scrapyard", "Peridot Ward", "Obsidia Ward", "Agate City", "Labradorra City"));

        Train reborn = Train.make("Reborn");
        Train azurine = Train.make("Azurine");
        Train chrysolia = Train.make("Chrysolia");
        Train carnelia = Train.make("Carnelia");
        Train tourmaline = Train.make("Tourmaline");

        Passenger vero = Passenger.make("Vero");

        Station peridot = Station.make("Peridot Ward");
        Station desert = Station.make("Tourmaline Desert");
        Station scrapyard = Station.make("Scrapyard");
        Station opal = Station.make("Opal Ward");
        Station onyx = Station.make("Onyx Ward");
        Station obsidia = Station.make("Obsidia Ward");
        Station spinel = Station.make("Spinel Town");
        Station agate = Station.make("Agate City");
        Station circus = Station.make("Agate Circus");
        Station calcenon = Station.make("Calcenon City");
        Station labradorra = Station.make("Labradorra City");
        Station coral = Station.make("Coral Ward");
        Station jasper = Station.make("Jasper Ward");
        Station beryl = Station.make("Beryl Ward");
        Station lapis = Station.make("Lapis Ward");
        Station byxbysion = Station.make("Byxbysion Wasteland");
        Station island = Station.make("Azurine Island");
        Station apophyll = Station.make("Apophyll Island");

        Log log = new Log();
        log.train_moves(tourmaline, peridot, desert);
        log.train_moves(tourmaline, desert, scrapyard);

        log.passenger_boards(vero, tourmaline, scrapyard);
        log.train_moves(tourmaline, scrapyard, desert);
        log.train_moves(reborn, opal, peridot);
        log.train_moves(reborn, peridot, onyx);
        log.train_moves(tourmaline, desert, peridot);
        log.passenger_deboards(vero, tourmaline, peridot);
        log.train_moves(tourmaline, peridot, desert);

        log.train_moves(chrysolia, obsidia, spinel);
        log.train_moves(reborn, onyx, obsidia);
        log.train_moves(azurine, coral, island);
        log.train_moves(reborn, obsidia, coral);
        log.train_moves(reborn, coral, jasper);
        log.train_moves(reborn, jasper, beryl);
        log.train_moves(reborn, beryl, lapis);
        log.train_moves(reborn, lapis, byxbysion);
        log.train_moves(reborn, byxbysion, lapis);
        log.train_moves(reborn, lapis, beryl);
        log.train_moves(reborn, beryl, jasper);
        log.train_moves(reborn, jasper, coral);
        log.train_moves(reborn, coral, obsidia);
        log.train_moves(reborn, obsidia, onyx);
        log.train_moves(reborn, onyx, peridot);

        log.passenger_boards(vero, reborn, peridot);
        log.train_moves(reborn, peridot, opal);
        log.train_moves(reborn, opal, peridot);
        log.train_moves(reborn, peridot, onyx);
        log.train_moves(reborn, onyx, obsidia);
        log.passenger_deboards(vero, reborn, obsidia);
        log.train_moves(reborn, obsidia, coral);

        log.train_moves(carnelia, agate, circus);
        log.train_moves(chrysolia, spinel, agate);
        log.train_moves(chrysolia, agate, spinel);
        log.train_moves(chrysolia, spinel, obsidia);

        log.passenger_boards(vero, chrysolia, obsidia);
        log.train_moves(chrysolia, obsidia, spinel);
        log.train_moves(chrysolia, spinel, agate);
        log.passenger_deboards(vero, chrysolia, agate);
        log.train_moves(chrysolia, agate, spinel);

        log.train_moves(carnelia, circus, calcenon);
        log.train_moves(carnelia, calcenon, labradorra);
        log.train_moves(carnelia, labradorra, calcenon);
        log.train_moves(carnelia, calcenon, circus);
        log.train_moves(carnelia, circus, agate);

        log.passenger_boards(vero, carnelia, agate);
        log.train_moves(carnelia, agate, circus);
        log.train_moves(carnelia, circus, calcenon);
        log.train_moves(carnelia, calcenon, labradorra);
        log.passenger_deboards(vero, carnelia, labradorra);

        log.train_moves(azurine, island, apophyll);

        Verify.verify(mbta, log);
    }

    @Test public void test() {
        MBTA mbta = new MBTA();
        Station arlington = Station.make("Arlington");
        Station boylston = Station.make("Boylston");
        Station parkSt = Station.make("Park St");
        Station govermentCenter = Station.make("Government Center");
        Station downtownCrossing = Station.make("Downtown Crossing");
        Station CharlesMGH = Station.make("Charles/MGH");
        Station kendallMIT = Station.make("Kendall/MIT");

        List<String> greenLine = List.of("Arlington","Boylston", "Park St", "Government Center");
        List<String> redLine = List.of("Downtown Crossing", "Park St", "Charles/MGH", "Kendall/MIT");

        Train green = Train.make("green");
        Train red = Train.make("red");

        mbta.addLine("green", greenLine);
        mbta.addLine("red", redLine);

        Passenger molly = Passenger.make("Molly");
        List<String> mollyJourney = List.of("Boylston", "Park St", "Charles/MGH");
        mbta.addJourney("Molly", mollyJourney);

        Log log = new Log();
        log.train_moves(green, arlington, boylston);
        log.passenger_boards(molly, green, boylston);
        log.train_moves(green, boylston, parkSt);
        log.passenger_deboards(molly, green, parkSt);
        log.train_moves(green, parkSt, govermentCenter);
        log.train_moves(red, downtownCrossing, parkSt);
        log.passenger_boards(molly, red, parkSt);
        log.train_moves(red, parkSt, CharlesMGH);
        log.passenger_deboards(molly, red, CharlesMGH);
        log.train_moves(red, CharlesMGH, kendallMIT);

        Verify.verify(mbta, log);
    }

    @Test public void skipStation() {
        MBTA mbta = new MBTA();

        Station a = Station.make("a");
        Station b = Station.make("b");
        Station c = Station.make("c");
        Station d = Station.make("d");

        Train t = Train.make("testLine");

        List<String> line = List.of("a", "b", "c", "d");

        mbta.addLine("testLine", line);

        Log log = new Log();

        log.train_moves(t, a, b);
        log.train_moves(t, b, c);
        log.train_moves(t, c, d);
        log.train_moves(t, d, c);
        log.train_moves(t, c, b);
        log.train_moves(t, b, a);
        log.train_moves(t, a, b);
        log.train_moves(t, b, d);
        log.train_moves(t, d, c);
        assertThrows(RuntimeException.class, () -> {
            Verify.verify(mbta, log);
        });
    }
}
