package skibiathlonstandings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class BiathlonStanding {
    public enum ShootingResult {
        HIT, MISS
    }

    public static class Athlete implements Comparable<Athlete> {
        private final int athleteNumber;
        private final String athleteName;
        private final String countryCode;
        private final double skiTimeInMinutes;
        private final int totalShootingPenalty;

        public Athlete(int athleteNumber, String athleteName, String countryCode, double skiTimeInMinutes, int totalShootingPenaulty) {
            this.athleteNumber = athleteNumber;
            this.athleteName = athleteName;
            this.countryCode = countryCode;
            this.skiTimeInMinutes = skiTimeInMinutes;
            this.totalShootingPenalty = totalShootingPenaulty;
        }

        public double getFinalTimeInMinutes() {
            return skiTimeInMinutes + totalShootingPenalty / 60.0;
        }

        @Override
        public int compareTo(Athlete other) {
            return Double.compare(getFinalTimeInMinutes(), other.getFinalTimeInMinutes());
        }
    }

    public static List<Athlete> parseCsvData(String csvContent) throws BiathlonParseException {
        List<Athlete> athletes = new ArrayList<>();
        for (String line : csvContent.split("\n")) {
            String[] data = line.split(",");
            if (data.length != 7) {
                throw new BiathlonParseException("Invalid CSV format: " + line);
            }
            int athleteNumber = Integer.parseInt(data[0]);
            String athleteName = data[1];
            String countryCode = data[2];
            double skiTimeInMinutes = parseTime(data[3]);
            int totalPenalty = calculatePenalty(data[4]) + calculatePenalty(data[5]) + calculatePenalty(data[6]);
            athletes.add(new Athlete(athleteNumber, athleteName, countryCode, skiTimeInMinutes, totalPenalty));
        }
        return athletes;
    }

    private static double parseTime(String timeString) throws BiathlonParseException {
        String[] parts = timeString.split(":");
        if (parts.length != 2) {
            throw new BiathlonParseException("Invalid time format: " + timeString);
        }
        try {
            int minutes = Integer.parseInt(parts[0]);
            double seconds = Double.parseDouble(parts[1]);
            return minutes + seconds / 60.0;
        } catch (NumberFormatException e) {
            throw new BiathlonParseException("Invalid time format: " + timeString, e);
        }
    }

    private static int calculatePenalty(String shootingResult) {
        int penalty = 0;
        for (char c : shootingResult.toCharArray()) {
            if (c == 'o') {
                penalty += 10;
            }
        }
        return penalty;
    }

    public static List<Athlete> getTopThreeAthletes(List<Athlete> athletes) {
        Collections.sort(athletes);
        return athletes.subList(0, 3);
    }

    public static class BiathlonParseException extends Exception {
        public BiathlonParseException(String message) {
            super(message);
        }

        public BiathlonParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

     //Unit tests
    @org.junit.jupiter.api.Test
    public void testParseCsvValidData() throws Exception {
        String csvData = """
                12,Luca White,UK,21:27
                2,Ion Stoica,RO,20:15
                27,Piotr Lark,CZ,40:15
                """;
        List<Athlete> athletes = parseCsvData(csvData);
        assertEquals(3, athletes.size());
    }

    @org.junit.jupiter.api.Test
    public void testParseCsvInvalidFormat() throws Exception {
        String invalidCsvData = "Invalid data";
        assertThrows(BiathlonParseException.class, () -> parseCsvData(invalidCsvData));
    }

    @org.junit.jupiter.api.Test
    public void testParseCsvInvalidTimeFormat() throws Exception {
        String csvData = "1,Ion Stoica";
    }
}


