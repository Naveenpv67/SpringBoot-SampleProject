 public static void extractUserAgent(String userAgent) {
        // Patterns to match the required fields
        Pattern osPattern = Pattern.compile("\\(([^;]+); ([^;]+); ([^\\)]+)\\)");
        Pattern browserPattern = Pattern.compile("([a-zA-Z]+)/([\\d\\.]+)");

        Matcher osMatcher = osPattern.matcher(userAgent);
        Matcher browserMatcher = browserPattern.matcher(userAgent);

        String deviceOsPlatform = "", deviceOsVersion = "", deviceName = "", browserName = "", browserVersion = "";

        if (osMatcher.find()) {
            deviceOsPlatform = osMatcher.group(1);
            deviceOsVersion = osMatcher.group(2);
            deviceName = osMatcher.group(3);
        }

        if (browserMatcher.find()) {
            browserName = browserMatcher.group(1);
            browserVersion = browserMatcher.group(2);
        }

        // Printing the extracted information
        System.out.println("Device OS Platform: " + deviceOsPlatform);
        System.out.println("Device Name: " + deviceName);
        System.out.println("Device OS Version: " + deviceOsPlatform + " " + deviceOsVersion);
        System.out.println("Browser Version: " + browserName + " " + browserVersion);
    }

            String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        extractUserAgent(userAgent);
