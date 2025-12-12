package com.project.golf.utils;

import java.io.*;
import java.util.Properties;

/**
 * ServerConfig.java
 * 
 * Utility class for loading server configuration from properties file.
 * Allows dynamic server IP/host configuration without hardcoding.
 *
 * @author GitHub Copilot Agent
 * @version December 12, 2025
 */
public class ServerConfig {
    
    private static final String CONFIG_FILE = "server.properties";
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5050;
    
    private static Properties properties = null;
    
    /**
     * Load configuration from properties file
     * Only loads once (lazy initialization)
     */
    private static void loadConfig() {
        if (properties != null) {
            return;
        }
        
        properties = new Properties();
        File configFile = new File(CONFIG_FILE);
        
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
                System.out.println("Loaded server configuration from " + CONFIG_FILE);
            } catch (IOException e) {
                System.err.println("Error loading server configuration: " + e.getMessage());
                System.err.println("Using default values.");
            }
        } else {
            System.out.println("Configuration file not found. Using default values.");
            System.out.println("You can create " + CONFIG_FILE + " to customize server settings.");
        }
    }
    
    /**
     * Get the server host/IP address
     * 
     * @return the server host (defaults to "localhost" if not configured)
     */
    public static String getServerHost() {
        loadConfig();
        return properties.getProperty("server.host", DEFAULT_HOST);
    }
    
    /**
     * Get the server port number
     * 
     * @return the server port (defaults to 5050 if not configured)
     */
    public static int getServerPort() {
        loadConfig();
        String portStr = properties.getProperty("server.port", String.valueOf(DEFAULT_PORT));
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number in configuration: " + portStr);
            System.err.println("Using default port: " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }
    
    /**
     * Reload configuration from file
     * Useful if the configuration file has been updated
     */
    public static void reloadConfig() {
        properties = null;
        loadConfig();
    }
    
    /**
     * Save current configuration to file
     * 
     * @param host the server host
     * @param port the server port
     * @throws IOException if unable to save configuration
     */
    public static void saveConfig(String host, int port) throws IOException {
        properties = new Properties();
        properties.setProperty("server.host", host);
        properties.setProperty("server.port", String.valueOf(port));
        
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Server Configuration for ParTee Golf Reservation System");
            System.out.println("Server configuration saved to " + CONFIG_FILE);
        }
    }
}
