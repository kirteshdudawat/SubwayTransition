package com.kirtesh.subwaytransition;

import com.kirtesh.subwaytransition.controllers.ExternalController;
import com.kirtesh.subwaytransition.controllers.HealthCheckController;
import com.kirtesh.subwaytransition.response.CustomError;
import com.kirtesh.subwaytransition.response.Response;
import com.kirtesh.subwaytransition.response.SearchAvailablePathResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SubwayTransitionApplicationTests {

    @Autowired
    private HealthCheckController healthCheckController;

    @Autowired
    private ExternalController externalController;

    @Test
    void contextLoads() {
        assertThat(healthCheckController).isNotNull();
    }

    @Test
    void normalTimeTravelTest() {
        ExpectedResponses responses = new ExpectedResponses();
        LocalDateTime dateTime = LocalDateTime.of(2100, 06, 07, 10, 10);
        ResponseEntity<Response<SearchAvailablePathResponse>> response = externalController.getPaths("CC21", "DT14", dateTime);

        Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "HTTP Status code is invalid");
        Assert.isTrue(responses.normalTimeResponse.equals(response.getBody().getBody().getResponse()), "Invalid Steps provided.");
    }

    /**
     * Starts in non-peak hours
     */
    @Test
    void peakHoursTravelTest() {
        ExpectedResponses responses = new ExpectedResponses();
        LocalDateTime dateTime = LocalDateTime.of(2100, 06, 07, 17, 55);
        ResponseEntity<Response<SearchAvailablePathResponse>> response = externalController.getPaths("CC21", "DT14", dateTime);

        Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "HTTP Status code is invalid");
        Assert.isTrue(responses.peakHourResponse.equals(response.getBody().getBody().getResponse()), "Invalid Steps provided.");
    }

    /**
     * Starts in non-night hours
     */
    @Test
    void nightHoursTravelTest() {
        ExpectedResponses responses = new ExpectedResponses();
        LocalDateTime dateTime = LocalDateTime.of(2100, 06, 07, 21, 45);
        ResponseEntity<Response<SearchAvailablePathResponse>> response = externalController.getPaths("CC21", "DT14", dateTime);

        Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "HTTP Status code is invalid");
        Assert.isTrue(responses.nightHoursSteps.equals(response.getBody().getBody().getResponse()), "Invalid Steps provided.");
    }

    @Test
    void pastDateTest_shouldReturnErrorCode1302() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 06, 07, 21, 45);
        ResponseEntity<Response<SearchAvailablePathResponse>> response = externalController.getPaths("CC21", "DT14", dateTime);

        CustomError error = response.getBody().getErrors().get(0);
        Assert.isTrue(response.getStatusCode() == HttpStatus.EXPECTATION_FAILED, "HTTP Status code is invalid");
        Assert.isTrue(error.getCode().equals("1302"), "Invalid Error code returned");
        Assert.isTrue(error.getMessage().equals("Requested time of travel is in past, please provide correct travel time."), "Invalid error message returned");
    }

    @Test
    void sameSourceDestination_shouldReturnErrorCode1303() {
        LocalDateTime dateTime = LocalDateTime.of(2121, 06, 07, 21, 45);
        ResponseEntity<Response<SearchAvailablePathResponse>> response = externalController.getPaths("CC21", "CC21", dateTime);

        CustomError error = response.getBody().getErrors().get(0);
        Assert.isTrue(response.getStatusCode() == HttpStatus.EXPECTATION_FAILED, "HTTP Status code is invalid");
        Assert.isTrue(error.getCode().equals("1303"), "Invalid Error code returned");
        Assert.isTrue(error.getMessage().equals("Requested travel between source and destination station are same (Please provide different source and destination station)."),"Invalid error message returned");
    }

    @Test
    void invalidSourceDestination_shouldReturnErrorCode1301() {
        LocalDateTime dateTime = LocalDateTime.of(2121, 06, 07, 21, 45);
        ResponseEntity<Response<SearchAvailablePathResponse>> response = externalController.getPaths("CC21", "PQ21", dateTime);

        CustomError error = response.getBody().getErrors().get(0);
        Assert.isTrue(response.getStatusCode() == HttpStatus.EXPECTATION_FAILED, "HTTP Status code is invalid");
        Assert.isTrue(error.getCode().equals("1301"), "Invalid Error code returned");
        Assert.isTrue(error.getMessage().equals("Requested source or destination does not exist."),"Invalid error message returned");
    }

    @Test
    void noPathExist_shouldReturnErrorCode1601() {
        LocalDateTime dateTime = LocalDateTime.of(2121, 06, 07, 21, 55);
        ResponseEntity<Response<SearchAvailablePathResponse>> response = externalController.getPaths("CC21", "CG1", dateTime);

        CustomError error = response.getBody().getErrors().get(0);
        Assert.isTrue(response.getStatusCode() == HttpStatus.EXPECTATION_FAILED, "HTTP Status code is invalid");
        Assert.isTrue(error.getCode().equals("1601"), "Invalid Error code returned");
        Assert.isTrue(error.getMessage().equals("No way exist between selected source and destination. It could be due to station / lines are non-operational for selected time."),"Invalid error message returned");
    }

    class ExpectedResponses {
        String normalTimeResponse = "### Fastest Route from Holland Village to Bugis. (Time based)\n" +
                "Journey Start Time: 2100-06-07T10:10\n" +
                "Journey End Time: 2100-06-07T11:20\n" +
                "Total Travel Time (In Minutes): 70\n" +
                "Total Stations in Journey: 7\n" +
                "\n" +
                "Stations Travelled: [Holland Village, Farrer Road, Botanic Gardens, Stevens, Newton, Little India, Rochor, Bugis]\n" +
                "\n" +
                "Details: \n" +
                "Take CC line from Holland Village to Farrer Road [ TimeOfVisit: 2100-06-07T10:20, Travelled stations so far: 1, JourneyTime in Minutes: 10 ]\n" +
                "Take CC line from Farrer Road to Botanic Gardens [ TimeOfVisit: 2100-06-07T10:30, Travelled stations so far: 2, JourneyTime in Minutes: 20 ]\n" +
                "Change from CC line to DT line\n" +
                "Take DT line from Botanic Gardens to Stevens [ TimeOfVisit: 2100-06-07T10:48, Travelled stations so far: 3, JourneyTime in Minutes: 38 ]\n" +
                "Take DT line from Stevens to Newton [ TimeOfVisit: 2100-06-07T10:56, Travelled stations so far: 4, JourneyTime in Minutes: 46 ]\n" +
                "Take DT line from Newton to Little India [ TimeOfVisit: 2100-06-07T11:04, Travelled stations so far: 5, JourneyTime in Minutes: 54 ]\n" +
                "Take DT line from Little India to Rochor [ TimeOfVisit: 2100-06-07T11:12, Travelled stations so far: 6, JourneyTime in Minutes: 62 ]\n" +
                "Take DT line from Rochor to Bugis [ TimeOfVisit: 2100-06-07T11:20, Travelled stations so far: 7, JourneyTime in Minutes: 70 ]\n" +
                "\n" +
                "\n" +
                "### Shortest Route from Holland Village to Bugis. (Based on station travelled)\n" +
                "Journey Start Time: 2100-06-07T10:10\n" +
                "Journey End Time: 2100-06-07T11:20\n" +
                "Total Travel Time (In Minutes): 70\n" +
                "Total Stations in Journey: 7\n" +
                "\n" +
                "Stations Travelled: [Holland Village, Farrer Road, Botanic Gardens, Stevens, Newton, Little India, Rochor, Bugis]\n" +
                "\n" +
                "Details: \n" +
                "Take CC line from Holland Village to Farrer Road [ TimeOfVisit: 2100-06-07T10:20, Travelled stations so far: 1, JourneyTime in Minutes: 10 ]\n" +
                "Take CC line from Farrer Road to Botanic Gardens [ TimeOfVisit: 2100-06-07T10:30, Travelled stations so far: 2, JourneyTime in Minutes: 20 ]\n" +
                "Change from CC line to DT line\n" +
                "Take DT line from Botanic Gardens to Stevens [ TimeOfVisit: 2100-06-07T10:48, Travelled stations so far: 3, JourneyTime in Minutes: 38 ]\n" +
                "Take DT line from Stevens to Newton [ TimeOfVisit: 2100-06-07T10:56, Travelled stations so far: 4, JourneyTime in Minutes: 46 ]\n" +
                "Take DT line from Newton to Little India [ TimeOfVisit: 2100-06-07T11:04, Travelled stations so far: 5, JourneyTime in Minutes: 54 ]\n" +
                "Take DT line from Little India to Rochor [ TimeOfVisit: 2100-06-07T11:12, Travelled stations so far: 6, JourneyTime in Minutes: 62 ]\n" +
                "Take DT line from Rochor to Bugis [ TimeOfVisit: 2100-06-07T11:20, Travelled stations so far: 7, JourneyTime in Minutes: 70 ]";

        String peakHourResponse = "### Fastest Route from Holland Village to Bugis. (Time based)\n" +
                "Journey Start Time: 2100-06-07T17:55\n" +
                "Journey End Time: 2100-06-07T19:20\n" +
                "Total Travel Time (In Minutes): 85\n" +
                "Total Stations in Journey: 7\n" +
                "\n" +
                "Stations Travelled: [Holland Village, Farrer Road, Botanic Gardens, Stevens, Newton, Little India, Rochor, Bugis]\n" +
                "\n" +
                "Details: \n" +
                "Take CC line from Holland Village to Farrer Road [ TimeOfVisit: 2100-06-07T18:05, Travelled stations so far: 1, JourneyTime in Minutes: 10 ]\n" +
                "Take CC line from Farrer Road to Botanic Gardens [ TimeOfVisit: 2100-06-07T18:15, Travelled stations so far: 2, JourneyTime in Minutes: 20 ]\n" +
                "Change from CC line to DT line\n" +
                "Take DT line from Botanic Gardens to Stevens [ TimeOfVisit: 2100-06-07T18:40, Travelled stations so far: 3, JourneyTime in Minutes: 45 ]\n" +
                "Take DT line from Stevens to Newton [ TimeOfVisit: 2100-06-07T18:50, Travelled stations so far: 4, JourneyTime in Minutes: 55 ]\n" +
                "Take DT line from Newton to Little India [ TimeOfVisit: 2100-06-07T19:00, Travelled stations so far: 5, JourneyTime in Minutes: 65 ]\n" +
                "Take DT line from Little India to Rochor [ TimeOfVisit: 2100-06-07T19:10, Travelled stations so far: 6, JourneyTime in Minutes: 75 ]\n" +
                "Take DT line from Rochor to Bugis [ TimeOfVisit: 2100-06-07T19:20, Travelled stations so far: 7, JourneyTime in Minutes: 85 ]\n" +
                "\n" +
                "\n" +
                "### Shortest Route from Holland Village to Bugis. (Based on station travelled)\n" +
                "Journey Start Time: 2100-06-07T17:55\n" +
                "Journey End Time: 2100-06-07T19:20\n" +
                "Total Travel Time (In Minutes): 85\n" +
                "Total Stations in Journey: 7\n" +
                "\n" +
                "Stations Travelled: [Holland Village, Farrer Road, Botanic Gardens, Stevens, Newton, Little India, Rochor, Bugis]\n" +
                "\n" +
                "Details: \n" +
                "Take CC line from Holland Village to Farrer Road [ TimeOfVisit: 2100-06-07T18:05, Travelled stations so far: 1, JourneyTime in Minutes: 10 ]\n" +
                "Take CC line from Farrer Road to Botanic Gardens [ TimeOfVisit: 2100-06-07T18:15, Travelled stations so far: 2, JourneyTime in Minutes: 20 ]\n" +
                "Change from CC line to DT line\n" +
                "Take DT line from Botanic Gardens to Stevens [ TimeOfVisit: 2100-06-07T18:40, Travelled stations so far: 3, JourneyTime in Minutes: 45 ]\n" +
                "Take DT line from Stevens to Newton [ TimeOfVisit: 2100-06-07T18:50, Travelled stations so far: 4, JourneyTime in Minutes: 55 ]\n" +
                "Take DT line from Newton to Little India [ TimeOfVisit: 2100-06-07T19:00, Travelled stations so far: 5, JourneyTime in Minutes: 65 ]\n" +
                "Take DT line from Little India to Rochor [ TimeOfVisit: 2100-06-07T19:10, Travelled stations so far: 6, JourneyTime in Minutes: 75 ]\n" +
                "Take DT line from Rochor to Bugis [ TimeOfVisit: 2100-06-07T19:20, Travelled stations so far: 7, JourneyTime in Minutes: 85 ]";

        String nightHoursSteps = "### Fastest Route from Holland Village to Bugis. (Time based)\n" +
                "Journey Start Time: 2100-06-07T21:45\n" +
                "Journey End Time: 2100-06-07T23:35\n" +
                "Total Travel Time (In Minutes): 110\n" +
                "Total Stations in Journey: 10\n" +
                "\n" +
                "Stations Travelled: [Holland Village, Buona Vista, Commonwealth, Queenstown, Redhill, Tiong Bahru, Outram Park, Tanjong Pagar, Raffles Place, City Hall, Bugis]\n" +
                "\n" +
                "Details: \n" +
                "Take CC line from Holland Village to Buona Vista [ TimeOfVisit: 2100-06-07T21:55, Travelled stations so far: 1, JourneyTime in Minutes: 10 ]\n" +
                "Change from CC line to EW line\n" +
                "Take EW line from Buona Vista to Commonwealth [ TimeOfVisit: 2100-06-07T22:15, Travelled stations so far: 2, JourneyTime in Minutes: 30 ]\n" +
                "Take EW line from Commonwealth to Queenstown [ TimeOfVisit: 2100-06-07T22:25, Travelled stations so far: 3, JourneyTime in Minutes: 40 ]\n" +
                "Take EW line from Queenstown to Redhill [ TimeOfVisit: 2100-06-07T22:35, Travelled stations so far: 4, JourneyTime in Minutes: 50 ]\n" +
                "Take EW line from Redhill to Tiong Bahru [ TimeOfVisit: 2100-06-07T22:45, Travelled stations so far: 5, JourneyTime in Minutes: 60 ]\n" +
                "Take EW line from Tiong Bahru to Outram Park [ TimeOfVisit: 2100-06-07T22:55, Travelled stations so far: 6, JourneyTime in Minutes: 70 ]\n" +
                "Take EW line from Outram Park to Tanjong Pagar [ TimeOfVisit: 2100-06-07T23:05, Travelled stations so far: 7, JourneyTime in Minutes: 80 ]\n" +
                "Take EW line from Tanjong Pagar to Raffles Place [ TimeOfVisit: 2100-06-07T23:15, Travelled stations so far: 8, JourneyTime in Minutes: 90 ]\n" +
                "Take EW line from Raffles Place to City Hall [ TimeOfVisit: 2100-06-07T23:25, Travelled stations so far: 9, JourneyTime in Minutes: 100 ]\n" +
                "Take EW line from City Hall to Bugis [ TimeOfVisit: 2100-06-07T23:35, Travelled stations so far: 10, JourneyTime in Minutes: 110 ]\n" +
                "\n" +
                "\n" +
                "### Shortest Route from Holland Village to Bugis. (Based on station travelled)\n" +
                "Journey Start Time: 2100-06-07T21:45\n" +
                "Journey End Time: 2100-06-07T23:35\n" +
                "Total Travel Time (In Minutes): 110\n" +
                "Total Stations in Journey: 10\n" +
                "\n" +
                "Stations Travelled: [Holland Village, Buona Vista, Commonwealth, Queenstown, Redhill, Tiong Bahru, Outram Park, Tanjong Pagar, Raffles Place, City Hall, Bugis]\n" +
                "\n" +
                "Details: \n" +
                "Take CC line from Holland Village to Buona Vista [ TimeOfVisit: 2100-06-07T21:55, Travelled stations so far: 1, JourneyTime in Minutes: 10 ]\n" +
                "Change from CC line to EW line\n" +
                "Take EW line from Buona Vista to Commonwealth [ TimeOfVisit: 2100-06-07T22:15, Travelled stations so far: 2, JourneyTime in Minutes: 30 ]\n" +
                "Take EW line from Commonwealth to Queenstown [ TimeOfVisit: 2100-06-07T22:25, Travelled stations so far: 3, JourneyTime in Minutes: 40 ]\n" +
                "Take EW line from Queenstown to Redhill [ TimeOfVisit: 2100-06-07T22:35, Travelled stations so far: 4, JourneyTime in Minutes: 50 ]\n" +
                "Take EW line from Redhill to Tiong Bahru [ TimeOfVisit: 2100-06-07T22:45, Travelled stations so far: 5, JourneyTime in Minutes: 60 ]\n" +
                "Take EW line from Tiong Bahru to Outram Park [ TimeOfVisit: 2100-06-07T22:55, Travelled stations so far: 6, JourneyTime in Minutes: 70 ]\n" +
                "Take EW line from Outram Park to Tanjong Pagar [ TimeOfVisit: 2100-06-07T23:05, Travelled stations so far: 7, JourneyTime in Minutes: 80 ]\n" +
                "Take EW line from Tanjong Pagar to Raffles Place [ TimeOfVisit: 2100-06-07T23:15, Travelled stations so far: 8, JourneyTime in Minutes: 90 ]\n" +
                "Take EW line from Raffles Place to City Hall [ TimeOfVisit: 2100-06-07T23:25, Travelled stations so far: 9, JourneyTime in Minutes: 100 ]\n" +
                "Take EW line from City Hall to Bugis [ TimeOfVisit: 2100-06-07T23:35, Travelled stations so far: 10, JourneyTime in Minutes: 110 ]";
    }

}
