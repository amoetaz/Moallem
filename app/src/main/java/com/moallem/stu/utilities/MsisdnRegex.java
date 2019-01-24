package com.moallem.stu.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum MsisdnRegex {

    PATTERN_1("[+]?[0-9]{10}")
    , PATTERN_2("^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$")
    , PATTERN_3("[0-9]{10}")
    ,PATTERN_4("[+]?[0-9]{2}[-][0-9]{10}")
    ,PATTERN_5("[+]?[0-9]{12}");

    String  pattern = "";

    MsisdnRegex(String pattern)
    {
        this.pattern = pattern;
    }

    /**
     * @return the pattern
     */
    public String getPattern()
    {
        return pattern;
    }

    public static boolean isValidMsisdn(String msisdn)
    {
        for (MsisdnRegex regex : values())
        {
            Pattern pattern = Pattern.compile(regex.getPattern());
            Matcher matcher = pattern.matcher(msisdn);
            if(matcher.matches())
                return true;
        }
        return false;
    }
}
