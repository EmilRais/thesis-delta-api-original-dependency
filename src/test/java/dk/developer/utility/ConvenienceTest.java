package dk.developer.utility;

import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static dk.developer.testing.Truth.ASSERT;
import static dk.developer.utility.Convenience.list;
import static dk.developer.utility.Convenience.set;

public class ConvenienceTest {
    @Test
    public void listWithZeroItemsIsEmpty() throws Exception {
        ASSERT.that(list()).isEmpty();
    }

    @Test
    public void listWithOneElementContainsElement() throws Exception {
        List<String> list = list("Peter");
        ASSERT.that(list).containsExactly("Peter");
    }

    @Test
    public void listWithSeveralElementsContainElementsInOrder() throws Exception {
        List<String> list = list("Peter", "Lars");
        ASSERT.that(list).containsExactly("Peter", "Lars").inOrder();
    }

    @Test
    public void setWithZeroItemsIsEmpty() throws Exception {
        ASSERT.that(set()).isEmpty();
    }

    @Test
    public void setWithOneElementContainsElement() throws Exception {
        Set<String> set = set("Peter");
        ASSERT.that(set).containsExactly("Peter");
    }

    @Test
    public void setWithSeveralElementsContainsElements() throws Exception {
        Set<String> set = set("Peter", "Lars");
        ASSERT.that(set).containsExactly("Peter", "Lars");
    }
}