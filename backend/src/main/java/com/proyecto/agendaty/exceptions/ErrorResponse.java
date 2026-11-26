package com.proyecto.agendaty.exceptions;


public record ErrorResponse(String timeStamp, int status, String errorMessage, String errorsDetails) {


}
