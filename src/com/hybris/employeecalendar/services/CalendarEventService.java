/**
 *
 */
package com.hybris.employeecalendar.services;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.hybris.employeecalendar.data.EventDto;
import com.hybris.employeecalendar.enums.EventType;
import com.hybris.employeecalendar.model.SapEmployeeModel;
import com.hybris.employeecalendar.model.SapEventModel;




/**
 * @author I310388
 *
 */
public interface CalendarEventService
{

	public void saveEventOnCalendar(EventDto event);

	public void saveEventOnCalendar(final EventDto event, SapEmployeeModel employee);

	public void saveEventsOnCalendar(List<EventDto> events, SapEmployeeModel employee) throws ParseException;

	public void deleteEventOnCalendar(EventDto event) throws Exception;

	public List<Date> getMonthlyEventByInumber(String iNumber);

	public List<EventDto> getAllEventsForDay(Date date) throws ParseException;

	public List<EventDto> getMonthlySchedule(Date today);

	public List<EventDto> getMonthlyScheduleFromDateToDate(Date from, Date to);

	public List<EventDto> getMonthlyScheduleOnCallAndQM(Date from, Date to);

	public List<EventDto> getReport(Date date, EventType event, String PK) throws ParseException;

	List<SapEventModel> getAllEventsInTheDay(Date date, String name, String event) throws ParseException;

	public void deleteEventsInTheDay(Date date, String PK) throws ParseException;

	public void deleteEventFromPk(String pk);
	
	public List<EventDto> getSapEventByInumberAndDate(final String iNumber, final String fromDate) throws ParseException;

}
