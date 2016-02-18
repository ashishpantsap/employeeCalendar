/**
 *
 */
package com.hybris.employeecalendar.controllers;

import de.hybris.platform.servicelayer.model.ModelService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hybris.employeecalendar.data.DateRangeDto;
import com.hybris.employeecalendar.data.EventDto;
import com.hybris.employeecalendar.data.MessageDto;
import com.hybris.employeecalendar.data.enums.Alerts;
import com.hybris.employeecalendar.enums.EventType;
import com.hybris.employeecalendar.model.SapEmployeeModel;
import com.hybris.employeecalendar.services.CalendarEventService;
import com.hybris.employeecalendar.services.CalendarValidationService;
import com.hybris.employeecalendar.services.SAPEmployeeService;
import com.hybris.employeecalendar.util.HelperUtil;


@Controller
public class EventController
{

	private static final Logger LOG = Logger.getLogger(EventController.class.getName());

	private CalendarEventService calendarEventService;

	private SAPEmployeeService sapEmployeeService;

	private CalendarValidationService calendarValidationService;

	private ModelService modelService;

	@Autowired
	public void setCalendarEventService(final CalendarEventService calendarEventService)
	{
		this.calendarEventService = calendarEventService;
	}

	@Autowired
	public void setValidationService(final CalendarValidationService calendarValidationService)
	{
		this.calendarValidationService = calendarValidationService;
	}

	@Autowired
	public void setSapEmployeeService(final SAPEmployeeService sapEmployeeService)
	{
		this.sapEmployeeService = sapEmployeeService;
	}

	@Autowired
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	//Parse the dates, validate the inputs and save the events
	@ResponseBody
	@RequestMapping(value = "/sendevents", method = RequestMethod.POST, headers = "Accept=application/json")
	public MessageDto sendEvents(final Model model, @RequestParam(value = "pk") final String pk,
			@RequestParam(value = "dates") final String[] dates,
			@RequestParam(value = "description", required = false) final String description,
			@RequestParam(value = "training-time", required = false) final String trainingTime,
			@RequestParam(value = "ooo-type", required = false) final String oooType,
			@RequestParam(value = "typeevent") final String typeevent)
	{
		MessageDto msave = null;
		List<Date> validDates = null;
		try
		{
			final List<EventDto> events = new ArrayList<>();
			DateRangeDto dateRange = null;
			validDates = HelperUtil.parseStringsToDate(dates, EventType.valueOf(typeevent));
			msave = calendarValidationService.validateInputData(pk, validDates, typeevent);
			final SapEmployeeModel employee = sapEmployeeService.getSapEmployeeByPK(pk);
			if (msave != null)
			{
				return msave;
			}
			for (final Date date : validDates)
			{
				dateRange = HelperUtil.getDateRangeOfTheDay(date, EventType.valueOf(typeevent));
				final EventDto event = new EventDto();
				event.setFromDate(dateRange.getFromDate());
				event.setToDate(dateRange.getToDate());
				event.setDescription(description == null ? "" : description);
				event.setType(typeevent);
				event.setTrainingTime(trainingTime);
				event.setOooType(oooType);
				//fixing date with time ADDED in the service redudant
				//event = HelperUtil.getDateRangeFromEventType(event);
				events.add(event);
			}
			calendarEventService.saveEventsOnCalendar(events, employee);

		}
		catch (final ParseException e)
		{
			LOG.debug("error parsing date");
			msave = HelperUtil.createMessage(e.getMessage(), Alerts.DANGER);
		}
		catch (final Exception ex)
		{
			msave = HelperUtil.createMessage(ex.getMessage(), Alerts.DANGER);
		}
		if (msave == null)
		{
			msave = HelperUtil.createMessage("Event saved successfully for " + Arrays.toString(dates), Alerts.SUCCESS);
		}

		model.addAttribute("message", msave);

		return msave;
	}

	@RequestMapping(value = "/sapevents", method = RequestMethod.GET)
	@ResponseBody
	public List<String> sapEvents()
	{
		final List<String> events = new ArrayList<String>();
		for (final EventType event : EventType.values())
		{
			events.add(event.getCode());
		}
		return events;
	}

	@RequestMapping(value = "/daysevents", method = RequestMethod.GET)
	@ResponseBody
	public List<EventDto> daysEvents(@RequestParam(value = "date") final Date date)
	{
		List<EventDto> events = new ArrayList<EventDto>();
		try
		{
			events = calendarEventService.getAllEventsForDay(date);
		}
		catch (final ParseException e)
		{
			LOG.info("Could Not Retrieve Events For Today");
		}

		return events;
	}
}
