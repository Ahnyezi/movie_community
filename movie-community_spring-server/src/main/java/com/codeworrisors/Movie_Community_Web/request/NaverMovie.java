package com.codeworrisors.Movie_Community_Web.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class NaverMovie {
    String title;
    String link;
    String image;
    String actor;
    String director;
    String userRating;
    String pupDate;
}