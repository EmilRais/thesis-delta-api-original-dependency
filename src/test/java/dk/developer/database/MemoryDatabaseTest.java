package dk.developer.database;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static dk.developer.database.DatabaseTestHelper.*;
import static dk.developer.database.MemoryDatabase.memoryDatabase;

public class MemoryDatabaseTest {
    private DatabaseLayer database;

    @BeforeMethod
    public void setUp() throws Exception {
        database = memoryDatabase();
    }

    @Test
    public void saveNewObject() throws Exception {
        saveAndLoadObject(database);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void saveExistingObject() throws Exception {
        saveSameObjectTwice(database);
    }

    @Test
    public void loadNonExistingObject() throws Exception {
        loadNoObject(database);
    }

    @Test
    public void loadExistingObject() throws Exception {
        saveAndLoadObject(database);
    }

    @Test
    public void loadNoObjects() throws Exception {
        loadZeroObjects(database);
    }

    @Test
    public void loadManyObjects() throws Exception {
        loadSeveralObjects(database);
    }

    @Test
    public void loadManyObjectsWithoutId() throws Exception {
        loadSeveralObjectsWithoutId(database);
    }

    @Test
    public void deleteNonExistingObject() throws Exception {
        deleteNoObject(database);
    }

    @Test
    public void deleteExistingObject() throws Exception {
        saveAndDeleteObject(database);
    }

    @Test
    public void updateNonExistingObject() throws Exception {
        updateNoObject(database);
    }

    @Test
    public void updateExistingObject() throws Exception {
        saveUpdateAndLoadObject(database);
    }
}