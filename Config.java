import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;

public class Config {
    public Map<String, List<String>> lines;
    public Map<String, List<String>> trips;

    private static Gson gson = new Gson();
    
    public static Config fromFile(File f) {
        try {
            FileReader fr = new FileReader(f);
            Config c = gson.fromJson(fr, Config.class);
            fr.close();
            return c;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void toFile(File f) {
        try {
            FileWriter fw = new FileWriter(f);
            gson.toJson(this, this.getClass(), fw);
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Config fromJsonString(String jsonString) {
        return gson.fromJson(jsonString, Config.class);
    }
    
    public String toJsonString() {
        return gson.toJson(this, Config.class);
    }

    public String toString() {
        return String.format("lines: %s\ntrips: %s", lines, trips);
    }

    public boolean equals(Object o) {
        if (o instanceof Config c) {
            return this.lines.equals(c.lines) && this.trips.equals(c.trips);
        }
        return false;
    }
}
