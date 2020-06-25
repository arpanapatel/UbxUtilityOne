package com.example.UBXUtilityOne.model;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Data
public class EncryptionRequest {
    String flag = "NODB";
    String text ;
    String textOperation = "E";
}
