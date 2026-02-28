package com.example.demo.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class Idgenerator {
    public static String generateId() {
        return NanoIdUtils.randomNanoId();
    }
}
