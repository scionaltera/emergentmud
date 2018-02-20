package com.emergentmud.core.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ZoneTest {
    private Zone zone;

    @Before
    public void setUp() {
        zone = new Zone();
        zone.setBottomLeftX(0L);
        zone.setBottomLeftY(0L);
        zone.setTopRightX(10L);
        zone.setTopRightY(10L);
    }

    @Test
    public void testEncompasses() {
        assertTrue(zone.encompasses(5L, 5L, 0L));
        assertTrue(zone.encompasses(0L, 0L, 0L));
        assertTrue(zone.encompasses(5L, 5L, 150L));
        assertFalse(zone.encompasses(-5L, -5L, 0L));
        assertFalse(zone.encompasses(15L, 15L, 0L));
    }
}
