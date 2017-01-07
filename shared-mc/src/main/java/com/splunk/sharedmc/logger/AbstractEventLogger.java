package com.splunk.sharedmc.logger;

import com.splunk.sharedmc.logger.events.LoggableEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * EventLoggers log to the Minecraft minecraft_server console and send data to Splunk.
 */
public abstract class AbstractEventLogger {
    public static final String LOGGER_NAME = "LogToSplunk";

    public static final String LOG_EVENTS_TO_CONSOLE_PROP_KEY = "splunk.craft.enable.consolelogging";
    public static final String SPLUNK_HOST = "splunk.craft.connection.host";
    public static final String SPLUNK_PORT = "splunk.craft.connection.port";
    public static final String MINECRAFT_SERVER = "splunk.craft.server.name";
    public static final String SPLUNK_TOKEN = "splunk.craft.token";

    protected static final Logger logger = LogManager.getLogger(LOGGER_NAME);
    protected static String minecraft_server;
    private static SingleSplunkConnection connection;
    /**
     * If true, events will be logged to the minecraft_server console.
     */
    private static boolean logEventsToConsole;
    private static String host;
    private static int port;
    private static String token;

    public AbstractEventLogger(Properties properties) {
        //  brittle way to do this
        if (connection == null) {
            logEventsToConsole = Boolean.valueOf(properties.getProperty(LOG_EVENTS_TO_CONSOLE_PROP_KEY, "true"));
            host = properties.getProperty(SPLUNK_HOST, "127.0.0.1");
            port = Integer.valueOf(properties.getProperty(SPLUNK_PORT, "8088"));
            minecraft_server = properties.getProperty(MINECRAFT_SERVER, "default");
            token = properties.getProperty(SPLUNK_TOKEN);
            if (token == null) {
                throw new IllegalArgumentException("The property `splunk.craft.token` must be set with the value of a" +
                        " splunk token in order to use the Splunk minecraft plugin/mod!");
            }

            connection = new SingleSplunkConnection(host, port, minecraft_server, token, true);
        }
    }


    /**
     * Logs via slf4j-simple and forwards the message to the message preparer.
     *
     * @param loggable The message to log.
     */
    protected void logAndSend(LoggableEvent loggable) {


        String message = loggable.toJSON();
        if (logEventsToConsole) {
            logger.info(message);
        }
        connection.sendToSplunk(message);
    }
}
