package com.blueming.myrest.events;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface EventRepository extends PagingAndSortingRepository<Event, Integer> {
    //name으로 조회하는 finder method
    Optional<Event> findByName(String name);
}
