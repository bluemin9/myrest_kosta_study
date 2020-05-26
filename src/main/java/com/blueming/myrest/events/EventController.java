package com.blueming.myrest.events;

import com.blueming.myrest.accounts.Account;
import com.blueming.myrest.accounts.AccountAdapter;
import com.blueming.myrest.accounts.CurrentUser;
import com.blueming.myrest.index.IndexController;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Controller
@RequestMapping(value="/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors, @CurrentUser Account account) {

        // DTO에 문제가 생기면 Error에 값이 생기고 그 값이 있으면 badRequest로 전달
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        // 로직에 에러가 발생 했는지 확인
        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        // DTO에 문제가 생기면 Error에 값이 생기고 그 값이 있으면 badRequest로 전달
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Event event = modelMapper.map(eventDto, Event.class);

        event.update();

        // 현재 로그인 한 정보도 event에 넣어줌
        event.setManager(account);

        Event addEvent = eventRepository.save(event);

        // 링크를 생성해서 넘겨주며 /api/events/{Id} 로 만들어서 넘겨줌
        // 해당 값은 헤더(Location)에서 볼 수 있음
        //URI createUri = linkTo(EventController.class).slash(addEvent.getId()).toUri();
        //return ResponseEntity.created(createUri).body(addEvent);

        // ControllerLinkBuilder has been moved into server.mvc and deprecated to be replaced by WebMvcLinkBuilder
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(addEvent.getId());
        URI createUri = selfLinkBuilder.toUri();

        // 링크 추가를 위한 Resource 생성 (hateoas)
        EventResource eventResource = new EventResource(addEvent);

        // withRel 로 클래스의 링크 이름 설정
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        // self 라는 이름으로 selfBuilder의 링크 추가
        eventResource.add(selfLinkBuilder.withSelfRel());
        // selfBuilder의 링크 이름을 설정
        eventResource.add(selfLinkBuilder.withRel("update-event"));

        return ResponseEntity.created(createUri).body(eventResource);

    }

    //@Autowired
    //PagedResourcesAssembler<Event> assembler;

    @GetMapping
    public ResponseEntity getEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler,
                                    //@AuthenticationPrincipal AccountAdapter currentUser) {
                                    @CurrentUser Account currentUser) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        // PagedResources -> PagedModel
        // Resource -> EntityModel
        // toResource -> toModel
        // Hateoas 1.1.0 릴리즈에서는 에러 안뜸.. 1.0.0에서는 뜸..
        PagedModel<EntityModel<Event>> pagedResources = assembler.toModel(page);

        if(currentUser != null) {
            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
        }

        return ResponseEntity.ok(pagedResources);

    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id, @CurrentUser Account account) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        if(optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);

        // 매니저 id가 연결되어 있는데 그게 현재 로그인한 사용하면 update 링크 전달
        if((event.getManager() != null) && (event.getManager().equals(account))) {
            eventResource.add(linkTo(EventController.class)
                    .slash(event.getId()).withRel("update-event"));
        }

        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors, @CurrentUser Account currentUser) {
        // 입력받은 id로 조회
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        // event가 없으면 notFound(404) 전달
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // error가 있으면 badRequest 전달
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event existingEvent = optionalEvent.get();

        // 등록한 사용자일 경우에만 업데이트 가능
        if((existingEvent.getManager() != null) &&
                (!existingEvent.getManager().equals(currentUser))) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        this.modelMapper.map(eventDto, existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);
        EventResource eventResource = new EventResource(savedEvent);
        return ResponseEntity.ok(eventResource);
    }


    private ResponseEntity<Errors> badRequest(Errors errors) {
        // 이건 또 hateoas 1.1.0 에서는 사용 안하고 1.0.0까지는 사용 했었음..
        // new 로 할당한 걸 그냥 of로 넣어주면 해결됨
        EntityModel<Errors> errorsEntityModel = EntityModel.of(errors);
        errorsEntityModel.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));

        return ResponseEntity.badRequest().body(errorsEntityModel.getContent());
    }
}
