package com.blueming.myrest.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;

// ResourceSupport is now RepresentationModel
public class EventResource extends RepresentationModel {

    // event 이름의 Json으로 묶지 말고 펼치기
    @JsonUnwrapped
    private Event event;
    public EventResource(Event event) {
        this.event = event;
    }
    public Event getEvent() {
        return event;
    }

}

// Resource is now EntityModel
// 이제 이런 식으로 안 쓰는 듯..
//public class EventResource extends EntityModel<Event> {
//
//    public EventResource(Event event, Link... links) {
//        super(event, links);
//        add(linkTo(EventController.class).slash(event.getId()).withRel("query-events"));
//        EntityModel<Event> eventModel = new EntityModel<>(event);
//        eventModel.(linkTo(EventController.class).slash(event.getId()).withRel("query-events"));
//    }
//
//}
