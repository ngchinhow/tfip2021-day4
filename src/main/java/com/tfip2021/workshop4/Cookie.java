package com.tfip2021.workshop4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;

public class Cookie {
    private String strPath;

    public Cookie(String fileName) {
        this.strPath = fileName;
    }
    public String getStrPath() { return this.strPath; }

    public String getCookie() throws IOException {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 876);
        int i = 0;
        String result = "";
        InputStream in = getClass().getResourceAsStream(this.getStrPath()); 
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while (i < randomNum) {
            result = reader.readLine();
            i++;
        }
        return result;
    }
}
