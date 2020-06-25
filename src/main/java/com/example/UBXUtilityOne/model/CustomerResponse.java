package com.example.UBXUtilityOne.model;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Data
public class CustomerResponse implements Serializable {

    Map<String,Object> metadata;
    Object data;

}
