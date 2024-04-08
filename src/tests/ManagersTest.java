package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import taskManager.*;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void assertEqualsInMemoryHistoryManagerTest() {
        HistoryManager expected = new InMemoryHistoryManager();
        HistoryManager actual = Managers.getDefaultHistory();
        Assertions.assertNotNull(actual, "Объект не был создан.");
        assertEquals(expected.getHistory(), actual.getHistory(), ", history");
    }

    @Test
    public void assertEqualsTaskManagerTest() {
        TaskManager expected = new InMemoryTaskManager();
        TaskManager actual = Managers.getDefault();
        Assertions.assertNotNull(actual, "Объект не был создан.");
        assertEquals(expected.getAllTask(), actual.getAllTask(), ", tasks");
    }
}