package net.jaggerwang.jwnettydemo.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationConfig {

    private static final Logger logger = LogManager.getLogger();

    private static Properties properties = new Properties();

    static {
        InputStream input;
        try {
            input = ApplicationConfig.class.getClassLoader().getResourceAsStream("application.properties");
            if (input != null) {
                properties.load(input);
                input.close();
                logger.info("load properties ok");
            } else {
                logger.error("open resource application.properties failed");
            }
        } catch (Exception e) {
            logger.error("load properties failed", e);
        }
    }

    public static String getProperty(String key) {
        return expandEnvVars(properties.getProperty(key));
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public static String expandEnvVars(String text) {
        if (text == null) {
            return text;
        }

        Map<String, String> envMap = System.getenv();
        String pattern = "\\$\\{([A-Z0-9_]+)(?::-(.+))?\\}";
        Pattern expr = Pattern.compile(pattern);
        Matcher matcher = expr.matcher(text);
        while (matcher.find()) {
            String key = matcher.group(1).toUpperCase();
            String value = envMap.get(key);
            if (value == null) {
                value = matcher.group(2);
                if (value == null) {
                    value = "";
                }
            }
            value = value.replace("\\", "\\\\");
            text = Pattern.compile(Pattern.quote(matcher.group(0))).matcher(text).replaceAll(value);
        }
        return text;
    }
}
