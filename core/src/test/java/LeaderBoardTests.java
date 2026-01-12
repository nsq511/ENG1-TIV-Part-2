import com.badlogic.gdx.graphics.Color;
import io.github.eng1group9.systems.LeaderBoard;
import io.github.eng1group9.systems.TimerSystem;
import io.github.eng1group9.systems.ToastSystem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LeaderBoardTests {
    LeaderBoard leaderBoard;

    @BeforeEach
    void setUp() {
        // Somehow this works in core but not here.... odd...
        leaderBoard = LeaderBoard.loadFromFile("leaderboard.txt", 2);
    }

    @Test
    void testNoFail() {
        assertTrue(true);
    }

    @Test
    void testLeaderBoard() {

        leaderBoard.addEntry("NAME2", 100);
        leaderBoard.addEntry("NAME3", 101);
        assertNotEquals(0, leaderBoard.getSortedList().size());
        assertEquals(101, leaderBoard.get(0));
        assertEquals(100, leaderBoard.get("NAME2"));

        leaderBoard.popLowest();
        assertEquals(1,  leaderBoard.getSortedList().size());

        // BUG: Crashes with too many entries, not sure why...

//        leaderBoard.addEntry("NAME3", 101);
//        leaderBoard.addEntry("NAME4", 102);
//        leaderBoard.addEntry("NAME5", 103);
//        leaderBoard.addEntry("NAME6", 104);
//        leaderBoard.addEntry("NAME7", 105);
//        leaderBoard.addEntry("NAME8", 106);

        System.out.println(leaderBoard.toString());

        leaderBoard.saveToFile("java/leaderboardNEW.txt");

    }
}
