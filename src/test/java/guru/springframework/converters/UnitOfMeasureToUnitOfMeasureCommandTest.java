package guru.springframework.converters;

import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.domain.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnitOfMeasureToUnitOfMeasureCommandTest {

    public static final Long LONG_VALUE = Long.valueOf("1");
    public static final String DESCRIPTION =  "description";

    UnitOfMeasureToUnitOfMeasureCommand converter;

    @Before
    public void setUp() throws Exception {
        converter =  new UnitOfMeasureToUnitOfMeasureCommand();
    }

    @Test
    public void testNullObject() {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() {
        assertNotNull(converter.convert(new UnitOfMeasure()));
    }

    @Test
    public void convert() {
        // given
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setDescription(DESCRIPTION);
        uom.setId(LONG_VALUE);

        // when
        UnitOfMeasureCommand uomCommand = converter.convert(uom);

        // then
        assertNotNull(uomCommand);
        assertEquals(LONG_VALUE, uomCommand.getId());
        assertEquals(DESCRIPTION, uom.getDescription());
    }
}