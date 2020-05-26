package com.blueming.myrest.accounts;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AccountSerializer extends JsonSerializer<Account> {
	@Override
	public void serialize(Account account, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		// pw 이런건 보여주면 안되니까 id 만 쓰고 종료
		gen.writeStartObject();
		gen.writeNumberField("id", account.getId());
		gen.writeEndObject();

	}
}
