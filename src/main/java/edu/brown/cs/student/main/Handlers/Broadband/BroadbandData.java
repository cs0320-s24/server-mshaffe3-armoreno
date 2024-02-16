package Handlers.Broadband;

/**
 * This will hold the response data we want to give to clients
 *
 * @param percentage - the percentage of people that have broadband access in a given state and
 *     county
 * @param dateTime - the time at which this data was accessed
 */
public record BroadbandData(String result, Broadband percentage, String dateTime, String state, String county) {}
