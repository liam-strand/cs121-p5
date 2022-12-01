import java.util.*;

import org.junit.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;


public class VerifyTest {
    
    public static boolean sim_implemented = true;

    @BeforeClass public static void checkIfSimIsImplemented() {
        try {
            Sim.run_sim(null, null);
        } catch (UnsupportedOperationException ex) {
            sim_implemented = false;
        }
    }

    @Test public void negativeEdgeCase() {
        Assume.assumeTrue(sim_implemented);

        MBTA testLine = new MBTA();
        
        testLine.addLine("purple", List.of("Cummings", "SEC", "Tisch", "Granoff"));
        
        Log testLog = new Log();
        Sim.run_sim(testLine, testLog);
        Verify.verify(testLine, testLog);
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
        mbta.addJourney("P1", line1);
        mbta.addJourney("P2", line2);
        mbta.addJourney("P3", line3);
        mbta.addJourney("P4", line4);
        
        Log log = new Log();
        
        log.passenger_boards(P1, T1, S1_1);
        log.passenger_boards(P2, T2, S3_3);
        log.passenger_boards(P3, T3, S2_1);
        log.passenger_boards(P4, T4, S3_2);
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
        log.train_moves(T1, S3_2, S3_3);
        log.passenger_deboards(P1, T1, S3_3); 
        log.train_moves(T2, S1_2, S1_1);
        log.train_moves(T4, S2_2, S1_2);
        log.passenger_deboards(P2, T2, S1_1);
        log.passenger_deboards(P4, T4, S1_2);
        
        try{
            Verify.verify(mbta, log);
        } catch(Exception e){
            e.printStackTrace();
        }
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
}
