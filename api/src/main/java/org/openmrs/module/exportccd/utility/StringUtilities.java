/**
 * Copyright 2007 Regenstrief Institute, Inc. All Rights Reserved.
 */
package org.openmrs.module.exportccd.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang.StringUtils;

/**
 * Class providing useful utilities in the way of String interaction.
 * Warning: some methods in this class are used by the Velocity templates
 * for the aggregate and unusual condition rate reports.
 */
public class StringUtilities {
    
    static private Pattern pattern = null;
    static private Matcher matcher = null;
    static private String lastMatch = null;
    
    public StringUtilities() {
    }

    public static boolean isNullEmptyOrWhitespace(String src) {
        return trim(src) == null;
    }

    /**
     * Appends spaces to a string to make it the size of
     * <code>desiredLength</code>. If the string is already the desired
     * length or longer, <code>str</code> is returned
     * 
     * @param str - string to append spaces to
     * @param desiredLength - length of the new string
     * @return String of desired length.
     */
    public static String padString(String str, int desiredLength) {
    	
    	StringBuffer paddedString = new StringBuffer();
    	if (str != null) {
    		paddedString = new StringBuffer(str);
    	}

        while (paddedString.length() < desiredLength) {
            paddedString.append(' '); // pad with spaces
        }

        return paddedString.toString();
    }

    /**
     * Constructs a String containing "count" copies of "str".
     * 
     * @param str The String to be repeated.
     * @param count The number of copies of "str" to include.
     * @return The constructed string.
     */
    public static String repeat(String str, int count) {
        
        StringBuffer result = new StringBuffer();
        while (count-- > 0) {
            result.append(str);
        }
        return result.toString();
    }

    /**
     * Takes a string that contains escaped unicode strings in the form of
     * \\u00xx and turns them into the actual unicode character. It also removes
     * line feeds and carriage returns.
     * 
     * @param str - string to be escaped
     * @return The escaped string
     */
    public static String escapeMessageString(String str) {
        str = str.replaceAll("\n", "").replaceAll("\r", "");

        int index = str.indexOf("\\u00");

        while (index > -1) {
            String escapeString = str.substring(index, index + 6);

            Character escapedChar = Character.valueOf((char) ((Character.digit(
                    escapeString.charAt(4), 16) * 16) + Character.digit(
                    escapeString.charAt(5), 16)));

            str = str.replaceAll("\\" + escapeString, escapedChar.toString());

            index = str.indexOf("\\u00");
        }

        return str;
    }
    
    /**
     * Takes a possibly null String, trims off leading and trailing
     * whitespace, and returns null if the result is null or empty.
     * 
     * @param src The string to be trimmed.
     * @return null, if src is null, empty or all whitespace; src.trim()
     * otherwise.
     */
    public static String trim(String src) {
        
        if (src == null) {
            return null;
        }
        else {
            
            String trimmed = src.trim();
            if (StringUtils.isEmpty(trimmed)) {
                return null;
            }
            else {
                return trimmed;
            }
        }
    }

    /**
     * Converts an array of String to a single String in CSV format (with escaping).
     *  
     * @param sarray The String array to be converted to a CSV string.
     * @return The CSV representation of the string array.
     */
    public static String toCSV(String[] sarray) {

        if (sarray == null) {
            return "";
        }
        
        String result = "";
        for (int i=0; i < sarray.length; i++) {
            result += csvEscape(sarray[i]);
            if (i < sarray.length - 1) {
                result += ",";
            }
        }
        
        return result;
    }

    public static String csvEscape(String s) {
        
        // If the string contains no special characters, just return it without escaping
        if (s.indexOf('\n') == -1 && 
            s.indexOf('\r') == -1 && 
            s.indexOf(',') == -1 && 
            s.indexOf('"') == -1) {
            
            return s;
        }
        
        // Incoming string contains at least one special character and requires escaping.
        // Output the original string wrapped in double quotes, and with any embedded double quotes
        // escaped by duplicating the embedded double quote.
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i=0; i < s.length(); i++) {
            if (s.charAt(i) == '"') {
                sb.append('"');
            }
            sb.append(s.charAt(i));
        }
        sb.append('"');
        
        return sb.toString();
    }

    /**
     * Converts a CSV String to a String array (with unescaping).
     * @param s The CSV string to be converted to a String array. 
     * @return The String array.
     */
    public static String[] fromCSV(String s) {
        if (s == null) {
            return new String[0];
        }
        int i=0;
        ArrayList<String> strings = new ArrayList<String>();
        boolean inQuote = false;
        StringBuilder sb = new StringBuilder();
        while (i < s.length()) {
            if (inQuote) {
                if (s.charAt(i) == '\"') {
                    if (i+1 < s.length() && s.charAt(i+1) == '\"') {
                        sb.append('\"');
                        i+=2;
                    } else {
                        inQuote = false;
                        i++;
                    }
                } else {
                    sb.append(s.charAt(i++));
                }
            } else {
                if (s.charAt(i) == ',') {
                    strings.add(sb.toString());
                    sb = new StringBuilder();
                    i++;
                } else if (s.charAt(i) == '\"') {
                    inQuote = true;
                    i++;
                } else {
                    sb.append(s.charAt(i++));
                }
            }
        }
        if (sb.length() > 0) {
            strings.add(sb.toString());
            sb = null;
        }
        
        String[] result = new String[strings.size()];
        strings.toArray(result);
        return result;
    }
    
    public static List<String> splitStringToList(String stringToSplit, String splitAt) {
        // default the string array to null
        String[] retVal = null;
        if (stringToSplit != null)
        {
            // set the string array if the negString is not null
            retVal = stringToSplit.split(splitAt);
        }
        return (retVal == null ? null : Arrays.asList(retVal));        
    }
    
    public static boolean doesRegExMatch(String regEx, String toMatch, boolean caseSensitive, int groupNum) {
        pattern = ((caseSensitive) ? Pattern.compile(regEx) : Pattern.compile(regEx, Pattern.CASE_INSENSITIVE));
        matcher = pattern.matcher(toMatch);
        boolean retVal = matcher.find();
        if (retVal) {
            lastMatch = groupNum == 0 ? matcher.group() : matcher.group(groupNum);
        }
        return retVal;
    }
    
    public static boolean doesRegExMatch(String regEx, String toMatch, boolean caseSensitive) {
        return doesRegExMatch(regEx, toMatch, caseSensitive, 0);
    }
    
    public static boolean doesRegExMatch(String regEx, String toMatch) {
        return doesRegExMatch(regEx, toMatch, true);
    }
    
    public static boolean doesRegExMatch(String regEx, String toMatch, int groupNum) {
        return doesRegExMatch(regEx, toMatch, true, groupNum);
    }
    
    public static String getGroupFromLastMatch() {
        return lastMatch;
    }

    /**
     * Splits a String like String.split, except that the pattern is a simple string, not a regular expression.
     * 
     * @param src
     * @param pattern
     * @return
     */
    public static String[] strSplit(String src, String delim) {
    	
    	// TODO: replace this by return src.split(Pattern.quote(delim));
    	
    	ArrayList<String> results = new ArrayList<String>();
    	int fromIndex = 0;
    	int len = src.length();
    	while (fromIndex <= len) {
    		int toIndex = src.indexOf(delim, fromIndex);
    		if (toIndex < 0) {
    			toIndex = len;
    		}
    		String next = src.substring(fromIndex, toIndex);
    		results.add(next);
    		fromIndex = toIndex + delim.length();
    	}

    	return results.toArray(new String[0]);
    }

    public static String merge(String[] elements, String separator) {
        
        if (elements == null || elements.length == 0) {
            
            return "";
        }
        
        StringBuilder buf = new StringBuilder();
        for (String element : elements) {
            if (buf.length() > 0) {
                buf.append(separator);
            }
            buf.append(element);
        }
        
        return buf.toString();
    }
    
    public static String merge(Collection<String> elements, String separator) {
        
        if (elements == null || elements.size() == 0) {
            
            return "";
        }
        
        StringBuilder buf = new StringBuilder();
        for (String element : elements) {
            if (buf.length() > 0) {
                buf.append(separator);
            }
            buf.append(element);
        }
        
        return buf.toString();
    }

    /**
     * Compare two possibly null Strings for equality
     * 
     * @param left
     * @param right
     * @return
     */
    public static boolean equals(String left, String right) {

        // If both are null, they're equal
        if (left == null && right == null) {
            
            return true;
        }

        // If one is null, they're not equal
        if (left == null || right == null) {
            
            return false;
        }

        // Otherwise compare the string text
        return left.equals(right);
    }
    
    /**
     * 
     */
    public static int compareTo(String left, String right) {
        
        // If both are null, they're equal
        if (left == null && right == null) {
            
            return 0;
        }

        // If the left is null, it's more than the right (null comes last)
        if (left == null) {
            
            return 1;
        }

        // If the right is null, it's more than the left
        if (right == null) {
            
            return -1;
        }
        
        // Otherwise compare the string text
        return left.compareTo(right);
    }
    
    public static double calculateNormalizedCompressionDistance(String x, String y) 
    	throws IOException {    	
    	String xy = x + y;    	
    	
    	ByteArrayOutputStream xOutStream = new ByteArrayOutputStream();
    	ByteArrayOutputStream yOutStream = new ByteArrayOutputStream();
    	ByteArrayOutputStream xyOutStream = new ByteArrayOutputStream();
    	GZIPOutputStream gzipStream = null;
    	gzipStream = new GZIPOutputStream(xOutStream);  
    	gzipStream.write(x.getBytes());
    	gzipStream.close();
    	gzipStream = new GZIPOutputStream(yOutStream);
    	gzipStream.write(y.getBytes());
    	gzipStream.close();
    	gzipStream = new GZIPOutputStream(xyOutStream);
    	gzipStream.write(xy.getBytes());
    	gzipStream.close();
    	
    	int numXBytes = xOutStream.size();
    	int numYBytes = yOutStream.size();
    	int numXYBytes = xyOutStream.size();
    	
    	int maxC = numXYBytes - Math.min(numXBytes, numYBytes);
    	int maxXY = Math.max(numXBytes, numYBytes);
    	    	
    	double normCompDist = (double)maxC/(double)maxXY;    	
    	return normCompDist;
    }
    
    public static String truncateString(String stringToTruncate, int maxSize) {
    	String retVal = stringToTruncate;
    	if (stringToTruncate != null && stringToTruncate.length() > maxSize) {
    		retVal = stringToTruncate.substring(0, maxSize);
    	}
    	
    	return retVal;
    }
    
    public static long parseSize(String value) {
    	long multiplier = 1L;
        String strValue = value.toLowerCase();
        if (strValue.endsWith("kb")) {
        	strValue = strValue.substring(0, strValue.length() - 2);
        	multiplier = 1024L;
        }
        else if (strValue.endsWith("mb")) {
        	strValue = strValue.substring(0, strValue.length() - 2);
        	multiplier = 1048576L;
        }
        else if (strValue.endsWith("gb")) {
        	strValue = strValue.substring(0, strValue.length() - 2);
        	multiplier = 1073741824L;
        }
        long longValue = Long.parseLong(strValue);
        return longValue * multiplier;
    }

    /**
     * Returns a copy of the source String with any non-printable characters converted to escape sequences.
     * @param value
     * @return
     */
    public static String dump(String value) {

    	StringBuilder result = new StringBuilder();
    	for (int index = 0; index < value.length(); index++) {
    		
    		char c = value.charAt(index);

    		if (c == '\r') {
    			result.append("\\r");
    		}
    		else if (c == '\n') {
    			result.append("\\n");
    		}
    		else if (c == '\t') {
    			result.append("\\t");
    		}
    		else if (Character.isISOControl(c)) {
    			result.append("\\u0");
    			result.append(Integer.toString(c, 8));
    		}
    		else {
    			result.append(c);
    		}
    	}
    	return result.toString();
    }
}
