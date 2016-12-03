package org.cpen321.discovr;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.cpen321.discovr.model.Building;
import org.cpen321.discovr.model.Course;
import org.cpen321.discovr.model.EventInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by lerir on 2016-11-21.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class SQLiteDBHandlerTest {

    int[] eventID = {0, 1, 2, 3, 4, 5};
    String[] eventName = {"Event 1", "Event 2", "Event 3", "Event 4", "Event 5", "Event 6"};
    String[] hostName = {"Host 1", "Host 2", "Host 3", "Host 4", "Host 5", "Host 6"};
    String[] loc = {"Loc 1", "Loc 2", "Loc 3", "Loc 4", "Loc 5", "Loc 6"};
    String[] startTime = {"2016-11-21T08:00:00", "2016-11-21T09:00:00", "2016-11-22T10:00:00", "2016-11-22T11:00:00", "2016-11-24T12:00:00", "2016-11-21T12:00:00"};
    String[] endTime = {"2016-11-21T09:00:00", "2016-11-22T10:00:00", "2016-11-22T11:00:00", "2016-11-25T12:00:00", "2016-11-24T13:00:00", "2016-11-26T12:00:00"};
    String[] description = {"Description for event 1", "Description for event 2", "Description for event 3", "Description for event 4", "Description for event 5", "Description for event 6"};

    int courseSize = 3;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    String[] courseCategory = {"MATH", "CPEN", "ELEC"};
    String[] courseNumber = {"256", "321", "201"};
    String[] courseSection = {"101", "101", "201"};
    String[] courseBuilding = {"building1", "building2", "building3"};
    String[] courseRoom = {"Room1", "Room2", "Room3"};
    Long[] courseStartTime = {Long.valueOf(1200),Long.valueOf(800) , Long.valueOf(1530)};
    Long[] courseEndTime = {Long.valueOf(1230),Long.valueOf(1000) , Long.valueOf(2000)};
    Date[] courseStartDate = {fmt.parse("2016-12-04"),fmt.parse("2016-11-20"), fmt.parse("2015-10-04")};
    Date[] courseEndDate = {fmt.parse("2016-12-26"),fmt.parse("2016-12-20"), fmt.parse("2015-11-26")};
    String[] courseDayOfWeek = {"Day1", "Day2", "Day3"};

    int buildingSize = 4;
    String[] buildingName = {"MacLeod", "Neville Scarfe", "Buchanan", "Asian Centre"};
    String[] buildingCode = {"MacLeod", "NevilleScarfe", "Buchanan", "AsianCentre"};
    String[] buildingAdrress = {"Addr1","Addr2","Addr3","Addr4"};
    String[] buildingHours = {"9:00-11:00","8:00-12:00","13:00-20:00","10:00-21:00"};
    String[] buildingCoordinates = {"-123, 321","11111, -12345", "77463, 23746", "-6444,7654"};

    SQLiteDBHandler dbh;

    List<EventInfo> events;

    List<Course> courses;

    List<Building> buildings;

    public SQLiteDBHandlerTest() throws ParseException {
    }

    @Before
    public void setUp() {
        dbh = new SQLiteDBHandler(RuntimeEnvironment.application);
        events = new ArrayList<>();

        for (int i = 0; i < eventID.length; i++) {
            events.add(new EventInfo(eventID[i],
                                    eventName[i],
                                    hostName[i],
                                    loc[i],
                                    startTime[i],
                                    endTime[i],
                                    description[i]));
        }

        courses = new ArrayList<>();
        for (int j = 0; j < courseSize; j++){
            courses.add(new Course(courseCategory[j],
                                   courseNumber[j],
                                   courseSection[j],
                                   courseBuilding[j],
                                   courseRoom[j],
                                   courseStartTime[j],
                                   courseEndTime[j],
                                   courseStartDate[j],
                                   courseEndDate[j],
                                   courseDayOfWeek[j]));
        }

        buildings = new ArrayList<>();
        for(int k = 0; k < buildingSize; k++){
            buildings.add(new Building( buildingName[k],
                                        buildingCode[k],
                                        buildingAdrress[k],
                                        buildingHours[k],
                                        buildingCoordinates[k]));
        }
    }

    @Test
    public void testExistence() {
        assertNotNull(dbh);
    }


    @Test
    public void testEventCount() {
        assertEquals(0, dbh.getEventCount());
    }

    @Test
    public void testAddingEvent() {
        assertEquals(0, dbh.getEventCount());
        dbh.addEvent(events.get(0));
        assertEquals(1, dbh.getEventCount());
    }

    @Test
    public void testGetEventByID() {
        addEventsToDatabase();
        EventInfo with_id_2 = dbh.getEvent(eventID[2]);
        assertEquals(events.get(2), with_id_2);
    }

    // TODO: Expand to search by Location and Host
    @Test
    public void testGetEventBySearch() {
        addEventsToDatabase();
        List<EventInfo> events = dbh.getEventbySearch("event");
        assertEquals(eventID.length, events.size());
        List<EventInfo> event1 = dbh.getEventbySearch(eventName[1]);
        assertEquals(events.get(1), event1.get(0));
    }

    @Test
    public void testGetAllEvents() {
        addEventsToDatabase();
        List<EventInfo> allEvents = dbh.getAllEvents();
        assertEquals(eventID.length, allEvents.size());
        for (int i = 0; i < allEvents.size(); i++) {
            assertTrue(allEvents.contains(events.get(i)));
        }
    }

    @Test
    public void getUpdateEvent() {
        addEventsToDatabase();
        EventInfo updateEvent = new EventInfo(eventID[4], eventName[4], "New Updated Host", loc[4], startTime[4], endTime[4], description[4]);
        dbh.updateEvent(updateEvent);
        List<EventInfo> allEvents = dbh.getEventbySearch(eventName[4]);
        assertEquals(1, allEvents.size());
        assertEquals(updateEvent, allEvents.get(0));
    }

    @Test
    public void testDeleteEvent() {
        addEventsToDatabase();
        dbh.deleteEvent(eventID[5]);
        assertEquals(eventID.length - 1, dbh.getEventCount());
        List<EventInfo> deletedEvent = dbh.getEventbySearch(eventName[5]);
        assertEquals(0, deletedEvent.size());
    }

    @Test
    public void testCourseCount() throws ParseException {
        assertEquals(0 , dbh.getAllCourses().size());
    }

    @Test
    public void testAddSingleCourses() throws ParseException {
        assertEquals(0, dbh.getAllCourses().size());
        dbh.addSingleCourse(courses.get(0));
        assertEquals(1, dbh.getAllCourses().size());
    }

    @Test
    public void testAddAllCourses() throws ParseException {
        assertEquals(0, dbh.getAllCourses().size());
        dbh.addCourses(courses);
        assertEquals(courses.size(), dbh.getAllCourses().size());
    }

    @Test
    public void testGetAllCourses() throws ParseException {
        assertEquals(0, dbh.getAllCourses().size());
        dbh.addCourses(courses);
        List<Course> allCourses = dbh.getAllCourses();
        assertEquals(courses.size(), allCourses.size());
        for (int i = 0; i < courseSize; i++){
            assertTrue(courses.get(i).getCategory().contains(allCourses.get(i).category));
            assertTrue(courses.get(i).getNumber().contains(allCourses.get(i).number));
            assertTrue(courses.get(i).getSection().contains(allCourses.get(i).section));
            assertTrue(courses.get(i).getBuilding().contains(allCourses.get(i).building));
            assertTrue(courses.get(i).getRoom().contains(allCourses.get(i).room));
        }
    }

    @Test
    public void testAddBuilding() {
        assertEquals(0, dbh.getAllBuildings().size());
        dbh.addBuilding(buildings.get(0));
        assertEquals(1, dbh.getAllBuildings().size());
    }

    @Test
    public void testGetBuildingByCode() {
        assertEquals(0, dbh.getAllBuildings().size());
        dbh.addBuilding(buildings.get(0));
        assertEquals(1, dbh.getAllBuildings().size());
        String code = "MacLeod";
        Building result = dbh.getBuildingByCode(code);
        assertTrue(buildings.get(0).name.contains(result.name));
        assertTrue(buildings.get(0).address.contains(result.address));
        assertTrue(buildings.get(0).hours.contains(result.hours));
        assertTrue(buildings.get(0).code.contains(result.code));
        assertTrue(buildings.get(0).getCoordinatesAsString().contains(result.getCoordinatesAsString()));
    }

    @Test
    public void testGetBuildingCount() {
        assertEquals(0, dbh.getAllBuildings().size());
        for (int i = 0; i < buildingSize; i++){
            dbh.addBuilding(buildings.get(i));
        }
        assertEquals(4, dbh.getBuildingCount());
    }

    @Test
    public void testGetBuildingByID() {
        assertEquals(0, dbh.getAllBuildings().size());
        dbh.addBuilding(buildings.get(0));
        assertEquals(1, dbh.getAllBuildings().size());
        Building result = dbh.getBuildingByID(0);
        assertTrue(buildings.get(0).name.contains(result.name));
        assertTrue(buildings.get(0).address.contains(result.address));
        assertTrue(buildings.get(0).hours.contains(result.hours));
        assertTrue(buildings.get(0).code.contains(result.code));
        assertTrue(buildings.get(0).getCoordinatesAsString().contains(result.getCoordinatesAsString()));
    }

    @Test
    public void testGetAllBuildings() {
        assertEquals(0, dbh.getAllBuildings().size());
        for (int i = 0; i < buildingSize; i++){
            dbh.addBuilding(buildings.get(i));
        }
        List<Building> allBuildings = dbh.getAllBuildings();
        assertEquals(buildingSize, allBuildings.size());
        for (int i = 0; i < buildingSize; i ++){
            assertTrue(buildings.get(i).name.contains(allBuildings.get(i).name));
            assertTrue(buildings.get(i).code.contains(allBuildings.get(i).code));
            assertTrue(buildings.get(i).address.contains(allBuildings.get(i).address));
            assertTrue(buildings.get(i).hours.contains(allBuildings.get(i).hours));
            assertTrue(buildings.get(i).getCoordinatesAsString().contains(allBuildings.get(i).getCoordinatesAsString()));
        }
    }

    public void addEventsToDatabase() {
        dbh.addEvent(events.get(0));
        dbh.addEvent(events.get(1));
        dbh.addEvent(events.get(2));
        dbh.addEvent(events.get(3));
        dbh.addEvent(events.get(4));
        dbh.addEvent(events.get(5));
    }


}