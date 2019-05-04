package at.fh.swenga.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;

@Repository
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class FlightService {
	private List<FlightModel> flights = new ArrayList<FlightModel>();

	/**
	 * Add flight to List
	 * 
	 * @param flight
	 */
	public void addFlight(FlightModel flight) {
		flights.add(flight);
	}

	/**
	 * Verify if list contains flight with same flightId
	 * 
	 * @param flight
	 * @return
	 */
	public boolean contains(FlightModel flight) {
		return flights.contains(flight);
	}

	/**
	 * convenient method: true if list is empty
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return flights.isEmpty();
	}

	/**
	 * try to find FlightModel with given flightId return model, otherwise null
	 * 
	 * @param flightId
	 * @return
	 */
	public FlightModel getFlightByFlightId(int flightId) {
		for (FlightModel FlightModel : flights) {
			if (FlightModel.getFlightId() == flightId) {
				return FlightModel;
			}
		}
		return null;
	}

	/**
	 * return size of list
	 * 
	 * @return
	 */
	public int getSize() {
		return flights.size();
	}

	/**
	 * return list with all flights
	 * 
	 * @return
	 */
	public List<FlightModel> getAllFlights() {
		return flights;
	}

	/**
	 * return a new list with all flights where aircraft, origin, destination or
	 * airline contains search string
	 * 
	 * @param searchString
	 * @return
	 */
	public List<FlightModel> getFilteredFlights(String searchString) {

		if (searchString == null || searchString.equals("")) {
			return flights;
		}

		// new list for results
		List<FlightModel> filteredList = new ArrayList<FlightModel>();

		// check every flight
		for (FlightModel FlightModel : flights) {

			if ((FlightModel.getAircraft() != null && FlightModel.getAircraft().contains(searchString))
					|| (FlightModel.getOrigin() != null && FlightModel.getOrigin().contains(searchString))
					|| (FlightModel.getDestination() != null && FlightModel.getDestination().contains(searchString))
					|| (FlightModel.getAirline() != null && FlightModel.getAirline().contains(searchString))) {
				filteredList.add(FlightModel);
			}
		}
		return filteredList;
	}

	/**
	 * remove flights with same flightId
	 * 
	 * @param flightId
	 * @return
	 */
	public boolean remove(int flightId) {
		return flights.remove(new FlightModel(flightId, null, null, null, null, null, 0, null, false));
	}
}
