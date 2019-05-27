package at.fh.swenga.controller;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.model.FlightModel;
import at.fh.swenga.model.FlightService;

@Controller
public class FlightManagerController {

	@Autowired
	private FlightService flightService;

	@RequestMapping(value = { "/", "listFlights" })
	public String showAllFlights(Model model) {
		model.addAttribute("flights", flightService.getAllFlights());
		return "listFlights";
	}

	@RequestMapping("/fillFlightList")
	public String fillFlightList(Model model) {

		Date now = new Date();
		flightService.addFlight(new FlightModel(flightService.getSize() + 1, "Boeing 737", "Germany", "Spanien", now, now, 148, "Lufthansa", false));
		flightService.addFlight(new FlightModel(flightService.getSize() + 1, "Boeing 767", "Austria", "Belgium", now, now, 72, "Austrian", true));
		flightService.addFlight(new FlightModel(flightService.getSize() + 1, "Boeing 777", "France", "Portugal", now, now, 65, "Eurowings", false));
		flightService.addFlight(new FlightModel(flightService.getSize() + 1, "Airbus A380", "Germany", "Thailand", now, now, 225, "Emirates", false));
		flightService.addFlight(new FlightModel(flightService.getSize() + 1, "Boeing 767", "Switzerland", "Germany", now, now, 103, "Swiss", false));
		flightService.addFlight(new FlightModel(flightService.getSize() + 1, "Airbus A320", "Spain", "Portugal", now, now, 62, "Iberia", true));
		flightService.addFlight(new FlightModel(flightService.getSize() + 1, "Boeing 747", "France", "Sweden", now, now, 98, "Norwegian", false));
		flightService.addFlight(new FlightModel(flightService.getSize() + 1, "Boeing 737", "Sweden", "Spain", now, now, 46, "Iberia", false));

		model.addAttribute("flights", flightService.getAllFlights());
		return "listFlights";
	}

	// Spring 4: @RequestMapping(value = "/deleteFlight", method = RequestMethod.GET)
	@GetMapping("/deleteFlight")
	public String delete(Model model, @RequestParam int flightId) {
		boolean isRemoved = flightService.remove(flightId);

		if (isRemoved) {
			model.addAttribute("warningMessage", "Flight " + flightId + " deleted");
		} else {
			model.addAttribute("errorMessage", "There is no flight " + flightId);
		}

		// Multiple ways to "forward"
		// return "forward:/listFlights";
		return showAllFlights(model);
	}

	// Spring 4: @RequestMapping(value = "/searchFlights", method = RequestMethod.POST)
	@PostMapping("/searchFlights")
	public String search(Model model, @RequestParam String searchString) {
		model.addAttribute("flights", flightService.getFilteredFlights(searchString));
		return "listFlights";
	}

	// Spring 4: @RequestMapping(value = "/addFlight", method = RequestMethod.GET)
	@GetMapping("/addFlight")
	public String showAddFlightForm(Model model) {
		return "editFlight";
	}

	// Spring 4: @RequestMapping(value = "/addFlight", method = RequestMethod.POST)
	@PostMapping("/addFlight")
	public String addFlight(@Valid FlightModel newFlightModel, BindingResult bindingResult, Model model) {

		// Any errors? -> Create a String out of all errors and return to the page
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid: " + fieldError.getCode() + "<br>";
			}
			model.addAttribute("errorMessage", errorMessage);

			// Multiple ways to "forward"
			return "forward:/listFlights";
		}

		// Look for flight in the List. One available -> Error
		FlightModel flight = flightService.getFlightByFlightId(newFlightModel.getFlightId());

		if (flight != null) {
			model.addAttribute("errorMessage", "Flight already exists!<br>");
		} else {
			flightService.addFlight(newFlightModel);
			model.addAttribute("message", "New flight " + newFlightModel.getFlightId() + " added.");
		}

		return "forward:/listFlights";
	}

	// Spring 4: @RequestMapping(value = "/editFlight", method = RequestMethod.GET)
	@GetMapping("/editFlight")
	public String showChangeFlightForm(Model model, @RequestParam int flightId) {

		FlightModel flight = flightService.getFlightByFlightId(flightId);

		if (flight != null) {
			model.addAttribute("flight", flight);
			return "editFlight";
		} else {
			model.addAttribute("errorMessage", "Couldn't find flight " + flightId);
			return "forward:/listFlights";
		}
	}

	// Spring 4: @RequestMapping(value = "/editFlight", method = RequestMethod.POST)
	@PostMapping("/editFlight")
	public String editFlight(@Valid FlightModel changedFlightModel, BindingResult bindingResult, Model model) {

		// Any errors? -> Create a String out of all errors and return to the page
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid: " + fieldError.getCode() + "<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return "forward:/listFlights";
		}

		// Get the flight we want to change
		FlightModel flight = flightService.getFlightByFlightId(changedFlightModel.getFlightId());

		if (flight == null) {
			model.addAttribute("errorMessage", "Flight does not exist!<br>");
		} else {
			// Change the attributes
			flight.setFlightId(changedFlightModel.getFlightId());
			flight.setAircraft(changedFlightModel.getAircraft());
			flight.setOrigin(changedFlightModel.getOrigin());
			flight.setDestination(changedFlightModel.getDestination());
			flight.setDeparture(changedFlightModel.getDeparture());
			flight.setArrival(changedFlightModel.getArrival());
			flight.setNumberOfPassengers(changedFlightModel.getNumberOfPassengers());
			flight.setAirline(changedFlightModel.getAirline());
			flight.setCancelled(changedFlightModel.getCancelled());

			// Save a message for the web page
			model.addAttribute("message", "Changed flight " + changedFlightModel.getFlightId());
		}

		return "forward:/listFlights";
	}

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		return "error";
	}
}
