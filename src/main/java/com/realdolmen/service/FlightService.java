package com.realdolmen.service;

import java.util.List;

import javax.ejb.Remote;

import com.realdolmen.domain.Flight;
import com.realdolmen.exception.ConcurrentUpdateException;

@Remote
public interface FlightService {

	List<Flight> findAllFlightsByPartnerId(long id);

	Flight findFlightById(long id);

	Flight update(Flight flight) throws ConcurrentUpdateException;

}
