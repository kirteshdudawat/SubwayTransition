### NOTE:
This solution uses Rule Engine. Make sure if you add / update rules in application.yml, it should be in correct syntax. All calculations happens via Rule defined in application.yml. Rules should be defined for each lines (CC, DT, etc) of Subway.

# Problem Statement
We want to build a routing service, to help users find routes from source to any destination on network based on some efficiency heuristic. We would be using Singapore's urban rail system as example.

The API should read node / station information from a datasource (We are using CSV for an example, see StationMap.csv in resources for example).
Note, API will provide information to user to follow to reach from source to destination with relevant information.

### Data Description

The included file, StationMap.csv, describes Singapore's urban rail network. Here is an extract:

```
EW23,Clementi,12 March 1988
EW24,Jurong East,5 November 1988
EW25,Chinese Garden,5 November 1988
EW26,Lakeside,5 November 1988
```

Each line in the file has 3 fields, station code, station name, and date of opening.

Note that there may be interchange stations (where train lines cross) like Buona Vista, and these are listed each time they appear in a line. For e.g., for Buona Vista, it's listed as EW21 and CC22 both. Additionally, position numbers are not always sequential; the gaps represent spaces left for future stations.

Trains can be assumed to run in both directions on every line.

For this example, we would consider line names to be displayed, using the two-letter code eg. EW.

### Other complexity / constraints:

Travel times between stations change based on the time of day, due to increased/decreased traffic and frequency in the following manner:

```
Non-Peak hours (all other times)
DT and TE lines take 8 minutes per stop
All trains take 10 minutes per stop
Every train line change adds 10 minutes of waiting time to the journey

Peak hours (6am-9am and 6pm-9pm on Mon-Fri)
NS and NE lines take 12 minutes per station
All other train lines take 10 minutes
Every train line change adds 15 minutes of waiting time to the journey

Night hours (10pm-6am on Mon-Sun)
DT, CG and CE lines do not operate
TE line takes 8 minutes per stop
All trains take 10 minutes per stop
Every train line change adds 10 minutes of waiting time to the journey
```

To account for these constraints, application will expose method that accepts a source, destination and start time ("YYYY-MM-DDThh:mm" format, e.g. '2019-01-31T16:00') and returns one or more routes ordered by an efficiency heuristic with clear steps involved, as well as the total travel time for each route generated. If no route is available between the selected stations, this will also be communicated clearly.



# Assumptions:
1. StationList provided in excel would always have date in `d MMMM yyyy` format.
2. Station code would be unique for each station for a line. If station is interchanging station, then it should have separate station code corresponding to each line.
3. If station is interchanging station, station name will still be same on all lines (Case Sensitive).
4. In case we have multiple optimized path (with same complexity), we would return any one of the optimized path.
5. Two station / node could only have one connecting line.
6. Each Station Line would have its corresponding rules to calculate travel-time and stop-time in application.yml.
7. Station Code should be alpha-numeric, with first two character as Capital alphabet, which should be the line code as well. Forst two characters could be followed by numbers.




# Implementation understandings:
We have solved the problem using Dijkstra's algorithm using PriorityQueue. For time calculation and decision making we have used rule engine. All Rules have been defined in application.yml. All line codes should have corresponding rules in application.yml.

1. Node in between has a future opening date.
    ```
    Consider DT line to: A --> B --> C --> D, 
    where A,B,C,D are nodes with C having opening date on Jan 01, 2100.
    Consider train takes 10 minutes per stop.
    ```
   ######Case I:
   Travel Path for user U1 from A to D, at Dec 31, 2099 23:55 Hrs should be

   1. A (Dec 31, 2099 23:55) -> B (Jan 01, 2100 00:05)
   2. B (Jan 01, 2100 00:05) -> C (Jan 01, 2100 00:15) as C is open while user is on node B on Jan 01, 00:05.
   3. C (Jan 01, 2100 00:15) -> D (Jan 01, 2100 00:25).

   ######Case II:
   Travel Path for user U2 from A to D, at Dec 31, 2099 23:30 Hrs should be

   1. A (Dec 31, 2099 23:30) -> B (Dec 31, 2099 23:40)
   2. B (Dec 31, 2099 23:40) -> D (Dec 31, 2099 23:50) as C is closed while user is on node B on Dec 31, 23:40.

   ######Case III:
   Travel Path for user U1 from A to D, at Dec 31, 2099 23:45 Hrs should be

   1. A (Dec 31, 2099 23:45) -> B (Dec 31, 2099 23:55)
   2. B (Dec 31, 2099 23:55) -> D (Jan 01, 2100 00:05) as C is closed while user is on node B on Dec 31, 23:55.


2. Travel time calculation during Peak / Night hour:
    ```
    Consider DT line to: A --> B --> C --> D --> E, 
    where A,B,C,D are nodes. All nodes are operational.
    Train takes 10 minutes per stop.
    Train takes 15 minutes per stop during peak hours.
    Peak hour for DT line from 18:00 hrs to 18:30 hrs
    ```
   ######Normal Case:
   Travel Path for user U1 from A to E, at Jan 01, 2021 12:55 Hrs should be

   1. A (Jan 01, 2021 12:55) -> B (Jan 01, 2021 13:05)
   2. B (Jan 01, 2021 13:05) -> C (Jan 01, 2021 13:15)
   3. C (Jan 01, 2021 13:15) -> D (Jan 01, 2021 13:25)
   4. D (Jan 01, 2021 13:25) -> E (Jan 01, 2021 13:35)
   ###### Total Travel Time in Normal Case -> 40 minutes

   ######Hybrid Case:
   Travel Path for user U2 from A to E, Jan 01, 2021 17:55 Hrs should be

   1. A (Jan 01, 2021 17:55) -> B (Jan 01, 2021 18:05) (Travel time between A and B is 10 min, as user starts from A to B on 17:55 which is non-peak hours.)
   2. B (Jan 01, 2021 18:05) -> C (Jan 01, 2021 18:20) (Travel time between B and C is 15 min, as user starts from B to C at 18:05 which is peak hours.)
   3. C (Jan 01, 2021 18:20) -> D (Jan 01, 2021 18:35) (Travel time between C and D is 15 min, as user starts from C to D at 18:20 which is peak hours.)
   4. D (Jan 01, 2021 18:35) -> E (Jan 01, 2021 18:45) (Travel time between D and E is 10 min, as user starts from D to E at 18:35 which is non-peak hours.)
   ###### Total Travel Time in Hybrid Case -> 50 minutes

3. Peak / Night hour calculation with LineChange case, consider the following case:
    ```
    DT line => A --> B --> C --> D
    DT line travel time in non-peak hours = 8 min and 12 min in peak hours
    DT line change wait time in non-peak hours = 5 min and 7 min in peak hours
    
    SW line => P --> B --> Q --> S
    SW line travel time in non-peak hours = 9 min and 13 min in peak hours
    SW line change wait time in non-peak hours = 11 min and 14 min in peak hours
   
    DC line => X --> Y --> Q --> Z
    DC line travel time in non-peak hours = 1 min and 2 min in peak hours
    DC line change wait time in non-peak hours = 18 min and 21 min in peak hours
   
    All lines have peak-hour of 18:00 to 18:30.
    ```
   Travel Path for user U3 from A to Z, Jan 01, 2021 17:45 Hrs should be

   1. On DT line, A (Jan 01, 2021 17:45) -> B (Jan 01, 2021 17:53)

      (Travel time between A and B is 8 min, as user starts from A to B on 17:45 which is non-peak hours on DT line.)
   2. Change line from DT to SW

      (Wait time from Jan 01, 2021 17:53 to Jan 01, 2021 18:04)

      (Line change wait time at SW is 11 min, as user changes line to SW on 17:53 which is non-peak hours.)
   3. On SW line, B (Jan 01, 2021 18:04) -> Q (Jan 01, 2021 18:17)

      (Travel time between B and Q on SW line is 13 min, as user starts from B on SW line to Q at 18:04 which is peak hours.)
   4. Change line SW to DC.

      (Wait time from Jan 01, 2021 18:17 to Jan 01, 2021 18:38)

      (Line change wait time at DC is 21 min, as user changes line to DC on 18:17 which is peak hours.)
   5. On DC line, Q (Jan 01, 2021 18:38) -> Z (Jan 01, 2021 18:39)

      (Travel time between Q and Z on DC line is 1 min, as user starts from Q on DC line to Z at 18:38 which is non-peak hours.)
   ###### Total Travel Time -> 54 minutes


4. Total Nodes travelled count logic.
    ```
    Consider DT line to: A --> B --> C --> D, 
    where A,B,C,D are nodes with all nodes open.
    Consider train takes 10 minutes per stop.
    ```
   ######Number of nodes travelled by user U1 from A to D, at Jan 01, 2021 12:55 Hrs

   1. A (Jan 01, 2021 12:55) -> B (Jan 01, 2121 13:05) [Total Nodes travelled = 1 (B)] [10 minutes]
   2. B (Jan 01, 2021 13:05) -> C (Jan 01, 2121 13:15) [Total Nodes travelled = 2 (B, C)] [10 minutes]
   3. C (Jan 01, 2021 13:15) -> D (Jan 01, 2021 13:25) [Total Nodes travelled = 3 (B, C, D)] [10 minutes]
   ##### In above example, even if user started at Node A, but actual node he travelled is 3 (B, C, D).
   ###### Hence,
   ######  1. Path Travelled: A -> B -> C -> D
   ######  2. Total Node in Path: 3
   ######  3. Total travel time in minutes: 30

# Business APIs / Curls:
API to fetch path between source and destination:
```
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:9090/search/path/{sourceStationCode}/{destinationStationCode}?travelTime=yyyy-MM-ddTHH:MM
```
######Notes:
1. sourceStationCode and destinationStationCode are mandatory. Sample StationCode - EW20
2. Query param travel time is optional. If not provided API would calculate path for current time.
3. While specifying travelTime query param follow format as shown in example / curl.

Example:
```
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:9090/search/path/CC21/DT14?travelTime=2021-06-07T10:00
```

# Sample Response of Business API
1. API could generate different response depending upon time for which path has been calculated.
2. Success Response of API would look like, you could format response string to look like as shown in step 5 by replacing `\n` with new line in notepad.
   ```
   {
    "success": true,
     "body": { 
        "response":"### Fastest Route from Holland Village to Bugis. (Time based)\n......."
     },
     "errors":null
   }
   ```
3. If no path exist between source and destination, response would look like:
   ```
    {
        "success": false,
         "body":null,
         "errors":[
            {
                "message":"No way exist between selected source and destination. It could be due to station / lines are non-operational for selected time.",
                "code":"1601",
                "category":"NO_PATH_AVAILABLE_BETWEEN_SOURCE_AND_DESTINATION"
            }
         ]
    }
    ```
4. If case of error, response would look like:
   ```
    {
        "success": false,
         "body":null,
         "errors":[
            {
                "message":"Requested source or destination does not exist.",
                "code":"1301",
                "category":"INVALID_REQUEST_PARAMETER"
            }
         ]
    }
    ```
5. Every API returns 2 paths, one shortestPath based on time other shortest path based on station travelled, below is two different response based of time of travel from Holland Village to Bugis.
######5.1 Eg. Travel from Holland Village (CC21) to Bugis (DT14) during normal hours.
```
### Fastest Route from Holland Village to Bugis. (Time based)
Journey Start Time: 2021-06-07T10:00
Journey End Time: 2021-06-07T11:10
Total Travel Time (In Minutes): 70
Total Stations in Journey: 7

Stations Travelled: [Holland Village, Farrer Road, Botanic Gardens, Stevens, Newton, Little India, Rochor, Bugis]

Details: 
Take CC line from Holland Village to Farrer Road [ TimeOfVisit: 2021-06-07T10:10, Travelled stations so far: 1, JourneyTime in Minutes: 10 ]
Take CC line from Farrer Road to Botanic Gardens [ TimeOfVisit: 2021-06-07T10:20, Travelled stations so far: 2, JourneyTime in Minutes: 20 ]
Change from CC line to DT line
Take DT line from Botanic Gardens to Stevens [ TimeOfVisit: 2021-06-07T10:38, Travelled stations so far: 3, JourneyTime in Minutes: 38 ]
Take DT line from Stevens to Newton [ TimeOfVisit: 2021-06-07T10:46, Travelled stations so far: 4, JourneyTime in Minutes: 46 ]
Take DT line from Newton to Little India [ TimeOfVisit: 2021-06-07T10:54, Travelled stations so far: 5, JourneyTime in Minutes: 54 ]
Take DT line from Little India to Rochor [ TimeOfVisit: 2021-06-07T11:02, Travelled stations so far: 6, JourneyTime in Minutes: 62 ]
Take DT line from Rochor to Bugis [ TimeOfVisit: 2021-06-07T11:10, Travelled stations so far: 7, JourneyTime in Minutes: 70 ]


### Shortest Route from Holland Village to Bugis. (Based on station travelled)
Journey Start Time: 2021-06-07T10:00
Journey End Time: 2021-06-07T11:10
Total Travel Time (In Minutes): 70
Total Stations in Journey: 7

Stations Travelled: [Holland Village, Farrer Road, Botanic Gardens, Stevens, Newton, Little India, Rochor, Bugis]

Details: 
Take CC line from Holland Village to Farrer Road [ TimeOfVisit: 2021-06-07T10:10, Travelled stations so far: 1, JourneyTime in Minutes: 10 ]
Take CC line from Farrer Road to Botanic Gardens [ TimeOfVisit: 2021-06-07T10:20, Travelled stations so far: 2, JourneyTime in Minutes: 20 ]
Change from CC line to DT line
Take DT line from Botanic Gardens to Stevens [ TimeOfVisit: 2021-06-07T10:38, Travelled stations so far: 3, JourneyTime in Minutes: 38 ]
Take DT line from Stevens to Newton [ TimeOfVisit: 2021-06-07T10:46, Travelled stations so far: 4, JourneyTime in Minutes: 46 ]
Take DT line from Newton to Little India [ TimeOfVisit: 2021-06-07T10:54, Travelled stations so far: 5, JourneyTime in Minutes: 54 ]
Take DT line from Little India to Rochor [ TimeOfVisit: 2021-06-07T11:02, Travelled stations so far: 6, JourneyTime in Minutes: 62 ]
Take DT line from Rochor to Bugis [ TimeOfVisit: 2021-06-07T11:10, Travelled stations so far: 7, JourneyTime in Minutes: 70 ]
```

######5.2. Eg. Travel from Holland Village (CC21) to Bugis (DT14) during peak hours.
```
### Fastest Route from Holland Village to Bugis. (Time based)
Journey Start Time: 2021-06-07T17:55
Journey End Time: 2021-06-07T19:20
Total Travel Time (In Minutes): 85
Total Stations in Journey: 7

Stations Travelled: [Holland Village, Farrer Road, Botanic Gardens, Stevens, Newton, Little India, Rochor, Bugis]

Details: 
Take CC line from Holland Village to Farrer Road [ TimeOfVisit: 2021-06-07T18:05, Travelled stations so far: 1, JourneyTime in Minutes: 10 ]
Take CC line from Farrer Road to Botanic Gardens [ TimeOfVisit: 2021-06-07T18:15, Travelled stations so far: 2, JourneyTime in Minutes: 20 ]
Change from CC line to DT line
Take DT line from Botanic Gardens to Stevens [ TimeOfVisit: 2021-06-07T18:40, Travelled stations so far: 3, JourneyTime in Minutes: 45 ]
Take DT line from Stevens to Newton [ TimeOfVisit: 2021-06-07T18:50, Travelled stations so far: 4, JourneyTime in Minutes: 55 ]
Take DT line from Newton to Little India [ TimeOfVisit: 2021-06-07T19:00, Travelled stations so far: 5, JourneyTime in Minutes: 65 ]
Take DT line from Little India to Rochor [ TimeOfVisit: 2021-06-07T19:10, Travelled stations so far: 6, JourneyTime in Minutes: 75 ]
Take DT line from Rochor to Bugis [ TimeOfVisit: 2021-06-07T19:20, Travelled stations so far: 7, JourneyTime in Minutes: 85 ]


### Shortest Route from Holland Village to Bugis. (Based on station travelled)
Journey Start Time: 2021-06-07T17:55
Journey End Time: 2021-06-07T19:20
Total Travel Time (In Minutes): 85
Total Stations in Journey: 7

Stations Travelled: [Holland Village, Farrer Road, Botanic Gardens, Stevens, Newton, Little India, Rochor, Bugis]

Details: 
Take CC line from Holland Village to Farrer Road [ TimeOfVisit: 2021-06-07T18:05, Travelled stations so far: 1, JourneyTime in Minutes: 10 ]
Take CC line from Farrer Road to Botanic Gardens [ TimeOfVisit: 2021-06-07T18:15, Travelled stations so far: 2, JourneyTime in Minutes: 20 ]
Change from CC line to DT line
Take DT line from Botanic Gardens to Stevens [ TimeOfVisit: 2021-06-07T18:40, Travelled stations so far: 3, JourneyTime in Minutes: 45 ]
Take DT line from Stevens to Newton [ TimeOfVisit: 2021-06-07T18:50, Travelled stations so far: 4, JourneyTime in Minutes: 55 ]
Take DT line from Newton to Little India [ TimeOfVisit: 2021-06-07T19:00, Travelled stations so far: 5, JourneyTime in Minutes: 65 ]
Take DT line from Little India to Rochor [ TimeOfVisit: 2021-06-07T19:10, Travelled stations so far: 6, JourneyTime in Minutes: 75 ]
Take DT line from Rochor to Bugis [ TimeOfVisit: 2021-06-07T19:20, Travelled stations so far: 7, JourneyTime in Minutes: 85 ]
```

######5.3. Eg. Travel from Holland Village (CC21) to Bugis (DT14) during night hours.
```
### Fastest Route from Holland Village to Bugis. (Time based)
Journey Start Time: 2021-06-07T21:45
Journey End Time: 2021-06-07T23:35
Total Travel Time (In Minutes): 110
Total Stations in Journey: 10

Stations Travelled: [Holland Village, Buona Vista, Commonwealth, Queenstown, Redhill, Tiong Bahru, Outram Park, Tanjong Pagar, Raffles Place, City Hall, Bugis]

Details: 
Take CC line from Holland Village to Buona Vista [ TimeOfVisit: 2021-06-07T21:55, Travelled stations so far: 1, JourneyTime in Minutes: 10 ]
Change from CC line to EW line
Take EW line from Buona Vista to Commonwealth [ TimeOfVisit: 2021-06-07T22:15, Travelled stations so far: 2, JourneyTime in Minutes: 30 ]
Take EW line from Commonwealth to Queenstown [ TimeOfVisit: 2021-06-07T22:25, Travelled stations so far: 3, JourneyTime in Minutes: 40 ]
Take EW line from Queenstown to Redhill [ TimeOfVisit: 2021-06-07T22:35, Travelled stations so far: 4, JourneyTime in Minutes: 50 ]
Take EW line from Redhill to Tiong Bahru [ TimeOfVisit: 2021-06-07T22:45, Travelled stations so far: 5, JourneyTime in Minutes: 60 ]
Take EW line from Tiong Bahru to Outram Park [ TimeOfVisit: 2021-06-07T22:55, Travelled stations so far: 6, JourneyTime in Minutes: 70 ]
Take EW line from Outram Park to Tanjong Pagar [ TimeOfVisit: 2021-06-07T23:05, Travelled stations so far: 7, JourneyTime in Minutes: 80 ]
Take EW line from Tanjong Pagar to Raffles Place [ TimeOfVisit: 2021-06-07T23:15, Travelled stations so far: 8, JourneyTime in Minutes: 90 ]
Take EW line from Raffles Place to City Hall [ TimeOfVisit: 2021-06-07T23:25, Travelled stations so far: 9, JourneyTime in Minutes: 100 ]
Take EW line from City Hall to Bugis [ TimeOfVisit: 2021-06-07T23:35, Travelled stations so far: 10, JourneyTime in Minutes: 110 ]


### Shortest Route from Holland Village to Bugis. (Based on station travelled)
Journey Start Time: 2021-06-07T21:45
Journey End Time: 2021-06-07T23:35
Total Travel Time (In Minutes): 110
Total Stations in Journey: 10

Stations Travelled: [Holland Village, Buona Vista, Commonwealth, Queenstown, Redhill, Tiong Bahru, Outram Park, Tanjong Pagar, Raffles Place, City Hall, Bugis]

Details: 
Take CC line from Holland Village to Buona Vista [ TimeOfVisit: 2021-06-07T21:55, Travelled stations so far: 1, JourneyTime in Minutes: 10 ]
Change from CC line to EW line
Take EW line from Buona Vista to Commonwealth [ TimeOfVisit: 2021-06-07T22:15, Travelled stations so far: 2, JourneyTime in Minutes: 30 ]
Take EW line from Commonwealth to Queenstown [ TimeOfVisit: 2021-06-07T22:25, Travelled stations so far: 3, JourneyTime in Minutes: 40 ]
Take EW line from Queenstown to Redhill [ TimeOfVisit: 2021-06-07T22:35, Travelled stations so far: 4, JourneyTime in Minutes: 50 ]
Take EW line from Redhill to Tiong Bahru [ TimeOfVisit: 2021-06-07T22:45, Travelled stations so far: 5, JourneyTime in Minutes: 60 ]
Take EW line from Tiong Bahru to Outram Park [ TimeOfVisit: 2021-06-07T22:55, Travelled stations so far: 6, JourneyTime in Minutes: 70 ]
Take EW line from Outram Park to Tanjong Pagar [ TimeOfVisit: 2021-06-07T23:05, Travelled stations so far: 7, JourneyTime in Minutes: 80 ]
Take EW line from Tanjong Pagar to Raffles Place [ TimeOfVisit: 2021-06-07T23:15, Travelled stations so far: 8, JourneyTime in Minutes: 90 ]
Take EW line from Raffles Place to City Hall [ TimeOfVisit: 2021-06-07T23:25, Travelled stations so far: 9, JourneyTime in Minutes: 100 ]
Take EW line from City Hall to Bugis [ TimeOfVisit: 2021-06-07T23:35, Travelled stations so far: 10, JourneyTime in Minutes: 110 ]
```




#Pending Work:

### Performance enhancements:
    1. Currently, getPaths is taking ~38 ms to response. It could be reduced to less then 10 ms.
        1.1. PathSearchServiceImpl is calculating shortestPath via Time, Distance in serialized order. 
             If we concurrently calculate it, it will improve our API response time by factor of half (i.e. less then 20ms)
        1.2. We will replace PriorityQueue with Treeset for Dijkstra shortest path search. 
             It will further improve our API response time by factor of half (i.e. from 20 ms to less then 12ms)
        1.3. Response generation is taking 1 ms, should be optimized.
### Technical enhancements:
    1. Provide logback.xml with proper logging facilities.
    2. Dockerize the application.
    3. Migrate from CSV data source to actual data-source.
    4. Move configuration to DB, so that config could be updated real time via Admin APIs.
    5. Add performance tracing metrics.

### Business enhancements:
Update travel time calculation logic, based on station number. Missing station numbers should also be added to travel time.
###### Current Implementation:
```
EW01 --> EW02 --> EW05 --> EW06
```
Considering all travel between station is 10 minutes.

TravelTime from EW01 to EW06 = 30 minutes

1. EW01 -> EW02 (10 minutes)
2. EW02 -> EW05 (10 minutes)
3. EW05 -> EW06 (10 minutes)

###### New Implementation:
```
EW01 --> EW02 --> EW05 --> EW06
```
Considering all travel between station is 10 minutes.

TravelTime from EW01 to EW06 = 50 minutes

1. EW01 -> EW02 (10 minutes)


2. EW02 -> EW05 (30 minutes)

   (
   Explanation:
   EW02 --> EW03  10 minutes
   EW03 --> EW04  10 Minutes
   EW04 --> EW05  10 Minutes
   )

   Even if Station EW03, EW04 do not exist. They will come up in the future.


3. EW05 -> EW06 (10 minutes)





# How to start the application?
###Notes:
1. Instructions / guidelines mentioned below is as per Mac OS. Steps should be executable on other OS, but syntax may differ.
2. Default application port is 9090.

### 1. Using Terminal
    1.1. Prerequisites:
         Make sure you have following sofware /tools installed on your system:
            1.1.1. Java JDK (Recommended version: JDK 11.0.11)
            1.1.2. Maven (Recommended version: 3.8.1)

    1.2. Go to project directory/workspace. Project directory path would be something like: 
    `/Users/<YOUR_SYSTEM_PATH_HERE>/SubwayTransition`

    1.3. In the project directory, execute following commands:
        1.3.1. `mvn clean compile`
        1.3.2. `mvn package`

    1.4. To run the project use command in same directory / workspace:
        1.4.1. `java -jar target/SubwayTransition-0.0.1-SNAPSHOT.jar`

    1.5. To verify application has successfully started, 
        1.5.1. Open browser, and go to `http://localhost:9090/healthCheck`
               OR use CURL via terminal: `curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:9090/healthCheck` 
        1.5.2. You should see response as `{"success":true,"body":"healthCheck successful.","errors":null}`

### 2. Using IntelliJ IDEA
    2.1. Prerequisites:
         Make sure you have IntelliJ to import / open maven based Spring-Boot web projects.
         Make sure you have JDK and Maven plugins installed or you could manually install JDF and Maven.   

    2.2. Once you import the project in IntelliJ, follow the below mentioned steps:
        2.2.1. Go to SubwayTransitionApplication.java file, and Run SubwayTransitionApplication class. 
               (SubwayTransitionApplication is entry point for application)

    2.3. To verify application has successfully started, 
        2.3.1. Open browser, and go to `http://localhost:9090/healthCheck`
               OR use CURL via terminal: `curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:9090/healthCheck` 
        2.3.2. You should see response as `{"success":true,"body":"healthCheck successful.","errors":null}`

# How to run test?
Use command `mvn test` in terminal to run all test cases.

# Trouble-shooting:

## Port 9090 already in use, how to change port?
    In application.properties file change config server.port to an open port. 

    Path to application file would be something like: 
    /Users/<YOUR_SYSTEM_PATH_HERE>/SubwayTransition/src/main/resources
