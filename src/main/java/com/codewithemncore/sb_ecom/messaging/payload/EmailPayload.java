package com.codewithemncore.sb_ecom.messaging.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailPayload {
    private String to;
    private String from;
    private  String subject;
    private String body;
}
