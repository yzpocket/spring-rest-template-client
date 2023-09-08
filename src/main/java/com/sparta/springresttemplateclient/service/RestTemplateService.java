package com.sparta.springresttemplateclient.service;

import com.sparta.springresttemplateclient.dto.ItemDto;
import com.sparta.springresttemplateclient.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RestTemplateService {
    private final RestTemplate restTemplate;

    // RestTemplateBuilder의 build()를 사용하여 RestTemplate을 생성합니다.
    public RestTemplateService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    // [1]클라이언트 - 하나 가져오기 요청 쿼리스트링
    public ItemDto getCallObject(String query) {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070") // 목적지 서버 7070으로 보낸다.
                .path("/api/server/get-call-obj")  // -> 서버의 목적지 get메소드 중 "get-call-obj"fㅏ는곳
                .queryParam("query", query) //
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);

        ResponseEntity<ItemDto> responseEntity = restTemplate.getForEntity(uri, ItemDto.class); // 위 uri와 getForEntity()메소드는 deserialize 역직렬화 시켜서 받아낸다.

        log.info("statusCode = " + responseEntity.getStatusCode());

        return responseEntity.getBody(); // 그 중 바디를 반환
    }

    // [2]클라이언트 - 모두 가져오기 요청
    public List<ItemDto> getCallList() {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070")
                .path("/api/server/get-call-list")
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

        log.info("statusCode = " + responseEntity.getStatusCode());
        log.info("Body = " + responseEntity.getBody());

        return fromJSONtoItems(responseEntity.getBody());
    }

    // [3]클라이언트 - PathVariable
    public ItemDto postCall(String query) {
        // 요청 URL 만들기 //PathVariable 방식
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070")
                .path("/api/server/post-call/{query}") //PathVariable 방식
                .encode()
                .build()
                .expand(query)
                .toUri();
        log.info("uri = " + uri);

        User user = new User("Robbie", "1234");

        ResponseEntity<ItemDto> responseEntity = restTemplate.postForEntity(uri, user, ItemDto.class); // 1.uri, 2.body object, 3.response object

        log.info("statusCode = " + responseEntity.getStatusCode());

        return responseEntity.getBody();
    }

    // [4]클라이언트 - requestEntity라는 객체를 만들어서 요청을 보냄 uri랑 user 객체는 바디에, 헤더에 토큰을 담아서 보냄
    public List<ItemDto> exchangeCall(String token) {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070")
                .path("/api/server/exchange-call")
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);

        User user = new User("Robbie", "1234");

        RequestEntity<User> requestEntity = RequestEntity
                .post(uri)
                .header("X-Authorization", token)
                .body(user);

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        return fromJSONtoItems(responseEntity.getBody());
    }

    // 모두 가져오기에서
    // List로 모두 가져오는 요청에서 서버로부터 반환받는 형태, String임
    // 이것을 아래 메소드로 배열로 만듦
    //{
    //"items":[
    //		{"title":"Mac","price":3888000},
    //		{"title":"iPad","price":1230000},
    //		{"title":"iPhone","price":1550000},
    //		{"title":"Watch","price":450000},
    //		{"title":"AirPods","price":350000}
    //	]
    //}
    public List<ItemDto> fromJSONtoItems(String responseEntity) {
        JSONObject jsonObject = new JSONObject(responseEntity);
        JSONArray items  = jsonObject.getJSONArray("items");
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Object item : items) { //한줄씩 ItemDto에서 객체로 만들고, 배열에 add하게됨
            ItemDto itemDto = new ItemDto((JSONObject) item);
            itemDtoList.add(itemDto);
        }

        return itemDtoList;
    }
}