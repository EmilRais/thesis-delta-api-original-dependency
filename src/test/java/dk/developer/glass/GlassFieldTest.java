package dk.developer.glass;

import dk.developer.glass.TestObjects.Fields;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static dk.developer.testing.Truth.ASSERT;
import static dk.developer.glass.Handler.*;

public class GlassFieldTest {
    @Test
    public void shouldBecomeGlass() throws Exception {
        Field justShard = Fields.class.getDeclaredField("justShard");
        Handler handler = Handler.of(justShard);
        ASSERT.that(handler).isEqualTo(GLASS);
    }

    @Test
    public void shouldBecomeJackson() throws Exception {
        Field defaultShard = Fields.class.getDeclaredField("defaultShard");
        Handler handler = Handler.of(defaultShard);
        ASSERT.that(handler).isEqualTo(JACKSON);
    }
}