package com.tfip2021.workshop4;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class CookieTest {
    @Test
    public void testGetCookie() throws IOException {
        Cookie cookie = new Cookie("cookie_file.txt");
        Set<String> s = new HashSet<String>();
        int n = 10;
        for (int i = 0; i < n; i++) {
            s.add(cookie.getCookie());
        }
        assertEquals(n, s.size());
    }
}
