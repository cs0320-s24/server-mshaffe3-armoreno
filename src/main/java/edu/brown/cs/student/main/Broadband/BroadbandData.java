package Broadband;

import java.util.Calendar;

/**
 * This will hold the response data we want to give to clients
 *
 * @param percentage - the percentage of people that have broadband access in a given state and
 *     county
 * @param dateTime - the time at which this data was accessed
 */
public record BroadbandData(Broadband percentage, Calendar dateTime, String state, String county) {}
