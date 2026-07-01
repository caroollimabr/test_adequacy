package br.ufscar.dc.dsw.projeto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Controller
public class HomeController {

    //documentacao OpenAPI
    @Operation(summary = "Página de Home")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Retorna a view home")
    })
    //   
    @GetMapping("/")
    public String home() {
        return "home"; 
    }
}
