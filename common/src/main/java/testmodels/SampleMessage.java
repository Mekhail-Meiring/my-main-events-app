package testmodels;

/**
 * POJO used to parse to Json.
 */
public record SampleMessage(String fromPersonEmail, String toPersonEmail, String date, String time, String messageBody) {
}
