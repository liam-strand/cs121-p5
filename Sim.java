/* Sim.java
 * 
 * By: Liam Strand
 * On: December 2022
 * 
 * The program driver for the simulation.
 */

import java.io.*;

public class Sim {

    public static void run_sim(MBTA mbta, Log log) {
        mbta.run(log);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("usage: ./sim <config file>");
            System.exit(1);
        }

        MBTA mbta = new MBTA();
        mbta.loadConfig(args[0]);

        Log log = new Log();

        run_sim(mbta, log);

        String s = new LogJson(log).toJson();
        PrintWriter out = new PrintWriter("log.json");
        out.print(s);
        out.close();

        mbta.reset();
        mbta.loadConfig(args[0]);
        Verify.verify(mbta, log);
    }
}
