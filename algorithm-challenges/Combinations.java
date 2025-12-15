package sprint;

import java.util.ArrayList;
import java.util.List;

public class Combinations {
    public List<String> combN(int n) {
        List<String> results = new ArrayList<>();
        if (n <= 0 || n > 10) {
            return results;
        }
        combinations(0, n, "", results);
        return results;
    }

    private void combinations(int startDigit, int remaining, String current, List<String> results) {
        if (remaining == 0) {
            results.add(current);
            return;
        }

        for (int i = startDigit; i <= 9; i++) {
            combinations(i + 1, remaining - 1, current + i, results);
        }
    }
}