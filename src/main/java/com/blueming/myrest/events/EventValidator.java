package com.blueming.myrest.events;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {
	public void validate(EventDto eventDto, Errors errors) {
		// MaxPrice 값이 있을 때 BasePrice가 MaxPrice보다 크면 오류 발생
		if(eventDto.getBasePrice() > eventDto.getMaxPrice() && 
				eventDto.getMaxPrice() != 0) {
			//Field Error
			errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong");
			errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong");
			//Global Error
			errors.reject("wrongPrices", "Values for prices are wrong");
		}
		// Event 시작 일자가 Event 종료 일자보다 이후면 에러
		// Event 종료 일자가 Event 등록 시작/종료 일자 보다 이전 일자이면 오류
		LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
		if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
		   endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
		   endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ) {
			errors.rejectValue("endEventDateTime", "wrongValue", "EndEventDateTime is wrong");
		}
	}
}